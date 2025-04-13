package com.example.campus_share.service;

import java.time.LocalDate;
import java.util.Map;

public interface AdminStatsService {
    // 获取仪表盘统计数据
    Map<String, Object> getDashboardStats();
    
    // 获取用户活跃度统计
    Map<String, Object> getUserActivityStats(LocalDate startDate, LocalDate endDate);
    
    // 获取内容统计数据
    Map<String, Object> getContentStats(LocalDate startDate, LocalDate endDate);
} 