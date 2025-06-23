package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.Grade;
import com.ouc.aamanagement.mapper.GradeMapper;
import com.ouc.aamanagement.service.GradeService;
import org.springframework.stereotype.Service;

/**
 * 年级表Service实现类
 */
@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {
}
