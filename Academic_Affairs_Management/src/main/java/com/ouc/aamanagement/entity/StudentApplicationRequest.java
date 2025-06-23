package com.ouc.aamanagement.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentApplicationRequest {
    private String name;
    private String gender;
    private LocalDate birthDate;
    private LocalDate admissionDate;
    private String major;
}