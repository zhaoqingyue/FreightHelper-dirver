/*
 * @Title InputView.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @Description 
 * @author Zhouls
 * @date 2015-1-6 9:03:59
 * @version 1.0
 */
package com.yunbaba.apitest.ui;

import com.yunbaba.ols.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * The Class InputView.
 * 
 * @Description 输入参数视图
 * @author Zhouls
 * @date 2014-11-14 上午10:53:57
 */
public class InputView extends View {

	/** The view. */
	public View view;

	/** The context. */
	private Context context;

	/** The edt5. */
	public EditText edt1, edt2, edt3, edt4, edt5;

	/** The btn2. */
	public Button btn1, btn2;

	/**
	 * Instantiates a new input view.
	 * 
	 * @param context
	 *            the context
	 */
	public InputView(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new input view.
	 * 
	 * @param context
	 *            the context
	 * @param afterListener
	 *            the after listener
	 */
	public InputView(Context context, AfterListener afterListener) {
		super(context);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.input, null);
		initControls();
		setListener(afterListener);
	}

	/**
	 * Inits the controls.
	 */
	public void initControls() {
		edt1 = (EditText) view.findViewById(R.id.editText1);
		edt2 = (EditText) view.findViewById(R.id.editText2);
		edt3 = (EditText) view.findViewById(R.id.editText3);
		edt4 = (EditText) view.findViewById(R.id.editText4);
		edt5 = (EditText) view.findViewById(R.id.editText5);
		btn1 = (Button) view.findViewById(R.id.submit);
		btn2 = (Button) view.findViewById(R.id.reset);
	}

	/**
	 * Uninit.
	 */
	public void uninit() {
		edt1.setText("");
		edt2.setText("");
		edt3.setText("");
		edt4.setText("");
		edt5.setText("");
	}

	/**
	 * The listener interface for receiving after events. The class that is
	 * interested in processing a after event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's <code>addAfterListener<code> method. When
	 * the after event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see AfterEvent
	 */
	public interface AfterListener {

		/**
		 * To do.
		 */
		public void toDo();

		public void stopDo();
	}

	/**
	 * Sets the listener.
	 * 
	 * @param afterListener
	 *            the new listener
	 */
	public void setListener(final AfterListener afterListener) {
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(context, "提交", Toast.LENGTH_SHORT).show();
				DalInput.getInstance().put(edt1.getText().toString(),
						edt2.getText().toString(), edt3.getText().toString(),
						edt4.getText().toString(), edt5.getText().toString());
				afterListener.toDo();
			}
		});
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uninit();
				afterListener.stopDo();
				DalInput.getInstance().put(edt1.getText().toString(),
						edt2.getText().toString(), edt3.getText().toString(),
						edt4.getText().toString(), edt5.getText().toString());
			}
		});
	}
}
