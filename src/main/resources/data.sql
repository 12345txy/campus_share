-- 管理员用户 (密码为加密后的 'admin123')
INSERT INTO t_user (username, password, nickname, avatar, bio, email, status, create_time, update_time, is_admin)
VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '系统管理员', 
        'https://example.com/avatar/admin.jpg', '系统管理员', 'admin@campus.com', 0, 
        NOW(), NOW(), 1)
ON DUPLICATE KEY UPDATE update_time = NOW();

-- 测试用户 (密码为加密后的 'password')
INSERT INTO t_user (username, password, nickname, avatar, bio, email, status, create_time, update_time, is_admin)
VALUES ('user1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '测试用户', 
        'https://example.com/avatar/user1.jpg', '普通用户账号', 'user1@campus.com', 0, 
        NOW(), NOW(), 0)
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

-- 示例帖子
INSERT INTO t_post (id,user_id, title, content, category_id, status, view_count, like_count, comment_count, create_time, update_time)
VALUES (1, 1, '校园分享平台使用指南', '欢迎使用校园分享平台，这里可以分享学习资料、校园活动等信息...', 1, 0, 100, 50, 10, NOW(), NOW()),
       (2, 1, '计算机科学导论课程资料分享', '分享一些计算机科学导论的学习资料，希望对大家有帮助...', 1, 0, 80, 30, 5, NOW(), NOW())
ON DUPLICATE KEY UPDATE update_time = NOW();

-- 示例帖子标签关联
INSERT INTO t_post_tag (post_id, tag_id)
VALUES (1, 7), (1, 8), (2, 1), (2, 7)
ON DUPLICATE KEY UPDATE post_id = VALUES(post_id);

-- 示例评论
INSERT INTO t_comment (id, post_id, user_id, content, like_count, status, create_time, update_time)
VALUES (1, 1, 2, '感谢分享，非常有用！', 5, 0, NOW(), NOW()),
       (2, 2, 2, '这些资料对我学习很有帮助', 3, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE update_time = NOW();