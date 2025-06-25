<<<<<<< HEAD
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
=======
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
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
}