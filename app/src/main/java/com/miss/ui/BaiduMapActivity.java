package com.miss.ui;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;

import com.map.track.BaiduMapFragment;
import com.map.track.CommonMapFragment;
import com.map.track.utils.BaiduMapHelper;
import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.utils.LogUtils;


/**
 * 百度地图 轨迹回放示例
 */
public class BaiduMapActivity extends BaseActivity {

    private SeekBar mSeekBar;
    private  BaiduMapFragment mapFragment;
    @Override
    public boolean isFullScreen() {
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_map);
        setStatusBarDark();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = (BaiduMapFragment) fragmentManager.findFragmentById(R.id.fragment_map);
        //BaiduMapHelper mapHelper = mapFragment.getMapHelper();
        mSeekBar = findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBarListener());
        int max = mapFragment.getMoveArrLength();
        mSeekBar.setMax(max);
        setSeekBarColor(mSeekBar, Color.parseColor("#3D93FB"));
    }
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogUtils.getInstance().e("progress = "+progress);
            mapFragment.setChangeIndex(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * 代码设置SeekBar颜色
     *
     * @param seekBar
     * @param color
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setSeekBarColor(SeekBar seekBar, int color) {
        LayerDrawable layerDrawable = (LayerDrawable)
                seekBar.getProgressDrawable();
        Drawable dra = layerDrawable.getDrawable(2);
        dra.setColorFilter(color, PorterDuff.Mode.SRC);
        seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        seekBar.invalidate();
    }
    public void speedType1(View view){
        mapFragment.setSpeed(BaiduMapFragment.SPEED_TYPE_1);
    }
    public void speedType2(View view){
        mapFragment.setSpeed(BaiduMapFragment.SPEED_TYPE_2);
    }
    public void speedType4(View view){
        mapFragment.setSpeed(BaiduMapFragment.SPEED_TYPE_4);
    }

}
