package com.example.campus_share.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostLike;

import java.util.List;

public interface PostLikeService extends IService<PostLike> {
    // 点赞帖子
    boolean likePost(Long postId, Long userId);
    
    // 取消点赞
    boolean unlikePost(Long postId, Long userId);
    
    // 检查用户是否已点赞某帖子
    boolean isLiked(Long postId, Long userId);
    
    // 获取帖子的点赞数
    int getLikeCount(Long postId);
    
    // 获取帖子的点赞用户ID列表
    List<Long> getLikeUserIds(Long postId);

    boolean likeComment(Long commentId, Long userId);
    boolean unlikeComment(Long commentId, Long userId);

} 