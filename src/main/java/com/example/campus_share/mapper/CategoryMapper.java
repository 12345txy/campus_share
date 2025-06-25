package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campus_share.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper // ✅ 必须加！
public interface CategoryMapper extends BaseMapper<Category> {
    // 可以添加自定义 SQL 方法
}
