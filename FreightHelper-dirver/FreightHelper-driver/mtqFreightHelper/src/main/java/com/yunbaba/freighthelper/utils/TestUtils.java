package com.yunbaba.freighthelper.utils;

import java.util.ArrayList;
import java.util.List;

import com.yunbaba.freighthelper.bean.msg.MsgInfo;

public class TestUtils {

	public static List<MsgInfo> getMsgList() {
		List<MsgInfo> list = new ArrayList<MsgInfo>();

		MsgInfo parm0 = new MsgInfo();
		parm0.setMessageId(10000);
		parm0.setMsgType(99);
		parm0.setCreatetime("1490685525");
		// 加入车队: 1008#邀请码#有效截止UTC时间#企业ID#车队ID#物流企业#车队名称#lockcorpid
		parm0.setContent("1008#yaoqingma#2017-03-28#corpid#groupid#马蹄圈物流公司#深圳凯立德车队#lockcorpid");
		list.add(parm0);

		MsgInfo parm1 = new MsgInfo();
		parm1.setMessageId(10001);
		parm1.setMsgType(99);
		parm1.setCreatetime("1490595125");
		// 退出车队: 1010#踢出UTC时间#企业ID#车队ID#物流企业#车队名称
		parm1.setContent("1010#2017-03-28#corpid#groupid#马蹄圈物流公司#深圳凯立德车队");
		list.add(parm1);

		MsgInfo parm2 = new MsgInfo();
		parm2.setMessageId(10002);
		parm2.setMsgType(99);
		parm2.setCreatetime("1490405431");
		// 普通任务消息: 1001#配送任务ID#运货点数量#物流企业#标题信息#送货车辆#企业ID
		parm2.setContent("1001#123456789ZQY#5#马蹄圈物流公司#信息#送货车辆#corpid");
		list.add(parm2);

		MsgInfo parm3 = new MsgInfo();
		parm3.setMessageId(10003);
		parm3.setMsgType(99);
		parm3.setCreatetime("1490319031");
		// 提醒任务消息(自定义): 1099#配送任务ID#物流企业#企业ID#消息内容#筛选ID
		parm3.setContent("1099#123456789ZQY#马蹄圈物流公司#corpid#距离要求送达时间只剩一个小时，请尽快配送#14");
		list.add(parm3);

		MsgInfo parm4 = new MsgInfo();
		parm4.setMessageId(10004);
		parm4.setMsgType(99);
		parm4.setCreatetime("1487899831");
		// K码调度消息: 1006#凯立德K码#物流企业#消息内容#POI名称#POI地址
		parm4.setContent("1006#凯立德K码#马蹄圈物流公司#请下午到福田区凯立德公司来#凯立德科技股份有限公司#深南大道6024号创建大厦");
		list.add(parm4);

		MsgInfo parm5 = new MsgInfo();
		parm5.setMessageId(10005);
		parm5.setMsgType(99);
		parm5.setCreatetime("1490409592");
		// 普通调度消息: 1004#物流企业#信息内容
		parm5.setContent("1004#马蹄圈物流公司#信息内容");
		list.add(parm5);

		MsgInfo parm6 = new MsgInfo();
		parm6.setMessageId(10006);
		parm6.setMsgType(99);
		parm6.setCreatetime("1490771524");
		// 报警消息：2002#车牌号码#报警ID#报警时间#x#y#报警名称#报警详情
		parm6.setContent("2002#粤B88888#103#1490471985#x#y#报警名称#您已持续行驶超过4小时， 请注意安全，不要疲劳驾驶！");
		list.add(parm6);

		MsgInfo parm7 = new MsgInfo();
		parm7.setMessageId(10007);
		parm7.setMsgType(99);
		parm7.setCreatetime("1490692785");
		// 报警消息：2002#车牌号码#报警ID#报警时间#x#y#报警名称#报警详情
		parm7.setContent("2002#粤B88888#217#1490471985#x#y#报警名称#车辆在行驶过程中，左前门没有关闭，请注意安全！");
		list.add(parm7);

		MsgInfo parm8 = new MsgInfo();
		parm8.setMessageId(10008);
		parm8.setMsgType(99);
		parm8.setCreatetime("1490583345");
		// 报警消息：2002#车牌号码#报警ID#报警时间#x#y#报警名称#报警详情
		parm8.setContent("2002#粤B88888#109#1490471985#x#y#报警名称#OBD终端设备已断电， 不要直接从车辆取电，暂时靠终端电池供电，请尽快检查保证设备持续供电。");
		list.add(parm8);

		MsgInfo parm9 = new MsgInfo();
		parm9.setMessageId(10009);
		parm9.setMsgType(99);
		parm9.setCreatetime("1490471985");
		// 报警消息：2002#车牌号码#报警ID#报警时间#x#y#报警名称#报警详情
		parm9.setContent("2002#粤B88888#219#1490471985#x#y#报警名称#水温过高，请检查后安全行驶！");
		list.add(parm9);

		return list;
	}
	
	public static List<MsgInfo> getJoinList() {
		List<MsgInfo> list = new ArrayList<MsgInfo>();

		MsgInfo parm0 = new MsgInfo();
		parm0.setMessageId(10000);
		parm0.setMsgType(99);
		parm0.setCreatetime("1490685525");
		// 加入车队: 1008#邀请码#有效截止UTC时间#企业ID#车队ID#物流企业#车队名称#lockcorpid
		parm0.setContent("1008#yaoqingma#2017-03-28#corpid#groupid#马蹄圈物流公司#深圳凯立德车队#0");
		list.add(parm0);

		return list;
	}
}
