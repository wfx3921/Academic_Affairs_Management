package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.Course;
import com.ouc.aamanagement.entity.Schedule;
import com.ouc.aamanagement.entity.ScheduleDTO;
import com.ouc.aamanagement.entity.ScheduleVO;
import com.ouc.aamanagement.mapper.CourseMapper;
import com.ouc.aamanagement.mapper.ScheduleMapper;
import com.ouc.aamanagement.service.CourseService;
import com.ouc.aamanagement.service.ScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 排课管理 Controller
 * 基路径: /api/schedule
 */
@CrossOrigin
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleMapper scheduleMapper;
    @Autowired
    CourseService courseService;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ScheduleService scheduleService;
    @GetMapping("/teacher/{teacherId}")
    public R<List<Course>> getCoursesByTeacher(@PathVariable String teacherId) {
        List<Course> courses;

        if ("all".equals(teacherId)) {
            // 教务老师模式：查询所有课程
            courses = courseService.list();
        } else {
            // 任课老师模式：查询指定老师的课程
            Long teacherIdLong = Long.parseLong(teacherId);
            courses = courseService.lambdaQuery()
                    .eq(Course::getTeacherId, teacherIdLong)
                    .list();
        }

        return R.success(courses);
    }
    // 获取课程选项（适配前端Select）
    @GetMapping("/courses")
    public R<List<Map<String, Object>>> getCourses() {
        List<Course> courses = courseMapper.selectList(
                new QueryWrapper<Course>()
                        .select("id", "course_name as courseName", "course_code as courseCode")        );
        List<Map<String, Object>> options = courses.stream()
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", c.getId());
                    map.put("label", c.getCourseName());
                    map.put("code", c.getCourseCode());
                    return map;
                }).collect(Collectors.toList());

        return R.success(options);
    }

    @PostMapping("/add")
    public R<Object> addSchedule(@RequestBody ScheduleDTO dto,
                                 @RequestParam(required = false) Boolean forceOverride) {
        try {
            // 参数校验
            if(dto.getGradeId() == null || dto.getClassId() == null
                    || dto.getTerm() == null || dto.getCourseId() == null){
                return R.error("必要参数缺失");
            }
            // 校验季度范围
            if(dto.getTerm() < 1 || dto.getTerm() > 6) {
                return R.error("季度参数不合法");
            }

            Schedule entity = new Schedule();
            // 设置所有字段
            entity.setGradeId(String.valueOf(Long.parseLong(dto.getGradeId())));
            entity.setClassId(Long.parseLong(dto.getClassId()));
            entity.setTerm(dto.getTerm());
            entity.setStartTime(dto.getStartWeek());
            entity.setEndTime(dto.getEndWeek());
            entity.setCourseId(Long.parseLong(dto.getCourseId()));
            entity.setJieStart(dto.getJieStart());
            entity.setJieEnd(dto.getJieEnd());
            entity.setWeekDay(dto.getWeekDay());
            entity.setLocation(dto.getLocation());
            entity.setTeacherName(dto.getTeacherName());

            // 检查冲突
            List<String> conflicts = scheduleService.checkScheduleConflict(entity,null);

            if (!conflicts.isEmpty()) {
                if (Boolean.TRUE.equals(forceOverride)) {
                    // 强制覆盖 - 先删除冲突的排课
                    scheduleMapper.delete(new LambdaQueryWrapper<Schedule>()
                            .eq(Schedule::getGradeId, entity.getGradeId())
                            .eq(Schedule::getClassId, entity.getClassId())
                            .eq(Schedule::getTerm, entity.getTerm())
                            .eq(Schedule::getWeekDay, entity.getWeekDay())
                            .apply("jie_start <= {0} AND jie_end >= {1}",
                                    entity.getJieEnd(), entity.getJieStart()));
                } else {
                    // 返回冲突信息，让前端决定是否覆盖
                    Map<String, Object> result = new HashMap<>();
                    result.put("conflicts", conflicts);
                    result.put("canOverride", true);
                    return R.success(result);
                }
            }

            // 保存数据
            return scheduleMapper.insert(entity) > 0
                    ? R.success("排课成功")
                    : R.error("排课失败");
        } catch (Exception e) {
            return R.error("系统异常：" + e.getMessage());
        }
    }

    @PutMapping("/update")
    public R<Object> updateSchedule(@RequestBody ScheduleDTO dto,
                                    @RequestParam(required = false) Boolean forceOverride) {
        try {
            // 参数校验
            if (dto.getId() == null) {
                return R.error("ID不能为空");
            }
            if (dto.getStartWeek() > dto.getEndWeek()) {
                return R.error("结束周不能小于开始周");
            }

            // 构建实体
            Schedule entity = new Schedule();
            entity.setId(dto.getId());
            entity.setGradeId(dto.getGradeId());
            entity.setClassId(Long.valueOf(dto.getClassId()));
            entity.setTerm(dto.getTerm());
            entity.setStartTime(dto.getStartWeek());
            entity.setEndTime(dto.getEndWeek());
            entity.setCourseId(Long.parseLong(dto.getCourseId()));
            entity.setJieStart(dto.getJieStart());
            entity.setJieEnd(dto.getJieEnd());
            entity.setWeekDay(dto.getWeekDay());
            entity.setLocation(dto.getLocation());
            entity.setTeacherName(dto.getTeacherName());

            // 检查冲突（排除自身）
            List<String> conflicts = scheduleService.checkScheduleConflict(entity, dto.getId());

            if (!conflicts.isEmpty()) {
                if (Boolean.TRUE.equals(forceOverride)) {
                    // 强制覆盖 - 先删除冲突的排课（排除自身）
                    scheduleMapper.delete(new LambdaQueryWrapper<Schedule>()
                            .eq(Schedule::getGradeId, entity.getGradeId())
                            .eq(Schedule::getClassId, entity.getClassId())
                            .eq(Schedule::getTerm, entity.getTerm())
                            .eq(Schedule::getWeekDay, entity.getWeekDay())
                            .ne(Schedule::getId, entity.getId()) // 排除自身
                            .apply("jie_start <= {0} AND jie_end >= {1}",
                                    entity.getJieEnd(), entity.getJieStart()));
                } else {
                    // 返回冲突信息
                    Map<String, Object> result = new HashMap<>();
                    result.put("conflicts", conflicts);
                    result.put("canOverride", true);
                    return R.success(result);
                }
            }

            // 更新数据
            return scheduleMapper.updateById(entity) > 0
                    ? R.success("更新成功")
                    : R.error("更新失败");
        } catch (Exception e) {
            return R.error("系统异常：" + e.getMessage());
        }
    }

    // 删除课程
    @DeleteMapping("/delete/{id}")
    public R<String> deleteSchedule(@PathVariable Long id) {
        try {
            return scheduleMapper.deleteById(id) > 0
                    ? R.success("删除成功")
                    : R.error("删除失败");
        } catch (Exception e) {
            return R.error("系统异常：" + e.getMessage());
        }
    }
    @GetMapping("/byCondition")
    public R<List<ScheduleVO>> getByCondition(
            @RequestParam Long gradeId,
            @RequestParam Long classId,
            @RequestParam Integer term,
            @RequestParam(required = false) Integer weekDay) {

        try {
            List<Schedule> schedules = scheduleMapper.selectList(
                    new QueryWrapper<Schedule>()
                            .eq("grade_id", gradeId)
                            .eq("class_id", classId)
                            .eq("term", term)
                            .eq(weekDay != null, "week_day", weekDay)
                            .orderByAsc("week_day", "jie_start")
            );

            List<ScheduleVO> voList = schedules.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            return R.success(voList);
        } catch (Exception e) {
            return R.error("查询失败：" + e.getMessage());
        }
    }



    @GetMapping("/getScheduleById")
    public R<List<Map<String, Object>>> getScheduleById(
            @RequestParam String studentNumber,
            @RequestParam(required = false, defaultValue = "2024-2025") String term,
            @RequestParam(required = false) String jidu) {
        List<Map<String, Object>> result = scheduleMapper.getScheduleByStudentNumber(studentNumber, term,jidu);
        return R.success(result);
    }
    // 实体转VO（保持最小转换逻辑）
    private ScheduleVO convertToVO(Schedule schedule) {
        ScheduleVO vo = new ScheduleVO();
        Course course = courseMapper.selectById(schedule.getCourseId());

        vo.setId(schedule.getId());
        vo.setCourseName(course.getCourseName());
        vo.setTimeRange(schedule.getJieStart() + "-" + schedule.getJieEnd() + "节");
        vo.setWeekRange(schedule.getStartTime() + "-" + schedule.getEndTime() + "周");
        vo.setLocation(schedule.getLocation());
        vo.setTeacherName(schedule.getTeacherName());
        vo.setDayOfWeek("星期" + schedule.getWeekDay());
        vo.setJieStart(schedule.getJieStart());
        vo.setJieEnd(schedule.getJieEnd());
        vo.setStartTime(schedule.getStartTime());
        vo.setEndTime(schedule.getEndTime());
        vo.setCourseId(String.valueOf(schedule.getCourseId()));

        return vo;
    }
}