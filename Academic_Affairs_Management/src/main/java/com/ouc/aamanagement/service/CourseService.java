package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.entity.Course;

import java.util.Collection;
import java.util.List;

/**
 * 课程管理 Service 接口
 */
public interface CourseService extends IService<Course> {
    List<Course> listByCourseCodes(Collection<String> courseCodes);

    boolean saveBatch(List<Course> courses);
}
