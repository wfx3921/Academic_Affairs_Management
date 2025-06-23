package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.common.R;
import com.ouc.aamanagement.controller.UserController;
import com.ouc.aamanagement.entity.User;

public interface UserService extends IService<User> {

    R<Page> getListByFields(int page, int pageSize, UserController.SearchForm searchform) ;
}
