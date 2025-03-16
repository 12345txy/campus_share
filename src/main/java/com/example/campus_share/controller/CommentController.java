package com.example.campus_share.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.common.Result;
import com.example.campus_share.entity.Comment;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.CommentService;
import com.example.campus_share.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }
        
        comment.setUserId(currentUser.getId());
        Comment createdComment = commentService.createComment(comment);
        return Result.success(createdComment);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteComment(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }
        
        boolean success = commentService.deleteComment(id, currentUser.getId());
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(400, "删除失败，可能无权限或评论不存在");
        }
    }

    @GetMapping("/post/{postId}")
    public Result<IPage<Comment>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Comment> pageParam = new Page<>(page, size);
        IPage<Comment> comments = commentService.getCommentsByPostId(pageParam, postId);
        return Result.success(comments);
    }

    @GetMapping("/replies/{commentId}")
    public Result<IPage<Comment>> getRepliesByCommentId(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Comment> pageParam = new Page<>(page, size);
        IPage<Comment> replies = commentService.getRepliesByCommentId(pageParam, commentId);
        return Result.success(replies);
    }
} 