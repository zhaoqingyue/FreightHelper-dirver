/*
 * @Title package-info.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2016-3-2 下午4:02:31
 * @version 1.0
 */
/** 
 * 货运相关
 * 
 * 货运服务错误码对应表：
 * 所有接口case的通用错误码
 * CldOlsErrCode.PARAM_INVALID 参数错误
 * CldOlsErrCode.ACCOUT_NOT_LOGIN 未登录或登录失效
 * CldOlsErrCode.NET_TIMEOUT  网络超时
 * CldOlsErrCode.NET_OTHER_ERR 网络异常
 * 
 * 1900 系统错误
 * 特殊接口错误码
 * 1. joinGroup(1020车队邀请已失效（含邀请码不存在）,1021车队不存在,1022已加入其它车队,1023无权限加入车队,1039加入车队失败,1301不允许有加入其它企业车队,1302该用户已超过允许加入的车队数量,1303该车队的成员数已达到上限)
 * 2. unJoinGroup (1020车队邀请已失效（含邀请码不存在）,1021车队不存在)
 * 3. addMonitorAuth(1070 手机号已授权  1071 授权列表已达上限)
 * 4.updateDeliTaskStoreStatus(1401当前运单已存在执行的运货点)
 * @author Zhouls
 * @date 2016-3-2 下午4:02:31
 */
package com.yunbaba.ols.module.delivery;

