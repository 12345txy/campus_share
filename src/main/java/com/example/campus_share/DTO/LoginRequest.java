package com.example.campus_share.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String interest; // 可选的兴趣参数，对应分类名称
} 