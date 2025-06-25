package com.example.campus_share.service.impl;

import com.example.campus_share.DTO.TrendDTO;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.mapper.UserMapper;
import com.example.campus_share.mapper.CommentMapper;
import com.example.campus_share.service.AdminStatsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public AdminStatsServiceImpl(UserMapper userMapper, PostMapper postMapper, CommentMapper commentMapper) {
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long userTotal = userMapper.selectCount(null);
        long postTotal = postMapper.selectCount(null);
        long commentTotal = commentMapper.selectCount(null);

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long userToday = userMapper.selectUserActivityStats(todayStart, LocalDateTime.now()).stream().mapToInt(m -> (int) m.get("count")).sum();
        long postToday = postMapper.selectPostStats(todayStart, LocalDateTime.now()).stream().mapToInt(m -> (int) m.get("count")).sum();

        stats.put("userTotal", userTotal);
        stats.put("postTotal", postTotal);
        stats.put("commentTotal", commentTotal);
        stats.put("userToday", userToday);
        stats.put("postToday", postToday);

        return stats;
    }

    @Override
    public TrendDTO getUserActivityStats(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(6);
        if (endDate == null) endDate = LocalDate.now();

        List<Map<String, Object>> rawData = userMapper.selectUserActivityStats(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        return toTrendDTO(rawData);
    }

    @Override
    public Map<String, TrendDTO> getContentStats(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(6);
        if (endDate == null) endDate = LocalDate.now();

        List<Map<String, Object>> postStats = postMapper.selectPostStats(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        List<Map<String, Object>> commentStats = commentMapper.selectCommentStats(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        Map<String, TrendDTO> map = new HashMap<>();
        map.put("post", toTrendDTO(postStats));
        map.put("comment", toTrendDTO(commentStats));
        return map;
    }

    private TrendDTO toTrendDTO(List<Map<String, Object>> data) {
        List<String> dates = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (Map<String, Object> row : data) {
            dates.add(row.get("date").toString());
            values.add(Integer.parseInt(row.get("count").toString()));
        }

        return new TrendDTO(dates, values);
    }

    @Override
    public List<Map<String, Object>> getHotPosts(String sort, int limit) {
        return postMapper.selectHotPosts(sort, limit);
    }

}
