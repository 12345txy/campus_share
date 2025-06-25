package com.example.campus_share.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String bio;
    private String email;
    private String phone;
    private Integer gender;
    private Integer status; // 0-正常 1-禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isAdmin; // 0-普通用户 1-管理员
    private Integer followCount;    // 关注数
    private Integer followerCount;  // 粉丝数
} 