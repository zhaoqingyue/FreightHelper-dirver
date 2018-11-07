/*
 * @Title DalInput.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:58
 * @version 1.0
 */
package com.yunbaba.apitest.ui;


/**
 * The Class DalInput.
 *
 * @Description 输入数据
 * @author  Zhouls
 * @date  2014-11-14 上午10:16:08
 */
public class DalInput {
	
	/** The str ed5. */
	String strEd1, strEd2, strEd3, strEd4, strEd5;
	
	/** The dal input. */
	private static DalInput dalInput;

	/**
	 * Instantiates a new dal input.
	 */
	private DalInput() {
		strEd1 = "";
		strEd2 = "";
		strEd3 = "";
		strEd4 = "";
		strEd5 = "";
	}

	/**
	 * Gets the single instance of DalInput.
	 *
	 * @return single instance of DalInput
	 */
	public static DalInput getInstance() {
		if (null == dalInput) {
			dalInput = new DalInput();
		}
		return dalInput;
	}

	/**
	 * Put.
	 *
	 * @param st1 the st1
	 * @param st2 the st2
	 * @param st3 the st3
	 * @param st4 the st4
	 * @param st5 the st5
	 */
	public void put(String st1, String st2, String st3, String st4, String st5) {
		strEd1 = st1;
		strEd2 = st2;
		strEd3 = st3;
		strEd4 = st4;
		strEd5 = st5;

	}

	/**
	 * Gets the str ed1.
	 *
	 * @return the str ed1
	 */
	public String getStrEd1() {
		return strEd1;
	}

	/**
	 * Sets the str ed1.
	 *
	 * @param strEd1 the new str ed1
	 */
	public void setStrEd1(String strEd1) {
		this.strEd1 = strEd1;
	}

	/**
	 * Gets the str ed2.
	 *
	 * @return the str ed2
	 */
	public String getStrEd2() {
		return strEd2;
	}

	/**
	 * Sets the str ed2.
	 *
	 * @param strEd2 the new str ed2
	 */
	public void setStrEd2(String strEd2) {
		this.strEd2 = strEd2;
	}

	/**
	 * Gets the str ed3.
	 *
	 * @return the str ed3
	 */
	public String getStrEd3() {
		return strEd3;
	}

	/**
	 * Sets the str ed3.
	 *
	 * @param strEd3 the new str ed3
	 */
	public void setStrEd3(String strEd3) {
		this.strEd3 = strEd3;
	}

	/**
	 * Gets the str ed4.
	 *
	 * @return the str ed4
	 */
	public String getStrEd4() {
		return strEd4;
	}

	/**
	 * Sets the str ed4.
	 *
	 * @param strEd4 the new str ed4
	 */
	public void setStrEd4(String strEd4) {
		this.strEd4 = strEd4;
	}

	/**
	 * Gets the str ed5.
	 *
	 * @return the str ed5
	 */
	public String getStrEd5() {
		return strEd5;
	}

	/**
	 * Sets the str ed5.
	 *
	 * @param strEd5 the new str ed5
	 */
	public void setStrEd5(String strEd5) {
		this.strEd5 = strEd5;
	}
}
