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

    // 使用@GetMapping注解来映射HTTP GET请求到特定的处理方法上
    // "/count/{postId}" 表示URL路径，其中{postId}是一个路径变量
    @GetMapping("/count/{postId}")
    // 定义一个公共方法getLikeCount，用于获取指定帖子(postId)的点赞数
    // 该方法返回一个Result<Integer>对象，其中包含点赞数
    public Result<Integer> getLikeCount(@PathVariable Long postId) {
        // 使用@PathVariable注解将URL中的路径变量postId绑定到方法参数postId上
        // 调用postLikeService的getLikeCount方法，传入postId，获取点赞数
        int count = postLikeService.getLikeCount(postId);
        // 调用Result的success静态方法，将点赞数count包装成Result对象并返回
        return Result.success(count);
    }

    // 使用@GetMapping注解来映射HTTP GET请求到该方法
    // URL路径为"/users/{postId}"，其中{postId}是一个路径变量
    @GetMapping("/users/{postId}")
    // 定义一个公共方法getLikeUserIds，接收一个Long类型的路径变量postId
    public Result<List<Long>> getLikeUserIds(@PathVariable Long postId) {
        // 调用postLikeService的getLikeUserIds方法，传入postId，获取喜欢该帖子的用户ID列表
        List<Long> userIds = postLikeService.getLikeUserIds(postId);
        // 将获取到的用户ID列表封装在Result对象中，并返回成功的结果
        return Result.success(userIds);
    }
} 