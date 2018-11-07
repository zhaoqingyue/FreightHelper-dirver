package com.yunbaba.freighthelper.ui.customview;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yunbaba.freighthelper.utils.MLog;
import com.zhy.android.percent.support.PercentLinearLayout;

public class DragPercentLinearLayout extends PercentLinearLayout {

	private ViewDragHelper mDragger;

	private ViewDragHelper.Callback callback;

	private View mSupportDragView;

	private boolean mOnlyVerticalDrag = true;

	private int mTopLimit;
	private int mBottomLimit;

	private final int OFFSET = 50;

	private boolean firstCome = true;

	public DragPercentLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
			@Override
			public boolean tryCaptureView(View child, int pointerId) {
				if (mSupportDragView == null) {
					return true;
				}

				if (mSupportDragView.equals(child)) {
					return true;
				}

				return false;
			}

			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				if (mOnlyVerticalDrag) {
					// 只支持上下拖
					return 0;
				}

				return left;
			}

			@Override
			public int clampViewPositionVertical(View child, int top, int dy) {
				if (top < mTopLimit) {
					return mTopLimit;
				}

				if (top > mBottomLimit) {
					return mBottomLimit;
				}

				MLog.e("clampViewPositionVertical", top + " " + dy);

				return top;
			}

			@Override
			public void onEdgeDragStarted(int edgeFlags, int pointerId) {
				mDragger.captureChildView(mSupportDragView, pointerId);
			}

			@Override
			public void onViewReleased(View releasedChild, float xvel, float yvel) {
				//

				float movePrecent = (releasedChild.getHeight() + releasedChild.getTop())
						/ (float) releasedChild.getHeight();
				MLog.e("onreleasecheck", xvel + " " + yvel + " " + movePrecent);

				if (mSupportDragView == null) {
					return;
				}

				if (releasedChild.equals(mSupportDragView)) {

					if (yvel < 0) {
						mDragger.settleCapturedViewAt(0, 0);
					} else if (yvel > 0) {
						mDragger.settleCapturedViewAt(0, mBottomLimit);
					} else {

						int top = mSupportDragView.getTop();

						if (top >= getHeight() / 2f) {
							mDragger.settleCapturedViewAt(0, mBottomLimit);
						} else {
							mDragger.settleCapturedViewAt(0, 0);
						}

					}

					// if(top < mBottomLimit){
					// mDragger.settleCapturedViewAt(0,mBottomLimit);
					//
					// }else{
					// //mDragger.settleCapturedViewAt(0,mBottomLimit);
					//
					// mDragger.settleCapturedViewAt(0,0);
					// }
					//

					// if (top <= mBottomLimit/4){
					// mDragger.settleCapturedViewAt(0,0);
					// }else if (top >= mBottomLimit*0.75){
					// mDragger.settleCapturedViewAt(0,mBottomLimit);
					// }else{
					// mDragger.settleCapturedViewAt(0,mBottomLimit>>1);
					// }

					invalidate();
				} else {

					MLog.e("onreleasecheck", "not dragview");
				}
			}

		});

		mDragger.setEdgeTrackingEnabled(ViewDragHelper.DIRECTION_ALL);

	}

	@Override
	public void computeScroll() {
		if (mDragger.continueSettling(true)) {
			invalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		// MLog.e("currentaction", action+"");
		if (mSupportDragView != null && (event.getRawY() < mSupportDragView.getTop() + getTop() + OFFSET)) {
			//MLog.e("onInterceptTouchEvent", "false");
			return false;
		}

//		final int action = MotionEventCompat.getActionMasked(event);
//		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
//
//			mDragger.cancel();
//			return false;
//		}

		//MLog.e("onup", action + "dragview");

		// if (!(mSupportDragView != null && (event.getRawY() <
		// mSupportDragView.getTop() + getTop() + OFFSET))) {
		// return false;
		// }
	//	MLog.e("onInterceptTouchEvent", "true");

		return mDragger.shouldInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mSupportDragView != null && (event.getRawY() < mSupportDragView.getTop() + getTop() + OFFSET)) {
			/***
			 * 这个要加，否则从路径部分滑到地图部分Drag会无法完整处理触摸事件
			 * */
			mDragger.processTouchEvent(event);
			return false;
		}

		// final int action = MotionEventCompat.getActionMasked(event);
		// MLog.e("currentaction", action+"");

		mDragger.processTouchEvent(event);
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		if (firstCome) {
			super.onLayout(changed, l, t, r, b);
		}
		firstCome = false;
	}

	@Override
	protected void onFinishInflate() {

		super.onFinishInflate();

		// mSupportDragView = getChildAt(0);
	}

	public View getmSupportDragView() {
		return mSupportDragView;
	}

	public void setmSupportDragView(View mSupportDragView) {
		this.mSupportDragView = mSupportDragView;
	}

	public boolean ismOnlyVerticalDrag() {
		return mOnlyVerticalDrag;
	}

	public void setmOnlyVerticalDrag(boolean mOnlyVerticalDrag) {
		this.mOnlyVerticalDrag = mOnlyVerticalDrag;
	}

	public void setmTopLimit(int mTopLimit) {
		this.mTopLimit = mTopLimit;
	}

	public void setmBottomLimit(int mBottomLimit) {
		this.mBottomLimit = mBottomLimit;
	}

	public void setFirstCome(boolean firstCome) {
		this.firstCome = firstCome;
	}

	public void onLayout(int l, int t, int r, int b) {
		mSupportDragView.layout(l, t, r, b);
	}

}
