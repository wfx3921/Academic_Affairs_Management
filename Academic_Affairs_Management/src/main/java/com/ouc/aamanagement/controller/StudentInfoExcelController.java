package com.ouc.aamanagement.controller;

import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.entity.StudentApplication1;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.entity.StudentInfo1;
import com.ouc.aamanagement.service.StudentApplication1Service;
import com.ouc.aamanagement.service.StudentApplicationService;
import com.ouc.aamanagement.service.StudentInfo1Service;
import com.ouc.aamanagement.service.StudentInfoService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * 学生个人信息导入导出功能
 */

@CrossOrigin
@RestController
@RequestMapping("/api/student-info/excel")
public class StudentInfoExcelController {

    @Autowired
    private StudentApplication1Service studentApplicationService;

    @Autowired
    private StudentInfo1Service studentInfoService;

    // ================= 导入功能 =================

    /**
     * 单个导入：导入 Excel 文件中的单条学生个人信息记录
     * Excel 文件要求：第一行为表头，字段名称及顺序为：
     * 姓名, 性别, 出生日期, 身份证号, 高中学校, 毕业日期, 国家, 省, 市, 区, 详细地址, 家长电话, 手机号, 邮箱,
     * 其它语言成绩, 高考成绩, 高中毕业证, 开放日时间, scn号码, 学号, 年级, 专业, 班级, 学籍状态, 报到状态
     */
    @Transactional
    @PostMapping("/import/single")
    public R<Boolean> importSingle(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() < 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return R.error("Excel 文件中无数据");
            }
            // 读取第二行（第一行为表头）
            Row row = sheet.getRow(1);
            StudentApplication1 app = new StudentApplication1();
            StudentInfo1 info = new StudentInfo1();
            parseRow(row, app, info);
            // 先保存报名信息，获取生成的ID
            if (!studentApplicationService.save(app)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return R.error("保存学生报名信息失败");
            }
            // 设置外键关联
            info.setApplicationId(app.getId());
            if (!studentInfoService.save(info)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new RuntimeException("保存学生个人信息失败");
            }
            return R.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("单条导入异常: " + e.getMessage());
        }
    }

    /**
     * 批量导入：导入 Excel 文件中的多条学生个人信息记录
     * Excel 文件格式要求同单条导入
     */
    @Transactional
    @PostMapping("/import/batch")
    public R<Boolean> importBatch(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            // 从第二行开始遍历所有数据行
            Iterator<Row> rowIterator = sheet.rowIterator();
            // 跳过表头
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row == null) continue;
                StudentApplication1 app = new StudentApplication1();
                StudentInfo1 info = new StudentInfo1();
                parseRow(row, app, info);
                // 保存报名信息
                if (!studentApplicationService.save(app)) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new RuntimeException("保存学生报名信息失败");
                }
                info.setApplicationId(app.getId());
                if (!studentInfoService.save(info)) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new RuntimeException("保存学生个人信息失败");
                }
            }
            return R.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("批量导入异常: " + e.getMessage());
        }
    }

    // ================= 导出功能 =================

    /**
     * 单个导出：根据学生个人信息记录ID导出单条记录为 Excel 文件（不含编号列）
     * 导出 Excel 表头（中文）：姓名, 性别, 出生日期, 身份证号, 高中学校, 毕业日期, 国家, 省, 市, 区, 详细地址, 家长电话, 手机号, 邮箱, 其它语言成绩, 高考成绩, 高中毕业证, 开放日时间, scn号码, 学号, 年级, 专业, 班级, 学籍状态, 报到状态
     */
    @GetMapping("/export/single/{id}")
    public void exportSingle(@PathVariable Long id, HttpServletResponse response) {
        StudentInfo1 info = studentInfoService.getById(id);
        if (info == null) {
            try {
                response.getWriter().write("未找到对应记录");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("记录");
            // 写入表头
            Row header = sheet.createRow(0);
            String[] headers = {"姓名", "性别", "出生日期", "身份证号", "高中学校", "毕业日期", "国家", "省", "市", "区", "详细地址",
                    "家长电话", "手机号", "邮箱", "其它语言成绩", "高考成绩", "高中毕业证", "开放日时间", "scn号码", "学号", "年级", "专业", "班级", "学籍状态", "报到状态"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
            // 写入数据行
            Row row = sheet.createRow(1);
            fillRow(row, info);
            // 设置导出文件名（文件名必须为非中文）
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"student_" + info.getStudentNumber() + ".xlsx\"");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量导出：导出所有学生个人信息记录为 Excel 文件
     * 表头第一列为“编号”，后续列为：
     * 姓名, 性别, 出生日期, 身份证号, 高中学校, 毕业日期, 国家, 省, 市, 区, 详细地址, 家长电话, 手机号, 邮箱, 其它语言成绩, 高考成绩, 高中毕业证, 开放日时间, scn号码, 学号, 年级, 专业, 班级, 学籍状态, 报到状态
     */
    @GetMapping("/export/batch")
    public void exportBatch(HttpServletResponse response) {
        // 查询所有记录
        List<StudentInfo1> list = studentInfoService.list();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("全部记录");
            // 表头
            Row header = sheet.createRow(0);
            String[] headers = {"编号", "姓名", "性别", "出生日期", "身份证号", "高中学校", "毕业日期", "国家", "省", "市", "区", "详细地址",
                    "家长电话", "手机号", "邮箱", "其它语言成绩", "高考成绩", "高中毕业证", "开放日时间", "scn号码", "学号", "年级", "专业", "班级", "学籍状态", "报到状态"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
            // 数据行
            int rowIndex = 1;
            for (StudentInfo1 info : list) {
                Row row = sheet.createRow(rowIndex++);
                fillRowWithId(row, info);
            }
            // 设置导出文件名（必须为非中文）
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"student_info.xlsx\"");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= 辅助方法 =================

    /**
     * 从 Excel 行中解析数据，并填充到 StudentApplication 与 StudentInfo 对象中（不包括外键）
     * Excel 列顺序（共25列）：
     * 0 姓名, 1 性别, 2 出生日期, 3 身份证号, 4 高中学校, 5 毕业日期, 6 国家, 7 省, 8 市, 9 区, 10 详细地址,
     * 11 家长电话, 12 手机号, 13 邮箱, 14 其它语言成绩, 15 高考成绩, 16 高中毕业证, 17 开放日时间,
     * 18 scn号码, 19 学号, 20 年级, 21 专业, 22 班级, 23 学籍状态, 24 报到状态
     */
    private void parseRow(Row row, StudentApplication1 app, StudentInfo1 info) throws Exception {
        int idx = 0;
        // 姓名
        String name = getStringCellValue(row.getCell(idx++));
        app.setName(name);
        info.setName(name);
        // 性别
        String gender = getStringCellValue(row.getCell(idx++));
        app.setGender(gender);
        info.setGender(gender);
        // 出生日期
        Date birthDate = getDateCellValue(row.getCell(idx++));
        app.setBirthDate(birthDate);
        info.setBirthDate(birthDate);
        // 身份证号
        String idCard = getStringCellValue(row.getCell(idx++));
        app.setIdCard(idCard);
        info.setIdCard(idCard);
        // 高中学校
        String highSchool = getStringCellValue(row.getCell(idx++));
        app.setHighSchool(highSchool);
        info.setHighSchool(highSchool);
        // 毕业日期
        Date graduationDate = getDateCellValue(row.getCell(idx++));
        app.setGraduationDate(graduationDate);
        info.setGraduationDate(graduationDate);
        // 国家
        String country = getStringCellValue(row.getCell(idx++));
        app.setAddressCountry(country);
        info.setAddressCountry(country);
        // 省
        String province = getStringCellValue(row.getCell(idx++));
        app.setAddressProvince(province);
        info.setAddressProvince(province);
        // 市
        String city = getStringCellValue(row.getCell(idx++));
        app.setAddressCity(city);
        info.setAddressCity(city);
        // 区
        String district = getStringCellValue(row.getCell(idx++));
        app.setAddressDistrict(district);
        info.setAddressDistrict(district);
        // 详细地址
        String detail = getStringCellValue(row.getCell(idx++));
        app.setAddressDetail(detail);
        info.setAddressDetail(detail);
        // 家长电话
        String parentPhone = getStringCellValue(row.getCell(idx++));
        app.setParentPhone(parentPhone);
        info.setParentPhone(parentPhone);
        // 手机号
        String phone = getStringCellValue(row.getCell(idx++));
        app.setPhoneNumber(phone);
        info.setPhoneNumber(phone);
        // 邮箱
        String email = getStringCellValue(row.getCell(idx++));
        app.setEmail(email);
        info.setEmail(email);
        // 其它语言成绩
        String otherLangScore = getStringCellValue(row.getCell(idx++));
        app.setOtherLanguageScore(otherLangScore);
        info.setOtherLanguageScore(otherLangScore);
        // 高考成绩
        Double collegeExamScore = getNumericCellValue(row.getCell(idx++));
        app.setCollegeExamScore(collegeExamScore.intValue());
        info.setCollegeExamScore(collegeExamScore.intValue());
        // 高中毕业证
        String diploma = getStringCellValue(row.getCell(idx++));
        app.setHighSchoolDiploma(diploma);
        info.setHighSchoolDiploma(diploma);
        // 开放日时间
        Date openDayTime = getDateTimeCellValue(row.getCell(idx++));
        app.setOpenDayTime(openDayTime);
        info.setOpenDayTime(openDayTime);
        // 以下为 student_info 专有字段
        String scnNumber = getStringCellValue(row.getCell(idx++));
        info.setScnNumber(scnNumber);
        String stuNumber = getStringCellValue(row.getCell(idx++));
        info.setStudentNumber(stuNumber);
        String grade = getStringCellValue(row.getCell(idx++));
        info.setGrade(grade);
        String major = getStringCellValue(row.getCell(idx++));
        info.setMajor(major);
        String clazz = getStringCellValue(row.getCell(idx++));
        info.setClazz(clazz);
        String studentStatus = getStringCellValue(row.getCell(idx++));
        info.setStudentStatus(studentStatus);
        String registrationStatus = getStringCellValue(row.getCell(idx++));
        info.setRegistrationStatus(registrationStatus);
    }

    /**
     * 填充单个导出 Excel 数据行（不含编号/id），共25列，顺序与导入要求一致
     */
    private void fillRow(Row row, StudentInfo1 info) {
        int cellIndex = 0;
        row.createCell(cellIndex++).setCellValue(info.getName());
        row.createCell(cellIndex++).setCellValue(info.getGender());
        row.createCell(cellIndex++).setCellValue(formatDate(info.getBirthDate()));
        row.createCell(cellIndex++).setCellValue(info.getIdCard());
        row.createCell(cellIndex++).setCellValue(info.getHighSchool());
        row.createCell(cellIndex++).setCellValue(formatDate(info.getGraduationDate()));
        row.createCell(cellIndex++).setCellValue(info.getAddressCountry());
        row.createCell(cellIndex++).setCellValue(info.getAddressProvince());
        row.createCell(cellIndex++).setCellValue(info.getAddressCity());
        row.createCell(cellIndex++).setCellValue(info.getAddressDistrict());
        row.createCell(cellIndex++).setCellValue(info.getAddressDetail());
        row.createCell(cellIndex++).setCellValue(info.getParentPhone());
        row.createCell(cellIndex++).setCellValue(info.getPhoneNumber());
        row.createCell(cellIndex++).setCellValue(info.getEmail());
        row.createCell(cellIndex++).setCellValue(info.getOtherLanguageScore());
        row.createCell(cellIndex++).setCellValue(info.getCollegeExamScore());
        row.createCell(cellIndex++).setCellValue(info.getHighSchoolDiploma());
        row.createCell(cellIndex++).setCellValue(formatDateTime(info.getOpenDayTime()));
        row.createCell(cellIndex++).setCellValue(info.getScnNumber());
        row.createCell(cellIndex++).setCellValue(info.getStudentNumber());
        row.createCell(cellIndex++).setCellValue(info.getGrade());
        row.createCell(cellIndex++).setCellValue(info.getMajor());
        row.createCell(cellIndex++).setCellValue(info.getClazz());
        row.createCell(cellIndex++).setCellValue(info.getStudentStatus());
        row.createCell(cellIndex++).setCellValue(info.getRegistrationStatus());
    }

    /**
     * 填充批量导出 Excel 数据行（第一列为编号/id），共26列
     */
    private void fillRowWithId(Row row, StudentInfo1 info) {
        int cellIndex = 0;
        row.createCell(cellIndex++).setCellValue(info.getId());
        row.createCell(cellIndex++).setCellValue(info.getName());
        row.createCell(cellIndex++).setCellValue(info.getGender());
        row.createCell(cellIndex++).setCellValue(formatDate(info.getBirthDate()));
        row.createCell(cellIndex++).setCellValue(info.getIdCard());
        row.createCell(cellIndex++).setCellValue(info.getHighSchool());
        row.createCell(cellIndex++).setCellValue(formatDate(info.getGraduationDate()));
        row.createCell(cellIndex++).setCellValue(info.getAddressCountry());
        row.createCell(cellIndex++).setCellValue(info.getAddressProvince());
        row.createCell(cellIndex++).setCellValue(info.getAddressCity());
        row.createCell(cellIndex++).setCellValue(info.getAddressDistrict());
        row.createCell(cellIndex++).setCellValue(info.getAddressDetail());
        row.createCell(cellIndex++).setCellValue(info.getParentPhone());
        row.createCell(cellIndex++).setCellValue(info.getPhoneNumber());
        row.createCell(cellIndex++).setCellValue(info.getEmail());
        row.createCell(cellIndex++).setCellValue(info.getOtherLanguageScore());
        row.createCell(cellIndex++).setCellValue(info.getCollegeExamScore());
        row.createCell(cellIndex++).setCellValue(info.getHighSchoolDiploma());
        row.createCell(cellIndex++).setCellValue(formatDateTime(info.getOpenDayTime()));
        row.createCell(cellIndex++).setCellValue(info.getScnNumber());
        row.createCell(cellIndex++).setCellValue(info.getStudentNumber());
        row.createCell(cellIndex++).setCellValue(info.getGrade());
        row.createCell(cellIndex++).setCellValue(info.getMajor());
        row.createCell(cellIndex++).setCellValue(info.getClazz());
        row.createCell(cellIndex++).setCellValue(info.getStudentStatus());
        row.createCell(cellIndex++).setCellValue(info.getRegistrationStatus());
    }

    // ================= 辅助方法：单元格值解析与格式化 =================

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private double getNumericCellValue(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else {
            try {
                return Double.parseDouble(getStringCellValue(cell));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    private Date getDateCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else {
            String dateStr = getStringCellValue(cell);
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private Date getDateTimeCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else {
            String dateTimeStr = getStringCellValue(cell);
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeStr);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private String formatDateTime(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}