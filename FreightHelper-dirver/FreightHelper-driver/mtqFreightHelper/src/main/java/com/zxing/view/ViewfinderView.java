/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.view;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.yunbaba.freighthelper.R;
import com.zxing.camera.CameraManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 */
public final class ViewfinderView extends View {
	// private static final String TAG = "log";

	/**
	 * 绘制边框的颜色
	 */
	private static final String RGB_WHITE = "#FFFFFF";
	private static final String RGB_GREEN = "#01fd47";
	/**
	 * 刷新界面的时间
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	/**
	 * 四个顶角对应的半径
	 */
	private int CircleRate;

	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 5;

	/**
	 * 字体大小
	 */
	private static final int TEXT_SIZE = 14;
	/**
	 * 网络状况
	 */
	private boolean netState = true;

	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;

	/**
	 * 扫描线内容区
	 */
	private Rect lineRect;
	/**
	 * 底部提示文本
	 */
	private String bottomTips;
	/**
	 * 底部提示文本与扫描框的距离
	 */
	private int bottomTipsPadding;
	/**
	 * 网络异常提示文本
	 */
	private String netErrorTips;

	/**
	 * 将扫描的二维码拍下来，这里没有这个功能，暂时不考虑
	 */
	private Bitmap resultBitmap;
	private int maskColor;
	private int resultColor;

	private int resultPointColor;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	boolean isFirst;

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ViewfinderView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		CircleRate = dp2px(getContext(), 5);
		paint = new Paint();
		lineRect = new Rect();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);

		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}


	@Override
	public void onDraw(Canvas canvas) {
		// 中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}

		// 初始化中间线滑动的最上边和最下边
		if (!isFirst) {
			isFirst = true;
			slideTop = frame.top;
		}

		// 获取屏幕的宽和高
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		// 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
		// 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		paint.setColor(android.graphics.Color.parseColor(RGB_WHITE));
		canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2,
				paint);
		canvas.drawRect(frame.left, frame.top + 2, frame.left + 2,
				frame.bottom - 1, paint);
		canvas.drawRect(frame.right - 1, frame.top, frame.right + 1,
				frame.bottom - 1, paint);
		canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1,
				frame.bottom + 1, paint);
		

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {
			// 画扫描框边上的角，总共8个部分
			paint.setColor(android.graphics.Color.parseColor(RGB_GREEN));

			int linewidth = 10;
			
			float lineLenght = 50;

			// draw rect
			canvas.drawRect(frame.left, frame.top,  (linewidth + frame.left),  (lineLenght + frame.top), paint);  // 左上 |
			canvas.drawRect(frame.left, frame.top,  (lineLenght + frame.left),  (linewidth + frame.top), paint);   // 左上 ——
			canvas.drawRect( ((0 - linewidth) + frame.right), frame.top, frame.right, (lineLenght + frame.top), paint);  //右上 |
			canvas.drawRect( (-lineLenght + frame.right), frame.top,  frame.right,  (linewidth + frame.top), paint);    // 右上 ——
			canvas.drawRect( frame.left, (-lineLenght + frame.bottom),  (linewidth + frame.left),  frame.bottom, paint);  //左下|
			canvas.drawRect(frame.left, (- linewidth + frame.bottom),  (lineLenght + frame.left),  frame.bottom, paint);  //左下 ——
			canvas.drawRect( (-linewidth + frame.right),  (-lineLenght + frame.bottom), frame.right,   frame.bottom, paint); //右下|
			canvas.drawRect( (-lineLenght + frame.right), (- linewidth + frame.bottom),  frame.right, frame.bottom, paint);  //右下 ——

			

			// 绘制扫描线
			Bitmap searchBitmap = ((BitmapDrawable) (getResources()
					.getDrawable(R.drawable.scan_search))).getBitmap();
			int searchHeight = searchBitmap.getHeight();
			if (netState) {
				slideTop += SPEEN_DISTANCE;
				if (slideTop + searchHeight >= frame.bottom) {
					slideTop = frame.top;
				}
			} else {
				slideTop = (frame.top + frame.bottom - searchHeight) / 2;
//				// 画扫描框下面的字
//				paint.setColor(android.graphics.Color.parseColor("#ffffff"));
//				paint.setTextSize(sp2px(getContext(), 18));
//				String[] lines = netErrorTips.split("\n");
//				int textWidth = (int) (getTextMaxWidth(paint, lines));
//				int x = (frame.left + frame.right - textWidth) / 2;
//
//				int lineHeight = 0;
//				int yoffset = 0;
//				// 设置1.2倍行距
//				paint.getTextBounds("Ig", 0, 2, lineRect);
//				lineHeight = (int) ((float) lineRect.height() * 1.2);
//				// draw each line
//				for (int i = lines.length - 1; i >= 0; i--) {
//					canvas.drawText(lines[i], x, slideTop + yoffset, paint);
//					yoffset = yoffset - lineHeight;
//				}
//
			}
			lineRect.left = frame.left;
			lineRect.right = frame.right;
			lineRect.top = slideTop;
			lineRect.bottom = slideTop + searchHeight;
			canvas.drawBitmap(searchBitmap, null, lineRect, paint);

			// 只刷新扫描框的内容，其他地方不刷新
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);

		}
	}

	/**
	 * 计算宽度最大的字符串
	 * 
	 * @param paint
	 * @param str
	 * @return 返回最长字符串的宽度
	 * @return int
	 * @author hejie
	 * @date 2015年7月2日 下午8:37:45
	 */
	public int getTextMaxWidth(Paint paint, String[] str) {
		int max = 0;
		int maxIndex = 0;
		for (int i = 0; i < str.length; i++) {
			if (max < str[i].length()) {
				max = str[i].length();
				maxIndex = i;
			}
		}
		return (int) paint.measureText(str[maxIndex]);
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 * @return int
	 * @author hejie
	 * @date 2015年7月2日 下午8:05:49
	 */
	public static int dp2px(Context context, float dp) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				r.getDisplayMetrics());
		return Math.round(px);
	}

	/**
	 * sp转px
	 * 
	 * @param context
	 * @param sp
	 * @return
	 * @return int
	 * @author hejie
	 * @date 2015年7月2日 下午8:05:44
	 */
	public static int sp2px(Context context, float sp) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				r.getDisplayMetrics());
		return Math.round(px);
	}

	public boolean isNetState() {
		return netState;
	}

	public void setNetState(boolean netState) {
		this.netState = netState;
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
