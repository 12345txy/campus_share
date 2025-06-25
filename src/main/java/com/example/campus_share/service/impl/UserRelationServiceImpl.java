package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.User;
import com.example.campus_share.entity.UserRelation;
import com.example.campus_share.mapper.UserMapper;
import com.example.campus_share.mapper.UserRelationMapper;
import com.example.campus_share.service.UserRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class UserRelationServiceImpl extends ServiceImpl<UserRelationMapper, UserRelation> implements UserRelationService {

    private final UserMapper userMapper;

    public UserRelationServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public boolean followUser(Long userId, Long followUserId) {
        // 不能关注自己
        if (userId.equals(followUserId)) {
            return false;
        }
        
        // 检查是否已关注
        if (isFollowing(userId, followUserId)) {
            return false;
        }
        
        // 创建关注关系
        UserRelation relation = new UserRelation();
        relation.setUserId(userId);
        relation.setFollowUserId(followUserId);
        relation.setCreateTime(LocalDateTime.now());
        
        boolean success = this.save(relation);
        
        if (success) {
            // 更新关注者的关注数
            updateUserFollowStats(userId);
            // 更新被关注者的粉丝数
            updateUserFollowStats(followUserId);
        }
        
        return success;
    }

    @Override
    @Transactional
    public boolean unfollowUser(Long userId, Long followUserId) {
        LambdaQueryWrapper<UserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRelation::getUserId, userId)
               .eq(UserRelation::getFollowUserId, followUserId);
        
        boolean success = this.remove(wrapper);
        
        if (success) {
            // 更新关注者的关注数
            updateUserFollowStats(userId);
            // 更新被关注者的粉丝数
            updateUserFollowStats(followUserId);
        }
        
        return success;
    }

    @Override
    public boolean isFollowing(Long userId, Long followUserId) {
        LambdaQueryWrapper<UserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRelation::getUserId, userId)
               .eq(UserRelation::getFollowUserId, followUserId);
        
        return this.count(wrapper) > 0;
    }

    @Override
    public List<User> getFollowingList(Long userId) {
        return this.baseMapper.selectFollowingUsers(userId);
    }

    @Override
    public  List<User>  getFollowerList(Long userId) {
        return this.baseMapper.selectFollowerUsers(userId);
    }


    @Override
    public int getFollowingCount(Long userId) {
        LambdaQueryWrapper<UserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRelation::getUserId, userId);
        return Math.toIntExact(this.count(wrapper));
    }

    @Override
    public int getFollowerCount(Long userId) {
        LambdaQueryWrapper<UserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRelation::getFollowUserId, userId);
        return Math.toIntExact(this.count(wrapper));
    }

    @Override
    @Transactional
    public void updateUserFollowStats(Long userId) {
        // 获取用户关注数和粉丝数
        int followCount = getFollowingCount(userId);
        int followerCount = getFollowerCount(userId);
        
        // 更新用户的关注数和粉丝数
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setFollowCount(followCount);
            user.setFollowerCount(followerCount);
            userMapper.updateById(user);
        }
    }
}