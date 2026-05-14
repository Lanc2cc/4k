-- 创建数据库
CREATE DATABASE IF NOT EXISTS movie_4k DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE movie_4k;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(200) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0普通用户 1管理员',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 电影分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影分类表';

-- 电影表
CREATE TABLE IF NOT EXISTS `movie` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `movie_name` VARCHAR(200) NOT NULL COMMENT '电影名称',
  `original_title` VARCHAR(200) DEFAULT NULL COMMENT '原始标题',
  `overview` TEXT COMMENT '简介',
  `poster_path` VARCHAR(500) DEFAULT NULL COMMENT '海报地址',
  `backdrop_path` VARCHAR(500) DEFAULT NULL COMMENT '背景图',
  `release_year` VARCHAR(10) DEFAULT NULL COMMENT '上映年份',
  `release_date` VARCHAR(20) DEFAULT NULL COMMENT '上映日期',
  `vote_average` DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分',
  `vote_count` INT DEFAULT 0 COMMENT '评分人数',
  `popularity` DECIMAL(10,2) DEFAULT 0.00 COMMENT '人气',
  `tmdb_id` INT DEFAULT NULL COMMENT 'TMDB ID',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0下架 1上架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_tmdb_id` (`tmdb_id`),
  KEY `idx_movie_name_year` (`movie_name`, `release_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影表';

-- 电影播放源表
CREATE TABLE IF NOT EXISTS `movie_source` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `movie_id` BIGINT NOT NULL COMMENT '电影ID',
  `source_name` VARCHAR(100) NOT NULL COMMENT '播放源名称',
  `source_url` VARCHAR(500) NOT NULL COMMENT '播放地址',
  `quality` VARCHAR(20) DEFAULT '1080P' COMMENT '画质',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_movie_id` (`movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影播放源表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `movie_id` BIGINT NOT NULL COMMENT '电影ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_movie` (`user_id`, `movie_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `movie_id` BIGINT NOT NULL COMMENT '电影ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0隐藏 1显示',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_movie_id` (`movie_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 观看历史表
CREATE TABLE IF NOT EXISTS `watch_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `movie_id` BIGINT NOT NULL COMMENT '电影ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_movie` (`user_id`, `movie_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='观看历史表';

-- API同步日志表
CREATE TABLE IF NOT EXISTS `api_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sync_type` VARCHAR(50) NOT NULL COMMENT '同步类型',
  `sync_count` INT DEFAULT 0 COMMENT '同步数量',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0失败 1成功',
  `message` TEXT COMMENT '日志信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API同步日志表';

-- 初始化分类数据
INSERT INTO `category` (`name`) VALUES
('动作'), ('冒险'), ('动画'), ('喜剧'), ('犯罪'),
('纪录片'), ('剧情'), ('家庭'), ('奇幻'), ('历史'),
('恐怖'), ('音乐'), ('悬疑'), ('爱情'), ('科幻'),
('电视电影'), ('惊悚'), ('战争'), ('西部');

-- 初始化管理员账号 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `role`) VALUES
('admin', 'DEFAULT_ADMIN_PWD', '管理员', 1);
