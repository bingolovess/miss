package com.map.track;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.map.track.utils.BaiduMapHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class BaiduMapFragment extends Fragment {

    private MapView mMapView = null;
    //private TextureMapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Handler mHandler = null;
    private Polyline mPolyline;
    private Marker mMoveMarker;
    private BaiduMapHelper mapHelper;
    /**
     * 通过设置间隔时间和距离可以控制速度和图标移动的距离
     */
    private static final int TIME_INTERVAL = 80;
    private static final double DISTANCE = 0.00002;
    /**
     * 三种速度值 1倍数/2倍数/4倍数
     */
    public static final int SPEED_TYPE_1 = 1;
    public static final int SPEED_TYPE_2 = 2;
    public static final int SPEED_TYPE_4 = 4;

    /**
     * 用IntDef 包含几个常量
     * 枚举类名用接口替代
     */
    @IntDef({SPEED_TYPE_1, SPEED_TYPE_2, SPEED_TYPE_4})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SpeedEnums {
    }

    /**
     * 当前倍数
     * 默认1倍速
     */
    private int SPEED_TYPE = SPEED_TYPE_1;

    /**
     * 设置速度模式
     *
     * @param speedType
     */
    public void setSpeed(@SpeedEnums int speedType) {
        this.SPEED_TYPE = speedType;
    }

    private BitmapDescriptor mGreenTexture = BitmapDescriptorFactory.fromAsset("Icon_road_green_arrow.png");
    private BitmapDescriptor mBitmapCar = BitmapDescriptorFactory.fromResource(R.drawable.car);
    public static final LatLng[] latlngs = new LatLng[]{
            new LatLng(40.055826, 116.307917),
            new LatLng(40.055916, 116.308455),
            new LatLng(40.055967, 116.308549),
            new LatLng(40.056014, 116.308574),
            new LatLng(40.056440, 116.308485),
            new LatLng(40.056816, 116.308352),
            new LatLng(40.057997, 116.307725),
            new LatLng(40.058022, 116.307693),
            new LatLng(40.058029, 116.307590),
            new LatLng(40.057913, 116.307119),
            new LatLng(40.057850, 116.306945),
            new LatLng(40.057756, 116.306915),
            new LatLng(40.057225, 116.307164),
            new LatLng(40.056134, 116.307546),
            new LatLng(40.055879, 116.307636),
            new LatLng(40.055826, 116.307697),
    };
    /**
     * 当前平滑移动的数据源
     */
    private LatLng[] moveArr = latlngs;
    /**
     * 当前平滑移动的位置
     */
    private int currentIndex = 0;
    private int changeIndex = -1;

    public int getMoveArrLength() {
        return moveArr == null?0:moveArr.length;
    }

    public void setMoveArr(LatLng[] moveArr) {
        currentIndex = 0;
        this.moveArr = moveArr;
    }

    /**
     * 改变当前位置
     * @param index
     */
    public void setChangeIndex(int index) {
        this.changeIndex = index;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_baidu,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(getContext(), savedInstanceState);
        mBaiduMap = mMapView.getMap();

        mapHelper = new BaiduMapHelper(getContext(),mMapView);
        mapHelper.defaultConfig();
        mHandler = new Handler(Looper.getMainLooper());
        mapHelper.setCenterPoint(40.056865, 116.307766);
        mapHelper.setMapZoom(19.0f);
        drawPolyLine();
        moveLooper();
    }
    private void drawPolyLine() {
        List<LatLng> polylines = new ArrayList<>();
        for (int index = 0; index < latlngs.length; index++) {
            polylines.add(latlngs[index]);
        }
        polylines.add(latlngs[0]);
        // 绘制纹理PolyLine
        PolylineOptions polylineOptions = new PolylineOptions().points(polylines).width(10).customTexture(mGreenTexture)
                .dottedLine(true);
        mPolyline = (Polyline) mBaiduMap.addOverlay(polylineOptions);

        // 添加小车marker
        OverlayOptions markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(mBitmapCar).
                position(polylines.get(0)).rotate((float) getAngle(0));
        mMoveMarker = (Marker) mBaiduMap.addOverlay(markerOptions);

        // 添加InfoWindow相关
        Button button = new Button(getContext());
        //button.setBackgroundResource(R.drawable.popup);
        button.setText("我是InfoWindow");
        InfoWindow.OnInfoWindowClickListener infoWindowClickListener = new InfoWindow.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {
                toast("点击了InfoWindow");
            }
        };
        // 添加 Marker 关联的InfoWindow,两者的更新是相互独立的。
        mMoveMarker.showInfoWindow(new InfoWindow(BitmapDescriptorFactory.fromView(button),polylines.get(0), -47,
                infoWindowClickListener));
    }

    /**
     * 根据点获取图标转的角度
     */
    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= mPolyline.getPoints().size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = mPolyline.getPoints().get(startIndex);
        LatLng endPoint = mPolyline.getPoints().get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        } else if (slope == 0.0) {
            if (toPoint.longitude > fromPoint.longitude) {
                return -90;
            } else {
                return 90;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {
        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mBitmapCar.recycle();
        //mGreenTexture.recycle();
        mBitmapCar = null;
        mGreenTexture = null;
        mBaiduMap.clear();
        mMapView.onDestroy();
    }

    /**
     * 计算x方向每次移动的距离
     */
    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE || slope == 0.0) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * 1 / slope) / Math.sqrt(1 + 1 / (slope * slope)));
    }

    /**
     * 计算y方向每次移动的距离
     */
    private double getYMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE || slope == 0.0) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 循环进行移动逻辑
     */
    public void moveLooper() {
        new Thread() {
            public void run() {
                while (true) {
                    for (int i = 0; i < moveArr.length - 1; i++) {
                        if (changeIndex != -1){
                            i = changeIndex -1;
                            changeIndex = -1;
                            continue;
                        }
                        currentIndex = i;
                        Log.e("当前位置：","currentIndex = "+currentIndex);
                        final LatLng startPoint = moveArr[i];
                        final LatLng endPoint = moveArr[i + 1];
                        mMoveMarker.setPosition(startPoint);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // refresh marker's rotate
                                if (mMapView == null) {
                                    return;
                                }
                                mMoveMarker.setRotate((float) getAngle(startPoint, endPoint));
                            }
                        });
                        double slope = getSlope(startPoint, endPoint);
                        // 是不是正向的标示
                        boolean isYReverse = (startPoint.latitude > endPoint.latitude);
                        boolean isXReverse = (startPoint.longitude > endPoint.longitude);
                        double intercept = getInterception(slope, startPoint);
                        double xMoveDistance = isXReverse ? getXMoveDistance(slope) : -1 * getXMoveDistance(slope);
                        double yMoveDistance = isYReverse ? getYMoveDistance(slope) : -1 * getYMoveDistance(slope);

                        for (double j = startPoint.latitude, k = startPoint.longitude;
                             !((j > endPoint.latitude) ^ isYReverse) && !((k > endPoint.longitude) ^ isXReverse); ) {
                            LatLng latLng = null;

                            if (slope == Double.MAX_VALUE) {
                                latLng = new LatLng(j, k);
                                j = j - yMoveDistance;
                            } else if (slope == 0.0) {
                                latLng = new LatLng(j, k - xMoveDistance);
                                k = k - xMoveDistance;
                            } else {
                                latLng = new LatLng(j, (j - intercept) / slope);
                                j = j - yMoveDistance;
                            }

                            final LatLng finalLatLng = latLng;
                            if (finalLatLng.latitude == 0 && finalLatLng.longitude == 0) {
                                continue;
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mMapView == null) {
                                        return;
                                    }
                                    mMoveMarker.setPosition(finalLatLng);
                                    // 设置 Marker 覆盖物的位置坐标,并同步更新与Marker关联的InfoWindow的位置坐标.
                                    mMoveMarker.setPositionWithInfoWindow(finalLatLng);
                                }
                            });
                            try {
                                Thread.sleep(TIME_INTERVAL / SPEED_TYPE);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }.start();
    }
    /**
     * 循环进行移动逻辑
     */
    public void moveLooper2() {
        new Thread() {
            public void run() {
                for (int i = 0; i < latlngs.length - 1; i++) {
                    final LatLng startPoint = latlngs[i];
                    final LatLng endPoint = latlngs[i + 1];
                    mMoveMarker.setPosition(startPoint);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // refresh marker's rotate
                            if (mMapView == null) {
                                return;
                            }
                            mMoveMarker.setRotate((float) getAngle(startPoint, endPoint));
                        }
                    });
                    double slope = getSlope(startPoint, endPoint);
                    // 是不是正向的标示
                    boolean isYReverse = (startPoint.latitude > endPoint.latitude);
                    boolean isXReverse = (startPoint.longitude > endPoint.longitude);
                    double intercept = getInterception(slope, startPoint);
                    double xMoveDistance = isXReverse ? getXMoveDistance(slope) : -1 * getXMoveDistance(slope);
                    double yMoveDistance = isYReverse ? getYMoveDistance(slope) : -1 * getYMoveDistance(slope);

                    for (double j = startPoint.latitude, k = startPoint.longitude;
                         !((j > endPoint.latitude) ^ isYReverse) && !((k > endPoint.longitude) ^ isXReverse); ) {
                        LatLng latLng = null;

                        if (slope == Double.MAX_VALUE) {
                            latLng = new LatLng(j, k);
                            j = j - yMoveDistance;
                        } else if (slope == 0.0) {
                            latLng = new LatLng(j, k - xMoveDistance);
                            k = k - xMoveDistance;
                        } else {
                            latLng = new LatLng(j, (j - intercept) / slope);
                            j = j - yMoveDistance;
                        }

                        final LatLng finalLatLng = latLng;
                        if (finalLatLng.latitude == 0 && finalLatLng.longitude == 0) {
                            continue;
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mMapView == null) {
                                    return;
                                }
                                mMoveMarker.setPosition(finalLatLng);
                                // 设置 Marker 覆盖物的位置坐标,并同步更新与Marker关联的InfoWindow的位置坐标.
                                mMoveMarker.setPositionWithInfoWindow(finalLatLng);
                            }
                        });
                        try {
                            Thread.sleep(TIME_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }.start();
    }
    private void toast(String msg){
        Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
    }
}
