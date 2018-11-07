package com.yunbaba.freighthelper.constant;

public class FreightConstant {

	public static final  int TASK_READY =  0;
	public static final  int TASK_FINISH = 1;
	public static final  int TASK_RESUME = 2;
	public static final  int TASK_PAUSE = 3;
	public static final  int TASK_START = 4;
	
	
	
	public static final int TASK_POINT_REQUSEST_CODE = 23;
	//运货点需要更新
	public static final int TASK_POINT_INFO_NEED_UPDATE = 666;
	//运货单和运货点都需要更新   这个ID暂时没有用，从导航界面传递到主界面太麻烦了。 直接用Eventbus了
	public static final int TASK_AND_POINT_NEED_UPDATE = 667;
	
	//是否节省流量
	public static boolean isSaveFlow = true;

	//是否显示地图
	public static boolean isShowMap = true;

}
