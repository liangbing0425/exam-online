-- =============================================
-- 基于SSM的在线考试与自动组卷系统数据库设计
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS exam_online DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE exam_online;

-- =============================================
-- 1. 用户表
-- =============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(加密)',
  `name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `role` VARCHAR(20) NOT NULL COMMENT '角色: admin-管理员, teacher-教师, student-学生',
  `email` VARCHAR(100) COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `avatar` VARCHAR(200) COMMENT '头像URL',
  `student_id` VARCHAR(20) COMMENT '学号(学生专用)',
  `class_name` VARCHAR(50) COMMENT '班级(学生专用)',
  `subject` VARCHAR(50) COMMENT '任教科目(教师专用)',
  `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-正常, inactive-禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 2. 科目表
-- =============================================
DROP TABLE IF EXISTS `subject`;
CREATE TABLE `subject` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '科目ID',
  `name` VARCHAR(50) NOT NULL COMMENT '科目名称',
  `code` VARCHAR(20) NOT NULL COMMENT '科目代码',
  `description` TEXT COMMENT '科目描述',
  `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科目表';

-- =============================================
-- 3. 章节表
-- =============================================
DROP TABLE IF EXISTS `chapter`;
CREATE TABLE `chapter` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `subject_id` BIGINT NOT NULL COMMENT '科目ID',
  `name` VARCHAR(100) NOT NULL COMMENT '章节名称',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父章节ID',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `description` TEXT COMMENT '章节描述',
  `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_subject_id` (`subject_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节表';

-- =============================================
-- 4. 试题表
-- =============================================
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '试题ID',
  `type` VARCHAR(20) NOT NULL COMMENT '题型: single-单选题, multiple-多选题, judge-判断题, fill-填空题, essay-简答题',
  `content` TEXT NOT NULL COMMENT '题目内容',
  `options` JSON COMMENT '选项(JSON格式)',
  `answer` TEXT NOT NULL COMMENT '正确答案',
  `analysis` TEXT COMMENT '答案解析',
  `difficulty` VARCHAR(20) NOT NULL DEFAULT 'medium' COMMENT '难度: easy-简单, medium-中等, hard-困难',
  `subject` VARCHAR(50) NOT NULL COMMENT '所属科目',
  `chapter` VARCHAR(100) COMMENT '所属章节',
  `tags` JSON COMMENT '标签',
  `score` DECIMAL(5,2) NOT NULL DEFAULT 2.00 COMMENT '分值',
  `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
  `creator_name` VARCHAR(50) NOT NULL COMMENT '创建者姓名',
  `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-启用, inactive-禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_subject` (`subject`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试题表';

-- =============================================
-- 5. 试卷表
-- =============================================
DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `name` VARCHAR(200) NOT NULL COMMENT '试卷名称',
  `subject` VARCHAR(50) NOT NULL COMMENT '所属科目',
  `duration` INT NOT NULL COMMENT '考试时长(分钟)',
  `total_score` DECIMAL(6,2) NOT NULL DEFAULT 100.00 COMMENT '总分',
  `pass_score` DECIMAL(6,2) NOT NULL DEFAULT 60.00 COMMENT '及格分',
  `question_count` INT NOT NULL DEFAULT 0 COMMENT '题目数量',
  `difficulty` VARCHAR(20) NOT NULL DEFAULT 'medium' COMMENT '整体难度',
  `description` TEXT COMMENT '试卷描述',
  `config` JSON COMMENT '组卷配置(JSON格式)',
  `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态: draft-草稿, published-已发布',
  `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
  `creator_name` VARCHAR(50) NOT NULL COMMENT '创建者姓名',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_subject` (`subject`),
  KEY `idx_status` (`status`),
  KEY `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- =============================================
-- 6. 试卷-试题关联表
-- =============================================
DROP TABLE IF EXISTS `paper_question`;
CREATE TABLE `paper_question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
  `question_id` BIGINT NOT NULL COMMENT '试题ID',
  `question_type` VARCHAR(20) NOT NULL COMMENT '试题类型',
  `question_content` TEXT NOT NULL COMMENT '试题内容(冗余字段)',
  `question_score` DECIMAL(5,2) NOT NULL COMMENT '试题分值',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷-试题关联表';

-- =============================================
-- 7. 考试表
-- =============================================
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '考试ID',
  `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
  `paper_name` VARCHAR(200) NOT NULL COMMENT '试卷名称',
  `subject` VARCHAR(50) NOT NULL COMMENT '科目',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `duration` INT NOT NULL COMMENT '考试时长(分钟)',
  `status` VARCHAR(20) NOT NULL DEFAULT 'upcoming' COMMENT '状态: upcoming-未开始, ongoing-进行中, ended-已结束',
  `participant_count` INT NOT NULL DEFAULT 0 COMMENT '参与人数',
  `submitted_count` INT NOT NULL DEFAULT 0 COMMENT '已提交人数',
  `allow_review` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否允许查看答卷',
  `random_order` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否随机题目顺序',
  `anti_cheat` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开启防作弊',
  `max_switch_times` INT NOT NULL DEFAULT 3 COMMENT '最大切屏次数',
  `description` TEXT COMMENT '考试说明',
  `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
  `creator_name` VARCHAR(50) NOT NULL COMMENT '创建者姓名',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_status` (`status`),
  KEY `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

-- =============================================
-- 8. 考试-学生关联表
-- =============================================
DROP TABLE IF EXISTS `exam_student`;
CREATE TABLE `exam_student` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `exam_id` BIGINT NOT NULL COMMENT '考试ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `student_no` VARCHAR(20) NOT NULL COMMENT '学号',
  `student_name` VARCHAR(50) NOT NULL COMMENT '学生姓名',
  `status` VARCHAR(20) NOT NULL DEFAULT 'not_started' COMMENT '状态: not_started-未开始, in_progress-考试中, submitted-已提交, absent-缺考',
  `start_exam_time` DATETIME COMMENT '开始考试时间',
  `submit_time` DATETIME COMMENT '提交时间',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `switch_times` INT NOT NULL DEFAULT 0 COMMENT '切屏次数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_student` (`exam_id`, `student_id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试-学生关联表';

-- =============================================
-- 9. 答题记录表
-- =============================================
DROP TABLE IF EXISTS `answer_record`;
CREATE TABLE `answer_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` BIGINT NOT NULL COMMENT '考试ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `question_id` BIGINT NOT NULL COMMENT '试题ID',
  `question_type` VARCHAR(20) NOT NULL COMMENT '试题类型',
  `question_content` TEXT NOT NULL COMMENT '试题内容',
  `question_score` DECIMAL(5,2) NOT NULL COMMENT '试题分值',
  `student_answer` TEXT COMMENT '学生答案',
  `correct_answer` TEXT NOT NULL COMMENT '正确答案',
  `is_correct` TINYINT(1) COMMENT '是否正确: 1-正确, 0-错误, NULL-待批阅',
  `score` DECIMAL(5,2) DEFAULT 0.00 COMMENT '得分',
  `teacher_comment` TEXT COMMENT '教师评语',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';

-- =============================================
-- 10. 成绩表
-- =============================================
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `exam_id` BIGINT NOT NULL COMMENT '考试ID',
  `exam_name` VARCHAR(200) NOT NULL COMMENT '考试名称',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `student_no` VARCHAR(20) NOT NULL COMMENT '学号',
  `student_name` VARCHAR(50) NOT NULL COMMENT '学生姓名',
  `total_score` DECIMAL(6,2) NOT NULL DEFAULT 0.00 COMMENT '总分',
  `objective_score` DECIMAL(6,2) NOT NULL DEFAULT 0.00 COMMENT '客观题得分',
  `subjective_score` DECIMAL(6,2) NOT NULL DEFAULT 0.00 COMMENT '主观题得分',
  `rank` INT COMMENT '排名',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending-待阅卷, graded-已阅卷',
  `submit_time` DATETIME COMMENT '提交时间',
  `grade_time` DATETIME COMMENT '阅卷时间',
  `grader_id` BIGINT COMMENT '阅卷人ID',
  `grader_name` VARCHAR(50) COMMENT '阅卷人姓名',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_student` (`exam_id`, `student_id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_student_no` (`student_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- =============================================
-- 11. 系统日志表
-- =============================================
DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT COMMENT '用户ID',
  `username` VARCHAR(50) COMMENT '用户名',
  `operation` VARCHAR(100) NOT NULL COMMENT '操作',
  `module` VARCHAR(50) NOT NULL COMMENT '模块',
  `method` VARCHAR(200) COMMENT '方法名',
  `params` TEXT COMMENT '请求参数',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `user_agent` VARCHAR(500) COMMENT '用户代理',
  `execute_time` BIGINT COMMENT '执行时间(ms)',
  `status` VARCHAR(20) NOT NULL DEFAULT 'success' COMMENT '状态: success-成功, error-失败',
  `error_msg` TEXT COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- =============================================
-- 12. 通知表
-- =============================================
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `type` VARCHAR(20) NOT NULL DEFAULT 'system' COMMENT '类型: system-系统, exam-考试, score-成绩',
  `target_type` VARCHAR(20) NOT NULL DEFAULT 'all' COMMENT '目标类型: all-全部, role-角色, user-指定用户',
  `target_value` VARCHAR(200) COMMENT '目标值',
  `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  `priority` VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '优先级: low-低, normal-普通, high-高',
  `sender_id` BIGINT COMMENT '发送者ID',
  `sender_name` VARCHAR(50) COMMENT '发送者姓名',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_target_type` (`target_type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入默认管理员账户 (密码: 123456, MD5加密后: e10adc3949ba59abbe56e057f20f883e)
INSERT INTO `user` (`username`, `password`, `name`, `role`, `email`, `status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'admin', 'admin@edu.cn', 'active');

-- 插入教师账户
INSERT INTO `user` (`username`, `password`, `name`, `role`, `email`, `subject`, `status`) VALUES
('teacher01', 'e10adc3949ba59abbe56e057f20f883e', '张教授', 'teacher', 'zhang@edu.cn', '计算机科学', 'active'),
('teacher02', 'e10adc3949ba59abbe56e057f20f883e', '李老师', 'teacher', 'li@edu.cn', '数学', 'active');

-- 插入学生账户
INSERT INTO `user` (`username`, `password`, `name`, `role`, `email`, `student_id`, `class_name`, `status`) VALUES
('student01', 'e10adc3949ba59abbe56e057f20f883e', '王小明', 'student', 'wang@edu.cn', '2022001', '计算机2201', 'active'),
('student02', 'e10adc3949ba59abbe56e057f20f883e', '李小红', 'student', 'lixh@edu.cn', '2022002', '计算机2201', 'active'),
('student03', 'e10adc3949ba59abbe56e057f20f883e', '张伟', 'student', 'zhangw@edu.cn', '2022003', '计算机2202', 'active');

-- 插入科目数据
INSERT INTO `subject` (`name`, `code`, `description`) VALUES
('Java编程', 'JAVA', 'Java程序设计语言'),
('Java Web', 'JAVAWEB', 'Java Web开发技术'),
('数据库', 'DB', '数据库原理与应用'),
('计算机网络', 'NETWORK', '计算机网络基础'),
('数学', 'MATH', '高等数学'),
('英语', 'ENGLISH', '大学英语');

-- 插入章节数据
INSERT INTO `chapter` (`subject_id`, `name`, `parent_id`, `sort_order`) VALUES
(1, '基础语法', 0, 1),
(1, '面向对象', 0, 2),
(1, '集合框架', 0, 3),
(1, '异常处理', 0, 4),
(2, 'Servlet', 0, 1),
(2, 'JSP', 0, 2),
(2, 'SSM框架', 0, 3),
(2, 'Spring Boot', 0, 4),
(3, 'SQL基础', 0, 1),
(3, '数据库设计', 0, 2),
(3, '事务管理', 0, 3),
(3, '索引优化', 0, 4);

-- 插入试题数据
INSERT INTO `question` (`type`, `content`, `options`, `answer`, `difficulty`, `subject`, `chapter`, `tags`, `score`, `creator_id`, `creator_name`) VALUES
('single', 'Java中哪个关键字用于定义类？', '["class", "struct", "define", "type"]', 'A', 'easy', 'Java编程', '基础语法', '["Java", "基础"]', 2.00, 2, '张教授'),
('single', 'SSM框架中，M代表什么？', '["Model", "Module", "Method", "Main"]', 'A', 'medium', 'Java Web', 'SSM框架', '["SSM", "框架"]', 2.00, 2, '张教授'),
('multiple', '以下哪些是Spring框架的核心特性？', '["IoC", "AOP", "MVC", "ORM"]', 'ABC', 'medium', 'Java Web', 'Spring框架', '["Spring", "框架"]', 4.00, 2, '张教授'),
('judge', 'MyBatis是一种ORM框架。', '[]', 'A', 'easy', 'Java Web', 'MyBatis', '["MyBatis", "ORM"]', 2.00, 3, '李老师'),
('single', 'SQL语句中，用于查询的关键字是？', '["SELECT", "QUERY", "FIND", "GET"]', 'A', 'easy', '数据库', 'SQL基础', '["SQL", "数据库"]', 2.00, 3, '李老师'),
('fill', 'Spring MVC中，控制器使用____注解标识。', '[]', '@Controller', 'medium', 'Java Web', 'Spring MVC', '["Spring", "MVC"]', 3.00, 2, '张教授'),
('essay', '请简述SSM框架的工作原理及其优势。', '[]', 'SSM框架由Spring、Spring MVC和MyBatis组成...', 'hard', 'Java Web', 'SSM框架', '["SSM", "架构"]', 10.00, 2, '张教授'),
('single', 'HTTP协议默认使用的端口是？', '["80", "8080", "443", "3306"]', 'A', 'easy', '计算机网络', '应用层', '["HTTP", "网络"]', 2.00, 3, '李老师');

-- 插入试卷数据
INSERT INTO `paper` (`name`, `subject`, `duration`, `total_score`, `pass_score`, `question_count`, `difficulty`, `status`, `creator_id`, `creator_name`) VALUES
('Java基础测试卷', 'Java编程', 60, 100.00, 60.00, 25, 'medium', 'published', 2, '张教授'),
('SSM框架综合测试', 'Java Web', 90, 100.00, 60.00, 30, 'hard', 'published', 2, '张教授'),
('数据库原理测试', '数据库', 60, 100.00, 60.00, 20, 'medium', 'draft', 3, '李老师');

-- 插入考试数据
INSERT INTO `exam` (`paper_id`, `paper_name`, `subject`, `start_time`, `end_time`, `duration`, `status`, `participant_count`, `submitted_count`, `creator_id`, `creator_name`) VALUES
(1, 'Java基础测试卷', 'Java编程', '2024-12-20 09:00:00', '2024-12-20 10:00:00', 60, 'ongoing', 45, 23, 2, '张教授'),
(2, 'SSM框架综合测试', 'Java Web', '2024-12-21 14:00:00', '2024-12-21 15:30:00', 90, 'upcoming', 38, 0, 2, '张教授'),
(3, '数据库原理测试', '数据库', '2024-12-19 09:00:00', '2024-12-19 10:00:00', 60, 'ended', 50, 48, 3, '李老师');

-- 插入成绩数据
INSERT INTO `score` (`exam_id`, `exam_name`, `student_id`, `student_no`, `student_name`, `total_score`, `objective_score`, `subjective_score`, `rank`, `status`, `submit_time`, `grade_time`, `grader_id`, `grader_name`) VALUES
(1, 'Java基础测试卷', 4, '2022001', '王小明', 85.00, 45.00, 40.00, 2, 'graded', '2024-12-19 10:45:00', '2024-12-19 11:00:00', 2, '张教授'),
(1, 'Java基础测试卷', 5, '2022002', '李小红', 92.00, 48.00, 44.00, 1, 'graded', '2024-12-19 10:30:00', '2024-12-19 11:00:00', 2, '张教授'),
(1, 'Java基础测试卷', 6, '2022003', '张伟', 78.00, 42.00, 36.00, 3, 'graded', '2024-12-19 10:55:00', '2024-12-19 11:00:00', 2, '张教授'),
(3, '数据库原理测试', 4, '2022001', '王小明', 88.00, 46.00, 42.00, 1, 'graded', '2024-12-19 10:40:00', '2024-12-19 11:00:00', 3, '李老师');

-- 插入通知数据
INSERT INTO `notification` (`title`, `content`, `type`, `target_type`, `priority`, `sender_id`, `sender_name`) VALUES
('系统维护通知', '系统将于今晚23:00-01:00进行维护升级，请提前保存数据。', 'system', 'all', 'high', 1, '系统管理员'),
('新考试发布', 'Java基础测试卷已发布，请及时参加。', 'exam', 'role', 'normal', 2, '张教授'),
('成绩公布', '数据库原理测试成绩已公布，请查看。', 'score', 'role', 'normal', 3, '李老师');
