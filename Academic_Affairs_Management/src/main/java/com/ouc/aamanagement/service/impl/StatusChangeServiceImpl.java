package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.StatusChange;
import com.ouc.aamanagement.mapper.StatusChangeMapper;
import com.ouc.aamanagement.service.StatusChangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StatusChangeServiceImpl extends ServiceImpl <StatusChangeMapper, StatusChange> implements StatusChangeService {

}
