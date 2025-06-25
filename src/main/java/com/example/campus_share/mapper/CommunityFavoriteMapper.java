package com.example.campus_share.mapper;
import com.example.campus_share.entity.CommunityFavorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface CommunityFavoriteMapper extends BaseMapper<CommunityFavorite> {
    void deleteByCommunityId(Long communityId);
}