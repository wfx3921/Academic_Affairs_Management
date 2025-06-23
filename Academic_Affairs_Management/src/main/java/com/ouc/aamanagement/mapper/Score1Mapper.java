package com.ouc.aamanagement.mapper;


import com.ouc.aamanagement.entity.Score1;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface Score1Mapper extends BaseMapper<Score1> {
    @Select("SELECT * FROM score1 WHERE student_number = #{studentNumber} AND acticity_id = #{activityId}")
    Score1 selectByStudentAndActivity(
            @Param("studentNumber") String studentNumber,
            @Param("activityId") String activityId);
}