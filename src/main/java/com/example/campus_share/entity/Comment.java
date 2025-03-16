package com.example.campus_share.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;        // 帖子ID
    private Long userId;        // 评论用户ID
    private Long parentId;      // 父评论ID，用于回复功能
    private String content;     // 评论内容
    private Integer status;     // 状态：0-正常 1-隐藏 2-删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer likeCount;  // 点赞数
    private Integer replyCount; // 回复数
} 