package com.yunbaba.freighthelper.bean;

import com.cld.setting.CldSetting;


public class UserInfo {
	
	private String username = "";  // 账号
	private String useralias = ""; // 别名
	private String mobile = "";	   // 手机
	private int sex = 2;	       // 性别: 2-男； 1-女
	private String address = "";   // 地区
	private String imghead = "";   // 头像
	private int isSuccess = -1;	   // 是否获取到详细信息:-1: 未获取;0: 成功; >0: 失败
	private int loginStatus = 0;   
	private int loginType = 0;     // 1-手机号+验证码；2-账号+密码; 3-自动登录
	
	public static enum ChangeTaskEnum {
		eSEX,
		eUSERALIAS,
		eADDRESS,
		eIMGHEAD,
		eALL,
	};
	
	// 更改用户信息标识，0:性别; 1:化名; 2:区域; 3:手机
	private int[] infoChange = { 0, 0, 0, 0 };
	
	public UserInfo() {
		sex = 2;
		username = "";
		useralias = "";	
		address = "";
		mobile = "";
		imghead = "";
		loginStatus = 0;
		loginType= 0;
	}
	
	public UserInfo(UserInfo info) {
		this.sex = info.getSex();
		this.username = info.getUserName();
		this.useralias = info.getUserAlias();	
		this.address = info.getAddress();
		this.mobile = info.getMobile();
		this.imghead = info.getImgHead();
		this.infoChange = info.getChangeStatus();
		this.loginStatus = info.getLoginStatus();
		this.loginType = info.getLoginType();
	}

	public void assignVaule(UserInfo info) {
		this.sex = info.getSex();
		this.username = info.getUserName();
		this.useralias = info.getUserAlias();	
		this.address = info.getAddress();
		this.mobile = info.getMobile();
		this.imghead = info.getImgHead();
		this.infoChange = info.getChangeStatus();
		this.loginStatus = info.getLoginStatus();
		this.loginType = info.getLoginType();
	}
	
	public void reset() {
		sex = 2;
		username = "";
		useralias = "";	
		address = "";
		mobile = "";
		imghead = "";
		loginStatus = 0;
		loginType = 0;
	}
	
	public int getSex() {
		return sex;
	}
	
	public void setSex(int sex) {
		CldSetting.put("sex", sex);
		this.sex = sex;
	}
	
	public void changeSex(int sex) {
		setChangeStatus(ChangeTaskEnum.eSEX);
		CldSetting.put("sex", sex);
		this.sex = sex;
	}
	
	public String getUserAlias() {
		return useralias;
	}
	
	public void setUserAlias(String useralias) {
		CldSetting.put("useralias", useralias);
		this.useralias = useralias;
	}
	
	public void changeUserAlias(String useralias) {
		setChangeStatus(ChangeTaskEnum.eUSERALIAS);
		CldSetting.put("useralias", useralias);
		this.useralias = useralias;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		CldSetting.put("address", address);
		this.address = address;
	}
	
	public void changeAddress(String address) {
		setChangeStatus(ChangeTaskEnum.eADDRESS);
		CldSetting.put("address", address);
		this.address = address;
	}
	
	public String getImgHead() {
		return imghead;
	}
	
	public void setImgHead(String imghead) {
		CldSetting.put("imghead", imghead);
		this.imghead = imghead;
	}
	
	public void changeImgHead(String imghead) {
		setChangeStatus(ChangeTaskEnum.eIMGHEAD);
		CldSetting.put("imghead", imghead);
		this.imghead = imghead;
	}
	
	public String getUserName() {
		return username;
	}

	public void setUserName(String username) {
		CldSetting.put("username", username);
		this.username = username;
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		CldSetting.put("mobile", mobile);
		this.mobile = mobile;
	}

	public int[] getChangeStatus() {
		return infoChange;
	}
	
	public void resetChangeStatus(ChangeTaskEnum eTask) {
		switch (eTask) {
		case eSEX:
			infoChange[0] = 0;
			break;
			
		case eUSERALIAS:
			infoChange[1] = 0;
			break;
			
		case eADDRESS:
			infoChange[2] = 0;
			break;
			
		case eIMGHEAD:
			infoChange[3] = 0;
			break;
			
		default:
			infoChange[0] = 0;
			infoChange[1] = 0;
			infoChange[2] = 0;
			infoChange[3] = 0;
			break;
		}
	}
	
	public void setChangeStatus(ChangeTaskEnum eTask) {
		switch (eTask) {
		case eSEX:
			infoChange[0] = 1;
			break;
			
		case eUSERALIAS:
			infoChange[1] = 1;
			break;
			
		case eADDRESS:
			infoChange[2] = 1;
			break;
			
		case eIMGHEAD:
			infoChange[3] = 1;
			break;
			
		default:
			break;
		}
	}
	
	public void setSuccess(int isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	public int getSuccess() {
		return isSuccess;
	}
	
	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
	}
	
	public int getLoginStatus() {
		return loginStatus;
	}
	
	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}
	
	public int getLoginType() {
		return loginType;
	}
}
