<<<<<<< HEAD
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campus_share.entity.SensitiveWord;
import com.example.campus_share.entity.SystemConfig;
import com.example.campus_share.mapper.SensitiveWordMapper;
import com.example.campus_share.mapper.SystemConfigMapper;
import com.example.campus_share.service.AdminConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminConfigServiceImpl implements AdminConfigService {

    private final SystemConfigMapper configMapper;
    private final SensitiveWordMapper sensitiveWordMapper;

    public AdminConfigServiceImpl(SystemConfigMapper configMapper, SensitiveWordMapper sensitiveWordMapper) {
        this.configMapper = configMapper;
        this.sensitiveWordMapper = sensitiveWordMapper;
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        return configMapper.selectList(null);
    }

    @Override
    public SystemConfig getConfigByKey(String key) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key);
        return configMapper.selectOne(wrapper);
    }

    @Override
    public boolean updateConfig(String key, String value) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key);
        
        SystemConfig config = configMapper.selectOne(wrapper);
        if (config == null) {
            return false;
        }
        
        config.setConfigValue(value);
        config.setUpdateTime(LocalDateTime.now());
        
        return configMapper.updateById(config) > 0;
    }

    @Override
    @Transactional
    public boolean updateSensitiveWords(List<String> words) {
        // 清空现有敏感词
        sensitiveWordMapper.delete(null);
        
        // 批量插入新敏感词
        if (words != null && !words.isEmpty()) {
            List<SensitiveWord> sensitiveWords = new ArrayList<>();
            for (String word : words) {
                SensitiveWord sensitiveWord = new SensitiveWord();
                sensitiveWord.setWord(word);
                sensitiveWord.setCreateTime(LocalDateTime.now());
                sensitiveWords.add(sensitiveWord);
            }
            
            for (SensitiveWord word : sensitiveWords) {
                sensitiveWordMapper.insert(word);
            }
        }
        
        return true;
    }
=======
package com.example.campus_share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campus_share.entity.SensitiveWord;
import com.example.campus_share.entity.SystemConfig;
import com.example.campus_share.mapper.SensitiveWordMapper;
import com.example.campus_share.mapper.SystemConfigMapper;
import com.example.campus_share.service.AdminConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminConfigServiceImpl implements AdminConfigService {

    private final SystemConfigMapper configMapper;
    private final SensitiveWordMapper sensitiveWordMapper;

    public AdminConfigServiceImpl(SystemConfigMapper configMapper, SensitiveWordMapper sensitiveWordMapper) {
        this.configMapper = configMapper;
        this.sensitiveWordMapper = sensitiveWordMapper;
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        return configMapper.selectList(null);
    }

    @Override
    public SystemConfig getConfigByKey(String key) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key);
        return configMapper.selectOne(wrapper);
    }

    @Override
    public boolean updateConfig(String key, String value) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key);
        
        SystemConfig config = configMapper.selectOne(wrapper);
        if (config == null) {
            return false;
        }
        
        config.setConfigValue(value);
        config.setUpdateTime(LocalDateTime.now());
        
        return configMapper.updateById(config) > 0;
    }

    @Override
    @Transactional
    public boolean updateSensitiveWords(List<String> words) {
        // 清空现有敏感词
        sensitiveWordMapper.delete(null);
        
        // 批量插入新敏感词
        if (words != null && !words.isEmpty()) {
            List<SensitiveWord> sensitiveWords = new ArrayList<>();
            for (String word : words) {
                SensitiveWord sensitiveWord = new SensitiveWord();
                sensitiveWord.setWord(word);
                sensitiveWord.setCreateTime(LocalDateTime.now());
                sensitiveWords.add(sensitiveWord);
            }
            
            for (SensitiveWord word : sensitiveWords) {
                sensitiveWordMapper.insert(word);
            }
        }
        
        return true;
    }
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
} 