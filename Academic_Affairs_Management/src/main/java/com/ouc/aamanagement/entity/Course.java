package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("course")
public class Course {
    @TableId
    private Long id;

    // 课程名称（中文）
    private String courseName;

    // 课程名称（英文）
    private String courseNameEn;

    // 课程代码
    private String courseCode;

    // 学分
    private Double credit;

    // 所属专业（直接存储专业名称）
    private String major;

    // 任课教师姓名
    private String teacherName;
    private Long teacherId;

    // 课程类型：必修、选修、实践
    private String courseType;

    // 总课时数
    private Integer totalHours;

    // 选课学年，如2024-2025
    private String academicYear;

    // 学期，如春季、秋季
    private String semester;

    // 适用年级，如2025级
    private String grade;

    // 描述或备注
    private String description;

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
