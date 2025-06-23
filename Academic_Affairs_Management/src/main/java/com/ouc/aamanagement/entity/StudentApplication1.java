package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("student_application")
public class StudentApplication1 {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String gender;

    private Date birthDate;

    private String idCard;

    private String highSchool;

    private Date graduationDate;

    private String addressCountry;

    private String addressProvince;

    private String addressCity;

    private String addressDistrict;

    private String addressDetail;

    private String phoneNumber;

    private String email;

    private String parentPhone;

    private Integer collegeExamScore;

    private String otherLanguageScore;

    private String highSchoolDiploma;

    private Date openDayTime;
}