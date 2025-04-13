package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostFavorite;
import org.apache.ibatis.annotations.Param;

public interface PostFavoriteMapper extends BaseMapper<PostFavorite> {
    // 查询用户收藏的帖子列表
    IPage<Post> selectFavoritePostsByUserId(Page<Post> page, @Param("userId") Long userId);
} 