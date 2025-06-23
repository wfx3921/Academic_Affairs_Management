package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.entity.StudentApplication1;


public interface StudentApplication1Service extends IService<StudentApplication1> {
    // 你可以在此声明额外的业务方法

    /**
     * 分页查询招生信息
     */
    IPage<StudentApplication1> getStudentApplicationsPage(int page, int size);

    /**
     * 根据 ID 查询招生信息
     */
    StudentApplication1 getStudentApplicationById(Long id);

    /**
     * 保存招生信息
     */
    boolean saveStudentApplication(StudentApplication1 studentApplication);

    /**
     * 更新招生信息
     */
    boolean updateStudentApplication(StudentApplication1 studentApplication);

    /**
     * 根据 ID 删除招生信息
     */
    boolean deleteStudentApplication(Long id);

    /**
     * 根据applicationId
     */
    StudentApplication1 getById(Long applicationId);

}