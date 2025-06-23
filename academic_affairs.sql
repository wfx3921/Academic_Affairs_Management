/*
 Navicat Premium Data Transfer

 Source Server         : wfx
 Source Server Type    : MySQL
 Source Server Version : 80039
 Source Host           : localhost:3306
 Source Schema         : academic_affairs2

 Target Server Type    : MySQL
 Target Server Version : 80039
 File Encoding         : 65001

 Date: 23/06/2025 10:26:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `course_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `grade_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `teacher_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `weight` double(99, 0) NULL DEFAULT NULL,
  `name` varchar(99) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `type` varchar(99) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `permission` int(0) NULL DEFAULT 1,
  `exam_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity
-- ----------------------------
INSERT INTO `activity` VALUES (18, '2', '2020级', '2020级1班', '1915091814727446529', 20, '作业1', 'homework', 1, NULL);
INSERT INTO `activity` VALUES (19, '2', '2020级', '2020级1班', '1915091814727446529', 40, '作业2', 'homework', 1, NULL);
INSERT INTO `activity` VALUES (20, '2', '2020级', '2020级1班', '1915091814727446529', 20, '期中考试', 'exam', 1, NULL);
INSERT INTO `activity` VALUES (21, '2', '2020级', '2020级1班', '1915091814727446529', 20, '期末考试', 'exam', 1, NULL);
INSERT INTO `activity` VALUES (22, 'CS201', '1', '2', '1915091814727446529', 100, '1', 'homework', 0, NULL);
INSERT INTO `activity` VALUES (23, '2', '1', '2', '1915091814727446529', 100, '1', 'homework', 1, NULL);
INSERT INTO `activity` VALUES (27, '2', '1', '2', '1915091814727446529', 0, '00', 'exam', 1, '2025-05-26 15:42:23');
INSERT INTO `activity` VALUES (28, '2', '1', '2', '1912907341909479426', 0, '1', 'homework', 1, NULL);
INSERT INTO `activity` VALUES (29, '1', '1', '2', '1915091814727446529', 50, '11', 'homework', 1, NULL);
INSERT INTO `activity` VALUES (30, '1', '1', '2', '1915091814727446529', 50, '21', 'exam', 0, '2025-06-10 00:00:00');

-- ----------------------------
-- Table structure for class
-- ----------------------------
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称',
  `grade_id` bigint(0) UNSIGNED NOT NULL COMMENT '所属年级ID',
  `major_id` bigint(0) UNSIGNED NOT NULL COMMENT '所属专业ID',
  `class_student_count` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '班级总学生数',
  `head_teacher` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班主任姓名',
  `class_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级代码',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述或备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_class_grade`(`grade_id`) USING BTREE,
  INDEX `fk_class_major`(`major_id`) USING BTREE,
  CONSTRAINT `fk_class_grade` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1922573073806954498 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of class
-- ----------------------------
INSERT INTO `class` VALUES (1922566262882246657, '2', 1922566244158873602, 1898357239878107137, 2, '2', '2', NULL, '2025-05-14 16:14:51', '2025-05-14 16:14:51', NULL, NULL);
INSERT INTO `class` VALUES (1922573035554902018, '4', 1922566244158873602, 1898357239878107137, 4, '4', '4', NULL, '2025-05-14 16:41:46', '2025-05-14 16:41:46', NULL, NULL);
INSERT INTO `class` VALUES (1922573073806954497, '77', 1922573020765786113, 1898357239878107137, 77, '77', '77', '77', '2025-05-14 16:41:55', '2025-05-14 16:41:55', NULL, NULL);

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程名称（中文）',
  `course_name_en` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程名称（英文）',
  `course_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程代码',
  `credit` decimal(5, 3) NOT NULL COMMENT '学分',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属专业',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任课教师姓名',
  `course_type` enum('必修','选修','实践') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '必修' COMMENT '课程类型',
  `total_hours` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '总课时数',
  `academic_year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '选课学年，如2024-2025',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学期，如春季、秋季',
  `grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用年级，如2025级',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述或备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `day` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `times` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `teacher_id` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `course_code`(`course_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1932695973671141380 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1898359157098598401, '综合英语(一）', 'Comprehensive English', '1', 4.000, '综合英语', 'renke', '必修', 72, '2024-2025', '1', '2025级', '课程描述已更新', '2025-03-08 21:04:28', '2025-05-09 01:08:38', '1111', NULL, NULL, NULL, 191290734190947942);
INSERT INTO `course` VALUES (1898359157098598461, '数据结构', 'Data Structures', 'CS201', 4.000, '计算机科学与技术', 'renke', '必修', 48, '2024-2025', '2', '2025级', '数据结构课程描述已更新', '2025-03-08 21:04:28', '2025-05-08 23:50:50', '1001', NULL, '2', '4', 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392067, '口语(一）', 'Speaking', '2', 4.000, '口语', 'renke', '必修', 72, '2024-2025', '1', '2025级', NULL, '2025-04-14 13:12:56', '2025-05-08 23:50:51', '1111', NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392068, '阅读(一）', 'Reading', '4', 4.000, '阅读', 'renke', '必修', 72, NULL, '1', '2025级', NULL, '2025-04-14 13:12:56', '2025-05-08 23:50:52', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392069, '写作(一）', 'Writing ', '5', 3.000, '写作', 'renke', '必修', 72, '2024-2025', '1', '2025级', NULL, '2025-04-14 13:12:57', '2025-05-08 23:50:53', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392075, '经济学导论', 'Economic Issues: An Introduction', '13', 4.000, '经济学导论', 'renke', '必修', 40, '2024-2025', '3', '2025级', NULL, '2025-04-14 13:13:03', '2025-05-08 23:50:54', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392076, '商法导论', 'Business Law: An Introduction', '14', 5.000, '商法导论', 'renke', '必修', 40, '2024-2025', '3', '2025级', NULL, '2025-04-14 13:13:03', '2025-05-08 23:50:56', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392077, '人力与组织管理', 'Managing People and Organisations', '15', 3.000, '人力与组织管理', 'renke', '必修', 80, '2024-2025', '3', '2025级', NULL, '2025-04-14 13:13:05', '2025-05-08 23:50:57', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392079, '商务会计', 'Business Accounting', '17', 2.000, '商务会计', 'renke', '必修', 80, '2024-2025', '3', '2025级', NULL, '2025-04-14 13:14:41', '2025-05-08 23:50:58', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392080, '职场英语', 'Workplace Communication in English', '18', 2.000, '职场英语', 'renke', '必修', 40, '2024-2025', '3', '2025级', NULL, '2025-04-14 13:14:49', '2025-05-08 23:50:59', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392082, '保险原理', 'Principles of Insurance', '20', 0.000, '保险原理', 'renke', '必修', 40, '2024-2025', '4', NULL, NULL, '2025-04-14 13:15:10', '2025-05-08 23:51:00', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392084, '客户服务文化构建', 'Creating a Culture of Customer Care', '222', 0.000, '客户服务文化构建', 'renke', '必修', 40, '2024-2025', '4', NULL, NULL, '2025-04-14 13:15:17', '2025-05-10 14:37:31', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392085, '个人理财服务', 'Personal Financial Services', '22', 0.000, '个人理财服务', 'renke', '必修', 40, '2024-2025', '4', NULL, NULL, '2025-04-14 13:15:17', '2025-05-08 23:51:02', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392086, '金融业导论', 'Financial Sector: An Introduction', '23', 0.000, '金融业导论', 'renke', '必修', 80, '2024-2025', '4', NULL, NULL, '2025-04-14 13:15:34', '2025-05-08 23:51:03', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392087, '沟通学：商务沟通', 'Communications: Business Communication', '24', 0.000, '沟通学：商务沟通', 'renke', '必修', 40, '2024-2025', '4', NULL, NULL, '2025-04-14 13:15:41', '2025-05-08 23:51:03', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392088, '研究技巧', 'Research Skills', '25', 0.000, '研究技巧', 'renke', '必修', 80, '2024-2025', '4', NULL, NULL, '2025-04-14 13:15:50', '2025-05-08 23:51:04', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392089, '国际理财综合课1 ', 'Financial Services: Graded Unit 1', '26', 0.000, '国际理财综合课1 ', 'renke', '必修', 40, '2024-2025', '4', NULL, NULL, '2025-04-14 13:15:57', '2025-05-08 23:51:05', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392090, '经济学2:世界经济', 'Economics 2:The World Economy', '27', 0.000, '经济学2:世界经济', 'renke', '必修', 80, '2024-2025', '5', NULL, NULL, '2025-04-14 13:16:04', '2025-05-08 23:51:06', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392091, '金融服务：国际交易', 'Financial Services: International Transactions', '28', 0.000, '金融服务：国际交易', 'renke', '必修', 80, '2024-2025', '5', NULL, NULL, '2025-04-14 13:16:15', '2025-05-08 23:51:06', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392092, '商务信息技术：应用软件2', ' Information Technology: Applications Software 2', '29', 0.000, '商务信息技术：应用软件2', 'renke', '必修', 80, '2024-2025', '5', NULL, NULL, '2025-04-14 13:16:21', '2025-05-08 23:51:07', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392093, '养老金', 'Pension Provision', '30', 0.000, '养老金', 'renke', '必修', 40, '2024-2025', '5', NULL, NULL, '2025-04-14 13:16:29', '2025-05-08 23:51:08', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392094, '金融服务监管框架', 'Financial Services Regulatory Framework', '31', 0.000, '金融服务监管框架', 'renke', '必修', 40, '2024-2025', '5', NULL, NULL, '2025-04-14 13:16:35', '2025-05-08 23:51:08', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392095, '投资学', 'Investment', '32', 0.000, '投资学', 'renke', '必修', 80, '2024-2025', '5', NULL, NULL, '2025-04-14 13:16:41', '2025-05-08 23:51:09', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392096, '财政预算', 'Preparing Financial Forecasts', '33', 0.000, '财政预算', 'renke', '必修', 40, '2024-2025', '6', NULL, NULL, '2025-04-14 13:16:48', '2025-05-08 23:51:10', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392097, '收入税', 'Income Tax', '34', 0.000, '收入税', 'renke', '必修', 80, '2024-2025', '6', NULL, NULL, '2025-04-14 13:16:53', '2025-05-08 23:51:10', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392098, '个人商业信贷', 'Personal and Business Lending', '35', 0.000, '个人商业信贷', 'renke', '必修', 40, '2024-2025', '6', NULL, NULL, '2025-04-14 13:16:58', '2025-05-08 23:51:11', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1904062398786392099, '国际理财综合课2 ', 'Financial Services Graded Unit 2', '36', 0.000, '国际理财综合课2 ', 'renke', '必修', 80, '2024-2025', '6', NULL, NULL, '2025-04-14 13:17:08', '2025-05-08 23:51:12', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1912701005780713473, '数学', 'math', '102', 3.000, 'major', 'renke', '必修', 48, '2024-2025', NULL, '2023', NULL, '2025-04-17 10:53:51', '2025-05-08 23:51:14', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1913188358567698434, '综合英语(二）', 'Comprehensive English', '200', 4.000, '综合英语', 'renke', '必修', 72, '2024-2025', '2', NULL, NULL, '2025-04-23 14:46:18', '2025-05-08 23:51:15', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1914934422031294466, '听力(一）', 'Listening', '3', 3.000, '英语', 'renke', '必修', 72, '2024-2025', '1', '2022', NULL, '2025-04-23 14:48:39', '2025-05-08 23:51:17', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1914934599446159362, '口语(二）', 'Speaking', '203', 72.000, '英语', 'renke', '必修', 0, '2024-2025', '2', '1', NULL, '2025-04-23 14:49:21', '2025-05-08 23:51:19', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1914934682887643137, '阅读(二）', 'Reading', '204', 72.000, '英语', 'renke', '必修', 0, '2024-2025', '2', '1', NULL, '2025-04-23 14:49:41', '2025-05-08 23:51:22', NULL, NULL, NULL, NULL, 1912907341909479426);
INSERT INTO `course` VALUES (1914934749577076738, '写作(二）', 'Writing', '205', 72.000, '英语', 'renke', '必修', 0, '2024-2025', '2', '1', NULL, '2025-04-23 14:49:57', '2025-05-08 23:51:30', NULL, NULL, NULL, NULL, 1912907341909479426);

-- ----------------------------
-- Table structure for grade
-- ----------------------------
DROP TABLE IF EXISTS `grade`;
CREATE TABLE `grade`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `grade_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '年级名称，如2025级',
  `total_students` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '该年级总学生数',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述或备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1922573020765786114 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '年级信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of grade
-- ----------------------------
INSERT INTO `grade` VALUES (1922566244158873602, '1', 1, '1', '2025-05-14 16:14:47', '2025-05-14 16:14:47', NULL, NULL);
INSERT INTO `grade` VALUES (1922573020765786113, '4', 4, '4', '2025-05-14 16:41:42', '2025-05-14 16:41:42', NULL, NULL);

-- ----------------------------
-- Table structure for graduation
-- ----------------------------
DROP TABLE IF EXISTS `graduation`;
CREATE TABLE `graduation`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `student_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生学号，关联 student_info 表',
  `graduation_status` enum('pending','approved','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '毕业审核状态',
  `graduate_destination` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '毕业去向/升学去向',
  `employment_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '就业情况说明',
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `special_type` enum('none','nontraditional','exempt','other') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'none' COMMENT '特殊毕业生类型：untraditional表示非传统，nontraditional表示非传统学历路径，exampt表示免修课程，other表示其他特殊申请',
  `special_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '特殊情况说明，如具体描述特殊毕业原因',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `is_passed` tinyint(1) NULL DEFAULT 0 COMMENT '是否通过:0-否 1-是',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `student_number`(`student_number`, `graduation_status`, `graduate_destination`) USING BTREE,
  CONSTRAINT `fk_graduation_student` FOREIGN KEY (`student_number`) REFERENCES `student_info` (`student_number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1922134646087192578 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '毕业管理信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of graduation
-- ----------------------------
INSERT INTO `graduation` VALUES (1922134646087192577, '1', 'pending', '升学', '前端开发', '', 'none', '', '2025-05-13 11:39:46', '2025-05-13 12:03:16', NULL, NULL, 1, NULL);

-- ----------------------------
-- Table structure for graduation_open_time
-- ----------------------------
DROP TABLE IF EXISTS `graduation_open_time`;
CREATE TABLE `graduation_open_time`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '填写毕业信息时间表id',
  `start_time` datetime(0) NULL DEFAULT NULL,
  `end_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of graduation_open_time
-- ----------------------------

-- ----------------------------
-- Table structure for major
-- ----------------------------
DROP TABLE IF EXISTS `major`;
CREATE TABLE `major`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `major_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专业代码',
  `major_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业名称',
  `college` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属学院或系部',
  `total_students` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '该专业总学生数',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述或备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `major_code`(`major_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1912701272278401026 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '专业信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of major
-- ----------------------------
INSERT INTO `major` VALUES (1898357239878107137, 'CS01232', '计算机科学与技术', '信息学院', 220, '计算机科学与技术描述', '2025-03-08 20:56:50', '2025-05-13 11:24:18', '1001', NULL);
INSERT INTO `major` VALUES (1912701272278401025, '202', '土木工程', '国际教育', 40, '土木工程专业描述', '2025-04-17 10:54:54', '2025-05-13 11:24:05', NULL, NULL);

-- ----------------------------
-- Table structure for schedule
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `course_id` bigint(0) UNSIGNED NOT NULL COMMENT '课程ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任课教师姓名',
  `class_id` bigint(0) UNSIGNED NOT NULL COMMENT '上课班级ID',
  `week_day` tinyint(0) NULL DEFAULT NULL COMMENT '星期几（1-7）',
  `start_time` int(0) NULL DEFAULT NULL COMMENT '上课开始时间',
  `end_time` int(0) NULL DEFAULT NULL COMMENT '上课结束时间',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上课地点',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `jie_start` int(0) NULL DEFAULT NULL,
  `jie_end` int(0) NULL DEFAULT NULL,
  `term` int(0) NULL DEFAULT NULL,
  `grade_id` bigint(0) NULL DEFAULT NULL COMMENT '年级id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_schedule_course`(`course_id`) USING BTREE,
  INDEX `fk_schedule_class`(`class_id`) USING BTREE,
  CONSTRAINT `fk_schedule_class` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_schedule_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1932696228743544835 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程排课表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schedule
-- ----------------------------
INSERT INTO `schedule` VALUES (1926911147932327937, 1904062398786392075, '2', 1922566262882246657, 1, 7, 9, '3', '2025-05-26 15:59:53', '2025-05-26 15:59:53', NULL, NULL, 1, 1, 2, 1922566244158873602);
INSERT INTO `schedule` VALUES (1932696228743544834, 1904062398786392075, '1', 1922566262882246657, 1, 1, 2, '1', '2025-06-11 15:07:43', '2025-06-11 15:07:43', NULL, NULL, 2, 5, 1, 1922566244158873602);

-- ----------------------------
-- Table structure for score
-- ----------------------------
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `student_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生学号，关联 student_info 表',
  `course_id` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '课程ID',
  `score_value` decimal(5, 2) NULL DEFAULT NULL COMMENT '成绩分数',
  `audit_status` enum('pending','approved','rejected','approved1','approved2','initial') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'initial' COMMENT '成绩审核状态',
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教务审核意见',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '录入或审核成绩教师姓名',
  `score_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '成绩类型',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `exam_type` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `course_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `score_modify` decimal(5, 0) NULL DEFAULT NULL,
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `audit_zhuren` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主任审核意见',
  `audit_yuanzhang` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '院长审核意见',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `student_number`(`student_number`, `course_id`, `score_type`, `audit_status`, `exam_type`) USING BTREE,
  INDEX `fk_score_course`(`course_id`) USING BTREE,
  CONSTRAINT `fk_score_student` FOREIGN KEY (`student_number`) REFERENCES `student_info` (`student_number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1922607777071468546 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '成绩信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of score
-- ----------------------------

-- ----------------------------
-- Table structure for score1
-- ----------------------------
DROP TABLE IF EXISTS `score1`;
CREATE TABLE `score1`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `acticity_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `student_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of score1
-- ----------------------------
INSERT INTO `score1` VALUES (22, '20', '20', '21240213280');
INSERT INTO `score1` VALUES (23, '20', '21', '21240213280');
INSERT INTO `score1` VALUES (24, '10', '18', '21240213280');
INSERT INTO `score1` VALUES (25, '10', '19', '21240213280');
INSERT INTO `score1` VALUES (26, '100', '24', '21240213280');
INSERT INTO `score1` VALUES (27, '32', '22', '21240213280');
INSERT INTO `score1` VALUES (28, '67', '23', '21240213280');
INSERT INTO `score1` VALUES (29, '55', '27', '21240213188');
INSERT INTO `score1` VALUES (30, '44', '27', '21240213187');
INSERT INTO `score1` VALUES (31, '33', '27', '22');
INSERT INTO `score1` VALUES (32, '22', '27', '1');
INSERT INTO `score1` VALUES (33, '22', '29', '1');
INSERT INTO `score1` VALUES (34, '33', '29', '22');
INSERT INTO `score1` VALUES (35, '44', '29', '21240213187');
INSERT INTO `score1` VALUES (36, '55', '29', '21240213188');
INSERT INTO `score1` VALUES (37, '100', '30', '21240213188');
INSERT INTO `score1` VALUES (38, '60', '30', '21240213187');
INSERT INTO `score1` VALUES (39, '40', '30', '22');
INSERT INTO `score1` VALUES (40, '20', '30', '1');

-- ----------------------------
-- Table structure for status_change
-- ----------------------------
DROP TABLE IF EXISTS `status_change`;
CREATE TABLE `status_change`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `student_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `before_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `after_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `pass_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `if_pass` int(0) NULL DEFAULT 0 COMMENT '0:未审批 1：未通过 2：通过',
  `approval_table` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `if_pass2` int(0) NULL DEFAULT 0,
  `pass_by2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of status_change
-- ----------------------------
INSERT INTO `status_change` VALUES (1, '1', '王凤祥1', '正常', '休学', 'a', 'a', '1', 2, 'C:/uploads/diplomas/____1748770098624.jpg', 1, '1');
INSERT INTO `status_change` VALUES (2, '1', '王凤祥2', '正常', '延期毕业', 's', 's', '2', 1, 'C:/uploads/diplomas/____1748792895053.jpg', 0, '2');
INSERT INTO `status_change` VALUES (8, '22', '王凤祥4', '正常', '休学', 'c', 'c', '3', 2, 'C:/uploads/diplomas/22_1748798083182.png', 2, '3');
INSERT INTO `status_change` VALUES (9, '21240213187', '王凤祥3', '已毕业', '休学', 'd', 'a', '4', 2, 'C:/uploads/diplomas/____1748841308230.png', 2, '4');
INSERT INTO `status_change` VALUES (10, '21240213188', '王凤祥5', '正常', '退学', 'e', 's', '5', 2, 'C:/uploads/diplomas/____1748882400722.jpg', 2, '5');
INSERT INTO `status_change` VALUES (11, '21240212186', '王凤祥6', '正常', '休学', 'f', 'e', '6', 2, 'C:/uploads/diplomas/____1748922311596.png', 2, '6');

-- ----------------------------
-- Table structure for student_application
-- ----------------------------
DROP TABLE IF EXISTS `student_application`;
CREATE TABLE `student_application`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '性别',
  `birth_date` date NOT NULL COMMENT '出生日期',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `high_school` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '高中学校',
  `graduation_date` date NOT NULL COMMENT '毕业日期',
  `address_country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '国家',
  `address_province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省',
  `address_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '市',
  `address_district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区',
  `address_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `parent_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '家长电话',
  `college_exam_score` int(0) NULL DEFAULT NULL COMMENT '高考成绩',
  `other_language_score` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '其它语言成绩',
  `high_school_diploma` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '高中毕业证',
  `open_day_time` datetime(0) NOT NULL COMMENT '开放日时间',
  `pass` int(0) NULL DEFAULT 0 COMMENT '是否录取',
  `pay` int(0) NULL DEFAULT 0,
  `firstname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名',
  `lastname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓',
  `firstname_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名_拼音',
  `lastname_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓_拼音',
  `name_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名_拼音',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id_card`(`id_card`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_application
-- ----------------------------
INSERT INTO `student_application` VALUES (59, '王凤祥1', '男', '2010-05-13', '370285200101083517', '莱西一中', '2019-05-14', '中国', '山东', '青岛', '莱西', '沽河街道-莱西', '19861109706', '2237097623@qq.com', '15092232564', 341, '80', 'C:/uploads/diplomas/____1747106404056.png', '2025-05-22 08:00:00', 1, 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `student_application` VALUES (67, '王凤祥2', '男', '2025-05-07', '371102200112014118', 'aa', '2025-05-15', 'aa', 'aa', 'aaa', 'aa', 'aaaaa', '18263369381', '2534474957@qq.com', '18263369381', 541, '43', 'C:/uploads/diplomas/____1747651954684.jpg', '2025-05-08 08:00:00', 1, 1, '艺洋', '高', 'yiyang', 'gao', 'gaoyiyang');

-- ----------------------------
-- Table structure for student_awards_punishments
-- ----------------------------
DROP TABLE IF EXISTS `student_awards_punishments`;
CREATE TABLE `student_awards_punishments`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `student_id` bigint(0) NULL DEFAULT NULL COMMENT '学生id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `student_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '年级',
  `major` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专业',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '奖惩类型（奖、惩）',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '奖惩级别',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '描述',
  `record_date` date NOT NULL COMMENT '记录日期',
  `start_date` date NULL DEFAULT NULL COMMENT '开始时间',
  `end_date` date NULL DEFAULT NULL COMMENT '结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '有效' COMMENT '记录状态（有效、撤销）',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录人',
  `created_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '记录时间',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id`) USING BTREE,
  CONSTRAINT `student_awards_punishments_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_awards_punishments
-- ----------------------------
INSERT INTO `student_awards_punishments` VALUES (13, NULL, 'aaa', '111111', '1', '计算机科学与技术', '奖励', '1', '11', '2025-05-28', '2025-05-06', '2025-05-07', '有效', '11', '2025-05-23 23:15:33', NULL, '2025-05-23 23:15:33');

-- ----------------------------
-- Table structure for student_course
-- ----------------------------
DROP TABLE IF EXISTS `student_course`;
CREATE TABLE `student_course`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生学号',
  `course_id` bigint(0) NOT NULL COMMENT '课程ID',
  `term` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期，例如2024-2025秋',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_term`(`student_number`, `term`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生选课表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_course
-- ----------------------------

-- ----------------------------
-- Table structure for student_info
-- ----------------------------
DROP TABLE IF EXISTS `student_info`;
CREATE TABLE `student_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `application_id` bigint(0) NULL DEFAULT NULL COMMENT '报名信息表的外键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '性别',
  `birth_date` date NOT NULL COMMENT '出生日期',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `high_school` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '高中学校',
  `graduation_date` date NOT NULL COMMENT '毕业日期',
  `address_country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '国家',
  `address_province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省',
  `address_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '市',
  `address_district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区',
  `address_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `parent_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '家长电话',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '电话号码',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `other_language_score` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '其它语言成绩',
  `college_exam_score` int(0) NULL DEFAULT NULL COMMENT '高考成绩',
  `high_school_diploma` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '高中毕业证',
  `open_day_time` datetime(0) NOT NULL COMMENT '开放日时间',
  `scn_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'scn号码',
  `student_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `major` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `class1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级',
  `student_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学籍状态',
  `registration_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报到状态',
  `pay` int(0) NULL DEFAULT 0 COMMENT '是否缴费',
  `lastname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓',
  `firstname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名',
  `lastname_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓_拼音',
  `firstname_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名_拼音',
  `name_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名_拼音',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `scn_number`(`scn_number`) USING BTREE,
  UNIQUE INDEX `student_number`(`student_number`) USING BTREE,
  INDEX `application_id`(`application_id`) USING BTREE,
  CONSTRAINT `student_info_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `student_application` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_info
-- ----------------------------
INSERT INTO `student_info` VALUES (40, 59, '王凤祥1', '男', '2010-05-13', '370285200101083517', '莱西一中', '2019-05-14', '中国', '山东', '青岛', '莱西', '沽河街道-莱西', '15092232564', '19861109706', '2237097623@qq.com', '80', 341, 'C:/uploads/diplomas/____1747106404056.png', '2025-05-22 08:00:00', '875416', '1', '1', '土木工程', '2', '已毕业', '已报到', 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `student_info` VALUES (41, NULL, '王凤祥2', '男', '2025-05-05', '22', '22', '2025-05-12', '222', '22', '22', '22', '222222', '222222222222222', '22222222222222222', '222@qq.com', '222', 222, 'C:/uploads/diplomas/22_1747391889673.png', '2025-05-21 08:00:00', '4444444', '22', '1', '计算机科学与技术', '2', '已毕业', '已毕业', 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `student_info` VALUES (49, 67, '王凤祥3', '男', '2025-05-07', '371102200112014118', 'aa', '2025-05-15', 'aa', 'aa', 'aaa', 'aa', 'aaaaa', '18263369381', '18263369381', '2534474957@qq.com', '43', 541, 'C:/uploads/diplomas/____1747651954684.jpg', '2025-05-08 08:00:00', NULL, '21240213187', '1', '计算机科学与技术', '2', '已毕业', '已毕业', 1, '高', '艺洋', 'gao', 'yiyang', 'gaoyiyang');
INSERT INTO `student_info` VALUES (50, NULL, '王凤祥4', '男', '2025-05-07', '21', 'aa', '2025-05-12', 'aa', 'aa', 'aa', 'aa', 'aaaaa', '111', '111', 'aa@qq.com', '65', 588, NULL, '2025-05-15 08:00:00', '321321', '21240213188', '1', '计算机科学与技术', '2', '已毕业', '已毕业', 0, '李', '小伟', NULL, NULL, NULL);
INSERT INTO `student_info` VALUES (51, NULL, '王凤祥5', '女', '2025-05-12', '212', 'aa', '2025-05-12', 'aa', 'aa', 'aa', 'aa', 'aaaaa', '111', '111', 'aa@qq.com', NULL, NULL, NULL, '2025-05-13 08:00:00', '123456', '21240213189', '4', '计算机科学与技术', '77', '已毕业', '已毕业', 0, '刘', '邓发', NULL, NULL, NULL);
INSERT INTO `student_info` VALUES (52, NULL, '王凤祥6', '男', '2025-05-08', '2313', '11', '2025-05-06', '11', '11', '11', '11', '11111', '111', '111', '11@qq.com', NULL, NULL, 'C:/uploads/diplomas/____1747839963146.jpg', '2025-05-13 08:00:00', NULL, '21240212186', '1', '计算机科学与技术', '4', '退学', '未报到', 0, '大', '老王', 'da', 'laowang', 'dalaowang');

-- ----------------------------
-- Table structure for student_user
-- ----------------------------
DROP TABLE IF EXISTS `student_user`;
CREATE TABLE `student_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生邮箱，作为登录账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密后的密码',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_user
-- ----------------------------
INSERT INTO `student_user` VALUES (43, '2237097623@qq.com', '015e20bdd093034e97363ef1439a72dc', '2025-05-13 11:17:54');
INSERT INTO `student_user` VALUES (44, '2534474957@qq.com', 'dc483e80a7a0bd9ef71d8cf973673924', '2025-05-19 12:34:55');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `login_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `dept_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `phone_num` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `user_type` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` timestamp(0) NULL DEFAULT NULL,
  `create_user` bigint(0) NULL DEFAULT NULL,
  `update_time` timestamp(0) NULL DEFAULT NULL,
  `update_user` bigint(0) NULL DEFAULT NULL,
  `delete_flag` tinyint(1) NOT NULL,
  `version` int(0) NULL DEFAULT NULL,
  `student_info_id` bigint(0) NULL DEFAULT NULL,
  `permission` int(0) NULL DEFAULT NULL,
  `message` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1929820236614131715 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1912807736362405890, 'gyy', 'gyy', '$2a$12$XukmfvtWzaz8xpDeITFAAuaq9gB7DJAN8FUWde1UADUGEzCf8oQ1e', '...', 'aa@qq.com', '231654646', '学生', 'active', '2025-04-17 17:57:57', NULL, '2025-04-17 17:57:57', NULL, 0, NULL, 6, 0, NULL);
INSERT INTO `user` VALUES (1912807736362405891, 'guanliyuan', 'guanliyuan', '$2a$12$4l52Si/1pyOrp11d42JlOuN7zEz2QF3uxQt9ry/eqJY2i84rqQz5y', '...', NULL, NULL, '管理员', 'active', NULL, NULL, NULL, NULL, 0, 0, NULL, 0, NULL);
INSERT INTO `user` VALUES (1912907341909479426, 'renke', 'renke', '$2a$12$WnSKfu.dOIZy1R29x0IZwecLsMtfV1/h61xjFHjOnCbCauHuaclVW', 'aa', 'a@qq.com', '15161561', '任课老师', 'active', '2025-04-18 00:33:45', NULL, '2025-05-09 10:09:22', NULL, 0, NULL, NULL, 0, '0');
INSERT INTO `user` VALUES (1914999965706846209, 'jiaowu', 'jiaowu', '$2a$12$FaZt7I0GUaOH1qxUJCn9Ku0ZGJYsnDaDNSN.a06EpaKWsvHZtGvU2', '123', '1@qq.com', '15096625485', '教务老师', 'active', '2025-04-23 19:09:05', NULL, '2025-04-23 19:09:05', NULL, 0, NULL, NULL, 0, NULL);
INSERT INTO `user` VALUES (1915040863127859201, 'fudaoyuan', 'fudaoyuan', '$2a$12$52ytuNrM1hWTC7SA5Aowqev6d2emsIFJDcN74FCNNFluHLIF1u6nC', '124', '1@qq.com', '15092232564', '辅导员', 'active', '2025-04-23 21:51:36', NULL, '2025-05-08 11:18:00', NULL, 0, NULL, NULL, 0, NULL);
INSERT INTO `user` VALUES (1915091814727446529, 'supadmin', 'supadmin', '$2a$12$kthaj6GBsaT5uNKSDH0GZeU.ygf5efggKw0QDCZaExVMF1hEZImj6', 'qqqq', 'q@qq.com', '15092232111', '超级管理员', 'active', '2025-04-24 01:14:04', NULL, '2025-04-24 01:14:04', NULL, 0, NULL, NULL, 0, NULL);
INSERT INTO `user` VALUES (1924418449489080321, '21240213187', '21240213187', '$2a$12$jVaeLuQVQMsoUM8q.iP1eO5zo3eM3vWDnsh4ShBCgwbBWm2vTGyxW', NULL, '2534474957@qq.com', '18263369381', '学生', 'inactive', '2025-05-19 18:54:47', NULL, '2025-05-22 00:48:01', NULL, 0, NULL, 49, NULL, NULL);
INSERT INTO `user` VALUES (1924464292971753473, '21240213188', '21240213188', '$2a$12$PkaH6LG4bYFaM6aeaepdMu3CLN1zQj0uq2DrMGbh68iVNyrFzU0Mu', '学生', 'aa@qq.com', '111', '学生', 'active', '2025-05-19 21:56:57', NULL, '2025-05-19 21:56:57', NULL, 0, NULL, 50, 0, NULL);
INSERT INTO `user` VALUES (1924468897113780225, '21240213189', '21240213189', '$2a$12$j11TIiL5jYqsUVYaqFp/m.PJvsDRZ5.SCcOu8BYRenKqa.gUhUByS', '学生', 'aa@qq.com', '111', '学生', 'active', '2025-05-19 22:15:15', NULL, '2025-05-19 22:15:15', NULL, 0, NULL, 51, 0, NULL);
INSERT INTO `user` VALUES (1925206460569079810, '21240212186', '21240212186', '$2a$12$MRdicIfXeUaPfsMqobfI2eEXepkQQNGbnrgdoVZpqF5J8NedgfOEa', '学生', '11@qq.com', '111', '学生', 'active', '2025-05-21 23:06:03', NULL, '2025-06-03 16:44:00', NULL, 0, NULL, 52, 0, NULL);
INSERT INTO `user` VALUES (1929820128573054978, 'zhuren', 'zhuren', '$2a$12$HMq1nu4k8dh0fZYQrj225.Lw3VHxbCXKtaB8K/1cXdKLHJSEvx3KO', '112424', '1@qq.com', '19861109706', '项目主任', 'active', '2025-06-03 16:39:08', NULL, '2025-06-03 16:39:08', NULL, 0, NULL, NULL, 0, NULL);
INSERT INTO `user` VALUES (1929820236614131714, 'yuanzhang', 'yuanzhang', '$2a$12$vc32ZdSzQgnw7rgQIXrT7u07Vy3ffRvUH6O/95mxxZfwvOCDy/xpm', '111', '1@q.com', '19861102452', '分管院长', 'active', '2025-06-03 16:39:33', NULL, '2025-06-03 16:39:33', NULL, 0, NULL, NULL, 0, NULL);

-- ----------------------------
-- Table structure for verification_code
-- ----------------------------
DROP TABLE IF EXISTS `verification_code`;
CREATE TABLE `verification_code`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收验证码的邮箱',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码内容',
  `expire_time` datetime(0) NOT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of verification_code
-- ----------------------------
INSERT INTO `verification_code` VALUES (17, '2237097623@qq.com', '665340', '2025-05-13 11:22:35');
INSERT INTO `verification_code` VALUES (18, '2534474957@qq.com', '669751', '2025-05-19 12:38:56');

SET FOREIGN_KEY_CHECKS = 1;
