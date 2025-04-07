package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.User;
import com.example.campus_share.entity.UserRelation;
import org.apache.ibatis.annotations.Param;

public interface UserRelationMapper extends BaseMapper<UserRelation> {
    // 查询用户关注的用户列表
    IPage<User> selectFollowingUsers(Page<User> page, @Param("userId") Long userId);
    
    // 查询用户的粉丝列表
    IPage<User> selectFollowerUsers(Page<User> page, @Param("userId") Long userId);
}
