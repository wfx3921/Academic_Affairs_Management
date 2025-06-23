package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@TableName("student_application")
public class StudentApplication {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(exist = false)
    private StudentInfo studentInfo; // 非数据库字段，用于存储关联信息
    private String name;
    @TableField(exist = false)
    private MultipartFile diplomaFile; // 仅用于接收文件，不存储到数据库
    private String gender;
    private Integer pass;
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
    private Integer pay;

    private String otherLanguageScore;

    private String highSchoolDiploma;

    private Date openDayTime;
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