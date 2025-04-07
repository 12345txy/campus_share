package com.example.campus_share.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.common.Result;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.UserRelationService;
import com.example.campus_share.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relations")
public class UserRelationController {

    private final UserRelationService userRelationService;
    private final UserService userService;

    public UserRelationController(UserRelationService userRelationService, UserService userService) {
        this.userRelationService = userRelationService;
        this.userService = userService;
    }

    // 关注用户
    @PostMapping("/follow/{followUserId}")
    public Result<Boolean> followUser(@PathVariable Long followUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }
        
        boolean success = userRelationService.followUser(currentUser.getId(), followUserId);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(400, "关注失败，可能已关注或不能关注自己");
        }
    }
    
    // 取消关注
    @DeleteMapping("/follow/{followUserId}")
    public Result<Boolean> unfollowUser(@PathVariable Long followUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }
        
        boolean success = userRelationService.unfollowUser(currentUser.getId(), followUserId);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(400, "取消关注失败");
        }
    }
    
    // 检查是否已关注
    @GetMapping("/is-following/{followUserId}")
    public Result<Boolean> isFollowing(@PathVariable Long followUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }
        
        boolean isFollowing = userRelationService.isFollowing(currentUser.getId(), followUserId);
        return Result.success(isFollowing);
    }
    
    // 获取关注列表
    @GetMapping("/following")
    public Result<IPage<User>> getFollowingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }
        
        // 如果没有指定userId，则获取当前登录用户的关注列表
        if (userId == null) {
            userId = currentUser.getId();
        }
        
        Page<User> pageParam = new Page<>(page, size);
        IPage<User> followingList = userRelationService.getFollowingList(pageParam, userId);
        
        return Result.success(followingList);
    }
    
    // 获取粉丝列表
    @GetMapping("/followers")
    public Result<IPage<User>> getFollowerList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error(403, "用户未登录或不存在");
        }
        
        // 如果没有指定userId，则获取当前登录用户的粉丝列表
        if (userId == null) {
            userId = currentUser.getId();
        }
        
        Page<User> pageParam = new Page<>(page, size);
        IPage<User> followerList = userRelationService.getFollowerList(pageParam, userId);
        
        return Result.success(followerList);
    }
}