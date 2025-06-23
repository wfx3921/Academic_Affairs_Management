package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.Graduation;
import com.ouc.aamanagement.entity.GranDTO;

import com.ouc.aamanagement.entity.SpecialStudentVO;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.mapper.GraduationMapper;
import com.ouc.aamanagement.service.GraduationService;
import com.ouc.aamanagement.service.StudentInfoService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 毕业信息 Service 实现类
 */
@Service
@Transactional
public class GraduationServiceImpl extends ServiceImpl<GraduationMapper, Graduation> implements GraduationService {
    @Override
    public Page<GranDTO> getGranWithStudentPage(Page<Graduation> page, QueryWrapper<Graduation> queryWrapper) {
        // 转换分页对象
        Page<GranDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize());
        return baseMapper.selectGranWithStudent(dtoPage, queryWrapper);
    }
    @Autowired
    private GraduationMapper graduationMapper;



    @Override
    public Page<SpecialStudentVO> getSpecialStudents(Page<Graduation> pageParam,
                                                     String studentNumber,
                                                     String studentName,
                                                     String gradeName,
                                                     String className) {
        return graduationMapper.selectSpecialPage(
                pageParam,
                studentNumber,
                studentName,
                gradeName,
                className
        );
    }
    @Override
    public Page<SpecialStudentVO> getHistoryStudents(Page<Graduation> pageParam,
                                                     String studentNumber,
                                                     String studentName,
                                                     String gradeName,
                                                     String className) {
        return graduationMapper.selectHistoryStudentsPage(
                pageParam,
                studentNumber,
                studentName,
                gradeName,
                className
        );}

    @Override
    public void exportHistoryStudents(OutputStream outputStream,
                                      String studentNumber,
                                      String studentName,
                                      String gradeName,
                                      String className) {
        // 复用原有查询逻辑，设置超大分页获取全部数据
        Page<Graduation> pageParam = new Page<>(1, Integer.MAX_VALUE);
        Page<SpecialStudentVO> pageResult = graduationMapper.selectHistoryStudentsPage(
                pageParam,
                studentNumber,
                studentName,
                gradeName,
                className
        );

        // 使用Apache POI创建Excel
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("特殊毕业生数据");

            // 创建表头
            String[] headers = {"学号", "姓名", "毕业去向", "就业情况", "学籍状态", "特殊毕业生类型", "特殊毕业生说明"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // 填充数据
            int rowNum = 1;
            for (SpecialStudentVO vo : pageResult.getRecords()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(vo.getStudentNumber());
                row.createCell(1).setCellValue(vo.getStudentName());
                row.createCell(2).setCellValue(vo.getGraduateDestination());
                row.createCell(3).setCellValue(vo.getEmploymentInfo());
                row.createCell(4).setCellValue(vo.getStudentStatus());
                row.createCell(5).setCellValue(convertSpecialType(vo.getSpecialType()));
                row.createCell(6).setCellValue(vo.getSpecialInfo());
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败");
        }
    }

    // 特殊类型转换
    private String convertSpecialType(String type) {
        if (type == null) return "";
        switch (type) {
            case "none": return "无";
            case "nontraditional": return "非传统学历";
            case "exempt": return "课程免修";
            case "other": return "特殊申请";
            default: return type;
        }
    }
}

