package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.User;
import com.example.campus_share.entity.Comment;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.mapper.UserMapper;
import com.example.campus_share.mapper.CommentMapper;
import com.example.campus_share.service.AdminStatsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        
        // 总用户数
        long totalUsers = userMapper.selectCount(null);
        
        // 总帖子数
        long totalPosts = postMapper.selectCount(null);
        
        // 总评论数
        long totalComments = commentMapper.selectCount(null);
        
        // 今日新增用户
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.ge(User::getCreateTime, todayStart);
        long newUsers = userMapper.selectCount(userWrapper);
        
        // 今日新增帖子
        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.ge(Post::getCreateTime, todayStart);
        long newPosts = postMapper.selectCount(postWrapper);
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalPosts", totalPosts);
        stats.put("totalComments", totalComments);
        stats.put("newUsers", newUsers);
        stats.put("newPosts", newPosts);
        
        return stats;
    }

    @Override
    public Map<String, Object> getUserActivityStats(LocalDate startDate, LocalDate endDate) {
        // 如果没有指定日期，默认为最近30天
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(29);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // TODO: 实现按日期统计用户活跃度的逻辑，可以使用自定义SQL
        
        return stats;
    }

    @Override
    public Map<String, Object> getContentStats(LocalDate startDate, LocalDate endDate) {
        // 如果没有指定日期，默认为最近30天
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(29);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // TODO: 实现按日期统计内容数据的逻辑，可以使用自定义SQL
        
        return stats;
    }
} 