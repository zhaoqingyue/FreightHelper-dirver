package com.yunbaba.freighthelper.ui.fragment.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cld.mapapi.map.CldMap;
import com.cld.mapapi.map.MapView;
import com.cld.mapapi.map.MarkerOptions;
import com.cld.mapapi.map.OverlayOptions;
import com.cld.mapapi.model.LatLng;
import com.cld.navisdk.routeplan.CldRoutePlaner;
import com.cld.nv.map.overlay.Overlay;
import com.yunbaba.api.map.MapViewAPI;
import com.yunbaba.api.trunk.FreightPointDeal;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.base.BaseMainFragment;
import com.yunbaba.freighthelper.bean.PointInfo;
import com.yunbaba.freighthelper.bean.eventbus.FreightPointEvent;
import com.yunbaba.freighthelper.bean.eventbus.RefreshAdapterEvent;
import com.yunbaba.freighthelper.constant.FreightConstant;
import com.yunbaba.freighthelper.ui.adapter.FreightPointMapBottomAdapter;
import com.yunbaba.freighthelper.utils.MessageId;
import com.viewpagerindicator.CirclePageIndicator;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.yunbaba.api.trunk.FreightPointDeal.getInstace;

public class FreightPointMapFragment extends BaseMainFragment implements OnPageChangeListener {

    private View mView;
    private View v1;
    private View v2;
    private View v3;
    private View v4;
    private View v5;
    private MapView mMapView = null;
    private ViewPager mViewPager;
    private RelativeLayout mBonttomLayout;
    private PercentLinearLayout pll_indicator;
    private int mSelectedIndex = -1;
    FrameLayout viewgroup;
    private int mType = 0;
    FreightPointMapBottomAdapter adapeter;
    private boolean mIsInMap = false;
    //   LinearLayoutManager linearLayoutManager;
    List<OverlayOptions> options;
    List<Overlay> mylistOverlay;
    CirclePageIndicator mIndicator;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fragment_freightpoint_map, container, false);

        initControl();
        initData();

        EventBus.getDefault().register(this);
        return mView;
    }



    private void initData() {

        viewgroup = (FrameLayout) mView.findViewById(R.id.id_freightpoint_mapView);
        mViewPager = (ViewPager) mView.findViewById(R.id.id_freightpoint_viewpager);
        mBonttomLayout = (RelativeLayout) mView.findViewById(R.id.freightpoint_bottom);
        pll_indicator = (PercentLinearLayout) mView.findViewById(R.id.pll_indicator);

        v1 = (View) mView.findViewById(R.id.v_1);
        v2 = (View) mView.findViewById(R.id.v_2);
        v3 = (View) mView.findViewById(R.id.v_3);
        v4 = (View) mView.findViewById(R.id.v_4);
        v5 = (View) mView.findViewById(R.id.v_5);

        mBonttomLayout.setVisibility(View.INVISIBLE);
        mIndicator = (CirclePageIndicator) mView.findViewById(R.id.idc_indicator);
        if (FreightConstant.isShowMap) {
            mMapView = MapViewAPI.getInstance().createMapView(getActivity());
            mMapView.getMap().setTrafficEnabled(false);
            viewgroup.addView(mMapView);
        }

        //mViewPager.setOnPageChangeListener(this);

        if (getInstace().setAdpate) {
            setAdapter();
        }
    }

    public void setAdapter() {
        //	mBonttomLayout.setVisibility(View.VISIBLE);
        if (adapeter == null)
            adapeter = new FreightPointMapBottomAdapter(this._mActivity);
        int size = FreightPointDeal.getInstace().mPointList.size();


        if (size <= 0)
            mBonttomLayout.setVisibility(View.GONE);
        else {
            mBonttomLayout.setVisibility(View.VISIBLE);

            if (mViewPager.getAdapter() == null) {

                mViewPager.setAdapter(adapeter);
                mViewPager.setOffscreenPageLimit(2);
//        if (linearLayoutManager == null) {
//            linearLayoutManager = new LinearLayoutManager(this._mActivity);
//            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        }
//        mViewPager.setLayoutManager(linearLayoutManager);

                if (FreightPointDeal.getInstace().mPointList != null && FreightPointDeal.getInstace().mPointList.size() <= 5) {
                    mIndicator.setViewPager(mViewPager);
                    final float width = getResources().getDisplayMetrics().widthPixels;

                    final float density = getResources().getDisplayMetrics().density;
                    //  mIndicator.setBackgroundColor(0xFFCCCCCC);
                    // mIndicator.setRadius((int)(0.008*width));
                    mIndicator.setRadius((int) (3 * density));
                    mIndicator.setPageColor(0x22000000);
                    mIndicator.setFillColor(0xFFec0927);  //0xFF0B928C
                    mIndicator.setStrokeColor(0x00000000);
                    mIndicator.setStrokeWidth(0);

                    mIndicator.setCentered(true);
                    mIndicator.setOnPageChangeListener(this);
                    mIndicator.setVisibility(View.VISIBLE);
                    pll_indicator.setVisibility(View.GONE);
                } else {
                    mIndicator.setVisibility(View.GONE);
                    pll_indicator.setVisibility(View.VISIBLE);
                    mViewPager.addOnPageChangeListener(this);
                }
                showPoi(0, 0);

                adapeter.notifyDataSetChanged();


            } else {

                adapeter.RefreshData();


            }
            //   mIndicator.setStrokeWidth(2 * density);


            if (FreightConstant.isSaveFlow && !mIsInMap) {
                if (mMapView != null) {


                    mMapView.onPause();

                }
            }
        }

//        if (adapeter.getItemCount() <= 0)
//            mBonttomLayout.setVisibility(View.GONE);
//        else
//            mBonttomLayout.setVisibility(View.VISIBLE);
    }

    private void initControl() {


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(RefreshAdapterEvent event) {

        // MLog.e("refreshmappoint","fresh");
        //adapeter.RefreshData();
        if (adapeter != null) {
            int position = adapeter.getItemPosition(event.waybill);
            //  MLog.e("refreshmappoint","fresh"+position);
            adapeter.notifyItemChanged(position);

        }
        //adapeter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {

        super.onResume();


        if (mMapView != null) {
            mMapView.getMap().setTrafficEnabled(false);
            mMapView.onResume();// 当地图控件存在时，调用相应的恢复方法
            if (mSelectedIndex != -1) {

                try {
                    CldRoutePlaner.getInstance().clearRoute();
                } catch (Exception e) {

                }

                showPoi(mSelectedIndex, mType);
            }
            mMapView.update();// 同时更新地图控件状态
        }

    }

    @Override
    public void onPause() {



        super.onPause();

        if (mMapView != null) {
            mMapView.onPause();
        }

//        ArrayList<Overlay> list = CldCustomMarkManager.getInstatnce().getOverlayListCustom();
//        @BindView(d("checkmap","size"+list.size());
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUI(FreightPointEvent event) {
        switch (event.msg.what) {
            case MessageId.MSGID_FREIGHTPOINT_ADPTER:
                setAdapter();
                break;
            case MessageId.MSGID_FREIGHTPOINT_SHOWPOI:

                try {
                    int mappos = 0;

//                    if(FreightPointDeal.getInstace().mPointList.size() == FreightPointDeal.getInstace().getmStore().size()) {
//                        mappos = event.msg.arg1;
//                    }
//                    else {
                    PointInfo info = null;

                    over:
                    for (int i = 0; i < FreightPointDeal.getInstace().mPointList.size(); i++) {

                        info = FreightPointDeal.getInstace().mPointList.get(i);

                        if (info.getPos() == event.msg.arg1) {
                            mappos = i;
                            break over;
                        }
                    }

                    // }
                    showPoi(mappos, 1);
                    mViewPager.setCurrentItem(mappos);
                } catch (Exception e) {

                }

                break;
            case MessageId.MSGID_MAP_RESUME:
                if (mMapView != null) {
                    mMapView.onResume();
                    showPoi(mSelectedIndex, mType);
                    mMapView.update();
                    mIsInMap = true;
                }
                break;
            case MessageId.MSGID_MAP_PAUSE:
                if (mMapView != null) {
                    mMapView.onPause();
                    mIsInMap = false;
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {

        EventBus.getDefault().unregister(this);
        if (mMapView != null) {
            mMapView.destroy();
            mMapView = null;
        }
        super.onDestroyView();
    }

    /**
     * @param index
     * @param type:显示的方式，0是全部显示，要调整比例尺。 1是设置index POI 为中心位置，不调整比例尺
     * @Title: showPoi
     * @Description: TODO
     * @return: void
     */
    private synchronized void showPoi(int index, int type) {

        mType = type;
        if (mMapView == null) {
            return;
        }
        CldMap map = mMapView.getMap();

        MapViewAPI.getInstance().setLocationIconEnabled(map, true);

        if (options == null)
            options = new ArrayList<>();


        paintMapMarker(map, index);
        if (index == -1)
            index = 0;
        mSelectedIndex = index;

        if (type == 0) {
            if (pointList != null)
                MapViewAPI.getInstance().zoomToSpan(pointList);
        } else {
            if (getInstace().mPointList != null)
                MapViewAPI.getInstance().setMapCenter(map, getInstace().mPointList.get(index).getLoc());
        }


        if (FreightPointDeal.getInstace().mPointList != null && FreightPointDeal.getInstace().mPointList.size() > 5) {


            if (index == 0) {
                setViewState(1);
            } else if (index == 1) {
                setViewState(2);
            } else if (index == FreightPointDeal.getInstace().mPointList.size() - 1) {
                setViewState(5);
            } else if (index == FreightPointDeal.getInstace().mPointList.size() - 2) {
                setViewState(4);
            } else {
                setViewState(3);
            }


        }


    }


    public void setViewState(int i) {

        switch (i) {

            case 1:
                v1.setBackground(this.getResources().getDrawable(R.drawable.circle_appcolor));
                v2.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v3.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v4.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v5.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                break;
            case 2:
                v1.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v2.setBackground(this.getResources().getDrawable(R.drawable.circle_appcolor));
                v3.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v4.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v5.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                break;
            case 3:
                v1.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v2.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v3.setBackground(this.getResources().getDrawable(R.drawable.circle_appcolor));
                v4.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v5.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                break;
            case 4:
                v1.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v2.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v3.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v4.setBackground(this.getResources().getDrawable(R.drawable.circle_appcolor));
                v5.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                break;
            case 5:
                v1.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v2.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v3.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v4.setBackground(this.getResources().getDrawable(R.drawable.circle_gray));
                v5.setBackground(this.getResources().getDrawable(R.drawable.circle_appcolor));
                break;

        }

    }


    ArrayList<PointInfo> pointList2;
    ArrayList<LatLng> pointList;

    public synchronized void paintMapMarker(final CldMap map, int index) {


        if (index == -1)
            index = 0;

        MapViewAPI.getInstance().removeAllOverlay(map);
        options.clear();


        ArrayList<PointInfo> tmppointList = getInstace().mPointList;
        if (pointList == null)
            pointList = new ArrayList<LatLng>();

        if (pointList2 == null)
            pointList2 = new ArrayList<PointInfo>();

        pointList.clear();
        pointList2.clear();
        int tenNum = index / 10;
        tenNum = tenNum * 10;

        getlist:
        for (int i = 0; i < 10; i++) {

            if (tmppointList.size() > tenNum + i) {

                pointList.add(tmppointList.get(tenNum + i).getLoc());
                pointList2.add(tmppointList.get(tenNum + i));

            } else {

                break getlist;
            }

        }


        LatLng start = getInstace().mPointList.get(index).getLoc();


        OverlayOptions selectOption = null;
        for (int i = 0; i < pointList2.size(); i++) {
            PointInfo point = pointList2.get(i);

            int picId = R.drawable.ic_water_unselect;
            if (index == i + tenNum) {
                picId = R.drawable.ic_water_select;
            }

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_freightpoint_poi, null);
            TextView txtNum = (TextView) view.findViewById(R.id.freight_point_poi_number);
            ImageView img = (ImageView) view.findViewById(R.id.freight_point_poi_img);

//            txtNum.setHeight();
//            txtNum.setWidth();


            txtNum.setText(point.getPos() + "");//("" + (tenNum+(i + 1)));

            img.setImageResource(picId);

            if (index == i + tenNum) {
                selectOption = new MarkerOptions().position(point.getLoc()).layout(view);
            } else {
                options.add(new MarkerOptions().position(point.getLoc()).layout(view));
            }
        }

        // 选中放在最上面画，不被其他的盖住
        options.add(selectOption);


        //  try {
        MapViewAPI.getInstance().addOverlay(map, options);
//       }catch (Exception e){
//
//       }


    }


    @Override
    public void onPageScrollStateChanged(int arg0) {


    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {


    }

    @Override
    public void onPageSelected(int arg0) {

        showPoi(arg0, 1);
    }

    @Override
    public boolean onBackPressedSupport() {

        return false;
    }

}
