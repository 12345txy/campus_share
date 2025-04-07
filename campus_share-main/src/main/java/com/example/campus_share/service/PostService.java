package com.example.campus_share.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.campus_share.entity.Post;
import com.example.campus_share.entity.PostDTO;

import java.util.List;

public interface PostService extends IService<Post> {
    // 创建一个帖子，并传入帖子对象和标签ID列表
    Post createPost(Post post, List<Long> tagIds);
    // 根据传入的Post对象和tagIds列表，更新Post对象
    Post updatePost(Post post, List<Long> tagIds);
    // 根据id删除帖子
    void deletePost(Long id);
    // 根据id获取帖子
    Post getPostById(Long id);
    // 根据分页参数获取帖子列表
    IPage<PostDTO> getPostsByPage(Page<Post> page);
    // 根据分类ID获取帖子列表
    IPage<PostDTO>  getPostsByCategoryId(Page<Post> page, Long categoryId);
    // 根据用户ID获取帖子列表
    IPage<Post> getPostsByUserId(Page<Post> page, Long userId);
    // 根据关键字搜索帖子，并返回分页结果
    IPage<Post> searchPosts(Page<Post> page, String keyword);
} 