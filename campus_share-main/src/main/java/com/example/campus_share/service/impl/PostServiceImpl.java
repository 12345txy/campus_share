package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.PostDTO;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostTag;
import com.example.campus_share.entity.Tag;
import com.example.campus_share.mapper.PostMapper;
import com.example.campus_share.mapper.PostTagMapper;
import com.example.campus_share.mapper.TagMapper;
import com.example.campus_share.mapper.UserMapper;
import com.example.campus_share.service.PostService;
import com.example.campus_share.service.PostFavoriteService;
import com.example.campus_share.service.PostLikeService;
import com.example.campus_share.service.UserRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final PostTagMapper postTagMapper;
    private final UserMapper userMapper;
    private final TagMapper tagMapper;
    private final PostFavoriteService postFavoriteService;
    private final PostLikeService postLikeService;
    private final UserRelationService userRelationService;

    public PostServiceImpl(PostTagMapper postTagMapper, UserMapper userMapper, TagMapper tagMapper,
                           PostFavoriteService postFavoriteService, PostLikeService postLikeService,
                           UserRelationService userRelationService) {
        this.postTagMapper = postTagMapper;
        this.userMapper = userMapper;
        this.tagMapper = tagMapper;
        this.postFavoriteService = postFavoriteService;
        this.postLikeService = postLikeService;
        this.userRelationService = userRelationService;
    }
    private PostDTO convertToPostDTO(Post post, Long currentUserId) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setUserId(post.getUserId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setCategoryId(post.getCategoryId());
        postDTO.setCoverImage(post.getCoverImage());
        postDTO.setViewCount(post.getViewCount());
        postDTO.setLikeCount(post.getLikeCount());
        postDTO.setCommentCount(post.getCommentCount());
        postDTO.setFavoriteCount(post.getFavoriteCount());
        postDTO.setCreateTime(post.getCreateTime());

        // 查询用户信息
        Map<String, Object> userInfo = userMapper.getUserAvatarUrlAndNicknameByUserId(post.getUserId());
        if (userInfo != null) {
            postDTO.setUserAvatarUrl((String) userInfo.get("avatar"));
            postDTO.setUserNickname((String) userInfo.get("nickname"));
        }

        // 查询帖子的标签
        LambdaQueryWrapper<PostTag> postTagWrapper = new LambdaQueryWrapper<>();
        postTagWrapper.eq(PostTag::getPostId, post.getId());
        List<PostTag> postTags = postTagMapper.selectList(postTagWrapper);
        List<Long> tagIds = postTags.stream().map(PostTag::getTagId).collect(Collectors.toList());

        LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
        if (!tagIds.isEmpty()) {
            tagWrapper.in(Tag::getId, tagIds);
        }
        List<Tag> tags = tagMapper.selectList(tagWrapper);
        List<String> tagNames = tags.stream().map(Tag::getName).collect(Collectors.toList());
        postDTO.setTags(tagNames);

        // 处理组图字段
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            String imagesStr = post.getImages().replace("[", "").replace("]", "");
            String[] imageArray = imagesStr.split(",");
            List<String> imageList = new ArrayList<>();
            for (String image : imageArray) {
                imageList.add(image.trim().replace("'", ""));
            }
            postDTO.setImages(imageList);
        }

        // 判断当前用户是否点赞、收藏以及是否关注了帖子的作者
        if (currentUserId != null) {
            postDTO.setLiked(postLikeService.isLiked(post.getId(), currentUserId));
            postDTO.setFavorite(postFavoriteService.isFavorited(post.getId(), currentUserId));
            postDTO.setFollowing(userRelationService.isFollowing(currentUserId, post.getUserId()));
        } else {
            postDTO.setLiked(false);
            postDTO.setFavorite(false);
            postDTO.setFollowing(false);
        }

        return postDTO;
    }
    @Override
    @Transactional
    public Post createPost(Post post, List<Long> tagIds) {
        // 设置初始值
        post.setCreateTime(LocalDateTime.now()); // 设置帖子的创建时间为当前时间
        post.setUpdateTime(LocalDateTime.now()); // 设置帖子的更新时间为当前时间
        post.setViewCount(0);
        post.setLikeCount(0); // 设置帖子的点赞次数为0
        post.setCommentCount(0); // 设置帖子的评论次数为0
        post.setFavoriteCount(0); // 设置帖子的收藏次数为0
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
    public IPage<PostDTO> getPostsByPage(Page<Post> page) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        wrapper.orderByDesc(Post::getCreateTime);
        IPage<Post> postPage = this.baseMapper.selectPage(page, wrapper);

        IPage<PostDTO> postDTOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<PostDTO> postDTOList = new ArrayList<>();

        // 获取当前登录用户的ID
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = null;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            // 假设 UserMapper 中有一个方法 getUserIdByUsername 根据用户名查询用户ID
            currentUserId = userMapper.getUserIdByUsername(username);
        }

        for (Post post : postPage.getRecords()) {
            PostDTO postDTO = convertToPostDTO(post, currentUserId);
            postDTOList.add(postDTO);
        }

        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }


    @Override
    public IPage<PostDTO> getPostsByCategoryId(Page<Post> page, Long categoryId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        wrapper.eq(Post::getCategoryId, categoryId);
        wrapper.orderByDesc(Post::getCreateTime);
        IPage<Post> postPage = this.baseMapper.selectPage(page, wrapper);

        IPage<PostDTO> postDTOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<PostDTO> postDTOList = new ArrayList<>();

        // 获取当前登录用户的ID
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = null;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            // 假设 UserMapper 中有一个方法 getUserIdByUsername 根据用户名查询用户ID
            currentUserId = userMapper.getUserIdByUsername(username);
        }

        for (Post post : postPage.getRecords()) {
            PostDTO postDTO = convertToPostDTO(post, currentUserId);
            postDTOList.add(postDTO);
        }

        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }


    @Override
    public IPage<PostDTO> getPostsByUserId(Page<Post> page, Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        wrapper.eq(Post::getUserId, userId);
        wrapper.orderByDesc(Post::getCreateTime);
        IPage<Post> postPage = this.baseMapper.selectPage(page, wrapper);

        IPage<PostDTO> postDTOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<PostDTO> postDTOList = new ArrayList<>();

        // 获取当前登录用户的ID
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = null;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            currentUserId = userMapper.getUserIdByUsername(username);
        }

        for (Post post : postPage.getRecords()) {
            PostDTO postDTO = convertToPostDTO(post, currentUserId);
            postDTOList.add(postDTO);
        }

        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }

    @Override
    public IPage<PostDTO> searchPosts(Page<Post> page, String keyword) {

            IPage<Post> postPage = this.baseMapper.searchPosts(page, keyword);

            IPage<PostDTO> postDTOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
            List<PostDTO> postDTOList = new ArrayList<>();

            // 获取当前登录用户的ID
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long currentUserId = null;
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                currentUserId = userMapper.getUserIdByUsername(username);
            }

            for (Post post : postPage.getRecords()) {
                PostDTO postDTO = convertToPostDTO(post, currentUserId);
                postDTOList.add(postDTO);
            }

            postDTOPage.setRecords(postDTOList);
            return postDTOPage;

    }
}