/*
 * @Title CldKAccount.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:57
 * @version 1.0
 */
package com.yunbaba.ols.bll;

import android.os.SystemClock;
import android.text.TextUtils;

import com.cld.device.CldPhoneManager;
import com.cld.device.CldPhoneNet;
import com.cld.log.CldLog;
import com.cld.net.CldHttpClient;
import com.cld.net.CldResponse.ICldResponse;
import com.cld.net.volley.VolleyError;
import com.cld.setting.CldSetting;
import com.yunbaba.ols.api.CldKAccountAPI;
import com.yunbaba.ols.api.CldKAccountAPI.CldThirdLoginType;
import com.yunbaba.ols.api.CldOlsBase.IInitListener;
import com.yunbaba.ols.dal.CldDalKAccount;
import com.yunbaba.ols.module.delivery.tool.CldPubFuction.CldOlsThreadPool;
import com.yunbaba.ols.sap.CldSapKAccount;
import com.yunbaba.ols.sap.CldSapUtil;
import com.yunbaba.ols.sap.bean.CldSapKAParm.CldLicenceInfo;
import com.yunbaba.ols.sap.bean.CldSapKAParm.CldUserInfo;
import com.yunbaba.ols.sap.parse.CldKAccountParse.ProtDeviceInfo;
import com.yunbaba.ols.sap.parse.CldKAccountParse.ProtLogin;
import com.yunbaba.ols.sap.parse.CldKAccountParse.ProtQrCode;
import com.yunbaba.ols.sap.parse.CldKAccountParse.ProtSvrTime;
import com.yunbaba.ols.sap.parse.CldKAccountParse.ProtUserInfo;
import com.yunbaba.ols.sap.parse.CldKAccountParse.ProtUserKuid;
import com.yunbaba.ols.sap.parse.CldKAccountParse.ProtUserRegister;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtBase;
import com.yunbaba.ols.sap.parse.CldKBaseParse.ProtKeyCode;
import com.yunbaba.ols.tools.CldErrUtil;
import com.yunbaba.ols.tools.CldSapNetUtil;
import com.yunbaba.ols.tools.CldSapParser;
import com.yunbaba.ols.tools.CldSapReturn;
import com.yunbaba.ols.tools.err.CldOlsErrManager;
import com.yunbaba.ols.tools.err.CldOlsErrManager.CldOlsErrCode;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cld.setting.CldSetting.getInt;
import static com.cld.setting.CldSetting.getString;

/**
 * 帐户系统
 *
 * @author Zhouls
 * @date 2015-4-8 下午4:24:35
 */
public class CldKAccount {

    /**
     * 登录状态 0未登录 1正在登录 2已登录
     */
    private int loginStatus;
    /**
     * 是否中断登录.
     */
    private boolean interruptLogin;
    /**
     * 快捷登录的密码缓存
     */
    private String tempFastLoginPwd;

    private static CldKAccount cldKAccount;

    /**
     * Instantiates a new cld k account.
     */
    private CldKAccount() {
        interruptLogin = false;
        tempFastLoginPwd = "";
        setLoginStatus(0);
    }

    /**
     * Gets the single instance of CldKAccount.
     *
     * @return single instance of CldKAccount
     */
    public static CldKAccount getInstance() {
        if (cldKAccount == null)
            cldKAccount = new CldKAccount();
        return cldKAccount;
    }

    /**
     * 反初始化
     *
     * @return void
     * @author Zhouls
     * @date 2015-1-26 上午8:48:07
     */
    public void uninit() {
        interruptLogin = false;
        tempFastLoginPwd = "";
        setLoginStatus(0);
        CldDalKAccount.getInstance().uninit();
    }

    /**
     * 初始化密钥
     *
     * @return void
     * @author Zhouls
     * @date 2015-1-26 上午8:48:25
     */
    public void initKey() {
        String cldKAKey = CldDalKAccount.getInstance().getCldKAKey();
        int slpTimer = 5;// 休眠周期
        while (TextUtils.isEmpty(cldKAKey)) {
            /**
             * 没有密钥
             */
            int sleepSc = 0;// 休眠秒数
            if (!CldPhoneNet.isNetConnected()) {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                continue;
            }
            CldSapReturn errRes = CldSapKAccount.initKeyCode();
            String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
            ProtKeyCode protKeyCode = CldSapParser.parseJson(strRtn,
                    ProtKeyCode.class, errRes);
            CldLog.d("ols", errRes.errCode + "_initKAKey");
            CldLog.d("ols", errRes.errMsg + "_initKAKey");
            CldErrUtil.handleErr(errRes);
            if (null != protKeyCode && errRes.errCode == 0
                    && !TextUtils.isEmpty(protKeyCode.getCode())) {
                cldKAKey = protKeyCode.getCode();
                CldDalKAccount.getInstance().setCldKAKey(cldKAKey);
            } else {
                try {
                    while (sleepSc < slpTimer) {
                        Thread.sleep(1000);
                        sleepSc++;
                    }
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
            if (slpTimer >= 30) {
                /**
                 * 超过30S以后固定30S周期
                 */
                slpTimer = 30;
            } else {
                /**
                 * 不足30S每次加5S
                 */
                slpTimer += 5;
            }
        }
        CldSapKAccount.keyCode = cldKAKey;
    }

    /**
     * 初始化duid
     *
     * @return void
     * @author Zhouls
     * @date 2015-3-1 上午10:07:27
     */
    public void initDuid(IInitListener listener) {
        long duid = CldDalKAccount.getInstance().getDuid();
        int slpTimer = 5;// 休眠周期
        while (0 == duid) {
            int sleepSc = 0;// 休眠秒数
            if (!CldPhoneNet.isNetConnected()) {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                continue;
            }
            int errCode = deviceRegister().errCode;
            if (errCode == 0 || errCode == 301) {
                duid = CldDalKAccount.getInstance().getDuid();
            } else {
                try {
                    while (sleepSc < slpTimer) {
                        Thread.sleep(1000);
                        sleepSc++;
                    }
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
            if (slpTimer >= 30) {
                /**
                 * 超过30S以后固定30S周期
                 */
                slpTimer = 30;
            } else {
                /**
                 * 不足30S每次加5S
                 */
                slpTimer = +5;
            }
        }
        if (null != listener) {
            listener.onInitDuid();
        }
        /**
         * 获取到duid，比较MD5duid看是否需要更新设备信息
         */
        String sourceStr = duid
                + CldSapUtil.isStrParmRequest("", CldBllUtil.getInstance()
                .getWifiMac())
                + CldSapUtil.isStrParmRequest("", CldBllUtil.getInstance()
                .getBlueMac())
                + CldSapUtil.isStrParmRequest("", CldBllUtil.getInstance()
                .getImei())
                + CldSapUtil.isStrParmRequest("", CldBllUtil.getInstance()
                .getImsi()) + CldSapUtil.isStrParmRequest("", CldBllUtil.getInstance()
                .getAppver());
        String MD5duid = CldSapUtil.MD5(sourceStr);

        String checkduid =  CldSetting.getString("MD5duid");

        if (!MD5duid.equals(checkduid)) {
            int errCode = deviceRegister().errCode;
            if (errCode == 301) {
                updateDeviceInfo();
            }
        }
    }

    /**
     * 设备注册
     *
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:26:29
     */
    public CldSapReturn deviceRegister() {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount.deviceRegister(CldBllUtil.getInstance()
                            .getDeviceCode(), CldBllUtil.getInstance().getDeviceName(),
                    CldBllUtil.getInstance().getOsType(), CldBllUtil
                            .getInstance().getApptype(), CldBllUtil
                            .getInstance().getSDKVersion(), CldBllUtil
                            .getInstance().getModel(), CldBllUtil.getInstance()
                            .getWifiMac(), CldBllUtil.getInstance()
                            .getBlueMac(), CldBllUtil.getInstance().getImei(),
                    CldBllUtil.getInstance().getSN(), CldBllUtil.getInstance()
                            .getProver(), null, CldBllUtil.getInstance()
                            .getAppver(), CldBllUtil.getInstance().getMapver());
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            ProtDeviceInfo protDeviceInfo = CldSapParser.parseJson(strRtn,
                    ProtDeviceInfo.class, errRes);
            CldLog.d("ols", "deviceRegister json = " + strRtn);
            CldLog.d("ols", "DeviceCode:"
                    + CldBllUtil.getInstance().getDeviceCode());
            CldLog.d("ols", errRes.errCode + "_deviceReg");
            CldLog.d("ols", errRes.errMsg + "_deviceReg");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (null != protDeviceInfo) {
                if (errRes.errCode == 0 || errRes.errCode == 301) {
                    CldDalKAccount.getInstance().setDuid(
                            protDeviceInfo.getDuid());
                    String sourceStr = CldDalKAccount.getInstance().getDuid()
                            + CldSapUtil.isStrParmRequest("", CldBllUtil
                            .getInstance().getWifiMac())
                            + CldSapUtil.isStrParmRequest("", CldBllUtil
                            .getInstance().getBlueMac())
                            + CldSapUtil.isStrParmRequest("", CldBllUtil
                            .getInstance().getImei())
                            + CldSapUtil.isStrParmRequest("", CldBllUtil
                            .getInstance().getImsi()) + CldSapUtil.isStrParmRequest("", CldBllUtil.getInstance()
                            .getAppver());;
                    String MD5duid = CldSapUtil.MD5(sourceStr);
                    CldSetting.put("MD5duid", MD5duid);
                }
            }
        }
        return errRes;
    }

    /**
     * 更新设备信息
     *
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:26:40
     */
    public CldSapReturn updateDeviceInfo() {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount.updateDeviceInfo(CldDalKAccount
                            .getInstance().getDuid(), CldBllUtil.getInstance()
                            .getDeviceName(), CldBllUtil.getInstance().getOsType(),
                    CldBllUtil.getInstance().getApptype(), CldBllUtil
                            .getInstance().getSDKVersion(), CldBllUtil
                            .getInstance().getModel(), CldBllUtil.getInstance()
                            .getWifiMac(), CldBllUtil.getInstance()
                            .getBlueMac(), CldBllUtil.getInstance().getImei(),
                    CldBllUtil.getInstance().getSN(), CldBllUtil.getInstance()
                            .getProver(), null, CldBllUtil.getInstance()
                            .getAppver(), CldBllUtil.getInstance().getMapver());
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_deviceUpdte");
            CldLog.d("ols", errRes.errMsg + "_deviceUpdte");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 判断用户是否已注册
     *
     * @param loginName 登录名（电话号码,邮箱,用户名）
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:26:51
     */
    public CldSapReturn isRegisterUser(String loginName) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount.isRegisterUser(loginName, getSvrTime());
            String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
            ProtUserKuid protUserKuid = CldSapParser.parseJson(strRtn,
                    ProtUserKuid.class, errRes);
            CldLog.d("ols", errRes.errCode + "_isRegUser");
            CldLog.d("ols", errRes.errMsg + "_isRegUser");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (null != protUserKuid) {
                CldDalKAccount.getInstance().setKuidRegUser(
                        protUserKuid.getKuid());
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 校验手机验证码
     *
     * @param mobile        手机号
     * @param verifycode    验证码
     * @param bussinessCode 业务ID
     * @return
     * @author Zhouls
     * @date 2015-10-9 上午10:06:16
     */
    public CldSapReturn checkMobileVeriCode(String mobile, String verifycode,
                                            int bussinessCode) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            long timeStamp = CldKAccount.getInstance().getSvrTime();
            errRes = CldSapKAccount.checkMobileVeriCode(mobile, verifycode,
                    bussinessCode, timeStamp);
            CldLog.d("ols", "errRes.url: " + errRes.url);
            CldLog.d("ols", "errRes.jsonPost: " + errRes.jsonPost);
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_checkCode");
            CldLog.d("ols", errRes.errMsg + "_checkCode");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
        } else {
            errRes.errCode = -2;
        }
        return errRes;

    }

    /**
     * 通过上行短信注册
     *
     * @param password 用户设置的密码
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:27:07
     */
    public CldSapReturn registerBySms(String password) {
        CldSapReturn errRes = new CldSapReturn();
        String guid = CldSapUtil.getGuid(getSvrTime());
        int svrType = CldBllUtil.getInstance().getUimtype();
        String address = CldSapUtil.getSvrAddr(svrType);
        if (CldPhoneManager.isSimReady()) {
            // SIM卡正常
            if (CldPhoneNet.isNetConnected()) {
                // 网络正常
                String smsContent;// 短信内容
                smsContent = 3 + "|" + guid + "|" + CldSapUtil.MD5(password);
                CldSapUtil.sendSMS(address, smsContent, CldBllUtil
                        .getInstance().getContext());
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                for (int i = 0; i < 6; i++) {
                    String ip = CldSapUtil.getLocalIpAddress();
                    errRes = CldSapKAccount.registerBySms(guid, ip);
                    String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                            errRes.jsonPost);
                    ProtUserRegister protUserRegister = CldSapParser.parseJson(
                            strRtn, ProtUserRegister.class, errRes);
                    CldLog.d("ols", errRes.errCode + "_regBySms");
                    CldLog.d("ols", errRes.errMsg + "_regBySms");
                    CldErrUtil.handleErr(errRes);
                    errCodeFix(errRes);
                    if (null != protUserRegister) {
                        CldDalKAccount.getInstance().setKuidRegSms(
                                protUserRegister.getKuid());
                        CldDalKAccount.getInstance().setLoginNameRegSms(
                                protUserRegister.getLoginname());
                    }
                    if (errRes.errCode == 0) {
                        return errRes;
                    }
                    if (errRes.errCode == 201) {
                        // 手机已被认证
                        return errRes;
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
            } else {
                errRes.errCode = -3;
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 登录
     *
     * @param loginName 登录名
     * @param loginPwd  密码
     * @param pwdtype   密码类型,1普通登录密码（默认）,2快捷登录密码,
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:27:24
     */
    public synchronized CldSapReturn login(String loginName, String loginPwd,
                                           int pwdtype) {
        CldSapReturn errRes = new CldSapReturn();
        long duid = CldDalKAccount.getInstance().getDuid();
        if (duid != 0) {
            // 成功获取到duid
            if (CldPhoneNet.isNetConnected()) {
                setLoginStatus(1);
                // 网络正常
                long timestamp = getSvrTime();
                /**
                 * 拼接请求URL
                 */
                errRes = CldSapKAccount.login(loginName, loginPwd, CldBllUtil
                                .getInstance().getBussinessid(), pwdtype, timestamp,
                        null, duid);
                /**
                 * 网络请求
                 */
                String strRtn = CldSapNetUtil.sapPostMethod2(errRes.url,
                        errRes.jsonPost);
                /**
                 * 解析结果
                 */
                ProtLogin protLogin = CldSapParser.parseJson(strRtn,
                        ProtLogin.class, errRes);
                CldLog.d("ols", errRes.errCode + "_login");
                CldLog.d("ols", errRes.errMsg + "_login");
                CldErrUtil.handleErr(errRes);
                kaKeyFix(errRes.errCode);
                if (null != protLogin && errRes.errCode == 0) {
                    // 外面判断是否登录成功的标识
                    setLoginStatus(2);
                    CldDalKAccount.getInstance().setLoginName(loginName);
                    CldDalKAccount.getInstance().setLoginPwd(loginPwd);
                    CldDalKAccount.getInstance().setPwdtype(pwdtype);
                    CldDalKAccount.getInstance().setKuid(protLogin.getKuid());
                    CldDalKAccount.getInstance().setSession(
                            protLogin.getSession());
                    protLogin.protParse(CldDalKAccount.getInstance()
                            .getCldUserInfo());
                    CldKAccountAPI.getInstance().getUserInfo();
                } else {
                    // 其他原因登录失败(102,103,104帐号原因登录补上其他服务端故障)
                    if (errRes.errCode == 910) {
                        /**
                         * 时间被更改
                         */
                        initSvrTime();
                    } else {
                        setLoginStatus(0);
                        //CldDalKAccount.getInstance().uninit();
                        //	CldDalKAccount.getInstance().setLoginPwd("");
                    }
                }
            } else {
                errRes.errCode = -2;
                errRes.errMsg = "网络异常";
            }
        } else {
            errRes.errCode = -1;
            errRes.errMsg = "duid初始化失败";

        }
        return errRes;
    }

    /**
     * 注销登录
     *
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:27:45
     */
    public CldSapReturn loginOut() {
        CldSapReturn errRes = new CldSapReturn();
        CldDalKAccount.getInstance().uninit();
        CldDalKAccount.getInstance().setLoginPwd("");
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount.loginOut(CldDalKAccount.getInstance()
                            .getKuid(), CldDalKAccount.getInstance().getSession(),
                    CldBllUtil.getInstance().getBussinessid());
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_loginOut");
            CldLog.d("ols", errRes.errMsg + "_loginOut");
            CldErrUtil.handleErr(errRes);
            setLoginStatus(0);
            kaKeyFix(errRes.errCode);
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 登录状态监听
     *
     * @author Zhouls
     * @date 2015-4-8 下午4:27:55
     */
    public interface IAutoLoginListener {

        /**
         * 登录状态监听回调
         *
         * @param loginState 登录状态（0未登录,1正在登录,2登录成功，3登录失败,4外部中断）
         *                   <p>
         *                   登录返回的errCode
         * @return void
         * @author Zhouls
         * @date 2014-9-1 下午5:46:38
         */
        public void onLoginStateChange(int loginState, CldSapReturn err);
    }

    /**
     * 循环登录
     *
     * @param listener 自动登录回调
     * @return void
     * @author Zhouls
     * @date 2015-2-12 上午10:34:21
     */
    public void cycleLogin(IAutoLoginListener listener) {
        int loginState = 0;
        CldSapReturn errRes = new CldSapReturn();
        /**
         * 先清理异常退出程序时未清理掉的session和kuid
         */
      //  CldKAccountAPI.getInstance().cleanKuid();
      //  CldKAccountAPI.getInstance().cleanSession();
        /**
         * 未登录(0,-2)
         */
        if (null != listener) {
            listener.onLoginStateChange(loginState, errRes);
        }
        // 取得保存的用户名和密码ols_ka_pwdtype
        int pwdtype = getInt("ols_ka_pwdtype");
       // int pwdtype = 2;
        //needchangechange
        if (pwdtype == 0) {
            /**
             * 兼容之前没有此字段版本正常自动登录
             */
            pwdtype = 1;


        }





        String username = CldDalKAccount.getInstance().getLoginName();
        String password = CldDalKAccount.getInstance().getLoginPwd();





        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            // 只要没有中断登录
            int slpTimer = 3;// 休眠周期
            interruptLogin = false;
            while (!interruptLogin) {
                int sleepSc = 0;// 休眠秒数
                loginState = 1;
                errRes.errCode = -3;
                /**
                 * 正在登录(1,-3) -3还未开始登录
                 */
                setLoginStatus(1);
                if (null != listener) {
                    listener.onLoginStateChange(loginState, errRes);
                }
                // 登录
                errRes = login(username, password, pwdtype);
                CldLog.d("ols", "autologin errorCode = " + errRes.errCode);
                if (errRes.errCode == 104) {
                    // 密码错误
                    loginState = 3;
                    /**
                     * 登录失败(3,104)
                     */
                    if (null != listener) {
                        listener.onLoginStateChange(loginState, errRes);
                    }
                    // 中断登录
                    interruptLogin = true;
                } else {
                    if (errRes.errCode == 0) {
                        loginState = 2;
                        /**
                         * 登录成功(2,0)
                         */
                        if (null != listener) {
                            listener.onLoginStateChange(loginState, errRes);
                        }
                        // 中断登录
                        interruptLogin = true;
                        if (null == listener) {
                            /**
                             * session 失效重登陆 做此处理 避免短时间多个接口触发重新登录
                             */
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        }
                    } else if (errRes.errCode == 102 || errRes.errCode == 104) {
                        switch (errRes.errCode) {
                            case 102:
                                interruptLogin = true;
                                CldSetting.put("userName", "");
                                loginState = 0;
                                listener.onLoginStateChange(loginState, errRes);
                                break;
                            case 104:
                                interruptLogin = true;
                                CldSetting.put("password", "");
                                break;
                        }
                    } else {

                        loginState = 1;
                        /**
                         * 正在登录(1,-1||-2)(未获取到duid||网络异常)
                         */
                        if (null != listener) {
                            listener.onLoginStateChange(loginState, errRes);
                        }
                        //try {
                        //	while (slpTimer<9) {
                        //Thread.sleep(1000);
                        //sleepSc++;
                        // 中断登录
                        if (interruptLogin) {
                            loginState = 4;
                            /**
                             * 外部中断(4,errCode)
                             */
                            if (null != listener) {
                                listener.onLoginStateChange(loginState,
                                        errRes);
                            }
                        }
                        //}
//						} catch (Exception e) {
//
//							e.printStackTrace();
//						}
                    }
                }
                if (slpTimer > 9) {
                    /**
                     * 超过30S以后固定30S周期
                     */
                    slpTimer = 90;
                    if (null != listener) {
                        listener.onLoginStateChange(4, errRes);
                    }
                    interruptLogin = true;
                } else {
                    /**
                     * 不足30S每次加5S
                     */
                    slpTimer += 3;
                }
            }


        } else {
            /**
             * 未登录(0,-1)(用户名和密码为空)
             */
            errRes.errCode = -1;
            if (null != listener) {
                listener.onLoginStateChange(loginState, errRes);
            }
        }
    }

    /**
     * 自动登录
     *
     * @param listener 登录状态监听
     * @return void
     * @author Zhouls
     * @date 2014-9-2 上午8:54:24
     */
    public void startAutoLogin(IAutoLoginListener listener) {
        cycleLogin(listener);
    }

    /**
     * session失效重新登录
     *
     * @return void
     * @author Zhouls
     * @date 2015-2-5 下午4:01:12
     */
    public void sessionRelogin() {
        CldOlsThreadPool.submit(new Runnable() {

            @Override
            public void run() {

                cycleLogin(null);
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:28:21
     */
    public CldSapReturn getUserInfo() {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            if (getLoginStatus() == 2) {
                errRes = CldSapKAccount.getUserInfo(CldDalKAccount
                        .getInstance().getKuid(), CldDalKAccount.getInstance()
                        .getSession(), CldBllUtil.getInstance()
                        .getBussinessid(), 1);
                String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
                ProtUserInfo protUserInfo = CldSapParser.parseJson(strRtn,
                        ProtUserInfo.class, errRes);
                CldLog.d("ols", errRes.errCode + "_getUserInfo");
                CldLog.d("ols", errRes.errMsg + "_getUserInfo");
                CldErrUtil.handleErr(errRes);
                errCodeFix(errRes);
                if (null != protUserInfo) {
                    CldUserInfo userInfor = CldDalKAccount.getInstance()
                            .getCldUserInfo();
                    if (errRes.errCode == 0) {
                        protUserInfo.protParse(userInfor);
                    }
                }
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 同步服务器时间
     *
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:28:32
     */
    public void initSvrTime() {
        CldSapReturn errRes = new CldSapReturn();
        boolean isInited = false;
        int slpTimer = 5;// 休眠周期
        while (!isInited) {
            int sleepSc = 0;// 休眠秒数
            if (!CldPhoneNet.isNetConnected()) {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                continue;
            }
            errRes = CldSapKAccount.getKAconfig();
            String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
            ProtSvrTime protSvrTime = CldSapParser.parseJson(strRtn,
                    ProtSvrTime.class, errRes);
            CldLog.d("ols", errRes.errCode + "_initSvrtime");
            CldLog.d("ols", errRes.errMsg + "_initSvrtime");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (null != protSvrTime && null != protSvrTime.getData()
                    && errRes.errCode == 0) {
                // long timeDif = CldSapUtil.getLocalTime()
                // - protSvrTime.getData().getServer_time();
                long timeDif = SystemClock.elapsedRealtime() / 1000
                        - protSvrTime.getData().getServer_time();
                CldDalKAccount.getInstance().setTimeDif(timeDif);
                isInited = true;
            } else {
                try {
                    while (sleepSc < slpTimer) {
                        Thread.sleep(1000);
                        sleepSc++;
                    }
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
            if (slpTimer >= 30) {
                /**
                 * 超过30S以后固定30S周期
                 */
                slpTimer = 30;
            } else {
                /**
                 * 不足30S每次加5S
                 */
                slpTimer += 5;
            }
        }
    }

    /**
     * 获取手机验证码
     *
     * @param mobile        电话号码
     * @param bussinessCode 业务类型 101注册 102绑定 103改绑 104解绑 105重置密码 106快捷登录
     * @param oldmoble      103必传
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:28:48
     */
    public CldSapReturn getVerifyCode(String mobile, int bussinessCode,
                                      String oldmoble) {
        CldSapReturn errRes = new CldSapReturn();
        long timeStamp = CldKAccount.getInstance().getSvrTime();
        if (CldPhoneNet.isNetConnected()) {
            switch (bussinessCode) {
                case 101:
                case 105:
                case 106:
                    errRes = CldSapKAccount.getVerifyCode(mobile, bussinessCode,
                            timeStamp, 0, null, -1, null);
                    break;
                case 103:
                    errRes = CldSapKAccount.getVerifyCode(mobile, bussinessCode,
                            timeStamp, CldDalKAccount.getInstance().getKuid(),
                            CldDalKAccount.getInstance().getSession(), CldBllUtil
                                    .getInstance().getBussinessid(), oldmoble);
                    break;
                case 102:
                case 104:
                    errRes = CldSapKAccount.getVerifyCode(mobile, bussinessCode,
                            timeStamp, CldDalKAccount.getInstance().getKuid(),
                            CldDalKAccount.getInstance().getSession(), CldBllUtil
                                    .getInstance().getBussinessid(), null);
                    break;
            }
            String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_getVericode");
            CldLog.d("ols", errRes.errMsg + "_getVericode");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 注册（个性账号)
     *
     * @param password   密码

     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:29:55
     */
    public CldSapReturn registerEx(String username, String password) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            String ip = CldSapUtil.getLocalIpAddress();
            errRes = CldSapKAccount.registerEx(username, password, ip);
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            ProtUserRegister protUserRegister = CldSapParser.parseJson(strRtn,
                    ProtUserRegister.class, errRes);
            CldLog.d("ols", errRes.errCode + "_regbycode");
            CldLog.d("ols", errRes.errMsg + "_regbycode");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (null != protUserRegister) {
                CldDalKAccount.getInstance().setLoginNameReg(
                        protUserRegister.getLoginname());
                CldDalKAccount.getInstance().setKuidReg(
                        protUserRegister.getKuid());
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 注册（带手机验证码)
     *
     * @param mobile     电话号码
     * @param password   密码
     * @param verifyCode 手机验证码
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:29:55
     */
    public CldSapReturn register(String mobile, String password,
                                 String verifyCode) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            String ip = CldSapUtil.getLocalIpAddress();
            errRes = CldSapKAccount.register(mobile, password, verifyCode,
                    null, ip);
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            ProtUserRegister protUserRegister = CldSapParser.parseJson(strRtn,
                    ProtUserRegister.class, errRes);
            CldLog.d("ols", errRes.errCode + "_regbycode");
            CldLog.d("ols", errRes.errMsg + "_regbycode");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (null != protUserRegister) {
                CldDalKAccount.getInstance().setLoginNameReg(
                        protUserRegister.getLoginname());
                CldDalKAccount.getInstance().setKuidReg(
                        protUserRegister.getKuid());
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 通过手机号 验证重置密码
     *
     * @param mobile     电话号码
     * @param newPwd     新密码
     * @param verifyCode 手机验证码
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:30:07
     */
    public CldSapReturn retrievePwd(String mobile, String newPwd,
                                    String verifyCode) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount.retrievePwd(mobile, newPwd, verifyCode);
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_resetPwdByCode");
            CldLog.d("ols", errRes.errMsg + "_resetPwdByCode");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (errRes.errCode == 0) {
                CldDalKAccount.getInstance().setLoginName(mobile);
                CldDalKAccount.getInstance()
                        .setLoginPwd(CldSapUtil.MD5(newPwd));
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 修改密码（普通密码修改）
     *
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:30:23
     */
    public CldSapReturn revisePwd(String oldPwd, String newPwd) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount
                    .revisePwd(oldPwd, newPwd, CldDalKAccount.getInstance()
                            .getKuid(), CldDalKAccount.getInstance()
                            .getSession(), CldBllUtil.getInstance()
                            .getBussinessid(), 1);
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_revisePwd");
            CldLog.d("ols", errRes.errMsg + "_revisePwd");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (errRes.errCode == 0) {
                CldDalKAccount.getInstance()
                        .setLoginPwd(CldSapUtil.MD5(newPwd));
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 修改密码（快捷登录密码修改）
     *
     * @param newPwd
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:30:39
     */
    public CldSapReturn revisePwdByFastPwd(String newPwd) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            String oldPwd = CldDalKAccount.getInstance().getLoginPwd();
            errRes = CldSapKAccount
                    .revisePwd(oldPwd, newPwd, CldDalKAccount.getInstance()
                            .getKuid(), CldDalKAccount.getInstance()
                            .getSession(), CldBllUtil.getInstance()
                            .getBussinessid(), 2);
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_revisePwd");
            CldLog.d("ols", errRes.errMsg + "_revisePwd");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (errRes.errCode == 0) {
                CldDalKAccount.getInstance()
                        .setLoginPwd(CldSapUtil.MD5(newPwd));
                CldDalKAccount.getInstance().setPwdtype(1);
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 更新用户信息
     *
     * @param username  用户名称
     * @param useralias 用户别名
     * @param email     邮箱
     * @param mobile    手机
     * @param sex       性别
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:31:28
     */
    public CldSapReturn updateUserInfo(String username, String useralias,
                                       String email, String mobile, String sex, String address,
                                       byte[] photoData) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount.updateUserInfo(CldDalKAccount.getInstance()
                            .getKuid(), CldDalKAccount.getInstance().getSession(),
                    CldBllUtil.getInstance().getBussinessid(), username,
                    useralias, email, mobile, sex, address, -1, null, null,
                    photoData);
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_updateUserInfo");
            CldLog.d("ols", errRes.errMsg + "_updateUserInfo");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 绑定手机号
     *
     * @param mobile     手机号
     * @param verifycode 短信验证码
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:31:44
     */
    public CldSapReturn bindMobile(String mobile, String verifycode) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount.bindMobile(CldDalKAccount.getInstance()
                            .getKuid(), CldDalKAccount.getInstance().getSession(),
                    CldBllUtil.getInstance().getBussinessid(), mobile,
                    verifycode);
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_bindMobile");
            CldLog.d("ols", errRes.errMsg + "_bindMobile");
            if (errRes.errCode == 0) {
                CldDalKAccount.getInstance().getCldUserInfo().setMobileBind(1);
                CldDalKAccount.getInstance().getCldUserInfo().setMobile(mobile);
            }
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 解绑手机号
     *
     * @param mobile     手机号
     * @param verifycode 短信验证码
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:31:56
     */
    public CldSapReturn unbindMobile(String mobile, String verifycode) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount.unbindMobile(CldDalKAccount.getInstance()
                            .getKuid(), CldDalKAccount.getInstance().getSession(),
                    CldBllUtil.getInstance().getBussinessid(), mobile,
                    verifycode);
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_unbindMobile");
            CldLog.d("ols", errRes.errMsg + "_unbindMobile");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (errRes.errCode == 0) {
                CldDalKAccount.getInstance().getCldUserInfo().setMobile(mobile);
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 改绑手机号
     *
     * @param mobile     手机号
     * @param oldmobile  旧手机号
     * @param verifycode 短信验证码
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-4-8 下午4:32:16
     */
    public CldSapReturn updateMobile(String mobile, String oldmobile,
                                     String verifycode) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount.updateMobile(CldDalKAccount.getInstance()
                            .getKuid(), CldDalKAccount.getInstance().getSession(),
                    CldBllUtil.getInstance().getBussinessid(), oldmobile,
                    mobile, verifycode);
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
            CldLog.d("ols", errRes.errCode + "_updateMobile");
            CldLog.d("ols", errRes.errMsg + "_updateMobile");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (errRes.errCode == 0) {
                CldDalKAccount.getInstance().getCldUserInfo().setMobile(mobile);
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 修改密码后保存新密码
     *
     * @param newPwd the new login pwd
     * @return void
     * @author Zhouls
     * @date 2014-9-4 下午6:28:07
     */
    public void setLoginPwd(String newPwd) {
        String password = CldSapUtil.MD5(newPwd);
        CldSetting.put("password", password);
    }

    /**
     * 获取保存的用户名
     *
     * @return String
     * @author Zhouls
     * @date 2014-11-25 下午7:33:28
     */
    public String getLoginName() {
        return getString("userName");
    }

    /**
     * 获取保存的密码（MD5值）
     *
     * @return String
     * @author Zhouls
     * @date 2014-9-5 上午8:59:44
     */
    public String getLoginPwd() {
        return getString("password");
    }

    /**
     * 从私有数据区获取duid
     *
     * @return (空:0;不为空:duid) int
     * @author Zhouls
     * @date 2014-8-29 下午4:13:53
     */
    public long getServiceDuid() {
        return preferToLong("duid");
    }

    /**
     * 从私有数据区获取kuid
     *
     * @return (空:0;不为空:kuid) int
     * @author Zhouls
     * @date 2014-8-29 上午9:47:51
     */
    public long getServiceKuid() {
        return preferToLong("kuid");
    }

    /**
     * 中断登录
     *
     * @return void
     * @author Zhouls
     * @date 2014-8-26 上午11:09:28
     */
    public void interruptLogin() {
        interruptLogin = true;
    }

    /**
     * 取KEY转成Long
     *
     * @param key the key
     * @return long
     * @author Zhouls
     * @date 2014-9-4 下午4:48:55
     */
    private long preferToLong(String key) {
        if (getString(key).length() == 0) {
            return 0;
        } else {
            return Long.parseLong(getString(key));
        }
    }

    /**
     * 获取服务器时间
     *
     * @return 当前服务器时间 long
     * @author Zhouls
     * @date 2014-9-19 上午10:28:30
     */
    public long getSvrTime() {
        // return CldSapUtil.getLocalTime()
        // - CldDalKAccount.getInstance().getTimeDif();
        return SystemClock.elapsedRealtime() / 1000
                - CldDalKAccount.getInstance().getTimeDif();
    }

    /**
     * 返回随机生成的快捷登录密码
     *
     * @return String
     * @author Zhouls
     * @date 2015-1-19 下午4:34:06
     */
    public String randomFastLoginPwd() {
        return CldSapUtil.MD5(CldSapUtil.genRandomNum(8));
    }

    /**
     * 快捷登录
     *
     * @param mobile     手机号
     * @param verifycode 验证码
     * @return int
     * @author Zhouls
     * @date 2015-1-19 上午8:51:57
     */
    public CldSapReturn fastLogin(String mobile, String verifycode) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            String loginname = "M" + mobile;
            String fastloginpwd = randomFastLoginPwd();
           // Log.e("fastloginpwd","fastlogin"+fastloginpwd);
            setLoginStatus(1);
            errRes = CldSapKAccount.fastLogin(mobile, verifycode, fastloginpwd,
                    loginname, CldDalKAccount.getInstance().getDuid(),
                    CldBllUtil.getInstance().getBussinessid());
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            ProtLogin protLogin = CldSapParser.parseJson(strRtn,
                    ProtLogin.class, errRes);
            CldLog.d("ols", errRes.errCode + "_fastLogin");
            CldLog.d("ols", errRes.errMsg + "_fastLogin");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (null != protLogin) {
                if (errRes.errCode == 0) {
                    // 外面判断是否登录成功的标识
                    setLoginStatus(2);
                    CldDalKAccount.getInstance().setLoginName(
                            protLogin.getLoginname());
                    CldDalKAccount.getInstance().setLoginPwd(fastloginpwd);
                    CldDalKAccount.getInstance().setPwdtype(2);
                    CldDalKAccount.getInstance().setKuid(protLogin.getKuid());
                    CldDalKAccount.getInstance().setSession(
                            protLogin.getSession());
                    protLogin.protParse(CldDalKAccount.getInstance()
                            .getCldUserInfo());
                    CldKAccountAPI.getInstance().getUserInfo();
                } else {
                    if (errRes.errCode == 910) {
                        /**
                         * 时间被更改
                         */
                        initSvrTime();
                    } else {
                        setLoginStatus(0);
                       // CldDalKAccount.getInstance().uninit();
                        CldDalKAccount.getInstance().setLoginPwd("");
                    }
                }
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 获取扫描登录的二维码
     *
     * @param QRsideLength 二维码边长(像素)
     * @return int
     * @author Zhouls
     * @date 2015-1-19 上午9:27:22
     */
    public CldSapReturn getQRcode(int QRsideLength) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            String fastloginpwd = randomFastLoginPwd();
            tempFastLoginPwd = fastloginpwd;
            /**
             * 是否获取二维码图像数据
             */
            int img = 0;
            int size = -1;
            if (QRsideLength > 0) {
                img = 1;
                size = QRsideLength / 25;
                if (size < 1) {
                    /**
                     * 如果像素小于1 size置1
                     */
                    size = 1;
                }
            }
            errRes = CldSapKAccount.getQRcode(CldBllUtil.getInstance()
                            .getBussinessid(), fastloginpwd, img, size, getSvrTime(),
                    CldDalKAccount.getInstance().getDuid());
            String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
            ProtQrCode protQrCode = CldSapParser.parseJson(strRtn,
                    ProtQrCode.class, errRes);
            CldLog.d("ols", errRes.errCode + "_getQRcode");
            CldLog.d("ols", errRes.errMsg + "_getQRcode");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (null != protQrCode) {
                if (errRes.errCode == 0) {
                    CldDalKAccount.getInstance().setGuid(protQrCode.getGuid());
                    CldDalKAccount.getInstance().setCreateTime(
                            protQrCode.getCreate_time());
                    CldDalKAccount.getInstance().setImgdata(
                            protQrCode.getImgdata());
                }
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 二维码登录
     *
     * @param guid 唯一标识符,扫描二维码得到的内容
     * @return int
     * @author Zhouls
     * @date 2015-1-19 上午9:45:47
     */
    public CldSapReturn loginByQRcode(String guid) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            if (getLoginStatus() == 2) {
                errRes = CldSapKAccount.loginByQRcode(CldDalKAccount
                        .getInstance().getKuid(), CldDalKAccount.getInstance()
                        .getSession(), CldBllUtil.getInstance()
                        .getBussinessid(), guid);
                String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                        errRes.jsonPost);
                CldSapParser.parseJson(strRtn, ProtBase.class, errRes);
                CldLog.d("ols", errRes.errCode + "_loginByQR");
                CldLog.d("ols", errRes.errMsg + "_loginByQR");
                CldErrUtil.handleErr(errRes);
                errCodeFix(errRes);
            } else {
                errRes.errCode = -1;
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 获取二维码登录状态
     *
     * @param guid 唯一标识符,获取二维码时所返回
     * @return int
     * @author Zhouls
     * @date 2015-1-19 上午9:57:17
     */
    public CldSapReturn getLoginStatusByQRcode(String guid) {
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            errRes = CldSapKAccount.getLoginStatusByQRcode(guid);
            String strRtn = CldSapNetUtil.sapGetMethod(errRes.url);
            ProtLogin protLogin = CldSapParser.parseJson(strRtn,
                    ProtLogin.class, errRes);
            CldLog.d("ols", errRes.errCode + "_getLoginStatus");
            CldLog.d("ols", errRes.errMsg + "_getLoginStatus");
            CldErrUtil.handleErr(errRes);
            errCodeFix(errRes);
            if (null != protLogin) {
                if (!TextUtils.isEmpty(tempFastLoginPwd)) {
                    if (errRes.errCode == 0) {
                        // 外面判断是否登录成功的标识
                        setLoginStatus(2);
                        CldDalKAccount.getInstance().setLoginName(
                                protLogin.getLoginname());
                        CldDalKAccount.getInstance().setLoginPwd(
                                tempFastLoginPwd);
                        CldDalKAccount.getInstance().setPwdtype(2);
                        CldDalKAccount.getInstance().setKuid(
                                protLogin.getKuid());
                        CldDalKAccount.getInstance().setSession(
                                protLogin.getSession());
                        protLogin.protParse(CldDalKAccount.getInstance()
                                .getCldUserInfo());
                    }
                }
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 第三方登录
     *
     * @param openid 第三方登录返回的唯一Id
     * @param type   CldThirdLoginType
     * @return CldSapReturn
     * @author Zhouls
     * @date 2015-6-10 上午9:58:43
     */
    public CldSapReturn thirdLogin(String openid,
                                   CldThirdLoginType cldThirdLoginType) {
        /**
         * 先清理异常退出程序时未清理掉的session和kuid
         */
       // CldKAccountAPI.getInstance().cleanKuid();
        CldKAccountAPI.getInstance().cleanSession();
        CldSapReturn errRes = new CldSapReturn();
        if (CldPhoneNet.isNetConnected()) {
            final String fastloginpwd = randomFastLoginPwd();
            errRes = CldSapKAccount.thirdLogin(openid, CldThirdLoginType
                    .valueOf(cldThirdLoginType), fastloginpwd, CldBllUtil
                    .getInstance().getBussinessid(), CldDalKAccount
                    .getInstance().getDuid());
            String strRtn = CldSapNetUtil.sapPostMethod(errRes.url,
                    errRes.jsonPost);
            ProtLogin protLogin = CldSapParser.parseJson(strRtn,
                    ProtLogin.class, errRes);
            if (null != protLogin) {
                if (errRes.errCode == 0) {
                    setLoginStatus(2);
                    CldDalKAccount.getInstance().setLoginName(
                            protLogin.getLoginname());
                    CldDalKAccount.getInstance().setLoginPwd(fastloginpwd);
                    CldDalKAccount.getInstance().setPwdtype(2);
                    CldDalKAccount.getInstance().setKuid(protLogin.getKuid());
                    CldDalKAccount.getInstance().setSession(
                            protLogin.getSession());
                    protLogin.protParse(CldDalKAccount.getInstance()
                            .getCldUserInfo());
                    CldLog.d("ols", errRes.errCode + "_thirdLogin");
                    CldLog.d("ols", errRes.errMsg + "_thirdLogin");
                }
                CldErrUtil.handleErr(errRes);
                errCodeFix(errRes);
            }
        } else {
            errRes.errCode = -2;
        }
        return errRes;
    }

    /**
     * 上传星级认证信息
     *
     * @param driveLicenceData   驾驶证信息
     * @param vehicleLicenceData 行驶证信息
     * @param listener
     * @return void
     * @author Zhouls
     * @date 2016-5-6 下午3:28:26
     */
    public void uploadStarAuth(byte[] driveLicenceData,
                               byte[] vehicleLicenceData, final ICldResultListener listener) {
        final CldSapReturn errRes = new CldSapReturn();
        if (!CldPhoneNet.isNetConnected()) {
            if (null != listener) {
                errRes.errCode = CldOlsErrCode.NET_NO_CONNECTED;
                listener.onGetResult(errRes.errCode);
            }
            return;
        }
        if (null == driveLicenceData || driveLicenceData.length <= 0
                || null == vehicleLicenceData || vehicleLicenceData.length <= 0) {
            if (null != listener) {
                errRes.errCode = CldOlsErrCode.PARAM_INBALID;
                listener.onGetResult(errRes.errCode);
            }
            return;
        }
        final CldSapReturn request = CldSapKAccount
                .uploadStarAuth(CldDalKAccount.getInstance().getKuid(),
                        CldDalKAccount.getInstance().getSession(), CldBllUtil
                                .getInstance().getBussinessid(),
                        driveLicenceData, vehicleLicenceData);
        CldHttpClient.post(request.url, request.jsonPost, ProtBase.class,
                new ICldResponse<ProtBase>() {

                    @Override
                    public void onResponse(ProtBase response) {
                        CldSapParser.parseObject(response, ProtBase.class,
                                errRes);
                        CldLog.d("ols", errRes.errCode + "_uploadStarAuth");
                        CldLog.d("ols", errRes.errMsg + "_uploadStarAuth");
                        CldOlsErrManager.handleErr(request, errRes);
                        errCodeFix(errRes);
                        if (errRes.errCode == 0) {
                            CldUserInfo userinfo = CldDalKAccount.getInstance()
                                    .getCldUserInfo();
                            if (null != userinfo) {
                                CldLicenceInfo licenceInfo = userinfo
                                        .getLicenceInfo();
                                if (null != licenceInfo) {
                                    // 提交成功为审核中
                                    licenceInfo.status = 1;
                                }
                                userinfo.setLicenceInfo(licenceInfo);
                            }
                        }
                        if (null != listener) {
                            listener.onGetResult(errRes.errCode);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /** 通用异常捕获 */
                        CldOlsErrManager.handlerException(error, errRes);
                        if (null != listener) {
                            listener.onGetResult(errRes.errCode);
                        }
                    }

                    @Override
                    public void onGetReqKey(String arg0) {


                    }
                });
    }

    /**
     * 错误码处理
     *
     * @param res
     * @return void
     * @author Zhouls
     * @date 2015-4-8 下午4:33:09
     */
    public void errCodeFix(CldSapReturn res) {
        switch (res.errCode) {
            case 402: {
                /**
                 * 密钥错误
                 */
                CldDalKAccount.getInstance().setCldKAKey("");
                initKey();
            }
            break;
            case 500: {
                /**
                 * session失效自动登录一次刷新session
                 */
                sessionRelogin();
            }
            break;
            case 501: {
                /**
                 * 被挤下线处理
                 */
                if (res.session.equals(CldDalKAccount.getInstance().getSession())) {
                    /**
                     * 当接口使用session和当前帐户里的session相同才挤下线
                     */
                    if (!TextUtils.isEmpty(res.session)) {
                        CldKAccountAPI.getInstance().sessionInvalid(501, 0);
                    }
                }
            }
            break;
        }
    }

    /**
     * 密钥验证机制
     *
     * @param errCode
     * @return void
     * @author Zhouls
     * @date 2015-1-23 上午11:09:26
     */
    public void kaKeyFix(int errCode) {
        switch (errCode) {
            case 402: {
                /**
                 * 密钥错误
                 */
                CldDalKAccount.getInstance().setCldKAKey("");
                initKey();
            }
            break;
        }
    }

    // synchronized ? 导航概率卡住，yyh
    public int getLoginStatus() {
        return loginStatus;
    }

    public synchronized void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }
}
