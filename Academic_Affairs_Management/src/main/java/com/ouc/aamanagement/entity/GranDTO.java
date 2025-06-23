package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GranDTO extends Graduation {
    @TableField("student_name") // 映射SQL中的别名
    private String studentName;



}