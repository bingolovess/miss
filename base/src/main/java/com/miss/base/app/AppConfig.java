package com.miss.base.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AppConfig implements Application.ActivityLifecycleCallbacks {
    private static AppConfig config;
    //Activity栈管理
    private List<Activity> activityStack;
    private Application application;
    //字体缩放倍数
    private float mFontScale = 1.0f;
    private AppConfig(){
        activityStack = new ArrayList<>();
    }
    public static AppConfig getConfig(){
        if (config == null){
            synchronized (AppConfig.class){
                if (config == null){
                    config = new AppConfig();
                }
            }
        }
        return  config;
    }

    public void initConfig(Application application){
        this.application = application;
        application.registerActivityLifecycleCallbacks(this);
    }

    public float getFontScale() {
        return mFontScale;
    }

    /**
     * 设置字体大小
     * @param fontScale
     */
    public void setAppFontSize(float fontScale) {
        if (application != null) {
            if (activityStack != null && !activityStack.isEmpty()) {
                for (Activity activity : activityStack) {
                    /*if (activity instanceof SettingActivity) {
                        continue;
                    }*/
                    Resources resources = activity.getResources();
                    if (resources != null) {
                        android.content.res.Configuration configuration = resources.getConfiguration();
                        configuration.fontScale = fontScale;
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                        activity.recreate();
                        if (mFontScale != fontScale) {
                            mFontScale = fontScale;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        if (activityStack == null) {
            activityStack = new ArrayList<>();
        }
        // 禁止字体大小随系统设置变化
        Resources resources = activity.getResources();
        if (resources != null && resources.getConfiguration().fontScale != mFontScale) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = mFontScale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        activityStack.add(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activity.finish();
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activityStack != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 获取本地软件版本号
     */
    public int getLocalVersion(Context context) {
        int localVersion = 0;
        try {
        PackageInfo packageInfo = context.getApplicationContext()
                .getPackageManager()
                .getPackageInfo(context.getPackageName(), 0);
        localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public String getLocalVersionName(Context context) {
        String localVersionName = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersionName;
    }

    /**
     * 关闭最上面的一个Activity
     */
    public void finishTopActivity(){
        if (activityStack != null) {
            int size = activityStack.size();
            if (size>0){
                Activity activity = activityStack.get(size - 1);
                if (activity!=null && !activity.isFinishing()){
                    activity.finish();
                    activityStack.remove(activity);
                }
            }
        }
    }

    /**
     * 页面关闭返回
     * @param step  返回的步数
     */
    public void back(int step){
        if (activityStack != null) {
            int size = activityStack.size();
            List<Activity> temp = new ArrayList<>();
            if (step>0 && step<size){
                for (int i = size-1;i>0;i--){
                    Activity activity = activityStack.get(i);
                    temp.add(activity);
                    if (activity != null && !activity.isFinishing()){
                        activity.finish();
                    }
                }
                activityStack.removeAll(temp);
            }
        }
    }
}
