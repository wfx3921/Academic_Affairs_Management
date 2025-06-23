package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.service.StudentApplicationService;
import com.ouc.aamanagement.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
@CrossOrigin
@RestController
@RequestMapping("/diploma")
public class HighSchoolDiplomaController {

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private StudentApplicationService studentApplicationService;
    /**
     * 上传高中毕业证照片
     * @param file 上传的照片文件
     * @param applicationId 学生报名记录的ID，用于关联 student_info 与 student_application
     * @return 上传结果（R.success(true)表示成功）
     */
    @Transactional
    @PostMapping("/upload")
    public R<Boolean> uploadDiploma(@RequestParam("file") MultipartFile file,
                                    @RequestParam("applicationId") Long applicationId) {
        if (file.isEmpty()) {
            return R.error("上传文件为空");
        }
        try {
            // 获取原始文件名并生成新文件名，保留文件扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            /**
             * 部署在服务器上的时候需要修改地址
             */
            // 定义文件保存目录，可根据实际需要进行调整
            String uploadDir = "E:/test/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 将文件保存到目标目录
            File dest = new File(uploadDir + newFileName);
            file.transferTo(dest);
            // 构造保存到数据库中的文件路径（可配置为访问 URL 或相对路径）
            String filePath = uploadDir + newFileName;

            // 更新 student_info 表中的毕业证信息
            StudentInfo studentInfo = studentInfoService.getOne(new QueryWrapper<StudentInfo>()
                    .eq("application_id", applicationId));
            if (studentInfo != null) {
                studentInfo.setHighSchoolDiploma(filePath);
                boolean infoResult = studentInfoService.updateById(studentInfo);
                if (!infoResult) {
                    throw new RuntimeException("更新学生个人信息中的毕业证信息失败");
                }
            } else {
                return R.error("未找到对应的学生个人信息");
            }

            // 更新 student_application 表中的毕业证信息
            StudentApplication studentApplication = studentApplicationService.getById(applicationId);
            if (studentApplication != null) {
                studentApplication.setHighSchoolDiploma(filePath);
                boolean appResult = studentApplicationService.updateStudentApplication(studentApplication);
                if (!appResult) {
                    throw new RuntimeException("更新学生报名信息中的毕业证信息失败");
                }
            } else {
                return R.error("未找到对应的学生报名信息");
            }

            return R.success(true);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件上传异常: " + e.getMessage());
        }
    }
    @Transactional
    @PostMapping("/upload1")
    public R<String> uploadDiploma1(@RequestParam("file") MultipartFile file,
                                   @RequestParam("applicationId") Long applicationId) {
        if (file.isEmpty()) {
            return R.error("上传文件为空");
        }
        try {
            // 获取原始文件名并生成新文件名，保留文件扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // 定义文件保存目录，可根据实际需要进行调整
            String uploadDir = "C:/uploads/diplomas/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                System.out.println("目录创建结果: " + created);
            }

            // 将文件保存到目标目录
            File dest = new File(uploadDir + newFileName);
            file.transferTo(dest);
            System.out.println("文件已保存到: " + dest.getAbsolutePath());

            // 构造保存到数据库中的文件路径（可配置为访问 URL 或相对路径）
            String filePath = uploadDir + newFileName;

            // 更新 student_info 表中的毕业证信息
            StudentInfo studentInfo = studentInfoService.getOne(new QueryWrapper<StudentInfo>()
                    .eq("application_id", applicationId));
            if (studentInfo != null) {
                studentInfo.setHighSchoolDiploma(filePath);
                boolean infoResult = studentInfoService.updateById(studentInfo);
                if (!infoResult) {
                    throw new RuntimeException("更新学生个人信息中的毕业证信息失败");
                }
            } else {
                return R.error("未找到对应的学生个人信息");
            }

            // 更新 student_application 表中的毕业证信息
            StudentApplication studentApplication = studentApplicationService.getById(applicationId);
            if (studentApplication != null) {
                studentApplication.setHighSchoolDiploma(filePath);
                boolean appResult = studentApplicationService.updateStudentApplication(studentApplication);
                if (!appResult) {
                    throw new RuntimeException("更新学生报名信息中的毕业证信息失败");
                }
            } else {
                return R.error("未找到对应的学生报名信息");
            }

            // 构造返回的文件 URL
            String fileUrl = "http://localhost:8080/uploads/diplomas/" + newFileName;
            return R.success(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return R.error("文件上传异常: " + e.getMessage());
        }
    }
}
