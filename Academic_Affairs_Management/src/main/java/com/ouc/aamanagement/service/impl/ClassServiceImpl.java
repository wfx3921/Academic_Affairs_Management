package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.ClassInfo;
import com.ouc.aamanagement.mapper.ClassMapper;
import com.ouc.aamanagement.service.ClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 班级管理 Service 实现类
 */
@Service
@Transactional
public class ClassServiceImpl extends ServiceImpl<ClassMapper, ClassInfo> implements ClassService {
    @Override
    public ClassInfo getByGradeId(Long gradeId) {
        return lambdaQuery()
                .eq(ClassInfo::getGradeId, gradeId)  // 使用实体类属性名
                .one();  // 查询单条记录（确保gradeid唯一）
}}
