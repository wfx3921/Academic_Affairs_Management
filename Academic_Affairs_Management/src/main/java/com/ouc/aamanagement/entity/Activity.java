package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String courseCode;
    private String gradeName;
    private String className;
    private String teacherId;
    private Double weight;
    private String name;
    private String type;
    private Integer permission;
    private LocalDateTime examTime;
}