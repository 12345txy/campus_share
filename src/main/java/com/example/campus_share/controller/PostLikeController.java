package com.example.campus_share.controller;

import com.example.campus_share.common.Result;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.PostLikeService;
import com.example.campus_share.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-likes")
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final UserService userService;

    public PostLikeController(PostLikeService postLikeService, UserService userService) {
        this.postLikeService = postLikeService;
        this.userService = userService;
    }

    @PostMapping("/{postId}")
    public Result<Boolean> likePost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }
        
        boolean success = postLikeService.likePost(postId, currentUser.getId());
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(400, "点赞失败，可能已点赞");
        }
    }

    @DeleteMapping("/{postId}")
    public Result<Boolean> unlikePost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }
        
        boolean success = postLikeService.unlikePost(postId, currentUser.getId());
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(400, "取消点赞失败");
        }
    }

    @GetMapping("/is-liked/{postId}")
    public Result<Boolean> isLiked(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }
        
        boolean isLiked = postLikeService.isLiked(postId, currentUser.getId());
        return Result.success(isLiked);
    }

    @GetMapping("/count/{postId}")
    public Result<Integer> getLikeCount(@PathVariable Long postId) {
        int count = postLikeService.getLikeCount(postId);
        return Result.success(count);
    }

    @GetMapping("/users/{postId}")
    public Result<List<Long>> getLikeUserIds(@PathVariable Long postId) {
        List<Long> userIds = postLikeService.getLikeUserIds(postId);
        return Result.success(userIds);
    }
} 