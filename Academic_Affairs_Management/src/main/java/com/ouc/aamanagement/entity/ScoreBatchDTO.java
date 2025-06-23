package com.ouc.aamanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
@Accessors(chain = true)
@Data
public class ScoreBatchDTO {
    private Long id;
    private String studentNumber;
    private Long courseId;
    private Double initial;    // 初始成绩
    private Double makeup;     // 补考成绩
    private Double homework;   // 作业成绩
    private Long createUser;
    private Long updateUser;
    private String auditStatus;
}