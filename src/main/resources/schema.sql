-- 用户表（保持不变）
CREATE TABLE `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `bio` varchar(255) DEFAULT NULL COMMENT '个人简介',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-未知 1-男 2-女',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-正常 1-禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_admin` tinyint DEFAULT '0' COMMENT '是否管理员：0-否 1-是',
  `follow_count` int DEFAULT '0' COMMENT '关注数',
  `follower_count` int DEFAULT '0' COMMENT '粉丝数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 帖子表（保持不变）
CREATE TABLE `t_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `images` text NOT NULL COMMENT '组图URL',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-正常 1-审核中 2-已删除',
  `view_count` int DEFAULT '0' COMMENT '浏览数',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `favorite_count` int DEFAULT '0' COMMENT '收藏数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 修改评论表，添加parent_id字段支持回复功能
CREATE TABLE `t_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论ID，用于回复功能',
  `content` varchar(500) NOT NULL COMMENT '内容',
  `avatar` varchar(500) NOT NULL COMMENT '评论者头像',
  `nickname` varchar(500) NOT NULL COMMENT '评论者昵称',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-正常 1-隐藏 2-删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `likeCount` bigint DEFAULT '0' COMMENT '点赞数',
  `replyCount` bigint DEFAULT '0' COMMENT '回复数',

  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 帖子点赞表（修改为专门针对帖子的点赞表）
CREATE TABLE `t_post_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_post` (`user_id`,`post_id`) COMMENT '确保用户只能点赞一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

-- 帖子收藏表（修改为与实体类一致的表结构）
CREATE TABLE `t_post_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_post` (`user_id`,`post_id`) COMMENT '确保用户只能收藏一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子收藏表';

-- 分类表（保持不变）
CREATE TABLE `t_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-正常 1-禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 标签表（保持不变）
CREATE TABLE `t_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 帖子标签关联表（保持不变）
CREATE TABLE `t_post_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_post_tag` (`post_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子标签关联表';

-- 用户关系表（保持不变）
CREATE TABLE `t_user_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '关注者ID',
  `follow_user_id` bigint NOT NULL COMMENT '被关注者ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_follow` (`user_id`,`follow_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关系表';

-- 添加角色表
CREATE TABLE `t_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 添加用户角色关联表
CREATE TABLE `t_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_role` (`user_id`,`role_id`) COMMENT '确保一个用户一个角色只有一条记录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 添加系统配置表
CREATE TABLE `t_system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `description` varchar(255) DEFAULT NULL COMMENT '说明',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 添加敏感词表
CREATE TABLE `t_sensitive_word` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `word` varchar(100) NOT NULL COMMENT '敏感词',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_word` (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

-- 添加用户访问日志表
CREATE TABLE `t_user_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `action` varchar(100) NOT NULL COMMENT '操作类型',
  `object_id` bigint DEFAULT NULL COMMENT '操作对象ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户操作日志表';

-- 插入初始角色数据
INSERT INTO `t_role` (`name`, `description`) VALUES ('ROLE_ADMIN', '管理员');
INSERT INTO `t_role` (`name`, `description`) VALUES ('ROLE_USER', '普通用户');

-- 插入初始系统配置
INSERT INTO `t_system_config` (`config_key`, `config_value`, `description`, `update_time`) 
VALUES ('site_name', '校园分享平台', '站点名称', NOW());
INSERT INTO `t_system_config` (`config_key`, `config_value`, `description`, `update_time`) 
VALUES ('site_description', '校园生活分享社区', '站点描述', NOW());
INSERT INTO `t_system_config` (`config_key`, `config_value`, `description`, `update_time`) 
VALUES ('post_review', 'false', '帖子是否需要审核', NOW());