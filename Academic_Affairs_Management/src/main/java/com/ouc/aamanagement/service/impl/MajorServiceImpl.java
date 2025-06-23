package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.Major;
import com.ouc.aamanagement.mapper.MajorMapper;
import com.ouc.aamanagement.service.MajorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 专业管理 Service 实现类
 */
@Service
@Transactional
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {
}
