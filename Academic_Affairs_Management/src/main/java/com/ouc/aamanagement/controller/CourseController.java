package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.Course;
import com.ouc.aamanagement.entity.User;
import com.ouc.aamanagement.service.CourseService;
import com.ouc.aamanagement.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 课程管理 Controller
 * 基路径: /api/course
 */
@CrossOrigin
@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 新增课程信息
     * 请求路径: POST /api/course/add
     *
     * @param course 课程信息实体
     * @return 新增后的课程信息
     */
    @PostMapping("/add")
    public R<Course> addCourse(@RequestBody Course course) {
        boolean saved = courseService.save(course);
        if (saved) {
            return R.success(course);
        } else {
            return R.error("添加课程失败");
        }
    }
    @PostMapping("/import")
    public R<String> importCourses(@RequestBody List<Course> courses) {
        try {
            // 收集所有教师姓名
            Set<String> teacherNames = courses.stream()
                    .map(Course::getTeacherName)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());

            // 批量查询教师信息
            Map<String, Long> teacherIdMap = userService.lambdaQuery()
                    .in(User::getUserName, teacherNames)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(User::getUserName, User::getUserId));

            // 验证并设置教师ID
            List<String> missingTeachers = new ArrayList<>();
            for (Course course : courses) {
                if (StringUtils.isNotBlank(course.getTeacherName())) {
                    Long teacherId = teacherIdMap.get(course.getTeacherName());
                    if (teacherId == null) {
                        missingTeachers.add(course.getTeacherName());
                    } else {
                        course.setTeacherId(teacherId);
                    }
                }
            }

            if (!missingTeachers.isEmpty()) {
                String errorMsg = "系统中没有以下教师信息，请先添加: " + String.join(", ", missingTeachers);
                return R.error(errorMsg);
            }

            boolean saved = courseService.saveBatch(courses);
            if (saved) {
                return R.success("成功导入" + courses.size() + "条课程数据");
            } else {
                return R.error("导入失败");
            }

        } catch (Exception e) {
            return R.error("导入课程失败: " + e.getMessage());
        }
    }
    /**
     * 删除课程信息（根据 id）
     * 请求路径: DELETE /api/course/delete/{id}
     *
     * @param id 课程 id
     * @return 删除结果提示
     */
    @DeleteMapping("/delete/{id}")
    public R<String> deleteCourse(@PathVariable Long id) {
        boolean removed = courseService.removeById(id);
        if (removed) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    /**
     * 更新课程信息
     * 请求路径: PUT /api/course/update
     *
     * @param course 课程信息实体（需包含 id）
     * @return 更新后的课程信息
     */
    @PutMapping("/update")
    public R<Course> updateCourse(@RequestBody Course course) {
        boolean updated = courseService.updateById(course);
        if (updated) {
            return R.success(course);
        } else {
            return R.error("更新课程失败");
        }
    }

    /**
     * 根据 id 查询课程信息
     * 请求路径: GET /api/course/get/{id}
     *
     * @param id 课程 id
     * @return 课程信息
     */
    @GetMapping("/get/{id}")
    public R<Course> getCourse(@PathVariable Long id) {
        Course course = courseService.getById(id);
        if (course != null) {
            return R.success(course);
        } else {
            return R.error("课程信息不存在");
        }
    }

    /**
     * 分页及条件查询课程信息
     * 请求路径: GET /api/course/page
     *
     * @param page         当前页码，默认值 1
     * @param size         每页条数，默认值 10
     * @param courseName   课程名称（条件查询，可选）
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public R<Page<Course>> getCoursePage(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String courseName) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (courseName != null && !courseName.trim().isEmpty()) {
            queryWrapper.like("course_name", courseName);
        }
        Page<Course> coursePage = new Page<>(page, size);
        courseService.page(coursePage, queryWrapper);
        return R.success(coursePage);
    }
    @Autowired
    UserService userService;
    @GetMapping("/teachers")
    public R<List<TeacherDTO>> getTeachers() {
        // 查询user_type为任课老师的用户
        List<User> teachers = userService.lambdaQuery()
                .eq(User::getUserType, "任课老师")
                .list();

        // 转换为DTO对象
        List<TeacherDTO> teacherDTOs = teachers.stream()
                .map(teacher -> new TeacherDTO(teacher.getUserId(), teacher.getUserName()))
                .collect(Collectors.toList());

        return R.success(teacherDTOs);
    }

    // DTO类定义
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeacherDTO {
        private Long value;  // 对应前端Select的value
        private String label; // 对应前端Select的label
    }
}
