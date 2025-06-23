package com.ouc.aamanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.entity.StudentInfo1;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentInfoMapper1 extends BaseMapper<StudentInfo1> {
    // 可在此添加自定义数据库操作方法
}
