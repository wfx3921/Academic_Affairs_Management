package com.ouc.aamanagement.controller;

import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StudentAwardsPunishments;
import com.ouc.aamanagement.service.StudentAwardsPunishmentsService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 奖惩信息导入导出功能
 */
@CrossOrigin
@RestController
@RequestMapping("/api/student-awards-punishments/excel")
public class StudentAwardsPunishmentsExcelController {

    @Autowired
    private StudentAwardsPunishmentsService service;

    // ========== 导入 ==========

    /**
     * 单个导入：导入 Excel 文件中的单个奖惩记录
     * 要求 Excel 文件第一行为表头，且列顺序为：
     * 姓名, 学号, 年级, 专业, 奖惩类型, 奖惩级别, 描述, 记录日期, 开始时间, 结束时间, 记录状态, 记录人, 记录时间, 更新人, 更新时间
     */
    @Transactional
    @PostMapping("/import/single")
    public R<Boolean> importSingle(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() < 1) {
                return R.error("Excel文件无数据");
            }
            // 仅处理第一条数据（从第2行开始，索引为1）
            Row row = sheet.getRow(1);
            StudentAwardsPunishments record = parseRow(row);
            boolean result = service.save(record);
            return result ? R.success(true) : R.error("导入失败");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入异常: " + e.getMessage());
        }
    }

    /**
     * 批量导入：导入 Excel 文件中的多条奖惩记录
     * 文件格式要求与单个导入相同，第一行为表头，后续行为数据记录
     */
    @Transactional
    @PostMapping("/import/batch")
    public R<Boolean> importBatch(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<StudentAwardsPunishments> records = new ArrayList<>();
            // 从第二行开始读取
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                StudentAwardsPunishments record = parseRow(row);
                records.add(record);
            }
            boolean result = service.saveBatch(records);
            return result ? R.success(true) : R.error("批量导入失败");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("批量导入异常: " + e.getMessage());
        }
    }

    // ========== 导出 ==========


    /**
     * 单个导出：根据ID导出单个奖惩记录为 Excel 文件（不含编号列）
     * 导出文件中表头为中文，顺序为：
     * 姓名, 学号, 年级, 专业, 奖惩类型, 奖惩级别, 描述, 记录日期, 开始时间, 结束时间, 记录状态, 记录人, 记录时间, 更新人, 更新时间
     */
    @GetMapping("/export/single/{id}")
    public void exportSingle(@PathVariable Long id, HttpServletResponse response) {
        StudentAwardsPunishments record = service.getById(id);
        if (record == null) {
            try {
                response.getWriter().write("未找到对应记录");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("单条记录");
            // 表头
            Row header = sheet.createRow(0);
            String[] headers = {"姓名", "学号", "年级", "专业", "奖惩类型", "奖惩级别", "描述", "记录日期", "开始时间", "结束时间", "记录状态", "记录人", "记录时间", "更新人", "更新时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // 数据行
            Row row = sheet.createRow(1);
            fillRow(row, record);
            // 输出 Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"record_" + record.getStudentNumber() + ".xlsx\"");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量导出：导出所有奖惩记录（或符合条件的记录）为 Excel 文件
     * 导出文件中表头为中文，第一列为“编号”即 id，其后依次为：
     * 姓名, 学号, 年级, 专业, 奖惩类型, 奖惩级别, 描述, 记录日期, 开始时间, 结束时间, 记录状态, 记录人, 记录时间, 更新人, 更新时间
     */
    @GetMapping("/export/batch")
    public void exportBatch(HttpServletResponse response) {
        List<StudentAwardsPunishments> list = service.list();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("全部记录");
            // 表头
            Row header = sheet.createRow(0);
            String[] headers = {"姓名", "学号", "年级", "专业", "奖惩类型", "奖惩级别", "描述", "记录日期", "开始时间", "结束时间", "记录状态", "记录人", "记录时间", "更新人", "更新时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // 数据行
            int rowIndex = 1;
            for (StudentAwardsPunishments record : list) {
                Row row = sheet.createRow(rowIndex++);
                fillRowWithId(row, record);
            }
            // 输出 Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"records.xlsx\"");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ========== 工具方法 ==========

    /**
     * 根据 Excel 行解析出一个 StudentAwardsPunishments 对象
     * 读取顺序（15列）：姓名, 学号, 年级, 专业, 奖惩类型, 奖惩级别, 描述, 记录日期, 开始时间, 结束时间, 记录状态, 记录人, 记录时间, 更新人, 更新时间
     */
    private StudentAwardsPunishments parseRow(Row row) throws Exception {
        StudentAwardsPunishments record = new StudentAwardsPunishments();
        int idx = 0;
        record.setName(getStringCellValue(row.getCell(idx++)));
        record.setStudentNumber(getStringCellValue(row.getCell(idx++)));
        record.setGrade(getStringCellValue(row.getCell(idx++)));
        record.setMajor(getStringCellValue(row.getCell(idx++)));
        record.setType(getStringCellValue(row.getCell(idx++)));
        record.setLevel(getStringCellValue(row.getCell(idx++)));
        record.setDescription(getStringCellValue(row.getCell(idx++)));
        record.setRecordDate(getDateCellValue(row.getCell(idx++)));
        record.setStartDate(getDateCellValue(row.getCell(idx++)));
        record.setEndDate(getDateCellValue(row.getCell(idx++)));
        record.setStatus(getStringCellValue(row.getCell(idx++)));
        record.setCreatedBy(getStringCellValue(row.getCell(idx++)));
        record.setCreatedTime(getDateTimeCellValue(row.getCell(idx++)));
        record.setUpdatedBy(getStringCellValue(row.getCell(idx++)));
        record.setUpdatedTime(getDateTimeCellValue(row.getCell(idx++)));
        return record;
    }

    /**
     * 填充单个导出 Excel 行（不含编号/id），顺序与导入要求一致（15列）
     */
    private void fillRow(Row row, StudentAwardsPunishments record) {
        int cellIndex = 0;
        row.createCell(cellIndex++).setCellValue(record.getName());
        row.createCell(cellIndex++).setCellValue(record.getStudentNumber());
        row.createCell(cellIndex++).setCellValue(record.getGrade());
        row.createCell(cellIndex++).setCellValue(record.getMajor());
        row.createCell(cellIndex++).setCellValue(record.getType());
        row.createCell(cellIndex++).setCellValue(record.getLevel());
        row.createCell(cellIndex++).setCellValue(record.getDescription() != null ? record.getDescription() : "");
        row.createCell(cellIndex++).setCellValue(formatDate(record.getRecordDate()));
        row.createCell(cellIndex++).setCellValue(formatDate(record.getStartDate()));
        row.createCell(cellIndex++).setCellValue(formatDate(record.getEndDate()));
        row.createCell(cellIndex++).setCellValue(record.getStatus());
        row.createCell(cellIndex++).setCellValue(record.getCreatedBy());
        row.createCell(cellIndex++).setCellValue(formatDateTime(record.getCreatedTime()));
        row.createCell(cellIndex++).setCellValue(record.getUpdatedBy() != null ? record.getUpdatedBy() : "");
        row.createCell(cellIndex++).setCellValue(formatDateTime(record.getUpdatedTime()));
    }

    /**
     * 填充批量导出 Excel 行（包含编号/id作为第一列），后续顺序与单个导出一致，共16列
     */
    private void fillRowWithId(Row row, StudentAwardsPunishments record) {
        int cellIndex = 0;
        row.createCell(cellIndex++).setCellValue(record.getName());
        row.createCell(cellIndex++).setCellValue(record.getStudentNumber());
        row.createCell(cellIndex++).setCellValue(record.getGrade());
        row.createCell(cellIndex++).setCellValue(record.getMajor());
        row.createCell(cellIndex++).setCellValue(record.getType());
        row.createCell(cellIndex++).setCellValue(record.getLevel());
        row.createCell(cellIndex++).setCellValue(record.getDescription() != null ? record.getDescription() : "");
        row.createCell(cellIndex++).setCellValue(formatDate(record.getRecordDate()));
        row.createCell(cellIndex++).setCellValue(formatDate(record.getStartDate()));
        row.createCell(cellIndex++).setCellValue(formatDate(record.getEndDate()));
        row.createCell(cellIndex++).setCellValue(record.getStatus());
        row.createCell(cellIndex++).setCellValue(record.getCreatedBy());
        row.createCell(cellIndex++).setCellValue(formatDateTime(record.getCreatedTime()));
        row.createCell(cellIndex++).setCellValue(record.getUpdatedBy() != null ? record.getUpdatedBy() : "");
        row.createCell(cellIndex++).setCellValue(formatDateTime(record.getUpdatedTime()));
    }

    // ======= 辅助方法 =======

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
