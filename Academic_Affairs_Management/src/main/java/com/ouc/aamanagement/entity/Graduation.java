package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("graduation")
public class Graduation {
    @TableId
    private Long id;

    // 学生学号，关联 student_info 表
    private String studentNumber;

    // 毕业审核状态：pending, approved, rejected
    private String graduationStatus;
    private String name;
    // 毕业去向/升学去向
    private String graduateDestination;

    // 就业情况说明
    private String employmentInfo;

    // 审核意见
    private String auditRemark;

    // 特殊毕业生类型：none, nontraditional, exempt, other
    private String specialType;

    // 特殊情况说明，如具体描述特殊毕业原因
    private String specialInfo;
    @TableField("is_passed")
    private Integer isPassed;
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
