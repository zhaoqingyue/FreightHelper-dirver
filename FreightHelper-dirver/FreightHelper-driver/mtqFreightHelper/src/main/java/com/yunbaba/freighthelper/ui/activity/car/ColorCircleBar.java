/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: ColorArcBar.java
 * @Prject: PersonalCenter
 * @Package: com.matiquan.personalcenter.ui.customview
 * @Description: 自定义彩色圆弧
 * @author: zhaoqy
 * @date: 2017年2月17日 下午4:33:15
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.activity.car;

import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.utils.DensityUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class ColorCircleBar extends View {

	private PaintFlagsDrawFilter mDrawFilter;
	private SweepGradient sweepGradient;
	private Matrix rotateMatrix;

	private Paint bgPaint;
	private Paint progressPaint;
	private Paint contentPaint;
	private Paint titlePaint;
	private Paint circlePaint;
	private RectF bgRect;

	private float centerX; // 圆心X坐标
	private float centerY; // 圆心Y坐标

	private float startAngle = 135;
	private float totalAngle = 270;
	private float currentAngle = 0;

	private float bgWidth = 0; // 背景圆弧的宽度
	private float progressWidth = 0; // 进度圆弧的宽度

	private float titleSize = 0;
	private float contentSize = 0;

	private float maxValues = 100;
	private float curValues = 0;
	private float k;

	private int diameter = 0; // 直径
	private int bgColor = 0;

	private int[] progressColors = new int[] { Color.GREEN, Color.YELLOW,
			Color.RED };
	private int[] tempProgressColors = new int[] { Color.GREEN, Color.YELLOW,
			Color.RED };

	private int titleColor = 0;
	private int contentColor = 0;
	private int tempContentColor = 0;

	private String titleString;
	private String contentString;

	public ColorCircleBar(Context context) {
		super(context, null);
		initView();
	}

	public ColorCircleBar(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		initCofig(context, attrs);
		initView();
	}

	public ColorCircleBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initCofig(context, attrs);
		initView();
	}

	/**
	 * 初始化布局配置
	 * 
	 * @param context
	 * @param attrs
	 */
	private void initCofig(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ColorArcProgressBar);

		bgColor = a.getColor(R.styleable.ColorArcProgressBar_background_color,
				Color.DKGRAY);
		int color1 = a.getColor(
				R.styleable.ColorArcProgressBar_progress_color_start,
				Color.GREEN);
		int color2 = a.getColor(
				R.styleable.ColorArcProgressBar_progress_color_middle, color1);
		int color3 = a.getColor(
				R.styleable.ColorArcProgressBar_progress_color_end, color1);
		progressColors = new int[] { color1, color2, color3 };
		tempProgressColors = progressColors;

		bgWidth = a.getDimension(
				R.styleable.ColorArcProgressBar_background_width, dipToPx(2));
		progressWidth = a.getDimension(
				R.styleable.ColorArcProgressBar_progress_width, dipToPx(10));

		diameter = a.getInteger(R.styleable.ColorArcProgressBar_diameter, 0);
		/**
		 * 适配不同分辨率的设备
		 */
		diameter = (int) (DensityUtils.getDensityWidth(context) * 0.24);

		totalAngle = a.getInteger(R.styleable.ColorArcProgressBar_total_angle,
				270);

		titleString = a.getString(R.styleable.ColorArcProgressBar_title_string);
		contentString = a
				.getString(R.styleable.ColorArcProgressBar_value_string);

		titleColor = a.getColor(R.styleable.ColorArcProgressBar_title_color, 0);
		contentColor = a.getColor(R.styleable.ColorArcProgressBar_value_color,
				0);
		tempContentColor = contentColor;

		titleSize = a.getDimension(
				R.styleable.ColorArcProgressBar_title_text_size, 0);
		contentSize = a.getDimension(
				R.styleable.ColorArcProgressBar_value_text_size, 0);

		maxValues = a.getFloat(R.styleable.ColorArcProgressBar_max_value, 100);
		curValues = a.getFloat(R.styleable.ColorArcProgressBar_cur_value, 0);

		setMaxValue(maxValues);
		setCurValue(contentString);
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = (int) (progressWidth + diameter);
		int height = (int) (progressWidth + diameter);
		setMeasuredDimension(width, height);
	}

	private void initView() {
		// 弧形的矩阵区域
		bgRect = new RectF();
		bgRect.top = progressWidth / 2;
		bgRect.left = progressWidth / 2;
		bgRect.right = diameter + progressWidth / 2;
		bgRect.bottom = diameter + progressWidth / 2;

		// 圆心
		centerX = (progressWidth + diameter) / 2;
		centerY = (progressWidth + diameter) / 2;

		// 整个弧形
		bgPaint = new Paint();
		bgPaint.setAntiAlias(true);
		bgPaint.setStyle(Paint.Style.STROKE);
		bgPaint.setStrokeWidth(progressWidth);
		bgPaint.setColor(Color.parseColor("#329d98"));
		bgPaint.setStrokeCap(Paint.Cap.ROUND);

		// 当前进度的弧形
		progressPaint = new Paint();
		progressPaint.setAntiAlias(true);
		progressPaint.setStyle(Paint.Style.STROKE);
		progressPaint.setStrokeCap(Paint.Cap.ROUND);
		progressPaint.setStrokeWidth(progressWidth);
		progressPaint.setColor(Color.GREEN);

		// 内容显示文字
		contentPaint = new Paint();
		contentPaint.setTextSize(contentSize);
		contentPaint.setColor(contentColor);
		contentPaint.setTextAlign(Paint.Align.CENTER);

		// 显示标题文字
		titlePaint = new Paint();
		titlePaint.setTextSize(titleSize);
		titlePaint.setColor(titleColor);
		titlePaint.setTextAlign(Paint.Align.CENTER);

		// 背景圆形
		circlePaint = new Paint();
		circlePaint.setColor(Color.parseColor("#309b97"));
		circlePaint.setAntiAlias(true);

		mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG);
		sweepGradient = new SweepGradient(centerX, centerY, progressColors,
				null);
		rotateMatrix = new Matrix();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 抗锯齿
		canvas.setDrawFilter(mDrawFilter);
		// 设置渐变色
		rotateMatrix.setRotate(130, centerX, centerY);
		sweepGradient.setLocalMatrix(rotateMatrix);
		progressPaint.setShader(sweepGradient);

		canvas.drawCircle(centerX, centerY, 200, circlePaint);

		// // 整个弧
		// canvas.drawArc(bgRect, startAngle, totalAngle, false, bgPaint);
		// // 当前进度
		// canvas.drawArc(bgRect, startAngle, currentAngle, false,
		// progressPaint);
		// canvas.drawText("综合得分", centerX, centerY + contentSize / 3,
		// contentPaint);
		// canvas.drawText("综合得分", centerX, centerY + contentSize + 30,
		// titlePaint);

		// Paint circlePaint = new Paint();
		// circlePaint.setColor(Color.parseColor("#ff0000"));
		// canvas.drawCircle(centerX, centerY, 360, circlePaint);

		invalidate();
	}

	/**
	 * 设置最大值
	 * 
	 * @param maxValues
	 */
	public void setMaxValue(float maxValues) {
		this.maxValues = maxValues;
		k = totalAngle / maxValues;
	}

	/**
	 * 设置当前值
	 * 
	 * @param curValue
	 */
	public void setCurValue(String curValue) {
		contentString = curValue;
	}

	public void setRate(float rate) {
		float currentValues = rate * 100;
		if (currentValues > maxValues) {
			currentValues = maxValues;
		}

		if (currentValues < 5) {
			if (currentValues < 0) {
				currentValues = 0;
			}

			// Less than 5%
			int color = Color.parseColor("#ed420a");
			progressColors = new int[] { color, color, color };
			sweepGradient = new SweepGradient(centerX, centerY, progressColors,
					null);
			contentColor = color;
			contentPaint.setColor(contentColor);
		} else if (currentValues < 20) {
			// Less than 20%
			int color = Color.parseColor("#ecb10a");
			progressColors = new int[] { color, color, color };
			sweepGradient = new SweepGradient(centerX, centerY, progressColors,
					null);
			contentColor = color;
			contentPaint.setColor(contentColor);
		} else {
			progressColors = tempProgressColors;
			sweepGradient = new SweepGradient(centerX, centerY, progressColors,
					null);
			contentColor = tempContentColor;
			contentPaint.setColor(contentColor);
		}

		this.curValues = currentValues;
		currentAngle = curValues * k;
	}

	/**
	 * dip 转换成px
	 * 
	 * @param dip
	 * @return
	 */
	private int dipToPx(float dip) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
	}
}
