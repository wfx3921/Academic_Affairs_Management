package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.ClassInfo;
import com.ouc.aamanagement.mapper.ClassMapper;
import com.ouc.aamanagement.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 班级管理 Controller
 * 基路径: /api/class
 */
@CrossOrigin
@RestController
@RequestMapping("/api/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    /**
     * 新增班级信息
     * 请求路径: POST /api/class/add
     *
     * @param classInfo 班级信息实体
     * @return 新增后的班级信息
     */
    @PostMapping("/add")
    public R<ClassInfo> addClass(@RequestBody ClassInfo classInfo) {
        boolean saved = classService.save(classInfo);
        if (saved) {
            return R.success(classInfo);
        } else {
            return R.error("添加班级失败");
        }
    }
    @Autowired
    private ClassMapper classMapper;

    @GetMapping("/courselist")
    public R<List<Map<String, Object>>> getClassList() {
        // 1. 构建查询条件（只查id和class_name）
        QueryWrapper<ClassInfo> wrapper = new QueryWrapper<>();
        wrapper.select("id", "class_name");

        // 2. 执行查询
        List<Map<String, Object>> classList = classMapper.selectMaps(wrapper);

        // 3. 使用R类包装返回
        return R.success(classList);
    }
    /**
     * 删除班级信息（根据 id）
     * 请求路径: DELETE /api/class/delete/{id}
     *
     * @param id 班级 id
     * @return 删除结果提示
     */
    @DeleteMapping("/delete/{id}")
    public R<String> deleteClass(@PathVariable Long id) {
        boolean removed = classService.removeById(id);
        if (removed) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }
    @GetMapping("/byGrade/{gradeId}")
    public R<List<Map<String, Object>>> getClassesByGrade(@PathVariable Long gradeId) {
        QueryWrapper<ClassInfo> wrapper = new QueryWrapper<>();
        wrapper.select("id", "class_name")
                .eq("grade_id", gradeId); // 添加年级ID条件

        List<Map<String, Object>> classList = classMapper.selectMaps(wrapper);
        return R.success(classList);
    }
    /**
     * 更新班级信息
     * 请求路径: PUT /api/class/update
     *
     * @param classInfo 班级信息实体（需包含 id）
     * @return 更新后的班级信息
     */
    @PutMapping("/update")
    public R<ClassInfo> updateClass(@RequestBody ClassInfo classInfo) {
        boolean updated = classService.updateById(classInfo);
        if (updated) {
            return R.success(classInfo);
        } else {
            return R.error("更新班级失败");
        }
    }

    /**
     * 根据 id 查询班级信息
     * 请求路径: GET /api/class/get/{id}
     *
     * @param id 班级 id
     * @return 班级信息
     */
    @GetMapping("/get/{id}")
    public R<ClassInfo> getClass(@PathVariable Long id) {
        ClassInfo classInfo = classService.getById(id);
        if (classInfo != null) {
            return R.success(classInfo);
        } else {
            return R.error("班级信息不存在");
        }
    }

    /**
     * 根据 年级id 查询班级信息
     * 请求路径: GET /api/class/get/{id}
     *
     * @param gradeid 班级 id
     * @return 班级信息
     */
    /**
     * 分页及条件查询班级信息
     * 请求路径: GET /api/class/page
     *
     * @param page      当前页码，默认值 1
     * @param size      每页条数，默认值 10
     * @param gradeId 班级名称（条件查询，可选）
     *        @param classname 查询，可选）
     * @return 分页查询结果
     */
    @GetMapping("/grade")
    public R<Page<ClassInfo>> getByGradeId(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String gradeId,
                                           @RequestParam(required = false) String classname
    ) {
        QueryWrapper<ClassInfo> queryWrapper = new QueryWrapper<>();
        if (gradeId != null) {
            queryWrapper.like("grade_id", gradeId);
        }
        System.out.println(classname);
        if (classname != null ) {
            queryWrapper.like("class_name", classname);
        }
        Page<ClassInfo> classPage = new Page<>(page, size);
        classService.page(classPage, queryWrapper);
        return R.success(classPage);
    }

    /**
     * 分页及条件查询班级信息
     * 请求路径: GET /api/class/page
     *
     * @param page      当前页码，默认值 1
     * @param size      每页条数，默认值 10
     * @param className 班级名称（条件查询，可选）
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public R<Page<ClassInfo>> getClassPage(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String className) {
        QueryWrapper<ClassInfo> queryWrapper = new QueryWrapper<>();
        if (className != null && !className.trim().isEmpty()) {
            queryWrapper.like("class_name", className);
        }
        Page<ClassInfo> classPage = new Page<>(page, size);
        classService.page(classPage, queryWrapper);
        return R.success(classPage);
    }
    @GetMapping("/getAll")
    public R<List<ClassInfo>> getAllClass(@RequestParam String gradeId) {
        LambdaQueryWrapper<ClassInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ClassInfo::getGradeId,gradeId);
        List<ClassInfo> classList = classService.list(lambdaQueryWrapper);
        if (classList != null)
            return R.success(classList);
        else return R.error("没有数据");
    }
}
