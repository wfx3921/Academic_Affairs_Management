package com.ouc.aamanagement.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 这个类需要自己编写
 * 全局异常处理器：要是使用try catch语句对代码块进行异常捕获会非常麻烦，因为要对所有可能产生异常的代码块进行try catch，这样会导致写很多的try catch，所以使用全局异常处理器比较简单
 */
//@ControllerAdvice注解会处理所有使用 @RestController 和 @Controller 注解的控制器中抛出的异常，然后返回一个包含自定义错误消息的响应。
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {


    /**
     * 当数据插入数据库时，若该数据的主键与数据库中已有数据的主键重复，则会报 SQLIntegrityConstraintViolationException 异常，此方法便是处理该异常
     * 例如：前端添加员工时重复创建用户名相同的用户
     * @param ex
     * @return 将R对象返回给前端，前端根据需要获取R中的属性值并进行消息展示，一般情况下前端使用this.$message.error(res.msg)方法进行消息展示，res代表后端传给前端的R对象
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)           //@ExceptionHandler 注解允许自定义方法，以处理特定类型的异常。在此处处理的便是SQLIntegrityConstraintViolationException异常
    public R<String> exceptionHanler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());   //控制台打印出的内容：Duplicate entry 'xxx' for key 'employee.idx_username'
        if(ex.getMessage().contains("Duplicate entry")){  //根据异常信息返回给前端消息
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
}
