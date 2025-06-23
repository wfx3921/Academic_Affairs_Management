package com.ouc.aamanagement.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class SpecialStudentVO extends Graduation{
    @TableField("name") // 映射SQL中的别名
    private String studentName;
    private Date graduationDate;
    @TableField("grade")  // 新增年级字段
    private String gradeName;

    @TableField("class")  // 新增班级字段
    private String className;

    @TableField("student_status")  // 新增班级字段
    private String studentStatus;
}
