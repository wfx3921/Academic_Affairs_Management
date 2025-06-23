package com.ouc.aamanagement.entity;

import lombok.Data;

@Data
public class ScheduleDTO {
    private Long id;
    private String gradeId;     // 年级ID
    private String classId;     // 班级ID
    private Integer term;       // 季度(1-6)
    private String courseId;    // 课程ID
    private Integer startWeek;  // 开始周
    private Integer endWeek;    // 结束周
    private Integer jieStart;   // 开始节
    private Integer jieEnd;     // 结束节
    private Integer weekDay;    // 星期几
    private String location;    // 地点
    private String teacherName; // 教师姓名
    private String courseName;  // 新增课程名字段
}