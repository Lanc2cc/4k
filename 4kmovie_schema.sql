-- ============================================================
-- 4K电影网站 MySQL 数据库建表脚本
-- 数据库: MySQL 5.7  字符集: utf8mb4
-- 数据来源: TMDB API + 本站自管理数据
-- ============================================================

CREATE DATABASE IF NOT EXISTS `4kmovie` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `4kmovie`;

-- ============================================================
-- 一、电影模块
-- ============================================================

-- 1. 电影主表（来自 TMDB /movie/{id}）
CREATE TABLE `movie` (
    `id` INT NOT NULL COMMENT 'TMDB电影ID，作为主键',
    `title` VARCHAR(255) NOT NULL COMMENT '中文标题',
    `original_title` VARCHAR(255) DEFAULT NULL COMMENT '原始标题',
    `overview` TEXT DEFAULT NULL COMMENT '剧情简介',
    `poster_path` VARCHAR(255) DEFAULT NULL COMMENT '海报图片路径',
    `backdrop_path` VARCHAR(255) DEFAULT NULL COMMENT '背景大图路径',
    `release_date` DATE DEFAULT NULL COMMENT '上映日期',
    `runtime` INT DEFAULT NULL COMMENT '时长（分钟）',
    `vote_average` DECIMAL(4,2) DEFAULT 0 COMMENT '评分（0-10）',
    `vote_count` INT DEFAULT 0 COMMENT '评价数量',
    `popularity` DECIMAL(10,4) DEFAULT 0 COMMENT '热度值',
    `original_language` VARCHAR(10) DEFAULT NULL COMMENT '原始语言代码(en/zh/ja/ko等)',
    `status` VARCHAR(50) DEFAULT NULL COMMENT '状态(Released等)',
    `tagline` VARCHAR(500) DEFAULT NULL COMMENT '宣传语',
    `adult` TINYINT(1) DEFAULT 0 COMMENT '是否成人内容 0否1是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_release_date` (`release_date`),
    INDEX `idx_vote_average` (`vote_average`),
    INDEX `idx_popularity` (`popularity`),
    INDEX `idx_original_language` (`original_language`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影主表';

-- 2. 电影类型字典表（来自 TMDB genres）
CREATE TABLE `genre` (
    `id` INT NOT NULL COMMENT 'TMDB类型ID',
    `name` VARCHAR(50) NOT NULL COMMENT '类型名称(动作/科幻/剧情等)',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影类型字典表';

-- 3. 电影-类型关联表（多对多）
CREATE TABLE `movie_genre` (
    `movie_id` INT NOT NULL COMMENT '电影ID',
    `genre_id` INT NOT NULL COMMENT '类型ID',
    PRIMARY KEY (`movie_id`, `genre_id`),
    INDEX `idx_genre_id` (`genre_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影类型关联表';

-- 4. 人员表（演员/导演，来自 TMDB credits）
CREATE TABLE `person` (
    `id` INT NOT NULL COMMENT 'TMDB人员ID',
    `name` VARCHAR(255) NOT NULL COMMENT '中文名',
    `original_name` VARCHAR(255) DEFAULT NULL COMMENT '原名',
    `profile_path` VARCHAR(255) DEFAULT NULL COMMENT '头像路径',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0未知 1女 2男',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='演员/导演人员表';

-- 5. 电影-演员关联表
CREATE TABLE `movie_cast` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `movie_id` INT NOT NULL COMMENT '电影ID',
    `person_id` INT NOT NULL COMMENT '人员ID',
    `character_name` VARCHAR(255) DEFAULT NULL COMMENT '饰演角色名',
    `cast_order` INT DEFAULT 0 COMMENT '演员排序(主演靠前)',
    PRIMARY KEY (`id`),
    INDEX `idx_movie_id` (`movie_id`),
    INDEX `idx_person_id` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影演员关联表';

-- 6. 电影-幕后人员关联表（导演等）
CREATE TABLE `movie_crew` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `movie_id` INT NOT NULL COMMENT '电影ID',
    `person_id` INT NOT NULL COMMENT '人员ID',
    `department` VARCHAR(100) DEFAULT NULL COMMENT '部门(Directing/Writing等)',
    `job` VARCHAR(100) DEFAULT NULL COMMENT '职位(Director/Screenplay等)',
    PRIMARY KEY (`id`),
    INDEX `idx_movie_id` (`movie_id`),
    INDEX `idx_person_id` (`person_id`),
    INDEX `idx_job` (`job`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影幕后人员关联表';

-- 7. 播放源/线路表（本站管理，非TMDB数据）
CREATE TABLE `movie_source` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `movie_id` INT NOT NULL COMMENT '电影ID',
    `source_name` VARCHAR(100) NOT NULL COMMENT '线路名称(线路1-4K高清)',
    `source_url` VARCHAR(500) NOT NULL COMMENT '播放地址',
    `quality` VARCHAR(50) DEFAULT '4K' COMMENT '清晰度(4K高清/4K超清)',
    `stability` VARCHAR(50) DEFAULT '流畅' COMMENT '稳定性(流畅/高速)',
    `is_recommended` TINYINT(1) DEFAULT 0 COMMENT '是否推荐线路 0否1是',
    `sort_order` INT DEFAULT 0 COMMENT '排序权重',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态 0禁用1启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_movie_id` (`movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='播放源线路表';

-- ============================================================
-- 二、用户模块
-- ============================================================

-- 8. 用户表
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密存储)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态 0禁用1正常',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 9. 观看历史表
CREATE TABLE `user_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `movie_id` INT NOT NULL COMMENT '电影ID',
    `watch_progress` INT DEFAULT 0 COMMENT '观看进度(秒)',
    `source_id` BIGINT DEFAULT NULL COMMENT '上次观看的播放源ID',
    `watched_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最近观看时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_movie` (`user_id`, `movie_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_watched_at` (`watched_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='观看历史表';

-- 10. 收藏表
CREATE TABLE `user_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `movie_id` INT NOT NULL COMMENT '电影ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_movie` (`user_id`, `movie_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ============================================================
-- 三、运营模块
-- ============================================================

-- 11. 广告表
CREATE TABLE `advertisement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `title` VARCHAR(100) NOT NULL COMMENT '广告标题',
    `ad_type` VARCHAR(50) NOT NULL COMMENT '广告类型(carousel轮播/banner横幅/native原生/pre_play播前)',
    `image_url` VARCHAR(500) NOT NULL COMMENT '广告图片URL',
    `link_url` VARCHAR(500) DEFAULT NULL COMMENT '点击跳转URL',
    `position` VARCHAR(50) DEFAULT NULL COMMENT '投放位置(home首页/library影库/detail详情)',
    `duration` INT DEFAULT 3 COMMENT '展示时长(秒)',
    `sort_order` INT DEFAULT 0 COMMENT '排序权重',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态 0禁用1启用',
    `start_time` DATETIME DEFAULT NULL COMMENT '投放开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '投放结束时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_ad_type` (`ad_type`),
    INDEX `idx_position` (`position`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告表';

-- 12. 热搜词表
CREATE TABLE `hot_search` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `keyword` VARCHAR(100) NOT NULL COMMENT '热搜关键词',
    `sort_order` INT DEFAULT 0 COMMENT '排序权重(越大越靠前)',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态 0禁用1启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热搜词表';

-- 13. 运营活动表
CREATE TABLE `activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `title` VARCHAR(100) NOT NULL COMMENT '活动标题(新片上线/热门合集等)',
    `description` TEXT DEFAULT NULL COMMENT '活动描述',
    `banner_url` VARCHAR(500) DEFAULT NULL COMMENT '活动横幅图片URL',
    `link_url` VARCHAR(500) DEFAULT NULL COMMENT '活动详情链接',
    `sort_order` INT DEFAULT 0 COMMENT '排序权重',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态 0禁用1启用',
    `start_time` DATETIME DEFAULT NULL COMMENT '活动开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '活动结束时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营活动表';

-- 14. 弹幕表
CREATE TABLE `danmu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `movie_id` INT NOT NULL COMMENT '电影ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` VARCHAR(200) NOT NULL COMMENT '弹幕内容',
    `time_point` INT NOT NULL COMMENT '弹幕出现的视频时间点(秒)',
    `color` VARCHAR(10) DEFAULT '#FFFFFF' COMMENT '弹幕颜色',
    `font_size` TINYINT DEFAULT 1 COMMENT '字体大小 0小 1中 2大',
    `status` TINYINT(1) DEFAULT 1 COMMENT '审核状态 0待审核1通过2拒绝',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    INDEX `idx_movie_id` (`movie_id`),
    INDEX `idx_time_point` (`movie_id`, `time_point`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='弹幕表';
