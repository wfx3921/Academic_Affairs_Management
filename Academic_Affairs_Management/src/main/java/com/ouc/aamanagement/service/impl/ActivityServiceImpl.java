package com.ouc.aamanagement.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ouc.aamanagement.entity.Activity;
import com.ouc.aamanagement.entity.StudentQueryResult;
import com.ouc.aamanagement.mapper.ActivityMapper;
import com.ouc.aamanagement.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// ActivityServiceImpl.java
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity>
        implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public List<StudentQueryResult> getStudentsByConditions(
            String gradeName, String className,
            String courseCode, String teacherId,String activityId) {

        return activityMapper.findStudentsByActivityConditions(
                gradeName, className, courseCode, teacherId,activityId);
    }
    
}