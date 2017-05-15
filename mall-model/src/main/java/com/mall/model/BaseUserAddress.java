package com.mall.model;

import com.mall.common.BaseModel;

/**
 * 用户收货地址表
 * 
 * @date 2016年12月7日
 */
public class BaseUserAddress extends BaseModel {

	private static final long serialVersionUID = 24149337328130052L;
	/** 用户ID */
	private String userId;
	/** 省 */
	private String province;
	/** 市 */
	private String city;
	/** 县 */
	private String county;
	/** 详细地址 */
	private String addr;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}
}
