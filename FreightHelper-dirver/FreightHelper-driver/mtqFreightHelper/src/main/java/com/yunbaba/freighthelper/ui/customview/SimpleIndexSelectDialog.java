package com.yunbaba.freighthelper.ui.customview;

import com.yunbaba.freighthelper.R;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class SimpleIndexSelectDialog {
	
	
	public static void ShowSelectDialog(Context context,final String[] STR,boolean outsidecancel,final OnSimpleIndexSelectCallBack callback) {
		
	
		
		
		AlertDialog a = new AlertDialog.Builder(
				new ContextThemeWrapper(context, R.style.AlertDialogCustom))
						.setItems(STR, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							
								
									callback.OnIndexSelect(which,STR[which]);
								
							}
						}).create();
		a.setCanceledOnTouchOutside(outsidecancel);
		
		
		
		//
		//setDialogFontSize(a,50);
		//setDialogFontSize(a,50);
		a.show();
		
	
		
		
		
		//setDialogFontSize(a,50);
		
		

	}
	
	private static void setDialogFontSize(AlertDialog dialog,int size)
    {
        Window window = dialog.getWindow();
        View view = window.getDecorView();
        setViewFontSize(view,size);
    }
	
	private static void setViewFontSize(View view,int size)
    {
        if(view instanceof ViewGroup)
        {
            ViewGroup parent = (ViewGroup)view;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++)
            {
                setViewFontSize(parent.getChildAt(i),size);
            }
        }
        else if(view instanceof TextView){
            TextView textview = (TextView)view;
            textview.setTextSize(size,TypedValue.COMPLEX_UNIT_PX);
        }
    }
	
	public static interface OnSimpleIndexSelectCallBack{
		
		

		void OnIndexSelect(int index, String select);
		
		
	}
	

}
