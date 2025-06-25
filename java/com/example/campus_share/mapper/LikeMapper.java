package com.example.campus_share.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campus_share.entity.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeMapper extends BaseMapper<Like> {

    @Select("SELECT COUNT(*) > 0 FROM t_like WHERE user_id = #{userId} AND target_id = #{targetId} AND type = #{type}")
    boolean existsByUserIdAndTargetIdAndType(Long userId, Long targetId, Integer type);
    int insert(Like like);

    @Delete("DELETE FROM t_like WHERE user_id = #{userId} AND target_id = #{targetId} AND type = #{type}")
    int deleteByUserIdAndTargetIdAndType(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("type") Integer type);
}