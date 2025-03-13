package com.example.campus_share.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.common.PageResult;
import com.example.campus_share.common.Result;
import com.example.campus_share.entity.Post;
import com.example.campus_share.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public Result<Post> createPost(@RequestBody Post post, @RequestParam(required = false) List<Long> tagIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 获取当前用户ID并设置
        // 这里简化处理，实际应用中应该根据认证用户获取用户ID
        // post.setUserId(userId);
        
        Post createdPost = postService.createPost(post, tagIds);
        return Result.success(createdPost);
    }

    @PutMapping("/{id}")
    public Result<Post> updatePost(@PathVariable Long id, @RequestBody Post post, @RequestParam(required = false) List<Long> tagIds) {
        post.setId(id);
        Post updatedPost = postService.updatePost(post, tagIds);
        return Result.success(updatedPost);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return Result.success(post);
    }

    @GetMapping
    public Result<PageResult<Post>> getPostsByPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<Post> page = new Page<>(current, size);
        IPage<Post> postPage = postService.getPostsByPage(page);
        return Result.success(PageResult.build(postPage));
    }

    @GetMapping("/category/{categoryId}")
    public Result<PageResult<Post>> getPostsByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<Post> page = new Page<>(current, size);
        IPage<Post> postPage = postService.getPostsByCategoryId(page, categoryId);
        return Result.success(PageResult.build(postPage));
    }

    @GetMapping("/user/{userId}")
    public Result<PageResult<Post>> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<Post> page = new Page<>(current, size);
        IPage<Post> postPage = postService.getPostsByUserId(page, userId);
        return Result.success(PageResult.build(postPage));
    }

    @GetMapping("/search")
    public Result<PageResult<Post>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<Post> page = new Page<>(current, size);
        IPage<Post> postPage = postService.searchPosts(page, keyword);
        return Result.success(PageResult.build(postPage));
    }
} 