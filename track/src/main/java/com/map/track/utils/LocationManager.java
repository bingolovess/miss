package com.map.track.utils;

import android.content.Context;
import android.util.Log;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.map.track.listener.MapOrientationListener;
import com.map.track.listener.OnLocationListener;
import com.map.track.model.LocationModel;

public class LocationManager {
    private LocationClient mLocationClient;
    // 设定图标方向
    private float course;
    private Context context;
    //方向传感器的监听
    private MapOrientationListener orientationListener;
    private MylocationListener mylocationListener;
    private OnLocationListener onLocationListener;
    public void setOnLocationListener(OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
    }
    public LocationManager(Context context){
        this.context = context;
        this.init();
    }
    private void init() {
        //初始化定位
        mLocationClient = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        /**可选，设置定位模式，默认高精度LocationMode.Hight_Accuracy：高精度；
         * LocationMode. Battery_Saving：低功耗；LocationMode. Device_Sensors：仅使用设备；*/
        option.setCoorType("bd09ll");
        /**可选，设置返回经纬度坐标类型，默认gcj02gcj02：国测局坐标；bd09ll：百度经纬度坐标；bd09：百度墨卡托坐标；
         海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标*/
        option.setScanSpan(3000);
        /**可选，设置发起定位请求的间隔，int类型，单位ms如果设置为0，则代表单次定位，即仅定位一次，默认为0如果设置非0，需设置1000ms以上才有效*/
        option.setOpenGps(true);
        /**可选，设置是否使用gps，默认false使用高精度和仅用设备两种定位模式的，参数必须设置为true*/
        option.setLocationNotify(true);
        /**可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false*/
        option.setIgnoreKillProcess(false);
        /**定位SDK内部是一个service，并放到了独立进程。设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)*/
        option.SetIgnoreCacheException(false);
        /**可选，设置是否收集Crash信息，默认收集，即参数为false*/
        option.setIsNeedAltitude(true);/**设置海拔高度*/
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        /**可选，7.2版本新增能力如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位*/
        option.setEnableSimulateGps(false);
        /**可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false*/
        option.setIsNeedAddress(true);
        /**可选，设置是否需要地址信息，默认不需要*/
        // 使用连续定位
        option.setOnceLocation(false);
        mLocationClient.setLocOption(option);
        /**mLocationClient为第二步初始化过的LocationClient对象需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用*/
        iniListener();
    }
    public void start(){
        //为系统的方向传感器注册监听器
        if (orientationListener != null) {
            orientationListener.start();
        }
        if (mylocationListener != null){
            mLocationClient.start();
        }
    }
    public void stop(){
        if (orientationListener != null) {
            orientationListener.stop();
        }
        if (mylocationListener != null){
            mylocationListener = new MylocationListener();
            mLocationClient.unRegisterLocationListener(mylocationListener);
            mLocationClient.stop();
        }
    }
    public void onDestroy() {
        Log.e("LocationManager>>>","onDestroy");
        if (mLocationClient != null) {
            if (mylocationListener!=null){
                mLocationClient.unRegisterLocationListener(mylocationListener);
            }
            mLocationClient.stop();
        }
        if (orientationListener != null) {
            orientationListener.stop();
        }
    }

    /**
     * 处理方向传感器 SensorManager，并实现监听
     */
    private void iniListener() {
        orientationListener = new MapOrientationListener(context);
        orientationListener.setOnOrientationListener(new MapOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                Log.e("LocationManager>>>","course ="+x);
                course = x;
            }
        });
        mylocationListener = new MylocationListener();
        mLocationClient.registerLocationListener(mylocationListener);
    }

    /**
     * 所有的定位信息都通过接口回调来实现  参考示例
     * <p>
     * 可以通过BDLocation配置如下参数
     * 1.accuracy 定位精度
     * 2.latitude 百度纬度坐标
     * 3.longitude 百度经度坐标
     * 4.satellitesNum GPS定位时卫星数目 getSatelliteNumber() gps定位结果时，获取gps锁定用的卫星数
     * 5.speed GPS定位时速度 getSpeed()获取速度，仅gps定位结果时有速度信息，单位公里/小时，默认值0.0f
     * 6.direction GPS定位时方向角度
     */
    public class MylocationListener implements BDLocationListener {
        /**
         * 定位请求回调接口
         */
        private boolean isFirstIn = true;

        /**
         * 定位请求回调函数,这里面会得到定位信息
         * BDLocation 回调的百度坐标类，内部封装了如经纬度、半径等属性信息
         * MyLocationData 定位数据,定位数据建造器
         *
         * @param bdLocation
         */
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //数据包装，回调传出
            LocationModel model = new LocationModel();
            model.latitude = bdLocation.getLatitude();
            model.longitude = bdLocation.getLongitude();
            model.radius = bdLocation.getRadius();//半径
            model.direction = bdLocation.getDirection();//方向
            model.course = course;
            model.adCode = bdLocation.getAdCode();
            model.addrStr = bdLocation.getAddrStr();
            model.country = bdLocation.getCountry();
            model.countryCode = bdLocation.getCountryCode();
            model.province = bdLocation.getProvince();
            model.city = bdLocation.getCity();
            model.cityCode = bdLocation.getCityCode();
            model.district = bdLocation.getDistrict();
            model.street = bdLocation.getStreet();
            model.streetNumber = bdLocation.getStreetNumber();
            model.address = bdLocation.getAddress().address;
            model.adcode = bdLocation.getAdCode();
            model.town = bdLocation.getTown();

            //判断是否为第一次定位,是的话需要定位到用户当前位置
            if (isFirstIn) {
                isFirstIn = false;
            }
            Log.e("LocationManager>>>",model.latitude+"------"+model.longitude+"--------------"+ model.addrStr);
            if (onLocationListener!=null){
                onLocationListener.onLocation(model);
            }
        }
    }
}
