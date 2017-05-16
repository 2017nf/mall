package com.mall.controller;

import com.mall.common.BaseModel;
import com.mall.core.page.JsonResult;
import com.mall.core.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public abstract class BaseController<D extends BaseModel> {
    
    protected  abstract JsonResult index(HttpServletRequest request, D model, Page page);

    protected  abstract JsonResult add(HttpServletRequest request, D model);

    protected  abstract JsonResult edit(HttpServletRequest request, D model);

    protected  abstract JsonResult list(HttpServletRequest request, D model, Page page);
    
}
