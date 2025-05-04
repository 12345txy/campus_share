package com.example.campus_share.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.campus_share.entity.Tag;

import java.util.List;

public interface TagService extends IService<Tag> {
    List<Tag> getAllTags();
}