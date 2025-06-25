<<<<<<< HEAD
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostFavorite;
import com.example.campus_share.mapper.PostFavoriteMapper;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.service.PostFavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PostFavoriteServiceImpl extends ServiceImpl<PostFavoriteMapper, PostFavorite> implements PostFavoriteService {

    private final PostMapper postMapper;

    public PostFavoriteServiceImpl(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    @Transactional
    public boolean favoritePost(Long postId, Long userId) {
        // 检查是否已收藏
        if (isFavorited(postId, userId)) {
            return false;
        }

        // 创建收藏记录
        PostFavorite favorite = new PostFavorite();
        favorite.setPostId(postId);
        favorite.setUserId(userId);
        favorite.setCreateTime(LocalDateTime.now());

        boolean success = this.save(favorite);

        if (success) {
            // 更新帖子的收藏数
            updatePostFavoriteCount(postId);
        }

        return success;
    }

    @Override
    @Transactional
    public boolean unfavoritePost(Long postId, Long userId) {
        LambdaQueryWrapper<PostFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavorite::getPostId, postId)
                .eq(PostFavorite::getUserId, userId);

        boolean success = this.remove(wrapper);

        if (success) {
            // 更新帖子的收藏数
            updatePostFavoriteCount(postId);
        }

        return success;
    }

    @Override
    public boolean isFavorited(Long postId, Long userId) {
        LambdaQueryWrapper<PostFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavorite::getPostId, postId)
                .eq(PostFavorite::getUserId, userId);

        return this.count(wrapper) > 0;
    }

    @Override
    public int getFavoriteCount(Long postId) {
        LambdaQueryWrapper<PostFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavorite::getPostId, postId);

        return Math.toIntExact(this.count(wrapper));
    }


    // 更新帖子的收藏数
    private void updatePostFavoriteCount(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post != null) {
            int favoriteCount = getFavoriteCount(postId);

            post.setFavoriteCount(favoriteCount);
            postMapper.updateById(post);
        }
    }
=======
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostFavorite;
import com.example.campus_share.mapper.PostFavoriteMapper;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.service.PostFavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PostFavoriteServiceImpl extends ServiceImpl<PostFavoriteMapper, PostFavorite> implements PostFavoriteService {

    private final PostMapper postMapper;

    public PostFavoriteServiceImpl(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    @Transactional
    public boolean favoritePost(Long postId, Long userId) {
        // 检查是否已收藏
        if (isFavorited(postId, userId)) {
            return false;
        }

        // 创建收藏记录
        PostFavorite favorite = new PostFavorite();
        favorite.setPostId(postId);
        favorite.setUserId(userId);
        favorite.setCreateTime(LocalDateTime.now());

        boolean success = this.save(favorite);

        if (success) {
            // 更新帖子的收藏数
            updatePostFavoriteCount(postId);
        }

        return success;
    }

    @Override
    @Transactional
    public boolean unfavoritePost(Long postId, Long userId) {
        LambdaQueryWrapper<PostFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavorite::getPostId, postId)
                .eq(PostFavorite::getUserId, userId);

        boolean success = this.remove(wrapper);

        if (success) {
            // 更新帖子的收藏数
            updatePostFavoriteCount(postId);
        }

        return success;
    }

    @Override
    public boolean isFavorited(Long postId, Long userId) {
        LambdaQueryWrapper<PostFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavorite::getPostId, postId)
                .eq(PostFavorite::getUserId, userId);

        return this.count(wrapper) > 0;
    }

    @Override
    public int getFavoriteCount(Long postId) {
        LambdaQueryWrapper<PostFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostFavorite::getPostId, postId);

        return Math.toIntExact(this.count(wrapper));
    }


    // 更新帖子的收藏数
    private void updatePostFavoriteCount(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post != null) {
            int favoriteCount = getFavoriteCount(postId);

            post.setFavoriteCount(favoriteCount);
            postMapper.updateById(post);
        }
    }
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
} 