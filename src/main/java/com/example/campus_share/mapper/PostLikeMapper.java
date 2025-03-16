package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campus_share.entity.PostLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostLikeMapper extends BaseMapper<PostLike> {
    // 查询帖子的点赞用户ID列表
    List<Long> selectLikeUserIdsByPostId(@Param("postId") Long postId);
} 