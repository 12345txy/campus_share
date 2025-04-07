package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.User;
import com.example.campus_share.entity.UserRole;
import com.example.campus_share.mapper.UserMapper;
import com.example.campus_share.mapper.UserRoleMapper;
import com.example.campus_share.service.AdminUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    public AdminUserServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public IPage<User> getUserList(Page<User> page, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                  .or().like(User::getNickname, keyword)
                  .or().like(User::getEmail, keyword);
        }
        
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean assignUserRole(Long userId, Long roleId) {
        // 先删除用户原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);
        
        // 分配新角色
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        
        return userRoleMapper.insert(userRole) > 0;
    }
} 