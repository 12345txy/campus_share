package com.example.campus_share.controller;

import com.example.campus_share.common.Result;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current/{userId}")
    public Result<Map<String, Object>> getUserByUserId(@PathVariable long userId) {
        User user =null;

        if(userId!=-1) {
            // 查询目标用户
        user = userService.getUserByID(userId);}

        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserByUsername(currentUsername); // 假设按用户名查询
           if(userId==-1){
               user=currentUser;
           }
        // 处理用户存在的情况
        if (user != null) {
            user.setPassword(null); // 不返回密码

            // 添加isCurrentUser字段（使用Map扩展返回结果）
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("isCurrentUser", currentUser != null && currentUser.getId().equals(user.getId()));

            return Result.success(result);
        }

        // 用户不存在时直接返回user=null
        return Result.success(null);
    }
    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        user.setId(currentUser.getId());
        User updatedUser = userService.updateUserProfile(user);
        updatedUser.setPassword(null); // 不返回密码
        return Result.success(updatedUser);
    }
} 