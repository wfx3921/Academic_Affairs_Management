package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.entity.Activity;
import com.ouc.aamanagement.entity.StudentQueryResult;

import java.util.List;


public interface ActivityService extends IService<Activity> {
    List<StudentQueryResult> getStudentsByConditions(
            String gradeName,
            String className,
            String courseCode,
            String teacherId,
            String activityId);
}

