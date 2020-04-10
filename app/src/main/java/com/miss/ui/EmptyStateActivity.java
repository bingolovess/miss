package com.miss.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.base.toast.ToastUtil;
import com.miss.view.CommonHeader;
import com.ww.emptystate.EmptyState;

public class EmptyStateActivity extends BaseActivity {
    public EmptyState emptyState;
    private int i = 0;
    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_state);
        StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.colorPrimary));//设置背景颜色
        CommonHeader commonHeader = findViewById(R.id.common_header);
        commonHeader.setTitle("状态组件");

        emptyState = findViewById(R.id.emptyState);
        emptyState.setButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (i){
                    case 1:
                        setNoNet();
                        break;
                    case 2:
                    default:
                        setEmpty();
                        break;
                }
                ToastUtil.show(EmptyStateActivity.this,"Hello"+i);
                i++;
                if (i > 2){
                    i = 0 ;
                }
            }
        });
        setEmpty();
        show(true);
    }

    /**
     *  延迟操作
     * @param show  显示还是隐藏
     */
    private void show(boolean show){
        if (show){
            emptyState.show(android.R.anim.slide_in_left,new OvershootInterpolator());
        } else {
            emptyState.hide(android.R.anim.slide_out_right, new OvershootInterpolator());
        }
    }

    /**
     * 设置空数据
     */
    private void setEmpty(){
        emptyState.setTitle("");
        emptyState.setDescription("暂无数据");
        emptyState.setButtonText("点击刷新");
        emptyState.setIcon(R.drawable.ic_empty);
    }

    /**
     * 设置无网络
     */
    private void setNoNet(){
        emptyState.setTitle("");
        emptyState.setDescription("暂无网络");
        emptyState.setButtonText("点击刷新");
        emptyState.setIcon(R.drawable.ic_no_net);
    }
}
