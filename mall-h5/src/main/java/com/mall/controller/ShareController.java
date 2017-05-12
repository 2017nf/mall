package com.mall.controller;

import com.mall.core.page.JsonResult;
import com.mall.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/4/18 0018.
 */
@Controller
@RequestMapping("/share")
public class ShareController {

    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private MallGoodsService goodsService;
    @Autowired
    private BaseUserService userService;
    @Autowired
    private MallStoreService mallStoreService;
    @Autowired
    private BaseFileService fileLinkService;
    @Autowired
    private MallOrderService goodsWinService;

    /**
     * 商铺详情
     */
    @ResponseBody
    @RequestMapping(value = "/storeInfo", method = RequestMethod.GET)
    public JsonResult storeInfo(HttpServletRequest request, String storeId) throws Exception {
        return new JsonResult();
    }
}
