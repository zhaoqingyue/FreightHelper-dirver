package com.yunbaba.freighthelper.utils;

import java.math.BigDecimal;

public class DigitsUtil {
	
	
	public static String getPrettyNumber(String number) {  
	    return BigDecimal.valueOf(Double.parseDouble(number))  
	            .stripTrailingZeros().setScale(2,BigDecimal.ROUND_DOWN).toPlainString();  
	}  
	
	
	public static String getPrettyNumber(float number) {  
	    return BigDecimal.valueOf(Double.parseDouble(String.valueOf(number))) 
	            .stripTrailingZeros().setScale(2,BigDecimal.ROUND_DOWN).toPlainString();  
	}  
	
	
	/**
	 * �ж��ַ����Ƿ�������
	 */
	public static boolean isNumber(String value) {
	    return isInteger(value) || isDouble(value);
	}
	/**
	 * �ж��ַ����Ƿ�������
	 */
	public static boolean isInteger(String value) {
	    try {
	        Integer.parseInt(value);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}

	/**
	 * �ж��ַ����Ƿ��Ǹ�����
	 */
	public static boolean isDouble(String value) {
	    try {
	        Double.parseDouble(value);
	        if (value.contains("."))
	            return true;
	        return false;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	

}
