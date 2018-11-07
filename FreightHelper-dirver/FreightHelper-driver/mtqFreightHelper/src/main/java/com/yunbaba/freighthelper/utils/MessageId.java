package com.yunbaba.freighthelper.utils;

public class MessageId {

	public static final int MSGID_DEFAULT = 1024;

	public static final int MSGID_LOGIN_GET_QRCODE_SUCCESS = 1;
	public static final int MSGID_LOGIN_GET_QRCODE_FAILED = 2;
	public static final int MSGID_LOGIN_QRCODE_LOGIN_SUCCESS = 3; // 二维码登录成功能

	public static final int MSGID_LOGIN_MOBILE_LOGIN_SUCCESS = 4; // 手机登录成功
	public static final int MSGID_LOGIN_MOBILE_LOGIN_FAILED = 5; // 手机登录失败
	public static final int MSGID_LOGIN_ACCOUNT_LOGIN_SUCCESS = 6; // 帐户登录成功
	public static final int MSGID_LOGIN_ACCOUNT_LOGIN_FAILED = 7; // 帐户登录失败
	public static final int MSGID_LOGIN_THIRD_LOGIN_SUCCESS = 8; // 第三方登录成功
	public static final int MSGID_LOGIN_THIRD_LOGIN_FAILED = 9; // 第三方登录成功
	public static final int MSGID_LOGIN_AUTO_LOGIN_SUCCESS = 10; // 自动登录成功
	public static final int MSGID_LOGIN_AUTO_LOGIN_FAILED = 11; // 自动登录失败
	public static final int MSGID_LOGIN_AUTO_LOGIN_FAILED_NET = 111 ;// 自动登录失败
	public static final int MSGID_LOGIN_SESSION_INVAILD = 12; // sesstion失效
	public static final int MSGID_LOGOUT_SUCCESS = 13;// 登出成功
	public static final int MSGID_LOGOUT_FAILED = 14;// 登出失败

	// 注册
	public static final int MSGID_GET_REG_VERICODE_SUCCESS = 15;
	public static final int MSGID_GET_REG_VERICODE_FAILED = 16;

	// 绑定
	public static final int MSGID_GET_BIND_VERICODE_SUCCESS = 17;
	public static final int MSGID_GET_BIND_VERICODE_FAILED = 18;

	// 改绑
	public static final int MSGID_GET_REVISE_BIND_VERICODE_SUCCESS = 19;
	public static final int MSGID_GET_REVISE_BIND_VERICODE_FAILED = 20;

	// 绑定
	public static final int MSGID_GET_UNBIND_VERICODE_SUCCESS = 21;
	public static final int MSGID_GET_UNBIND_VERICODE_FAILED = 22;

	// 重置密码
	public static final int MSGID_GET_RETRIEVE_PWD_VERICODE_SUCCESS = 23;
	public static final int MSGID_GET_RETRIEVE_PWD_VERICODE_FAILED = 24;

	// 快速登录
	public static final int MSGID_GET_LOGIN_VERICODE_SUCCESS = 25; // 获取验证码失败
	public static final int MSGID_GET_LOGIN_VERICODE_FAILED = 26; // 获取验证码成功

	public static final int MSGID_CHECK_RETRIEVE_PWD_VERICODE_SUCCESS = 27;
	public static final int MSGID_CHECK_RETRIEVE_PWD_VERICODE_FAILED = 28;

	public static final int MSGID_USERINFO_GETDETAIL_SUCCESS = 29;
	public static final int MSGID_USERINFO_GETDETAIL_FAILED = 30;
	public static final int MSGID_USERINFO_UPDATE_SUCCESS = 31;
	public static final int MSGID_USERINFO_UPDATE_FAILED = 32;

	public static final int MSGID_USERINFO_BIND_MOBILE_SUCCESS = 33;
	public static final int MSGID_USERINFO_BIND_MOBILE_FAILED = 34;
	public static final int MSGID_USERINFO_UNBIND_MOBILE_SUCCESS = 35;
	public static final int MSGID_USERINFO_UNBIND_MOBILE_FAILED = 36;
	public static final int MSGID_USERINFO_REVISE_MOBILE_SUCCESS = 37;
	public static final int MSGID_USERINFO_REVISE_MOBILE_FAILED = 38;

	public static final int MSGID_PWD_SET_PWD_SUCCESS = 39;
	public static final int MSGID_PWD_SET_PWD_FAILED = 40;
	public static final int MSGID_PWD_MODIFY_PWD_SUCCESS = 41;
	public static final int MSGID_PWD_MODIFY_PWD_FAILED = 42;
	
	/**
	 * 有新消息
	 */
	public static final int MSGID_MSG_NEW = 43;
	
	/**
	 * 获取最近行程
	 */
	public static final int MSGID_GET_LATEST_ROUTE_SUCCESS = 44;
	public static final int MSGID_GET_LATEST_ROUTE_FAILED = 45;
	/**
	 * 获取司机驾驶车辆信息
	 */
	public static final int MSGID_GET_CARINFO_SUCCESS = 46;
	public static final int MSGID_GET_CARINFO_FAILED = 47;
	
	//消息刷新
	public static final int MSGID_MSG_UPDATE = 48;
	
	
	
	//设置运单列表adpter、viewpager
	public static final int MSGID_FREIGHTPOINT_ADPTER = MSGID_MSG_UPDATE + 1;
	//运单列表显示poi
	public static final int MSGID_FREIGHTPOINT_SHOWPOI = MSGID_FREIGHTPOINT_ADPTER + 1;
	//算路成功消息
	public static final int MSGID_ROUTE_SUCESS = MSGID_FREIGHTPOINT_SHOWPOI + 1;
	//算路失败消息
	public static final int MSGID_ROUTE_FAIL = MSGID_ROUTE_SUCESS + 1;
	
	
	//获取行驶车辆动态信息失败
	public static final int MSGID_GET_TASKNAVI_FAIL = MSGID_ROUTE_FAIL + 1;
	
	//获取货单列表失败
	public static final int MSGID_FREIGHTPOINT_ADPTER_FAIL = MSGID_ROUTE_FAIL + 1;
	
	
	//地图resume 可以刷新
	public static final int MSGID_MAP_RESUME = MSGID_FREIGHTPOINT_ADPTER_FAIL + 1;
	//地图onpause 不刷新地图
	public static final int MSGID_MAP_PAUSE = MSGID_MAP_RESUME + 1;
	
	
	
	//请求超时
	public static final int MSGID_REQUEST_TIMEOUT = 2046;
}
