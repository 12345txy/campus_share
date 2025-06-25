<<<<<<< HEAD
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.campus_share.entity.Category;
import com.example.campus_share.mapper.CategoryMapper;
import com.example.campus_share.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Long getIdByName(String name) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        Category category = categoryMapper.selectOne(wrapper);
        return category != null ? category.getId() : null;
    }
}
=======
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.campus_share.entity.Category;
import com.example.campus_share.mapper.CategoryMapper;
import com.example.campus_share.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Long getIdByName(String name) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        Category category = categoryMapper.selectOne(wrapper);
        return category != null ? category.getId() : null;
    }
}
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
