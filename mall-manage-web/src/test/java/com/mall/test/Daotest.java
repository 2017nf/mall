package com.mall.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mall.model.Ad;
import com.mall.model.Admin;
import com.mall.model.Goods;
import com.mall.model.User;
import com.mall.service.ADService;
import com.mall.service.AdminService;
import com.mall.service.GoodsIssueDetailService;
import com.mall.service.MallGoodsService;
import com.mall.service.BaseUserService;
import com.mall.service.WalletDonateService;
import com.mall.service.MallRecordService;
import com.mall.util.RandomUtil;
import com.mall.util.UUIDUtil;
import com.mall.vo.GoodsIssueDetailVo;

/**
 * Created by summer on 2016-12-08:11:04;
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/spring-*.xml")
public class Daotest {
    @Autowired
    AdminService adminService;
    @Autowired
    ADService adService;
    @Autowired
    MallGoodsService goodsService;
    @Autowired
    BaseUserService userService;
    @Autowired
    MallRecordService walletRecordService;
    @Autowired
    WalletDonateService walletDonateService;
    @Autowired
    GoodsIssueDetailService goodsIssueDetailService;

    @Test
    public void test1(){
        Admin admin=new Admin();
        admin.setPassword("123456");
        admin.setId(UUIDUtil.getUUID());
        adminService.create(admin);
        admin.setPassword("1234567");
        adminService.updateById(admin.getId(),admin);
        System.out.println(admin.getAccount());
    }
    @Test
    public void test2(){
        Ad ad=adService.readById("1");
        System.out.println(ad.getAdImg());
        adService.deleteById("2");
        ad.setId(UUIDUtil.getUUID());
        adService.create(ad);
    }
    @Test
    public void test3(){
        Goods goods=new Goods();
        goods.setIsRecommend(1);
        goods.setStatus(1);
        List<Goods> goodses=goodsService.readAll(goods);
        System.out.println(goodses.size());
    }
    @Test
    public void test4(){
//        User user=userService.readById("1");
//        System.out.println(user.getAccount());
        User user=new User();
        user.setAccount(UUIDUtil.getUUID(1)[0]);
        user.setId(UUIDUtil.getUUID());
        user.setUid(RandomUtil.randomInt(1000,9000000));
        userService.create(user);
    }

    @Test
    public void test5(){
        try {
            GoodsIssueDetailVo goodsIssueDetailVo=new GoodsIssueDetailVo();
            List<GoodsIssueDetailVo> goodsIssueDetailVos=goodsIssueDetailService.getGoodsIssueDetailVoList(goodsIssueDetailVo,0,10);
            System.out.println(goodsIssueDetailService.getGoodsIssueDetailVoCount(goodsIssueDetailVo));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

}
