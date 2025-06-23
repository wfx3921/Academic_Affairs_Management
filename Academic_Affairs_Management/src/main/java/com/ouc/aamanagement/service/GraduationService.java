package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.entity.Graduation;
import com.ouc.aamanagement.entity.GranDTO;
import com.ouc.aamanagement.entity.SpecialStudentVO;

import java.io.OutputStream;
import java.util.List;


/**
 * 毕业信息 Service 接口
 */
public interface GraduationService extends IService<Graduation> {
    Page<GranDTO> getGranWithStudentPage(Page<Graduation> page, QueryWrapper<Graduation> queryWrapper);
        Page<SpecialStudentVO> getSpecialStudents(Page<Graduation> pageParam,
                                                  String studentNumber,
                                                  String studentName,
                                                  String gradeName,
                                                  String className);
    Page<SpecialStudentVO> getHistoryStudents(Page<Graduation> pageParam,
                                              String studentNumber,
                                              String studentName,
                                              String gradeName,
                                              String className);

    public void exportHistoryStudents(OutputStream outputStream,
                                      String studentNumber,
                                      String studentName,
                                      String gradeName,
                                      String className);
}