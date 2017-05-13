package com.mall.controller;

import com.mall.common.Token;
import com.mall.constant.*;
import com.mall.core.page.JsonResult;
import com.mall.model.BaseUser;
import com.mall.model.MallGoods;
import com.mall.model.MallOrder;
import com.mall.model.MallOrderDetail;
import com.mall.pay.wechatpay.GenerateOrder;
import com.mall.service.BaseUserService;
import com.mall.service.MallGoodsService;
import com.mall.service.MallOrderDetailService;
import com.mall.service.MallOrderService;
import com.mall.util.OrderNoUtil;
import com.mall.util.TokenUtil;
import com.mall.vo.OrderPurchaseVo;
import com.mall.vo.PurchaseingVo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.mall.constant.OrderType.PURCHASE;

@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private BaseUserService userService;
    @Autowired
    private MallGoodsService goodsService;
    @Autowired
    private MallOrderService orderService;
    @Autowired
    private MallOrderDetailService orderDetailService;
    @Value("${purchase_weixin_notifyUrl}")
    private String purchaseWeixinNotifyUrl;

    /**
     * 订单直接购买
     */
    @ResponseBody
    @RequestMapping(value = "/appPurchase", method = RequestMethod.POST)
    public JsonResult appPurchase(HttpServletRequest request, @RequestBody OrderPurchaseVo orderPurchaseVo) throws Exception {
        Token token = TokenUtil.getSessionUser(request);


        if (orderPurchaseVo.getCartList() == null || orderPurchaseVo.getCartList().size() <= 0 || StringUtils.isBlank(orderPurchaseVo.getCartList().get(0).getGoodsId())) {
            return new JsonResult(1, "请选择商品");
        }
        //支付来源
        int source = orderPurchaseVo.getPayType().intValue();
        //支付来源是否合法有效
        boolean isVaildPay = source == PayType.WEIXINPAY.getCode().intValue() || source == PayType.BALANCEPAY.getCode().intValue();
        if (!isVaildPay) {
            return new JsonResult(1, "目前仅支持微信或会员卡支付");
        }
        if (StringUtils.isBlank(orderPurchaseVo.getUserName()) || StringUtils.isBlank(orderPurchaseVo.getPhone()) || StringUtils.isBlank(orderPurchaseVo.getAddr())) {
            return new JsonResult(6, "请完善收货信息");
        }
        final BaseUser user = userService.readById(token.getId());
        if (null == user) {
            logger.error(String.format("Illegal user id[%s]", token.getId()));
            throw new IllegalArgumentException();
        }

        MallGoods goods = null;
        List<MallGoods> goodsList = new ArrayList<>();
        int length = orderPurchaseVo.getCartList().size();
        for (int i = 0; i < length; i++) {
            goods = goodsService.readById(orderPurchaseVo.getCartList().get(i).getGoodsId());
            if (goods == null || StatusType.FALSE.getCode() == goods.getStatus()) {
                return new JsonResult(2, goods.getName() + "已下架");
            }
            //购买数量不传默认为1
            if (orderPurchaseVo.getCartList().get(i).getNum() == null || orderPurchaseVo.getCartList().get(i).getNum() <= 0) {
                orderPurchaseVo.getCartList().get(i).setNum(1);
            }
            int num = orderPurchaseVo.getCartList().get(i).getNum();
            if (goods.getStock() < num) {
                return new JsonResult(3, goods.getName() + "库存不足");
            }
            goodsList.add(goods);
        }


        //生成待付款的订单
        MallOrder order = orderService.createOrder(user, goodsList, orderPurchaseVo);

        //返回参数给前端
        Map<Object, Object> map = new HashedMap();
        if (source == PayType.WEIXINPAY.getCode().intValue()) {
            //微信支付
            String ip = request.getRemoteAddr();
            String attach = token.getId() + "@" + PayType.WEIXINPAY.getCode();
            GenerateOrder generateOrder = new GenerateOrder();
            String m = "";
            if (order.getOrderType().intValue() == PURCHASE.getCode().intValue()) {
                m = order.getPrice() * order.getNum() * 100 + "";
            } else {
                m = order.getPrice() * 100 + "";
            }
            String money = m.substring(0, m.indexOf("."));
            Map<String, String> wxMap = generateOrder.generate(money, ip, attach, order.getOrderNo(), purchaseWeixinNotifyUrl);

            map.put("appid", wxMap.get("appid"));
            map.put("partnerid", wxMap.get("partnerid"));
            map.put("noncestr", wxMap.get("noncestr"));
            map.put("timestamp", wxMap.get("timestamp"));
            map.put("package", wxMap.get("package"));
            map.put("prepayid", wxMap.get("prepayid"));
            map.put("sign", wxMap.get("sign"));
        } else if (source == PayType.BALANCEPAY.getCode().intValue()) {
            //余额支付
            Double score = 0d;
            if (order.getOrderType().intValue() == OrderType.PURCHASE.getCode().intValue()) {
                score = order.getPrice() * order.getNum();
            } else if (order.getOrderType().intValue() == OrderType.CARTPAY.getCode().intValue()) {
                score = order.getPrice();
            }
//            if (user.getScore() == null || user.getScore() < score) {
//                return new JsonResult(5, "您的积分不足");
//            }
            //处理业务
            orderService.purchaseCallBack(user, order.getOrderNo());
        }
        map.put("orderNo", order.getOrderNo());
        map.put("time", order.getCreateTime().getTime());
        return new JsonResult(map);
    }


    /**
     * 待付款订单直接购买
     */
    @ResponseBody
    @RequestMapping(value = "/purchasing", method = RequestMethod.POST)
    public JsonResult purchasing(HttpServletRequest request, @RequestBody PurchaseingVo vo) throws Exception {
        Token token = TokenUtil.getSessionUser(request);

        BaseUser user = userService.readById(token.getId());
        if (null == user) {
            logger.error(String.format("Illegal user id[%s]", token.getId()));
            throw new IllegalArgumentException();
        }
        if (StringUtils.isEmpty(vo.getOrderNo())) {
            return new JsonResult(ResultCode.ERROR.getCode(), "orderNo不能为空");
        }
        String orderNo = vo.getOrderNo();
        //TODO(已完善)
       // 根据订单号查询订单
        MallOrder order = orderService.getByOrderNo(orderNo, user.getId());
        if (order == null) {
            return new JsonResult(ResultCode.ERROR.getCode(), "订单不存在");
        }
        if (order.getStatus().intValue() != OrderStatus.PENDING.getCode()) {
            logger.error(order.getOrderNo() + "：订单状态不是待付款，不需要处理");
            return new JsonResult(ResultCode.SUCCESS.getCode(), "订单已付款");
        }
        //支付来源
        int source = vo.getPayPay();
        //支付来源是否合法有效
        boolean isVaildPay = source == PayType.WEIXINPAY.getCode().intValue() || source == PayType.BALANCEPAY.getCode().intValue();
        if (!isVaildPay) {
            return new JsonResult(1, "目前仅支持支付宝、微信支付、余额付款");
        }

        MallGoods goods = null;
        if (order.getOrderType().intValue() == OrderType.CARTPAY.getCode().intValue()) {
            //TODO(已完善，创建一个实体类)
            MallOrderDetail mallOrderDetail = new MallOrderDetail();
            List<MallOrderDetail> list = orderDetailService.readList(mallOrderDetail, 1, 10, 1000);
            if(list == null && list.size() <= 0){
                return new JsonResult(4,"购物车中没有商品");
            }
            for (MallOrderDetail orderDetail : list) {
                //TODO(未完善)
                goods = goodsService.readById(orderDetail.getGoodsId());
                if (goods == null || StatusType.FALSE.getCode() == goods.getStatus()) {
                    return new JsonResult(2, goods.getName() + "已下架,请重新下订单");
                }
                int num = orderDetail.getNum();
                if (goods.getStock() < num) {
                    return new JsonResult(3, goods.getName() + "库存不足,请重新下订单");
                }
            }
        } else {
            goods = goodsService.readById(order.getGoodsId());
            if (goods == null || StatusType.FALSE.getCode() == goods.getStatus()) {
                return new JsonResult(2, goods.getName() + "已下架,请重新下订单");
            }
            if (goods.getStock() < order.getNum()) {
                return new JsonResult(3, goods.getName() + "库存不足,请重新下订单");
            }
        }

        //返回参数给前端
        Map<Object, Object> map = new HashedMap();
        MallOrder model = new MallOrder();
        model.setId(order.getId());
        model.setOrderNo(OrderNoUtil.get());
        model.setUpdateTime(new Date());

        orderService.updateById(model.getId(), model);

        order.setOrderNo(model.getOrderNo());
        if (order.getPayWay().intValue() == PayType.WEIXINPAY.getCode().intValue()) {
            //微信支付
            String ip = request.getRemoteAddr();
            String attach = token.getId() + "@" + PayType.WEIXINPAY.getCode();
            GenerateOrder generateOrder = new GenerateOrder();
            String m = "";
            if (order.getOrderType().intValue() == OrderType.PURCHASE.getCode().intValue()) {
                m = order.getPrice() * order.getNum() * 100 + "";
            } else {
                m = order.getPrice() * 100 + "";
            }
            String money = m.substring(0, m.indexOf("."));
            Map<String, String> wxMap = generateOrder.generate(money, ip, attach, order.getOrderNo(), purchaseWeixinNotifyUrl);
            map.put("appid", wxMap.get("appid"));
            map.put("partnerid", wxMap.get("partnerid"));
            map.put("noncestr", wxMap.get("noncestr"));
            map.put("timestamp", wxMap.get("timestamp"));
            map.put("package", wxMap.get("package"));
            map.put("prepayid", wxMap.get("prepayid"));
            map.put("sign", wxMap.get("sign"));


        } else if (order.getPayWay().intValue() == PayType.BALANCEPAY.getCode().intValue()) {

            //余额支付
            Double score = 0d;
            if (order.getOrderType().intValue() == OrderType.PURCHASE.getCode().intValue()) {
                score = order.getPrice() * order.getNum();
            } else if (order.getOrderType().intValue() == OrderType.CARTPAY.getCode().intValue()) {
                score = order.getPrice();
            }

            //处理业务
            orderService.purchaseCallBack(user, order.getOrderNo());
        }
        map.put("orderNo", order.getOrderNo());
        map.put("time", order.getCreateTime().getTime());
        return new JsonResult(map);
    }


}

