package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.Course;
import com.ouc.aamanagement.mapper.CourseMapper;
import com.ouc.aamanagement.mapper.UserMapper;
import com.ouc.aamanagement.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 课程管理 Service 实现类
 */
@Service
@Transactional
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    public CourseServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<Course> listByCourseCodes(Collection<String> courseCodes) {
        if (CollectionUtils.isEmpty(courseCodes)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Course::getCourseCode, courseCodes);
        return this.list(queryWrapper);
    }

    private final UserMapper userMapper;

    @Override
    @Transactional
    public boolean saveBatch(List<Course> courses) {
        // 先处理教师ID映射
        for (Course course : courses) {
            if (StringUtils.isNotBlank(course.getTeacherName())) {
                Long teacherId = userMapper.findIdByUserName(course.getTeacherName());
                course.setTeacherId(teacherId);
            }
        }
        return super.saveBatch(courses);
    }
}
