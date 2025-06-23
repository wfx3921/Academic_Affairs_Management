package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("schedule")
public class Schedule {
    @TableId
    private Long id;

    // 课程ID
    private Long courseId;
    @TableField("jie_start")
    private Integer jieStart;
    @TableField("jie_end")
    private Integer jieEnd;
    // 任课教师姓名
    private String teacherName;

    // 上课班级ID
    private Long classId;

    // 星期几（1-7）
    private Integer weekDay;

    // 上课开始时间
    private Integer startTime;

    // 上课结束时间
    private Integer endTime;

    // 上课地点
    private String location;
    private Integer term;       // 季度(1-6)
    private String gradeId;     // 年级ID

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
