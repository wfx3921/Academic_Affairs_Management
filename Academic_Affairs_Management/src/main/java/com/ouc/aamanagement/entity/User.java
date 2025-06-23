package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "user_id")
    private Long userId;
    private String userName;
    private String loginName;
    private String password;
    private String deptName;
    private String email;
    private String phoneNum;
    private String userType;
    private String status;
    private Integer deleteFlag;
    @TableField("message")
    private  String message;
//    @Version
//    private  Integer version = 0;

    /**
     * 下面这四个字段是公共字段。使用@TableField字段来指定
     * Mybatis-Plus提供了公共字段填充功能，需要在公共字段上加入注解@TableField，指定自动填充策略。
     */
    @JsonProperty("create_time")
    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private LocalDateTime createTime;

    @JsonProperty("update_time")
    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时填充字段
    private LocalDateTime updateTime;

    @JsonProperty("create_user")
    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private Long createUser;

    @JsonProperty("update_user")
    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时填充字段
    private Long updateUser;

    private Long studentInfoId;
    private Integer permission;

    public Long getStudentInfoId() {
        return studentInfoId;
    }

    public void setStudentInfoId(Long studentInfoId) {
        this.studentInfoId = studentInfoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }


}
