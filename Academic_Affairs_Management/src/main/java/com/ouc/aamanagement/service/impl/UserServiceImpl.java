package com.ouc.aamanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.controller.UserController;

import com.ouc.aamanagement.entity.User;
import com.ouc.aamanagement.mapper.UserMapper;
import com.ouc.aamanagement.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    public R<Page> getListByFields(int page, int pageSize, UserController.SearchForm searchForm) {

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (searchForm.getLoginName() != null&&searchForm.getLoginName() !="") {
            userLambdaQueryWrapper.eq(User::getLoginName, searchForm.getLoginName());
        }
        if (searchForm.getPhoneNum() != null&&searchForm.getPhoneNum() != "") {
            userLambdaQueryWrapper.eq(User::getPhoneNum, searchForm.getPhoneNum());
        }
        if (searchForm.getStatus() != null &&searchForm.getStatus() !="") {
            userLambdaQueryWrapper.eq(User::getStatus, searchForm.getStatus());
        }
        if (searchForm.getCreateTimeStart() != null&&searchForm.getCreateTimeEnd()!=null) {
            userLambdaQueryWrapper.between(User::getCreateTime, searchForm.getCreateTimeStart(),searchForm.getCreateTimeEnd());
        }
        System.out.println(searchForm.getUserType()+"-----------------------");
        if(searchForm.getUserType()!=null&&searchForm.getUserType()!=""){
            userLambdaQueryWrapper.eq(User::getUserType,searchForm.getUserType());
        }
        userLambdaQueryWrapper.eq(User::getDeleteFlag,0);
        userLambdaQueryWrapper.orderByDesc(User::getUserId);
        Page<User> pageInfo = new Page<>(page, pageSize);
        this.page(pageInfo, userLambdaQueryWrapper);
        return R.success(pageInfo);
    }

}
