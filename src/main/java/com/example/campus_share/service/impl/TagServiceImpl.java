package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Tag;
import com.example.campus_share.mapper.TagMapper;
import com.example.campus_share.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Override
    public List<Tag> getAllTags() {
        return this.list();
    }
}