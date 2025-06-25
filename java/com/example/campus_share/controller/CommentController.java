package com.example.campus_share.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.common.Result;
import com.example.campus_share.entity.Comment;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.CommentService;
import com.example.campus_share.service.LikeService;
import com.example.campus_share.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    @Autowired
    private LikeService likeService;
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment) {
        // 如果是回复且没有提供postId
        if (comment.getParentId() != null && comment.getParentId() > 0
                && comment.getPostId() == null) {
            // 查询父评论获取postId
            Comment parentComment = commentService.getById(comment.getParentId());
            if (parentComment == null) {
                return Result.error(404, "要回复的评论不存在");
            }
            // 设置postId
            comment.setPostId(parentComment.getPostId());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }

        comment.setUserId(currentUser.getId());
        comment.setAvatar(currentUser.getAvatar());
        if(currentUser.getNickname()!=null) {
            comment.setNickname(currentUser.getNickname());
        }
        else {
            comment.setNickname(currentUser.getUsername());
        }
        Comment createdComment = commentService.createComment(comment);
        System.out.println(createdComment);
        return Result.success(createdComment);
    }

    // 使用@DeleteMapping注解来映射HTTP DELETE请求到该方法
    @DeleteMapping("/{id}")
    // 定义一个方法用于删除评论，接收一个路径变量id，返回一个Result<Boolean>对象
    public Result<Boolean> deleteComment(@PathVariable Long id) {
        // 从SecurityContextHolder获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 从认证信息中获取当前用户名
        String username = authentication.getName();

        // 通过用户名从userService获取当前用户对象
        User currentUser = userService.getUserByUsername(username);
        // 如果当前用户为空，返回一个错误结果，状态码403，错误信息为"用户未登录或不存在"
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }

        // 调用commentService的deleteComment方法尝试删除评论，传入评论id和当前用户id
        boolean success = commentService.deleteComment(id, currentUser.getId());
        // 如果删除成功，返回一个成功结果，数据为true
        if (success) {
            return Result.success(true);
        } else {
            // 如果删除失败，返回一个错误结果，状态码400，错误信息为"删除失败，可能无权限或评论不存在"
            return Result.error(400, "删除失败，可能无权限或评论不存在");
        }
    }

    @GetMapping("/post/{postId}")
    public Result<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return Result.success(comments);
    }

    @GetMapping("/replies/{commentId}")
    public Result<List<Comment>>  getRepliesByCommentId(
            @PathVariable Long commentId){


        List<Comment> replies = commentService.getRepliesByCommentId(commentId);
        return Result.success(replies);
    }
    @GetMapping("/like/exists")
    public boolean existsByUserIdAndTargetIdAndType( @RequestParam Long targetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        Integer type = 1; // 固定 type 为 1
        return likeService.existsByUserIdAndTargetIdAndType(currentUser.getId(), targetId, type);
    }
    @PostMapping("/like/{commentId}")
    public Result<Boolean> likePost(@PathVariable Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }

        boolean success = likeService.likeComment(commentId, currentUser.getId());
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(400, "点赞失败，可能已点赞");
        }
    }

    @DeleteMapping("/like/{commentId}")
    public Result<Boolean> unlikePost(@PathVariable Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }

        boolean success = likeService.unlikeComment(commentId, currentUser.getId());
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(400, "取消点赞失败");
        }
    }
} 