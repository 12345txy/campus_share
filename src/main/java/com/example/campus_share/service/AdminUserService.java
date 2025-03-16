package com.example.campus_share.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.User;

public interface AdminUserService {
    // 获取用户列表，支持关键词搜索
    IPage<User> getUserList(Page<User> page, String keyword);
    
    // 更新用户状态（正常/禁用）
    boolean updateUserStatus(Long userId, Integer status);
    
    // 分配用户角色
    boolean assignUserRole(Long userId, Long roleId);
} 