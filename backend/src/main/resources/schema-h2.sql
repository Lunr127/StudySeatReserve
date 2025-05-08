-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态: 0=禁用, 1=启用',
    `role` varchar(10) NOT NULL COMMENT '角色: admin=管理员, student=学生',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 自习室表
CREATE TABLE IF NOT EXISTS `study_room` (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `name` varchar(50) NOT NULL COMMENT '自习室名称',
    `location` varchar(100) NOT NULL COMMENT '位置',
    `capacity` int(11) NOT NULL COMMENT '容量(座位数)',
    `open_time` time NOT NULL COMMENT '开放时间',
    `close_time` time NOT NULL COMMENT '关闭时间',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态: 0=关闭, 1=开放',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `department` varchar(50) DEFAULT NULL COMMENT '所属院系(为空表示全校通用)',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室表';

-- 座位表
CREATE TABLE IF NOT EXISTS `seat` (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `room_id` bigint(20) NOT NULL COMMENT '自习室ID',
    `seat_number` varchar(20) NOT NULL COMMENT '座位编号',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态: 0=不可用, 1=可用',
    `has_power` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有电源: 0=无, 1=有',
    `position` varchar(20) DEFAULT NULL COMMENT '位置描述(如:靠窗)',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_room_seat` (`room_id`,`seat_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位表';

-- 预约表
CREATE TABLE IF NOT EXISTS `reservation` (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `seat_id` bigint(20) NOT NULL COMMENT '座位ID',
    `room_id` bigint(20) NOT NULL COMMENT '自习室ID',
    `start_time` datetime NOT NULL COMMENT '开始时间',
    `end_time` datetime NOT NULL COMMENT '结束时间',
    `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态: 0=待使用, 1=使用中, 2=已完成, 3=已取消, 4=违约',
    `checkin_time` datetime DEFAULT NULL COMMENT '签到时间',
    `checkout_time` datetime DEFAULT NULL COMMENT '签退时间',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_reservation_user_id` (`user_id`),
    KEY `idx_reservation_seat_id` (`seat_id`),
    KEY `idx_reservation_room_id` (`room_id`),
    KEY `idx_reservation_status` (`status`),
    KEY `idx_reservation_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 签到表
CREATE TABLE IF NOT EXISTS `check_in` (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `reservation_id` bigint(20) NOT NULL COMMENT '预约ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `code` varchar(20) NOT NULL COMMENT '签到码',
    `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态: 0=未签到, 1=已签到',
    `check_time` datetime DEFAULT NULL COMMENT '签到时间',
    `expire_time` datetime NOT NULL COMMENT '过期时间',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_checkin_reservation_id` (`reservation_id`),
    KEY `idx_checkin_user_id` (`user_id`),
    KEY `idx_checkin_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到表';

-- 违约记录表
CREATE TABLE IF NOT EXISTS `violation` (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `reservation_id` bigint(20) NOT NULL COMMENT '预约ID',
    `reason` varchar(100) NOT NULL COMMENT '违约原因',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_violation_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违约记录表';

-- 系统参数表
CREATE TABLE IF NOT EXISTS `system_param` (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `param_key` varchar(50) NOT NULL COMMENT '参数键',
    `param_value` varchar(500) NOT NULL COMMENT '参数值',
    `remark` varchar(200) DEFAULT NULL COMMENT '备注',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_param_key` (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数表';

-- 通知消息表
CREATE TABLE IF NOT EXISTS `notification` (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `title` varchar(100) NOT NULL COMMENT '标题',
    `content` varchar(500) NOT NULL COMMENT '内容',
    `type` tinyint(1) NOT NULL COMMENT '类型: 1=系统通知, 2=预约提醒, 3=违约提醒',
    `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态: 0=未读, 1=已读',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
    PRIMARY KEY (`id`),
    KEY `idx_notification_user_id` (`user_id`),
    KEY `idx_notification_type` (`type`),
    KEY `idx_notification_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息表'; 