package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Comment;
import com.example.campus_share.entity.Like;
import com.example.campus_share.entity.Post;
import com.example.campus_share.mapper.CommentMapper;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final PostMapper postMapper;

    public CommentServiceImpl(PostMapper postMapper, CommentMapper commentMapper) {
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }
    private final CommentMapper commentMapper;
    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        log.info("开始创建评论: {}", comment);
        // 设置评论状态为正常
        comment.setStatus(0);
        // 设置创建和更新时间
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        // 初始化点赞数和回复数
        comment.setLikeCount(0);
        comment.setReplyCount(0);

        // 保存评论
        boolean saved = this.save(comment);
        log.info("评论保存结果: {}, 评论ID: {}", saved, comment.getId());

        // 更新帖子的评论数
        log.info("开始更新帖子评论数, 帖子ID: {}", comment.getPostId());
        updatePostCommentCount(comment.getPostId());
        // 如果是回复，更新父评论的回复数
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            log.info("更新父评论回复数, 父评论ID: {}", comment.getParentId());
            updateParentCommentReplyCount(comment.getParentId());
        }

        return comment;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long id, Long userId) {
        // 获取评论
        Comment comment = this.getById(id);
        if (comment == null) {
            return false;
        }

        // 检查是否是评论作者或管理员（这里仅做用户检查）
        if (!comment.getUserId().equals(userId)) {
            return false;
        }

        // 逻辑删除评论
        comment.setStatus(2); // 删除状态
        this.updateById(comment);

        // 更新帖子的评论数
        updatePostCommentCount(comment.getPostId());

        return true;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return this.commentMapper.selectCommentsByPostId(postId);
    }

    @Override
    public List<Comment> getRepliesByCommentId( Long commentId) {
        return this.commentMapper.selectRepliesByCommentId( commentId);
    }

    @Override
    public int getCommentCount(Long postId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getStatus, 0); // 只统计正常状态的评论
        return Math.toIntExact(this.count(wrapper));
    }


    // 更新帖子的评论数
    // 定义一个私有方法，用于更新指定帖子的评论数量
    private void updatePostCommentCount(Long postId) {
        // 通过帖子ID从数据库中查询帖子对象
        Post post = postMapper.selectById(postId);
        // 检查查询到的帖子对象是否为空
        if (post != null) {
            // 获取该帖子的评论数量
            int commentCount = getCommentCount(postId);
            // 将获取到的评论数量设置到帖子对象中
            post.setCommentCount(commentCount);
            // 更新数据库中的帖子对象，包括新的评论数量
            postMapper.updateById(post);
        }
    }
    // 添加新方法来更新父评论的回复数
    private void updateParentCommentReplyCount(Long parentId) {
        Comment parentComment = this.getById(parentId);
        if (parentComment != null) {
            // 查询回复数量
            LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Comment::getParentId, parentId)
                    .eq(Comment::getStatus, 0); // 只统计正常状态的回复
            int replyCount = Math.toIntExact(this.count(wrapper));

            // 更新父评论的回复数
            parentComment.setReplyCount(replyCount);
            this.updateById(parentComment);
        }
    }
<<<<<<< HEAD
=======
}
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1

} 