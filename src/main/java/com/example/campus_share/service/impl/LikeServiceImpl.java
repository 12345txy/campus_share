package com.example.campus_share.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Like;
<<<<<<< HEAD
import com.example.campus_share.entity.PostLike;
=======
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
import com.example.campus_share.mapper.LikeMapper;
import com.example.campus_share.service.LikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {
<<<<<<< HEAD
    private final LikeMapper likeMapper;

    public LikeServiceImpl (LikeMapper likeMapper) {
        this.likeMapper = likeMapper;
    }
=======
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1

    @Override
    public boolean existsByUserIdAndTargetIdAndType(Long userId, Long targetId, Integer type) {
        return baseMapper.existsByUserIdAndTargetIdAndType(userId, targetId, type);
    }
    @Override
    @Transactional
    public boolean likeComment(Long commentId, Long userId) {
        // 先检查用户是否已经点赞过该评论
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetId, commentId)
                .eq(Like::getType, 1);

        if (this.count(wrapper) > 0) {
            // 用户已经点赞过该评论，直接返回 false
            return false;
        }

        // 创建新的点赞记录
        Like newLike = new Like();
        newLike.setUserId(userId);
        newLike.setTargetId(commentId);
        newLike.setType(1);
        newLike.setCreateTime(LocalDateTime.now());

        // 保存点赞记录
        return this.save(newLike);
    }

    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 取消点赞是否成功
     */
    @Override
    @Transactional
    public boolean unlikeComment(Long commentId, Long userId) {
        // 构建查询条件
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetId, commentId)
                .eq(Like::getType, 1);

        // 删除点赞记录
        return this.remove(wrapper);
    }
<<<<<<< HEAD
    @Override
    public int getCommentLikeCount(Long commentId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getTargetId, commentId)
                .eq(Like::getType, 1); // 1 代表评论
        return Math.toIntExact(likeMapper.selectCount(wrapper));
    }
=======
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
}