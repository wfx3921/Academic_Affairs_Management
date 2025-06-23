package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.GraduationOpenTime;
import com.ouc.aamanagement.mapper.GraduationOpenTimeMapper;
import com.ouc.aamanagement.service.GraduationOpenTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraduationOpenTimeServiceImpl extends ServiceImpl<GraduationOpenTimeMapper, GraduationOpenTime> implements GraduationOpenTimeService {
    @Autowired
    private GraduationOpenTimeMapper graduationOpenTimeMapper;

}
