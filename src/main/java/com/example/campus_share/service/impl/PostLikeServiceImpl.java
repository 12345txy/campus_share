package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Like;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostLike;
import com.example.campus_share.mapper.LikeMapper;
import com.example.campus_share.mapper.PostLikeMapper;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.service.PostLikeService;
import com.example.campus_share.service.RedisService;
import com.example.campus_share.service.CacheSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements PostLikeService {

    private final PostMapper postMapper;
    private final LikeMapper likeMapper;
    
    @Autowired
    private RedisService redisService;

    @Autowired
    private CacheSyncService cacheSyncService;

    // Redis key 常量
    private static final String POST_LIKE_KEY = "post:like:";
    private static final String POST_LIKE_COUNT_KEY = "post:like:count:";
    private static final String USER_LIKE_KEY = "user:like:";

    public PostLikeServiceImpl(PostMapper postMapper, LikeMapper likeMapper) {
        this.postMapper = postMapper;
        this.likeMapper = likeMapper;
    }

    @Override
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        String postLikeKey = POST_LIKE_KEY + postId;
        String postLikeCountKey = POST_LIKE_COUNT_KEY + postId;
        String userLikeKey = USER_LIKE_KEY + userId;

        // 检查是否已点赞（优先从Redis检查）
        if (redisService.sHasKey(postLikeKey, userId)) {
            return false;
        }

        try {
            // 1. 在Redis中添加点赞记录
            redisService.sSet(postLikeKey, userId);
            redisService.sSet(userLikeKey, postId);
            
            // 2. 增加点赞计数
            redisService.incr(postLikeCountKey, 1);

            // 3. 异步写入数据库
            PostLike postLike = new PostLike();
            postLike.setPostId(postId);
            postLike.setUserId(userId);
            postLike.setCreateTime(LocalDateTime.now());
            this.save(postLike);

            // 4. 标记需要同步
            cacheSyncService.addPostForSync(postId);

            // 5. 更新帖子的点赞数（异步）
            updatePostLikeCountAsync(postId);

            return true;
        } catch (Exception e) {
            // 发生异常时回滚Redis操作
            redisService.setRemove(postLikeKey, userId);
            redisService.setRemove(userLikeKey, postId);
            redisService.decr(postLikeCountKey, 1);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        String postLikeKey = POST_LIKE_KEY + postId;
        String postLikeCountKey = POST_LIKE_COUNT_KEY + postId;
        String userLikeKey = USER_LIKE_KEY + userId;

        // 检查是否已点赞
        if (!redisService.sHasKey(postLikeKey, userId)) {
            return false;
        }

        try {
            // 1. 在Redis中移除点赞记录
            redisService.setRemove(postLikeKey, userId);
            redisService.setRemove(userLikeKey, postId);
            
            // 2. 减少点赞计数
            redisService.decr(postLikeCountKey, 1);

            // 3. 从数据库删除
            LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PostLike::getPostId, postId)
                    .eq(PostLike::getUserId, userId);
            this.remove(wrapper);

            // 4. 标记需要同步
            cacheSyncService.addPostForSync(postId);

            // 5. 更新帖子的点赞数（异步）
            updatePostLikeCountAsync(postId);

            return true;
        } catch (Exception e) {
            // 发生异常时回滚Redis操作
            redisService.sSet(postLikeKey, userId);
            redisService.sSet(userLikeKey, postId);
            redisService.incr(postLikeCountKey, 1);
            throw e;
        }
    }

    @Override
    public boolean isLiked(Long postId, Long userId) {
        String postLikeKey = POST_LIKE_KEY + postId;
        
        // 优先从Redis查询
        if (redisService.hasKey(postLikeKey)) {
            return redisService.sHasKey(postLikeKey, userId);
        }

        // Redis中没有数据，从数据库查询并缓存
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);
        
        boolean isLiked = this.count(wrapper) > 0;
        
        // 将结果缓存到Redis
        if (isLiked) {
            redisService.sSet(postLikeKey, userId);
        }
        
        return isLiked;
    }

    @Override
    public int getLikeCount(Long postId) {
        String postLikeCountKey = POST_LIKE_COUNT_KEY + postId;
        
        // 优先从Redis获取
        if (redisService.hasKey(postLikeCountKey)) {
            Object count = redisService.get(postLikeCountKey);
            return count != null ? Integer.parseInt(count.toString()) : 0;
        }

        // Redis中没有数据，从数据库查询并缓存
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId);
        int count = Math.toIntExact(this.count(wrapper));
        
        // 缓存到Redis，设置过期时间24小时
        redisService.set(postLikeCountKey, count, 24 * 60 * 60);
        
        return count;
    }

    @Override
    public List<Long> getLikeUserIds(Long postId) {
        String postLikeKey = POST_LIKE_KEY + postId;
        
        // 优先从Redis获取
        if (redisService.hasKey(postLikeKey)) {
            Set<Object> userIds = redisService.sGet(postLikeKey);
            return userIds.stream()
                    .map(obj -> Long.parseLong(obj.toString()))
                    .collect(Collectors.toList());
        }

        // Redis中没有数据，从数据库查询并缓存
        List<Long> userIds = this.baseMapper.selectLikeUserIdsByPostId(postId);
        
        // 缓存到Redis
        if (!userIds.isEmpty()) {
            redisService.sSet(postLikeKey, userIds.toArray());
        }
        
        return userIds;
    }

    /**
     * 异步更新帖子的点赞数
     */
    private void updatePostLikeCountAsync(Long postId) {
        // 可以使用@Async注解或者线程池来异步执行
        Post post = postMapper.selectById(postId);
        if (post != null) {
            int likeCount = getLikeCount(postId);
            post.setLikeCount(likeCount);
            postMapper.updateById(post);
        }
    }

    /**
     * 预热缓存 - 将热门帖子的点赞数据加载到Redis
     */
    public void warmupCache(Long postId) {
        // 预加载点赞用户列表
        List<Long> userIds = this.baseMapper.selectLikeUserIdsByPostId(postId);
        if (!userIds.isEmpty()) {
            String postLikeKey = POST_LIKE_KEY + postId;
            redisService.sSet(postLikeKey, userIds.toArray());
        }

        // 预加载点赞数
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId);
        int count = Math.toIntExact(this.count(wrapper));
        
        String postLikeCountKey = POST_LIKE_COUNT_KEY + postId;
        redisService.set(postLikeCountKey, count, 24 * 60 * 60);
    }

    @Override
    public boolean likeComment(Long commentId, Long userId) {
        // 实现评论点赞逻辑
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetId(commentId);
        like.setType(1); // 1 表示评论
        // 调用Mapper插入点赞记录
        return likeMapper.insert(like) > 0;
    }

    @Override
    public boolean unlikeComment(Long commentId, Long userId) {
        // 实现评论取消点赞逻辑
        return likeMapper.deleteByUserIdAndTargetIdAndType(userId, commentId, 1) > 0;
    }
} 