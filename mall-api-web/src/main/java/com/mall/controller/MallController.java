package com.mall.controller;

import com.mall.common.Token;
import com.mall.core.page.JsonResult;
import com.mall.core.page.Page;
import com.mall.core.page.PageResult;
import com.mall.model.MallGoods;
import com.mall.model.MallSort;
import com.mall.service.*;
import com.mall.util.TokenUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/goods")
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
    
    
    /**
	 * 商品详情
	 */
	@ResponseBody
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public JsonResult goodsDetail(HttpServletRequest request, String goodsId) throws Exception {
		Token token = TokenUtil.getSessionUser(request);
		return null;
/*		GoodsVo vo = null;
		Goods goods = goodsService.getById(goodsId);
		if (goods != null) {
			vo = new GoodsVo();
			BeanUtils.copyProperties(vo, goods);
			GoodsIssue issue = goodsIssueService.getById(goods.getCurIssueId());
			if (issue != null) {
//				vo.setPrice(issue.getPrice());
				vo.setDrawPrice(issue.getDrawPrice());
				vo.setDrawNum(issue.getDrawNum());
				vo.setCurNum(issue.getCurNum());
//				vo.setBusinessSendEp(issue.getBusinessSendEp());
				// 获取参与记录
				if (issue.getCurNum() > 0) {
					List<DrawUserVo> drawUsers = new ArrayList<DrawUserVo>();
					List<GoodsIssueDetail> drawList = goodsIssueDetailService.getDrawList(goods.getCurIssueId(), DrawType.PENDING.getCode());
					if (drawList != null && drawList.size() > 0) {
						for (GoodsIssueDetail draw : drawList) {
							User drawUser = userService.getById(draw.getUserId());
							if (drawUser != null) {
								DrawUserVo drawUserVo = new DrawUserVo();
								BeanUtils.copyProperties(drawUserVo, drawUser);
								drawUserVo.setCreateTime(draw.getCreateTime());
								drawUsers.add(drawUserVo);
							}
						}
					}
					vo.setDrawUsers(drawUsers);
				}
			}
			// 获取相关图片
			List<ImageVo> icons = new ArrayList<ImageVo>();
			List<SysFileLink> links = fileLinkService.getList(goodsId);
			if (links != null && links.size() > 0) {
				for (SysFileLink link : links) {
					SysFile file = fileService.getById(link.getFileId());
					if (file != null) {
						ImageVo icon = new ImageVo();
						icon.setId(file.getId());
						icon.setPath(file.getPath());
						icon.setIsDefault(link.getIsDefault());
						icons.add(icon);
					}
				}
			}
			vo.setIcons(icons);
			// 查询用户是否已参与竞拍
			if (token != null) {
				if (goodsIssueDetailService.checkDraw(goods.getCurIssueId(), token.getId())) {
					vo.setIsDraw(1);
				}
			}
		}
		return new JsonResult(vo);*/
	}
    
    
}
