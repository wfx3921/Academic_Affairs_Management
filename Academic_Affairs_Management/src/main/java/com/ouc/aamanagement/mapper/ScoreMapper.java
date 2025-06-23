package com.ouc.aamanagement.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.entity.Score;
import com.ouc.aamanagement.entity.ScoreDTO;
import com.ouc.aamanagement.entity.StudentScoreDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 成绩管理 Mapper
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

        @Select("SELECT s.*, si.name AS student_name " +
                "FROM score s " +
                "LEFT JOIN student_info si ON s.student_number = si.student_number " +
                "${ew.customSqlSegment}")
        Page<ScoreDTO> selectScoreWithStudent(Page<ScoreDTO> page, @Param("ew") QueryWrapper<Score> wrapper);
        @Select("SELECT " +
                "CONCAT(c.course_name, ' ', c.course_name_en) as courseName, " +
                "c.total_hours, " +
                "c.credit, " +
                "c.semester, " +
                "MAX(CASE WHEN s.exam_type = 'initial' THEN s.score_value END) as initialScore, " +
                "MAX(CASE WHEN s.exam_type = 'makeup' THEN s.score_value END) as makeupScore " +
                "FROM score s " +
                "JOIN course c ON s.course_code = c.course_code " +
                "WHERE s.student_number = #{studentNumber} " +
                "GROUP BY s.student_number, c.course_code, c.course_name, c.course_name_en, c.total_hours, c.credit, c.semester")
        List<StudentScoreDTO> selectScoresByStudent(String studentNumber);
}
