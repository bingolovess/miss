package com.map.track.utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polygon;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bingo on 2019/4/3.
 * Time:2019/4/3
 * 百度地图的Api封装
 */

public class BaiduMapHelper {
    //地图对象 可能是MapView 也可能是TextureMapView
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Context context;
    //默认位置点是北京
    private LatLng defaultPoint = new LatLng(40.056865, 116.307766);
    private float zoom = 16.0f;

    public BaiduMapHelper(Context context, MapView mapView) {
        this.context = context;
        this.mMapView = mapView;
        this.mBaiduMap = mapView.getMap();
    }

    /**
     * 全局地图初始化
     * @param context
     */
    public static void init(Context context){
        SDKInitializer.initialize(context);
    }
    /**
     * 默认配置
     */
    public void defaultConfig(){
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(defaultPoint);
        builder.zoom(zoom);
        mMapView.showZoomControls(false);
        setRotateGestures(false);
    }
    /**
     * 设置我的当前位置
     *
     * @param latitude
     * @param longitude
     * @param markerId
     */
    public void setMyLocation(double latitude,double longitude, float course, int markerId) {
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData data = new MyLocationData.Builder()
                //设定图标方向
                .direction(course)
                //.accuracy(bdLocation.getRadius())//getRadius 获取定位精度,默认值0.0f
                //百度纬度坐标
                .latitude(latitude)
                //百度经度坐标
                .longitude(longitude)
                .build();
        //设置定位数据, 只有先允许定位图层后设置数据才会生效，参见 setMyLocationEnabled(boolean)
        mBaiduMap.setMyLocationData(data);
        //配置定位图层显示方式,三个参数的构造器
        /**
         * 1.定位图层显示模式
         * 2.是否允许显示方向信息
         * 3.用户自定义定位图标
         */
        BitmapDescriptor iconLocation = BitmapDescriptorFactory.fromResource(markerId);
        MyLocationConfiguration configuration
                = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, iconLocation);
        //设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效，参见 setMyLocationEnabled(boolean)
        mBaiduMap.setMyLocationConfigeration(configuration);
    }
    /**
     * 设置中心点和添加marker
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @param isShowLoc
     */
    public void setPosition2Center(double latitude,double longitude, float radius,Boolean isShowLoc) {
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(radius)
                .direction(radius)
                .latitude(latitude)
                .longitude(longitude).build();
        mBaiduMap.setMyLocationData(locData);
        if (isShowLoc) {
            LatLng ll = new LatLng(latitude, longitude);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
        /*BitmapDescriptor iconLocation = BitmapDescriptorFactory.fromResource(R.drawable.map);
        MyLocationConfiguration configuration
                = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, iconLocation);
        //设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效，参见 setMyLocationEnabled(boolean)
        mBaiduMap.setMyLocationConfigeration(configuration);*/
    }


    /**
     * 画文字
     *
     * @param point     位置坐标
     * @param bgColor   背景颜色
     * @param fontSize  字体大小
     * @param fontColor 字体颜色
     * @param text      文字
     * @param rotate    旋转角度
     */
    public void drawText(LatLng point, int bgColor, int fontSize, int fontColor, String text, int rotate) {
        OverlayOptions ooText = new TextOptions()
                .bgColor(bgColor)
                .fontSize(fontSize)
                .fontColor(fontColor)
                .text(text)
                .rotate(rotate)
                .position(point);
        mBaiduMap.addOverlay(ooText);
    }

    /**
     * 画弧线  根据任意三点画弧线
     *
     * @param color
     * @param width
     * @param point1
     * @param point2
     * @param point3
     */
    public void drawArc(int color, int width, LatLng point1, LatLng point2, LatLng point3) {
        OverlayOptions ooArc = new ArcOptions().color(color).width(width).points(point1, point2, point3);
        mBaiduMap.addOverlay(ooArc);
    }

    /**
     * 画折线
     *
     * @param colorId
     * @param width
     * @param points
     */
    public void drawPolyline(int colorId, int width, List<LatLng> points) {
        OverlayOptions ooPolyline = new PolylineOptions()
                .width(width)
                .color(colorId)
                .points(points)
                //.colorsValues(colorValue);//可添加多颜色分段折线
                ;
        mBaiduMap.addOverlay(ooPolyline);
    }

    /**
     *  画多边形
     * @param points
     */
    public Polygon drawPolygon(List<LatLng> points){
        OverlayOptions ooPolygon = new PolygonOptions().points(points)
                .stroke(new Stroke(2, Color.parseColor("#FF0000")))
                .fillColor(Color.parseColor("#4Dca0d0d"));
                //.fillColor(0xAAFFFF00);
        return (Polygon) mBaiduMap.addOverlay(ooPolygon);
    }

    /**
     * 获取点集合中最大的长度
     * @param points
     */
    public int getMaxLength(List<LatLng> points){
        List<Integer> list = new ArrayList<>();
        for (int i = 0;i<points.size();i++){
            if (i < points.size()-1){
                for (int j = i+1;j<points.size();j++){
                    LatLng currentPoint = points.get(i);
                    LatLng nextPoint = points.get(j);
                    double distance = CommonUtil.GetDistance(currentPoint.latitude, currentPoint.longitude, nextPoint.latitude, nextPoint.longitude);
                    list.add((int)(distance*1000));
                }
            }
        }
        //"最大值："+ Collections.max(list)
        return Collections.max(list);
    }

    /**
     * 获取不规则多边形重心点
     * @param mPoints
     */
    public LatLng getCenterOfGravityPoint(List<LatLng> mPoints) {
        double area = 0.0f;//多边形面积
        double x = 0.0f, y = 0.0f;// 重心的x、y
        for (int i = 1; i <= mPoints.size(); i++) {
            double iLat = mPoints.get(i % mPoints.size()).latitude;
            double iLng = mPoints.get(i % mPoints.size()).longitude;
            double nextLat = mPoints.get(i - 1).latitude;
            double nextLng = mPoints.get(i - 1).longitude;
            double temp = (iLat * nextLng - iLng * nextLat) / 2.0f;
            area += temp;
            x += temp * (iLat + nextLat) / 3.0f;
            y += temp * (iLng + nextLng) / 3.0f;
        }
        x = x / area;
        y = y / area;
        return new LatLng(x, y);
    }

    /**
     * 画带纹理的折线
     * @param points 折线坐标组
     * @param textureResId  纹理图片 如：箭头图片 "ic_travel_arraw.png" 必须放在asset文件下
     * @param width         折线的宽度
     * BaiduMapHelper.drawTexturePolyline(Arrays.asList(latLngs),"ic_travel_arraw.png",15);
     */
    public Polyline drawTexturePolyline(List<LatLng> points, String textureResId, int width){
        //添加纹理图片
        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
        BitmapDescriptor mRedTexture = BitmapDescriptorFactory.fromAsset(textureResId);
        textureList.add(mRedTexture);
        // 添加纹理图片对应的顺序
        List<Integer> textureIndexs = new ArrayList<Integer>();
        for (int i=0;i<points.size();i++){
            textureIndexs.add(0);
        }
        OverlayOptions options = new PolylineOptions()
                .textureIndex(textureIndexs)//设置分段纹理index数组
                .customTextureList(textureList)//设置线段的纹理，建议纹理资源长宽均为2的n次方
                .dottedLine(true)
                .color(0xAAFF0000)
                .width(width)
                .points(points);
        return (Polyline) mBaiduMap.addOverlay(options);
    }
    /**
     * 纹理折线，点击时获取折线上点数及width
     *
     * @param width
     * @param points
     * @param textureList
     * @param textureIndexs
     */
    public void drawColorFullPolyline(int width, List<LatLng> points, List<BitmapDescriptor> textureList, List<Integer> textureIndexs) {
        OverlayOptions ooPolyline11 = new PolylineOptions()
                .width(width)
                .points(points)
                .dottedLine(true)
                .customTextureList(textureList)
                .textureIndex(textureIndexs);
         mBaiduMap.addOverlay(ooPolyline11);
    }

    /**
     * 画圆
     *
     * @param point   中心点
     * @param colorId 颜色id  填充色
     * @param radius  半径
     */
    public Overlay drawCircle(LatLng point, int colorId, int radius) {
        OverlayOptions ooCircle = new CircleOptions()
//                .fillColor(0x000000FF)
//                .fillColor(Color.argb(0.3f,255f,0f,0f))
                .fillColor(colorId)
                .center(point)
                .stroke(new Stroke(2, Color.parseColor("#FF0000")))
                .radius(radius);
        return  mBaiduMap.addOverlay(ooCircle);
    }

    /**
     * 画圆
     *
     * @param point    中心点
     * @param colorStr 色值 填充色
     * @param radius   半径
     */
    public Overlay drawCircle(LatLng point, String colorStr, int radius) {
       return drawCircle(point, Color.parseColor(colorStr), radius);
    }

    /**
     * 默认电子围栏 画圆  中间填充30%透明红(#4Dca0d0d)
     *
     * @param point  中心点
     * @param radius 半径
     */
    public Overlay drawCircle(LatLng point, int radius) {
        return drawCircle(point, "#4Dca0d0d", radius);
    }

    /**
     * 清除覆盖物
     */
    public void removeMarker(Marker marker) {
        marker.remove();
    }

    /**
     * 修改覆盖物图标
     *
     * @param marker
     * @param resId
     */
    public void setMarkerIcon(Marker marker, int resId) {
        marker.setIcon(BitmapDescriptorFactory.fromResource(resId));
        mBaiduMap.hideInfoWindow();
    }

    /**
     * 修改覆盖物图标
     *
     * @param marker
     * @param resName assets 资源路径下的文件名称
     */
    public void setMarkerIcon(Marker marker, String resName) {
        marker.setIcon(BitmapDescriptorFactory.fromAssetWithDpi(resName));
        mBaiduMap.hideInfoWindow();
    }

    /**
     * 设置覆盖物位置
     *
     * @param marker
     * @param point
     */
    public void setMarkerPostion(Marker marker, LatLng point) {
        marker.setPosition(point);
        mBaiduMap.hideInfoWindow();
    }

    /**
     * 清除所有图层
     */
    public void clearAllMarker() {
        mBaiduMap.clear();
    }

    /**
     * 定位中心点
     * 示例中心点：40.056865, 116.307766
     *
     * @param latitude
     * @param longitude
     */
    public void setCenterPoint(double latitude, double longitude) {
        setCenterPoint(new LatLng(latitude, longitude));
    }

    /**
     * 设置地图缩放级别 3-20 共十八的级别
     * {"10m", "20m", "50m", "100m", "200m", "500m", "1km", "2km", "5km", "10km", "20km", "25km", "50km", "100km", "200km", "500km", "1000km", "2000km"}
     * Level依次为：20、19、18、17、16、15、14、13、12、11、10、9、8、7、6、5、4、3
     *
     * @param zoom
     */
    public void setMapZoom(float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(zoom);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 通过设置enable为true或false 选择是否启用地图旋转功能
     * @param isRotate
     */
    public void setRotateGestures(boolean isRotate){
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(isRotate);
        uiSettings.setCompassEnabled(false);//不显示指南针
    }

    /**
     * 设置是否可以俯视
     */
    public void setOverlookingGesturesEnabled(boolean overLook){
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setOverlookingGesturesEnabled(true);
    }
    /**
     * 设置中心点
     *
     * @param point
     */
    public void setCenterPoint(LatLng point) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(point);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    /**
     * 定位 指定中心点位置 并显示到地图上
     *
     * @param point
     * @param assetsName assets资源图片名稱
     */
    public void setLocation(LatLng point, String assetsName) {
        setCenterPoint(point);
        addMarker(point, assetsName);
    }

    /**
     * 定位 指定中心点位置 并显示到地图上
     *
     * @param point 位置
     * @param resId 自定义资源图片
     */
    public void setLocation(LatLng point, int resId) {
        setCenterPoint(point);
        addMarker(point, resId);
    }

    /**
     * 设置地图的的缩放按钮
     *
     * @param show true:显示  false:影藏
     */
    public void showZoomControls(boolean show) {
        mMapView.showZoomControls(show);
    }

    /**
     * 是否显示比例尺
     */
    public void showScaleControl(boolean show) {
        mMapView.showScaleControl(show);
    }

    /**
     * 添加自定义坐标位置的图标
     *
     * @param latitude    纬度
     * @param longitude   精度
     * @param markerResId 标记物资源id 如：R.drawable.ic_marker_driving
     */
    public void addMarker(double latitude, double longitude, int markerResId) {
        addMarker(new LatLng(latitude, longitude), markerResId);
    }

    /**
     * 添加自定义坐标位置的图标
     *
     * @param latitude    纬度
     * @param longitude   精度
     * @param markerResId 标记物Assets资源Name 如：ic_marker_driving.png
     */
    public void addMarker(double latitude, double longitude, String markerResId) {
        addMarker(new LatLng(latitude, longitude), markerResId);
    }

    /**
     * 添加自定义坐标位置的图标
     *
     * @param point       坐标
     * @param markerResId 标记物资源id 如：R.drawable.ic_marker_driving
     */
    public Marker addMarker(LatLng point, int markerResId, float[] anchor) {
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(markerResId);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .perspective(false)
                .anchor(anchor[0], anchor[1])
                .icon(bitmap);
        //在地图上添加Marker，并显示
        return (Marker) (mBaiduMap.addOverlay(option));
    }
    public Marker addMarker(LatLng point, int markerResId) {
        return addMarker(point,markerResId, new float[]{0.5f, 0.5f});
    }
    /**
     * 添加自定义坐标位置的图标
     *
     * @param point       坐标
     * @param markerResId 标记物资源id 如：R.drawable.ic_marker_driving
     * @param angle 旋转角度
     */
    public Marker addMarker(LatLng point, int markerResId, int angle) {
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(markerResId);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .perspective(false)
                .rotate(angle)
                .anchor(0.5f, 0.5f)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        return  (Marker) (mBaiduMap.addOverlay(option));
    }

    /**
     * 是否在可視區域
     */
    public boolean getPointVisible(LatLng point){
        LatLngBounds lngBounds = mBaiduMap.getMapStatusLimit();
        List<Marker> markersInBounds = mBaiduMap.getMarkersInBounds(lngBounds);
        for (Marker marker : markersInBounds) {
            LatLng position = marker.getPosition();
            if (point.equals(position)){
                return true;
            }
        }
        return false;
    }
    /**
     * 获取设备资源id
     * @param statusName
     * @param iconTypeId
     */
    private static int getDeviceResId(Context context, String statusName, int iconTypeId){
        return context.getResources().getIdentifier("ic_device_"+statusName+"_"+iconTypeId, "drawable", context.getPackageName());
    }
    /**
     * 采用头条适配后的 适配  Assets下资源适配
     * 添加自定义坐标位置的图标
     *
     * @param point      坐标
     * @param markerName 标记物资源名称 如：ic_marker_driving.png
     * @deprecated  适配已解决 不推荐使用
     */
    public Marker addMarker(LatLng point, String markerName) {
        //构建Marker图标
        BitmapDescriptor bitmap = getFitBitmapDescriptor(markerName);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .perspective(false)
                //覆盖物的对齐点，0.5f,0.5f为覆盖物的中心点
                .anchor(0.5f, 0.5f)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        return (Marker) (mBaiduMap.addOverlay(option));
    }

    /**
     * 适配屏幕 Bitmap
     *
     * @param marker
     * @return BitmapDescriptor
     */
    public BitmapDescriptor getFitBitmapDescriptor(String marker) {
        return BitmapDescriptorFactory.fromAssetWithDpi(marker);
    }

    /**
     * 适配屏幕 Bitmap
     *
     * @param marker
     * @return BitmapDescriptor
     */
    public BitmapDescriptor getFitBitmapDescriptor(int marker) {
        return BitmapDescriptorFactory.fromResource(marker);
    }

    /**
     * 返回是否存在标记物
     *
     * @param marker
     * @return
     */
    public boolean isExist(List<Marker> markerList, Marker marker) {
        if (marker == null) return false;
        for (Marker item : markerList) {
            if (item.equals(marker)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置地图默认图层展示
     */
    public void setMapType(int mapType) {
        mBaiduMap.setMapType(mapType);
    }

    /**
     * 地图图层切换 只是功能切换
     */
    public void toggleMapType() {
        int mapType = mBaiduMap.getMapType();
        switch (mapType) {
            case BaiduMap.MAP_TYPE_SATELLITE:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case BaiduMap.MAP_TYPE_NORMAL:
            default:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
        }
    }

    /**
     * 地图图层切换 普通图层和卫星图层切换
     *
     * @param imageView 图层切换的图片显示
     */
    public void toggleMapType(ImageView imageView) {
        if (imageView != null) {
            imageView.setActivated(!imageView.isActivated());
            if (imageView.isActivated()) {
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            } else {
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            }
        }
    }

    /**
     * 设置是否显示交通图
     *
     * @param isTraffic
     */
    public void setTraffic(boolean isTraffic) {
        mBaiduMap.setTrafficEnabled(isTraffic);
    }

    /**
     * 设置是否显示百度热力图
     *
     * @param isHeatMap
     */
    public void setBaiduHeatMap(boolean isHeatMap) {
        mBaiduMap.setBaiduHeatMapEnabled(isHeatMap);
    }

    /**
     * activity 暂停时同时暂停地图控件
     */
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    /**
     * activity 恢复时同时恢复地图控件
     */
    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    /**
     * 记得释放资源
     * 关闭定位图层/销毁时同时销毁地图控件
     */
    public void clear() {
        if (mMapView != null) {
            mBaiduMap.setMyLocationEnabled(false);
            mMapView.onDestroy();
            mMapView = null;
        }
        if (mBaiduMap != null) {
            mBaiduMap.clear();
        }
    }
}
