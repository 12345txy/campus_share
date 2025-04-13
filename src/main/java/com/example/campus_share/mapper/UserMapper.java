package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campus_share.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT avatar, nickname FROM t_user WHERE id = #{userId}")
    Map<String, Object> getUserAvatarUrlAndNicknameByUserId(Long userId);
    @Select("SELECT id FROM t_user WHERE username = #{username}")
    Long getUserIdByUsername(String username);
}
