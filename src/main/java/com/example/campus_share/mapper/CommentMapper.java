package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    // 查询帖子评论列表，bu!!!支持分页
    List<Comment> selectCommentsByPostId(@Param("postId") Long postId);

    // 查询评论的回复列表
    List<Comment>selectRepliesByCommentId(@Param("parentId") Long parentId);
<<<<<<< HEAD

    @Select("""
    SELECT DATE(create_time) AS date, COUNT(*) AS count
    FROM t_comment
    WHERE create_time BETWEEN #{start} AND #{end}
    GROUP BY DATE(create_time)
    ORDER BY DATE(create_time)
    """)
    List<Map<String, Object>> selectCommentStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
=======
} 
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
