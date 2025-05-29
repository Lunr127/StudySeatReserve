-- 插入测试用户
INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `user_type`, `status`, `create_time`, `update_time`) VALUES 
(1, 'admin', '$2a$10$V/9SzQtw.7rD6ZT/GGY2Weh5DAzI9S6stE3TIc9D/i1unmkK26QnO', '管理员', 1, 1, NOW(), NOW()),
(2, 'student1', '$2a$10$V/9SzQtw.7rD6ZT/GGY2Weh5DAzI9S6stE3TIc9D/i1unmkK26QnO', '学生1', 2, 1, NOW(), NOW()),
(3, 'student2', '$2a$10$V/9SzQtw.7rD6ZT/GGY2Weh5DAzI9S6stE3TIc9D/i1unmkK26QnO', '学生2', 2, 1, NOW(), NOW());

-- 插入管理员信息
INSERT INTO `admin` (`id`, `user_id`, `admin_type`, `department`, `create_time`, `update_time`) VALUES 
(1, 1, 1, '计算机学院', NOW(), NOW());

-- 插入学生信息
INSERT INTO `student` (`id`, `user_id`, `student_id`, `college`, `major`, `grade`, `class_name`, `create_time`, `update_time`) VALUES 
(1, 2, '2022001', '计算机学院', '计算机科学与技术', '2022级', '1班', NOW(), NOW()),
(2, 3, '2022002', '数学学院', '应用数学', '2022级', '2班', NOW(), NOW());

-- 插入测试自习室
INSERT INTO `study_room` (`id`, `name`, `location`, `building`, `floor`, `room_number`, `capacity`, `description`, `open_time`, `close_time`, `belongs_to`, `is_active`, `admin_id`, `create_time`, `update_time`) VALUES 
(1, '计算机自习室', 'A栋2楼', 'A栋', '2', 'A201', 50, '计算机学院自习室', '08:00:00', '22:00:00', '计算机学院', 1, 1, NOW(), NOW()),
(2, '数学自习室', 'B栋3楼', 'B栋', '3', 'B301', 40, '数学学院自习室', '09:00:00', '21:00:00', '数学学院', 1, 1, NOW(), NOW()),
(3, '图书馆自习室', '图书馆1楼', '图书馆', '1', 'L101', 100, '全校开放自习室', '08:00:00', '22:00:00', '全校', 1, 1, NOW(), NOW());

-- 插入测试座位（自习室1）
INSERT INTO `seat` (`study_room_id`, `seat_number`, `row_number`, `column_number`, `has_power`, `is_window`, `is_corner`, `status`, `create_time`, `update_time`) VALUES 
(1, 'A-1-1', 1, 1, 1, 1, 1, 1, NOW(), NOW()),
(1, 'A-1-2', 1, 2, 1, 1, 0, 1, NOW(), NOW()),
(1, 'A-1-3', 1, 3, 1, 1, 0, 1, NOW(), NOW()),
(1, 'A-2-1', 2, 1, 1, 0, 0, 1, NOW(), NOW()),
(1, 'A-2-2', 2, 2, 0, 0, 0, 1, NOW(), NOW()),
(1, 'A-2-3', 2, 3, 0, 0, 0, 1, NOW(), NOW());

-- 插入测试座位（自习室2）
INSERT INTO `seat` (`study_room_id`, `seat_number`, `row_number`, `column_number`, `has_power`, `is_window`, `is_corner`, `status`, `create_time`, `update_time`) VALUES 
(2, 'B-1-1', 1, 1, 1, 1, 1, 1, NOW(), NOW()),
(2, 'B-1-2', 1, 2, 1, 1, 0, 1, NOW(), NOW()),
(2, 'B-2-1', 2, 1, 1, 0, 0, 1, NOW(), NOW()),
(2, 'B-2-2', 2, 2, 0, 0, 0, 0, NOW(), NOW());

-- 插入测试座位（自习室3）
INSERT INTO `seat` (`study_room_id`, `seat_number`, `row_number`, `column_number`, `has_power`, `is_window`, `is_corner`, `status`, `create_time`, `update_time`) VALUES 
(3, 'L-1-1', 1, 1, 1, 1, 1, 1, NOW(), NOW()),
(3, 'L-1-2', 1, 2, 1, 1, 0, 1, NOW(), NOW()),
(3, 'L-2-1', 2, 1, 1, 0, 0, 1, NOW(), NOW()),
(3, 'L-2-2', 2, 2, 0, 0, 0, 1, NOW(), NOW()); 