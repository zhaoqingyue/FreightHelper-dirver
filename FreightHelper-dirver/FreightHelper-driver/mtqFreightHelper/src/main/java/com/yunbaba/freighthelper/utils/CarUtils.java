package com.yunbaba.freighthelper.utils;

import com.yunbaba.api.trunk.listner.OnBooleanListner;
import com.yunbaba.freighthelper.bean.car.CarInfo;
import com.yunbaba.freighthelper.bean.car.Navi;
import com.yunbaba.freighthelper.bean.car.TravelDetail;
import com.yunbaba.freighthelper.bean.car.TravelTask;
import com.yunbaba.freighthelper.db.TravelTaskTable;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCar;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqCarRoute;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqNavi;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTask;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.MtqTaskDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CarUtils {

	public static CarInfo parseCarInfo(String taskId, String corpId,
			MtqCar result) {
		CarInfo carinfo = new CarInfo();
		carinfo.taskId = taskId;
		carinfo.corpId = corpId;
		carinfo.carlicense = result.carlicense;
		carinfo.carduid = result.carduid;
		carinfo.carmodel = result.carmodel;
		carinfo.brand = result.brand;
		carinfo.vehicletype = result.vehicletype;
		carinfo.devicename = result.devicename;
		carinfo.mcuid = result.mcuid;
		carinfo.sim_endtime = result.sim_endtime;
		carinfo.navi = result.navi;
		carinfo.carstate = result.carstate;
		carinfo.devicetype = result.devicetype;
		return carinfo;
	}

	public static List<Navi> formatNaviRoutes(List<MtqCarRoute> listOfResult) {
		List<Navi> routeList = new ArrayList<Navi>();
		if (listOfResult != null && !listOfResult.isEmpty()) {
			for (int i = 0; i < listOfResult.size(); i++) {
				MtqCarRoute mtqCarRoute = listOfResult.get(i);
				List<MtqNavi> naviList = mtqCarRoute.navis;
				//显示车牌
				boolean showCarlicense = true;
				if (naviList != null && !naviList.isEmpty()) {
					for (int j = 0; j < naviList.size(); j++) {
						MtqNavi mtqNavi = naviList.get(j);
						
						if (mtqNavi.traveltime.equals("0")){
							continue;
						}
						
						Navi route = new Navi();
//						route.taskId = (mtqCarRoute.taskid);
//						route.corpId = (mtqCarRoute.corpid);
						route.carlicense = (mtqCarRoute.carlicense);
						route.carduid = (mtqNavi.carduid);
						
						if (showCarlicense) {
							if (routeList.size() == 0){
								route.position = (0);
							}else{
								if (!routeList.get(routeList.size() - 1).carlicense.equals(mtqCarRoute.carlicense)){
									//和上一个单不是同辆车
									route.position = (0);
								}
							}
							showCarlicense = false;
						}
						
						route.serialid = (mtqNavi.serialid);
						route.starttime = (mtqNavi.starttime);
						route.endtime = (mtqNavi.endtime);
						route.mileage = (mtqNavi.mileage);
						route.traveltime = (mtqNavi.traveltime);
						route.orders = (mtqNavi.orders);
						
						if (!route.traveltime.equals("0")){
							routeList.add(route);
						}
					}
				}
			}
		}
		return routeList;
	}

	public static TravelDetail formatTaskDetail(MtqTaskDetail result,
			String carlicense, String serialid) {
		TravelDetail detail = new TravelDetail();
		detail.carlicense = carlicense;
		detail.serialid = serialid;
		detail.tracks = result.tracks;
		if (result.navi != null) {
			detail.navi.carduid = result.navi.carduid;
			detail.navi.starttime = result.navi.starttime;
			detail.navi.endtime = result.navi.endtime;
			detail.navi.fuelcon = result.navi.fuelcon + "L";
			detail.navi.idlefuelcon = result.navi.idlefuelcon + "L";

			detail.navi.topspeed = result.navi.topspeed + "km/h";
			detail.navi.warmedtime = result.navi.warmedtime;
			detail.navi.comfortscore = result.navi.comfortscore;
			float fuelcon = Float.parseFloat(result.navi.fuelcon);
			float mileage = Float.parseFloat(result.navi.mileage)*1000;
			float traveltime = Float.parseFloat(result.navi.traveltime);
			float idletime = Float.parseFloat(result.navi.idletime);

			detail.navi.mileage = FormatUtils.meterDisToString((int) (mileage),
					true);
			DecimalFormat df = new DecimalFormat("0.0");
			detail.navi.traveltime = df.format(traveltime / 60) + "min";
			detail.navi.idletime = df.format(idletime / 60) + "min";
			float hundred_fuel = 0.0f;
			if (Math.abs(fuelcon) < 0.00001) {
				hundred_fuel = 0.0f;
			} else {
				hundred_fuel = (mileage / fuelcon) * 100;
			}

			float average_speed = 0.0f;

			if (traveltime > 0.00001) {
				average_speed = mileage / (traveltime / 3600) / 1000;
			}

			detail.hundred_fuel = df.format(hundred_fuel) + "L";
			detail.average_speed = df.format(average_speed) + "km/h";
		}
		return detail;
	}

	public static void parseTask(List<MtqTask> listOfResult, OnBooleanListner lis) {
		// MLog.e("CarUtils", "parseTask len: " + listOfResult.size());
		List<TravelTask> taskList = new ArrayList<TravelTask>();
		int len = listOfResult.size();
		for (int i = 0; i < len; i++) {
			MtqTask mtqTask = listOfResult.get(i);
			
//			if (mtqTask.finishtime.equals("0") || mtqTask.starttime.equals("0") ){
//				continue;
//			}
			
			if (mtqTask.starttime.equals("0")){
				continue;
			}
			
			TravelTask task = new TravelTask();
			/**
			 * 同一天
			 */
			task.taskid = mtqTask.taskid;
			task.corpid = mtqTask.corpid;
			task.starttime = mtqTask.starttime;
			task.finishtime = mtqTask.finishtime;
			task.date = TimeUtils.stampToDay(mtqTask.starttime);
			taskList.add(task);
			
			if (mtqTask.finishtime.equals("0")){
				//单还没有完成。 将单的时候设置为现在的完成时间。
				mtqTask.finishtime = TimeUtils.getEndtime();
			}
			
			
			if (!TimeUtils.isSameDayOfMillis(mtqTask.starttime,
					mtqTask.finishtime)) {
				/**
				 * 跨天, 第二天
				 */
				TravelTask nextTask = new TravelTask();
				nextTask.taskid = mtqTask.taskid;
				nextTask.corpid = mtqTask.corpid;
				nextTask.starttime = mtqTask.starttime;
				nextTask.finishtime = mtqTask.finishtime;
				nextTask.date = TimeUtils.stampToDay(mtqTask.finishtime);
				
				int betweenday = TimeUtils.daysBetweenByDate(task.date, nextTask.date);
				taskList.add(nextTask);
				
				int addday = 0;
				while(betweenday > 1 && betweenday < 30){
					//间隔了不止1天,目前只能显示1个月的行程而已。
					addday++;
					TravelTask travelTask = new TravelTask();
					travelTask.taskid = mtqTask.taskid;
					travelTask.corpid = mtqTask.corpid;
					travelTask.starttime = mtqTask.starttime;
					travelTask.finishtime = mtqTask.finishtime;
					
					travelTask.date = TimeUtils.changeDay(addday, task.date);
					taskList.add(travelTask);
					betweenday--;
				}
			}
		}

		if (taskList.size() > 0) {
			TravelTaskTable.getInstance().insert(taskList,lis);
		}else{
			lis.onResult(true);

		}
	}
}
