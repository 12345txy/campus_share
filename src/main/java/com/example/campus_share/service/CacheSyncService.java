package com.example.campus_share.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campus_share.entity.PostLike;
import com.example.campus_share.mapper.PostLikeMapper;
import com.example.campus_share.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CacheSyncService {

    @Autowired
    private RedisService redisService;
    
    @Autowired
    private PostLikeMapper postLikeMapper;

    // Redis key 常量
    private static final String POST_LIKE_KEY = "post:like:";
    private static final String POST_LIKE_COUNT_KEY = "post:like:count:";
    private static final String SYNC_TRACKING_KEY = "sync:tracking:posts";
    
    // 需要同步的帖子ID集合
    private final Set<Long> postsToSync = Collections.synchronizedSet(new HashSet<>());

    /**
     * 添加需要同步的帖子ID
     */
    public void addPostForSync(Long postId) {
        postsToSync.add(postId);
        // 同时在Redis中记录
        redisService.sSet(SYNC_TRACKING_KEY, postId);
    }

    /**
     * 每3秒执行一次同步任务
     */
    @Scheduled(fixedRate = 3000) // 3秒 = 3000毫秒
    public void syncRedisWithDatabase() {
        try {
            // 获取所有需要同步的帖子ID
            Set<Long> postIds = getAllPostsToSync();
            
            if (postIds.isEmpty()) {
                return;
            }

            System.out.println("开始同步Redis缓存，帖子数量: " + postIds.size());
            
            for (Long postId : postIds) {
                syncSinglePost(postId);
            }
            
            // 清理已同步的帖子ID
            clearSyncedPosts(postIds);
            
            System.out.println("Redis缓存同步完成");
            
        } catch (Exception e) {
            System.err.println("Redis缓存同步失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取所有需要同步的帖子ID
     */
    private Set<Long> getAllPostsToSync() {
        Set<Long> allPostIds = new HashSet<>();
        
        // 从内存集合获取
        allPostIds.addAll(postsToSync);
        
        // 从Redis获取
        Set<Object> redisPostIds = redisService.sGet(SYNC_TRACKING_KEY);
        if (redisPostIds != null) {
            for (Object obj : redisPostIds) {
                try {
                    allPostIds.add(Long.parseLong(obj.toString()));
                } catch (NumberFormatException e) {
                    System.err.println("解析帖子ID失败: " + obj);
                }
            }
        }
        
        return allPostIds;
    }

    /**
     * 同步单个帖子的缓存
     */
    private void syncSinglePost(Long postId) {
        try {
            // 1. 同步点赞用户列表
            syncPostLikeUsers(postId);
            
            // 2. 同步点赞数量
            syncPostLikeCount(postId);
            
            System.out.println("帖子 " + postId + " 同步完成");
            
        } catch (Exception e) {
            System.err.println("同步帖子 " + postId + " 失败: " + e.getMessage());
        }
    }

    /**
     * 同步帖子的点赞用户列表
     */
    private void syncPostLikeUsers(Long postId) {
        String postLikeKey = POST_LIKE_KEY + postId;
        
        // 从数据库查询真实数据
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId);
        List<PostLike> dbLikes = postLikeMapper.selectList(wrapper);
        
        List<Long> dbUserIds = dbLikes.stream()
                .map(PostLike::getUserId)
                .collect(Collectors.toList());

        // 获取Redis中的数据
        Set<Object> redisUserIds = redisService.sGet(postLikeKey);
        Set<Long> redisUserIdSet = new HashSet<>();
        if (redisUserIds != null) {
            for (Object obj : redisUserIds) {
                try {
                    redisUserIdSet.add(Long.parseLong(obj.toString()));
                } catch (NumberFormatException e) {
                    System.err.println("解析用户ID失败: " + obj);
                }
            }
        }

        // 比较并同步
        Set<Long> dbUserIdSet = new HashSet<>(dbUserIds);
        
        if (!dbUserIdSet.equals(redisUserIdSet)) {
            // 数据不一致，需要同步
            System.out.println("发现数据不一致，帖子ID: " + postId);
            System.out.println("数据库用户数: " + dbUserIdSet.size() + ", Redis用户数: " + redisUserIdSet.size());
            
            // 清除Redis中的旧数据
            redisService.del(postLikeKey);
            
            // 重新设置Redis数据
            if (!dbUserIds.isEmpty()) {
                redisService.sSetAndTime(postLikeKey, 3600, dbUserIds.toArray()); // 1小时过期
            }
            
            System.out.println("帖子 " + postId + " 的点赞用户列表已同步");
        }
    }

    /**
     * 同步帖子的点赞数量
     */
    private void syncPostLikeCount(Long postId) {
        String postLikeCountKey = POST_LIKE_COUNT_KEY + postId;
        
        // 从数据库查询真实数量
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId);
        long dbCount = postLikeMapper.selectCount(wrapper);

        // 获取Redis中的数量
        Object redisCountObj = redisService.get(postLikeCountKey);
        long redisCount = 0;
        if (redisCountObj != null) {
            try {
                redisCount = Long.parseLong(redisCountObj.toString());
            } catch (NumberFormatException e) {
                System.err.println("解析Redis计数失败: " + redisCountObj);
            }
        }

        // 比较并同步
        if (dbCount != redisCount) {
            System.out.println("发现计数不一致，帖子ID: " + postId + ", 数据库: " + dbCount + ", Redis: " + redisCount);
            
            // 更新Redis中的计数
            redisService.set(postLikeCountKey, dbCount, 1800); // 30分钟过期
            
            System.out.println("帖子 " + postId + " 的点赞数量已同步: " + dbCount);
        }
    }

    /**
     * 清理已同步的帖子ID
     */
    private void clearSyncedPosts(Set<Long> syncedPostIds) {
        // 从内存集合中移除
        postsToSync.removeAll(syncedPostIds);
        
        // 从Redis中移除
        for (Long postId : syncedPostIds) {
            redisService.setRemove(SYNC_TRACKING_KEY, postId);
        }
    }

    /**
     * 手动触发全量同步（用于测试或管理）
     */
    public void manualFullSync() {
        System.out.println("开始手动全量同步...");
        
        // 获取所有有缓存的帖子ID
        Set<String> allKeys = getAllPostLikeKeys();
        
        for (String key : allKeys) {
            try {
                String postIdStr = key.replace(POST_LIKE_KEY, "");
                Long postId = Long.parseLong(postIdStr);
                syncSinglePost(postId);
            } catch (Exception e) {
                System.err.println("同步key " + key + " 失败: " + e.getMessage());
            }
        }
        
        System.out.println("手动全量同步完成");
    }

    /**
     * 获取所有点赞相关的Redis键
     */
    private Set<String> getAllPostLikeKeys() {
        // 这里需要根据Redis的具体实现来获取所有匹配的键
        // 由于Redis没有直接的方法获取所有键，这里返回空集合
        // 在实际应用中，可以维护一个专门的集合来记录所有的帖子ID
        return new HashSet<>();
    }

    /**
     * 获取同步统计信息
     */
    public Map<String, Object> getSyncStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingSyncCount", postsToSync.size());
        stats.put("lastSyncTime", new Date());
        
        // 获取Redis中待同步的数量
        long redisPendingCount = redisService.sGetSetSize(SYNC_TRACKING_KEY);
        stats.put("redisPendingSyncCount", redisPendingCount);
        
        return stats;
    }
} 