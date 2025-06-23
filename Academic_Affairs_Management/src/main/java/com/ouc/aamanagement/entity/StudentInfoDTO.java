package com.ouc.aamanagement.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentInfoDTO extends StudentInfo{
    private Integer isPassed;
}
