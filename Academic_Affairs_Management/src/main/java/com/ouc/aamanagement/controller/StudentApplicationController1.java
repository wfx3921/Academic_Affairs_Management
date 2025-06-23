package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.service.FileStorageService;
import com.ouc.aamanagement.service.StudentApplicationService;
import com.ouc.aamanagement.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@CrossOrigin
@RestController
@RequestMapping("/api/student-applications1")
public class StudentApplicationController1 {

    @Autowired
    private StudentApplicationService studentApplicationService;

    @Autowired
    private StudentInfoService studentInfoService;


    @Autowired
    private FileStorageService fileStorageService;

    @Value("${file.upload-dir}")
    private String uploadDir;
    @PutMapping("/review")
    public ResponseEntity<String> reviewApplication(
            @RequestParam Long id,
            @RequestParam Integer pass) {

        // 根据id查找学生申请
        StudentApplication application = studentApplicationService.getById(id);
        if (application == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("找不到该申请记录");
        }

        // 更新审核状态
        application.setPass(pass);
        boolean isUpdated = studentApplicationService.updateById(application);

        if (isUpdated) {
            return ResponseEntity.ok("审核状态更新成功");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("审核状态更新失败");
        }
    }
    /**
     * 分页查询招生信息
     */
    @GetMapping("/page")
    public R<IPage<StudentApplication>> getStudentApplicationsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String studentStatus,
            @RequestParam(required = false) String registrationStatus) {

        // 构建分页参数
        Page<StudentApplication> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<StudentApplication> queryWrapper = new LambdaQueryWrapper<>();

        // 1. 如果有studentinfo表的查询条件，先获取匹配的application_id列表
        if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(grade) ||
                StringUtils.isNotBlank(major) || StringUtils.isNotBlank(studentStatus) ||
                StringUtils.isNotBlank(registrationStatus)) {

            // 构建studentinfo表的查询条件
            LambdaQueryWrapper<StudentInfo> studentInfoWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(name)) {
                studentInfoWrapper.like(StudentInfo::getName, name);
            }
            if (StringUtils.isNotBlank(grade)) {
                studentInfoWrapper.eq(StudentInfo::getGrade, grade);
            }
            if (StringUtils.isNotBlank(major)) {
                studentInfoWrapper.eq(StudentInfo::getMajor, major);
            }
            if (StringUtils.isNotBlank(studentStatus)) {
                studentInfoWrapper.eq(StudentInfo::getStudentStatus, studentStatus);
            }
            if (StringUtils.isNotBlank(registrationStatus)) {
                studentInfoWrapper.eq(StudentInfo::getRegistrationStatus, registrationStatus);
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
            @RequestParam(required = false) String registrationStatus) {
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


    @Transactional
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)// 这句表示接收文件
    public R<Boolean> saveStudentApplication(
            @RequestPart("formData") StudentApplication  studentApplication,  // 接收前端传来的JSON字符串
            @RequestPart(value = "diplomaFile", required = false) MultipartFile diplomaFile , HttpServletRequest request
    ) throws IOException {

        // 2. 处理文件上传
        if (diplomaFile != null) {
            String filePath = fileStorageService.storeFile(diplomaFile, studentApplication.getName());
            System.out.println("File path: " + filePath);
            studentApplication.setHighSchoolDiploma(filePath);
        }
        // 保存学生报名信息
        boolean result = studentApplicationService.saveStudentApplication(studentApplication);

        if(result) {

            // 保存成功后，新建对应的学生个人信息记录
            StudentInfo studentInfo = new StudentInfo();
            // 设置外键关联：报名信息 idz
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

        if (diplomaFile != null) {
            // 删除旧文件（安全处理绝对路径）
            if (studentApplication.getHighSchoolDiploma() != null
                    && !studentApplication.getHighSchoolDiploma().trim().isEmpty()){
                String oldFilePath = studentApplication.getHighSchoolDiploma();

                // 处理绝对路径：移除盘符（如 C:）保留后续路径
                if (oldFilePath.startsWith("C:")) {
                    oldFilePath = oldFilePath.substring(2); // 移除 "C:"
                }

                // 统一替换斜杠并拼接
                Path oldPath = Paths.get(uploadDir, oldFilePath.replace("\\", "/"))
                        .normalize();

                // 安全检查：确保路径在 uploadDir 下
                if (oldPath.startsWith(Paths.get(uploadDir).normalize())) {
                    try {
                        Files.deleteIfExists(oldPath);
                    } catch (IOException ignored) {

                    }
                }
            }

            // 保存新文件（返回绝对路径）
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
