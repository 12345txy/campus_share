package com.example.campus_share.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campus_share.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeMapper extends BaseMapper<Like> {

    @Select("SELECT COUNT(*) > 0 FROM t_like WHERE user_id = #{userId} AND target_id = #{targetId} AND type = #{type}")
    boolean existsByUserIdAndTargetIdAndType(Long userId, Long targetId, Integer type);
}