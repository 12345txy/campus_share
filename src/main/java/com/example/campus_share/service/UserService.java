package com.example.campus_share.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.campus_share.entity.User;

public interface UserService extends IService<User> {
    User register(User user);
    User getUserByUsername(String username);
     User getUserByNickname(String nickname);
    User updateUserProfile(User user);
} 