package com.miss.ui;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.base.utils.LogUtils;
import com.miss.bean.User;
import com.miss.view.CommonHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.superLei.aoparms.annotation.Async;
import cn.com.superLei.aoparms.annotation.Cache;
import cn.com.superLei.aoparms.annotation.CacheEvict;
import cn.com.superLei.aoparms.annotation.Callback;
import cn.com.superLei.aoparms.annotation.Delay;
import cn.com.superLei.aoparms.annotation.DelayAway;
import cn.com.superLei.aoparms.annotation.Intercept;
import cn.com.superLei.aoparms.annotation.Permission;
import cn.com.superLei.aoparms.annotation.PermissionDenied;
import cn.com.superLei.aoparms.annotation.PermissionNoAskDenied;
import cn.com.superLei.aoparms.annotation.Prefs;
import cn.com.superLei.aoparms.annotation.PrefsEvict;
import cn.com.superLei.aoparms.annotation.Retry;
import cn.com.superLei.aoparms.annotation.Safe;
import cn.com.superLei.aoparms.annotation.Scheduled;
import cn.com.superLei.aoparms.annotation.SingleClick;
import cn.com.superLei.aoparms.common.permission.AopPermissionUtils;
import cn.com.superLei.aoparms.common.utils.ArmsCache;
import cn.com.superLei.aoparms.common.utils.ArmsPreference;

/**
 * PS: 存储缓存数据 若是对象,对象需要转成可序列话的对象
 */
public class AopArmsActivity extends BaseActivity {
    @BindView(R.id.common_header)
    CommonHeader commonHeader;

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aop_arms);
        StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.colorPrimary));//设置背景颜色
        ButterKnife.bind(this);
        commonHeader.setTitle("Aop");
        initCacheData();
        initUser();
    }
    /**
     * 开启请求权限注解
     * @ value 权限值
     * @ rationale 拒绝后的下一次提示(开启后，拒绝后，下一次会先提示该权限申请提示语)
     * @ requestCode 权限请求码标识
     */
    @Permission(value = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, rationale = "为了更好的体验，请打开相关权限")
    public void permission(View view) {
        LogUtils.getInstance().e( "permission: 权限已打开");
    }

    /**
     * 请求拒绝注解回调
     * @param requestCode 权限请求码标识
     * @param denyList 被拒绝的权限集合
     */
    @PermissionDenied
    public void permissionDenied(int requestCode, List<String> denyList){
        LogUtils.getInstance().e("permissionDenied: "+requestCode);
        LogUtils.getInstance().e("permissionDenied>>>: "+denyList.toString());
    }

    /**
     * 请求拒绝且不在提示注解回调
     * @param requestCode 权限请求码标识
     * @param denyNoAskList 被拒绝且不再提示的权限集合
     */
    @PermissionNoAskDenied
    public void permissionNoAskDenied(int requestCode, List<String> denyNoAskList){
        LogUtils.getInstance().e("permissionNoAskDenied: "+requestCode);
        LogUtils.getInstance().e("permissionNoAskDenied>>>: "+denyNoAskList.toString());
        AopPermissionUtils.showGoSetting(this, "为了更好的体验，建议前往设置页面打开权限");
    }
    /**
     * 插入缓存
     * key：缓存的键
     * expiry：缓存过期时间,单位s
     * @return 缓存的值
     */
    @Cache(key = "userList", expiry = 60 * 60 * 24)
    private ArrayList<User> initCacheData() {
        ArrayList<User> list = new ArrayList<>();
        for (int i=0; i<5; i++){
            User user = new User();
            user.setName("艾神一不小心:"+i);
            user.setAge(i);
            list.add(user);
        }
        return list;
    }
    public void getUserList(View view){
        ArrayList<User> userList = ArmsCache.get(this).getAsList("userList", User.class);
        LogUtils.getInstance().json(new Gson().toJson(userList));
    }
    @Prefs(key = "user")
    private User initUser() {
        return new User("bingo",18);
    }

    public void getUser(View view){
        User user = ArmsPreference.get(this, "user", null);
        LogUtils.getInstance().e("getUser: " + new Gson().toJson(user));
        LogUtils.getInstance().json(new Gson().toJson(user));
    }
    /**
     * key:缓存的键
     * beforeInvocation:缓存的清除是否在方法之前执行, 如果出现异常缓存就不会清除   默认false
     * allEntries：是否清空所有缓存(与key互斥)  默认false
     */
    @CacheEvict(key = "userList", beforeInvocation = true, allEntries = false)
    public void removeUser(View view) {
        LogUtils.getInstance().e( "removeUser: >>>>");
    }

    /**
     * key:sp的键
     * allEntries：是否清空所有存储(与key互斥)  默认false
     */
    @PrefsEvict(key = "article", allEntries = false)
    public void removeArticle(View view) {
        LogUtils.getInstance().e( "removeArticle: >>>>");
    }

    @Async
    public void asyn(View view) {
        LogUtils.getInstance().e( "useAync: " + Thread.currentThread().getName());
    }

    @Safe(callBack = "throwMethod")
    public void safe(View view) {
        //str.toString();
    }

    @SingleClick(value = 2000L)
    private void onclick() {
        LogUtils.getInstance().e( "onclick: >>>>");
    }

    @Callback
    public void throwMethod(Throwable throwable) {
        LogUtils.getInstance().e( "throwMethod: >>>>>" + throwable.toString());
    }

    @Retry(count = 3, delay = 1000, asyn = true, retryCallback = "retryCallback")
    public boolean retry(View view) {
        LogUtils.getInstance().e( "retryDo: >>>>>>" + Thread.currentThread().getName());
        return false;
    }

    @Callback
    public void retryCallback(boolean result) {
        LogUtils.getInstance().e( "retryCallback: >>>>" + result);
    }

    @Scheduled(interval = 1000L, count = 10, taskExpiredCallback = "taskExpiredCallback")
    public void scheduled(View view) {
        LogUtils.getInstance().e( "scheduled: >>>>");
    }

    @Callback
    public void taskExpiredCallback() {
        LogUtils.getInstance().e( "taskExpiredCallback: >>>>");
    }

    @Delay(key = "test", delay = 10000L)
    public void delay(View view) { LogUtils.getInstance().e( "delay: >>>>>");
    }

    @DelayAway(key = "test")
    public void cancelDelay(View view) {
        LogUtils.getInstance().e( "cancelDelay: >>>>");
    }

    @Intercept("login_intercept")
    public void intercept(View view) {
        LogUtils.getInstance().e(  "intercept: 已登陆>>>>");
    }

    @Prefs(key = "userId")
    public String login(View view) {
        return "1";
    }

    @PrefsEvict(key = "userId")
    public void logout(View view) {
        LogUtils.getInstance().e( "logout: >>>>>");
    }

    @SingleClick(ids = {R.id.singleClick, R.id.singleClick2})
    @OnClick({R.id.singleClick1, R.id.singleClick, R.id.singleClick2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.singleClick1:
                LogUtils.getInstance().e( "我不防抖");
                break;
            case R.id.singleClick:
                LogUtils.getInstance().e( "我防抖");
                break;
            case R.id.singleClick2:
                LogUtils.getInstance().e(  "我防抖2");
                break;
        }
    }
}
