package com.ouc.aamanagement.entity;

import lombok.Data;

@Data
public class ScheduleVO {
    private Long id;               // 新增ID字段
    private String courseName;
    private String timeRange;      // 格式："3-5节"
    private String weekRange;      // 格式："1-16周"
    private String location;
    private String courseId;
    private String teacherName;
    private String dayOfWeek;      // 格式："星期3"
    private Integer jieStart;      // 新增开始节次
    private Integer jieEnd;        // 新增结束节次
    private Integer startTime;     // 新增开始周（原startWeek）
    private Integer endTime;       // 新增结束周（原endWeek）
}