package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.Score;
import com.ouc.aamanagement.entity.ScoreBatchDTO;
import com.ouc.aamanagement.entity.ScoreDTO;

import java.util.List;

public interface ScoreService extends IService<Score> {
    // 保持原有方法声明
    Page<ScoreDTO> getScoreWithStudentPage(Page<Score> page, QueryWrapper<Score> queryWrapper);
    List<Score> listByStudentNumber(String studentNumber);
    public R<String> calculateAndSaveFinalScores(
            String gradeName, String className,
            String courseCode, String teacherId,String teacherName);
}