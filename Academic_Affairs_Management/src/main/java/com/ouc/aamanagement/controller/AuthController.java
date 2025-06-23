package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.StudentApplication;
import com.ouc.aamanagement.entity.StudentUser;
import com.ouc.aamanagement.entity.VerificationCode;
import com.ouc.aamanagement.mapper.StudentApplicationMapper;
import com.ouc.aamanagement.mapper.StudentUserMapper;
import com.ouc.aamanagement.mapper.VerificationCodeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VerificationCodeMapper codeMapper;

    @Autowired
    private StudentUserMapper userMapper;

    @Autowired
    private StudentApplicationMapper appMapper;

    /**
     * 从配置文件中读取登录邮箱，保证发件人与授权用户一致
     * application.yml 中 spring.mail.username=1095557027@qq.com
     */
    @Value("${spring.mail.username}")
    private String fromEmail;

    /** 1. 发送验证码 **/
    @Transactional
    @GetMapping("/sendCode")
    public R<String> sendCode(@RequestParam String email) {
        // 生成 6 位随机数字验证码
        String code = String.format("%06d", (int)(Math.random() * 1_000_000));
        // 5 分钟后过期
        Date expire = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

        // 先删除旧验证码，再插入新验证码（同一事务内完成）
        codeMapper.delete(new QueryWrapper<VerificationCode>().eq("email", email));
        VerificationCode vc = new VerificationCode();
        vc.setEmail(email);
        vc.setCode(code);
        vc.setExpireTime(expire);
        codeMapper.insert(vc);

        // 构造并发送邮件
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(email);
        msg.setSubject("【报名系统】验证码");
        msg.setText("您的验证码为：" + code + "，有效期 5 分钟。");
        try {
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("发送验证码邮件失败，email={}，code={}", email, code, e);
            return R.error("邮件发送失败，请检查邮箱地址或授权码配置");
        }

        return R.success("验证码发送成功");
    }

    /** 2. 注册账号 **/
    @PostMapping("/register")
    public R<String> register(@RequestParam String email,
                              @RequestParam String code,
                              @RequestParam String password) {
        // 校验验证码
        VerificationCode vc = codeMapper.selectOne(
                new QueryWrapper<VerificationCode>()
                        .eq("email", email)
                        .orderByDesc("id")
                        .last("LIMIT 1")
        );
        System.out.println("email"+email);
        if (vc == null || vc.getExpireTime().before(new Date())) {
            return R.error("验证码无效或已过期");
        }
        if (!vc.getCode().equals(code)) {
            return R.error("验证码错误");
        }
        // 检查是否已注册
        Integer cnt = userMapper.selectCount(
                new QueryWrapper<StudentUser>().eq("email", email)
        );
        if (cnt > 0) {
            return R.error("该邮箱已注册");
        }
        // 创建用户（示例使用 MD5 加密，生产环境请改用 BCrypt）
        StudentUser user = new StudentUser();
        user.setEmail(email);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setCreatedAt(new Date());
        userMapper.insert(user);

        return R.success("注册成功");
    }

    /** 3. 登录 **/
    public static class LoginParam{
        String email;
        String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginParam loginParam) {
        String email=loginParam.email;
        System.out.println(email);
        String password=loginParam.password;
        System.out.println(password);
        // 根据 email 查询用户
        StudentUser user = userMapper.selectOne(
                new QueryWrapper<StudentUser>().eq("email", email)
        );
        if (user == null) {
            return R.error("用户不存在");
        }
        // 校验密码（MD5）
        String md5 = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5.equals(user.getPassword())) {
            return R.error("密码错误");
        }
        // 返回用户基本信息
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("email", user.getEmail());
        return R.success(data);
    }

    /** 4. 发送录取信息 **/
    @PostMapping("/sendAdmission")
    public R<String> sendAdmission(@RequestParam Long studentId) {
        // 查报名表，获取学生信息
        StudentApplication app = appMapper.selectById(studentId);
        if (app == null) {
            return R.error("未找到该学生报名信息");
        }
        String toEmail = app.getEmail();
        // 组织邮件内容
        String subject = "【录取通知】恭喜您被录取";
        StringBuilder sb = new StringBuilder();
        sb.append("尊敬的 ").append(app.getName()).append(" 同学，\n\n")
                .append("恭喜您已通过中国海洋大学国际教育学院审核，获得录取资格！\n")
                .append("祝学习进步！\n");

        // 发送邮件
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(toEmail);
        msg.setSubject(subject);
        msg.setText(sb.toString());
        try {
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("发送录取通知邮件失败，studentId={}，email={}", studentId, toEmail, e);
            return R.error("录取通知邮件发送失败");
        }
        return R.success("录取通知邮件已发送");
    }
}