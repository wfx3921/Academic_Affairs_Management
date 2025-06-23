package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.mapper.StudentApplicationMapper;
import com.ouc.aamanagement.service.StudentApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentApplicationServiceImpl extends ServiceImpl<StudentApplicationMapper, StudentApplication>
        implements StudentApplicationService {
    // save() 方法由 ServiceImpl 自动提供，不需要手动实现

    @Autowired
    private StudentApplicationMapper studentApplicationMapper;

    @Override
    public IPage<StudentApplication> getStudentApplicationsPage(int page, int size) {
        Page<StudentApplication> studentPage = new Page<>(page, size);
        return studentApplicationMapper.selectPage(studentPage, null);
    }

    @Override
    public StudentApplication getStudentApplicationById(Long id) {
        return studentApplicationMapper.selectById(id);
    }

    @Override
    public boolean saveStudentApplication(StudentApplication studentApplication) {
        return studentApplicationMapper.insert(studentApplication) > 0;
    }

    @Override
    public boolean updateStudentApplication(StudentApplication studentApplication) {
        return studentApplicationMapper.updateById(studentApplication) > 0;
    }
    public boolean updateDiplomaPath(Long studentId, String filePath) {
        StudentApplication application = getById(studentId);
        if (application != null) {
            application.setHighSchoolDiploma(filePath);
            return updateById(application);
        }
        return false;
    }
    @Override
    public boolean deleteStudentApplication(Long id) {
        return studentApplicationMapper.deleteById(id) > 0;
    }

    @Override
    public StudentApplication getById(Long applicationId) {
        return studentApplicationMapper.selectById(applicationId);
    }


}
