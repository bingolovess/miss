package com.miss.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.base.toast.ToastUtil;
import com.miss.base.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p>
 *   谷歌爸爸在安卓8.0版本时为了支持全面屏，增加了一个限制：如果是透明的Activity，则不能固定它的方向，因为它的方向其实是依赖其父Activity的（因为透明）。
 *   然而这个bug只有在8.0中有，8.1中已经修复。具体crash有两种：
 *  1.Activity的风格为透明，在manifest文件中指定了一个方向，则在onCreate中crash
 *  2.Activity的风格为透明，如果调用setRequestedOrientation方法固定方向，则crash
 * </p>

 */
public abstract class BaseActivity extends AppCompatActivity {
    //是否可退出
    private boolean canBack = true;
    //是否全屏显示
    public abstract boolean isFullScreen();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fixWxFullScreenTranslucentBug();
    }

    /**
     * 设置黑白屏效果
     */
    protected void setBlackWhiteScreen(){
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE,paint);
    }


    /**
     *  适配android 8.0 全面屏透明主题Crash Bug
     */
    protected void fixWxFullScreenTranslucentBug(){
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            boolean result = fixOrientation();
            LogUtils.getInstance().d("onCreate fixOrientation when Oreo, result = " + result);
        }
    }
    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setContentView(int layoutResID) {
        //setBlackWhiteScreen();
        super.setContentView(layoutResID);
        initStatusBar();
    }

    private void initStatusBar() {
        //沉浸式代码配置
        if (isFullScreen()){
            StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        } else {
            //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
            StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        }
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
    }
    public void setStatusBarDark(){
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

    /**
     * 简单统一调用自定义 Toast
     */
    public void toast(String msg){
        ToastUtil.show(this,msg);
    }
    public boolean isCanBack() {
        return canBack;
    }

    public void setCanBack(boolean canBack) {
        this.canBack = canBack;
    }

    @Override
    public void onBackPressed() {
        if (!canBack) return;
        super.onBackPressed();
    }

    private boolean isTranslucentOrFloating(){
        boolean isTranslucentOrFloating = false;
        try {
            int [] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean)m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            LogUtils.getInstance().d("avoid calling setRequestedOrientation when Oreo.");
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);//右进左出效果
        //overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);//下进上出效果
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
        //overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
}
