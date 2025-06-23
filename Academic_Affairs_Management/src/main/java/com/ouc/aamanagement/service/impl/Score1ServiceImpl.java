package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.Score1;
import com.ouc.aamanagement.mapper.Score1Mapper;
import com.ouc.aamanagement.service.Score1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
public class Score1ServiceImpl extends ServiceImpl<Score1Mapper, Score1> implements Score1Service {

    @Autowired  // 添加这个注解
    Score1Mapper score1Mapper;  // 保持原有声明

    @Override
    @Transactional
    public boolean saveOrUpdateScore(String studentNumber, String score, String activityId) {
        // 检查是否已存在记录
        Score1 existing = score1Mapper.selectOne(new LambdaQueryWrapper<Score1>()
                .eq(Score1::getStudentNumber, studentNumber)
                .eq(Score1::getActicityId, activityId));

        Score1 score1 = new Score1();
        score1.setStudentNumber(studentNumber);
        score1.setValue(score);
        score1.setActicityId(activityId);

        if (existing != null) {
            // 更新
            score1.setId(existing.getId());
            return score1Mapper.updateById(score1) > 0;
        } else {
            // 新增
            return score1Mapper.insert(score1) > 0;
        }
    }
}