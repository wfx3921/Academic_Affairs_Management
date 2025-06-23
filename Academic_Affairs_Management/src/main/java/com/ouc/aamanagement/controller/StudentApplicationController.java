package com.ouc.aamanagement.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ouc.aamanagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;

import org.apache.poi.xwpf.usermodel.*;

import org.springframework.web.bind.annotation.*;

import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.service.StudentApplicationService;
import com.ouc.aamanagement.service.StudentInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;


@CrossOrigin
@RestController
@RequestMapping("/api/student-applications")
public class StudentApplicationController {

    @Autowired
    private StudentApplicationService studentApplicationService;

    @Autowired
    private StudentInfoService studentInfoService;


    @Autowired
    private FileStorageService fileStorageService;

    @Value("${file.upload-dir}")
    private String uploadDir;
    @GetMapping("/getByEmail")
    public R<StudentApplication> getStudentApplicationsByEmail(@RequestParam String email){
        LambdaQueryWrapper<StudentApplication> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StudentApplication::getEmail,email);
        StudentApplication studentApplication = studentApplicationService.getOne(lambdaQueryWrapper);
        if(studentApplication != null){
            return R.success(studentApplication);
        }
        else return R.error("没有查询到学生报名信息");
    }
    @GetMapping("/page6")
    public R<IPage<StudentApplication>> getStudentApplicationsPage6(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String studentStatus,
            @RequestParam(required = false) String registrationStatus) {

        // 构建分页参数
        Page<StudentApplication> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<StudentApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentApplication::getPass, 1);
        // 1. 如果有studentinfo表的查询条件，先获取匹配的application_id列表
        if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(gender) ||
                StringUtils.isNotBlank(country) || StringUtils.isNotBlank(studentStatus) ||
                StringUtils.isNotBlank(registrationStatus)) {
            // 构建studentinfo表的查询条件
            LambdaQueryWrapper<StudentInfo> studentInfoWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(name)) {
                studentInfoWrapper.like(StudentInfo::getName, name);
            }
            if (StringUtils.isNotBlank(gender)) {
                studentInfoWrapper.eq(StudentInfo::getGender, gender);
            }
            if (StringUtils.isNotBlank(country)) {
                studentInfoWrapper.like(StudentInfo::getAddressProvince, country);
            }
            // 获取匹配的application_id列表
            List<Long> applicationIds = studentInfoService.list(studentInfoWrapper)
                    .stream()
                    .map(StudentInfo::getApplicationId)
                    .distinct()
                    .collect(Collectors.toList());
            if (applicationIds.isEmpty()) {
                return R.success(new Page<>()); // 无匹配结果时返回空分页
            }
            queryWrapper.in(StudentApplication::getId, applicationIds);
        }

        // 2. 执行分页查询
        IPage<StudentApplication> studentPage = studentApplicationService.page(pageParam, queryWrapper);

        // 3. 关联查询studentinfo信息（可选）
        enrichStudentApplications(studentPage.getRecords());

        return R.success(studentPage);
    }
    @PutMapping("/{id}/payment")
    public R<String> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam Integer pay) {

        // 参数校验
        if (pay != 0 && pay != 1) {
            return R.error("参数错误：pay 必须是 0 或 1");
        }

        // 1. 先更新 student_application 表
        boolean appUpdated = studentApplicationService.lambdaUpdate()
                .eq(StudentApplication::getId, id)
                .set(StudentApplication::getPay, pay)
                .update();

        if (!appUpdated) {
            return R.error("更新申请记录失败");
        }

        // 2. 再更新关联的 student_info 表
        boolean infoUpdated = studentInfoService.lambdaUpdate()
                .eq(StudentInfo::getApplicationId, id)
                .set(StudentInfo::getPay, pay)  // 假设 student_info 表也有 pay 字段
                .update();

        return infoUpdated ? R.success("缴费状态更新成功") : R.error("更新学生信息失败");
    }
    /**
     * 分页查询招生信息
     */
    @GetMapping("/page")
    public R<IPage<StudentApplication>> getStudentApplicationsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String studentStatus,
            @RequestParam(required = false) String registrationStatus) {

        // 构建分页参数
        Page<StudentApplication> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<StudentApplication> queryWrapper = new LambdaQueryWrapper<>();

        // 1. 如果有studentinfo表的查询条件，先获取匹配的application_id列表
        if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(gender) ||
                StringUtils.isNotBlank(country) || StringUtils.isNotBlank(studentStatus) ||
                StringUtils.isNotBlank(registrationStatus)) {
            // 构建studentinfo表的查询条件
            LambdaQueryWrapper<StudentInfo> studentInfoWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(name)) {
                studentInfoWrapper.like(StudentInfo::getName, name);
            }
            if (StringUtils.isNotBlank(gender)) {
                studentInfoWrapper.eq(StudentInfo::getGender, gender);
            }
            if (StringUtils.isNotBlank(country)) {
                studentInfoWrapper.like(StudentInfo::getAddressProvince, country);
            }
            // 获取匹配的application_id列表
            List<Long> applicationIds = studentInfoService.list(studentInfoWrapper)
                    .stream()
                    .map(StudentInfo::getApplicationId)
                    .distinct()
                    .collect(Collectors.toList());

            if (applicationIds.isEmpty()) {
                return R.success(new Page<>()); // 无匹配结果时返回空分页
            }
            queryWrapper.in(StudentApplication::getId, applicationIds);
        }

        // 2. 执行分页查询
        IPage<StudentApplication> studentPage = studentApplicationService.page(pageParam, queryWrapper);

        // 3. 关联查询studentinfo信息（可选）
        enrichStudentApplications(studentPage.getRecords());

        return R.success(studentPage);
    }
    @GetMapping("/page1")
    public R<IPage<StudentInfo>> getStudentApplicationsPage1(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String studentStatus,
            @RequestParam(required = false) String registrationStatus,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String gradeId,
            @RequestParam(required = false) String classId
            ) {
        // 构建分页参数
        Page<StudentInfo> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<StudentInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(StudentInfo::getName, name);
        }
        if (StringUtils.isNotBlank(grade)) {
            queryWrapper.eq(StudentInfo::getGrade, grade);
        }
        if (StringUtils.isNotBlank(major)) {
            queryWrapper.eq(StudentInfo::getMajor, major);
        }
        if (StringUtils.isNotBlank(studentStatus)) {
            queryWrapper.eq(StudentInfo::getStudentStatus, studentStatus);
        }
        if (StringUtils.isNotBlank(registrationStatus)) {
            queryWrapper.eq(StudentInfo::getRegistrationStatus, registrationStatus);
        }
        if (StringUtils.isNotBlank(studentNumber)) {
            queryWrapper.eq(StudentInfo::getStudentNumber, studentNumber);
        }
        if (StringUtils.isNotBlank(gradeId)) {
            queryWrapper.eq(StudentInfo::getGrade, gradeId);
        }
        if (StringUtils.isNotBlank(classId)) {
            queryWrapper.eq(StudentInfo::getClass1, classId);
        }

        // 执行分页查询
        IPage<StudentInfo> studentPage = studentInfoService.page(pageParam, queryWrapper);

        return R.success(studentPage);
    }

    private void enrichStudentApplications(List<StudentApplication> applications) {
        if (CollectionUtils.isEmpty(applications)) return;

        // 获取所有关联的applicationId
        Set<Long> applicationIds = applications.stream()
                .map(StudentApplication::getId)
                .collect(Collectors.toSet());

        // 批量查询studentinfo信息
        Map<Long, StudentInfo> studentInfoMap = studentInfoService.listByApplicationIds(applicationIds)
                .stream()
                .collect(Collectors.toMap(
                        StudentInfo::getApplicationId,
                        Function.identity(),
                        (existing, replacement) -> existing));

        // 填充关联信息
        applications.forEach(app -> {
            StudentInfo info = studentInfoMap.get(app.getId());
            if (info != null) {
                app.setStudentInfo(info); // 假设StudentApplication中有studentInfo字段
            }
        });
    }
    /**
     * 根据 ID 查询招生信息
     */
    @GetMapping("/{id}")
    public R<StudentApplication> getStudentApplication(@PathVariable Long id) {
        StudentApplication studentApplication = studentApplicationService.getStudentApplicationById(id);
        if(studentApplication != null) {
            return R.success(studentApplication);
        } else {
            return R.error("没有查询到学生报名信息");
        }
    }

    /**
     * 新增招生信息，同时新增对应的学生个人信息
     */
    @Transactional
    @PostMapping
    public R<Boolean> saveStudentApplication(@RequestBody StudentApplication studentApplication) {
        // 保存学生报名信息
        boolean result = studentApplicationService.saveStudentApplication(studentApplication);
        if(result) {
            // 保存成功后，新建对应的学生个人信息记录
            StudentInfo studentInfo = new StudentInfo();
            // 设置外键关联：报名信息 id
            studentInfo.setApplicationId(studentApplication.getId());
            // 可根据需求设置其他字段的默认值，如姓名、联系方式等
            studentInfo.setName(studentApplication.getName());
            studentInfo.setGender(studentApplication.getGender());
            studentInfo.setBirthDate(studentApplication.getBirthDate());
            studentInfo.setIdCard(studentApplication.getIdCard());
            studentInfo.setGraduationDate(studentApplication.getGraduationDate());
            studentInfo.setHighSchool(studentApplication.getHighSchool());
            studentInfo.setAddressCountry(studentApplication.getAddressCountry());
            studentInfo.setAddressProvince(studentApplication.getAddressProvince());
            studentInfo.setAddressCity(studentApplication.getAddressCity());
            studentInfo.setAddressDistrict(studentApplication.getAddressDistrict());
            studentInfo.setAddressDetail(studentApplication.getAddressDetail());
            studentInfo.setPhoneNumber(studentApplication.getPhoneNumber());
            studentInfo.setEmail(studentApplication.getEmail());
            studentInfo.setParentPhone(studentApplication.getParentPhone());

            studentInfo.setOtherLanguageScore(studentApplication.getOtherLanguageScore());
            studentInfo.setCollegeExamScore(studentApplication.getCollegeExamScore());
            studentInfo.setHighSchoolDiploma(studentApplication.getHighSchoolDiploma());
            studentInfo.setOpenDayTime(studentApplication.getOpenDayTime());

            boolean infoResult = studentInfoService.save(studentInfo);
            if(infoResult) {
                return R.success(true);
            } else {
                // 当学生个人信息保存失败时，抛出异常以回滚事务
                throw new RuntimeException("添加学生个人信息失败");
            }
        } else {
            return R.error("无法添加学生报名信息");
        }
    }
    /**
     * 更新招生信息，同时更新对应的学生个人信息
     */
    @Transactional
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Boolean> updateStudentApplication(
            @RequestPart("formData") StudentApplication  studentApplication,
            @RequestPart(value = "diplomaFile", required = false) MultipartFile diplomaFile,HttpServletRequest request
    ) throws IOException {

        // 2. 处理文件上传
        if (diplomaFile != null) {
            // 安全删除旧文件（添加null检查）
            if (studentApplication.getHighSchoolDiploma() != null
                    && !studentApplication.getHighSchoolDiploma().trim().isEmpty())  {
                Path oldPath = Paths.get(uploadDir, studentApplication.getHighSchoolDiploma().replaceFirst("/uploads", ""));
                try {
                    Files.deleteIfExists(oldPath);
                } catch (IOException ignored) {
                }
            }

            // 保存新文件
            String newPath = fileStorageService.storeFile(diplomaFile, studentApplication.getName());
            studentApplication.setHighSchoolDiploma(newPath);
        }

        // 更新学生报名信息
        boolean result = studentApplicationService.updateStudentApplication(studentApplication);

        if(result) {
            // 根据报名信息 id 查找对应的学生个人信息
            StudentInfo studentInfo = studentInfoService.getOne(
                    new QueryWrapper<StudentInfo>().eq("application_id", studentApplication.getId())
            );
            if(studentInfo != null) {
                // 根据业务逻辑同步更新需要修改的字段
                studentInfo.setName(studentApplication.getName());
                studentInfo.setGender(studentApplication.getGender());
                studentInfo.setBirthDate(studentApplication.getBirthDate());
                studentInfo.setIdCard(studentApplication.getIdCard());
                studentInfo.setGraduationDate(studentApplication.getGraduationDate());
                studentInfo.setHighSchool(studentApplication.getHighSchool());
                studentInfo.setAddressCountry(studentApplication.getAddressCountry());
                studentInfo.setAddressProvince(studentApplication.getAddressProvince());
                studentInfo.setAddressCity(studentApplication.getAddressCity());
                studentInfo.setAddressDistrict(studentApplication.getAddressDistrict());
                studentInfo.setAddressDetail(studentApplication.getAddressDetail());
                studentInfo.setPhoneNumber(studentApplication.getPhoneNumber());
                studentInfo.setEmail(studentApplication.getEmail());
                studentInfo.setParentPhone(studentApplication.getParentPhone());

                studentInfo.setOtherLanguageScore(studentApplication.getOtherLanguageScore());
                studentInfo.setCollegeExamScore(studentApplication.getCollegeExamScore());
                studentInfo.setHighSchoolDiploma(studentApplication.getHighSchoolDiploma());
                studentInfo.setOpenDayTime(studentApplication.getOpenDayTime());

                boolean infoResult = studentInfoService.updateById(studentInfo);
                if(infoResult) {
                    return R.success(true);
                } else {
                    return R.error("更新学生个人信息失败");
                }
            } else {
                return R.error("未找到对应的学生个人信息");
            }
        } else {
            return R.error("无法更新学生报名信息");
        }
    }

    /**
     * 根据 ID 删除招生信息，同时删除对应的学生个人信息
     */
    @Transactional
    @DeleteMapping("/{id}")
    public R<Boolean> deleteStudentApplication(@PathVariable Long id) {
        // 删除学生报名信息
        boolean result = studentApplicationService.deleteStudentApplication(id);
        if(result) {
            // 同时删除对应的学生个人信息
            boolean infoResult = studentInfoService.remove(
                    new QueryWrapper<StudentInfo>().eq("application_id", id)
            );
            if(infoResult) {
                return R.success(true);
            } else {
                return R.error("删除学生个人信息失败");
            }
        } else {
            return R.error("无法删除学生报名信息");
        }
    }



}
