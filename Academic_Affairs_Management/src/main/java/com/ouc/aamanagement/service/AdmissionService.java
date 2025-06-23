package com.ouc.aamanagement.service;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.mapper.StudentApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.Random;

@Service
public class AdmissionService {

    @Autowired
    private StudentApplicationMapper studentApplicationMapper;

    public byte[] generateAdmissionLetter(Long studentId) throws IOException {
        // 1. 直接查询学生姓名（简化查询）
        StudentApplication student = studentApplicationMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("找不到ID为 " + studentId + " 的学生");
        }
        String studentName = student.getName(); // 只获取name字段

        // 2. 准备PDF文档
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfReader reader = new PdfReader(new ClassPathResource("templates/录取通知书.pdf").getInputStream());
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {

            // 3. 配置中文字体
            PdfFont chineseFont = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");

            // 4. 获取并填充PDF表单
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            fillFormFields(form, studentName, chineseFont);

            // 5. 扁平化表单
            form.flattenFields();
            return outputStream.toByteArray();
        }
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