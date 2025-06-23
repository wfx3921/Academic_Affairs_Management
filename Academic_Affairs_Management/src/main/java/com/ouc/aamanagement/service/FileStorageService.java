package com.ouc.aamanagement.service;

import java.io.IOException; // 使用标准库中的 IOException
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String studentName) throws IOException { // 修改异常类型
        // 1. 创建存放目录
        Path dirPath = Paths.get(uploadDir, "diplomas");
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 2. 生成安全文件名（替换特殊字符）
        String safeName = studentName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = String.format("%s_%d.%s", safeName, System.currentTimeMillis(), fileExt);

        // 3. 保存文件
        Path targetPath = dirPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 返回相对路径（如：/uploads/diplomas/张三_123.pdf）
        return "C:/uploads/diplomas/" + fileName;
    }
}
