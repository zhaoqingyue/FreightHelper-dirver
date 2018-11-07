/*
 * @Title CldsH.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-11-21 下午12:17:18
 * @version 1.0
 */
package com.yunbaba.ols.module.delivery.tool;

import java.util.List;

/**
 * 
 * 凯立德坐标
 * 
 * @author Zhouls
 * @date 2015-5-22 上午9:00:00
 */
public class CldShapeCoords {
	/** 经度 凯立德坐标 */
	private long x;
	/** 纬度 凯立德坐标 */
	private long y;

	public CldShapeCoords() {

	}

	public CldShapeCoords(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public static CldShapeCoords toLongLatitude(CldShapeCoords cp) {
		if (null != cp) {
			return new CldShapeCoords((int) (cp.getX() / 3.6),
					(int) (cp.getY() / 3.6));
		} else {
			return null;
		}
	}

	public String valueOf() {
		return x + "+" + y;
	}

	public String valueOfDot() {
		return x + "," + y;
	}

	public static String valueOfLstDot(List<CldShapeCoords> lstOfPoint) {
		if (null != lstOfPoint) {
			String temp = "";
			for (int i = 0; i < lstOfPoint.size(); i++) {
				if (null == lstOfPoint.get(i)) {
					continue;
				}
				temp += lstOfPoint.get(i).valueOfDot() + ";";
			}
			return temp;
		}
		return "";
	}

	/** @return the x */
	public long getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(long x) {
		this.x = x;
	}

	/** @return the y */
	public long getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(long y) {
		this.y = y;
	}
}