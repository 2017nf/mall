package com.mall.controller;

import com.mall.common.Token;
import com.mall.constant.RedisKey;
import com.mall.constant.StatusType;
import com.mall.core.page.JsonResult;
import com.mall.model.BaseUser;
import com.mall.redis.Sets;
import com.mall.redis.Strings;
import com.mall.service.BaseUserService;
import com.mall.util.BrowserUtil;
import com.mall.util.EmojiUtil;
import com.mall.util.TokenUtil;
import com.mall.util.WechatApiUtil;
import com.mall.vo.UserVo;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${api_domain}")
    private String apiDomain;
    @Value("${wechat_callback_domain}")
    private String wechatCallbackDomain;
    @Autowired
    private BaseUserService userService;

    /**
     * 微信登录
     */
    @ResponseBody
    @RequestMapping(value = "/wxlogin", method = RequestMethod.POST)
    public JsonResult loginByWeixin(HttpServletRequest request, @RequestBody BaseUser loginUser) throws Exception {
        if (StringUtils.isBlank(loginUser.getUnionId())) {
            return new JsonResult(-1, "openid不能为空");
        }
        BaseUser user = userService.readById(loginUser.getId());
        if (null == user) {
            String nickName = loginUser.getNickName();
            if (StringUtils.isNotBlank(nickName))
                nickName = nickName.replaceAll("[^\u0000-\uFFFF]", "?"); // 过滤非UTF-8字符集,用"?"代替，如Emoji表情
            loginUser.setNickName(nickName);
            userService.create(loginUser);
        }

        if (user.getStatus() == StatusType.FALSE.getCode()) {
            return new JsonResult(10002, "您的帐号已被禁用");
        }

        // 登录
        String token = TokenUtil.generateToken(user.getId(), user.getNickName());
        Strings.setEx(RedisKey.TOKEN_API.getKey() + user.getId(), RedisKey.TOKEN_API.getSeconds(), token);
        Sets.sadd(RedisKey.ALL_TOKENS.getKey(), token);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("user login[%s]", TokenUtil.getTokenObject(token)));
        }
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(vo, user);
        vo.setToken(token);
        setUserVo(user, vo);
        return new JsonResult(vo);
    }

    private void setUserVo(BaseUser user, UserVo vo) throws Exception {
        vo.setId(user.getId());
        vo.setNickName(user.getNickName());
    }


    /**
     * 微信分享
     */
    @RequestMapping(value = "/share", method = RequestMethod.GET)
    public ModelAndView share(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView();
        if (BrowserUtil.isWechat(request)) { // 进入微信授权页
            String redirect_uri = wechatCallbackDomain + "/user/share/callback";
            redirect_uri = URLEncoder.encode(redirect_uri, "utf8");
            //state组装uid+grouptype，用符号"_"分割
            String authorize = WechatApiUtil.authorize(redirect_uri, null, "");
            if (StringUtils.isNoneBlank(authorize)) {
                response.sendRedirect(authorize);
                return null;
            }
        }
        // 进入下载页
        mv.setViewName("download");
        return mv;
    }

    /**
     * 微信分享回调
     */
    @RequestMapping(value = "/share/callback", method = RequestMethod.GET)
    public ModelAndView share(HttpServletRequest request, String code, String state) throws Exception {
        ModelAndView mv = new ModelAndView();
        JSONObject json = WechatApiUtil.accessToken(code, state);
        if (json == null) {
            mv.setViewName("error");
            mv.addObject("msg", "获取您的公开信息（昵称、头像等）失败");
            return mv;
        }
        String unionid = WechatApiUtil.getJsonByKey(json, "unionid");
        String openid = WechatApiUtil.getJsonByKey(json, "openid");
        String nickName = WechatApiUtil.getJsonByKey(json, "nickname");
        nickName = nickName.replaceAll("[^\u0000-\uFFFF]", "?"); // 过滤非UTF-8字符集,用"?"代替，如Emoji表情
        String headImgUrl = WechatApiUtil.getJsonByKey(json, "headimgurl");

        BaseUser user = new BaseUser();
        user.setUnionId(unionid);
        user = userService.readById(unionid);
        if (null == user) {
            user = new BaseUser();
            user.setUnionId(unionid);
            user.setOpenId(openid);
            nickName = EmojiUtil.replaceEmoji(nickName);
            user.setNickName(nickName);
            user.setHeadImgUrl(headImgUrl);
            userService.create(user);
        }
        // 进入下载页
        mv.setViewName("redirect:" + apiDomain + "/user/download?uid=000");
        return mv;
    }

    /**
     * App下载页面
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ModelAndView share(HttpServletRequest request, String uid) throws Exception {
        ModelAndView mv = new ModelAndView();
        // 进入下载页
        mv.setViewName("download");
        mv.addObject("uid", uid);
        return mv;
    }

    /*
     * 根据token获取用户信息
     */
    @ResponseBody
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public JsonResult getUserInfo(HttpServletRequest request) throws Exception {
        Token token = TokenUtil.getSessionUser(request);
        if (token == null) {
            return new JsonResult(1, "用户登录失效");
        }

        BaseUser user = userService.readById(token.getId());
        if (null == user) {
            return new JsonResult(2, "无法找到用户信息");
        }
        if (user.getStatus() == StatusType.FALSE.getCode()) {
            return new JsonResult(3, "您的帐号已被禁用");
        }
        // Redis获取Token
        String redisToken = Strings.get(RedisKey.TOKEN_API.getKey() + token.getId());
        if (logger.isInfoEnabled()) {
            logger.info(String.format("user again login[%s]", TokenUtil.getTokenObject(redisToken)));
        }
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(vo, user);
        vo.setToken(redisToken);
        setUserVo(user, vo);
        return new JsonResult(vo);
    }
}
