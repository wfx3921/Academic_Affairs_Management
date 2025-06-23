package com.ouc.aamanagement.controller;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.service.StudentApplicationService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
@CrossOrigin
@RestController
@RequestMapping("/api/admission-notice")
@RequiredArgsConstructor
public class AdmissionController {
    private final StudentApplicationService studentApplicationService;

    private static final String PDF_PATH = "C:/templates/录取通知书.pdf";

    @PostMapping("/replace")
    public ResponseEntity<Map<String, String>> replacePdf(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        try {
            // 检查文件类型
            if (!"application/pdf".equals(file.getContentType())) {
                response.put("message", "仅支持PDF文件");
                return ResponseEntity.badRequest().body(response);
            }

            // 确保目标目录存在
            Path targetDir = Paths.get("C:/templates");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 保存文件
            Path target = Paths.get(PDF_PATH);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            response.put("message", "文件替换成功");
            response.put("filename", file.getOriginalFilename());
            return ResponseEntity.ok().body(response);
        } catch (IOException e) {
            response.put("message", "文件替换失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/pdf")
    public ResponseEntity<Resource> getPdf() throws Exception {
        // 1. 定义PDF文件路径
        Path filePath = Paths.get("C:/templates/录取通知书.pdf").normalize();

        // 2. 创建Resource对象
        Resource resource = new UrlResource(filePath.toUri());

        // 3. 检查文件是否存在
        if (!resource.exists()) {
            throw new RuntimeException("PDF文件不存在");
        }

        // 4. 设置响应头
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"录取通知书.pdf\"")
                .body(resource);
    }
    @GetMapping("/download")
    public void downloadAdmission(@RequestParam Long studentId,
                                  HttpServletResponse response) throws Exception {
        // 1. 获取学生申请信息
        StudentApplication application = studentApplicationService.getById(studentId);
        // 2. 处理PDF模板（修改点：直接使用文件路径）
        String pdfTemplatePath = "C:/templates/录取通知书.pdf"; // 替换为你的实际文件路径
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfReader reader = new PdfReader(pdfTemplatePath); // 修改点：直接传入路径
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {

            // 3. 配置中文字体（以下代码保持不变）
            PdfFont chineseFont = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");

            // 4. 获取并填充PDF表单
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            fillFormFields(form, application.getName(), chineseFont);

            // 5. 扁平化表单
            form.flattenFields();
        }

        // 6. 设置响应（以下代码保持不变）
        response.setContentType("application/pdf");
        String filename = "录取通知书_" + application.getName() + ".pdf";
        response.setHeader("Content-Disposition", "inline; filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"");
        response.getOutputStream().write(baos.toByteArray());
    }

    private void fillFormFields(PdfAcroForm form, String studentName, PdfFont font) {
        // 生成录取编号
        String admissionNo = "OUCAD" + Year.now().getValue() + "09" +
                String.format("%03d", new Random().nextInt(1000));

        // 填充所有表单域
        setFieldValue(form, "ad", admissionNo, font);
        setFieldValue(form, "studentName", studentName, font);
        setFieldValue(form, "year", String.valueOf(Year.now().getValue()), font);
        setFieldValue(form, "month", "9", font);
        setFieldValue(form, "day", "30", font);
        setFieldValue(form, "year1", String.valueOf(Year.now().getValue()), font);
        setFieldValue(form, "month1", "6", font);
    }

    private void setFieldValue(PdfAcroForm form, String fieldName, String value, PdfFont font) {
        PdfFormField field = form.getField(fieldName);
        if (field != null) {
            field.setValue(value);
            field.setFont(font);
        }
    }
}