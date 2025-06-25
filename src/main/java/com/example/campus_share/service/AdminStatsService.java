package com.example.campus_share.service;

import com.example.campus_share.DTO.TrendDTO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
public interface AdminStatsService {
    Map<String, Object> getDashboardStats();

    TrendDTO getUserActivityStats(LocalDate startDate, LocalDate endDate);

    Map<String, TrendDTO> getContentStats(LocalDate startDate, LocalDate endDate);


    List<Map<String, Object>> getHotPosts(String sort, int limit);
}