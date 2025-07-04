package com.example.campus_share.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.campus_share.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    // 创建评论
    Comment createComment(Comment comment);

    // 删除评论
    boolean deleteComment(Long id, Long userId);

    // 获取帖子的评论列表
    public List<Comment> getCommentsByPostId(Long postId);

    // 获取评论的回复列表
    List<Comment>  getRepliesByCommentId( Long commentId);
<<<<<<< HEAD

=======
    
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
    // 获取帖子的评论数
    int getCommentCount(Long postId);
    // 点赞帖子

} 