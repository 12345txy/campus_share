package com.example.campus_share.controller.admin;

import com.example.campus_share.entity.SystemConfig;
import com.example.campus_share.service.AdminConfigService;
import com.example.campus_share.util.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/config")
public class AdminConfigController {

    private final AdminConfigService configService;

    public AdminConfigController(AdminConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public Result<List<SystemConfig>> getAllConfigs() {
        List<SystemConfig> configs = configService.getAllConfigs();
        return Result.success(configs);
    }
    
    @GetMapping("/{key}")
    public Result<SystemConfig> getConfigByKey(@PathVariable String key) {
        SystemConfig config = configService.getConfigByKey(key);
        return Result.success(config);
    }
    
    @PutMapping("/{key}")
    public Result<Boolean> updateConfig(
            @PathVariable String key,
            @RequestBody SystemConfig config) {
        
        boolean success = configService.updateConfig(key, config.getConfigValue());
        return Result.success(success);
    }
    
    @PostMapping("/sensitive-words")
    public Result<Boolean> updateSensitiveWords(@RequestBody List<String> words) {
        boolean success = configService.updateSensitiveWords(words);
        return Result.success(success);
    }
} 