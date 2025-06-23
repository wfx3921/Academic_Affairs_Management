package com.ouc.aamanagement.controller;

import com.ouc.aamanagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("api/report")
    public void generateReport(@RequestParam String studentNumber, HttpServletResponse response) throws Exception {
        XWPFDocument doc = reportService.generateReport(studentNumber);
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=report.docx");
        doc.write(response.getOutputStream());
    }
    @GetMapping("api/report1")
    public void generateReport1(@RequestParam String studentNumber, HttpServletResponse response) throws Exception {
        XWPFDocument doc = reportService.generateReport1(studentNumber);
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=report.docx");
        doc.write(response.getOutputStream());
    }
}