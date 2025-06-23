package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.Course;
import com.ouc.aamanagement.entity.Schedule;
import com.ouc.aamanagement.mapper.CourseMapper;
import com.ouc.aamanagement.mapper.ScheduleMapper;
import com.ouc.aamanagement.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 排课管理 Service 实现类
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {
    private final ScheduleMapper scheduleMapper;
    private final CourseMapper courseMapper;
    public List<String> checkScheduleConflict(Schedule schedule, Long excludeId) {
        List<String> conflicts = new ArrayList<>();
        if (schedule == null) {
            return conflicts;
        }

        // 查询同一班级、年级、学期的所有排课（排除自身）
        LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<Schedule>()
                .eq(Schedule::getGradeId, schedule.getGradeId())
                .eq(Schedule::getClassId, schedule.getClassId())
                .eq(Schedule::getTerm, schedule.getTerm());
        if (excludeId != null) {
            queryWrapper.ne(Schedule::getId, excludeId);
        }
        List<Schedule> existingSchedules = scheduleMapper.selectList(queryWrapper);

        // 如果没有任何排课，直接返回空列表
        if (existingSchedules.isEmpty()) {
            return conflicts;
        }

        // 收集课程ID并批量查询课程名称
        Set<Long> courseIds = existingSchedules.stream()
                .map(Schedule::getCourseId)
                .filter(Objects::nonNull) // 过滤掉 null 的 courseId
                .collect(Collectors.toSet());

        // 如果 courseIds 为空，直接跳过查询
        Map<Long, Course> courseMap = courseIds.isEmpty()
                ? Collections.emptyMap()
                : courseMapper.batchGetCourseNames(courseIds);

        // 检查冲突
        for (Schedule existing : existingSchedules) {
            Long courseId = existing.getCourseId();
            String courseName = courseMap.containsKey(courseId)
                    ? courseMap.get(courseId).getCourseName()
                    : "未知课程";

            // 检查时间冲突（同一周几、节次范围重叠）
            if (existing.getWeekDay().equals(schedule.getWeekDay()) &&
                    !(existing.getJieEnd() < schedule.getJieStart() ||
                            existing.getJieStart() > schedule.getJieEnd())) {
                conflicts.add(String.format("时间冲突：与课程[%s]时间重叠", courseName));
            }

            // 检查教室冲突
            if (existing.getLocation().equals(schedule.getLocation()) &&
                    existing.getWeekDay().equals(schedule.getWeekDay()) &&
                    !(existing.getJieEnd() < schedule.getJieStart() ||
                            existing.getJieStart() > schedule.getJieEnd())) {
                conflicts.add(String.format("教室冲突：%s已被课程[%s]占用",
                        schedule.getLocation(), courseName));
            }

            // 检查教师冲突
            if (existing.getTeacherName().equals(schedule.getTeacherName()) &&
                    existing.getWeekDay().equals(schedule.getWeekDay()) &&
                    !(existing.getJieEnd() < schedule.getJieStart() ||
                            existing.getJieStart() > schedule.getJieEnd())) {
                conflicts.add(String.format("教师冲突：%s老师在同一时间已有课程[%s]",
                        schedule.getTeacherName(), courseName));
            }
        }

        return conflicts;
    }
}
