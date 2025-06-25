<<<<<<< HEAD
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Like;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostLike;
import com.example.campus_share.mapper.LikeMapper;
import com.example.campus_share.mapper.PostLikeMapper;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.service.PostLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements PostLikeService {

    private final PostMapper postMapper;
    private final LikeMapper likeMapper;
    public PostLikeServiceImpl(PostMapper postMapper,LikeMapper likeMapper) {
        this.postMapper = postMapper;
        this.likeMapper=likeMapper;
    }

    @Override
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        // 检查是否已点赞
        if (isLiked(postId, userId)) {
            return false;
        }

        // 创建点赞记录
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLike.setCreateTime(LocalDateTime.now());

        boolean success = this.save(postLike);

        if (success) {
            // 更新帖子的点赞数
            updatePostLikeCount(postId);
        }

        return success;
    }

    @Override
    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);

        boolean success = this.remove(wrapper);

        if (success) {
            // 更新帖子的点赞数
            updatePostLikeCount(postId);
        }

        return success;
    }

    @Override
    public boolean isLiked(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);

        return this.count(wrapper) > 0;
    }

    @Override
    public int getLikeCount(Long postId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId);

        return Math.toIntExact(this.count(wrapper));
    }

    @Override
    public List<Long> getLikeUserIds(Long postId) {
        return this.baseMapper.selectLikeUserIdsByPostId(postId);
    }

    // 更新帖子的点赞数
    private void updatePostLikeCount(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post != null) {
            int likeCount = getLikeCount(postId);
            post.setLikeCount(likeCount);
            postMapper.updateById(post);
        }
    }
    @Override
    public boolean likeComment(Long commentId, Long userId) {
        // 实现评论点赞逻辑
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetId(commentId);
        like.setType(1); // 1 表示评论
        // 调用Mapper插入点赞记录
        return likeMapper.insert(like) > 0;
    }

    @Override
    public boolean unlikeComment(Long commentId, Long userId) {
        // 实现评论取消点赞逻辑
        return likeMapper.deleteByUserIdAndTargetIdAndType(userId, commentId, 1) > 0;
    }

=======
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Like;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostLike;
import com.example.campus_share.mapper.LikeMapper;
import com.example.campus_share.mapper.PostLikeMapper;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.service.PostLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements PostLikeService {

    private final PostMapper postMapper;
    private final LikeMapper likeMapper;
    public PostLikeServiceImpl(PostMapper postMapper,LikeMapper likeMapper) {
        this.postMapper = postMapper;
        this.likeMapper=likeMapper;
    }

    @Override
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        // 检查是否已点赞
        if (isLiked(postId, userId)) {
            return false;
        }

        // 创建点赞记录
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLike.setCreateTime(LocalDateTime.now());

        boolean success = this.save(postLike);

        if (success) {
            // 更新帖子的点赞数
            updatePostLikeCount(postId);
        }

        return success;
    }

    @Override
    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);

        boolean success = this.remove(wrapper);

        if (success) {
            // 更新帖子的点赞数
            updatePostLikeCount(postId);
        }

        return success;
    }

    @Override
    public boolean isLiked(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);

        return this.count(wrapper) > 0;
    }

    @Override
    public int getLikeCount(Long postId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId);

        return Math.toIntExact(this.count(wrapper));
    }

    @Override
    public List<Long> getLikeUserIds(Long postId) {
        return this.baseMapper.selectLikeUserIdsByPostId(postId);
    }

    // 更新帖子的点赞数
    private void updatePostLikeCount(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post != null) {
            int likeCount = getLikeCount(postId);
            post.setLikeCount(likeCount);
            postMapper.updateById(post);
        }
    }
    @Override
    public boolean likeComment(Long commentId, Long userId) {
        // 实现评论点赞逻辑
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetId(commentId);
        like.setType(1); // 1 表示评论
        // 调用Mapper插入点赞记录
        return likeMapper.insert(like) > 0;
    }

    @Override
    public boolean unlikeComment(Long commentId, Long userId) {
        // 实现评论取消点赞逻辑
        return likeMapper.deleteByUserIdAndTargetIdAndType(userId, commentId, 1) > 0;
    }

>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
} 