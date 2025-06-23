package com.ouc.aamanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ouc.aamanagement.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 排课管理 Mapper
 */
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {
    @Select("select course.course_name,course.course_name_en,course.course_type,schedule.teacher_name," +
            "schedule.week_day,schedule.location,schedule.jie_start,schedule.jie_end,schedule.start_time,schedule.end_time " +
            "from student_info,schedule,class,course where student_info.class1=class.class_name " +
            "and class.id=schedule.class_id and schedule.course_id=course.id " +
            "and student_info.student_number=#{studentNumber}  and schedule.term=#{jidu}")
    List<Map<String, Object>> getScheduleByStudentNumber(
            @Param("studentNumber") String studentNumber,
            @Param("term") String term,
            @Param("jidu") String jidu
    );
}
