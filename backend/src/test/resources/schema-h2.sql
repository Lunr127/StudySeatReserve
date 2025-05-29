-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `open_id` varchar(100) DEFAULT NULL COMMENT '微信OpenID',
  `user_type` int DEFAULT '2' COMMENT '用户类型（1-管理员，2-学生）',
  `status` int DEFAULT '1' COMMENT '状态（0-禁用，1-正常）',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_open_id` (`open_id`)
);

-- 创建管理员表
CREATE TABLE IF NOT EXISTS `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `admin_type` int DEFAULT '2' COMMENT '管理员类型（1-系统管理员，2-自习室管理员）',
  `department` varchar(50) DEFAULT NULL COMMENT '所属部门',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
);

-- 创建学生表
CREATE TABLE IF NOT EXISTS `student` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `student_id` varchar(50) NOT NULL COMMENT '学号',
  `college` varchar(50) DEFAULT NULL COMMENT '学院',
  `major` varchar(50) DEFAULT NULL COMMENT '专业',
  `grade` varchar(20) DEFAULT NULL COMMENT '年级',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
  `violation_count` int DEFAULT '0' COMMENT '违约次数',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_student_id` (`student_id`),
  KEY `idx_user_id` (`user_id`)
);

-- 创建自习室表
CREATE TABLE IF NOT EXISTS `study_room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '自习室名称',
  `location` varchar(100) DEFAULT NULL COMMENT '位置描述',
  `building` varchar(50) DEFAULT NULL COMMENT '所在建筑',
  `floor` varchar(10) DEFAULT NULL COMMENT '所在楼层',
  `room_number` varchar(20) DEFAULT NULL COMMENT '房间号',
  `capacity` int DEFAULT '0' COMMENT '座位容量',
  `description` varchar(500) DEFAULT NULL COMMENT '详细描述',
  `open_time` time DEFAULT NULL COMMENT '开放时间(开始)',
  `close_time` time DEFAULT NULL COMMENT '开放时间(结束)',
  `belongs_to` varchar(50) DEFAULT '全校' COMMENT '归属(全校/特定院系)',
  `is_active` int DEFAULT '1' COMMENT '是否开放(0-关闭，1-开放)',
  `admin_id` bigint DEFAULT NULL COMMENT '管理员ID',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除(0-否，1-是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_building` (`building`),
  KEY `idx_belongs_to` (`belongs_to`)
);

-- 创建座位表
CREATE TABLE IF NOT EXISTS `seat` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `study_room_id` bigint NOT NULL COMMENT '所属自习室ID',
  `seat_number` varchar(20) NOT NULL COMMENT '座位编号',
  `row_number` int DEFAULT NULL COMMENT '行号',
  `column_number` int DEFAULT NULL COMMENT '列号',
  `has_power` int DEFAULT '0' COMMENT '是否有电源(0-无，1-有)',
  `is_window` int DEFAULT '0' COMMENT '是否靠窗(0-否，1-是)',
  `is_corner` int DEFAULT '0' COMMENT '是否角落(0-否，1-是)',
  `status` int DEFAULT '1' COMMENT '状态(0-停用，1-正常)',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除(0-否，1-是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_study_room_id` (`study_room_id`),
  KEY `idx_seat_number` (`seat_number`)
);

-- 创建预约表
CREATE TABLE IF NOT EXISTS `reservation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `seat_id` bigint NOT NULL COMMENT '座位ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` int DEFAULT '1' COMMENT '状态(0-已取消，1-待签到，2-使用中，3-已完成，4-已违约)',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除(0-否，1-是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_seat_id` (`seat_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`)
); 