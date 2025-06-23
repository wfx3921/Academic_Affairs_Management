package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StudentAwardsPunishments;
import com.ouc.aamanagement.entity.StudentInfo;
import com.ouc.aamanagement.service.StudentAwardsPunishmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/student-awards-punishments")
public class StudentAwardsPunishmentsController {

    @Autowired
    private StudentAwardsPunishmentsService service;

    /**
     * 分页查询奖惩记录
     */
    @GetMapping("/page")
    public R<IPage<StudentAwardsPunishments>> getPage(
            @RequestParam(required = false) String name,

            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        QueryWrapper<StudentAwardsPunishments> queryWrapper = new QueryWrapper<>();
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
        if(type!= null && !type.trim().isEmpty()){
            queryWrapper.eq("type", type);
        }


        IPage<StudentAwardsPunishments> iPage = service.page(new Page<>(page, size),queryWrapper);
        return R.success(iPage);
    }

    /**
     * 根据ID查询奖惩记录
     */
    @GetMapping("/{id}")
    public R<StudentAwardsPunishments> getById(@PathVariable Long id) {
        StudentAwardsPunishments record = service.getById(id);
        if (record != null) {
            return R.success(record);
        } else {
            return R.error("未找到该记录");
        }
    }

    /**
     * 新增奖惩记录（手工录入）
     */
    @Transactional
    @PostMapping
    public R<Boolean> saveRecord(@RequestBody StudentAwardsPunishments record) {
        boolean result = service.save(record);
        if (result) {
            return R.success(true);
        } else {
            return R.error("新增记录失败");
        }
    }

    /**
     * 更新奖惩记录
     */
    @Transactional
    @PutMapping
    public R<Boolean> updateRecord(@RequestBody StudentAwardsPunishments record) {
        boolean result = service.updateById(record);
        if (result) {
            return R.success(true);
        } else {
            return R.error("更新记录失败");
        }
    }

    /**
     * 删除奖惩记录
     */
    @Transactional
    @DeleteMapping("/{id}")
    public R<Boolean> deleteRecord(@PathVariable Long id) {
        boolean result = service.removeById(id);
        if (result) {
            return R.success(true);
        } else {
            return R.error("删除记录失败");
        }
    }


}
