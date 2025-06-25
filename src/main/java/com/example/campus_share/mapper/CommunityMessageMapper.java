package com.example.campus_share.mapper;
import com.example.campus_share.entity.CommunityMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface CommunityMessageMapper extends BaseMapper<CommunityMessage> {
    void deleteByCommunityId(Long communityId);
}