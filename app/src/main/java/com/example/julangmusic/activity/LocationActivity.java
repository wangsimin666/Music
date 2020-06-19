package com.example.julangmusic.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.julangmusic.R;

/**
 * @author l
 * @date 2020/5/20 - 9:51
 * @function 定位
 */
public class LocationActivity extends BaseActivity implements View.OnClickListener {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LatLng mUser_latlng;
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;

    //Activity生命周期开始方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initPermission();
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        location();
    }

    private void location() {
        mBaiduMap.setMyLocationEnabled(true);

        //定位初始化
        mLocationClient = new LocationClient(this);

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        //开启地图定位图层
        mLocationClient.start();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // mUser_latlng 用户在地图上的位置，抽成成员变量即可
            // location.getLatitude() 获取纬度信息  location.getLongitude() 获取经度信息
            mUser_latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mBaiduMap.setMyLocationData(locData);
            locationUser();
            String city = location.getCity();
        }
    }

    private void locationUser() {
        MapStatusUpdate status2 = MapStatusUpdateFactory.newLatLng(mUser_latlng);
        mBaiduMap.setMapStatus(status2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bmapView:
                break;
        }
    }

    private void initPermission() {
        String[] per = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA};

        ActivityCompat.requestPermissions(this, per, 100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

}
