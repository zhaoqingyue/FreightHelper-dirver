package com.yunbaba.freighthelper.ui.customview;

import java.util.ArrayList;

import com.yunbaba.freighthelper.R;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCarCheckHistory;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SpinerPopWindow extends PopupWindow {  
    private LayoutInflater inflater;  
    private ListView mListView;  
    private ArrayList<MtqCarCheckHistory> list;  
    private MyAdapter  mAdapter;  
      
    public SpinerPopWindow(Context context,ArrayList<MtqCarCheckHistory> list,OnItemClickListener clickListener) {  
        super(context);  
        inflater=LayoutInflater.from(context);  
        this.list=list;  
        init(clickListener);  
    }  
    
    
    public void setData(ArrayList<MtqCarCheckHistory> list){
    	  this.list=list;  
    	  if(mAdapter!=null)
    		  mAdapter.notifyDataSetChanged();
    	
    }
      
    private void init(OnItemClickListener clickListener){  
        View view = inflater.inflate(R.layout.spiner_window_layout, null);  
        setContentView(view);         
        setWidth(LayoutParams.WRAP_CONTENT);  
        setHeight(LayoutParams.WRAP_CONTENT);  
        setFocusable(true);  
            ColorDrawable dw = new ColorDrawable(0x00);  
        setBackgroundDrawable(dw);  
        mListView = (ListView) view.findViewById(R.id.listview);  
        mListView.setAdapter(mAdapter=new MyAdapter());  
        mListView.setOnItemClickListener(clickListener);  
    }  
      
    private class MyAdapter extends BaseAdapter{  
        @Override  
        public int getCount() {  
            return list.size();  
        }  
  
        @Override  
        public MtqCarCheckHistory getItem(int position) {  
            return list.get(position);  
        }  
  
        @Override  
        public long getItemId(int position) {  
            return position;  
        }  
  
        @Override  
        public View getView(int position, View convertView, ViewGroup parent) {  
            ViewHolder holder=null;  
            if(convertView==null){  
                holder=new ViewHolder();  
                convertView=inflater.inflate(R.layout.spiner_item_layout, null);  
                holder.tvName=(TextView) convertView.findViewById(R.id.tv_name);  
                convertView.setTag(holder);  
            }else{  
                holder=(ViewHolder) convertView.getTag();  
            }  
            holder.tvName.setText(getItem(position).carlicense);  
            return convertView;  
        }  
    }  
      
    private class ViewHolder{  
        private TextView tvName;  
    }  
}  