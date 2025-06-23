package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.Score1;
import com.ouc.aamanagement.mapper.Score1Mapper;


public interface Score1Service extends IService<Score1> {
    boolean saveOrUpdateScore(String studentNumber, String score, String activityId);
}