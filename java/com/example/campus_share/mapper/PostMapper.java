package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    IPage<Post> searchPosts(Page<Post> page, @Param("keyword") String keyword);
    IPage<Post> selectLikePostsByUserId(Page<Post> page, @Param("userId") Long userId);

    @Select("""
    SELECT DATE(create_time) AS date, COUNT(*) AS count
    FROM t_post
    WHERE create_time BETWEEN #{start} AND #{end}
    GROUP BY DATE(create_time)
    ORDER BY DATE(create_time)
    """)
    List<Map<String, Object>> selectPostStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Select("""
    SELECT p.title, u.nickname AS author, p.like_count AS likeCount, p.comment_count AS commentCount
    FROM t_post p
    JOIN t_user u ON p.user_id = u.id
    ORDER BY 
        CASE WHEN #{sort} = 'like' THEN p.like_count
             WHEN #{sort} = 'comment' THEN p.comment_count
        END DESC
    LIMIT #{limit}
    """)
    List<Map<String, Object>> selectHotPosts(@Param("sort") String sort, @Param("limit") int limit);

}