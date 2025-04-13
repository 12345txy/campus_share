package com.example.campus_share.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.AdminUserService;
import com.example.campus_share.util.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        Page<User> pageParam = new Page<>(page, size);
        IPage<User> userPage = adminUserService.getUserList(pageParam, keyword);
        return Result.success(userPage);
    }
    
    @PutMapping("/{userId}/status")
    public Result<Boolean> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        
        boolean success = adminUserService.updateUserStatus(userId, status);
        return Result.success(success);
    }
    
    @PutMapping("/{userId}/role")
    public Result<Boolean> assignUserRole(
            @PathVariable Long userId,
            @RequestParam Long roleId) {
        
        boolean success = adminUserService.assignUserRole(userId, roleId);
        return Result.success(success);
    }
} 