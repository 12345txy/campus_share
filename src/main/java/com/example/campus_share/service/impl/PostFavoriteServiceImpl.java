package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostFavorite;
import com.example.campus_share.mapper.PostFavoriteMapper;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.service.PostFavoriteService;
import com.example.campus_share.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostFavoriteServiceImpl extends ServiceImpl<PostFavoriteMapper, PostFavorite> implements PostFavoriteService {

    private final PostMapper postMapper;
    
    @Autowired
    private RedisService redisService;

    // Redis key 常量
    private static final String POST_FAVORITE_KEY = "post:favorite:";
    private static final String POST_FAVORITE_COUNT_KEY = "post:favorite:count:";
    private static final String USER_FAVORITE_KEY = "user:favorite:";

    public PostFavoriteServiceImpl(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    @Transactional
    public boolean favoritePost(Long postId, Long userId) {
        String postFavoriteKey = POST_FAVORITE_KEY + postId;
        String postFavoriteCountKey = POST_FAVORITE_COUNT_KEY + postId;
        String userFavoriteKey = USER_FAVORITE_KEY + userId;

        // 检查是否已收藏
        if (redisService.sHasKey(postFavoriteKey, userId)) {
            return false;
        }

        try {
            // 1. 在Redis中添加收藏记录
            redisService.sSet(postFavoriteKey, userId);
            redisService.sSet(userFavoriteKey, postId);
            
            // 2. 增加收藏计数
            redisService.incr(postFavoriteCountKey, 1);

            // 3. 写入数据库
            PostFavorite favorite = new PostFavorite();
            favorite.setPostId(postId);
            favorite.setUserId(userId);
            favorite.setCreateTime(LocalDateTime.now());
            this.save(favorite);

            // 4. 更新帖子的收藏数
            updatePostFavoriteCountAsync(postId);

            return true;
        } catch (Exception e) {
            // 回滚Redis操作
            redisService.setRemove(postFavoriteKey, userId);
            redisService.setRemove(userFavoriteKey, postId);
            redisService.decr(postFavoriteCountKey, 1);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean unfavoritePost(Long postId, Long userId) {
        String postFavoriteKey = POST_FAVORITE_KEY + postId;
        String postFavoriteCountKey = POST_FAVORITE_COUNT_KEY + postId;
        String userFavoriteKey = USER_FAVORITE_KEY + userId;

        // 检查是否已收藏
        if (!redisService.sHasKey(postFavoriteKey, userId)) {
            return false;
        }

        try {
            // 1. 在Redis中移除收藏记录
            redisService.setRemove(postFavoriteKey, userId);
            redisService.setRemove(userFavoriteKey, postId);
            
            // 2. 减少收藏计数
            redisService.decr(postFavoriteCountKey, 1);

            // 3. 从数据库删除
            LambdaQueryWrapper<PostFavorite> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PostFavorite::getPostId, postId)
                    .eq(PostFavorite::getUserId, userId);
            this.remove(wrapper);

            // 4. 更新帖子的收藏数
            updatePostFavoriteCountAsync(postId);

            return true;
        } catch (Exception e) {
            // 回滚Redis操作
            redisService.sSet(postFavoriteKey, userId);
            redisService.sSet(userFavoriteKey, postId);
            redisService.incr(postFavoriteCountKey, 1);
            throw e;
        }
    }

    @Override
    public boolean isFavorited(Long postId, Long userId) {
        String postFavoriteKey = POST_FAVORITE_KEY + postId;
        
        // 优先从Redis查询
        if (redisService.hasKey(postFavoriteKey)) {
            return redisService.sHasKey(postFavoriteKey, userId);
        }

        // 从数据库查询并缓存
        LambdaQueryWrapper<PostFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavorite::getPostId, postId)
                .eq(PostFavorite::getUserId, userId);
        
        boolean isFavorited = this.count(wrapper) > 0;
        
        if (isFavorited) {
            redisService.sSet(postFavoriteKey, userId);
        }
        
        return isFavorited;
    }

    @Override
    public int getFavoriteCount(Long postId) {
        String postFavoriteCountKey = POST_FAVORITE_COUNT_KEY + postId;
        
        // 优先从Redis获取
        if (redisService.hasKey(postFavoriteCountKey)) {
            Object count = redisService.get(postFavoriteCountKey);
            return count != null ? Integer.parseInt(count.toString()) : 0;
        }

        // 从数据库查询并缓存
        LambdaQueryWrapper<PostFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavorite::getPostId, postId);
        int count = Math.toIntExact(this.count(wrapper));
        
        redisService.set(postFavoriteCountKey, count, 24 * 60 * 60);
        
        return count;
    }

    /**
     * 获取用户收藏的帖子列表
     */
    public List<Long> getUserFavoritePostIds(Long userId) {
        String userFavoriteKey = USER_FAVORITE_KEY + userId;
        
        // 优先从Redis获取
        if (redisService.hasKey(userFavoriteKey)) {
            Set<Object> postIds = redisService.sGet(userFavoriteKey);
            return postIds.stream()
                    .map(obj -> Long.parseLong(obj.toString()))
                    .collect(Collectors.toList());
        }

        // 从数据库查询并缓存
        LambdaQueryWrapper<PostFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavorite::getUserId, userId);
        List<PostFavorite> favorites = this.list(wrapper);
        
        List<Long> postIds = favorites.stream()
                .map(PostFavorite::getPostId)
                .collect(Collectors.toList());
        
        if (!postIds.isEmpty()) {
            redisService.sSet(userFavoriteKey, postIds.toArray());
        }
        
        return postIds;
    }

    /**
     * 异步更新帖子的收藏数
     */
    private void updatePostFavoriteCountAsync(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post != null) {
            int favoriteCount = getFavoriteCount(postId);
<<<<<<< HEAD
            // 假设Post实体有favoriteCount字段
            // post.setFavoriteCount(favoriteCount);
            // postMapper.updateById(post);
=======

            post.setFavoriteCount(favoriteCount);
            postMapper.updateById(post);
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
        }
    }
} 