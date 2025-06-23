package com.ouc.aamanagement.controller;

import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.GraduationOpenTime;
import com.ouc.aamanagement.mapper.GraduationOpenTimeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/graduationOpenTime")
public class GraduationOpenTimeController {
    @Autowired
    private GraduationOpenTimeMapper graduationOpenTimeMapper;
    @GetMapping("/get")
    public R<GraduationOpenTime> getGraduationOpenTime() {
        return R.success(graduationOpenTimeMapper.selectOne(null));
    }
    @PostMapping("/update")
    public R<String> updateGraduationOpenTime(@RequestBody GraduationOpenTime graduationOpenTimeEntity) {
        if("".equals(graduationOpenTimeEntity.getId())|| graduationOpenTimeEntity.getId()==null){
            System.out.println("id:"+graduationOpenTimeEntity.getId());
            graduationOpenTimeMapper.insertTime(graduationOpenTimeEntity.getId(),  graduationOpenTimeEntity.getStartTime(), graduationOpenTimeEntity.getEndTime());
            return R.success("添加成功");
        }
        else {
            graduationOpenTimeMapper.updateTime(graduationOpenTimeEntity.getId(), graduationOpenTimeEntity.getStartTime(), graduationOpenTimeEntity.getEndTime()) ;
            return R.success("更新成功");
        }

    }
}
