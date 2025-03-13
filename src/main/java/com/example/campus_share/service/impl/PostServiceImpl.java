package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostTag;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.mapper.PostTagMapper;
import com.example.campus_share.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final PostTagMapper postTagMapper;

    public PostServiceImpl(PostTagMapper postTagMapper) {
        this.postTagMapper = postTagMapper;
    }

    @Override
    @Transactional
    public Post createPost(Post post, List<Long> tagIds) {
        // 设置初始值
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setFavoriteCount(0);
        post.setStatus(0); // 正常状态

        // 保存帖子
        this.baseMapper.insert(post);

        // 保存帖子与标签的关系
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                PostTag postTag = new PostTag();
                postTag.setPostId(post.getId());
                postTag.setTagId(tagId);
                postTagMapper.insert(postTag);
            }
        }

        return post;
    }

    @Override
    @Transactional
    public Post updatePost(Post post, List<Long> tagIds) {
        Post existPost = this.baseMapper.selectById(post.getId());
        if (existPost == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 更新帖子信息
        existPost.setTitle(post.getTitle());
        existPost.setContent(post.getContent());
        existPost.setCategoryId(post.getCategoryId());
        existPost.setCoverImage(post.getCoverImage());
        existPost.setUpdateTime(LocalDateTime.now());

        this.baseMapper.updateById(existPost);

        // 更新帖子与标签的关系
        if (tagIds != null) {
            // 先删除原有关系
            LambdaQueryWrapper<PostTag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PostTag::getPostId, post.getId());
            postTagMapper.delete(wrapper);

            // 建立新的关系
            if (!tagIds.isEmpty()) {
                for (Long tagId : tagIds) {
                    PostTag postTag = new PostTag();
                    postTag.setPostId(post.getId());
                    postTag.setTagId(tagId);
                    postTagMapper.insert(postTag);
                }
            }
        }

        return existPost;
    }

    @Override
    public void deletePost(Long id) {
        Post post = this.baseMapper.selectById(id);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 逻辑删除帖子
        post.setStatus(2); // 已删除状态
        this.baseMapper.updateById(post);

        // 注意：这里不删除评论、点赞等相关数据，以便恢复
    }

    @Override
    public Post getPostById(Long id) {
        Post post = this.baseMapper.selectById(id);
        if (post == null || post.getStatus() != 0) {
            throw new RuntimeException("帖子不存在或已删除");
        }
        
        // 增加浏览量
        post.setViewCount(post.getViewCount() + 1);
        this.baseMapper.updateById(post);
        
        return post;
    }

    @Override
    public IPage<Post> getPostsByPage(Page<Post> page) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        wrapper.orderByDesc(Post::getCreateTime);
        return this.baseMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<Post> getPostsByCategoryId(Page<Post> page, Long categoryId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        wrapper.eq(Post::getCategoryId, categoryId);
        wrapper.orderByDesc(Post::getCreateTime);
        return this.baseMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<Post> getPostsByUserId(Page<Post> page, Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        wrapper.eq(Post::getUserId, userId);
        wrapper.orderByDesc(Post::getCreateTime);
        return this.baseMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<Post> searchPosts(Page<Post> page, String keyword) {
        return this.baseMapper.searchPosts(page, keyword);
    }
} 