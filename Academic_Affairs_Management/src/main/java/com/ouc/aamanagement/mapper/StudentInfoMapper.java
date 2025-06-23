package com.ouc.aamanagement.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.entity.StudentInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {
    // 可在此添加自定义数据库操作方法

    @Select("<script>" +
            "SELECT si.*, g.is_passed " +
            "FROM student_info si " +
            "LEFT JOIN graduation g ON si.student_number = g.student_number " +
            "<where>" +
            "   <if test='ew != null'>${ew.sqlSegment}</if>" +
            "</where>" +
            "</script>")
    Page<StudentInfoDTO> selectWithGraduation(Page<StudentInfoDTO> page, @Param("ew") QueryWrapper<StudentInfo> wrapper);
}
