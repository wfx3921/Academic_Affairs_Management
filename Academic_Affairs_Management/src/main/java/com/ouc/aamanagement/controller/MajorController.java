package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.ClassInfo;
import com.ouc.aamanagement.entity.Major;
import com.ouc.aamanagement.mapper.ClassMapper;
import com.ouc.aamanagement.mapper.MajorMapper;
import com.ouc.aamanagement.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 专业管理 Controller
 * 基路径: /api/major
 */
@CrossOrigin
@RestController
@RequestMapping("/api/major")
public class MajorController {
    @Autowired
    private MajorMapper majorMapper;
    @Autowired
    private MajorService majorService;

    /**
     * 新增专业信息
     * 请求路径: POST /api/major/add
     *
     * @param major 专业信息实体
     * @return 新增后的专业信息
     */
    @PostMapping("/add")
    public R<Major> addMajor(@RequestBody Major major) {
        boolean saved = majorService.save(major);
        if (saved) {
            return R.success(major);
        } else {
            return R.error("添加专业失败");
        }
    }

    /**
     * 删除专业信息（根据 id）
     * 请求路径: DELETE /api/major/delete/{id}
     *
     * @param id 专业 id
     * @return 删除结果提示
     */
    @DeleteMapping("/delete/{id}")
    public R<String> deleteMajor(@PathVariable Long id) {
        boolean removed = majorService.removeById(id);
        if (removed) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    /**
     * 更新专业信息
     * 请求路径: PUT /api/major/update
     *
     * @param major 专业信息实体（需包含 id）
     * @return 更新后的专业信息
     */
    @PutMapping("/update")
    public R<Major> updateMajor(@RequestBody Major major) {
        boolean updated = majorService.updateById(major);
        if (updated) {
            return R.success(major);
        } else {
            return R.error("更新专业失败");
        }
    }

    /**
     * 根据 id 查询专业信息
     * 请求路径: GET /api/major/get/{id}
     *
     * @param id 专业 id
     * @return 专业信息
     */
    @GetMapping("/get/{id}")
    public R<Major> getMajor(@PathVariable Long id) {
        Major major = majorService.getById(id);
        if (major != null) {
            return R.success(major);
        } else {
            return R.error("专业信息不存在");
        }
    }
    @GetMapping("/majorlist")
    public R<List<Map<String, Object>>> getClassList() {
        // 1. 构建查询条件（只查id和class_name）
        QueryWrapper<Major> wrapper = new QueryWrapper<>();
        wrapper.select("id", "major_name");

        // 2. 执行查询
        List<Map<String, Object>> classList = majorMapper.selectMaps(wrapper);

        // 3. 使用R类包装返回
        return R.success(classList);
    }
    /**
     * 分页及条件查询专业信息
     * 请求路径: GET /api/major/page
     *
     * @param page      当前页码，默认值 1
     * @param size      每页条数，默认值 10
     * @param majorName 专业名称（条件查询，可选）
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public R<Page<Major>> getMajorPage(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String majorName) {
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        if (majorName != null && !majorName.trim().isEmpty()) {
            queryWrapper.like("major_name", majorName);
        }
        Page<Major> majorPage = new Page<>(page, size);
        majorService.page(majorPage, queryWrapper);
        return R.success(majorPage);
    }
    @GetMapping("/getAll")
    public R<List<Major>> getAllMajor() {
        List<Major> majorList = majorService.list();
        if(majorList != null)
            return R.success(majorList);
        else return R.error("没有数据");
    }
}
