package com.example.campus_share.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Long categoryId;
    private String coverImage;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private LocalDateTime createTime;
    private String userAvatarUrl;
    private String userNickname;
    private List<String> tags;
    private List<String> images;
    private boolean isFavorite;
    private boolean isLiked;
    private boolean isFollowing;

}