package com.mall.service.impl;

import com.mall.core.dao.CommonDao;
import com.mall.core.service.impl.CommonServiceImpl;
import com.mall.mapper.MallOrderMapper;
import com.mall.model.*;
import com.mall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author zhuzh
 * @date 2016年12月28日
 */
@Service
public class MallOrderServiceImpl extends CommonServiceImpl<MallOrder> implements MallOrderService {
	@Autowired
	private MallOrderMapper mallOrderDao;
	@Override
	protected CommonDao<MallOrder> getDao() {
		return mallOrderDao;
	}

	@Override
	protected Class<MallOrder> getModelClass() {
		return MallOrder.class;
	}
}
