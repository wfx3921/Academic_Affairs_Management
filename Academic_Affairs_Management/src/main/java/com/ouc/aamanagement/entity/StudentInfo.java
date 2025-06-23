package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("student_info")
public class StudentInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("application_id")
    private Long applicationId;

    @TableField("name")
    private String name;

    @TableField("gender")
    private String gender;

    @TableField("birth_date")
    private Date birthDate;

    @TableField("id_card")
    private String idCard;

    @TableField("high_school")
    private String highSchool;

    @TableField("graduation_date")
    private Date graduationDate;

    @TableField("address_country")
    private String addressCountry;

    @TableField("address_province")
    private String addressProvince;

    @TableField("address_city")
    private String addressCity;
    private Integer pay;

    @TableField("address_district")
    private String addressDistrict;

    @TableField("address_detail")
    private String addressDetail;

    @TableField("phone_number")
    private String phoneNumber;

    @TableField("parent_phone")
    private String parentPhone;

    @TableField("email")
    private String email;

    @TableField("college_exam_score")
    private Integer collegeExamScore;

    @TableField("other_language_score")
    private String otherLanguageScore;

    @TableField("high_school_diploma")
    private String highSchoolDiploma;

    @TableField("open_day_time")
    private Date openDayTime;

    @TableField("scn_number")
    private String scnNumber;

    @TableField("student_number")
    private String studentNumber;

    @TableField("grade")
    private String grade;

    @TableField("major")
    private String major;

    // 注意：Java中不能使用关键字 class，因此映射数据库字段 class 时，使用 clazz
    @TableField("class1")
    private String class1;

    @TableField("student_status")
    private String studentStatus;

    @TableField("registration_status")
    private String registrationStatus;
    @TableField("lastname")
    private String lastname;
    @TableField("firstname")
    private String firstname;
    @TableField("lastname_en")
    private String lastNameEn;
    @TableField("firstname_en")
    private String firstNameEn;
    @TableField("name_en")
    private String nameEn;
}