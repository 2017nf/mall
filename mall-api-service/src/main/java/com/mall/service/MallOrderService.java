package com.mall.service;

import com.mall.core.page.Page;
import com.mall.core.page.PageResult;
import com.mall.core.service.CommonService;
import com.mall.model.BaseUser;
import com.mall.model.MallGoods;
import com.mall.model.MallOrder;
import com.mall.vo.OrderPurchaseVo;

import java.util.List;

/**
 * @date 2016年11月27日
 */
public interface MallOrderService extends CommonService<MallOrder> {
    public MallOrder createOrder(BaseUser user, List<MallGoods> goodsList, OrderPurchaseVo orderPurchaseVo) throws Exception;

    public Boolean purchaseCallBack(BaseUser user, String orderNo) throws Exception;

    public MallOrder getByOrderNo(String orderNo, String userId) throws Exception;

    /**
     * 分页获取订单列表
     * @param model
     * @param page
     * @return
     * @throws Exception
     */
    public PageResult<MallOrder> getPage(MallOrder model, Page page) throws Exception;

}
