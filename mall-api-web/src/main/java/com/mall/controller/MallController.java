package com.mall.controller;

import com.mall.common.Token;
import com.mall.core.page.JsonResult;
import com.mall.core.page.Page;
import com.mall.core.page.PageResult;
import com.mall.model.MallGoods;
import com.mall.model.MallSort;
import com.mall.service.*;
import com.mall.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/mall")
public class MallController {

    private static final Logger logger = LoggerFactory.getLogger(MallController.class);

    @Autowired
    private BaseUserService userService;
    @Autowired
    private MallGoodsService goodsService;
    @Autowired
    private MallSortService goodsSortService;
    @Autowired
    private MallStoreService mallService;
    @Autowired
    private BaseFileService fileService;
    @Autowired
    private MallShoppingCardService shoppingCardService;

    /**
     * 商品分类
     */
    @ResponseBody
    @RequestMapping(value = "/sortlist", method = RequestMethod.GET)
    public JsonResult goodsSorts(HttpServletRequest request, MallSort model) throws Exception {
        List<MallSort> list = goodsSortService.readList(model, 1, 100, 100);
        return new JsonResult(list);
    }


    /**
     * 商品列表
     */
    @ResponseBody
    @RequestMapping(value = "/goodslist", method = RequestMethod.GET)
    public JsonResult goodsList(HttpServletRequest request, String goodsSortId, MallGoods model, Page page) throws Exception {
        Token token = TokenUtil.getSessionUser(request);
        Integer timeSort = page.getTimeSort();
        Integer priceSort = page.getPriceSort();
        if (timeSort == null) {
            timeSort = 0;
        }
        if (priceSort == null) {
            priceSort = 0;
        }
        if (timeSort > 0 && priceSort > 0) {
            timeSort = 0;
            priceSort = 0;
        }
        page.setTimeSort(timeSort);
        page.setPriceSort(priceSort);
        int count = goodsService.readCount(model);
        List<MallGoods> goodsList = goodsService.readList(model, page.getPageNo(), page.getPageSize(), count);
        PageResult<MallGoods> result = new PageResult<MallGoods>(page.getPageNo(), page.getPageSize(), count, goodsList);
        return new JsonResult(result);
    }
}
