package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.StudentAwardsPunishments;
import com.ouc.aamanagement.mapper.StudentAwardsPunishmentsMapper;
import com.ouc.aamanagement.service.StudentAwardsPunishmentsService;
import org.springframework.stereotype.Service;

@Service
public class StudentAwardsPunishmentsServiceImpl
        extends ServiceImpl<StudentAwardsPunishmentsMapper, StudentAwardsPunishments>
        implements StudentAwardsPunishmentsService {
    // 具体业务均在 Controller 中实现
}
