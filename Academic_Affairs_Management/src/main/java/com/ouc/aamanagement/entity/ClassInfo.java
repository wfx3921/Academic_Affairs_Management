package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("class")
public class ClassInfo {
    @TableId
    private Long id;

    // 班级名称
    private String className;

    // 所属年级ID
    private Long gradeId;

    // 所属专业ID
    private String majorId;

    // 班级总学生数
    private Integer classStudentCount;

    // 班主任姓名
    private String headTeacher;

    // 班级代码
    private String classCode;

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
