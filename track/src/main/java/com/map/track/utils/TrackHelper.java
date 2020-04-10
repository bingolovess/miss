package com.map.track.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.BaseRequest;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.baidu.trace.model.TransportMode;
import com.map.track.model.CurrentLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Track
 */
public class TrackHelper {
    private static final String TAG = TrackHelper.class.getCanonicalName();
    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    private LocRequest locRequest = null;
    public Context mContext = null;
    public SharedPreferences trackConf = null;
    /**
     * 轨迹客户端
     */
    public LBSTraceClient mClient = null;

    /**
     * 轨迹服务
     */
    public Trace mTrace = null;

    /**
     * 轨迹服务ID
     */
    public long serviceId = 218340;//这里是申请的鹰眼服务id

    /**
     * Entity标识
     */
    public String entityName = "myTrace";

    public boolean isRegisterReceiver = false;

    /**
     * 服务是否开启标识
     */
    public boolean isTraceStarted = false;

    /**
     * 采集是否开启标识
     */
    public boolean isGatherStarted = false;

    public int screenWidth = 0, screenHeight = 0;

    public TrackHelper(Context context) {
        init(context);
    }

    private void init(Context context){
        mContext = context;
        entityName = CommonUtil.getImei(context);
        // 若为创建独立进程，则不初始化成员变量
        if ("com.baidu.track:remote".equals(CommonUtil.getCurProcessName(mContext))) {
            return;
        }
        SDKInitializer.initialize(mContext);
        getScreenSize();
        mClient = new LBSTraceClient(mContext);
        mTrace = new Trace(serviceId, entityName);
        trackConf = context.getSharedPreferences("track_conf", MODE_PRIVATE);
        locRequest = new LocRequest(serviceId);
        mClient.setOnCustomAttributeListener(new OnCustomAttributeListener() {
            @Override
            public Map<String, String> onTrackAttributeCallback() {
                Map<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");
                return map;
            }
        });
        clearTraceStatus();
    }

    /**
     * 获取当前位置
     */
    public void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener) {
        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
        if (NetUtil.isNetworkAvailable(mContext)
                && trackConf.contains("is_trace_started")
                && trackConf.contains("is_gather_started")
                && trackConf.getBoolean("is_trace_started", false)
                && trackConf.getBoolean("is_gather_started", false)) {
            LatestPointRequest request = new LatestPointRequest(getTag(), serviceId, entityName);
            ProcessOption processOption = new ProcessOption();
            processOption.setRadiusThreshold(50);
            processOption.setTransportMode(TransportMode.walking);
            processOption.setNeedDenoise(true);
            processOption.setNeedMapMatch(true);
            request.setProcessOption(processOption);
            mClient.queryLatestPoint(request, trackListener);
        } else {
            mClient.queryRealTimeLoc(locRequest, entityListener);
        }
    }
    /**
     * 清除Trace状态：初始化app时，判断上次是正常停止服务还是强制杀死进程，根据trackConf中是否有is_trace_started字段进行判断。
     *
     * 停止服务成功后，会将该字段清除；若未清除，表明为非正常停止服务。
     */
    private void clearTraceStatus() {
        if (trackConf.contains("is_trace_started") || trackConf.contains("is_gather_started")) {
            SharedPreferences.Editor editor = trackConf.edit();
            editor.remove("is_trace_started");
            editor.remove("is_gather_started");
            editor.apply();
        }
    }

    /**
     * 初始化请求公共参数
     *
     * @param request
     */
    public void initRequest(BaseRequest request) {
        request.setTag(getTag());
        request.setServiceId(serviceId);
    }

    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }
    /**
     * 获取屏幕尺寸
     */
    private void getScreenSize() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
    }
    //================================服务启动模块=============================================

    private OnTraceListener traceListener = null;//轨迹服务监听器
    private OnTrackListener trackListener = null;//轨迹监听器(用于接收纠偏后实时位置回调)
    private OnEntityListener entityListener = null;//Entity监听器(用于接收实时定位回调)
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;
    private TrackReceiver trackReceiver = null;
    private MapUtil mapUtil = null;//地图工具
    private ViewUtil viewUtil = null;
    private SensorManager mSensorManager;
    private RealTimeHandler realTimeHandler = new RealTimeHandler();//实时定位任务
    private RealTimeLocRunnable realTimeLocRunnable = null;
    private int packInterval = Constants.DEFAULT_PACK_INTERVAL;//打包周期
    private List<LatLng> trackPoints;//轨迹点集合
    private boolean firstLocate = true;
    private int mCurrentDirection = 0;

    public void initTrackServer(){
        viewUtil = new ViewUtil();
        mapUtil = MapUtil.getInstance();
        //MapView mapView = new MapView(mContext);
        //mapUtil.init(mapView);
        //mapUtil.setCenter(mCurrentDirection);//设置地图中心点
        powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);// 获取传感器管理服务
        trackPoints = new ArrayList<>();
        initTrackListener();
    }

    private void initTrackListener() {
        trackListener = new OnTrackListener() {
            @Override
            public void onLatestPointCallback(LatestPointResponse response) {
                //经过服务端纠偏后的最新的一个位置点，回调
                try {
                    if (StatusCodes.SUCCESS != response.getStatus()) {
                        return;
                    }
                    LatestPoint point = response.getLatestPoint();
                    if (null == point || CommonUtil.isZeroPoint(point.getLocation().getLatitude(), point.getLocation()
                            .getLongitude())) {
                        return;
                    }
                    LatLng currentLatLng = mapUtil.convertTrace2Map(point.getLocation());
                    if (null == currentLatLng) {
                        return;
                    }
                    if(firstLocate){
                        firstLocate = false;
                        Log.d(TAG,"起点获取中，请稍后...");
                        return;
                    }
                    //当前经纬度
                    CurrentLocation.locTime = point.getLocTime();
                    CurrentLocation.latitude = currentLatLng.latitude;
                    CurrentLocation.longitude = currentLatLng.longitude;
                    if (trackPoints == null) {
                        return;
                    }
                    trackPoints.add(currentLatLng);
                    mapUtil.drawHistoryTrack(trackPoints, false, mCurrentDirection);//时时动态的画出运动轨迹
                } catch (Exception x) {

                }
            }
        };
        entityListener = new OnEntityListener() {
            @Override
            public void onReceiveLocation(TraceLocation location) {
                //本地LBSTraceClient客户端获取的位置
                try {
                    if (StatusCodes.SUCCESS != location.getStatus() || CommonUtil.isZeroPoint(location.getLatitude(),
                            location.getLongitude())) {
                        return;
                    }
                    LatLng currentLatLng = mapUtil.convertTraceLocation2Map(location);
                    if (null == currentLatLng) {
                        return;
                    }
                    CurrentLocation.locTime = CommonUtil.toTimeStamp(location.getTime());
                    CurrentLocation.latitude = currentLatLng.latitude;
                    CurrentLocation.longitude = currentLatLng.longitude;
                    if (null != mapUtil) {
                        mapUtil.updateMapLocation(currentLatLng, mCurrentDirection);//显示当前位置
                        mapUtil.animateMapStatus(currentLatLng);//缩放
                    }
                } catch (Exception x) {
                }
            }

        };
        traceListener = new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int errorNo, String message) {
                Log.d(TAG,String.format("onBindServiceCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            @Override
            public void onStartTraceCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.START_TRACE_NETWORK_CONNECT_FAILED <= errorNo) {
                    isTraceStarted = true;
                    SharedPreferences.Editor editor = trackConf.edit();
                    editor.putBoolean("is_trace_started", true);
                    editor.apply();
                    registerReceiver();
                }
                Log.d(TAG,String.format("onStartTraceCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            @Override
            public void onStopTraceCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.CACHE_TRACK_NOT_UPLOAD == errorNo) {
                    isTraceStarted = false;
                    isGatherStarted = false;
                    // 停止成功后，直接移除is_trace_started记录（便于区分用户没有停止服务，直接杀死进程的情况）
                    SharedPreferences.Editor editor = trackConf.edit();
                    editor.remove("is_trace_started");
                    editor.remove("is_gather_started");
                    editor.apply();
                    unregisterPowerReceiver();
                    firstLocate = true;
                }
                Log.d(TAG,String.format("onStopTraceCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            @Override
            public void onStartGatherCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STARTED == errorNo) {
                    isGatherStarted = true;
                    SharedPreferences.Editor editor = trackConf.edit();
                    editor.putBoolean("is_gather_started", true);
                    editor.apply();
                    stopRealTimeLoc();
                    startRealTimeLoc(packInterval);
                }
                Log.d(TAG,String.format("onStartGatherCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            @Override
            public void onStopGatherCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STOPPED == errorNo) {
                    isGatherStarted = false;
                    SharedPreferences.Editor editor = trackConf.edit();
                    editor.remove("is_gather_started");
                    editor.apply();
                    firstLocate = true;
                    stopRealTimeLoc();
                    startRealTimeLoc(Constants.LOC_INTERVAL);
                    if (trackPoints.size() >= 1) {
                        try {
                            mapUtil.drawEndPoint(trackPoints.get(trackPoints.size() - 1));
                        } catch (Exception e) {
                        }
                    }
                }
                Log.d(TAG, String.format("onStopGatherCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            @Override
            public void onPushCallback(byte messageType, PushMessage pushMessage) {

            }
        };
    }

    /**
     * 注册广播（电源锁、GPS状态）
     */
    private void registerReceiver() {
        if (isRegisterReceiver) {
            return;
        }

        if (null == wakeLock) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
        }
        if (null == trackReceiver) {
            trackReceiver = new TrackReceiver(wakeLock);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(StatusCodes.GPS_STATUS_ACTION);
        mContext.registerReceiver(trackReceiver, filter);
        isRegisterReceiver = true;
    }

    private void unregisterPowerReceiver() {
        if (!isRegisterReceiver) {
            return;
        }
        if (null != trackReceiver) {
            mContext.unregisterReceiver(trackReceiver);
        }
       isRegisterReceiver = false;
    }
    static class RealTimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
    /**
     * 实时定位任务
     */
    class RealTimeLocRunnable implements Runnable {

        private int interval = 0;

        public RealTimeLocRunnable(int interval) {
            this.interval = interval;
        }

        @Override
        public void run() {
            getCurrentLocation(entityListener, trackListener);
            realTimeHandler.postDelayed(this, interval * 1000);
        }
    }

    public void startRealTimeLoc(int interval) {
        realTimeLocRunnable = new RealTimeLocRunnable(interval);
        realTimeHandler.post(realTimeLocRunnable);
    }

    public void stopRealTimeLoc() {
        if (null != realTimeHandler && null != realTimeLocRunnable) {
            realTimeHandler.removeCallbacks(realTimeLocRunnable);
        }
        mClient.stopRealTimeLoc();
    }
    /**
     * 启动服务
     */
    public void startServer(){
        if (!isTraceStarted) {
            mClient.startTrace(mTrace, traceListener);//开始服务
        }
    }

    /**
     * 开始采集
     */
    public void startGather(){
        if (isGatherStarted) {
            mClient.stopGather(traceListener);
        } else {
            mClient.setInterval(Constants.DEFAULT_GATHER_INTERVAL, packInterval);
            mClient.startGather(traceListener);
        }
    }
    /**
     * 结束服务
     */
    public void stopServer(){
        if (isTraceStarted) {
            mClient.stopTrace(mTrace, traceListener);//停止服务
        }
    }
    //================================服务启动模块=============================================
}
