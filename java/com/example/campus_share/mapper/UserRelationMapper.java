<<<<<<< HEAD
package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.User;
import com.example.campus_share.entity.UserRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRelationMapper extends BaseMapper<UserRelation> {
    // 查询用户关注的用户列表
    List<User> selectFollowingUsers(@Param("userId") Long userId);

    // 查询用户的粉丝列表
    List<User> selectFollowerUsers(@Param("userId") Long userId);
    // 查询用户关注的用户列表（不分页）

}
=======
package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.User;
import com.example.campus_share.entity.UserRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRelationMapper extends BaseMapper<UserRelation> {
    // 查询用户关注的用户列表
    List<User> selectFollowingUsers(@Param("userId") Long userId);

    // 查询用户的粉丝列表
    List<User> selectFollowerUsers(@Param("userId") Long userId);
    // 查询用户关注的用户列表（不分页）

}
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
