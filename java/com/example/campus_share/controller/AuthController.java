package com.example.campus_share.controller;

import com.example.campus_share.common.Result;
import com.example.campus_share.entity.User;
import com.example.campus_share.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.campus_share.util.JwtUtils;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // 注入UserService依赖，用于用户注册和获取用户信息
    private final UserService userService;
    // 注入UserDetailsService依赖，用于加载用户详细信息
    private final UserDetailsService userDetailsService;
    // 注入PasswordEncoder依赖，用于密码加密和匹配
    private final PasswordEncoder passwordEncoder;
    // 注入JwtUtils依赖，用于生成和解析JWT令牌
    private final JwtUtils jwtUtils;

    // 构造函数，通过依赖注入初始化各个依赖
    public AuthController(UserService userService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // 处理用户注册请求
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        // 调用userService的register方法注册用户
        User registeredUser = userService.register(user);
        registeredUser.setPassword(null); // 不返回密码
        return Result.success(registeredUser);
    }

    // 使用@PostMapping注解指定该处理方法对应的URL路径为"/login"，并且该路径的HTTP请求方法为POST
    @PostMapping("/login")
    // 定义一个名为login的方法，该方法接收一个User类型的参数loginUser，并返回一个Result<Map<String, String>>类型的结果
    public Result<Map<String, String>> login(@RequestBody User loginUser) {
        // 调用userService的getUserByUsername方法，根据loginUser的username获取对应的User对象
        User user = userService.getUserByUsername(loginUser.getUsername());
        System.out.println(user);
        // 检查user是否为null或者密码是否不匹配
        if (user == null || !passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            // 如果用户不存在或密码错误，返回一个错误的结果，状态码为401，错误信息为"用户名或密码错误"
            System.out.println("wrong password or username");
            return Result.error(401, "用户名或密码错误");
        }

        // 调用userDetailsService的loadUserByUsername方法，根据user的username加载UserDetails对象
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        // 调用jwtUtils的generateToken方法，根据userDetails生成一个JWT token
        String token = jwtUtils.generateToken(userDetails);
        
        // 创建一个HashMap来存储返回的结果
        Map<String, String> result = new HashMap<>();
        // 将生成的token放入结果Map中，键为"token"
        result.put("token", token);
        // 将用户的username放入结果Map中，键为"username"
        result.put("username", user.getUsername());
        // 将用户的isAdmin状态转换为字符串并放入结果Map中，键为"isAdmin"
        result.put("isAdmin", user.getIsAdmin().toString());
        
        // 返回一个成功的结果，包含上述结果Map
        return Result.success(result);
    }
} 