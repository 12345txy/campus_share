package com.example.campus_share.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostFavorite;

public interface PostFavoriteService extends IService<PostFavorite> {
    // 收藏帖子
    boolean favoritePost(Long postId, Long userId);
    
    // 取消收藏
    boolean unfavoritePost(Long postId, Long userId);
    
    // 检查用户是否已收藏某帖子
    boolean isFavorited(Long postId, Long userId);
    
    // 获取帖子的收藏数
    int getFavoriteCount(Long postId);
    
    // 获取用户收藏的帖子列表
    IPage<Post> getFavoritePostsByUserId(Page<Post> page, Long userId);
} 