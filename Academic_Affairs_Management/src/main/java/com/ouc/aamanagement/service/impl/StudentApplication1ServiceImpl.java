package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.StudentApplication1;
import com.ouc.aamanagement.mapper.StudentApplication1Mapper;
import com.ouc.aamanagement.service.StudentApplication1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentApplication1ServiceImpl extends ServiceImpl<StudentApplication1Mapper, StudentApplication1>
        implements StudentApplication1Service {
    // save() 方法由 ServiceImpl 自动提供，不需要手动实现

    @Autowired
    private StudentApplication1Mapper studentApplicationMapper;

    @Override
    public IPage<StudentApplication1> getStudentApplicationsPage(int page, int size) {
        Page<StudentApplication1> studentPage = new Page<>(page, size);
        return studentApplicationMapper.selectPage(studentPage, null);
    }

    @Override
    public StudentApplication1 getStudentApplicationById(Long id) {
        return studentApplicationMapper.selectById(id);
    }

    @Override
    public boolean saveStudentApplication(StudentApplication1 studentApplication) {
        return studentApplicationMapper.insert(studentApplication) > 0;
    }

    @Override
    public boolean updateStudentApplication(StudentApplication1 studentApplication) {
        return studentApplicationMapper.updateById(studentApplication) > 0;
    }

    @Override
    public boolean deleteStudentApplication(Long id) {
        return studentApplicationMapper.deleteById(id) > 0;
    }

    @Override
    public StudentApplication1 getById(Long applicationId) {
        return studentApplicationMapper.selectById(applicationId);
    }


}
