package com.ouc.aamanagement.utils;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import java.io.*;
import java.util.Map;
public class WordTemplateProcessor
{


    public static File generateWord(Map<String, String> data) throws Exception {
        // 1. 加载模板文件
        ClassPathResource resource = new ClassPathResource("templates/zaidu.docx");
        InputStream templateStream = resource.getInputStream();

        // 2. 读取Word文档
        XWPFDocument doc = new XWPFDocument(templateStream);

        // 3. 替换段落中的占位符
        for (XWPFParagraph p : doc.getParagraphs()) {
            replaceText(p, data);
        }

        // 4. 替换表格中的占位符
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        replaceText(p, data);
                    }
                }
            }
        }

        // 5. 保存临时文件
        File tempFile = File.createTempFile("generated-", ".docx");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            doc.write(out);
        }

        return tempFile;
    }

    private static void replaceText(XWPFParagraph p, Map<String, String> data) {
        String text = p.getText();
        if (text == null) return;

        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (text.contains("${" + entry.getKey() + "}")) {
                text = text.replace("${" + entry.getKey() + "}", entry.getValue());
                p.getRuns().get(0).setText(text, 0);
            }
        }
    }
}
