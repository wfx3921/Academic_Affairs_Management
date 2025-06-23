package com.ouc.aamanagement.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.Activity;
import com.ouc.aamanagement.entity.Score;
import com.ouc.aamanagement.entity.StudentQueryResult;
import com.ouc.aamanagement.mapper.ActivityMapper;
import com.ouc.aamanagement.mapper.ScoreMapper;
import com.ouc.aamanagement.service.ActivityService;
import com.ouc.aamanagement.service.Score1Service;
import com.ouc.aamanagement.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping("/addHomework")
    public R<Activity> addHomework(@RequestBody Activity activity) {
        activity.setType("homework"); // 固定为作业类型
        return saveActivity(activity);
    }

    @PostMapping("/addExam")
    public R<Activity> addExam(@RequestBody Activity activity) {
        activity.setType("exam"); // 固定为考试类型
        return saveActivity(activity);
    }

    private R<Activity> saveActivity(Activity activity) {
        // 验证必要字段
        if (StringUtils.isBlank(activity.getCourseCode())) {
            return R.error("课程编码不能为空");
        }
        if (StringUtils.isBlank(activity.getName())) {
            return R.error("活动名称不能为空");
        }

        // 设置默认权重
        activity.setWeight(0.0);

        // 保存到数据库
        boolean saved = activityService.save(activity);
        return saved ? R.success(activity) : R.error("创建失败");
    }
    @GetMapping("/homeworks")
    public R<List<Activity>> getHomeworks(
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String gradeName,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String teacherId) {

        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "homework");

        if (StringUtils.isNotBlank(courseCode)) {
            queryWrapper.eq("course_code", courseCode);
        }
        if (StringUtils.isNotBlank(gradeName)) {
            queryWrapper.eq("grade_name", gradeName);
        }
        if (StringUtils.isNotBlank(className)) {
            queryWrapper.eq("class_name", className);
        }
        if (StringUtils.isNotBlank(teacherId)) {
            queryWrapper.eq("teacher_id", teacherId);
        }

        List<Activity> list = activityService.list(queryWrapper);
        return R.success(list);
    }
    @GetMapping("/exams")
    public R<List<Activity>> getExams(
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String gradeName,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String teacherId) {

        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "exam"); // 只查询考试类型

        if (StringUtils.isNotBlank(courseCode)) {
            queryWrapper.eq("course_code", courseCode);
        }
        if (StringUtils.isNotBlank(gradeName)) {
            queryWrapper.eq("grade_name", gradeName);
        }
        if (StringUtils.isNotBlank(className)) {
            queryWrapper.eq("class_name", className);
        }
        if (StringUtils.isNotBlank(teacherId)) {
            queryWrapper.eq("teacher_id", teacherId);
        }

        List<Activity> list = activityService.list(queryWrapper);
        return R.success(list);
    }
    @GetMapping("/permission")
    public R<Integer> getExamPermission(
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String gradeName,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String teacherId) {

        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(courseCode)) {
            queryWrapper.eq("course_code", courseCode);
        }
        if (StringUtils.isNotBlank(gradeName)) {
            queryWrapper.eq("grade_name", gradeName);
        }
        if (StringUtils.isNotBlank(className)) {
            queryWrapper.eq("class_name", className);
        }
        if (StringUtils.isNotBlank(teacherId)) {
            queryWrapper.eq("teacher_id", teacherId);
        }

        // 只查询第一条记录的permission字段
        queryWrapper.select("permission").last("LIMIT 1");

        Activity activity = activityService.getOne(queryWrapper);

        // 如果查询到记录则返回permission，否则返回0
        return R.success(activity != null ? activity.getPermission() : 9);
    }

    // ActivityController.java
    @PostMapping("/update-permissions")
    public R<String> updateActivityPermissions(
            @RequestParam String gradeName,
            @RequestParam String className,
            @RequestParam String courseCode,
            @RequestParam String teacherId) {

        UpdateWrapper<Activity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("grade_name", gradeName)
                .eq("class_name", className)
                .eq("course_code", courseCode)
                .eq("teacher_id", teacherId)
                .set("permission", 0);

        boolean updated = activityService.update(updateWrapper);
        return updated ? R.success("权限更新成功") : R.error("权限更新失败");
    }
    @DeleteMapping("/deleteHomework/{id}")
    public R<Void> deleteHomework(@PathVariable Integer id) {
        boolean removed = activityService.remove(
                new QueryWrapper<Activity>()
                        .eq("id", id)
                        .eq("type", "homework")
        );
        return R.success();
    }
    @DeleteMapping("/deleteExam/{id}")
    public R<Void> deleteExam(@PathVariable Integer id) {
        boolean removed = activityService.remove(
                new QueryWrapper<Activity>()
                        .eq("id", id)
                        .eq("type", "exam")
        );
        return R.success();
    }
    @GetMapping("/students")
    public R<List<StudentQueryResult>> getActivityStudents(
            @RequestParam String gradeName,
            @RequestParam String className,
            @RequestParam String courseCode,
            @RequestParam String teacherId,
            @RequestParam String activityId) {
        System.out.println(
                "gradeName=" + gradeName +
                        ", className=" + className +
                        ", courseCode=" + courseCode +
                        ", teacherId=" + teacherId +
                        ", activityId=" + activityId
        );
        List<StudentQueryResult> students = activityService.getStudentsByConditions(
                gradeName, className, courseCode, teacherId, activityId);

        return R.success(students);
    }
    @Autowired
    private Score1Service scoreService;
    @PostMapping("/homework")
    public R<String> saveScore(
            @RequestParam String studentNumber,
            @RequestParam String score,
            @RequestParam String activityId) {

        boolean success = scoreService.saveOrUpdateScore(studentNumber, score, activityId);
        return R.success("保存成功");
    }


    @Autowired
    private ActivityMapper activityMapper;

    // 获取课程所有活动
    @GetMapping("/weights")
    public R<List<Activity>> getActivitiesByCourse(
            @RequestParam String gradeName,
            @RequestParam String className,
            @RequestParam String courseCode) {

        List<Activity> activities = activityMapper.selectList(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getGradeName, gradeName)
                .eq(Activity::getClassName, className)
                .eq(Activity::getCourseCode, courseCode)
                .in(Activity::getType, Arrays.asList("homework", "exam")));

        return R.success(activities);
    }

    // 更新权重
    @PostMapping("/weights/update")
    @Transactional
    public R<String> updateWeights(@RequestBody List<Activity> activities) {
        // 修正1：将Double转为int计算总和
        int totalWeight = activities.stream()
                .mapToInt(a -> a.getWeight().intValue())
                .sum();

        if (totalWeight != 100) {
            return R.error("权重总和必须等于100%");
        }

        // 修正2：使用循环单个更新代替批量更新
        try {
            for (Activity activity : activities) {
                activityMapper.updateById(activity);
            }
            return R.success("权重设置成功");
        } catch (Exception e) {
            return R.error("权重设置失败: " + e.getMessage());
        }
    }
    @Autowired
    private ScoreMapper scoreMapper;
    @GetMapping("/checkFinalScore")
    public R<Score> checkFinalScore(
            @RequestParam String studentNumber,
            @RequestParam String courseCode) {

        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentNumber, studentNumber)
                .eq(Score::getCourseCode, courseCode)
                .eq(Score::getScoreType, "exam")  // 固定为总评成绩
                .eq(Score::getExamType, "initial"); // 固定为初始考试类型

        Score score = scoreMapper.selectOne(wrapper);
        return score != null ? R.success(score) : R.error("未找到记录");
    }
    @Autowired
    private ScoreService scoreService2;

    @PostMapping("/calculate-and-save")
    public R<String> calculateAndSaveFinalScores(
            @RequestParam String gradeName,
            @RequestParam String className,
            @RequestParam String courseCode,
            @RequestParam String teacherId,
            @RequestParam String teacherName
            ) {

        return scoreService2.calculateAndSaveFinalScores(
                gradeName, className, courseCode, teacherId,teacherName);
    }
}