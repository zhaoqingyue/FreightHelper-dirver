package com.yunbaba.freighthelper.ui.customview;

import com.yunbaba.freighthelper.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ScanToast {
	
	public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast result = new Toast(context);

        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View v = inflate.inflate(R.layout.layout_scan_toast, null);
        TextView tv = (TextView)v.findViewById(R.id.scantoast_text);
        tv.setText(text);
       
        result.setView(v);
        result.setDuration(duration);

        return result;
    }
	
	
	public static Toast makeText(Context context, int textid, int duration) {
        Toast result = new Toast(context);

        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View v = inflate.inflate(R.layout.layout_scan_toast, null);
        TextView tv = (TextView)v.findViewById(R.id.scantoast_text);
        tv.setText(context.getResources().getString(textid));
       
        result.setView(v);
        result.setDuration(duration);

        return result;
    }
	
}
