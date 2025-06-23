package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("verification_code")
public class VerificationCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String email;
    private String code;
    private Date expireTime;
}
