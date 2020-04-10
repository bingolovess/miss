package com.miss.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.view.CommonHeader;

public class FormInputActivity extends BaseActivity {

    private TextView infoTv;
    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_input);
        StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.colorPrimary));//设置背景颜色
        CommonHeader commonHeader = findViewById(R.id.common_header);
        commonHeader.setTitle("表单组件");
        infoTv = findViewById(R.id.cpu_info);
    }

}
