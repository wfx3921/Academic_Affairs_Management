package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.*;
import com.ouc.aamanagement.mapper.GraduationMapper;
import com.ouc.aamanagement.mapper.StudentInfoMapper;
import com.ouc.aamanagement.service.GraduationService;
import com.ouc.aamanagement.service.StudentInfoService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




/**
 * 毕业信息 Controller
 * 基路径: /api/graduation
 */
@CrossOrigin
@RestController
@RequestMapping("/api/graduation")
public class GraduationController {

    @Autowired
    private GraduationService graduationService;

    /**
     * 新增毕业信息
     * 请求地址: POST /api/graduation/add
     *
     * @param graduation 毕业信息实体
     * @return 新增后的毕业信息
     */
    @PostMapping("/add")
    public R<Graduation> addGraduation(@RequestBody Graduation graduation) {
        boolean saved = graduationService.save(graduation);
        if (saved) {
            return R.success(graduation);
        } else {
            return R.error("添加毕业信息失败");
        }
    }

    /**
     * 删除毕业信息（根据 id）
     * 请求地址: DELETE /api/graduation/delete/{id}
     *
     * @param id 毕业信息 id
     * @return 删除结果提示
     */


    /**
     * 更新毕业信息
     * 请求地址: PUT /api/graduation/update
     *
     * @param graduation 毕业信息实体（需包含 id）
     * @return 更新后的毕业信息
     */

    @Autowired
    private GraduationMapper graduationMapper;

    @DeleteMapping("/delete/{studentNumber}")
    public ResponseEntity<?> deleteByStudentNumber(@PathVariable String studentNumber) {
        // 创建查询条件
        QueryWrapper<Graduation> wrapper = new QueryWrapper<>();
        wrapper.eq("student_number", studentNumber);

        // 执行删除
        int affectedRows = graduationMapper.delete(wrapper);

        if (affectedRows == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("未找到学号为 " + studentNumber + " 的记录");
        }

        return ResponseEntity.ok().build();
    }
    @CrossOrigin
    @PutMapping("/update")
    public R<Graduation> updateGraduation(@RequestBody Graduation graduation) {
        boolean updated = graduationService.updateById(graduation);
        if (updated) {
            return R.success(graduation);
        } else {
            return R.error("更新毕业信息失败");
        }
    }

    /**
     * 根据 id 查询毕业信息
     * 请求地址: GET /api/graduation/get/{id}
     *
     * @param id 毕业信息 id
     * @return 毕业信息
     */
    @GetMapping("/get/{id}")
    public R<Graduation> getGraduation(@PathVariable Long id) {
        Graduation graduation = graduationService.getById(id);
        if (graduation != null) {
            return R.success(graduation);
        } else {
            return R.error("毕业信息不存在");
        }
    }

    /**
     * 分页及条件查询毕业信息
     * 请求地址: GET /api/graduation/page
     *
     * @param page          当前页码，默认值 1
     * @param size          每页条数，默认值 10
     * @param studentNumber 学生学号（条件查询，可选）
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public R<Page<GranDTO>> getGraduationPage(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String studentNumber) {
        QueryWrapper<Graduation> queryWrapper = new QueryWrapper<>();
        if (studentNumber != null && !studentNumber.trim().isEmpty()) {
            queryWrapper.eq("s.student_number", studentNumber);
        }
        Page<GranDTO> result = graduationService.getGranWithStudentPage(new Page<>(page, size), queryWrapper);
        return R.success(result);
    }




    @PutMapping("/pass")
    public R<Void> updatePassStatus(
            @RequestBody Map<String, Object> request) {  // 改用请求体接收

        String studentNumber = (String) request.get("studentNumber");
        Integer isPassed = (Integer) request.get("isPassed");

        UpdateWrapper<Graduation> wrapper = new UpdateWrapper<>();
        wrapper.eq("student_number", studentNumber)
                .set("is_passed", isPassed);

        graduationMapper.update(null, wrapper);
        return R.success();
    }


    @Autowired
    private StudentInfoMapper studentInfoMapper;
    // Java Spring Boot 示例
    @PostMapping("/updateSpecial")
    public R updateStudentInfo(@RequestBody Payload payload) {
        // 1. 更新毕业表（graduation 表）
        Graduation graduationUpdate = new Graduation();
        graduationUpdate.setSpecialType(payload.getSpecialType());
        graduationUpdate.setSpecialInfo(payload.getSpecialInfo());
        System.out.println(payload.getSpecialType());
        graduationMapper.update(
                graduationUpdate,
                new UpdateWrapper<Graduation>()
                        .eq("student_number", payload.getStudentNumber()) // WHERE student_number = ?
        );

        // 2. 更新学生信息表（student_info 表）
        StudentInfo studentInfoUpdate = new StudentInfo();
        studentInfoUpdate.setGraduationDate(payload.getGraduationDate());

        studentInfoMapper.update(
                studentInfoUpdate,
                new UpdateWrapper<StudentInfo>()
                        .eq("student_number", payload.getStudentNumber()) // WHERE student_number = ?
        );

        return R.success();
    }
    /**
     * 分页查询特殊学生列表
     * @param page 当前页码 (默认1)
     * @param size 每页数量 (默认10)
     */
    @GetMapping("/special-students")
    public R<Page<SpecialStudentVO>> getSpecialStudents(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String gradeName,
            @RequestParam(required = false) String className) {

        Page<Graduation> pageParam = new Page<>(page, size);

        try {
            Page<SpecialStudentVO> result = graduationService.getSpecialStudents(
                    pageParam,
                    studentNumber,
                    studentName,
                    gradeName,
                    className
            );
            return R.success(result);
        } catch (Exception e) {
            return R.error("查询失败: " + e.getMessage());
        }
    }
    @GetMapping("/history-students")
    public R<Page<SpecialStudentVO>> getHistoryStudents(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String gradeName,    // 新增参数
            @RequestParam(required = false) String className     // 新增参数
    ) {
        Page<Graduation> pageParam = new Page<>(page, size);
        try {
            Page<SpecialStudentVO> result = graduationService.getHistoryStudents(
                    pageParam,
                    studentNumber,
                    studentName,
                    gradeName,
                    className
            );
            return R.success(result);
        } catch (Exception e) {

            return R.error("查询失败: " + e.getMessage());
        }
    }
    @GetMapping("/history-students/export")
    public void exportHistoryStudents(
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String gradeName,
            @RequestParam(required = false) String className,
            HttpServletResponse response) throws IOException {

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode("特殊毕业生数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 执行导出
        graduationService.exportHistoryStudents(
                response.getOutputStream(),
                studentNumber,
                studentName,
                gradeName,
                className
        );
    }
    @Autowired
    private StudentInfoService studentInfoService;

    @GetMapping("/audit")
    public R<Page<StudentInfoDTO>> getStudentWithGraduationStatus(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String gradeName,  // 新增年级参数
            @RequestParam(required = false) String className   // 新增班级参数
    ) {
        Page<StudentInfoDTO> result = studentInfoService.getStudentWithGraduation(
                page, size, studentNumber, gradeName, className);
        return R.success(result);
    }
    @GetMapping("/export")
    public void exportGraduationExcel(HttpServletResponse response) {
        try {
            // 查询所有毕业信息数据
            List<Graduation> graduationList = graduationService.list();

            // 创建 Excel 工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("毕业信息");

            // 创建表头（去掉 ID、createTime、updateTime）
            String[] headers = {"学号", "毕业审核状态", "毕业去向", "就业情况", "审核意见", "特殊类型", "特殊情况说明"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            int rowIdx = 1;
            for (Graduation graduation : graduationList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(graduation.getStudentNumber());
                row.createCell(1).setCellValue(graduation.getGraduationStatus());
                row.createCell(2).setCellValue(graduation.getGraduateDestination());
                row.createCell(3).setCellValue(graduation.getEmploymentInfo());
                row.createCell(4).setCellValue(graduation.getAuditRemark());
                row.createCell(5).setCellValue(graduation.getSpecialType());
                row.createCell(6).setCellValue(graduation.getSpecialInfo());
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("毕业信息.xlsx", "UTF-8"));

            // 写入输出流
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @PostMapping("/import")
    public R<String> importGraduationExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("上传文件不能为空");
        }
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<Graduation> newGraduationList = new ArrayList<>();

            // 1️⃣ 查询数据库中已有数据
            List<Graduation> existingGraduations = graduationService.list();

            // 读取数据（跳过表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Graduation graduation = new Graduation();
                graduation.setStudentNumber(row.getCell(0).getStringCellValue());
                graduation.setGraduationStatus(row.getCell(1).getStringCellValue());
                graduation.setGraduateDestination(row.getCell(2).getStringCellValue());
                graduation.setEmploymentInfo(row.getCell(3).getStringCellValue());
                graduation.setAuditRemark(row.getCell(4).getStringCellValue());
                graduation.setSpecialType(row.getCell(5).getStringCellValue());
                graduation.setSpecialInfo(row.getCell(6).getStringCellValue());

                // 2️⃣ 判断是否已存在
                boolean isDuplicate = existingGraduations.stream()
                        .anyMatch(g -> g.getStudentNumber().equals(graduation.getStudentNumber()) &&
                                g.getGraduationStatus().equals(graduation.getGraduationStatus()) &&
                                g.getGraduateDestination().equals(graduation.getGraduateDestination()) &&
                                g.getEmploymentInfo().equals(graduation.getEmploymentInfo()));

                if (!isDuplicate) {
                    newGraduationList.add(graduation);
                }
            }

            // 3️⃣ 批量插入数据库（只插入不重复的数据）
            if (!newGraduationList.isEmpty()) {
                graduationService.saveBatch(newGraduationList);
            }
            workbook.close();
            return R.success("成功导入 " + newGraduationList.size() + " 条毕业信息记录");
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件解析失败");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("数据格式错误，请检查 Excel 文件");
        }
    }
    @Data
    public static class SpecialStudentDTO {
        private String studentNumber;
        private String studentName;
        private String specialType;
        private String specialInfo;

    }
    @PostMapping("/add-special")
    public R<String> addSpecialStudent(@RequestBody SpecialStudentDTO dto) {
        try {
            // 验证必填字段
            if (StringUtils.isBlank(dto.getStudentNumber())) {
                return R.error("学号不能为空");
            }
            if (StringUtils.isBlank(dto.getSpecialType())) {
                return R.error("特殊类型不能为空");
            }
            // 创建实体并保存
            Graduation graduation = new Graduation();
            graduation.setStudentNumber(dto.getStudentNumber());
            graduation.setSpecialType(dto.getSpecialType());
            graduation.setSpecialInfo(dto.getSpecialInfo());
            graduation.setGraduationStatus("pending");
            graduation.setName("name");
            boolean success = graduationService.save(graduation);
            return success ? R.success("添加成功") : R.error("添加失败");

        } catch (Exception e) {

            return R.error("系统异常: " + e.getMessage());
        }
    }

}
