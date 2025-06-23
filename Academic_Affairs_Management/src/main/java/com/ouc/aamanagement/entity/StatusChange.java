package com.ouc.aamanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("status_change")
public class StatusChange {
     @TableId(value = "id", type = IdType.AUTO)
     private Long id;
     private String studentNumber;
     private String name;
     private String beforeStatus;
     private String afterStatus;
     private String reason;
     private String createBy;
     private String passBy;
     private Integer ifPass;
     private String approvalTable;
     private Integer ifPass2;
     private String passBy2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(String beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public String getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(String afterStatus) {
        this.afterStatus = afterStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getPassBy() {
        return passBy;
    }

    public void setPassBy(String passBy) {
        this.passBy = passBy;
    }

    public Integer getIfPass() {
        return ifPass;
    }

    public void setIfPass(Integer ifPass) {
        this.ifPass = ifPass;
    }

    public String getApprovalTable() {
        return approvalTable;
    }

    public void setApprovalTable(String approvalTable) {
        this.approvalTable = approvalTable;
    }

    public Integer getIfPass2() {
        return ifPass2;
    }

    public void setIfPass2(Integer ifPass2) {
        this.ifPass2 = ifPass2;
    }

    public String getPassBy2() {
        return passBy2;
    }

    public void setPassBy2(String passBy2) {
        this.passBy2 = passBy2;
    }
}
