package com.mall.constant;

/**
 * 支付枚举类 <br/>
 * @date 2016年11月15日
 */
public enum PayType {

	PURCHASE(1, "直接购买"),

	EXCHANGEEP(2, "微信支付"),

	WEIXIN(3, "支付宝支付");


	private final Integer code;

	private final String msg;

	private PayType(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}
