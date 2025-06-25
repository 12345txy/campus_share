<<<<<<< HEAD
package com.example.campus_share.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.User;

public interface AdminUserService {
    IPage<User> getUserList(Page<User> page, String keyword);
    boolean updateUserStatus(Long userId, Integer status);
    boolean updateUserAdminRole(Long userId, Integer isAdmin);
    boolean deleteUserById(Long userId);

=======
package com.example.campus_share.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.User;

public interface AdminUserService {
    IPage<User> getUserList(Page<User> page, String keyword);
    boolean updateUserStatus(Long userId, Integer status);
    boolean updateUserAdminRole(Long userId, Integer isAdmin);
    boolean deleteUserById(Long userId);

>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
}