package com.ouc.aamanagement.controller;

import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StudentCourse;
import com.ouc.aamanagement.service.StudentCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/studentcourse")
public class StudentCourseController {

    @Autowired
    private StudentCourseService studentCourseService;

    /**
     * 添加选课记录
     */
    @PostMapping("/add")
    public R<StudentCourse> add(@RequestBody StudentCourse studentCourse) {
        boolean saved = studentCourseService.save(studentCourse);
        return saved ? R.success(studentCourse) : R.error("添加失败");
    }

    /**
     * 删除选课记录
     */
    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable Long id) {
        boolean removed = studentCourseService.removeById(id);
        return removed ? R.success("删除成功") : R.error("删除失败");
    }

    /**
     * 更新选课记录
     */
    @PutMapping("/update")
    public R<StudentCourse> update(@RequestBody StudentCourse studentCourse) {
        boolean updated = studentCourseService.updateById(studentCourse);
        return updated ? R.success(studentCourse) : R.error("更新失败");
    }

    /**
     * 查询所有选课记录
     */
    @GetMapping("/list")
    public R<List<StudentCourse>> list() {
        List<StudentCourse> list = studentCourseService.list();
        return R.success(list);
    }


    /**
     * 根据学号查询课表（联表查询，返回课程详细信息）
     */
    @GetMapping("/scheduleWithCourseName")
    public R<List<Map<String, Object>>> getScheduleWithCourseName(
            @RequestParam String studentNumber,
            @RequestParam(required = false, defaultValue = "2024-2025秋") String term) {
        List<Map<String, Object>> result = studentCourseService.getScheduleWithCourseName(studentNumber, term);
        return R.success(result);
    }

}
