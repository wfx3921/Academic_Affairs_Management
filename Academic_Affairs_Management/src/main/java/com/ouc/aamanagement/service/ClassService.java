package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.entity.ClassInfo;

/**
 * 班级管理 Service 接口
 */
public interface ClassService extends IService<ClassInfo> {
    ClassInfo getByGradeId(Long gradeId);
}
