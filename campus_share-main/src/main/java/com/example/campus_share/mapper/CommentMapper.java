package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    // 查询帖子评论列表，bu!!!支持分页
    List<Comment> selectCommentsByPostId(@Param("postId") Long postId);

    // 查询评论的回复列表
    IPage<Comment> selectRepliesByCommentId(Page<Comment> page, @Param("parentId") Long parentId);
} 