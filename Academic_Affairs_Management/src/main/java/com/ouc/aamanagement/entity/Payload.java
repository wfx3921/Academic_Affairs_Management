package com.ouc.aamanagement.entity;

import lombok.Data;

import java.util.Date;

@Data // Lombok注解，自动生成getter/setter
public class Payload {
    private String studentNumber; // 学号
    private String specialType;   // 特殊类型
    private String specialInfo;   // 特殊信息
    private Date graduationDate;  // 毕业日期


}