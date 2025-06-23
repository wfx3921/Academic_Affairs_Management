package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.entity.StudentApplication;


public interface StudentApplicationService extends IService<StudentApplication> {
    // 你可以在此声明额外的业务方法

    /**
     * 分页查询招生信息
     */
    IPage<StudentApplication> getStudentApplicationsPage(int page, int size);

    /**
     * 根据 ID 查询招生信息
     */
    StudentApplication getStudentApplicationById(Long id);

    /**
     * 保存招生信息
     */
    boolean saveStudentApplication(StudentApplication studentApplication);

    /**
     * 更新招生信息
     */
    boolean updateStudentApplication(StudentApplication studentApplication);

    /**
     * 根据 ID 删除招生信息
     */
    boolean deleteStudentApplication(Long id);

    /**
     * 根据applicationId
     */
    StudentApplication getById(Long applicationId);
    public boolean updateDiplomaPath(Long studentId, String filePath);

}