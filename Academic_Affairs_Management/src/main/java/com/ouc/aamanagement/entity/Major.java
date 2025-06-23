package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("major")
public class Major {
    @TableId
    private Long id;

    // 专业代码
    private String majorCode;

    // 专业名称
    private String majorName;

    // 所属学院或系部
    private String college;

    // 该专业总学生数
    private Integer totalStudents;

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
