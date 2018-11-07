package com.yunbaba.freighthelper.ui.customview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SideBar extends View {
	/*
	 * public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
	 * "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
	 * "X", "Y", "Z", "#" };
	 */

	private List<String> mLetters = new ArrayList<String>();
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	private int choose = -1;
	private Paint paint = new Paint();
	private TextView mTextDialog;

	public void setTextView(TextView textDialog) {
		mTextDialog = textDialog;
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SideBar(Context context) {
		super(context);
	}

	public void setLetters(List<String> letter) {
		mLetters = letter;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int length = mLetters.size();
		int height = getHeight();
		int width = getWidth();
		// int singleHeight = height / length;
		// int defY = 0;
		int singleHeight = height / 27;
		int defY = (height - singleHeight * length) / 2;

		for (int i = 0; i < length; i++) {
			String letter = mLetters.get(i);
			paint.setColor(Color.parseColor("#000000"));
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(30);
			if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - paint.measureText(letter) / 2;
			float yPos = defY + singleHeight * i + singleHeight;
			canvas.drawText(letter, xPos, yPos, paint);
			paint.reset();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}
			return true;
		} else {
			int length = mLetters.size();
			float y = event.getY();
			int oldChoose = choose;
			OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;

			int height = getHeight();
			int singleHeight = height / 27;
			int defY = (height - singleHeight * length) / 2;
			int c = (int) ((y - defY) / singleHeight);
			if (c < 0 || c >= length)
				return true;

			String letter = mLetters.get(c);
			if (oldChoose != c) {
				if (c >= 0 && c < length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(letter);
					}
					if (mTextDialog != null) {
						mTextDialog.setText(letter);
						mTextDialog.setVisibility(View.VISIBLE);
					}
					choose = c;
					invalidate();
				}
			}
			return true;
		}
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}
}
