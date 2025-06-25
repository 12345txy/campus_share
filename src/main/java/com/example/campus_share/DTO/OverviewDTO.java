package com.example.campus_share.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverviewDTO {
    private long totalUsers;
    private long totalPosts;
    private long totalComments;
    private long newUsers;
    private long newPosts;
}