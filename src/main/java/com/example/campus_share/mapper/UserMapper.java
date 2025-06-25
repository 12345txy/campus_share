package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campus_share.entity.User;
import org.apache.ibatis.annotations.Mapper;
<<<<<<< HEAD
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
=======
import org.apache.ibatis.annotations.Select;

>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT avatar, nickname FROM t_user WHERE id = #{userId}")
    Map<String, Object> getUserAvatarUrlAndNicknameByUserId(Long userId);
    @Select("SELECT id FROM t_user WHERE username = #{username}")
    Long getUserIdByUsername(String username);
<<<<<<< HEAD

    @Select("""
    SELECT DATE(create_time) AS date, COUNT(*) AS count
    FROM t_user
    WHERE create_time BETWEEN #{start} AND #{end}
    GROUP BY DATE(create_time)
    ORDER BY DATE(create_time)
    """)
    List<Map<String, Object>> selectUserActivityStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}

=======
}
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
