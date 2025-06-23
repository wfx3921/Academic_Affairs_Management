package com.ouc.aamanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ouc.aamanagement.entity.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentCourseMapper extends BaseMapper<StudentCourse> {

    @Select("SELECT " +
            "sc.id AS sc_id, " +
            "sc.student_number, " +
            "sc.term, " +
            "c.id AS course_id, " +
            "c.course_name, " +
            "c.course_code, " +
            "c.teacher_name, " +
            "c.day, "+
            "c.times, "+
            "c.credit " +
            "FROM student_course sc " +
            "JOIN course c ON sc.course_id = c.id " +
            "WHERE sc.student_number = #{studentNumber} AND sc.term = #{term}")
    List<Map<String, Object>> getScheduleWithCourseName(@Param("studentNumber") String studentNumber,
                                                        @Param("term") String term);

}
