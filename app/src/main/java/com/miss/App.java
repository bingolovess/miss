package com.miss;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.miss.launch.TaskDispatcher;
import com.miss.tasks.AopArmsTask;
import com.miss.tasks.DPushTask;
import com.miss.tasks.LogTask;
import com.miss.tasks.MapTask;
import com.miss.tasks.RetrofitTask;
import com.miss.tasks.RxHttpTask;
import com.miss.tasks.TypefaceTask;
/**性能检测启动速度 命令*/
//adb shell am start -W com.miss/com.miss.ui.GuideActivity
public class App extends Application {
    private static App app;
    private Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        context = this.getApplicationContext();

        TaskDispatcher.init(this);
        TaskDispatcher dispatcher = TaskDispatcher.createInstance();
        dispatcher
                .addTask(new RetrofitTask())
                .addTask(new AopArmsTask())
                .addTask(new MapTask())
                .addTask(new TypefaceTask())
                .addTask(new RxHttpTask())
                .addTask(new DPushTask())
                .addTask(new LogTask())
                .start();
        dispatcher.await();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static App getApp() {
        return app;
    }

    public Context getContext() {
        return context;
    }
}
