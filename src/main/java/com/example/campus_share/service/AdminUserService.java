package com.example.campus_share.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.User;

public interface AdminUserService {
    IPage<User> getUserList(Page<User> page, String keyword);
    boolean updateUserStatus(Long userId, Integer status);
    boolean updateUserAdminRole(Long userId, Integer isAdmin);
    boolean deleteUserById(Long userId);

}