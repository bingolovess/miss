package com.miss.base.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * Android 后台运行白名单，优雅实现保活
 * <p> 需要权限 <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" /> </p>
 */
public class WhiteListManager {

    /**
     * 判断我们的应用是否在白名单中
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return isIgnoring;
    }

    /**
     * 申请加入白名单
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestIgnoreBatteryOptimizations(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 跳转到指定应用的首页
     */
    public static void showActivity(@NonNull Context context,@NonNull String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    public static void showActivity(@NonNull Context context,@NonNull String packageName, @NonNull String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    //===================================部分手机厂商判断 =========================================//
    /**
     * 华为
     */
    public static boolean isHuawei() {
        if (Build.BRAND == null) {
            return false;
        } else {
            return Build.BRAND.toLowerCase().equals("huawei") || Build.BRAND.toLowerCase().equals("honor");
        }
    }

    /**
     * 跳转华为手机管家的启动管理页  应用启动管理 -> 关闭应用开关 -> 打开允许自启动
     * @param context
     */
    public static void goHuaweiSetting(Context context) {
        try {
            showActivity(context,"com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        } catch (Exception e) {
            showActivity(context,"com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
        }
    }

    /**
     * 小米
     */
    public static boolean isXiaomi() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("xiaomi");
    }

    /**
     * 跳转小米安全中心的自启动管理页面 授权管理 -> 自启动管理 -> 允许应用自启动
     * @param context
     */
    public static void goXiaomiSetting(Context context) {
        showActivity(context,"com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity");
    }

    /**
     * OPPO
     * @return
     */
    public static boolean isOPPO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("oppo");
    }

    /**
     * 跳转 OPPO 手机管家   操作步骤：权限隐私 -> 自启动管理 -> 允许应用自启动
     * @param context
     */
    public static void goOPPOSetting(Context context) {
        try {
            showActivity(context,"com.coloros.phonemanager");
        } catch (Exception e1) {
            try {
                showActivity(context,"com.oppo.safe");
            } catch (Exception e2) {
                try {
                    showActivity(context,"com.coloros.oppoguardelf");
                } catch (Exception e3) {
                    showActivity(context,"com.coloros.safecenter");
                }
            }
        }
    }

    /**
     * VIVO
     */
    public static boolean isVIVO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("vivo");
    }

    /**
     * 跳转 VIVO 手机管家     操作步骤：权限管理 -> 自启动 -> 允许应用自启动
     * @param context
     */
    public static void goVIVOSetting(Context context) {
        showActivity(context,"com.iqoo.secure");
    }

    /**
     * 魅族
     */
    public static boolean isMeizu() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("meizu");
    }

    /**
     * 跳转魅族手机管家     操作步骤：权限管理 -> 后台管理 -> 点击应用 -> 允许后台运行
     * @param context
     */
    public static void goMeizuSetting(Context context) {
        showActivity(context,"com.meizu.safe");
    }

    /**
     * 三星
     */
    public static boolean isSamsung() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("samsung");
    }

    /**
     * 跳转三星智能管理器
     * 操作步骤：自动运行应用程序 -> 打开应用开关 -> 电池管理 -> 未监视的应用程序 -> 添加应用
     */
    public static void goSamsungSetting(Context context) {
        try {
            showActivity(context,"com.samsung.android.sm_cn");
        } catch (Exception e) {
            showActivity(context,"com.samsung.android.sm");
        }
    }

    /**
     * 乐视
     */
    public static boolean isLeTV() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("letv");
    }

    /**
     * 跳转乐视手机管家     操作步骤：自启动管理 -> 允许应用自启动
     */
    public static void goLetvSetting(Context context) {
        showActivity(context,"com.letv.android.letvsafe","com.letv.android.letvsafe.AutobootManageActivity");
    }

    /**
     * 锤子
     */
    public static boolean isSmartisan() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("smartisan");
    }

    /**
     * 跳转锤子手机管理  操作步骤：权限管理 -> 自启动权限管理 -> 点击应用 -> 允许被系统启动
     */
    public static void goSmartisanSetting(Context context) {
        showActivity(context,"com.smartisanos.security");
    }
}
