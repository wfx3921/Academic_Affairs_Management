package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.StudentCourse;
import com.ouc.aamanagement.mapper.StudentCourseMapper;
import com.ouc.aamanagement.service.StudentCourseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StudentCourseServiceImpl extends ServiceImpl<StudentCourseMapper, StudentCourse>
        implements StudentCourseService {

    @Override
    public List<Map<String, Object>> getScheduleWithCourseName(String studentNumber, String term) {
        return baseMapper.getScheduleWithCourseName(studentNumber, term);
    }
}
