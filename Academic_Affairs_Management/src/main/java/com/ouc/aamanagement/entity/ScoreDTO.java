package com.ouc.aamanagement.entity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;



@Data
@EqualsAndHashCode(callSuper = true)
public class ScoreDTO extends Score {
    @TableField("student_name") // 映射SQL中的别名
    private String studentName;

}