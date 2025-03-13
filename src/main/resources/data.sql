-- 管理员用户 (密码为加密后的 'admin123')
INSERT INTO t_user (username, password, nickname, avatar, bio, email, status, create_time, update_time, is_admin)
VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '系统管理员', 
        'https://example.com/avatar/admin.jpg', '系统管理员', 'admin@campus.com', 0, 
        NOW(), NOW(), 1)
ON DUPLICATE KEY UPDATE update_time = NOW();

-- 基本分类
INSERT INTO t_category (name, description, status, create_time, update_time)
VALUES ('学习资料', '学习资料分享', 0, NOW(), NOW()),
       ('校园活动', '校园活动信息', 0, NOW(), NOW()),
       ('二手交易', '二手物品交易', 0, NOW(), NOW()),
       ('求职招聘', '求职招聘信息', 0, NOW(), NOW()),
       ('日常生活', '校园日常', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE update_time = NOW();

-- 基本标签
INSERT INTO t_tag (name, create_time)
VALUES ('考试', NOW()),
       ('作业', NOW()),
       ('讲座', NOW()),
       ('社团', NOW()),
       ('竞赛', NOW()),
       ('实习', NOW()),
       ('课程', NOW()),
       ('校园', NOW()),
       ('生活', NOW()),
       ('电子设备', NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name);