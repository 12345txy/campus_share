package com.example.campus_share.controller;

import com.example.campus_share.entity.*;
import com.example.campus_share.service.CommunityService;
import com.example.campus_share.common.Result;
import com.example.campus_share.entity.CommunityMessageVO;
import com.example.campus_share.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {

    @Autowired
    private CommunityService communityService;
    @Autowired
    private  UserService userService;


    // 创建社群
    @PostMapping
    public Result<Community> createCommunity(@RequestBody Community community
                                             ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
         Long userId=currentUser.getId();
        community.setOwnerId(userId);
        Community result = communityService.createCommunity(community);
        return Result.success(result);
    }

    // 删除社群
    @DeleteMapping("/{communityId}")
    public Result<Boolean> deleteCommunity(@PathVariable Long communityId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        Long userId=currentUser.getId();
        boolean success = communityService.deleteCommunity(communityId, userId);
        return Result.success(success);
    }

    // 获取社群详情
    @GetMapping("/{communityId}")
    public Result<Community> getCommunity(@PathVariable Long communityId) {
        Community community = communityService.getCommunityById(communityId);
        return Result.success(community);
    }

    // 分页查询社群列表
    @GetMapping
    public Result<List<Community>> listCommunities(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Community> communities = communityService.listCommunities(keyword, pageNum, pageSize);
        return Result.success(communities);
    }

    // 发送消息
    @PostMapping("/{communityId}/messages")
    public Result<CommunityMessage> sendMessage(@PathVariable Long communityId,

                                                @RequestBody String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        Long userId=currentUser.getId();
        CommunityMessage message = communityService.sendMessage(communityId, userId, content);
        return Result.success(message);
    }

    // 获取消息列表
    @GetMapping("/{communityId}/messages")
    public Result<List<CommunityMessageVO>> listMessages(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        List<CommunityMessageVO> messages = communityService.listMessages(communityId, pageNum, pageSize);
        return Result.success(messages);
    }

    // 收藏社群
    @PostMapping("/{communityId}/favorite")
    public Result<Boolean> favoriteCommunity(@PathVariable Long communityId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        Long userId=currentUser.getId();
        boolean success = communityService.favoriteCommunity(communityId, userId);
        return Result.success(success);
    }

    // 取消收藏
    @DeleteMapping("/{communityId}/favorite")
    public Result<Boolean> unfavoriteCommunity(@PathVariable Long communityId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        Long userId=currentUser.getId();
        boolean success = communityService.unfavoriteCommunity(communityId, userId);
        return Result.success(success);
    }

    // 检查是否已收藏
    @GetMapping("/{communityId}/isFavorite")
    public Result<Boolean> isCommunityFavorited(@PathVariable Long communityId,
                                                @RequestParam Long userId) {

        boolean isFavorited = communityService.isCommunityFavorited(communityId, userId);
        return Result.success(isFavorited);
    }

    // 获取用户收藏的社群列表
    @GetMapping("/favorites")
    public Result<List<Community>> listFavoriteCommunities(
            @RequestParam Long userId) {

        List<Community> communities = communityService.listFavoriteCommunities(userId);
        return Result.success(communities);
    }
}