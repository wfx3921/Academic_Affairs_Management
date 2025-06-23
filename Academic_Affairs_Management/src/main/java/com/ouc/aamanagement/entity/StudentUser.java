package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("student_user")
public class StudentUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String email;
    private String password;
    private Date createdAt;
}