package com.miss.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.map.track.CommonMapFragment;
import com.map.track.listener.OnLocationListener;
import com.map.track.model.LocationModel;
import com.map.track.utils.BaiduMapHelper;
import com.map.track.utils.LocationManager;
import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.utils.LogUtils;


/**
 * 百度鹰眼 轨迹查询
 */
public class CommonMapActivity extends BaseActivity {

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_map);
        setStatusBarDark();
        FragmentManager fragmentManager = getSupportFragmentManager();
        CommonMapFragment  commonMapFragment = (CommonMapFragment) fragmentManager.findFragmentById(R.id.common_map);
        BaiduMapHelper mapHelper = commonMapFragment.getMapHelper();
        mapHelper.defaultConfig();
        LocationManager locationManager = new LocationManager(this);
        locationManager.setOnLocationListener(new OnLocationListener() {
            @Override
            public void onLocation(LocationModel model) {
                LogUtils.getInstance().e(model.toString());
                //mapHelper.setCenterPoint(lat,lng);
                //mapHelper.setMyLocation(lat,lng,course,R.drawable.map);
                mapHelper.setPosition2Center(model.latitude,model.longitude,model.radius,true);
            }
        });
        locationManager.start();
    }
}
