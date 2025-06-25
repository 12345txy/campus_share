package com.example.campus_share.service;
import com.example.campus_share.entity.Community;
import com.example.campus_share.entity.CommunityMessage;
import com.example.campus_share.entity.CommunityFavorite;
import com.example.campus_share.entity.CommunityMessageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CommunityService extends IService<Community> {

    // 社群基本操作
    Community createCommunity(Community community);
    boolean deleteCommunity(Long communityId, Long userId);
    Community getCommunityById(Long communityId);
    List<Community> listCommunities(String keyword, Integer pageNum, Integer pageSize);

    // 消息管理
    CommunityMessage sendMessage(Long communityId, Long userId, String content);
    List<CommunityMessageVO> listMessages(Long communityId, Integer pageNum, Integer pageSize);

    // 收藏管理
    boolean favoriteCommunity(Long communityId, Long userId);
    boolean unfavoriteCommunity(Long communityId, Long userId);
    boolean isCommunityFavorited(Long communityId, Long userId);
    List<Community> listFavoriteCommunities(Long userId);
}
