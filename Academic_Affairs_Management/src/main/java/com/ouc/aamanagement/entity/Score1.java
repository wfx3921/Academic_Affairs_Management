package com.ouc.aamanagement.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("score1")
public class Score1 {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String value;

    private String acticityId;  // 注意：数据库字段是 acticity_id

    private String studentNumber;  // 数据库字段是 student_number
}