package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.Grade;
import com.ouc.aamanagement.entity.Major;
import com.ouc.aamanagement.mapper.ClassMapper;
import com.ouc.aamanagement.mapper.GradeMapper;
import com.ouc.aamanagement.mapper.MajorMapper;
import com.ouc.aamanagement.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 年级管理Controller
 * 基路径: /api/grade
 */
@CrossOrigin
@RestController
@RequestMapping("/api/grade")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    /**
     * 新增年级信息
     * 请求路径: POST /api/grade/add
     *
     * @param grade 年级信息
     * @return 新增的年级信息
     */
    @PostMapping("/add")
    public R<Grade> createGrade(@RequestBody Grade grade) {
        boolean saved = gradeService.save(grade);
        if (saved) {
            return R.success(grade);
        } else {
            return R.error("新增年级失败");
        }
    }

    /**
     * 删除年级信息（根据id）
     * 请求路径: DELETE /api/grade/delete/{id}
     *
     * @param id 年级id
     * @return 删除成功提示
     */
    @DeleteMapping("/delete/{id}")
    public R<String> deleteGrade(@PathVariable Long id) {
        boolean removed = gradeService.removeById(id);
        if (removed) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    /**
     * 更新年级信息
     * 请求路径: PUT /api/grade/update
     *
     * @param grade 年级信息
     * @return 更新后的年级信息
     */
    @PutMapping("/update")
    public R<Grade> updateGrade(@RequestBody Grade grade) {
        boolean updated = gradeService.updateById(grade);
        if (updated) {
            return R.success(grade);
        } else {
            return R.error("更新年级失败");
        }
    }

    /**
     * 根据ID查询年级信息
     * 请求路径: GET /api/grade/get/{id}
     *
     * @param id 年级id
     * @return 年级信息
     */
    @GetMapping("/get/{id}")
    public R<Grade> getGrade(@PathVariable Long id) {
        Grade grade = gradeService.getById(id);
        if (grade != null) {
            return R.success(grade);
        } else {
            return R.error("年级信息不存在");
        }
    }
    @Autowired
    private GradeMapper gradeMapper;
    @GetMapping("/gradelist")
    public R<List<Map<String, Object>>> getGradeList() {
        // 1. 构建查询条件（只查id和grade_name）
        QueryWrapper<Grade> wrapper = new QueryWrapper<>();
        wrapper.select("id", "grade_name");

        // 2. 执行查询
        List<Map<String, Object>> gradeList = gradeMapper.selectMaps(wrapper);

        // 3. 使用R类包装返回
        return R.success(gradeList);
    }
    @Autowired
    private MajorMapper majorMapper;
    @GetMapping("/majorlist")
    public R<List<Map<String, Object>>> getMajorList() {
        // 1. 构建查询条件（只查id和grade_name）
        QueryWrapper<Major> wrapper = new QueryWrapper<>();
        wrapper.select("id", "major_name");

        // 2. 执行查询
        List<Map<String, Object>> gradeList = majorMapper.selectMaps(wrapper);

        // 3. 使用R类包装返回
        return R.success(gradeList);
    }
    /**
     * 分页查询及条件查询所有年级信息
     * 请求路径: GET /api/grade/page
     *
     * @param page      当前页码，默认值1
     * @param size      每页条数，默认值10
     * @param gradeName 年级名称（条件查询，可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public R<Page<Grade>> getGradesPage(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String gradeName) {
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
        if (gradeName != null && !gradeName.trim().isEmpty()) {
            queryWrapper.like("grade_name", gradeName);
        }
        Page<Grade> gradePage = new Page<>(page, size);
        gradeService.page(gradePage, queryWrapper);
        return R.success(gradePage);
    }

    @GetMapping("/getAll")
    public R<List<Grade>> getAllGrades() {
        List<Grade> grades = gradeService.list();
        if(grades != null)
            return R.success(grades);
        else return R.error("没有数据");
    }
}
