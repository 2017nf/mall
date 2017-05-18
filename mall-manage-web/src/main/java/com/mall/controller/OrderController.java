package com.mall.controller;

import com.mall.core.page.JsonResult;
import com.mall.core.page.Page;
import com.mall.core.page.PageResult;
import com.mall.model.MallOrder;
import com.mall.service.MallOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 订单controller
 */
@Controller
@RequestMapping("/order")
public class OrderController extends BaseController<MallOrder> {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private MallOrderService mallOrderService;


    /**
     * 分页获取订单列表
     * @param request
     * @param page
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    protected JsonResult list(HttpServletRequest request, MallOrder model, Page page) throws  Exception {

        //查询订单列表
        PageResult<MallOrder> orderList = mallOrderService.getPage(model,page);

        //返回订单列表
        return new JsonResult(orderList);
    }

    /**
     *
     * @param request
     * @param model
     * @param page
     * @return
     */
    @RequestMapping("/index")
    @ResponseBody
    protected JsonResult index(HttpServletRequest request, MallOrder model, Page page) throws  Exception{
        return null;
    }

    /**
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/add")
    @ResponseBody
    protected JsonResult add(HttpServletRequest request, MallOrder model) throws  Exception{
        return null;
    }

    /**
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    protected JsonResult edit(HttpServletRequest request, MallOrder model) throws  Exception{
        return null;
    }
}
