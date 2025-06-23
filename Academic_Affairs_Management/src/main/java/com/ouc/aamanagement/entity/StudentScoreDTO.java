package com.ouc.aamanagement.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StudentScoreDTO {
    private String courseName;       // 组合中英文名称
    private Integer totalHours;
    private Integer credit;
    private Integer initialScore;
    private Integer makeupScore;
    private Integer semester;
}