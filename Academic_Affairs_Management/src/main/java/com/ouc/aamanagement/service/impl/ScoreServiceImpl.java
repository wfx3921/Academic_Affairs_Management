package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.*;
import com.ouc.aamanagement.mapper.ActivityMapper;
import com.ouc.aamanagement.mapper.Score1Mapper;
import com.ouc.aamanagement.mapper.ScoreMapper;
import com.ouc.aamanagement.service.ScoreService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 成绩管理 Service 实现类
 */
@Service
@Transactional

public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    @Autowired
    private  ActivityMapper activityMapper;
    @Autowired
    private  Score1Mapper score1Mapper;
    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public Page<ScoreDTO> getScoreWithStudentPage(Page<Score> page, QueryWrapper<Score> queryWrapper) {
        // 转换分页对象
        Page<ScoreDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize());
        return baseMapper.selectScoreWithStudent(dtoPage, queryWrapper);
    }
    @Override
    public List<Score> listByStudentNumber(String studentNumber) {
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getStudentNumber, studentNumber);
        return this.list(queryWrapper);
    }
    @Override
    @Transactional
    public R<String> calculateAndSaveFinalScores(
            String gradeName, String className,
            String courseCode, String teacherId, String teacherName) {

        // 1. 查询本课程所有活动
        List<Activity> activities = activityMapper.selectList(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getGradeName, gradeName)
                .eq(Activity::getClassName, className)
                .eq(Activity::getCourseCode, courseCode)
                .eq(Activity::getTeacherId, teacherId));

        if (activities.isEmpty()) {
            return R.error("未找到相关活动");
        }

        // 2. 验证权重总和是否为100%
        double totalWeight = activities.stream()
                .mapToDouble(Activity::getWeight)
                .sum();
        if (Math.abs(totalWeight - 100) > 0.001) {
            return R.error("活动权重总和必须为100%，当前为: " + totalWeight + "%");
        }

        // 3. 获取所有学生成绩并计算总成绩
        Map<String, Double> studentFinalScores = new HashMap<>();
        Map<String, List<ActivityScoreDetail>> scoreDetails = new HashMap<>();

        for (Activity activity : activities) {
            List<Score1> scores = score1Mapper.selectList(new LambdaQueryWrapper<Score1>()
                    .eq(Score1::getActicityId, activity.getId()));

            for (Score1 score : scores) {
                double weightedScore = Double.parseDouble(score.getValue()) * activity.getWeight() / 100;

                // 计算总成绩
                studentFinalScores.merge(
                        score.getStudentNumber(),
                        weightedScore,
                        Double::sum
                );

                // 保存明细（可选）
                scoreDetails.computeIfAbsent(score.getStudentNumber(), k -> new ArrayList<>())
                        .add(new ActivityScoreDetail(
                                activity.getName(),
                                score.getValue(),
                                activity.getWeight()
                        ));
            }
        }

        // 4. 保存或更新总成绩
        int successCount = 0;
        for (Map.Entry<String, Double> entry : studentFinalScores.entrySet()) {
            String studentNumber = entry.getKey();
            Double finalScore = entry.getValue();

            // 构建查询条件
            LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Score::getStudentNumber, studentNumber)
                    .eq(Score::getCourseCode, courseCode)
                    .eq(Score::getScoreType, "exam")
                    .eq(Score::getExamType, "initial");

            // 创建成绩对象
            Score score = new Score();
            score.setStudentNumber(studentNumber);
            score.setCourseCode(courseCode);
            score.setScoreValue(Double.parseDouble(String.format("%.2f", finalScore)));
            score.setScoreType("exam");
            score.setExamType("initial");
            score.setTeacherName(teacherName);

            // 检查是否已存在
            Score existingScore = scoreMapper.selectOne(queryWrapper);

            if (existingScore != null) {
                // 更新已有记录
                score.setId(existingScore.getId());
                scoreMapper.updateById(score);
            } else {
                // 新增记录
                scoreMapper.insert(score);
            }
            successCount++;
        }

        return R.success("成功处理" + successCount + "名学生的总成绩");
    }
    @Data
    @AllArgsConstructor
    private static class ActivityScoreDetail {
        private String activityName;
        private String scoreValue;
        private Double weight;
    }

}