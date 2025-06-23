package com.ouc.aamanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ouc.aamanagement.entity.ClassInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 班级管理 Mapper
 */
@Mapper
public interface ClassMapper extends BaseMapper<ClassInfo> {
}
