package com.ouc.aamanagement.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 毕业信息 Mapper
 */
@Mapper
public interface GraduationMapper extends BaseMapper<Graduation> {

    @Select("SELECT s.*, si.name AS student_name " +
            "FROM graduation s " +
            "LEFT JOIN student_info si ON s.student_number = si.student_number " +
            "${ew.customSqlSegment}")
    Page<GranDTO> selectGranWithStudent(Page<GranDTO> page, @Param("ew") QueryWrapper<Graduation> wrapper);


    @Select("<script>" +
            "SELECT g.*, s.name AS student_name, s.graduation_date AS graduation_date, " +
            "s.grade AS grade_name, s.class1 AS class_name " +  // 新增返回字段
            "FROM graduation g " +
            "LEFT JOIN student_info s ON g.student_number = s.student_number " +
            "WHERE g.special_type != 'none' " +
            "<if test='studentNumber != null and studentNumber != \"\"'> " +
            "   AND g.student_number = #{studentNumber} " +
            "</if> " +
            "<if test='studentName != null and studentName != \"\"'> " +
            "   AND s.name LIKE CONCAT('%', #{studentName}, '%') " +
            "</if>" +
            "<if test='gradeName != null and gradeName != \"\"'> " +  // 新增年级条件
            "   AND s.grade = #{gradeName} " +
            "</if>" +
            "<if test='className != null and className != \"\"'> " +  // 新增班级条件
            "   AND s.class1 = #{className} " +
            "</if>" +
            "</script>")
    Page<SpecialStudentVO> selectSpecialPage(
            Page<Graduation> page,
            @Param("studentNumber") String studentNumber,
            @Param("studentName") String studentName,
            @Param("gradeName") String gradeName,    // 新增参数
            @Param("className") String className     // 新增参数
    );
    @Select("<script>" +
            "SELECT g.*, s.name AS student_name, s.graduation_date AS graduation_date, " +
            "s.grade AS grade_name, s.class1 AS class_name, " +
            "s.student_status AS student_status " +  // 新增status字段
            "FROM graduation g " +
            "LEFT JOIN student_info s ON g.student_number = s.student_number " +
            "WHERE 1=1 " +
            "<if test='studentNumber != null and studentNumber != \"\"'> " +
            "   AND g.student_number = #{studentNumber} " +
            "</if> " +
            "<if test='studentName != null and studentName != \"\"'> " +
            "   AND s.name LIKE CONCAT('%', #{studentName}, '%') " +
            "</if>" +
            "<if test='gradeName != null and gradeName != \"\"'> " +
            "   AND s.grade = #{gradeName} " +
            "</if>" +
            "<if test='className != null and className != \"\"'> " +
            "   AND s.class1 = #{className} " +
            "</if>" +
            "</script>")
    Page<SpecialStudentVO> selectHistoryStudentsPage(
            Page<Graduation> page,
            @Param("studentNumber") String studentNumber,
            @Param("studentName") String studentName,
            @Param("gradeName") String gradeName,
            @Param("className") String className
    );
    @Select("<script>" +
            "SELECT g.*, s.name AS student_name, s.student_status AS student_status " +
            "FROM graduation g " +
            "LEFT JOIN student_info s ON g.student_number = s.student_number " +
            "<where>" +
            "   <if test='ew.sqlSegment != null'>${ew.sqlSegment}</if>" +
            "</where>" +
            "</script>")
    List<SpecialStudentVO> selectStudentsWithInfo(@Param(Constants.WRAPPER) QueryWrapper<Graduation> queryWrapper);
}
