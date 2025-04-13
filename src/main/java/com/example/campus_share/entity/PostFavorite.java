package com.example.campus_share.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_post_favorite")
public class PostFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;        // 帖子ID
    private Long userId;        // 收藏用户ID
    private LocalDateTime createTime;
} 