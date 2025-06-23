package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("student_awards_punishments")
public class StudentAwardsPunishments {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    // 姓名
    @TableField("name")
    private String name;

    // 学号
    @TableField("student_number")
    private String studentNumber;

    // 年级
    @TableField("grade")
    private String grade;

    // 专业
    @TableField("major")
    private String major;

    // 奖惩类型（奖、惩）
    @TableField("type")
    private String type;

    // 奖惩级别
    @TableField("level")
    private String level;

    // 描述
    @TableField("description")
    private String description;

    // 记录日期
    @TableField("record_date")
    private Date recordDate;

    // 开始时间
    @TableField("start_date")
    private Date startDate;

    // 结束时间
    @TableField("end_date")
    private Date endDate;

    // 记录状态（有效、撤销）
    @TableField("status")
    private String status;

    // 记录人
    @TableField("created_by")
    private String createdBy;

    // 记录时间（插入时自动填充）
    @TableField(value = "created_time")
    private Date createdTime;

    // 更新人
    @TableField("updated_by")
    private String updatedBy;

    // 更新时间（插入和更新时自动填充）
    @TableField(value = "updated_time")
    private Date updatedTime;
}
