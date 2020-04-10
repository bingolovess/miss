package com.miss.ui;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.map.track.BaiduMapFragment;
import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.paint.BrushesFragment;
import com.miss.view.CommonHeader;

public class BrushesActivity extends BaseActivity {

    private BrushesFragment brushesFragment;
    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brushes);
        StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.colorPrimary));//设置背景颜色
        CommonHeader commonHeader = findViewById(R.id.common_header);
        commonHeader.setTitle("画笔内嵌");
        FragmentManager fragmentManager = getSupportFragmentManager();
        brushesFragment = (BrushesFragment) fragmentManager.findFragmentById(R.id.fragment_brushes);
    }
}
