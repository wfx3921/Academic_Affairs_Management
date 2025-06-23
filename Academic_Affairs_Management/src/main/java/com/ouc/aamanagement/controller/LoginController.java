package com.ouc.aamanagement.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.User;
import com.ouc.aamanagement.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping
    public R<User> login(HttpServletRequest request, @RequestBody User user){

        // 1. 获取用户输入的明文密码
        String password = user.getPassword();

        // 2. 根据页面提交的登录名查找数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getLoginName, user.getLoginName());
        User user1 = userService.getOne(queryWrapper);
        if (user1 == null){
            return R.error("登录失败，用户不存在");
        }
        System.out.println(user.getPassword());
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
        System.out.println(hashedPassword);
        System.out.println(user1.getPassword());
        // 3. 如果没有查询到该用户名，则返回登录失败，用户不存在的消息


        // 4. 密码比对，如果密码不一致则返回登录失败，密码错误的消息
        if (!BCrypt.checkpw(password, user1.getPassword())){
            return R.error("登录失败，密码错误");
        }

        // 5. 查看用户状态，如果为已禁用状态（0 表示禁用状态），则返回该用户已禁用的消息
        if (user1.getStatus().equals("inactive")){
            return R.error("登录失败，用户已经被禁用");
        }


        // 6. 登录成功，将用户ID存入Session并返回登录成功的消息，并将用户数据传递给前台
        request.getSession().setAttribute("user", user1.getUserId());
        return R.success(user1);
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的员工的id
        request.getSession().removeAttribute("user");
        return R.success("退出账号成功");
    }



}
