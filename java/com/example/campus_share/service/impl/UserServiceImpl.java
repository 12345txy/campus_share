<<<<<<< HEAD
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.User;
import com.example.campus_share.mapper.UserMapper;
import com.example.campus_share.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service // 标记该类为Spring的服务组件，可以被Spring容器管理
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService { // 继承ServiceImpl类并实现UserService接口

    private final PasswordEncoder passwordEncoder; // 用于密码加密的PasswordEncoder对象

    // 构造函数，通过依赖注入获取PasswordEncoder实例
    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder; // 初始化PasswordEncoder对象
    }

    @Override // 重写UserService接口中的register方法
    public User register(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (this.baseMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(0); // 正常状态
        user.setIsAdmin(0); // 普通用户

        // 保存用户
        this.baseMapper.insert(user);
        return user;
    }

    // 重写父类或接口中的方法，用于根据用户名获取用户信息
    @Override
    public User getUserByUsername(String username) {
        // 创建一个LambdaQueryWrapper对象，用于构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 在查询条件中添加等于条件，即用户名等于传入的username
        wrapper.eq(User::getUsername, username);
        // 使用baseMapper的selectOne方法执行查询，并返回查询结果
        return this.baseMapper.selectOne(wrapper);
    }
    @Override
   public User getUserByNickname(String nickname){
        // 创建一个LambdaQueryWrapper对象，用于构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 在查询条件中添加等于条件，即用户名等于传入的username
        wrapper.eq(User::getNickname, nickname);
        // 使用baseMapper的selectOne方法执行查询，并返回查询结果
        return this.baseMapper.selectOne(wrapper);
    }

    @Override
    public User updateUserProfile(User user) {
        // 根据传入的用户ID查询数据库中存在的用户信息
        User existUser = this.baseMapper.selectById(user.getId());
        // 如果查询结果为空，说明用户不存在，抛出运行时异常
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 只更新允许修改的字段
        // 设置昵称
        existUser.setNickname(user.getNickname());
        // 设置头像
        existUser.setAvatar(user.getAvatar());
        // 设置个人简介
        existUser.setBio(user.getBio());
        // 设置邮箱
        existUser.setEmail(user.getEmail());
        // 设置电话号码
        existUser.setPhone(user.getPhone());
        // 设置性别
        existUser.setGender(user.getGender());
        // 设置更新时间
        existUser.setUpdateTime(LocalDateTime.now());

        // 将更新后的用户信息保存到数据库
        this.baseMapper.updateById(existUser);
        // 返回更新后的用户信息
        return existUser;
    }
    @Override
    public User getUserByID(long userId) {
        // 创建一个LambdaQueryWrapper对象，用于构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 在查询条件中添加等于条件，即用户ID等于传入的userId
        wrapper.eq(User::getId, userId);
        // 使用baseMapper的selectOne方法
        return this.baseMapper.selectOne(wrapper);
    }
=======
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.User;
import com.example.campus_share.mapper.UserMapper;
import com.example.campus_share.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service // 标记该类为Spring的服务组件，可以被Spring容器管理
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService { // 继承ServiceImpl类并实现UserService接口

    private final PasswordEncoder passwordEncoder; // 用于密码加密的PasswordEncoder对象

    // 构造函数，通过依赖注入获取PasswordEncoder实例
    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder; // 初始化PasswordEncoder对象
    }

    @Override // 重写UserService接口中的register方法
    public User register(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (this.baseMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(0); // 正常状态
        user.setIsAdmin(0); // 普通用户

        // 保存用户
        this.baseMapper.insert(user);
        return user;
    }

    // 重写父类或接口中的方法，用于根据用户名获取用户信息
    @Override
    public User getUserByUsername(String username) {
        // 创建一个LambdaQueryWrapper对象，用于构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 在查询条件中添加等于条件，即用户名等于传入的username
        wrapper.eq(User::getUsername, username);
        // 使用baseMapper的selectOne方法执行查询，并返回查询结果
        return this.baseMapper.selectOne(wrapper);
    }
    @Override
   public User getUserByNickname(String nickname){
        // 创建一个LambdaQueryWrapper对象，用于构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 在查询条件中添加等于条件，即用户名等于传入的username
        wrapper.eq(User::getNickname, nickname);
        // 使用baseMapper的selectOne方法执行查询，并返回查询结果
        return this.baseMapper.selectOne(wrapper);
    }

    @Override
    public User updateUserProfile(User user) {
        // 根据传入的用户ID查询数据库中存在的用户信息
        User existUser = this.baseMapper.selectById(user.getId());
        // 如果查询结果为空，说明用户不存在，抛出运行时异常
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 只更新允许修改的字段
        // 设置昵称
        existUser.setNickname(user.getNickname());
        // 设置头像
        existUser.setAvatar(user.getAvatar());
        // 设置个人简介
        existUser.setBio(user.getBio());
        // 设置邮箱
        existUser.setEmail(user.getEmail());
        // 设置电话号码
        existUser.setPhone(user.getPhone());
        // 设置性别
        existUser.setGender(user.getGender());
        // 设置更新时间
        existUser.setUpdateTime(LocalDateTime.now());

        // 将更新后的用户信息保存到数据库
        this.baseMapper.updateById(existUser);
        // 返回更新后的用户信息
        return existUser;
    }
    @Override
    public User getUserByID(long userId) {
        // 创建一个LambdaQueryWrapper对象，用于构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 在查询条件中添加等于条件，即用户ID等于传入的userId
        wrapper.eq(User::getId, userId);
        // 使用baseMapper的selectOne方法
        return this.baseMapper.selectOne(wrapper);
    }
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
} 