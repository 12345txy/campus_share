<<<<<<< HEAD
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.*;
import com.example.campus_share.mapper.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final PostTagMapper postTagMapper;
    private final UserMapper userMapper;
    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;
    private final PostFavoriteMapper postFavoriteMapper;
    private final TagMapper tagMapper;
    private final PostFavoriteService postFavoriteService;
    private final PostLikeService postLikeService;
    private final UserRelationService userRelationService;


    public PostServiceImpl(PostTagMapper postTagMapper, UserMapper userMapper, TagMapper tagMapper,
                           PostFavoriteService postFavoriteService, PostLikeService postLikeService,
                           UserRelationService userRelationService,PostLikeMapper postLikeMapper,PostMapper postMapper,PostFavoriteMapper postFavoriteMapper) {
        this.postTagMapper = postTagMapper;
        this.postLikeMapper=postLikeMapper;
        this.postMapper=postMapper;
        this.userMapper = userMapper;
        this.tagMapper = tagMapper;
        this.postFavoriteService = postFavoriteService;
        this.postLikeService = postLikeService;
        this.userRelationService = userRelationService;
        this.postFavoriteMapper=postFavoriteMapper;
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

        postDTO.setImages(post.getImage());


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
    public IPage<PostDTO> getFavoritesPostsByUserId(Page<Post> page, Long userId) {
        // 获取当前登录用户的ID
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = null;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            currentUserId = userMapper.getUserIdByUsername(username);
        }
        IPage<Post> postPage=getFavoritePostsByUserId(page, userId);

        IPage<PostDTO> postDTOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<PostDTO> postDTOList = new ArrayList<>();
        for (Post post : postPage.getRecords()) {
            PostDTO postDTO = convertToPostDTO(post, currentUserId);
            postDTOList.add(postDTO);
        }
        postDTOPage.setTotal(postPage.getTotal());
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }
    @Override
    public IPage<Post> getFavoritePostsByUserId(Page<Post> page, Long userId) {
        // 构建 PostFavorite 的查询条件
        LambdaQueryWrapper<PostFavorite> postFavoriteWrapper = new LambdaQueryWrapper<>();
        postFavoriteWrapper.eq(PostFavorite::getUserId, userId);
        // 查询指定用户收藏的所有 PostFavorite 记录
        List<PostFavorite> postFavorites = postFavoriteMapper.selectList(postFavoriteWrapper);

        // 提取出收藏记录中的 postId 列表
        List<Long> postIds = postFavorites.stream()
                .map(PostFavorite::getPostId)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            return new Page<>();
        }

        // 构建 Post 的查询条件
        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.in(Post::getId, postIds);

        // 根据 postId 列表从 t_post 表中查询帖子列表
        return postMapper.selectPage(page, postWrapper);
    }
    @Override
    public IPage<PostDTO> getLikesPostsByUserId(Page<Post> page, Long userId) {
        // 获取当前登录用户的ID
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = null;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            currentUserId = userMapper.getUserIdByUsername(username);
        }
        IPage<Post> postPage=getLikePostsByUserId(page, userId);
        IPage<PostDTO> postDTOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<PostDTO> postDTOList = new ArrayList<>();
        for (Post post : postPage.getRecords()) {
            PostDTO postDTO = convertToPostDTO(post, currentUserId);
            postDTOList.add(postDTO);
        }
        postDTOPage.setTotal(postPage.getTotal());
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }
    @Override
    public IPage<Post> getLikePostsByUserId(Page<Post> page, Long userId) {
        // 构建 PostLike 的查询条件
        LambdaQueryWrapper<PostLike> postLikeWrapper = new LambdaQueryWrapper<>();
        postLikeWrapper.eq(PostLike::getUserId, userId);
        // 查询指定用户点赞的所有 PostLike 记录
        List<PostLike> postLikes = postLikeMapper.selectList(postLikeWrapper);

        // 提取出点赞记录中的 postId 列表
        List<Long> postIds = postLikes.stream()
                .map(PostLike::getPostId)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            return new Page<>();
        }

        // 构建 Post 的查询条件
        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.in(Post::getId, postIds);

        // 根据 postId 列表从 t_post 表中查询帖子列表
        return postMapper.selectPage(page, postWrapper);
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
    public IPage<PostDTO> getPostsByPage(Page<Post> page,int sortOption,List<String> tags) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        // 根据 sortOption 动态设置排序条件
        if (sortOption == 0) {
            wrapper.orderByDesc(Post::getCreateTime);
        } else if (sortOption == 1) {
            wrapper.orderByDesc(Post::getPopularity);
        }
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
        int before=postDTOList.size();
// 使用 filterPostDTOsByTags 方法筛选帖子
        if (tags != null && !tags.isEmpty()) {
            postDTOList = PostDTO.filterPostDTOsByTags(tags, postDTOList);
        }
        int after=postDTOList.size();

        // 更新 postDTOPage 的总数
        postDTOPage.setTotal(postPage.getTotal()-before+after);
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }


    @Override
    public IPage<PostDTO> getPostsByCategoryId(Page<Post> page, Long categoryId,int sortOption,List<String> tags) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        wrapper.eq(Post::getCategoryId, categoryId);
        // 根据 sortOption 动态设置排序条件
        if (sortOption == 0) {
            wrapper.orderByDesc(Post::getCreateTime);
        } else if (sortOption == 1) {
            wrapper.orderByDesc(Post::getPopularity);
        }
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
        int before=postDTOList.size();
// 使用 filterPostDTOsByTags 方法筛选帖子
        if (tags != null && !tags.isEmpty()) {
            postDTOList = PostDTO.filterPostDTOsByTags(tags, postDTOList);
        }
        int after=postDTOList.size();

        // 更新 postDTOPage 的总数
        postDTOPage.setTotal(postPage.getTotal()-before+after);
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }


    @Override
    public IPage<PostDTO> getPostsByUserId(Page<Post> page, Long userId,int sortOption,List<String> tags) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        wrapper.eq(Post::getUserId, userId);
        // 根据 sortOption 动态设置排序条件
        if (sortOption == 0) {
            wrapper.orderByDesc(Post::getCreateTime);
        } else if (sortOption == 1) {
            wrapper.orderByDesc(Post::getPopularity);
        }
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
        int before=postDTOList.size();
// 使用 filterPostDTOsByTags 方法筛选帖子
        if (tags != null && !tags.isEmpty()) {
            postDTOList = PostDTO.filterPostDTOsByTags(tags, postDTOList);
        }
        int after=postDTOList.size();

        // 更新 postDTOPage 的总数
        postDTOPage.setTotal(postPage.getTotal()-before+after);
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }

    @Override
    public IPage<PostDTO> searchPosts(Page<Post> page, String keyword, int sortOption,List<String> tags) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        // 构建搜索条件，假设搜索 title 和 content 字段
        wrapper.like(Post::getTitle, keyword).or().like(Post::getContent, keyword);
        wrapper.eq(Post::getStatus, 0); // 正常状态

        // 根据 sortOption 动态设置排序条件
        if (sortOption == 0) {
            wrapper.orderByDesc(Post::getCreateTime);
        } else if (sortOption == 1) {
            wrapper.orderByDesc(Post::getPopularity);
        }

        IPage<Post> postPage = this.baseMapper.selectPage(page, wrapper);

        IPage<PostDTO> postDTOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<PostDTO> postDTOList = new ArrayList<>();

        // 获取当前登录用户的 ID
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
        int before=postDTOList.size();
// 使用 filterPostDTOsByTags 方法筛选帖子
        if (tags != null && !tags.isEmpty()) {
            postDTOList = PostDTO.filterPostDTOsByTags(tags, postDTOList);
        }
        int after=postDTOList.size();

        // 更新 postDTOPage 的总数
        postDTOPage.setTotal(postPage.getTotal()-before+after);
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }

=======
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.*;
import com.example.campus_share.mapper.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final PostTagMapper postTagMapper;
    private final UserMapper userMapper;
    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;
    private final PostFavoriteMapper postFavoriteMapper;
    private final TagMapper tagMapper;
    private final PostFavoriteService postFavoriteService;
    private final PostLikeService postLikeService;
    private final UserRelationService userRelationService;


    public PostServiceImpl(PostTagMapper postTagMapper, UserMapper userMapper, TagMapper tagMapper,
                           PostFavoriteService postFavoriteService, PostLikeService postLikeService,
                           UserRelationService userRelationService,PostLikeMapper postLikeMapper,PostMapper postMapper,PostFavoriteMapper postFavoriteMapper) {
        this.postTagMapper = postTagMapper;
        this.postLikeMapper=postLikeMapper;
        this.postMapper=postMapper;
        this.userMapper = userMapper;
        this.tagMapper = tagMapper;
        this.postFavoriteService = postFavoriteService;
        this.postLikeService = postLikeService;
        this.userRelationService = userRelationService;
        this.postFavoriteMapper=postFavoriteMapper;
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

        postDTO.setImages(post.getImage());


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
    public IPage<PostDTO> getFavoritesPostsByUserId(Page<Post> page, Long userId) {
        // 获取当前登录用户的ID
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = null;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            currentUserId = userMapper.getUserIdByUsername(username);
        }
        IPage<Post> postPage=getFavoritePostsByUserId(page, userId);

        IPage<PostDTO> postDTOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<PostDTO> postDTOList = new ArrayList<>();
        for (Post post : postPage.getRecords()) {
            PostDTO postDTO = convertToPostDTO(post, currentUserId);
            postDTOList.add(postDTO);
        }
        postDTOPage.setTotal(postPage.getTotal());
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }
    @Override
    public IPage<Post> getFavoritePostsByUserId(Page<Post> page, Long userId) {
        // 构建 PostFavorite 的查询条件
        LambdaQueryWrapper<PostFavorite> postFavoriteWrapper = new LambdaQueryWrapper<>();
        postFavoriteWrapper.eq(PostFavorite::getUserId, userId);
        // 查询指定用户收藏的所有 PostFavorite 记录
        List<PostFavorite> postFavorites = postFavoriteMapper.selectList(postFavoriteWrapper);

        // 提取出收藏记录中的 postId 列表
        List<Long> postIds = postFavorites.stream()
                .map(PostFavorite::getPostId)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            return new Page<>();
        }

        // 构建 Post 的查询条件
        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.in(Post::getId, postIds);

        // 根据 postId 列表从 t_post 表中查询帖子列表
        return postMapper.selectPage(page, postWrapper);
    }
    @Override
    public IPage<PostDTO> getLikesPostsByUserId(Page<Post> page, Long userId) {
        // 获取当前登录用户的ID
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = null;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            currentUserId = userMapper.getUserIdByUsername(username);
        }
        IPage<Post> postPage=getLikePostsByUserId(page, userId);
        IPage<PostDTO> postDTOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<PostDTO> postDTOList = new ArrayList<>();
        for (Post post : postPage.getRecords()) {
            PostDTO postDTO = convertToPostDTO(post, currentUserId);
            postDTOList.add(postDTO);
        }
        postDTOPage.setTotal(postPage.getTotal());
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }
    @Override
    public IPage<Post> getLikePostsByUserId(Page<Post> page, Long userId) {
        // 构建 PostLike 的查询条件
        LambdaQueryWrapper<PostLike> postLikeWrapper = new LambdaQueryWrapper<>();
        postLikeWrapper.eq(PostLike::getUserId, userId);
        // 查询指定用户点赞的所有 PostLike 记录
        List<PostLike> postLikes = postLikeMapper.selectList(postLikeWrapper);

        // 提取出点赞记录中的 postId 列表
        List<Long> postIds = postLikes.stream()
                .map(PostLike::getPostId)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            return new Page<>();
        }

        // 构建 Post 的查询条件
        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.in(Post::getId, postIds);

        // 根据 postId 列表从 t_post 表中查询帖子列表
        return postMapper.selectPage(page, postWrapper);
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
    public IPage<PostDTO> getPostsByPage(Page<Post> page,int sortOption,List<String> tags) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        // 根据 sortOption 动态设置排序条件
        if (sortOption == 0) {
            wrapper.orderByDesc(Post::getCreateTime);
        } else if (sortOption == 1) {
            wrapper.orderByDesc(Post::getPopularity);
        }
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
        int before=postDTOList.size();
// 使用 filterPostDTOsByTags 方法筛选帖子
        if (tags != null && !tags.isEmpty()) {
            postDTOList = PostDTO.filterPostDTOsByTags(tags, postDTOList);
        }
        int after=postDTOList.size();

        // 更新 postDTOPage 的总数
        postDTOPage.setTotal(postPage.getTotal()-before+after);
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }


    @Override
    public IPage<PostDTO> getPostsByCategoryId(Page<Post> page, Long categoryId,int sortOption,List<String> tags) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        wrapper.eq(Post::getCategoryId, categoryId);
        // 根据 sortOption 动态设置排序条件
        if (sortOption == 0) {
            wrapper.orderByDesc(Post::getCreateTime);
        } else if (sortOption == 1) {
            wrapper.orderByDesc(Post::getPopularity);
        }
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
        int before=postDTOList.size();
// 使用 filterPostDTOsByTags 方法筛选帖子
        if (tags != null && !tags.isEmpty()) {
            postDTOList = PostDTO.filterPostDTOsByTags(tags, postDTOList);
        }
        int after=postDTOList.size();

        // 更新 postDTOPage 的总数
        postDTOPage.setTotal(postPage.getTotal()-before+after);
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }


    @Override
    public IPage<PostDTO> getPostsByUserId(Page<Post> page, Long userId,int sortOption,List<String> tags) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 0); // 正常状态
        wrapper.eq(Post::getUserId, userId);
        // 根据 sortOption 动态设置排序条件
        if (sortOption == 0) {
            wrapper.orderByDesc(Post::getCreateTime);
        } else if (sortOption == 1) {
            wrapper.orderByDesc(Post::getPopularity);
        }
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
        int before=postDTOList.size();
// 使用 filterPostDTOsByTags 方法筛选帖子
        if (tags != null && !tags.isEmpty()) {
            postDTOList = PostDTO.filterPostDTOsByTags(tags, postDTOList);
        }
        int after=postDTOList.size();

        // 更新 postDTOPage 的总数
        postDTOPage.setTotal(postPage.getTotal()-before+after);
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }

    @Override
    public IPage<PostDTO> searchPosts(Page<Post> page, String keyword, int sortOption,List<String> tags) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        // 构建搜索条件，假设搜索 title 和 content 字段
        wrapper.like(Post::getTitle, keyword).or().like(Post::getContent, keyword);
        wrapper.eq(Post::getStatus, 0); // 正常状态

        // 根据 sortOption 动态设置排序条件
        if (sortOption == 0) {
            wrapper.orderByDesc(Post::getCreateTime);
        } else if (sortOption == 1) {
            wrapper.orderByDesc(Post::getPopularity);
        }

        IPage<Post> postPage = this.baseMapper.selectPage(page, wrapper);

        IPage<PostDTO> postDTOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<PostDTO> postDTOList = new ArrayList<>();

        // 获取当前登录用户的 ID
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
        int before=postDTOList.size();
// 使用 filterPostDTOsByTags 方法筛选帖子
        if (tags != null && !tags.isEmpty()) {
            postDTOList = PostDTO.filterPostDTOsByTags(tags, postDTOList);
        }
        int after=postDTOList.size();

        // 更新 postDTOPage 的总数
        postDTOPage.setTotal(postPage.getTotal()-before+after);
        postDTOPage.setRecords(postDTOList);
        return postDTOPage;
    }

>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
}