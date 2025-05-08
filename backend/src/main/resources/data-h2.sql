-- 插入管理员用户（密码为123456的BCrypt加密版本）
INSERT INTO `user` (`id`, `username`, `password`, `email`, `phone`, `status`, `role`, `create_time`, `update_time`, `del_flag`) 
VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'admin@example.com', '13800000000', 1, 'admin', NOW(), NULL, 0);

-- 插入学生用户
INSERT INTO `user` (`id`, `username`, `password`, `email`, `phone`, `status`, `role`, `create_time`, `update_time`, `del_flag`) 
VALUES (2, 'student', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'student@example.com', '13900000000', 1, 'student', NOW(), NULL, 0);

-- 插入自习室
INSERT INTO `study_room` (`id`, `name`, `location`, `capacity`, `open_time`, `close_time`, `status`, `description`, `department`, `create_time`, `update_time`, `del_flag`) 
VALUES (1, '图书馆自习室A', '图书馆一楼', 50, '08:00:00', '22:00:00', 1, '图书馆一楼安静自习区', NULL, NOW(), NULL, 0);

INSERT INTO `study_room` (`id`, `name`, `location`, `capacity`, `open_time`, `close_time`, `status`, `description`, `department`, `create_time`, `update_time`, `del_flag`) 
VALUES (2, '工学院自习室', '工学院大楼3楼', 30, '09:00:00', '21:00:00', 1, '工学院专用自习室', '工学院', NOW(), NULL, 0);

-- 插入座位
INSERT INTO `seat` (`id`, `room_id`, `seat_number`, `status`, `has_power`, `position`, `create_time`, `update_time`, `del_flag`) 
VALUES (1, 1, 'A-01', 1, 1, '靠窗', NOW(), NULL, 0);

INSERT INTO `seat` (`id`, `room_id`, `seat_number`, `status`, `has_power`, `position`, `create_time`, `update_time`, `del_flag`) 
VALUES (2, 1, 'A-02', 1, 1, '靠窗', NOW(), NULL, 0);

INSERT INTO `seat` (`id`, `room_id`, `seat_number`, `status`, `has_power`, `position`, `create_time`, `update_time`, `del_flag`) 
VALUES (3, 1, 'A-03', 1, 0, '中间', NOW(), NULL, 0);

INSERT INTO `seat` (`id`, `room_id`, `seat_number`, `status`, `has_power`, `position`, `create_time`, `update_time`, `del_flag`) 
VALUES (4, 2, 'B-01', 1, 1, '靠门', NOW(), NULL, 0);

-- 插入系统参数
INSERT INTO `system_param` (`id`, `param_key`, `param_value`, `remark`, `create_time`, `update_time`) 
VALUES (1, 'checkinTimeout', '15', '签到超时时间(分钟)', NOW(), NULL);

INSERT INTO `system_param` (`id`, `param_key`, `param_value`, `remark`, `create_time`, `update_time`) 
VALUES (2, 'maxReserveHours', '4', '最大预约时长(小时)', NOW(), NULL); 