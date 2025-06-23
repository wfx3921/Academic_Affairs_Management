package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("score")
public class Score {
    @TableId
    private Long id;

    // 学生学号，关联 student_info 表
    private String studentNumber;
    private  String message;
    // 课程ID
    private Long courseId;

    // 成绩分数
    private Double scoreValue;
    private Double scoreModify;
    // 考试类型：initial初试, makeup补考
    private String examType;

    // 成绩审核状态：pending, approved, rejected
    private String auditStatus;
    @TableField(exist = false) // 表示非数据库字段
    private String courseName; // 新增的课程名称字段
    @TableField(exist = false) // 表示非数据库字段
    private String studentName; // 新增的课程名称字段
    @TableField(exist = false) // 表示非数据库字段
    private String totalHours;
    @TableField(exist = false) // 表示非数据库字段
    private String credit;
    @TableField(exist = false)
    private String semester;
    @TableField(exist = false)
    private String courseNameEn;
    private String courseCode;
    // 审核意见
    private String auditZhuren;
    private String auditYuanzhang;
    private String auditRemark;
    @TableField(exist = false) // 表示非数据库字段

    private String class1;
    @TableField(exist = false) // 表示非数据库字段
    private String grade;

    // 录入或审核成绩教师姓名
    private String teacherName;

    private String scoreType; // "exam" 或 "homework"

    /**
     * 下面这四个字段是公共字段。使用@TableField字段来指定
     * Mybatis-Plus提供了公共字段填充功能，需要在公共字段上加入注解@TableField，指定自动填充策略。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}