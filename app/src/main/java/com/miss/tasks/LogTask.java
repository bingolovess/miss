package com.miss.tasks;

import android.os.StrictMode;

import androidx.annotation.Nullable;

import com.miss.BuildConfig;
import com.miss.launch.Task;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

public class LogTask extends Task {
    private boolean DEV_MODE = true;
    @Override
    public void run() {
        //LogUtils.getInstance().setDebug(true);
        // startStrictMode();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)           //（可选）是否显示线程信息。 默认值为true
                .methodCount(2)                  //（可选）要显示的方法行数。 默认2
                .methodOffset(7)                 //（可选）设置调用堆栈的函数偏移值，0的话则从打印该Log的函数开始输出堆栈信息，默认是0
                //.logStrategy()                 //（可选）更改要打印的日志策略。 默认LogCat
                .tag("Miss")                     //（可选）每个日志的全局标记。 默认PRETTY_LOGGER（如上图）
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                //return super.isLoggable(priority, tag);
                return BuildConfig.DEBUG;
            }
        });
    }
    /**
     * 严格模式的开启可以放在Application或者Activity以及其他组件的onCreate方法。为了更好地分析应用中的问题，建议放在Application的onCreate方法中。
     * 其中，我们只需要在app的开发版本下使用 StrictMode，线上版本避免使用 StrictMode，这里定义了一个布尔值变量DEV_MODE来进行控制。
     */
    private void startStrictMode() {
        if (DEV_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectCustomSlowCalls() //API等级11，使用StrictMode.noteSlowCode
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyDialog() //弹出违规提示对话框
                    .penaltyLog() //在Logcat 中打印违规异常信息
                    .penaltyFlashScreen() //API等级11
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects() //API等级11
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }
}
