package com.ouc.aamanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// StudentQueryResult.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentQueryResult {
    private String studentNumber;
    private String name;
    private String scoreValue;
}