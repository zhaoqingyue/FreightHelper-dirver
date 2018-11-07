package com.yunbaba.freighthelper.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class UserUtils {
	public static final int MAX_INPUT_LEN = 21; // 最长输入长度
	public static final int MIN_LEN_OF_USER_NAME = 3; // 用户名最小输入长度
	public static final int MAX_LEN_OF_USER_NAME = 15; // 用户名最大输入长度
	public static final int MIN_LEN_OF_USER_PASS = 6; // 密码最小输入长度
	public static final int MAX_LEN_OF_USER_PASS = 14; // 密码最大输入长度
	private static boolean bEmail = false;

	public static enum InputError {
		eERROR_NONE, eERROR_BASE, // 异常错误
		eERROR_ACCOUNT_EMPTY, // 账号为空
		eERROR_PASSWORD_EMPTY, // 密码为空
		eERROR_EMAIL_INPUT, // 错误的邮箱地址
		eERROR_ACCOUNT_INPUT, // 无效的账号
		eERROR_PASSWORD_INPUT, // 无效的密码
		eERROR_PASSWORD_CONTAINS_SPECAIL, // 密码包含特殊字符
		eERROR_PASSWORD_LESSONENUM, // 新密码至少包含1个数字和1个字母
		eERROR_OLD_PASSWORD_EMPTY, // 旧密码为空
		eERROR_NEW_PASSWORD_EMPTY, // 新密码为空
		eERROR_AFFIRM_PASSWORD_EMPTY, // 确认密码为空
		eERROR_NEW_OLD_SAME, // 新旧密码一致
		eERROR_NEW_AFFIRM_UNSAME, // 输入的新密码不一致
	};

	public static InputError checkInputIsValid(String strUsername,
			String strPassword) {
		if (null == strUsername || null == strPassword) {
			return InputError.eERROR_BASE;
		}

		if (TextUtils.isEmpty(strUsername)) {
			return InputError.eERROR_ACCOUNT_EMPTY; // 账号为空
		}

		if (TextUtils.isEmpty(strPassword)) {
			return InputError.eERROR_PASSWORD_EMPTY; // 密码为空
		}

		bEmail = isEmail(strUsername);
		if (bEmail
				&& (strUsername.length() < MIN_LEN_OF_USER_NAME || strUsername
						.length() > MAX_INPUT_LEN - 1)) // 邮箱
		{
			return InputError.eERROR_EMAIL_INPUT; // 错误的邮箱地址
		} else if (!bEmail
				&& (strUsername.length() < MIN_LEN_OF_USER_NAME || strUsername
						.length() > MAX_LEN_OF_USER_NAME)) {
			return InputError.eERROR_ACCOUNT_INPUT; // 无效的输入
		}

		if (strPassword.length() < MIN_LEN_OF_USER_PASS
				|| strPassword.length() > MAX_LEN_OF_USER_PASS) {
			return InputError.eERROR_PASSWORD_INPUT; // 无效的输入
		}

		return InputError.eERROR_NONE;
	}

	private static final boolean isEmail(String strInput) {
		boolean bFlag1 = false; // @
		boolean bFlag2 = false; // .
		boolean bFlag3 = false; // [a-zA-Z0-9_-]
		boolean bFlag4 = false; // @和.间有串
		boolean bFlag5 = false; // .后有串
		char[] stTmpInput = strInput.toCharArray();

		if (null == strInput || TextUtils.isEmpty(strInput)) {
			return false;
		}

		for (int i = 0; i < stTmpInput.length; i++) {
			if (stTmpInput[i] == '@') {
				if (bFlag1) {
					return false;
				}

				if (bFlag3 == false) {
					return false;
				}

				bFlag1 = true;
				continue;
			} else if (stTmpInput[i] == '.') {
				if (bFlag2) {
					return false;
				}

				if (bFlag1 == false || bFlag3 == false || bFlag4 == false) {
					return false;
				}
				bFlag2 = true;
				continue;
			}

			if ((stTmpInput[i] >= 'a' && stTmpInput[i] <= 'z')
					|| (stTmpInput[i] >= 'A' && stTmpInput[i] <= 'Z')
					|| (stTmpInput[i] >= '0' && stTmpInput[i] <= '9')
					|| (stTmpInput[i] == '_') || (stTmpInput[i] == '-')) {
				bFlag3 = true;
				if (bFlag1 == true && bFlag2 == false) {
					bFlag4 = true;
				}

				if (bFlag1 == true && bFlag2 == true && bFlag4 == true) {
					bFlag5 = true;
				}
			} else {
				return false; // 非法的
			}

		}

		if (bFlag1 && bFlag2 && bFlag3 && bFlag4 && bFlag5) {
			return true;
		}

		return false;
	}

	/**
	 * 密码至少含一个字母一个数字
	 * 
	 * @param strPassword
	 * @return
	 * @return boolean
	 */
	public static boolean isNewPwdInValid(String strPassword) {
		boolean isInvalid = false;

		String regx = "[a-zA-Z]";
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(strPassword);
		if (matcher.find()) {
			isInvalid = true;
		}
		regx = "[0-9]";
		pattern = Pattern.compile(regx);
		matcher = pattern.matcher(strPassword);
		if (!matcher.find()) {
			isInvalid = false;
		}
		return isInvalid;
	}

	/**
	 * 是否含有特殊字符
	 * 
	 * @param strPassword
	 * @return
	 * @return boolean
	 */
	public static boolean isNewPwdContainsSpecail(String strPassword) {
		String regx = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(strPassword);
		if (!matcher.matches()) {
			// 不包含特殊字符
			return false;
		}
		return true;
	}

	/**
	 * 校验重置密码
	 * 
	 * @param strPassword
	 * @return
	 */
	public static InputError checkRetrivePwd(String strPassword) {
		if (TextUtils.isEmpty(strPassword)) {
			return InputError.eERROR_PASSWORD_EMPTY; // 密码为空
		} else if (strPassword.length() < MIN_LEN_OF_USER_PASS
				|| strPassword.length() > MAX_LEN_OF_USER_PASS) {
			return InputError.eERROR_PASSWORD_INPUT; // 无效的输入
		} else if (!isNewPwdContainsSpecail(strPassword)) {
			return InputError.eERROR_PASSWORD_CONTAINS_SPECAIL; // 密码包含特殊字符
		} else if (!isNewPwdInValid(strPassword)) {
			return InputError.eERROR_PASSWORD_LESSONENUM; // 新密码至少包含1个数字和1个字母
		}

		return InputError.eERROR_NONE;
	}

	/**
	 * 校验修改密码NewPwd
	 * 
	 * @param mContext
	 * @param oldPwd
	 * @param newPwd
	 * @param newPwdConfirm
	 * @return void
	 */
	public static InputError checkRevisePwd(String oldPwd, String newPwd,
			String newPwdConfirm) {
		/*if (TextUtils.isEmpty(oldPwd)) {
			return InputError.eERROR_OLD_PASSWORD_EMPTY;
		} else*/ if (TextUtils.isEmpty(newPwd)) {
			return InputError.eERROR_NEW_PASSWORD_EMPTY;
		} else if (TextUtils.isEmpty(newPwdConfirm)) {
			return InputError.eERROR_AFFIRM_PASSWORD_EMPTY;
		} else if (newPwd.length() < MIN_LEN_OF_USER_PASS
				|| newPwd.length() > MAX_LEN_OF_USER_PASS) {
			return InputError.eERROR_PASSWORD_INPUT; // 无效的输入
		} else if (!isNewPwdContainsSpecail(newPwd)) {
			return InputError.eERROR_PASSWORD_CONTAINS_SPECAIL; // 密码包含特殊字符
		} else if (!isNewPwdInValid(newPwd)) {
			return InputError.eERROR_PASSWORD_LESSONENUM; // 新密码至少包含1个数字和1个字母
		} else if (newPwd.equals(oldPwd)) {
			return InputError.eERROR_NEW_OLD_SAME; // 新密码不能与旧密码相同
		} else if (!newPwd.equals(newPwdConfirm)) {
			return InputError.eERROR_NEW_AFFIRM_UNSAME; // 您输入的新密码不一致
		}

		return InputError.eERROR_NONE;
	}
}
