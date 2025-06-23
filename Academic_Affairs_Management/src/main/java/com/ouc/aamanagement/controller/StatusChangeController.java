package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StatusChange;
import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.entity.User;
import com.ouc.aamanagement.service.FileStorageService;
import com.ouc.aamanagement.service.StatusChangeService;
import com.ouc.aamanagement.service.StudentInfoService;
import com.ouc.aamanagement.service.UserService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/status-change")
public class StatusChangeController {
    @Autowired
    private StatusChangeService statusChangeService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private StudentInfoService  studentInfoService;
    @Autowired
    private UserService userService;

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Transactional
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Boolean> addStatusChange(
            @RequestPart("formData") StatusChange statusChange,
            @RequestPart(value = "approvalTableFile", required = true) MultipartFile approvalTableFile, HttpServletRequest request
    ) throws IOException {

        // 2. 处理文件上传
        if (approvalTableFile != null) {
            // 安全删除旧文件（添加null检查）
            if (statusChange.getApprovalTable() != null
                    && !statusChange.getApprovalTable().trim().isEmpty())  {
                Path oldPath = Paths.get(uploadDir, statusChange.getApprovalTable().replaceFirst("/uploads", ""));
                try {
                    Files.deleteIfExists(oldPath);
                } catch (IOException ignored) {
                }
            }

            // 保存新文件
            String newPath = fileStorageService.storeFile(approvalTableFile, statusChange.getName());
            System.out.println( "newPath: " + newPath);
            statusChange.setApprovalTable(newPath);
        }
        System.out.println("statusChange: " + statusChange);
        Boolean result =statusChangeService.save(statusChange);
        System.out.println(statusChange.getId());
        if( result){
             return R.success();
        }else  {
            return R.error("添加学籍状态失败");
        }
    }

    @GetMapping("/getStatusChangeById/{studentNumber}")
     public R<List<StatusChange>> getStatusChangeById(@PathVariable Long studentNumber) {
        QueryWrapper<StatusChange>  queryWrapper = new QueryWrapper<StatusChange>().eq("student_number", studentNumber);
        List<StatusChange> statusChangeList = statusChangeService.list(queryWrapper);
        if (statusChangeList != null) {
            return R.success(statusChangeList);
        } else {
            return R.error("无记录");
        }
    }
    @GetMapping("/notApproval")
     public R<IPage<StatusChange>> getNotApproval(@RequestParam(defaultValue =  "1") Integer page, @RequestParam(defaultValue =  "10") Integer size) {
        QueryWrapper<StatusChange>  queryWrapper = new QueryWrapper<StatusChange>().eq("if_pass", 0);
        IPage <StatusChange> statusChangePage =statusChangeService .page( new Page<>(page,size), queryWrapper);
        if (statusChangePage != null) {
            return R.success(statusChangePage);
        } else {
            return R.error("无记录");
        }
    }
    @GetMapping ("/notApproval1")
      public R<IPage<StatusChange>> getNotApproval1(@RequestParam(defaultValue =  "1") Integer page, @RequestParam(defaultValue =  "10") Integer size) {
        QueryWrapper<StatusChange>  queryWrapper = new QueryWrapper<StatusChange>().eq("if_pass", 2);
        queryWrapper.eq("if_pass2", 0);
        IPage<StatusChange> statusChangePage =statusChangeService.page( new Page<>(page,size), queryWrapper);
          if (statusChangePage != null) {
            return R.success(statusChangePage);
        } else {
            return R.error("无记录");
        }
    }
    @GetMapping("/pass")
    public R<IPage<StatusChange>> getPass(@RequestParam(defaultValue =  "1") Integer page, @RequestParam(defaultValue =  "10") Integer size) {
        QueryWrapper<StatusChange>  queryWrapper = new QueryWrapper<StatusChange>().eq("if_pass", 2);
        IPage <StatusChange> statusChangePage =statusChangeService .page( new Page<>(page,size), queryWrapper);
        if (statusChangePage != null) {
            return R.success(statusChangePage);
        } else {
            return R.error("无记录");
        }
    }
    @GetMapping("/pass1")
     public R<IPage<StatusChange>> getPass1(@RequestParam(defaultValue =  "1") Integer page, @RequestParam(defaultValue =  "10") Integer size) {
        QueryWrapper<StatusChange>  queryWrapper = new QueryWrapper<StatusChange>().eq("if_pass", 2);
         queryWrapper.eq("if_pass2", 2);
        IPage <StatusChange> statusChangePage =statusChangeService .page( new Page<>(page,size), queryWrapper);
        if (statusChangePage != null) {
            return R.success(statusChangePage);
        } else {
            return R.error("无记录");
        }
     }
    @GetMapping("/notPass")
    public R<IPage<StatusChange>> getNotPass(@RequestParam(defaultValue =  "1") Integer page, @RequestParam(defaultValue =  "10") Integer size) {
        QueryWrapper<StatusChange>  queryWrapper = new QueryWrapper<StatusChange>().eq("if_pass", 1);
        IPage <StatusChange> statusChangePage =statusChangeService .page( new Page<>(page,size), queryWrapper);
        if (statusChangePage != null) {
            return R.success(statusChangePage);
        } else {
            return R.error("无记录");
        }
    }
    @GetMapping("/notPass1")
     public R<IPage<StatusChange>> getNotPass1(@RequestParam(defaultValue =  "1") Integer page, @RequestParam(defaultValue =  "10") Integer size) {
        QueryWrapper<StatusChange>  queryWrapper = new QueryWrapper<StatusChange>().eq("if_pass", 2);
         queryWrapper.eq("if_pass2", 1);
        IPage <StatusChange> statusChangePage =statusChangeService .page( new Page<>(page,size), queryWrapper);
        if (statusChangePage != null) {
             return R.success(statusChangePage);
        }
         else {
            return R.error("无记录");
        }
     }
    @PostMapping("/delete/{id}")
     public R<Boolean> deleteChange(@PathVariable Long id) {
        Boolean result = statusChangeService.removeById(id);
        if (result) {
            return R.success();
        } else {
            return R.error("删除失败");
        }
    }
    @PostMapping("/updatePass")
    public R<String>  updatePass(@RequestBody StatusChange statusChange) {

        Boolean result = statusChangeService.updateById(statusChange);
        if(statusChange.getIfPass()==2&&statusChange.getIfPass2()==2){
            QueryWrapper<StudentInfo>  queryWrapper = new QueryWrapper<StudentInfo>().eq("student_number", statusChange.getStudentNumber());
            StudentInfo studentInfo = studentInfoService.getOne(queryWrapper);
            studentInfo.setStudentStatus(statusChange.getAfterStatus());
            studentInfoService.updateById(studentInfo);
            if(statusChange.getAfterStatus().equals("已毕业")){
                User user=userService.getOne(new QueryWrapper<User>().eq("student_info_id",studentInfo.getId()));
                user.setStatus("inactive");
                userService.updateById(user);
            }
            else{
                User user=userService.getOne(new QueryWrapper<User>().eq("student_info_id",studentInfo.getId()));
                user.setStatus("active");
                userService.updateById(user);
            }
        }
        if (result) {
            return R.success();
        } else {
            return R.error("更新失败");
        }
    }
}
