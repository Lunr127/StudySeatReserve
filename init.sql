-- 使用数据库
USE study_seat_reserve;

-- 用户表：存储所有用户的基本信息
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `open_id` VARCHAR(50) COMMENT '微信OpenID',
    `user_type` TINYINT NOT NULL COMMENT '用户类型：1-管理员，2-学生',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_open_id` (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 管理员表：存储管理员特有信息
CREATE TABLE IF NOT EXISTS `admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
    `admin_type` TINYINT NOT NULL DEFAULT 1 COMMENT '管理员类型：1-系统管理员，2-自习室管理员',
    `department` VARCHAR(50) COMMENT '所属部门',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_admin_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 学生表：存储学生特有信息
CREATE TABLE IF NOT EXISTS `student` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学生ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
    `student_id` VARCHAR(20) NOT NULL COMMENT '学号',
    `college` VARCHAR(50) COMMENT '学院',
    `major` VARCHAR(50) COMMENT '专业',
    `grade` VARCHAR(20) COMMENT '年级',
    `class_name` VARCHAR(50) COMMENT '班级',
    `violation_count` INT NOT NULL DEFAULT 0 COMMENT '违约次数',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 自习室表：存储自习室信息
CREATE TABLE IF NOT EXISTS `study_room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自习室ID',
    `name` VARCHAR(100) NOT NULL COMMENT '自习室名称',
    `location` VARCHAR(255) NOT NULL COMMENT '位置描述',
    `building` VARCHAR(100) COMMENT '所在建筑',
    `floor` VARCHAR(20) COMMENT '所在楼层',
    `room_number` VARCHAR(20) COMMENT '房间号',
    `capacity` INT NOT NULL COMMENT '座位容量',
    `description` TEXT COMMENT '详细描述',
    `open_time` TIME NOT NULL COMMENT '开放时间（开始）',
    `close_time` TIME NOT NULL COMMENT '开放时间（结束）',
    `belongs_to` VARCHAR(50) COMMENT '归属（全校/特定院系）',
    `is_active` TINYINT NOT NULL DEFAULT 1 COMMENT '是否开放：0-关闭，1-开放',
    `admin_id` BIGINT COMMENT '管理员ID',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`),
    CONSTRAINT `fk_study_room_admin` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室表';

-- 座位表：存储座位信息
CREATE TABLE IF NOT EXISTS `seat` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '座位ID',
    `study_room_id` BIGINT NOT NULL COMMENT '所属自习室ID',
    `seat_number` VARCHAR(20) NOT NULL COMMENT '座位编号',
    `row_number` INT COMMENT '行号',
    `column_number` INT COMMENT '列号',
    `has_power` TINYINT NOT NULL DEFAULT 0 COMMENT '是否有电源：0-无，1-有',
    `is_window` TINYINT NOT NULL DEFAULT 0 COMMENT '是否靠窗：0-否，1-是',
    `is_corner` TINYINT NOT NULL DEFAULT 0 COMMENT '是否角落：0-否，1-是',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-停用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_seat` (`study_room_id`, `seat_number`),
    CONSTRAINT `fk_seat_study_room` FOREIGN KEY (`study_room_id`) REFERENCES `study_room` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位表';

-- 预约表：存储预约信息
CREATE TABLE IF NOT EXISTS `reservation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `seat_id` BIGINT NOT NULL COMMENT '座位ID',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已取消，1-待签到，2-使用中，3-已完成，4-已违约',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_seat_id` (`seat_id`),
    KEY `idx_time` (`start_time`, `end_time`),
    CONSTRAINT `fk_reservation_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_reservation_seat` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 签到表：存储签到信息
CREATE TABLE IF NOT EXISTS `check_in` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '签到ID',
    `reservation_id` BIGINT NOT NULL COMMENT '预约ID',
    `check_in_time` DATETIME NOT NULL COMMENT '签到时间',
    `check_in_type` TINYINT NOT NULL COMMENT '签到类型：1-扫码签到，2-手动输入编码',
    `check_out_time` DATETIME COMMENT '签退时间（可为空，表示未签退）',
    `check_code` VARCHAR(50) COMMENT '签到码',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_reservation_id` (`reservation_id`),
    CONSTRAINT `fk_check_in_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到表';

-- 违约记录表：存储违约信息
CREATE TABLE IF NOT EXISTS `violation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '违约ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `reservation_id` BIGINT NOT NULL COMMENT '预约ID',
    `violation_type` TINYINT NOT NULL COMMENT '违约类型：1-未签到，2-迟到，3-提前离开',
    `description` VARCHAR(255) COMMENT '违约描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_reservation_id` (`reservation_id`),
    CONSTRAINT `fk_violation_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_violation_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违约记录表';

-- 系统参数表：存储系统配置参数
CREATE TABLE IF NOT EXISTS `system_param` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '参数ID',
    `param_key` VARCHAR(50) NOT NULL COMMENT '参数键',
    `param_value` VARCHAR(255) NOT NULL COMMENT '参数值',
    `description` VARCHAR(255) COMMENT '参数描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_param_key` (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数表';

-- 通知消息表：存储通知消息
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `type` TINYINT NOT NULL COMMENT '通知类型：1-系统通知，2-预约提醒，3-迟到提醒，4-违约通知',
    `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息表';

-- 收藏表：存储收藏的自习室和座位
CREATE TABLE IF NOT EXISTS `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `favorite_type` TINYINT NOT NULL COMMENT '收藏类型：1-自习室，2-座位',
    `study_room_id` BIGINT COMMENT '自习室ID（当收藏类型为1时有值）',
    `seat_id` BIGINT COMMENT '座位ID（当收藏类型为2时有值）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_study_room_id` (`study_room_id`),
    KEY `idx_seat_id` (`seat_id`),
    CONSTRAINT `fk_favorite_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_favorite_study_room` FOREIGN KEY (`study_room_id`) REFERENCES `study_room` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_favorite_seat` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 签到码表：存储每天的签到码
CREATE TABLE IF NOT EXISTS `check_code` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `study_room_id` BIGINT NOT NULL COMMENT '自习室ID',
    `code` VARCHAR(20) NOT NULL COMMENT '签到码',
    `valid_date` DATE NOT NULL COMMENT '有效日期',
    `is_active` TINYINT NOT NULL DEFAULT 1 COMMENT '是否有效：0-无效，1-有效',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_date` (`study_room_id`, `valid_date`),
    CONSTRAINT `fk_check_code_study_room` FOREIGN KEY (`study_room_id`) REFERENCES `study_room` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到码表';

-- 插入初始数据 --

-- 插入管理员用户
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`) VALUES
('admin', '$2a$10$1/WZqYiRyM5ORfeZk.Hqae2Fh1g9EJP1y0Sv7F5Y9cO0Z5.j7T8aC', '系统管理员', '13800138000', 'admin@example.com', 1, 1),
('roomadmin1', '$2a$10$1/WZqYiRyM5ORfeZk.Hqae2Fh1g9EJP1y0Sv7F5Y9cO0Z5.j7T8aC', '自习室管理员1', '13800138001', 'roomadmin1@example.com', 1, 1),
('roomadmin2', '$2a$10$1/WZqYiRyM5ORfeZk.Hqae2Fh1g9EJP1y0Sv7F5Y9cO0Z5.j7T8aC', '自习室管理员2', '13800138002', 'roomadmin2@example.com', 1, 1);

-- 插入管理员信息
INSERT INTO `admin` (`user_id`, `admin_type`, `department`) VALUES
(1, 1, '信息中心'),
(2, 2, '图书馆'),
(3, 2, '学生处');

-- 插入示例学生用户
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`) VALUES
('student1', '$2a$10$1/WZqYiRyM5ORfeZk.Hqae2Fh1g9EJP1y0Sv7F5Y9cO0Z5.j7T8aC', '张三', '13900139000', 'student1@example.com', 2, 1),
('student2', '$2a$10$1/WZqYiRyM5ORfeZk.Hqae2Fh1g9EJP1y0Sv7F5Y9cO0Z5.j7T8aC', '李四', '13900139001', 'student2@example.com', 2, 1),
('student3', '$2a$10$1/WZqYiRyM5ORfeZk.Hqae2Fh1g9EJP1y0Sv7F5Y9cO0Z5.j7T8aC', '王五', '13900139002', 'student3@example.com', 2, 1);

-- 插入学生信息
INSERT INTO `student` (`user_id`, `student_id`, `college`, `major`, `grade`, `class_name`) VALUES
(4, '2023001', '计算机学院', '计算机科学与技术', '2023级', '计科1班'),
(5, '2023002', '经济管理学院', '工商管理', '2023级', '工管1班'),
(6, '2023003', '文学院', '汉语言文学', '2023级', '汉文1班');

-- 插入示例自习室
INSERT INTO `study_room` (`name`, `location`, `building`, `floor`, `room_number`, `capacity`, `description`, `open_time`, `close_time`, `belongs_to`, `is_active`, `admin_id`) VALUES
('图书馆主自习室', '图书馆一楼西侧', '图书馆', '1', '101', 100, '图书馆主自习室，环境宽敞明亮，适合长时间学习', '08:00:00', '22:00:00', '全校', 1, 2),
('图书馆安静自习室', '图书馆二楼北侧', '图书馆', '2', '201', 50, '图书馆安静自习室，禁止交谈，适合需要高度专注的学习', '08:00:00', '22:00:00', '全校', 1, 2),
('工学院自习室', '工学院教学楼三楼', '工学院教学楼', '3', '301', 80, '工学院专用自习室，环境舒适', '08:30:00', '21:30:00', '工学院', 1, 3),
('计算机学院机房', '计算机学院一楼', '计算机学院大楼', '1', '108', 40, '计算机学院机房，配有高性能电脑', '09:00:00', '21:00:00', '计算机学院', 1, 3);

-- 为自习室1添加座位
INSERT INTO `seat` (`study_room_id`, `seat_number`, `row_number`, `column_number`, `has_power`, `is_window`, `is_corner`, `status`) VALUES
(1, 'A1', 1, 1, 1, 0, 1, 1),
(1, 'A2', 1, 2, 1, 0, 0, 1),
(1, 'A3', 1, 3, 1, 0, 0, 1),
(1, 'A4', 1, 4, 1, 0, 0, 1),
(1, 'A5', 1, 5, 1, 1, 0, 1),
(1, 'B1', 2, 1, 1, 0, 0, 1),
(1, 'B2', 2, 2, 0, 0, 0, 1),
(1, 'B3', 2, 3, 0, 0, 0, 1),
(1, 'B4', 2, 4, 0, 0, 0, 1),
(1, 'B5', 2, 5, 1, 1, 0, 1);

-- 为自习室2添加座位
INSERT INTO `seat` (`study_room_id`, `seat_number`, `row_number`, `column_number`, `has_power`, `is_window`, `is_corner`, `status`) VALUES
(2, 'A1', 1, 1, 1, 0, 1, 1),
(2, 'A2', 1, 2, 1, 0, 0, 1),
(2, 'A3', 1, 3, 1, 1, 0, 1),
(2, 'B1', 2, 1, 0, 0, 0, 1),
(2, 'B2', 2, 2, 0, 0, 0, 1),
(2, 'B3', 2, 3, 1, 1, 0, 1);

-- 插入系统参数
INSERT INTO `system_param` (`param_key`, `param_value`, `description`) VALUES
('MAX_RESERVATION_HOURS', '4', '最大预约时长（小时）'),
('MAX_ACTIVE_RESERVATIONS', '2', '同时活跃预约数量上限'),
('CHECKIN_TIMEOUT_MINUTES', '15', '签到超时时间（分钟）'),
('VIOLATION_PENALTY_DAYS', '3', '违约惩罚天数（天）'),
('MAX_VIOLATION_COUNT', '5', '最大违约次数（达到后禁止预约）'),
('RESERVATION_ADVANCE_DAYS', '7', '可提前预约的天数'); 