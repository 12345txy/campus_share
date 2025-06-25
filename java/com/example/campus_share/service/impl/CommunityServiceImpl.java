<<<<<<< HEAD
package com.example.campus_share.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.*;
import com.example.campus_share.mapper.*;
import com.example.campus_share.service.CommunityService;
import com.example.campus_share.entity.CommunityMessageVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, Community> implements CommunityService {

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private CommunityMessageMapper messageMapper;

    @Autowired
    private CommunityFavoriteMapper favoriteMapper;

    @Autowired
    private UserMapper userMapper;  // 用户Mapper

    @Override
    @Transactional
    public Community createCommunity(Community community) {
        community.setCreateTime(LocalDateTime.now());
        communityMapper.insert(community);

        // 创建者自动收藏社群
        CommunityFavorite favorite = new CommunityFavorite();
        favorite.setCommunityId(community.getId());
        favorite.setUserId(community.getOwnerId());
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);

        return community;
    }

    @Override
    @Transactional
    public boolean deleteCommunity(Long communityId, Long userId) {
        Community community = communityMapper.selectById(communityId);
        if (community == null || !community.getOwnerId().equals(userId)) {
            return false;
        }

        messageMapper.deleteByCommunityId(communityId);
        favoriteMapper.deleteByCommunityId(communityId);
        communityMapper.deleteById(communityId);

        return true;
    }

    @Override
    public Community getCommunityById(Long communityId) {
        return communityMapper.selectById(communityId);
    }

    @Override
    public List<Community> listCommunities(String keyword, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        QueryWrapper<Community> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("name", keyword)
                    .or()
                    .like("description", keyword);
        }

        // 设置分页参数
        Page<Community> page = new Page<>(pageNum, pageSize);

        // 执行分页查询
        IPage<Community> communityPage = communityMapper.selectPage(page, queryWrapper);

        return communityPage.getRecords();
    }

    @Override
    @Transactional
    public CommunityMessage sendMessage(Long communityId, Long userId, String content) {
        // 检查社群是否存在（不再检查用户是否是成员）
        Community community = communityMapper.selectById(communityId);
        if (community == null) {
            throw new RuntimeException("社群不存在");
        }

        CommunityMessage message = new CommunityMessage();
        message.setCommunityId(communityId);
        message.setUserId(userId);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);

        return message;
    }

    @Override
    public List<CommunityMessageVO> listMessages(Long communityId, Integer pageNum, Integer pageSize) {
        // 查询消息列表
        List<CommunityMessage> messages = messageMapper.selectList(
                new QueryWrapper<CommunityMessage>()
                        .eq("community_id", communityId)
                        .orderByDesc("create_time")
                        .last("LIMIT " + (pageNum-1)*pageSize + "," + pageSize)
        );

        // 转换为VO并填充用户信息
        List<CommunityMessageVO> result = new ArrayList<>();
        for (CommunityMessage message : messages) {
            CommunityMessageVO vo = new CommunityMessageVO();
            BeanUtils.copyProperties(message, vo);

            // 查询用户信息
            User user = userMapper.selectById(message.getUserId());
            if (user != null) {
                vo.setUsername(user.getNickname());
                vo.setAvatarUrl(user.getAvatar());
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    @Transactional
    public boolean favoriteCommunity(Long communityId, Long userId) {
        // 检查是否已收藏
        CommunityFavorite existFavorite = favoriteMapper.selectOne(
                new QueryWrapper<CommunityFavorite>()
                        .eq("community_id", communityId)
                        .eq("user_id", userId)
        );

        if (existFavorite != null) {
            return false;
        }

        CommunityFavorite favorite = new CommunityFavorite();
        favorite.setCommunityId(communityId);
        favorite.setUserId(userId);
        favorite.setCreateTime(LocalDateTime.now());
        return favoriteMapper.insert(favorite) > 0;
    }

    @Override
    @Transactional
    public boolean unfavoriteCommunity(Long communityId, Long userId) {
        return favoriteMapper.delete(
                new QueryWrapper<CommunityFavorite>()
                        .eq("community_id", communityId)
                        .eq("user_id", userId)
        ) > 0;
    }

    @Override
    public boolean isCommunityFavorited(Long communityId, Long userId) {
        return favoriteMapper.selectCount(
                new QueryWrapper<CommunityFavorite>()
                        .eq("community_id", communityId)
                        .eq("user_id", userId)
        ) > 0;
    }

    @Override
    public List<Community> listFavoriteCommunities(Long userId) {
        // 查询用户收藏的所有社群ID
        List<Long> communityIds = favoriteMapper.selectObjs(
                        new QueryWrapper<CommunityFavorite>()
                                .select("community_id")
                                .eq("user_id", userId)
                ).stream()
                .map(id -> (Long) id)
                .collect(Collectors.toList());

        if (communityIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 根据社群ID列表查询社群信息
        return communityMapper.selectBatchIds(communityIds);
    }
=======
package com.example.campus_share.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campus_share.entity.*;
import com.example.campus_share.mapper.*;
import com.example.campus_share.service.CommunityService;
import com.example.campus_share.entity.CommunityMessageVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, Community> implements CommunityService {

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private CommunityMessageMapper messageMapper;

    @Autowired
    private CommunityFavoriteMapper favoriteMapper;

    @Autowired
    private UserMapper userMapper;  // 用户Mapper

    @Override
    @Transactional
    public Community createCommunity(Community community) {
        community.setCreateTime(LocalDateTime.now());
        communityMapper.insert(community);

        // 创建者自动收藏社群
        CommunityFavorite favorite = new CommunityFavorite();
        favorite.setCommunityId(community.getId());
        favorite.setUserId(community.getOwnerId());
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);

        return community;
    }

    @Override
    @Transactional
    public boolean deleteCommunity(Long communityId, Long userId) {
        Community community = communityMapper.selectById(communityId);
        if (community == null || !community.getOwnerId().equals(userId)) {
            return false;
        }

        messageMapper.deleteByCommunityId(communityId);
        favoriteMapper.deleteByCommunityId(communityId);
        communityMapper.deleteById(communityId);

        return true;
    }

    @Override
    public Community getCommunityById(Long communityId) {
        return communityMapper.selectById(communityId);
    }

    @Override
    public List<Community> listCommunities(String keyword, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        QueryWrapper<Community> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("name", keyword)
                    .or()
                    .like("description", keyword);
        }

        // 设置分页参数
        Page<Community> page = new Page<>(pageNum, pageSize);

        // 执行分页查询
        IPage<Community> communityPage = communityMapper.selectPage(page, queryWrapper);

        return communityPage.getRecords();
    }

    @Override
    @Transactional
    public CommunityMessage sendMessage(Long communityId, Long userId, String content) {
        // 检查社群是否存在（不再检查用户是否是成员）
        Community community = communityMapper.selectById(communityId);
        if (community == null) {
            throw new RuntimeException("社群不存在");
        }

        CommunityMessage message = new CommunityMessage();
        message.setCommunityId(communityId);
        message.setUserId(userId);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);

        return message;
    }

    @Override
    public List<CommunityMessageVO> listMessages(Long communityId, Integer pageNum, Integer pageSize) {
        // 查询消息列表
        List<CommunityMessage> messages = messageMapper.selectList(
                new QueryWrapper<CommunityMessage>()
                        .eq("community_id", communityId)
                        .orderByDesc("create_time")
                        .last("LIMIT " + (pageNum-1)*pageSize + "," + pageSize)
        );

        // 转换为VO并填充用户信息
        List<CommunityMessageVO> result = new ArrayList<>();
        for (CommunityMessage message : messages) {
            CommunityMessageVO vo = new CommunityMessageVO();
            BeanUtils.copyProperties(message, vo);

            // 查询用户信息
            User user = userMapper.selectById(message.getUserId());
            if (user != null) {
                vo.setUsername(user.getNickname());
                vo.setAvatarUrl(user.getAvatar());
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    @Transactional
    public boolean favoriteCommunity(Long communityId, Long userId) {
        // 检查是否已收藏
        CommunityFavorite existFavorite = favoriteMapper.selectOne(
                new QueryWrapper<CommunityFavorite>()
                        .eq("community_id", communityId)
                        .eq("user_id", userId)
        );

        if (existFavorite != null) {
            return false;
        }

        CommunityFavorite favorite = new CommunityFavorite();
        favorite.setCommunityId(communityId);
        favorite.setUserId(userId);
        favorite.setCreateTime(LocalDateTime.now());
        return favoriteMapper.insert(favorite) > 0;
    }

    @Override
    @Transactional
    public boolean unfavoriteCommunity(Long communityId, Long userId) {
        return favoriteMapper.delete(
                new QueryWrapper<CommunityFavorite>()
                        .eq("community_id", communityId)
                        .eq("user_id", userId)
        ) > 0;
    }

    @Override
    public boolean isCommunityFavorited(Long communityId, Long userId) {
        return favoriteMapper.selectCount(
                new QueryWrapper<CommunityFavorite>()
                        .eq("community_id", communityId)
                        .eq("user_id", userId)
        ) > 0;
    }

    @Override
    public List<Community> listFavoriteCommunities(Long userId) {
        // 查询用户收藏的所有社群ID
        List<Long> communityIds = favoriteMapper.selectObjs(
                        new QueryWrapper<CommunityFavorite>()
                                .select("community_id")
                                .eq("user_id", userId)
                ).stream()
                .map(id -> (Long) id)
                .collect(Collectors.toList());

        if (communityIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 根据社群ID列表查询社群信息
        return communityMapper.selectBatchIds(communityIds);
    }
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
}