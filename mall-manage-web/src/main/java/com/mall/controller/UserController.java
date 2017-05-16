package com.mall.controller;

import com.mall.common.BaseModel;
import com.mall.common.Token;
import com.mall.core.page.JsonResult;
import com.mall.core.page.Page;
import com.mall.core.page.PageResult;
import com.mall.model.BaseUser;
import com.mall.model.MallGoods;
import com.mall.service.BaseUserService;
import com.mall.util.TokenUtil;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController  extends  BaseController{

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private BaseUserService userService;

    @Override
    protected JsonResult index(HttpServletRequest request, BaseModel model, Page page) {
        return null;
    }

    @Override
    protected JsonResult add(HttpServletRequest request, BaseModel model) {
        return null;
    }

    @Override
    protected JsonResult edit(HttpServletRequest request, BaseModel model) {
        return null;
    }

    @Override
    @RequestMapping("/list")
    @ResponseBody
    protected JsonResult list(HttpServletRequest request , BaseModel model ,Page page) {
        Token token = TokenUtil.getSessionUser(request);
        BaseUser m = new BaseUser();
        int count = userService.readCount(m);
        List<BaseUser> goodsList = userService.readList( m, page.getPageNo(), page.getPageSize(), count);
        PageResult<BaseUser> result = new PageResult<BaseUser>(page.getPageNo(), page.getPageSize(), count, goodsList);
        return new JsonResult(result);
    }

}
