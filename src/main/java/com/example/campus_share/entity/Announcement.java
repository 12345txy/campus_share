package com.example.campus_share.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Announcement {

    // Getters and Setters
    private Long id;
    private String title;
    private String content;
    private String author = "校园印记开发者";  // 默认作者
    private LocalDateTime createdAt;  // 公告创建时间

}
