package com.mall.service;

import com.mall.core.service.CommonService;
import com.mall.model.BaseUser;

import java.util.List;

/**
 * @date 2016年11月27日
 */
public interface BaseUserService extends CommonService<BaseUser> {
    List<BaseUser>  list();
}
