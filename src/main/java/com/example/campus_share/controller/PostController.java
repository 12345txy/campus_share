package com.example.campus_share.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.common.PageResult;
import com.example.campus_share.common.Result;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostDTO;
import com.example.campus_share.entity.Tag;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.CategoryService;
import com.example.campus_share.service.PostService;
import com.example.campus_share.service.TagService;
import com.example.campus_share.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.campus_share.DTO.PostCreateRequest;
import com.example.campus_share.util.OSSUtil;

<<<<<<< HEAD
import java.io.IOException;
=======
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
<<<<<<< HEAD



=======
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;
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
<<<<<<< HEAD
    @GetMapping("/tags")
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }
=======
@GetMapping("/tags")
public List<Tag> getAllTags() {
    return tagService.getAllTags();
}
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
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
    public Result<PageResult<PostDTO>> getPostsByPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "6") Long size,
            @RequestParam(defaultValue = "0") int sortOption,
            @RequestParam(required = false) String tags) {
        List<String> tagList = new ArrayList<>();
        if (tags != null &&!tags.isEmpty()) {
            // 将逗号分隔的字符串转换为List
            tagList = Arrays.asList(tags.split(","));
            tagList = tagList.stream()
                    .map(tag -> {
                        try {
                            return URLDecoder.decode(tag, StandardCharsets.UTF_8.name());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        }
        Page<Post> page = new Page<>(current, size);
        System.out.println(tagList);
        IPage<PostDTO> postPage = postService.getPostsByPage(page, sortOption, tagList);
        return Result.success(PageResult.build(postPage));
    }
    @GetMapping("/category/{categoryId}")
    public Result<PageResult<PostDTO>> getPostsByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "0") int sortOption,
            @RequestParam(defaultValue = "6") Long size,
            @RequestParam(required = false) List<String> tags) {
        Page<Post> page = new Page<>(current, size);

        IPage<PostDTO> postPage = postService.getPostsByCategoryId(page, categoryId,sortOption,tags);
        return Result.success(PageResult.build(postPage));
    }

    @GetMapping("/user/{userId}")
    public Result<PageResult<PostDTO>> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "")String userName,
            @RequestParam(defaultValue = "6") Long size,
            @RequestParam(defaultValue = "0") int sortOption,
            @RequestParam(required = false) List<String> tags) {
        Page<Post> page = new Page<>(current, size);
<<<<<<< HEAD
        System.out.println(userName);
//        if(userId==-1)userId=userService.getUserByNickname(userName).getId();
        if (userId == -1) {
            User user = userService.getUserByNickname(userName);
            if (user == null) {
                return Result.error("用户 " + userName + " 不存在");
            }
            userId = user.getId();
        }

=======
        if(userId==-1)userId=userService.getUserByNickname(userName).getId();
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
        System.out.println(userName+" "+userId);

        IPage<PostDTO> postPage= postService.getPostsByUserId(page, userId,sortOption,tags);
        return Result.success(PageResult.build(postPage));
    }

    @GetMapping("/search")
    public Result<PageResult<PostDTO>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "6") Long size,
            @RequestParam(defaultValue = "0") int sortOption,
            @RequestParam(required = false) List<String> tags) {
        Page<Post> page = new Page<>(current, size);

        IPage<PostDTO> postPage = postService.searchPosts(page, keyword,sortOption,tags);
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
        post.setImage(request.getImage()); // ✅ 多张图片
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
    // 新增方法：查找指定用户收藏的帖子列表
    @GetMapping("/user/Favorites/{userId}")
    public Result<IPage<PostDTO>> getFavoritesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "")String userName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if(userId==-1)userId=userService.getUserByNickname(userName).getId();
        System.out.println(userName+" "+userId);
        Page<Post> pageParam = new Page<>(page, size);
        IPage<PostDTO> favorites = postService.getFavoritesPostsByUserId(pageParam, userId);

<<<<<<< HEAD
        return Result.success(favorites);
    }
    @GetMapping("/user/Likes/{userId}")
    public Result<IPage<PostDTO>> getLikesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "")String userName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if(userId==-1)userId=userService.getUserByNickname(userName).getId();
        System.out.println(userName+" "+userId);
        Page<Post> pageParam = new Page<>(page, size);
        IPage<PostDTO> favorites = postService.getLikesPostsByUserId(pageParam, userId);

        return Result.success(favorites);
    }


    @Autowired
    private OSSUtil ossUtil;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = ossUtil.upload(file);
        return Result.success(url);
    }

=======
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
} 