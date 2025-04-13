package com.example.campus_share.controller.admin;

import com.example.campus_share.service.AdminStatsService;
import com.example.campus_share.util.Result;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    private final AdminStatsService statsService;

    public AdminStatsController(AdminStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = statsService.getDashboardStats();
        return Result.success(stats);
    }
    
    @GetMapping("/user-activity")
    public Result<Map<String, Object>> getUserActivityStats(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        
        Map<String, Object> stats = statsService.getUserActivityStats(startDate, endDate);
        return Result.success(stats);
    }
    
    @GetMapping("/content-stats")
    public Result<Map<String, Object>> getContentStats(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        
        Map<String, Object> stats = statsService.getContentStats(startDate, endDate);
        return Result.success(stats);
    }
} 