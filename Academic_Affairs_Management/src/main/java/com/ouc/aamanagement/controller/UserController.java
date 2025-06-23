package com.ouc.aamanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.entity.User;

import com.ouc.aamanagement.mapper.UserMapper;
import com.ouc.aamanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 用户信息
 */
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;


    /**
     * 用户信息分页查询
     * page:当前页，size：每页数据量，name:如果为null则查询全部数据，如果不为null则查询某一条数据
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),User::getUserName,name);
        queryWrapper.orderByDesc(User::getCreateTime);
        userService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    public static class SearchForm{
        String loginName;
        String phoneNum;
        String status;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createTimeStart;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createTimeEnd;
        String deptName;
        String userType;

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getCreateTimeStart() {
            return createTimeStart;
        }

        public void setCreateTimeStart(LocalDateTime createTimeStart) {
            this.createTimeStart = createTimeStart;
        }

        public LocalDateTime getCreateTimeEnd() {
            return createTimeEnd;
        }

        public void setCreateTimeEnd(LocalDateTime createTimeEnd) {
            this.createTimeEnd = createTimeEnd;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }
    }

    @PostMapping("/getlist")
    public R<Page> getList(@RequestParam int page, @RequestParam int pageSize, @RequestBody SearchForm searchForm) {
        // 参数校验（可选）
        if (page <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("分页参数无效");
        }
        R<Page> pageInfo = userService.getListByFields(page,pageSize,searchForm);
        return pageInfo;
    }
    /**
     * 新增用户
     */
    @PostMapping
    public R<String> save(@RequestBody User user) {
        if (user == null) {
            return R.error("用户信息不能为空");
        }
        try {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getLoginName, user.getLoginName());
            User existingUser = userService.getOne(userLambdaQueryWrapper);
            if (existingUser != null) {
                return R.error("账号已存在");
            }
            // 对用户设置的密码进行 BCrypt 加密处理
            String password = user.getPassword();
            if (password == null) {
                return R.error("密码不能为空");
            }
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            user.setPassword(hashedPassword);
            Date date=new Date();
            LocalDateTime localDateTime = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            user.setCreateTime(localDateTime);

            user.setDeleteFlag(0); // 假设0表示未删除状态

            userService.save(user);
            return R.success("创建用户成功");
        } catch (Exception e) {
            e.printStackTrace(); // 记录详细的异常信息
            return R.error("操作失败，请联系管理员");
        }
    }



    /**
     * 根据用户id删除用户
     *
     * url中的/{id}必须与函数参数名id一样
     *
     *
     */
    @DeleteMapping("/{id}")
    public R<String> deleteByUserId(@PathVariable Long id){
        User user = userService.getById(id);
        user.setDeleteFlag(1);
        userService.updateById(user);
        return R.success("用户删除成功");
    }



    /**
     * 修改用户信息
     * 修改用户信息的页面和添加用户信息的页面共用相同的页面，修改用户信息时需要把用户信息使用 getById（）方法在该页面查询出来，
     * 然后使用下面的 update（）方法对用户信息进行修改
     *
     *
     * url中的/{id}必须与函数参数名id一样
     *
     *
     *
     */
    @GetMapping("/{id}")
    public R<User> getById(@PathVariable Long id){
        User user = userService.getById(id);
        return R.success(user);
    }
    @GetMapping("/adminUsers")
    public List<Map<String, Object>> getAdminUsers() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_id", "user_name")
                .eq("permission", 9);
        return userMapper.selectMaps(queryWrapper);  // 返回List<Map<String, Object>>
    }
    @PostMapping("/approvePermission")
    public String approvePermission(@RequestParam Long userId) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                .set("permission", 1);

        return userMapper.update(null, updateWrapper) > 0
                ? "权限审批通过"
                : "用户不存在";
    }
    @PostMapping("/rejectPermission")
    public String rejectPermission(@RequestParam Long userId) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                .set("permission", 0);

        return userMapper.update(null, updateWrapper) > 0
                ? "权限已拒绝"
                : "用户不存在";
    }
    @PostMapping("/updatePermission")
    public String updatePermission(
            @RequestParam Long userId,
            @RequestParam String message  // 新增申请理由参数
    ) {
        // 使用 UpdateWrapper 构建更新条件
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)  // 条件：id = userId
                .set("permission", 9)       // 设置 permission = 9
                .set("message", message);   // 新增：设置 message = 申请理由

        // 执行更新
        int rows = userMapper.update(null, updateWrapper);

        return rows > 0 ? "权限更新成功" : "用户不存在或更新失败";
    }
//    @PutMapping
//    @Transactional
//    public R<String> update(@RequestBody User user){
//        User user1=userService.getById(user.getUserId());
//        if(user1.getVersion()==user.getVersion()) {
//            user1.setUserName(user.getUserName());
//            user1.setPassword(user.getPassword());
//            user1.setStatus(user.getStatus());
//            user1.setPhoneNum(user.getPhoneNum());
//            user1.setEmail(user.getEmail());
//            String password = user.getPassword();
//            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
//            user1.setPassword(hashedPassword);
//
//
//            userService.updateById(user1);
//            return R.success("操作成功");
//        }else{
//            return R.error("操作失败，已被其他用户修改或删除，请刷新页面");
//        }
//
//    }

    @PutMapping
    @Transactional
    public R<String> update(@RequestParam int flag,@RequestBody User user){
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getLoginName, user.getLoginName());
        User existingUser = userService.getOne(userLambdaQueryWrapper);
//        if (existingUser != null) {
//            return R.error("账号已存在");
//        }
        if (flag == 1) {
            String password = user.getPassword();
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            user.setPassword(hashedPassword);
        }


        LambdaQueryWrapper<User> userLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper1.eq(User::getUserId, user.getUserId());
        userService.update(user, userLambdaQueryWrapper1);
        return R.success("操作成功");
    }

    public static class credentials{
        private String userId;
        private String currentPassword;
        private String newPassword;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    @PostMapping("/resetPassword")
    public R<String> resetPassword(@RequestBody credentials credentials) {
        User user=userService.getById(credentials.getUserId());
        if(BCrypt.checkpw(credentials.getCurrentPassword(), user.getPassword())) {
            String hashedPassword = BCrypt.hashpw(credentials.getNewPassword(), BCrypt.gensalt(12));
            user.setPassword(hashedPassword);
            boolean isOk=userService.updateById(user);
            if(isOk){
                return R.success("修改成功");
            }
            else {
                return R.error("修改失败");
            }
        }else {
            return R.error("原密码错误");
        }
    }

}
