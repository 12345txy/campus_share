package com.example.campus_share.DTO;

import lombok.Data;

import java.util.List;

@Data
public class PostCreateRequest {
    private String title;
    private String content;
    private String author;       // 前端传用户名（可用于换成 userId）
    private String category;     // 分类名（可转 categoryId）
    private String coverImage;
    private List<String> image; // 帖子配图
}

