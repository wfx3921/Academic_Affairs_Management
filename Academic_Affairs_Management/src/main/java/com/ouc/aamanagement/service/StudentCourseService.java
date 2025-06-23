package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.entity.StudentCourse;

import java.util.List;
import java.util.Map;

public interface StudentCourseService extends IService<StudentCourse> {

    List<Map<String, Object>> getScheduleWithCourseName(String studentNumber, String term);
}
