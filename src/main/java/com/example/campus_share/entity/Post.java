package com.example.campus_share.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data // Lombok注解，自动生成getter、setter、toString、equals、hashCode方法
@TableName("t_post") // MyBatis-Plus注解，指定该实体类对应的数据库表名为t_post
public class Post {
    @TableId(type = IdType.AUTO) // MyBatis-Plus注解，指定该字段为表的主键，且主键生成策略为自增
    private Long id; // 帖子ID
    private Long userId; // 用户ID，关联用户表的外键
    private String title; // 帖子标题
    private String content; // 帖子内容
    private Long categoryId; // 分类ID，关联分类表的外键
    private String coverImage; // 封面图片URL
    private Integer status; // 0-正常 1-审核中 2-已删除
    private Integer viewCount; // 查看次数
    private Integer likeCount; // 点赞次数
    private Integer commentCount; // 评论次数
    private Integer favoriteCount; // 收藏次数
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;


} 