package com.example.campus_share.service;

import com.example.campus_share.entity.SystemConfig;

import java.util.List;

public interface AdminConfigService {
    // 获取所有配置
    List<SystemConfig> getAllConfigs();
    
    // 根据键获取配置
    SystemConfig getConfigByKey(String key);
    
    // 更新配置
    boolean updateConfig(String key, String value);
    
    // 更新敏感词列表
    boolean updateSensitiveWords(List<String> words);
} 