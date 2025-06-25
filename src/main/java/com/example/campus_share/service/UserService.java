package com.example.campus_share.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.campus_share.entity.User;

public interface UserService extends IService<User> {
    User register(User user);
    User getUserByUsername(String username);
     User getUserByNickname(String nickname);
<<<<<<< HEAD
    User getUserByID(long userId);
=======
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
    User updateUserProfile(User user);
} 