package com.ouc.aamanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ouc.aamanagement.entity.Course;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;
import java.util.Set;

/**
 * 课程管理 Mapper
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    // 单个查询方法 - 参数名与Schedule表的course_id对应
    @Select("SELECT course_name FROM course WHERE id = #{courseId}")
    String getCourseNameById(@Param("courseId") Long courseId);

    // 批量查询方法 - 参数名保持courseIds，但实际对应Schedule表的course_id
    @Select({
            "<script>",
            "SELECT id, course_name FROM course WHERE id IN",
            "<foreach collection='courseIds' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    @MapKey("id")  // 使用id作为Map的key
    Map<Long, Course> batchGetCourseNames(@Param("courseIds") Set<Long> courseIds);
}
