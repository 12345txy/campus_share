package com.example.campus_share.service;

import com.example.campus_share.entity.Category;
import java.util.List;

public interface CategoryService {
        Long getIdByName(String name);
        List<Category> getAllCategories();
}
