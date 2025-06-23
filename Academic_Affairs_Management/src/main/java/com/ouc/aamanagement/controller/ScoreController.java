package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.*;
import com.ouc.aamanagement.mapper.ScoreMapper;
import com.ouc.aamanagement.service.CourseService;
import com.ouc.aamanagement.service.ScoreService;
import com.ouc.aamanagement.service.StudentInfoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 成绩管理 Controller
 * 基路径: /api/score
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CourseService courseService;

    public ScoreController(ScoreMapper scoreMapper) {
        this.scoreMapper = scoreMapper;
    }

    /**
     * 新增成绩记录
     * 请求地址: POST /api/score/add
     *
     * @param score 成绩记录实体
     * @return 新增后的成绩记录
     */
    @PostMapping("/add")
    public R<Score> addScore(@RequestBody Score score) {
        boolean saved = scoreService.save(score);
        if (saved) {
            return R.success(score);
        } else {
            return R.error("添加成绩失败");
        }
    }

    /**
     * 删除成绩记录（根据 id）
     * 请求地址: DELETE /api/score/delete/{id}
     *
     * @param id 成绩记录 id
     * @return 删除结果提示
     */
    @DeleteMapping("/delete/{id}")
    public R<String> deleteById(@PathVariable String id) {
        // 方式1：直接使用removeById方法（主键删除）
        boolean removed = scoreService.removeById(id);

        // 方式2：使用Lambda表达式（效果相同）
        // boolean removed = scoreService.lambdaUpdate()
        //         .eq(Score::getId, id)
        //         .remove();

        if (removed) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败，可能不存在该记录");
        }
    }

    @GetMapping("/page1")
    public R<Page<Score>> getPendingScores(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 1. 创建分页对象
        Page<Score> pageInfo = new Page<>(page, size);
        // 2. 构建查询条件（只查询待审核记录）
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Score::getAuditStatus, "initial"); // ne表示not equal
        // 3. 执行分页查询
        Page<Score> result = scoreService.page(pageInfo, queryWrapper);
        // 4. 关联查询课程名称
        List<Score> records = result.getRecords();
        if (!records.isEmpty()) {
            Set<String> courseCodes = records.stream()
                    .map(Score::getCourseCode)
                    .filter(Objects::nonNull) // 过滤掉null值
                    .collect(Collectors.toSet());
            if (!courseCodes.isEmpty()) {
                Map<String, String> courseNameMap = courseService.listByCourseCodes(courseCodes).stream()
                        .collect(Collectors.toMap(
                                Course::getCourseCode,
                                Course::getCourseName,
                                (existing, replacement) -> existing
                        ));
                records.forEach(score ->
                        score.setCourseName(courseNameMap.get(score.getCourseCode()))
                );
            }
        }
        return R.success(result);
    }

    /**
     * 根据学号查询该学生的所有成绩记录
     * 请求地址: GET /api/score/listByStudentNumber?studentNumber=xxx
     */
    @GetMapping("/listByStudentNumber")
    public R<List<Score>> listByStudentNumber(
            @RequestParam(required = false) String studentNumber) {  // 改为可选参数
        // 1. 构建查询条件
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(studentNumber)) {
            queryWrapper.eq(Score::getStudentNumber, studentNumber);
        }

        // 2. 查询成绩列表
        List<Score> scores = scoreService.list(queryWrapper);
        for(Score score:scores){
            LambdaQueryWrapper<Course> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Course::getCourseCode,score.getCourseCode());
            Course course = courseService.getOne(queryWrapper1);
            score.setCourseName(course.getCourseName());
            score.setTotalHours(course.getTotalHours()+"");
            score.setCredit(course.getCredit()+"");
            score.setSemester(course.getSemester());
            score.setCourseNameEn(course.getCourseNameEn());
        }
        // 3. 关联查询课程名称
        if (!scores.isEmpty()) {
            Set<String> courseCodes = scores.stream()
                    .map(Score::getCourseCode)
                    .filter(Objects::nonNull)  // 过滤掉null值
                    .collect(Collectors.toSet());

            if (!courseCodes.isEmpty()) {
                Map<String, String> courseNameMap = courseService.listByCourseCodes(courseCodes).stream()
                        .collect(Collectors.toMap(
                                Course::getCourseCode,
                                Course::getCourseName,
                                (existing, replacement) -> existing
                        ));

                scores.forEach(score ->
                        score.setCourseName(courseNameMap.get(score.getCourseCode()))
                );
            }
        }

        return R.success(scores);
    }

    /**
     * 更新成绩记录
     * 请求地址: PUT /api/score/update
     *
     * @param score 成绩记录实体（需包含 id）
     * @return 更新后的成绩记录
     */

    @PutMapping("/update")
    public R<Score> updateScore(@RequestBody Score score) {
        // 1. 先查询出原来的成绩记录
        Score originalScore = scoreService.getById(score.getId());
        if (originalScore == null) {
            return R.error("未找到要更新的成绩记录");
        }
        // 2. 将原来的score_value值设置到score_modify字段
        originalScore.setScoreModify(score.getScoreValue());
        originalScore.setAuditStatus(score.getAuditStatus());
        originalScore.setMessage(score.getMessage());
        // 3. 执行更新
        boolean updated = scoreService.updateById(originalScore);
        if (updated) {
            return R.success(score);
        } else {
            return R.error("更新成绩失败");
        }
    }
    // 在ScoreController中添加
    @GetMapping("/checkDuplicate")
    public R<Boolean> checkDuplicate(
            @RequestParam String studentNumber,
            @RequestParam String courseCode,
            @RequestParam String scoreType,
            @RequestParam String examType) {

        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentNumber, studentNumber)
                .eq(Score::getCourseCode, courseCode)
                .eq(Score::getScoreType, scoreType)
                .eq(Score::getExamType, examType);
        return R.success(scoreService.count(wrapper) > 0);
    }

    @PostMapping("/modify")
    public R<Score> modifyScore(@RequestBody Score score) {
        if (score.getId() == null) {
            return R.error("ID不能为空");
        }
        boolean updated = scoreService.updateById(score);
        return updated ? R.success(score) : R.error("修改失败");
    }
    // ScoreController.java

    @GetMapping("/find")
    public R<Score> findScore(
            @RequestParam String studentNumber,
            @RequestParam String courseCode,
            @RequestParam String scoreType,
            @RequestParam String examType) {

        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentNumber, studentNumber)
                .eq(Score::getCourseCode, courseCode)
                .eq(Score::getScoreType, scoreType)
                .eq(Score::getExamType, examType);

        Score score = scoreService.getOne(wrapper);
        if (score == null) {
            return R.error("未找到匹配记录");
        }
        return R.success(score);
    }
    @PutMapping("/approve")
    public R<String> auditScore(
            @RequestParam Long id,
            @RequestParam(required = false) String auditRemark,
            @RequestParam String userType) { // 新增userType参数

        // 1. 查询原记录
        Score originalScore = scoreService.getById(id);
        if (originalScore == null) {
            return R.error("未找到对应成绩记录");
        }

        // 2. 根据用户角色确定新状态
        String newStatus;
        Score score = new Score();
        switch(userType) {
            case "教务老师":
                newStatus = "approved"; // 教务审批通过
                score.setAuditRemark(auditRemark);
                break;
            case "项目主任":
                if (!"approved".equals(originalScore.getAuditStatus())) {
                    return R.error("请等待教务老师先审批");
                }
                score.setAuditZhuren(auditRemark);
                newStatus = "approved1"; // 项目主任审批通过
                break;
            case "分管院长":
                if (!"approved1".equals(originalScore.getAuditStatus())) {
                    return R.error("请等待项目主任先审批");
                }
                newStatus = "approved2"; // 最终审批通过
                score.setAuditYuanzhang(auditRemark);
                break;
            default:
                return R.error("未知用户角色");
        }

        // 3. 创建更新对象

        score.setId(id);
        score.setAuditStatus(newStatus);


        // 4. 如果是最终审批，更新成绩
        if ("approved2".equals(newStatus) && originalScore.getScoreModify() != null) {
            score.setScoreValue(originalScore.getScoreModify());
        }

        // 5. 执行更新
        boolean updated = scoreService.updateById(score);
        return updated ? R.success("审核状态已更新") : R.error("更新失败");
    }
    @PutMapping("/reject")
    public R<String> rejectScore(
            @RequestParam Long id,
            @RequestParam(required = false) String auditRemark
    , @RequestParam String userType
    ) {
System.out.println(id+"*******************************");
        // 1. 获取原始记录
        Score originalScore = scoreService.getById(id);
        if (originalScore == null) {
            return R.error("未找到成绩记录");
        }

        // 2. 准备更新数据
        Score score = new Score();
        score.setId(id);
        score.setAuditStatus("rejected");
        score.setAuditRemark(auditRemark);
        boolean updated = scoreService.updateById(score);
        return updated ? R.success("已拒绝") : R.error("更新失败");
    }
    /**
     * 根据 id 查询成绩记录
     * 请求地址: GET /api/score/get/{id}
     *
     * @param id 成绩记录 id
     * @return 查询到的成绩记录
     */
    @GetMapping("/get/{id}")
    public R<Score> getScore(@PathVariable Long id) {
        Score score = scoreService.getById(id);
        if (score != null) {
            return R.success(score);
        } else {
            return R.error("成绩信息不存在");
        }
    }
    @GetMapping("/page")
    public R<Page<Score>> getScorePage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String class1,
            @RequestParam(required = false) String scoreType,  // 新增：成绩类型
            @RequestParam(required = false) String examType) { // 新增：考试类型

        Page<Score> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();

        // 1. 处理 studentNumber 精确匹配
        if (StringUtils.isNotBlank(studentNumber)) {
            queryWrapper.eq(Score::getStudentNumber, studentNumber);
        }

        // 2. 处理学生相关条件（姓名/年级/班级）
        if (StringUtils.isNotBlank(studentName) || StringUtils.isNotBlank(grade) || StringUtils.isNotBlank(class1)) {
            List<String> studentNumbers = getStudentNumbersByConditions(studentName, grade, class1);
            if (studentNumbers.isEmpty()) return R.success(new Page<>());
            queryWrapper.in(Score::getStudentNumber, studentNumbers);
        }

        // 3. 处理课程名称模糊查询
        if (StringUtils.isNotBlank(courseName)) {
            List<String> courseCodes = getCourseCodesByName(courseName);
            if (courseCodes.isEmpty()) return R.success(new Page<>());
            queryWrapper.in(Score::getCourseCode, courseCodes);
        }

        // 4. 新增：处理成绩类型条件
        if (StringUtils.isNotBlank(scoreType)) {
            queryWrapper.eq(Score::getScoreType, scoreType);
        }

        // 5. 新增：处理考试类型条件（仅当成绩类型为exam时有效）
        if (StringUtils.isNotBlank(examType)) {
            queryWrapper.eq(Score::getExamType, examType);
        }

        // 执行分页查询
        Page<Score> result = scoreService.page(pageInfo, queryWrapper);

        // 关联查询并补充信息
        enrichScoreRecords(result.getRecords());

        return R.success(result);
    }
    private List<String> getStudentNumbersByConditions(String name, String grade, String class1) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(StudentInfo::getName, name);
        }
        if (StringUtils.isNotBlank(grade)) {
            wrapper.eq(StudentInfo::getGrade, grade);
        }
        if (StringUtils.isNotBlank(class1)) {
            wrapper.eq(StudentInfo::getClass1, class1);
        }
        return studentService.list(wrapper)
                .stream()
                .map(StudentInfo::getStudentNumber)
                .collect(Collectors.toList());
    }

    @Autowired
    StudentInfoService studentService;
    private List<String> getStudentNumbersByName(String studentName) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StudentInfo::getName, studentName); // 模糊查询
        return studentService.list(wrapper)
                .stream()
                .map(StudentInfo::getStudentNumber)
                .collect(Collectors.toList());
    }
    private List<String> getCourseCodesByName(String courseName) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Course::getCourseName, courseName); // 模糊查询
        return courseService.list(wrapper)
                .stream()
                .map(Course::getCourseCode)
                .collect(Collectors.toList());
    }
    private void enrichScoreRecords(List<Score> records) {
        if (records == null || records.isEmpty()) return;

        Set<String> studentNumbers = records.stream()
                .map(Score::getStudentNumber)
                .collect(Collectors.toSet());
        Set<String> courseCodes = records.stream()
                .map(Score::getCourseCode)
                .collect(Collectors.toSet());

        // 批量查询学生信息（包含新增字段）
        Map<String, StudentInfo> studentInfoMap = studentService.listByStudentNumbers(studentNumbers)
                .stream()
                .collect(Collectors.toMap(
                        StudentInfo::getStudentNumber,
                        Function.identity(),
                        (existing, replacement) -> existing));

        // 批量查询课程信息
        Map<String, String> courseNameMap = courseService.listByCourseCodes(courseCodes)
                .stream()
                .collect(Collectors.toMap(
                        Course::getCourseCode,
                        Course::getCourseName,
                        (existing, replacement) -> existing));

        // 填充数据到Score对象
        records.forEach(score -> {
            StudentInfo student = studentInfoMap.get(score.getStudentNumber());
            if (student != null) {
                score.setStudentName(student.getName());
                score.setGrade(student.getGrade());    // 新增字段
                score.setClass1(student.getClass1());  // 新增字段
            }
            score.setCourseName(courseNameMap.get(score.getCourseCode()));
        });
    }
    private String getCourseNameById(Long courseId) {
        Course course = courseService.getById(courseId);
        return course != null ? course.getCourseName() : "未知课程";
    }
    private Long getCourseIdByName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            return null;
        }
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_name", courseName);
        Course course = courseService.getOne(queryWrapper);
        return course != null ? course.getId() : null;
    }
    @Data
    public class ScoreExportVO {
        private String studentNumber;  // 学号
        private String studentName;    // 学生姓名
        private String courseCode;    // 课程代码
        private String courseName;    // 课程名称
        private Double scoreValue;     // 成绩
        private String examType;
        private String scoreType;
    }

    @GetMapping("/exportExcel")
    public void exportExcel(
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String courseName,
            HttpServletResponse response) throws IOException {

        // 1. 获取数据并转换为VO
        List<ScoreExportVO> exportData = convertToExportVO(
                getAllScores(studentNumber, studentName, courseName)
        );

        // 2. 创建Excel工作簿（使用try-with-resources确保自动关闭）
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("成绩单");
            String[] headers = {"学号", "学生姓名", "课程号", "课程名称", "成绩","成绩类型","考试类型"};
            // 3. 创建带样式的表头
            createHeaderRow(workbook, sheet);

            // 4. 填充数据
            fillDataRows(sheet, exportData);

            // 5. 设置响应
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=score_export.xlsx");
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i); // 自动调整列宽
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000); // 额外增加宽度（单位：1/256字符宽度）
            }
            // 6. 写入响应流
            workbook.write(response.getOutputStream());
        }
    }

    // 数据转换方法（实际使用）
    private List<ScoreExportVO> convertToExportVO(List<Score> scores) {
        return scores.stream().map(score -> {
            ScoreExportVO vo = new ScoreExportVO();
            vo.setStudentNumber(score.getStudentNumber());
            vo.setStudentName(score.getStudentName());
            vo.setCourseCode(score.getCourseCode());
            vo.setCourseName(score.getCourseName());
            vo.setScoreValue(score.getScoreValue());
            vo.setScoreType(score.getScoreType()); // "exam"或"homework"
            vo.setExamType(score.getExamType());   // "initial"或"makeup"
            return vo;
        }).collect(Collectors.toList());
    }

    // 创建表头（带样式）
    private void createHeaderRow(Workbook workbook, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"学号", "学生姓名", "课程号", "课程名称", "成绩", "成绩类型", "考试类型"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i); // 自动调整列宽
        }
    }
    private String mapScoreType(String scoreType) {
        if (scoreType == null) return "未知";
        switch (scoreType.toLowerCase()) {
            case "exam": return "考试成绩";
            case "homework": return "作业成绩";
            default: return scoreType;
        }
    }

    private String mapExamType(String examType) {
        if (examType == null) return "未知";
        switch (examType.toLowerCase()) {
            case "initial": return "初试";
            case "makeup": return "补考";
            default: return examType;
        }
    }
    // 填充数据行
    private void fillDataRows(Sheet sheet, List<ScoreExportVO> data) {
        int rowNum = 1;
        for (ScoreExportVO vo : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(vo.getStudentNumber());
            row.createCell(1).setCellValue(vo.getStudentName());
            row.createCell(2).setCellValue(vo.getCourseCode());
            row.createCell(3).setCellValue(vo.getCourseName());

            Cell scoreCell = row.createCell(4);
            if (vo.getScoreValue() != null) {
                scoreCell.setCellValue(vo.getScoreValue());
            } else {
                scoreCell.setCellValue("N/A");
            }
            row.createCell(5).setCellValue(mapScoreType(vo.getScoreType()));  // 成绩类型
            row.createCell(6).setCellValue(mapExamType(vo.getExamType()));    // 考试类型
        }
    }

    private List<Score> getAllScores(String studentNumber, String studentName, String courseName) {
        // 直接复用原有查询条件构建逻辑
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(studentNumber)) {
            queryWrapper.eq(Score::getStudentNumber, studentNumber);
        }
        if (StringUtils.isNotBlank(studentName)) {
            List<String> studentNumbers = getStudentNumbersByName(studentName);
            if (studentNumbers.isEmpty()) return Collections.emptyList();
            queryWrapper.in(Score::getStudentNumber, studentNumbers);
        }
        if (StringUtils.isNotBlank(courseName)) {
            List<String> courseCodes = getCourseCodesByName(courseName);
            if (courseCodes.isEmpty()) return Collections.emptyList();
            queryWrapper.in(Score::getCourseCode, courseCodes);
        }

        // 查询所有数据（不分页）
        List<Score> scores = scoreService.list(queryWrapper);
        enrichScoreRecords(scores); // 关联学生和课程名称
        return scores;
    }

    /**
     * 导出成绩 Excel
     * 请求地址: GET /api/score/export
     */
    @GetMapping("/export")
    public void exportScoreExcel(HttpServletResponse response) {
        try {
            // 查询所有成绩数据
            List<Score> scoreList = scoreService.list();

            // 创建 Excel 工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("成绩数据");

            // 创建表头（去掉 ID、createTime、updateTime）
            String[] headers = {"学号", "课程名称", "成绩", "考试类型", "审核状态", "审核意见", "教师姓名", "成绩类型"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            int rowIdx = 1;
            for (Score score : scoreList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(score.getStudentNumber());

                // 查询课程名称
                String courseName = getCourseNameById(score.getCourseId());
                row.createCell(1).setCellValue(courseName);

                row.createCell(2).setCellValue(score.getScoreValue());
                row.createCell(3).setCellValue(score.getExamType());
                row.createCell(4).setCellValue(score.getAuditStatus());
                row.createCell(5).setCellValue(score.getAuditRemark());
                row.createCell(6).setCellValue(score.getTeacherName());
                row.createCell(7).setCellValue(score.getScoreType());
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("成绩数据.xlsx", "UTF-8"));

            // 写入输出流
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/saveOrUpdateScores")
    public R<?> saveOrUpdateScores(@RequestBody ScoreBatchDTO request) {
        // 2. 处理三种成绩类型
        handleScore(request, "initial", request.getInitial(), "initial", "exam");
        handleScore(request, "makeup", request.getMakeup(), "makeup", "exam");
        handleScore(request, "homework", request.getHomework(), null, "homework");

        return R.success(request);
    }

    /**
     * 处理单个成绩类型
     * @param request     请求参数
     * @param examType    exam类型（exam/makeup/null）
     * @param scoreValue  分数值
     * @param scoreType   成绩类型（exam/homework）
     */
    private final ScoreMapper scoreMapper;
    private void handleScore(ScoreBatchDTO request, String fieldName, Double scoreValue,
                             String examType, String scoreType) {
        if (scoreValue == null) return;

        QueryWrapper<Score> wrapper = new QueryWrapper<Score>()
                .eq("student_number", request.getStudentNumber())
                .eq("course_id", request.getCourseId())
                .apply("COALESCE(exam_type, '') = COALESCE({0}, '')", examType)
                .apply("COALESCE(score_type, '') = COALESCE({0}, '')", scoreType);
        System.out.println( request.getStudentNumber());
        System.out.println(request.getCourseId());
        System.out.println( examType);
        System.out.println( scoreType);
        // 查询是否存在记录
        Score existingScore = scoreMapper.selectOne(wrapper);

        // 新增或更新
        Score score = new Score();
        score.setStudentNumber(request.getStudentNumber());
        score.setCourseId(request.getCourseId());
        score.setScoreValue(scoreValue);
        score.setExamType(examType);
        score.setScoreType(scoreType);
        score.setUpdateUser(Long.valueOf(request.getUpdateUser()));

        if (existingScore != null) {
            score.setId(existingScore.getId());
            scoreMapper.updateById(score);
        } else {
            score.setCreateUser(request.getCreateUser());
            scoreMapper.insert(score);
        }
    }
    /**
     * 导入成绩 Excel
     * 请求地址: POST /api/score/import
     * @param file 上传的 Excel 文件
     * @return 导入结果
     */
    @PostMapping("/import")
    public R<String> importScoreExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("上传文件不能为空");
        }
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<Score> scoreList = new ArrayList<>();

            // 读取数据（跳过表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Score score = new Score();
                score.setStudentNumber(row.getCell(0).getStringCellValue());

                // 课程名称 转换成 课程ID
                String courseName = row.getCell(1).getStringCellValue();
                Long courseId = getCourseIdByName(courseName);
                if (courseId == null) {
                    return R.error("导入失败：课程名称 " + courseName + " 不存在！");
                }
                score.setCourseId(courseId);

                score.setScoreValue(row.getCell(2).getNumericCellValue());
                score.setExamType(row.getCell(3).getStringCellValue());
                score.setAuditStatus(row.getCell(4).getStringCellValue());
                score.setAuditRemark(row.getCell(5).getStringCellValue());
                score.setTeacherName(row.getCell(6).getStringCellValue());
                score.setScoreType(row.getCell(7).getStringCellValue());

                scoreList.add(score);
            }

            // 批量插入数据库
            scoreService.saveBatch(scoreList);
            workbook.close();
            return R.success("成功导入 " + scoreList.size() + " 条成绩记录");
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件解析失败");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("数据格式错误，请检查 Excel 文件");
        }
    }










    @GetMapping("/exportFiltered")
    public void exportFilteredScores(@RequestParam(required = false) String studentNumber,
                                     @RequestParam(required = false) Long courseId,
                                     @RequestParam(required = false) String examType,
                                     @RequestParam(required = false) String auditStatus,
                                     HttpServletResponse response) {
        try {
            // 1️⃣ 按条件查询符合要求的成绩数据
            QueryWrapper<Score> queryWrapper = new QueryWrapper<>();

            if (studentNumber != null && !studentNumber.trim().isEmpty()) {
                queryWrapper.eq("student_number", studentNumber);
            }
            if (courseId != null) {
                queryWrapper.eq("course_id", courseId);
            }
            if (examType != null && !examType.trim().isEmpty()) {
                queryWrapper.eq("exam_type", examType);
            }
            if (auditStatus != null && !auditStatus.trim().isEmpty()) {
                queryWrapper.eq("audit_status", auditStatus);
            }

            List<Score> scoreList = scoreService.list(queryWrapper);

            // 2️⃣ 创建 Excel 工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("筛选成绩数据");

            // 3️⃣ 创建表头
            String[] headers = {"学号", "课程名称", "成绩", "考试类型", "审核状态", "审核意见", "教师姓名", "成绩类型"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 4️⃣ 填充数据
            int rowIdx = 1;
            for (Score score : scoreList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(score.getStudentNumber());

                // 查询课程名称
                String courseName = getCourseNameById(score.getCourseId());
                row.createCell(1).setCellValue(courseName);

                row.createCell(2).setCellValue(score.getScoreValue());
                row.createCell(3).setCellValue(score.getExamType());
                row.createCell(4).setCellValue(score.getAuditStatus());
                row.createCell(5).setCellValue(score.getAuditRemark());
                row.createCell(6).setCellValue(score.getTeacherName());
                row.createCell(7).setCellValue(score.getScoreType());
            }

            // 5️⃣ 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("筛选成绩数据.xlsx", "UTF-8"));

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
