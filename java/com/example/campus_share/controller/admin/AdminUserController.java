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

    // 分页获取用户列表（支持关键词搜索）
    @GetMapping
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Page<User> pageParam = new Page<>(page, size);
        IPage<User> userPage = adminUserService.getUserList(pageParam, keyword);
        return Result.success(userPage);
    }

    // 修改用户状态（0-正常，1-禁用）
    @PutMapping("/{userId}/status")
    public Result<Boolean> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        boolean success = adminUserService.updateUserStatus(userId, status);
        return Result.success(success);
    }

    // 修改用户是否为管理员
    @PutMapping("/{userId}/admin")
    public Result<Boolean> updateUserAdminRole(
            @PathVariable Long userId,
            @RequestParam Integer isAdmin) {
        boolean success = adminUserService.updateUserAdminRole(userId, isAdmin);
        return Result.success(success);
    }

    // 删除用户
    @DeleteMapping("/{userId}")
    public Result<Boolean> deleteUser(@PathVariable Long userId) {
        boolean success = adminUserService.deleteUserById(userId);
        return Result.success(success);
    }
}
