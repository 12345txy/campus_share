package com.example.campus_share.controller.admin;

import com.example.campus_share.service.AdminStatsService;
import com.example.campus_share.util.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.campus_share.DTO.OverviewDTO;
import com.example.campus_share.DTO.TrendDTO;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

        private final AdminStatsService statsService;

        public AdminStatsController(AdminStatsService statsService) {
            this.statsService = statsService;
        }

        @GetMapping("/overview")
        public Result<Map<String, Object>> getOverview() {
            return Result.success(statsService.getDashboardStats());
        }

        @GetMapping("/trend")
        public Result<TrendDTO> getTrend(@RequestParam String type, @RequestParam String range) {
            LocalDate end = LocalDate.now();
            LocalDate start;

            switch (range) {
                case "week" -> start = end.minusWeeks(1);
                case "month" -> start = end.minusMonths(1);
                default -> start = end.minusDays(6);
            }

            return switch (type) {
                case "user" -> Result.success(statsService.getUserActivityStats(start, end));
                case "post", "comment" -> Result.success(statsService.getContentStats(start, end).get(type));
                default -> Result.success(new TrendDTO(Collections.emptyList(), Collections.emptyList()));
            };
        }


        @GetMapping("/hot-posts")
        public Result<List<Map<String, Object>>> getHotPosts(@RequestParam(defaultValue = "like") String sort,
                                                             @RequestParam(defaultValue = "10") int limit) {
            return Result.success(statsService.getHotPosts(sort, limit));
        }

}

