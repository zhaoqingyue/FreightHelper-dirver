/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: CldTriangle.java
 * @Prject: PersonalCenter
 * @Package: com.matiquan.personalcenter.ui.customview
 * @Description: 自定义三角形
 * @author: zhaoqy
 * @date: 2017年2月21日 下午4:19:34
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class Triangle extends View {
	private Paint mPaint;
	private static int DEFAULT_WIDTH = 26;
	private static int DEFAULT_HEIGHT = 24;
	private static int DEFAULT_COLOR = 0xffffffff;

	public Triangle(Context context) {
		super(context);
		init();
	}

	public Triangle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Triangle(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		/*DEFAULT_COLOR = PCContext.getContext().getResources()
				.getColor(R.color.triangle_color);*/
		mPaint = new Paint();
		mPaint.setColor(DEFAULT_COLOR);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		//DEFAULT_WIDTH = getWidth();
		//DEFAULT_HEIGHT = getHeight();
		
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int) DEFAULT_WIDTH + getPaddingLeft() + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int) DEFAULT_HEIGHT + getPaddingTop()
					+ getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Path path = new Path();

		/*
		 * 倒三角 filepathspaths.xml.moveTo(0, 0); filepathspaths.xml.lineTo(DEFAULT_WIDTH / 2,
		 * DEFAULT_HEIGHT); filepaths.xmls.xml.lineTo(DEFAULT_WIDTH, 0);
		 */
		// 正三角
		path.moveTo(0, DEFAULT_HEIGHT);
		path.lineTo(DEFAULT_WIDTH / 2, 0);
		path.lineTo(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		path.close();
		canvas.drawPath(path, mPaint);
	}

}
