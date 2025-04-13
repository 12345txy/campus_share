package com.example.campus_share.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;        // 角色名称：ROLE_ADMIN, ROLE_USER
    private String description;  // 角色描述
} 