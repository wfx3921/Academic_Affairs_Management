package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.entity.StudentInfoDTO;
import com.ouc.aamanagement.mapper.GraduationMapper;
import com.ouc.aamanagement.mapper.StudentInfoMapper;
import com.ouc.aamanagement.service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

// GraduationServiceImpl.java
@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo>
        implements StudentInfoService {
    @Override
    public boolean updateDiplomaPathByApplicationId(Long applicationId, String filePath) {
        StudentInfo info = getOne(
                new QueryWrapper<StudentInfo>().eq("application_id", applicationId)
        );
        if (info != null) {
            info.setHighSchoolDiploma(filePath);
            return updateById(info);
        }
        return false;
    }
    @Override
    public List<StudentInfo> listByStudentNumbers(Collection<String> studentNumbers) {
        if (CollectionUtils.isEmpty(studentNumbers)) return Collections.emptyList();
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(StudentInfo::getStudentNumber, studentNumbers);
        return this.list(wrapper);
    }
    @Override
    public List<StudentInfo> listByApplicationIds(Collection<Long> applicationIds) {
        if (CollectionUtils.isEmpty(applicationIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(StudentInfo::getApplicationId, applicationIds);
        return this.list(wrapper);
    }
    @Override
    public Page<StudentInfoDTO> getStudentWithGraduation(
            int page, int size,
            String studentNumber,
            String gradeName,  // 新增参数
            String className   // 新增参数
    ) {
        QueryWrapper<StudentInfo> wrapper = new QueryWrapper<>();
        wrapper.apply("si.student_number IN (SELECT student_number FROM graduation)")
                .eq(StringUtils.isNotBlank(studentNumber), "si.student_number", studentNumber)
                .eq(StringUtils.isNotBlank(gradeName), "si.grade", gradeName)      // 新增年级条件
                .eq(StringUtils.isNotBlank(className), "si.class1", className);    // 注意字段名是class1

        return baseMapper.selectWithGraduation(new Page<>(page, size), wrapper);
    }
}