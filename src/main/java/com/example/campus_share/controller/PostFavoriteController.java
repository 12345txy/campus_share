package com.example.campus_share.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.common.Result;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.PostFavoriteService;
import com.example.campus_share.service.PostService;
import com.example.campus_share.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class PostFavoriteController {

    private final PostFavoriteService postFavoriteService;
    private final UserService userService;
    private final PostService postService;
    public PostFavoriteController(PostFavoriteService postFavoriteService, UserService userService,PostService postService) {
        this.postFavoriteService = postFavoriteService;
        this.userService = userService;
        this.postService=postService;
    }

    @PostMapping("/{postId}")
    public Result<Boolean> favoritePost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }

        boolean success = postFavoriteService.favoritePost(postId, currentUser.getId());
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(400, "收藏失败，可能已收藏");
        }
    }

    @DeleteMapping("/{postId}")
    public Result<Boolean> unfavoritePost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }

        boolean success = postFavoriteService.unfavoritePost(postId, currentUser.getId());
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(400, "取消收藏失败");
        }
    }

    @GetMapping("/is-favorited/{postId}")
    public Result<Boolean> isFavorited(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }

        boolean isFavorited = postFavoriteService.isFavorited(postId, currentUser.getId());
        return Result.success(isFavorited);
    }

    @GetMapping("/count/{postId}")
    public Result<Integer> getFavoriteCount(@PathVariable Long postId) {
        int count = postFavoriteService.getFavoriteCount(postId);
        return Result.success(count);
    }

    @GetMapping("/my")
    public Result<IPage<Post>> getMyFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }

        Page<Post> pageParam = new Page<>(page, size);
        IPage<Post> favorites = postService.getFavoritePostsByUserId(pageParam, currentUser.getId());

        return Result.success(favorites);
    }

} 