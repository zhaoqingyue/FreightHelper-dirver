/**
 * 
 * Copyright © 2017mtq. All rights reserved.
 *
 * @Title: CalendarCard.java
 * @Prject: MTQFreightHelper
 * @Package: com.mtq.freighthelper.ui.customview
 * @Description: 自定义日历视图
 * @author: zhaoqy
 * @date: 2017年4月17日 下午7:27:06
 * @version: V1.0
 */

package com.yunbaba.freighthelper.ui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.yunbaba.freighthelper.bean.CustomDate;
import com.yunbaba.freighthelper.bean.CustomDate.DayState;
import com.yunbaba.freighthelper.bean.CustomDate.MonthState;
import com.yunbaba.freighthelper.utils.DateUtils;

public class CalendarCard extends View {
	private static final int TOTAL_COL = 7; // 7列
	private static final int TOTAL_ROW = 6; // 6行

	private Paint mCirclePaint; // 绘制圆形的画笔
	private Paint mTextPaint; // 绘制文本的画笔
	private int mViewWidth; // 视图的宽度
	private int mViewHeight; // 视图的高度
	private int mCellSpace; // 单元格间距
	private int touchSlop;
	private float mDownX;
	private float mDownY;
	private Row rows[] = new Row[TOTAL_ROW]; // 行数组，每个元素代表一行
	private OnCellClickListener mListener; // 单元格点击回调事件
	private static CustomDate mCurDate; // 自定义的日期，包括year, month, day

	public interface OnCellClickListener {

		/**
		 * @Title: clickDate
		 * @Description: 回调点击的日期
		 * @param date
		 * @return: void
		 */
		void clickDate(CustomDate date);

		/**
		 * @Title: changeDate
		 * @Description: 回调滑动ViewPager改变的日期
		 * @param date
		 * @return: void
		 */
		void changeDate(CustomDate date);
	}

	public CalendarCard(Context context, OnCellClickListener listener) {
		super(context);
		this.mListener = listener;
		init(context);
	}

	private void init(Context context) {
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setStyle(Paint.Style.FILL);
		mCirclePaint.setColor(Color.parseColor("#ef2c29")); // 圆形
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		initDate();
	}

	private void initDate() {
		mCurDate = new CustomDate();
		fillDate();
	}

	private void fillDate() {
		int monthDay = DateUtils.getCurMonthDay();
		int lastMonthDays = DateUtils.getMonthDays(mCurDate.year,
				mCurDate.month - 1);
		int curMonthDays = DateUtils
				.getMonthDays(mCurDate.year, mCurDate.month);
		int firstDayWeek = DateUtils.getWeekDayFromDate(mCurDate.year,
				mCurDate.month);
		MonthState monthState = DateUtils.getMonthState(mCurDate);

		boolean isCurMonth = false;
		if (DateUtils.isCurMonth(mCurDate)) {
			isCurMonth = true;
		}

		int day = 0;
		for (int j = 0; j < TOTAL_ROW; j++) {
			rows[j] = new Row(j);
			for (int i = 0; i < TOTAL_COL; i++) {
				int position = i + j * TOTAL_COL; // 单元格位置
				if (position >= firstDayWeek
						&& position < firstDayWeek + curMonthDays) {
					// 当前月
					day++;
					CustomDate customDay = new CustomDate();
					customDay.setYear(mCurDate.year);
					customDay.setMonth(mCurDate.month);
					customDay.setDay(day);
					customDay.setIsTravel(DateUtils.isTravelDay(customDay));
					customDay.setMonthState(monthState);
					customDay.setDayState(DayState.CUR_MONTH_DAY);

					if (isCurMonth) {
						/*
						 * if (day == monthDay) {
						 * customDay.setDayState(DayState.TODAY); } else
						 */if (day > monthDay) {
							customDay.setDayState(DayState.UNREACH_DAY);
						}

					}
					if (isClickDate(customDay)) {
						customDay.setDayState(DayState.CLICK);
					}

					rows[j].cells[i] = new Cell(customDay, i, j);
				} else if (position < firstDayWeek) {
					// 上个月
					int index = lastMonthDays - (firstDayWeek - position - 1);
					CustomDate customDay = new CustomDate();
					customDay.setYear(mCurDate.year);
					customDay.setMonth(mCurDate.month - 1);
					customDay.setDay(index);
					customDay.setMonthState(monthState);
					customDay.setDayState(DayState.PAST_MONTH_DAY);
					
					rows[j].cells[i] = new Cell(customDay, i, j);
				} else if (position >= firstDayWeek + curMonthDays) {
					// 下个月
					int index = position - firstDayWeek - curMonthDays + 1;
					CustomDate customDay = new CustomDate();
					customDay.setYear(mCurDate.year);
					customDay.setMonth(mCurDate.month + 1);
					customDay.setDay(index);
					customDay.setMonthState(monthState);
					customDay.setDayState(DayState.NEXT_MONTH_DAY);

					rows[j].cells[i] = new Cell(customDay, i, j);
				}
			}
		}
		mListener.changeDate(mCurDate);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < TOTAL_ROW; i++) {
			if (rows[i] != null) {
				rows[i].drawCells(canvas);
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mViewWidth = w;
		mViewHeight = h;
		int aveHeight = mViewHeight / TOTAL_ROW;
		int aveWidth = mViewWidth / TOTAL_COL;
		mCellSpace = Math.min(aveHeight, aveWidth);
		mTextPaint.setTextSize((float) (mCellSpace / 2.5));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = event.getX();
			mDownY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			float disX = event.getX() - mDownX;
			float disY = event.getY() - mDownY;
			if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
				int col = (int) (mDownX / mCellSpace);
				int row = (int) (mDownY / mCellSpace);
				measureClickCell(col, row);
			}
			break;
		default:
			break;
		}

		return true;
	}

	/**
	 * 计算点击的单元格
	 * 
	 * @param col
	 * @param row
	 */
	private void measureClickCell(int col, int row) {
		if (col >= TOTAL_COL || row >= TOTAL_ROW || rows[row] == null)
			return;

		CustomDate temp = rows[row].cells[col].date;
		temp.week = col;
		if (DateUtils.isTravelDay(temp)) {
			mClickDate = temp;
			mClickDate.setDayState(DayState.CLICK);
			update();
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					/**
					 * 要执行的操作
					 */
					mListener.clickDate(mClickDate);
				}
			}, 300);
		} 
	}

	/**
	 * 组元素
	 */
	class Row {
		public int j;

		Row(int j) {
			this.j = j;
		}

		public Cell[] cells = new Cell[TOTAL_COL];

		// 绘制单元格
		public void drawCells(Canvas canvas) {
			for (int i = 0; i < cells.length; i++) {
				if (cells[i] != null) {
					cells[i].drawSelf(canvas);
				}
			}
		}
	}

	/**
	 * 单元格元素
	 */
	class Cell {
		public CustomDate date;
		public int i;
		public int j;

		public Cell(CustomDate date, int i, int j) {
			super();
			this.date = date;
			this.i = i;
			this.j = j;
		}

		public void drawSelf(Canvas canvas) {

			switch (date.monthState) {
			case PRE_MONTH:
			case CUR_MONTH: {
				switch (date.dayState) {
				case TODAY:
				case CLICK:
					mTextPaint.setColor(Color.WHITE);
					float cx = (float) ((i + 0.5) * mCellSpace);
					float cy = (float) ((j + 0.45) * mCellSpace);
					float radius = (float) (mCellSpace / 2.5);
					canvas.drawCircle(cx, cy, radius, mCirclePaint);
					break;
				case CUR_MONTH_DAY:
					if (date.isTravel) {
						mTextPaint.setColor(Color.BLACK);
					} else {
						mTextPaint.setColor(Color.GRAY);
					}
					break;
				case PAST_MONTH_DAY:
				case NEXT_MONTH_DAY:
					mTextPaint.setColor(Color.WHITE);
					break;
				case UNREACH_DAY:
					mTextPaint.setColor(Color.GRAY);
					break;
				default:
					break;
				}
				break;
			}
			case NEXT_MONTH: {
				switch (date.dayState) {
				case PAST_MONTH_DAY:
				case NEXT_MONTH_DAY:
					mTextPaint.setColor(Color.WHITE);
					break;
				default:
					mTextPaint.setColor(Color.GRAY);
					break;
				}
				break;
			}
			default:
				break;
			}

			String content = date.day + "";
			float x = (float) ((i + 0.5) * mCellSpace - mTextPaint
					.measureText(content) / 2);
			float y = (float) ((j + 0.7) * mCellSpace - mTextPaint.measureText(
					content, 0, 1) / 2);
			canvas.drawText(content, x, y, mTextPaint);
		}
	}

	private CustomDate mClickDate;

	private boolean isClickDate(CustomDate date) {
		if (date != null && mClickDate != null) {
			if (date.year == mClickDate.year && date.month == mClickDate.month
					&& date.day == mClickDate.day) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Title: upSlide
	 * @Description: 从上往下划，上一个月
	 * @return: void
	 */
	public void upSlide() {
		if (mCurDate.month == 1) {
			mCurDate.month = 12;
			mCurDate.year -= 1;
		} else {
			mCurDate.month -= 1;
		}
		update();
	}

	/**
	 * @Title: downSlide
	 * @Description: 从下往上划，下一个月
	 * @return: void
	 */
	public void downSlide() {
		if (mCurDate.month == 12) {
			mCurDate.month = 1;
			mCurDate.year += 1;
		} else {
			mCurDate.month += 1;
		}
		update();
	}

	public void update() {
		fillDate();
		invalidate();
	}
}
