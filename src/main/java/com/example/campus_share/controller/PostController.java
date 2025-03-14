package com.example.campus_share.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.common.PageResult;
import com.example.campus_share.common.Result;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.PostService;
import com.example.campus_share.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping
    // 使用@PostMapping注解标记该方法为处理POST请求的方法
    public Result<Post> createPost(@RequestBody Post post, @RequestParam(required = false) List<Long> tagIds) {
        // 从安全上下文获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // SecurityContextHolder.getContext()获取当前安全上下文
        // getAuthentication()获取当前认证信息
        String username = authentication.getName();
        
        // 获取当前认证用户的用户名
        // 通过用户名获取完整用户信息
        User currentUser = userService.getUserByUsername(username);
        // 调用userService的getUserByUsername方法根据用户名获取用户信息
        if (currentUser == null) {
            // 如果用户信息为空，返回错误结果
            return Result.error(403, "用户未登录或不存在");
            // Result.error(403, "用户未登录或不存在")创建一个错误结果，状态码为403，错误信息为"用户未登录或不存在"
        }
        
        // 设置帖子关联的用户ID
        post.setUserId(currentUser.getId());
        
        // 设置帖子关联的用户ID为当前用户的ID
        Post createdPost = postService.createPost(post, tagIds);
        // 调用postService的createPost方法创建帖子，并传入帖子对象和标签ID列表
        return Result.success(createdPost);
        // 返回成功结果，包含创建的帖子对象
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