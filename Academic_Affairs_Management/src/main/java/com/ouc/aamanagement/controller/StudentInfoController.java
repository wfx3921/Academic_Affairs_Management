package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.entity.User;
import com.ouc.aamanagement.mapper.StudentInfoMapper;
import com.ouc.aamanagement.service.FileStorageService;
import com.ouc.aamanagement.service.StudentApplicationService;
import com.ouc.aamanagement.service.StudentInfoService;
import com.ouc.aamanagement.service.UserService;
import org.checkerframework.checker.units.qual.A;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/student-info")
public class StudentInfoController {

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private StudentApplicationService studentApplicationService;

    @Autowired
    private StudentInfoMapper studentInfoMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private FileStorageService fileStorageService;


    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    @PostMapping(path="/myAdd",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)// 这句表示接收文件
    public R<String> myAdd(@RequestPart("formData") StudentInfo studentInfo, @RequestPart(value = "diplomaFile", required = false) MultipartFile diplomaFile, HttpServletRequest request) {
        if (diplomaFile != null) {
            // 删除旧文件（安全处理绝对路径）
            System.out.println(studentInfo.getHighSchoolDiploma());
            if (studentInfo.getHighSchoolDiploma() != null
                    && !studentInfo.getHighSchoolDiploma().trim().isEmpty()){
                String oldFilePath = studentInfo.getHighSchoolDiploma();

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
            String newPath = null;
            try {
                newPath = fileStorageService.storeFile(diplomaFile, studentInfo.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            studentInfo.setHighSchoolDiploma(newPath);
        }


        // 添加学生信息
        studentInfo.setStudentStatus("正常");

        boolean result = studentInfoService.save(studentInfo);
        System.out.println(studentInfo.getId());
        if (result) {
            // 获取刚刚添加的学生信息
            User user=new User();
            user.setUserName(studentInfo.getStudentNumber());
            user.setLoginName(studentInfo.getStudentNumber());
            String idCard = studentInfo.getIdCard();
            String lastSixDigits = idCard.length() >= 6 ? idCard.substring(idCard.length() - 6) : idCard;
            String hashedPassword = BCrypt.hashpw("Ouc!#"+lastSixDigits, BCrypt.gensalt(12));
            user.setPassword(hashedPassword);
            user.setDeptName("学生");
            user.setEmail(studentInfo.getEmail());
            user.setPhoneNum(studentInfo.getPhoneNumber());
            user.setUserType("学生");
            user.setStatus("active");
            LocalDateTime createTime = LocalDateTime.now();
            user.setCreateTime(createTime);
            user.setDeleteFlag(0);
            user.setStudentInfoId(studentInfo.getId());
            user.setPermission(0);
            boolean newResult=userService.save(user);
            if(newResult){
                return R.success("添加成功");
            }else {
                return R.error("添加用户失败");
            }
        } else {
            return R.error("添加学生信息失败");
        }
    }

    public  static class editBatch {
        List<StudentInfo> studentInfoList;
        String major;
        String grade;
        String class1;

        public List<StudentInfo> getStudentInfoList() {
            return studentInfoList;
        }

        public void setStudentInfoList(List<StudentInfo> studentInfoList) {
            this.studentInfoList = studentInfoList;
        }

        public String getMajor() {
            return major;
        }

        public void setMajor(String major) {
            this.major = major;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getClass1() {
            return class1;
        }

        public void setClass1(String class1) {
            this.class1 = class1;
        }
    }
    @PostMapping("/editBatch")

    public R<String> editBatch(@RequestBody editBatch myEditBatch) {
        System.out.println(myEditBatch.getMajor());
        System.out.println(myEditBatch.getGrade());
        System.out.println(myEditBatch.getClass1());
        List<StudentInfo> studentInfoList = myEditBatch.getStudentInfoList();
        for (StudentInfo studentInfo : studentInfoList) {
            if(myEditBatch.getMajor()!=null&&(!"".equals(myEditBatch.getMajor()))){
                System.out.println(studentInfo.getMajor());
                studentInfo.setMajor(myEditBatch.getMajor());
            }
            if(myEditBatch.getGrade()!=null&&(!"".equals(myEditBatch.getGrade()))){
                System.out.println(studentInfo.getGrade());
                studentInfo.setGrade(myEditBatch.getGrade());
            }

            if(myEditBatch.getClass1()!=null&&(!"".equals(myEditBatch.getClass1()))){
                System.out.println(studentInfo.getClass1());
                studentInfo.setClass1(myEditBatch.getClass1());
            }

            studentInfoService.updateById(studentInfo);
        }
        return R.success("批量修改成功");
    }

    @PutMapping("/update-student-number")
    public R<String> updateStudentNumber(
            @RequestParam Long id,
            @RequestParam String studentNumber) {

        // 直接使用MyBatis Plus更新
        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setId(id);
        studentInfo.setStudentNumber(studentNumber);

        int updated = studentInfoMapper.updateById(studentInfo);

        if (updated > 0) {
            return R.success("学号更新成功");
        } else {
            return R.error("学号更新失败");
        }
    }
    /**
     * 根据 ID 查询学生信息
     */
    @GetMapping("/{id}")
    public R<StudentInfo> getById(@PathVariable Long id) {
        StudentInfo studentInfo = studentInfoService.getById(id);
        if (studentInfo != null) {
            return R.success(studentInfo);
        } else {
            return R.error("没有查询到学生信息");
        }
    }



    /**
     * 更新学生信息，并同步将更新的数据写入对应的学生报名信息
     */
    @Transactional
    @PutMapping
    public R<Boolean> update(@RequestBody StudentInfo studentInfo) {
        // 更新 student_info 表
        boolean result = studentInfoService.updateById(studentInfo);
        if (result) {
            // 根据 application_id 查询对应的报名记录
            StudentApplication studentApplication = studentApplicationService.getById(studentInfo.getApplicationId());
            if (studentApplication != null) {
                // 将 student_info 表中最新数据同步更新到 student_application 表
                studentApplication.setName(studentInfo.getName());
                studentApplication.setGender(studentInfo.getGender());
                studentApplication.setBirthDate(studentInfo.getBirthDate());
                studentApplication.setIdCard(studentInfo.getIdCard());
                studentApplication.setGraduationDate(studentInfo.getGraduationDate());
                studentApplication.setHighSchool(studentInfo.getHighSchool());
                studentApplication.setAddressCountry(studentInfo.getAddressCountry());
                studentApplication.setAddressProvince(studentInfo.getAddressProvince());
                studentApplication.setAddressCity(studentInfo.getAddressCity());
                studentApplication.setAddressDistrict(studentInfo.getAddressDistrict());
                studentApplication.setAddressDetail(studentInfo.getAddressDetail());
                studentApplication.setPhoneNumber(studentInfo.getPhoneNumber());
                studentApplication.setEmail(studentInfo.getEmail());
                studentApplication.setParentPhone(studentInfo.getParentPhone());
                studentApplication.setOtherLanguageScore(studentInfo.getOtherLanguageScore());
                studentApplication.setCollegeExamScore(studentInfo.getCollegeExamScore());
                studentApplication.setHighSchoolDiploma(studentInfo.getHighSchoolDiploma());
                studentApplication.setOpenDayTime(studentInfo.getOpenDayTime());

                boolean appResult = studentApplicationService.updateStudentApplication(studentApplication);
                if (appResult) {
                    return R.success(true);
                } else {
                    return R.error("更新学生报名信息失败");
                }
            } else {
                return R.error("未找到对应的学生报名信息");
            }
        } else {
            return R.error("更新学生信息失败");
        }
    }

    /**
     * 添加学生信息，并同步将数据更新到对应的学生报名信息
     */
    @Transactional
    @PostMapping
    public R<Boolean> save(@RequestBody StudentInfo studentInfo) {
        // 保存 student_info 记录
        boolean result = studentInfoService.save(studentInfo);
        if (result) {
            // 根据 application_id 查询对应的报名记录
            StudentApplication studentApplication = studentApplicationService.getById(studentInfo.getApplicationId());
            if (studentApplication != null) {
                // 将 student_info 表中的数据同步写入报名记录
                studentApplication.setName(studentInfo.getName());
                studentApplication.setGender(studentInfo.getGender());
                studentApplication.setBirthDate(studentInfo.getBirthDate());
                studentApplication.setIdCard(studentInfo.getIdCard());
                studentApplication.setGraduationDate(studentInfo.getGraduationDate());
                studentApplication.setHighSchool(studentInfo.getHighSchool());
                studentApplication.setAddressCountry(studentInfo.getAddressCountry());
                studentApplication.setAddressProvince(studentInfo.getAddressProvince());
                studentApplication.setAddressCity(studentInfo.getAddressCity());
                studentApplication.setAddressDistrict(studentInfo.getAddressDistrict());
                studentApplication.setAddressDetail(studentInfo.getAddressDetail());
                studentApplication.setPhoneNumber(studentInfo.getPhoneNumber());
                studentApplication.setEmail(studentInfo.getEmail());
                studentApplication.setParentPhone(studentInfo.getParentPhone());
                studentApplication.setOtherLanguageScore(studentInfo.getOtherLanguageScore());
                studentApplication.setCollegeExamScore(studentInfo.getCollegeExamScore());
                studentApplication.setHighSchoolDiploma(studentInfo.getHighSchoolDiploma());
                studentApplication.setOpenDayTime(studentInfo.getOpenDayTime());

                boolean appResult = studentApplicationService.updateStudentApplication(studentApplication);
                if (appResult) {
                    return R.success(true);
                } else {
                    throw new RuntimeException("更新学生报名信息失败");
                }
            } else {
                return R.error("未找到对应的学生报名信息");
            }
        } else {
            return R.error("添加学生信息失败");
        }
    }


    /**
     * 条件查询学生信息（支持分页）：可按姓名、年级、学号、专业、学籍状态、报到状态等条件查询
     */
    @GetMapping("/query")
    public R<IPage<StudentInfo>> queryStudentInfo(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String studentStatus,
            @RequestParam(required = false) String registrationStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String addressCountry,
            @RequestParam(required = false) String addressProvince,
            @RequestParam(required = false) String addressCity,
            @RequestParam(required = false) String addressDistrict) {


        QueryWrapper<StudentInfo> queryWrapper = new QueryWrapper<>();
        if(name != null && !name.trim().isEmpty()){
            queryWrapper.like("name", name);
        }
        if(grade != null && !grade.trim().isEmpty()){
            queryWrapper.eq("grade", grade);
        }
        if(studentNumber != null && !studentNumber.trim().isEmpty()){
            queryWrapper.like("student_number", studentNumber);
        }
        if(major != null && !major.trim().isEmpty()){
            queryWrapper.like("major", major);
        }
        if(studentStatus != null && !studentStatus.trim().isEmpty()){
            queryWrapper.eq("student_status", studentStatus);
        }
        if(registrationStatus != null && !registrationStatus.trim().isEmpty()){
            queryWrapper.eq("registration_status", registrationStatus);
        }
        if(gender != null && !gender.trim().isEmpty()){
            queryWrapper.eq("gender", gender);
        }
        if(addressCountry != null && !addressCountry.trim().isEmpty()){
            queryWrapper.eq("address_country", addressCountry);
        }
        if(addressProvince != null && !addressProvince.trim().isEmpty()){
            queryWrapper.eq("address_province", addressProvince);
        }
        if(addressCity != null && !addressCity.trim().isEmpty()){
            queryWrapper.eq("address_city", addressCity);
        }
        if(addressDistrict != null && !addressDistrict.trim().isEmpty()){
            queryWrapper.eq("address_district", addressDistrict);
        }
        queryWrapper.isNotNull("student_number");
        queryWrapper.orderByDesc("id");
        IPage<StudentInfo> studentPage = studentInfoService.page(new Page<>(page, size), queryWrapper);
        return R.success(studentPage);
    }

    @GetMapping("/count")
    public R<Integer> StudentInfoCount(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String studentStatus,
            @RequestParam(required = false) String registrationStatus,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String addressCountry,
            @RequestParam(required = false) String addressProvince,
            @RequestParam(required = false) String addressCity,
            @RequestParam(required = false) String addressDistrict
            ) {

        QueryWrapper<StudentInfo> queryWrapper = new QueryWrapper<>();
        if(name != null && !name.trim().isEmpty()){
            queryWrapper.like("name", name);
        }
        if(grade != null && !grade.trim().isEmpty()){
            queryWrapper.eq("grade", grade);
        }
        if(studentNumber != null && !studentNumber.trim().isEmpty()){
            queryWrapper.like("student_number", studentNumber);
        }
        if(major != null && !major.trim().isEmpty()){
            queryWrapper.like("major", major);
        }
        if(studentStatus != null && !studentStatus.trim().isEmpty()){
            queryWrapper.eq("student_status", studentStatus);
        }
        if(registrationStatus != null && !registrationStatus.trim().isEmpty()){
            queryWrapper.eq("registration_status", registrationStatus);
        }
        if(gender != null && !gender.trim().isEmpty()){
            queryWrapper.eq("gender", gender);
        }
        if(addressCountry != null && !addressCountry.trim().isEmpty()){
            queryWrapper.eq("address_country", addressCountry);
        }
        if(addressProvince != null && !addressProvince.trim().isEmpty()){
            queryWrapper.eq("address_province", addressProvince);
        }
        if(addressCity != null && !addressCity.trim().isEmpty()){
            queryWrapper.eq("address_city", addressCity);
        }
        if(addressDistrict != null && !addressDistrict.trim().isEmpty()){
            queryWrapper.eq("address_district", addressDistrict);
        }
        queryWrapper.isNotNull("student_number");
        Integer count=studentInfoService.count(queryWrapper);
        return R.success(count);
    }


    /**
     * 删除学生信息，并同步删除对应的学生报名信息
     */
    @Transactional
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        boolean result = studentInfoService.removeById(id);
        if(result) {
            studentApplicationService.deleteStudentApplication(id);
            return R.success(true);
        } else {
            return R.error("删除学生信息失败");
        }
    }



    /**
     * 修改学生报到状态
     * 接口示例：PUT /api/student-info/{id}/registration-status?registrationStatus=已报到
     */
    @Transactional
    @PutMapping("/{id}/registration-status")
    public R<Boolean> updateRegistrationStatus(@PathVariable Long id,
                                               @RequestParam String registrationStatus) {
        StudentInfo studentInfo = studentInfoService.getById(id);
        if (studentInfo == null) {
            return R.error("学生信息不存在");
        }
        studentInfo.setRegistrationStatus(registrationStatus);
        boolean result = studentInfoService.updateById(studentInfo);
        if(result) {
            // 示例：同步更新对应的报名信息（如需要）
            return R.success(true);
        } else {
            return R.error("更新学生报到状态失败");
        }
    }



    /**
     * 修改学生学籍状态
     * 接口示例：PUT /api/student-info/{id}/student-status?studentStatus=正常
     */
    @Transactional
    @PutMapping("/{id}/student-status")
    public R<Boolean> updateStudentStatus(@PathVariable Long id,
                                          @RequestParam String studentStatus) {
        StudentInfo studentInfo = studentInfoService.getById(id);
        if (studentInfo == null) {
            return R.error("学生信息不存在");
        }

        studentInfo.setStudentStatus(studentStatus);
        System.out.println(studentStatus);
        if(studentStatus.equals("已毕业")){
            User user=userService.getOne(new QueryWrapper<User>().eq("student_info_id",id));
            user.setStatus("inactive");
            userService.updateById(user);
        }
        else{
            User user=userService.getOne(new QueryWrapper<User>().eq("student_info_id",id));
            user.setStatus("active");
            userService.updateById(user);
        }
        boolean result = studentInfoService.updateById(studentInfo);
        if(result) {
            // 示例：同步更新对应的报名信息（如需要）
            return R.success(true);
        } else {
            return R.error("更新学生学籍状态失败");
        }
    }
}

