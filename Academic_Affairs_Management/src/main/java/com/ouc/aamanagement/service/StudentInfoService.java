package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.entity.StudentInfoDTO;

import java.util.Collection;
import java.util.List;

public interface StudentInfoService extends IService<StudentInfo> {
    // 可在此添加除基本 CRUD 以外的业务方法
     Page<StudentInfoDTO> getStudentWithGraduation(int page, int size, String studentNumber, String gradeName,  // 新增参数
                                                   String className );
    List<StudentInfo> listByStudentNumbers(Collection<String> studentNumbers);
    List<StudentInfo> listByApplicationIds(Collection<Long> applicationIds);
    public boolean updateDiplomaPathByApplicationId(Long applicationId, String filePath);
}

