package com.ouc.aamanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.ouc.aamanagement.entity.Activity;
import com.ouc.aamanagement.entity.StudentQueryResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
    @Select("SELECT " +
            "si.student_number as studentNumber, " +
            "si.name, " +
            "s.value as scoreValue " +
            "FROM student_info si " +
            "JOIN activity a ON si.grade = a.grade_name AND si.class1 = a.class_name " +
            "LEFT JOIN score1 s ON s.student_number = si.student_number AND s.acticity_id = a.id " +  // 增加activity_id关联条件
            "WHERE a.grade_name = #{gradeName} " +
            "AND a.class_name = #{className} " +
            "AND a.course_code = #{courseCode} " +
            "AND a.teacher_id = #{teacherId} " +
            "AND a.id = #{activityId}")  // 新增activityId查询条件
    List<StudentQueryResult> findStudentsByActivityConditions(
            @Param("gradeName") String gradeName,
            @Param("className") String className,
            @Param("courseCode") String courseCode,
            @Param("teacherId") String teacherId,
            @Param("activityId") String activityId);  // 新增参数
}
