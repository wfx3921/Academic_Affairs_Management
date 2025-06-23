package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.entity.StudentScoreDTO;
import com.ouc.aamanagement.mapper.ScoreMapper;
import com.ouc.aamanagement.mapper.StudentInfoMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReportService {
    // 常量定义
    private static final Pattern SEMESTER_PATTERN = Pattern.compile("第([一二三四五六])学期");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final Map<String, Integer> SPECIAL_COURSE_RULES =
            Collections.unmodifiableMap(new HashMap<String, Integer>() {{
                put("经济学导论", 50);  // 名称包含"体育"的课程50分及格
                put("理财综合课 ", 50);  // 名称包含"艺术"的课程50分及格
            }});
    private final StudentInfoMapper studentInfoMapper;

    private final ScoreMapper scoreMapper;

    public XWPFDocument generateReport(String studentNumber) throws Exception {
        // 获取学生基本信息（包含SCN号）
        StudentInfo student = getStudentInfo(studentNumber);

        // 获取课程成绩并按学期分组
        Map<Integer, List<StudentScoreDTO>> semesterMap = getScoresGroupedBySemester(studentNumber);

        // 从模板生成文档
        try (InputStream is = new ClassPathResource("score_template.doc").getInputStream()) {
            XWPFDocument doc = new XWPFDocument(is);
            fillStudentInfo(doc, student);
            fillScores(doc, semesterMap, student.getOpenDayTime());
            return doc;
        }
    }
    private void fillScores(XWPFDocument doc, Map<Integer, List<StudentScoreDTO>> semesterMap, Date openDay) {
        // 1. 计算学期日期范围
        Map<Integer, String> semesterDates = calculateSemesterDates(openDay);
        Integer currentSemester = null;

        // 2. 遍历所有表格
        for (XWPFTable table : doc.getTables()) {
            // 3. 找到"第一学期"行的位置
            int firstSemesterIndex = -1;
            for (int i = 0; i < table.getRows().size(); i++) {
                if (isFirstSemesterRow(table.getRow(i))) {
                    firstSemesterIndex = i;
                    break;
                }
            }
            if (firstSemesterIndex == -1) continue; // 没找到则跳过

            // 4. 从"第一学期"开始处理后续行
            Map<Integer, SemesterRows> semesterData = new LinkedHashMap<>();
            List<XWPFTableRow> currentSemesterRows = null;

            for (int i = firstSemesterIndex; i < table.getRows().size(); i++) {
                XWPFTableRow row = table.getRow(i);
                if (isSemesterRow(row)) {
                    currentSemester = chineseNumberToInt(getSemesterNumber(row));
                    if (currentSemester >= 1 && currentSemester <= 6) {
                        currentSemesterRows = new ArrayList<>();
                        semesterData.put(currentSemester, new SemesterRows(row, currentSemesterRows));
                        // 修改点：添加学期日期更新逻辑（与fillSimpleScores一致）
                        updateSemesterDate(row, semesterDates.get(currentSemester));
                    }
                } else if (currentSemesterRows != null) {
                    currentSemesterRows.add(row);
                }
            }

            // 5. 填充预留行并标记使用情况（保持不变）
            Set<XWPFTableRow> usedRows = new HashSet<>();
            for (Map.Entry<Integer, SemesterRows> entry : semesterData.entrySet()) {
                int semester = entry.getKey();
                SemesterRows semesterRows = entry.getValue();
                usedRows.add(semesterRows.titleRow);
                List<StudentScoreDTO> scores = semesterMap.getOrDefault(semester, Collections.emptyList());
                for (int i = 0; i < scores.size() && i < semesterRows.dataRows.size(); i++) {
                    fillCourseRow(semesterRows.dataRows.get(i), scores.get(i));
                    usedRows.add(semesterRows.dataRows.get(i));
                }
            }

            // 6. 删除未使用的预留行（从后往前处理）（保持不变）
            for (int i = table.getRows().size() - 1; i > firstSemesterIndex; i--) {
                XWPFTableRow row = table.getRow(i);
                if (!usedRows.contains(row) && !isSemesterRow(row)) {
                    table.removeRow(i);
                }
            }
        }
    }
    private boolean isSemesterRow(XWPFTableRow row) {
        String cellText = getCellText(row, 0);
        return cellText != null && SEMESTER_PATTERN.matcher(cellText).find();
    }
    // 判断是否是"第一学期"行
    private boolean isFirstSemesterRow(XWPFTableRow row) {
        String cellText = getCellText(row, 0);
        return cellText != null && cellText.contains("第一学期");
    }

    // 辅助类：存储学期行信息
    private static class SemesterRows {
        final XWPFTableRow titleRow;
        final List<XWPFTableRow> dataRows;

        SemesterRows(XWPFTableRow titleRow, List<XWPFTableRow> dataRows) {
            this.titleRow = titleRow;
            this.dataRows = dataRows;
        }
    }
    private String getSemesterNumber(XWPFTableRow row) {
        String cellText = getCellText(row, 0);
        Matcher matcher = SEMESTER_PATTERN.matcher(cellText);
        return matcher.find() ? matcher.group(1) : "0";
    }

    // 填充单行课程数据
    private void fillCourseRow(XWPFTableRow row, StudentScoreDTO score) {
        setCellValue(row, 0, score.getCourseName());    // 科目
        setCellValue(row, 1, score.getTotalHours());    // 学时
        setCellValue(row, 2, score.getCredit());        // 学分
        setCellValue(row, 3, score.getInitialScore() != null ?
                String.valueOf(score.getInitialScore()) : "暂无数据"); // 初修成绩
        setCellValue(row, 4, score.getMakeupScore() != null ?
                String.valueOf(score.getMakeupScore()) : "暂无数据");  // 补考成绩
    }
    private void fillCourseRow1(XWPFTableRow row, StudentScoreDTO score) {
        setCellValue(row, 0, score.getCourseName());    // 科目
        setCellValue(row, 1, score.getTotalHours());    // 学时
        setCellValue(row, 2, score.getCredit());        // 学分
    }
    public XWPFDocument generateReport1(String studentNumber) throws Exception {
        StudentInfo student = getStudentInfo(studentNumber);
        Map<Integer, List<StudentScoreDTO>> semesterMap = getScoresGroupedBySemester(studentNumber);
        try (InputStream is = new ClassPathResource("score_template1.doc").getInputStream()) {
            XWPFDocument doc = new XWPFDocument(is);
            fillStudentInfo1(doc, student);
            fillSimpleScores(doc, semesterMap, student.getOpenDayTime());
            return doc;
        }
    }
    private void fillSimpleScores(XWPFDocument doc, Map<Integer, List<StudentScoreDTO>> semesterMap, Date openDay) {
        // 1. 计算学期日期范围
        Map<Integer, String> semesterDates = calculateSemesterDates(openDay);
        Integer currentSemester = null;

        // 2. 遍历所有表格
        for (XWPFTable table : doc.getTables()) {
            // 3. 找到"第一学期"行的位置
            int firstSemesterIndex = -1;
            for (int i = 0; i < table.getRows().size(); i++) {
                if (isFirstSemesterRow(table.getRow(i))) {
                    firstSemesterIndex = i;
                    break;
                }
            }
            if (firstSemesterIndex == -1) continue; // 没找到则跳过

            // 4. 从"第一学期"开始处理后续行
            Map<Integer, SemesterRows> semesterData = new LinkedHashMap<>();
            List<XWPFTableRow> currentSemesterRows = null;

            for (int i = firstSemesterIndex; i < table.getRows().size(); i++) {
                XWPFTableRow row = table.getRow(i);
                if (isSemesterRow(row)) {
                    currentSemester = chineseNumberToInt(getSemesterNumber(row));
                    if (currentSemester >= 1 && currentSemester <= 6) {
                        currentSemesterRows = new ArrayList<>();
                        semesterData.put(currentSemester, new SemesterRows(row, currentSemesterRows));

                        // 修改点：添加学期日期更新逻辑（与fillSimpleScores一致）
                        updateSemesterDate(row, semesterDates.get(currentSemester));
                    }
                } else if (currentSemesterRows != null) {
                    currentSemesterRows.add(row);
                }
            }
            // 5. 填充预留行并标记使用情况（保持不变）
            Set<XWPFTableRow> usedRows = new HashSet<>();
            for (Map.Entry<Integer, SemesterRows> entry : semesterData.entrySet()) {
                int semester = entry.getKey();
                SemesterRows semesterRows = entry.getValue();
                usedRows.add(semesterRows.titleRow);
                List<StudentScoreDTO> scores = semesterMap.getOrDefault(semester, Collections.emptyList());
                for (int i = 0; i < scores.size() && i < semesterRows.dataRows.size(); i++) {
                    setResultCell(semesterRows.dataRows.get(i),3, scores.get(i));
                    fillCourseRow1(semesterRows.dataRows.get(i), scores.get(i));
                    usedRows.add(semesterRows.dataRows.get(i));
                }
            }

            // 6. 删除未使用的预留行（从后往前处理）（保持不变）
            for (int i = table.getRows().size() - 1; i > firstSemesterIndex; i--) {
                XWPFTableRow row = table.getRow(i);
                if (!usedRows.contains(row) && !isSemesterRow(row)) {
                    table.removeRow(i);
                }
            }
        }
    }

    // 新增判定结果设置方法
    private void setResultCell(XWPFTableRow row, int colIndex, StudentScoreDTO score) {
        if (score == null) {
            setCellValue(row, colIndex, "暂无数据");
        } else {
            String result = calculateResult(score);
            setCellValue(row, colIndex, result);
        }
    }

    private String calculateResult(StudentScoreDTO score) {
        // 获取适用的及格线
        int passingScore = getPassingScore(score.getCourseName());

        // 计算最高分
        Integer maxScore = Stream.of(score.getInitialScore(), score.getMakeupScore())
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);

        return maxScore >= passingScore ? "PASS" : "FAIL";
    }
    private StudentInfo getStudentInfo(String studentNumber) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getStudentNumber, studentNumber);
        StudentInfo student = studentInfoMapper.selectOne(wrapper);
        if (student == null) {
            System.err.println("错误: 学生信息不存在: " + studentNumber);
            throw new IllegalArgumentException("学生信息不存在: " + studentNumber);
        }
        return student;
    }
    // 根据课程名获取及格线
    private int getPassingScore(String courseName) {
        return SPECIAL_COURSE_RULES.entrySet().stream()
                .filter(entry -> courseName.contains(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(60); // 默认60分及格
    }
    private Map<Integer, List<StudentScoreDTO>> getScoresGroupedBySemester(String studentNumber) {
        List<StudentScoreDTO> scores = scoreMapper.selectScoresByStudent(studentNumber);
        Map<Integer, List<StudentScoreDTO>> result = scores.stream().collect(Collectors.groupingBy(StudentScoreDTO::getSemester));

        return result;
    }

    private void fillStudentInfo(XWPFDocument doc, StudentInfo student) {
        // 处理段落中的占位符
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            replaceInParagraph(paragraph, student);
        }

        // 处理表格中的占位符
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceInParagraph(paragraph, student);
                    }
                }
            }
        }
    }
    private void fillStudentInfo1(XWPFDocument doc, StudentInfo student) {
        // 处理段落中的占位符
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            replaceInParagraph1(paragraph, student);
        }

        // 处理表格中的占位符
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceInParagraph1(paragraph, student);
                    }
                }
            }
        }
    }
    private void replaceInParagraph(XWPFParagraph paragraph, StudentInfo student) {
        String text = paragraph.getText();
        if (text == null || text.isEmpty()) {
            return;
        }
        // 定义占位符映射
        Map<String, String> replacements = new HashMap<>();
        replacements.put("${scnNumber}", student.getScnNumber());
        replacements.put("${studentName}", student.getName());
        replacements.put("${birthDate}", formatDate(student.getBirthDate()));
        replacements.put("${gender}", student.getGender());
        replacements.put("${gender1}", "男".equals(student.getGender()) ? "male" : "female");
        replacements.put("${major}", student.getMajor());
        replacements.put("${attendanceDate}", formatDate(student.getOpenDayTime()));

        // 替换占位符
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            if (text.contains(entry.getKey())) {
                text = text.replace(entry.getKey(), entry.getValue());
                // 清空原有内容
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setText("", 0);
                }
                //新内容
                XWPFRun run = paragraph.createRun();
                run.setText(text);
                break;
            }
        }
    }
    private void replaceInParagraph1(XWPFParagraph paragraph, StudentInfo student) {
        String text = paragraph.getText();
        if (text == null || text.isEmpty()) {
            return;
        }
        // 定义占位符映射
        Map<String, String> replacements = new HashMap<>();
        replacements.put("${scnNumber}", student.getScnNumber());
        replacements.put("${studentName}", student.getName());
        replacements.put("${birthDate}", formatDate(student.getBirthDate()));
        replacements.put("${gender}", student.getGender());
        replacements.put("${gender1}", "男".equals(student.getGender()) ? "male" : "female");
        replacements.put("${major}", student.getMajor());
        replacements.put("${attendanceDate}", formatDate(student.getOpenDayTime()));
        // 替换占位符
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            if (text.contains(entry.getKey())) {
                text = text.replace(entry.getKey(), entry.getValue());
                // 清空原有内容
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setText("", 0);
                }
                //新内容
                XWPFRun run = paragraph.createRun();
                run.setText(text);
                break;
            }
        }
    }

    private void updateSemesterDate(XWPFTableRow row, String dateRange) {
        if (row == null || dateRange == null) {
            return;
        }
        try {
            int lastCellIndex = row.getTableCells().size() - 1;
            XWPFTableCell dateCell = row.getCell(lastCellIndex);
            if (dateCell != null) {
                String originalText = dateCell.getText();
                String newText = originalText.replaceFirst("\\d{2}/[A-Za-z]{3}/\\d{4}-\\d{2}/[A-Za-z]{3}/\\d{4}", dateRange);
                dateCell.removeParagraph(0);
                XWPFParagraph paragraph = dateCell.addParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(newText);
                run.setFontSize(12);
            } else {
            }
        } catch (Exception e) {
            System.err.println("更新学期日期失败: " + e.getMessage());
            throw new RuntimeException("更新学期日期失败: " + e.getMessage(), e);
        }
    }


    private boolean isCourseMatch(String dbCourse, String templateCourse) {
        // 统一清洗规则：移除所有空格、括号、横线等非字母/中文的字符
        String cleanedDb = dbCourse.replaceAll("[\\s·()-]", "").replaceAll("[^\\u4e00-\\u9fa5a-zA-Z]", "");
        String cleanedTemplate = templateCourse.replaceAll("[\\s()]", "").replaceAll("[^\\u4e00-\\u9fa5a-zA-Z]", "");
        boolean result = cleanedDb.equalsIgnoreCase(cleanedTemplate);
        System.out.printf("课程匹配: 数据库[%s] vs 模板[%s] => %s (清洗后: %s vs %s)%n",
                dbCourse, templateCourse, result, cleanedDb, cleanedTemplate);
        return result;
    }

    private Map<Integer, String> calculateSemesterDates(Date openDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(openDay);
        int startYear = cal.get(Calendar.YEAR);
        Map<Integer, String> dates = new LinkedHashMap<>();
        for (int semester = 1; semester <= 6; semester++) {
            // 计算学年显示
            int displayYear1, displayYear2;
            // 奇数学期：1, 3, 5 学期
            if (semester == 1) {
                // 当前学期对应的起始年份（根据学期编号设置）
                displayYear1 = startYear ;
                displayYear2 = startYear + 1;
            } else if(semester == 2) {
                // 偶数学期：2, 4, 6 学期
                displayYear1 = startYear + 1;
                displayYear2 = startYear + 1;
            }else if(semester == 3) {
                // 偶数学期：2, 4, 6 学期
                displayYear1 = startYear + 1;
                displayYear2 = startYear + 2;
            }
            else if(semester == 4) {
                // 偶数学期：2, 4, 6 学期
                displayYear1 = startYear + 2;
                displayYear2 = startYear + 2;
            }
            else if(semester == 5) {
                // 偶数学期：2, 4, 6 学期
                displayYear1 = startYear + 2;
                displayYear2 = startYear + 3;
            }
            else  {
                // 偶数学期：2, 4, 6 学期
                displayYear1 = startYear + 3;
                displayYear2 = startYear + 3;
            }
            // 设置具体日期
            if (semester % 2 == 1) {
                // 奇数学期：9月-次年1月
                String dateStr = String.format("01/Sep/%d-15/Jan/%d", displayYear1, displayYear2);
                dates.put(semester, dateStr);
                System.out.println("第" + semester + "学期: " + dateStr); // 打印奇数学期
            } else {
                // 偶数学期：2月-7月（第6学期到5月）
                String endDate = (semester == 6) ? "15/May" : "05/July";
                String dateStr = String.format("20/Feb/%d-%s/%d", displayYear1, endDate, displayYear2);
                dates.put(semester, dateStr);
                System.out.println("第" + semester + "学期: " + dateStr); // 打印偶数学期
            }
        }
        return dates;
    }

    private String getCellText(XWPFTableRow row, int cellIndex) {
        XWPFTableCell cell = row.getCell(cellIndex);
        String text = (cell != null) ? cell.getText().trim() : "";
        return text;
    }
    private void setCellValue(XWPFTableRow row, int cellIndex, Object value) {
        String textValue = (value != null) ? value.toString() : "/";
        XWPFTableCell cell = row.getCell(cellIndex);
        if (cell != null) {
            cell.removeParagraph(0);
            cell.addParagraph().createRun().setText(textValue);
        }
    }
    private int chineseNumberToInt(String chineseNum) {
        int result;
        switch (chineseNum) {
            case "一":
                result = 1;
                break;
            case "二":
                result = 2;
                break;
            case "三":
                result = 3;
                break;
            case "四":
                result = 4;
                break;
            case "五":
                result = 5;
                break;
            case "六":
                result = 6;
                break;
            default:
                result = 0;
        }
        return result;
    }

    private String formatDate(Date date) {
        String result = (date != null) ? DATE_FORMAT.format(date) : "";
        return result;
    }
}