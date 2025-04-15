package com.example.campus_share.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.common.PageResult;
import com.example.campus_share.common.Result;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.CategoryService;
import com.example.campus_share.service.PostService;
import com.example.campus_share.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.campus_share.DTO.PostCreateRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    @Autowired
    private CategoryService categoryService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

//    @PostMapping
//    public Result<Post> createPost(@RequestBody Post post, @RequestParam(required = false) List<Long> tagIds) {
//        // 从安全上下文获取当前认证用户
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        // 通过用户名获取完整用户信息
//        User currentUser = userService.getUserByUsername(username);
//        if (currentUser == null) {
//            return Result.error(403, "用户未登录或不存在");
//        }
//
//        // 设置帖子关联的用户ID
//        post.setUserId(currentUser.getId());
//
//        Post createdPost = postService.createPost(post, tagIds);
//        return Result.success(createdPost);
//    }

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

    @PostMapping
    public Result<Post> createPost(@RequestBody PostCreateRequest request) {
        // 1. 获取当前登录用户（Spring Security）
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }

        // 2. 构造 Post 实体
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCoverImage(request.getCoverImage());
        post.setImages(request.getImages()); // ✅ 多张图片
        post.setUserId(currentUser.getId()); // ✅ 登录用户ID
        post.setCategoryId(categoryService.getIdByName(request.getCategory())); // ✅ 分类转换
        post.setStatus(0); // 默认正常
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setFavoriteCount(0);

        // 3. 调用服务保存
        postService.save(post);

        return Result.success(post); // 返回保存成功的帖子
    }


} 