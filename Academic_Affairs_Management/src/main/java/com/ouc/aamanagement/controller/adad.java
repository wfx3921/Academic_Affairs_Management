package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StudentApplicationRequest;
import com.ouc.aamanagement.entity.User;
import com.ouc.aamanagement.mapper.UserMapper;
import com.ouc.aamanagement.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/student-applications")
public class adad {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @PostMapping("/upload-template")
    public R<String> uploadTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam String templateType) throws IOException {  // 接收模板类型参数

        // 1. 根据类型决定保存的文件名
        String filename;
        if ("current".equals(templateType)) {
            filename = "在读证明.docx";       // 在读模板
        } else if ("graduated".equals(templateType)) {
            filename = "在读证明_已毕业.docx";      // 已毕业模板
        } else {
            return R.error("无效的模板类型");
        }

        // 2. 指定模板存放目录
        String templateDir = "C:/templates/";

        // 3. 确保目录存在
        File dir = new File(templateDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4. 保存文件（覆盖原有）
        File dest = new File(templateDir + filename);
        file.transferTo(dest);

        return R.success(filename + "模板更新成功");
    }
    @GetMapping("/adminUsers")
    public R<List<UserPermissionVO>> getAdminUsers() {
        // 使用lambdaQuery确保字段名正确映射
        List<User> users = userService.lambdaQuery()
                .select(
                        User::getUserId,
                        User::getUserName,
                        User::getPermission,
                        User::getMessage  // 新增此行
                )
                .eq(User::getUserType, "任课老师")
                .list();

        // 转换为前端需要的VO对象
        List<UserPermissionVO> result = users.stream()
                .map(user -> new UserPermissionVO(
                        user.getUserId(),
                        user.getUserName(),
                        user.getMessage(),
                        user.getPermission()
                ))
                .collect(Collectors.toList());

        return R.success(result);
    }

    // 定义VO类
    @Data
    @AllArgsConstructor
    public static class UserPermissionVO {
        private Long user_id;
        private String user_name;
        private String message;
        private Integer p;


    }
    @PostMapping(path = "/generate", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<Resource> generateDocx(@RequestBody StudentRequest request) throws Exception {
        String externalTemplateDir = "C:/templates/";
        // 日期格式化
        DateTimeFormatter birthDateFormatter = DateTimeFormatter.ofPattern("MMM. dd yyyy", Locale.US);
        DateTimeFormatter admissionDateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US);

        Map<String, String> replacements = new HashMap<>();

        // 安全地添加替换值（只添加非空值）
        addIfNotEmpty(replacements, "${name}", request.getName());
        addIfNotEmpty(replacements, "${gender}", request.getGender());
        addIfNotEmpty(replacements, "${major}", request.getMajor());

        // 安全处理日期
        try {
            if (request.getBirthDate() != null && !request.getBirthDate().isEmpty()) {
                LocalDate birthDate = LocalDate.parse(request.getBirthDate());
                replacements.put("${birthDate}", birthDate.format(birthDateFormatter));
            }
            if (request.getAdmissionDate() != null && !request.getAdmissionDate().isEmpty()) {
                LocalDate admissionDate = LocalDate.parse(request.getAdmissionDate());
                replacements.put("${admissionDate}", admissionDate.format(admissionDateFormatter));
            }
        } catch (DateTimeParseException e) {
            // 日期格式错误时跳过，不替换
            System.err.println("日期格式错误: " + e.getMessage());
        }

        // 当前日期总是有值
        replacements.put("${currentDate}", LocalDate.now().format(birthDateFormatter));

        String templatePath;
        String studentStatus = request.getStudentStatus();

        if (studentStatus != null) {
            switch (studentStatus) {
                case "正常":
                case "复学":
                case "延期毕业":
                case "留级":
                    templatePath = "在读证明.docx";  // 这些状态使用同一个模板
                    break;
                default:
                    templatePath = "在读证明_已毕业.docx";  // 其他状态使用另一个模板
                    break;
            }
        } else {
            templatePath = "在读证明_已毕业.docx";  // 默认模板
        }
        File templateFile = new File(externalTemplateDir + templatePath);
        XWPFDocument doc = new XWPFDocument(new FileInputStream(templateFile));

        // 替换文本
        replaceTextSafely(doc, replacements);

        // 输出到字节流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        doc.close();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=generated.docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(out.toByteArray()));
    }

    // 辅助方法：只添加非空值
    private void addIfNotEmpty(Map<String, String> map, String key, String value) {
        if (value != null && !value.isEmpty()) {
            map.put(key, value);
        }
    }

    // 安全的文本替换方法
    private void replaceTextSafely(XWPFDocument doc, Map<String, String> replacements) {
        // 替换段落文本
        for (XWPFParagraph p : doc.getParagraphs()) {
            for (XWPFRun r : p.getRuns()) {
                String text = r.getText(0);
                if (text != null) {
                    // 只替换map中存在的键
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        if (text.contains(entry.getKey())) {
                            text = text.replace(entry.getKey(), entry.getValue());
                        }
                    }
                    r.setText(text, 0);
                }
            }
        }

        // 替换表格中的文本
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null) {
                                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                                    if (text.contains(entry.getKey())) {
                                        text = text.replace(entry.getKey(), entry.getValue());
                                    }
                                }
                                r.setText(text, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    @Data
    public static class StudentRequest {
        private String studentStatus;
        private String name;
        private String gender;
        private String birthDate;
        private String admissionDate;
        private String major;
    }

}