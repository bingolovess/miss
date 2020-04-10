package com.miss.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.adapter.recyclerview.CommonAdapter;
import com.miss.base.adapter.recyclerview.MultiItemTypeAdapter;
import com.miss.base.adapter.recyclerview.base.ViewHolder;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.base.toast.ToastUtil;
import com.miss.base.utils.LogUtils;
import com.miss.base.utils.MD5;
import com.miss.base.utils.ResourceUtils;
import com.miss.bean.News;
import com.miss.bean.User;
import com.miss.http.BaseObserver;
import com.miss.http.BaseObserverListener;
import com.miss.http.bean.BaseResponse;
import com.miss.promise.Callback;
import com.miss.promise.NextTask;
import com.miss.promise.Promise;
import com.miss.promise.Task;
import com.miss.promise.utils.ThreadUtils;
import com.miss.service.ServiceBuilder;
import com.miss.view.CommonHeader;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.superLei.aoparms.annotation.Permission;
import cn.com.superLei.aoparms.annotation.PermissionDenied;
import cn.com.superLei.aoparms.annotation.PermissionNoAskDenied;
import cn.com.superLei.aoparms.common.permission.AopPermissionUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private CommonHeader commonHeader;

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.colorPrimary));//设置背景颜色
        commonHeader = findViewById(R.id.common_header);
        commonHeader.setTitle("首页");
        initView();
        permission();
        /*boolean huawei = WhiteListManager.isHuawei();
        if (huawei){
            boolean ignoringBatteryOptimizations = WhiteListManager.isIgnoringBatteryOptimizations(this);
            if (!ignoringBatteryOptimizations){
                WhiteListManager.requestIgnoreBatteryOptimizations(this);
            }
        }*/
    }
    /**
     * 开启请求权限注解
     * @ value 权限值
     * @ rationale 拒绝后的下一次提示(开启后，拒绝后，下一次会先提示该权限申请提示语)
     * @ requestCode 权限请求码标识
     */
    @Permission(value = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE}, rationale = "为了更好的体验，请打开相关权限")
    public void permission() {
        LogUtils.getInstance().e( "permission: 权限已打开");
        Logger.i("no thread info and method info");

        Logger.t("tag").e("Custom tag for only one use");

        Logger.json("{ \"key\": 3, \"value\": something}");

        Logger.d(Arrays.asList("foo", "bar"));
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
    private void initData() {
        Promise.with(this,Integer.class)
                .then(new Task<Integer, String>() {

                    @Override
                    public void run(Integer value, NextTask<String> next) {
                        Map<String, String> map = new HashMap<>();
                        map.put("username", "bingo");
                        map.put("password", MD5.getMD5("123456"));
                        ServiceBuilder.getApiService()
                                .login(map)
                                .subscribeOn(Schedulers.io())//运行在io线程
                                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                                .subscribe(new BaseObserverListener<BaseResponse<User>>() {

                                    @Override
                                    protected void onSuccess(BaseResponse<User> userBaseResponse) {

                                    }
                                });
                                /*.subscribe(new BaseObserver<User>() {
                                    @Override
                                    public void onSuccess(User data) {
                                        String nextStr = new Gson().toJson(data);
                                        LogUtils.getInstance().e("--------1--------" + nextStr);
                                        next.run(nextStr);
                                    }

                                    @Override
                                    public void onFailure(Throwable e) {
                                        next.fail(null,(Exception) e);
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });*/

                    }
                })
                .thenOnAsyncThread(new Task<String, Object>(){
                    @Override
                    public void run(String value, NextTask<Object> next) {
                        try {
                            ThreadUtils.checkNotMainThread();
                            LogUtils.getInstance().e( "This task is running on the no main thread  value = "+ value);
                            Thread.sleep(1000);
                            //int i = 1/0;
                            next.run("bingo");
                        } catch (Exception e) {
                            next.fail(null,e);
                        }
                    }
                })
                .setCallback(new Callback<Object>() {
                    @Override
                    public void onSuccess(Object result) {
                        LogUtils.getInstance().e("onSuccess: "+ result);
                    }

                    @Override
                    public void onFailure(Bundle result, Exception exception) {
                        LogUtils.getInstance().e("onFailure: "+exception.getMessage());
                    }
                })
                .create().execute(1);
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        String data = ResourceUtils.readAssets2String(this, "news.json", "utf-8");
        News news = new Gson().fromJson(data, News.class);
        List<News.DataBean> list = news.getData();
        CommonAdapter adapter = new CommonAdapter<News.DataBean>(this, R.layout.layout_common_item,list) {
            @Override
            public void convert(ViewHolder holder, News.DataBean o, int pos) {
                holder.setText(R.id.title_tv,o.getTitle());
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                //startActivity(new Intent(MainActivity.this,AopArmsActivity.class));
                String className = list.get(position).getPage();
                ComponentName com = new ComponentName(getPackageName(), className);
                Intent intent = new Intent();
                intent.setComponent(com);
                startActivity(intent);
                ToastUtil.show(MainActivity.this,""+list.get(position).getTitle());
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        Promise.destroyWith(this);
        super.onPause();
    }
}
