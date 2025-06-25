package com.example.campus_share.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.campus_share.entity.User;
import com.example.campus_share.entity.UserRelation;

import java.util.List;

public interface UserRelationService extends IService<UserRelation> {
    // 关注用户
    boolean followUser(Long userId, Long followUserId);
    
    // 取消关注
    boolean unfollowUser(Long userId, Long followUserId);
    
    // 检查是否已关注
    boolean isFollowing(Long userId, Long followUserId);
    
    // 获取关注列表
    List<User> getFollowingList(Long userId);

    List<User>  getFollowerList(Long userId);

    // 获取关注数
    int getFollowingCount(Long userId);
    
    // 获取粉丝数
    int getFollowerCount(Long userId);
    
    // 更新用户的关注数和粉丝数
    void updateUserFollowStats(Long userId);
}
