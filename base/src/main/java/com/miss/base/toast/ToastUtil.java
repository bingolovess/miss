package com.miss.base.toast;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.miss.base.R;


/**
 * @Date: 2018/11/13
 * @Author: heweizong
 * @Description: 简单封装
 */
public class ToastUtil {

    /**
     * 使用默认布局
     */
    public static void show(Context mContext, String msg) {
        if (mContext == null || msg == null) return;
        DToast.make(mContext)
                .setText(R.id.tv_content_default, msg)
                .setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 120)
                .show();
    }


    /**
     * 通过setView()设置自定义的Toast布局
     */
    public static void showAtCenter(Context mContext, String msg) {
        if (mContext == null || msg == null) return;
        DToast.make(mContext)
                .setView(View.inflate(mContext, R.layout.layout_toast, null))
                .setText(R.id.tv_content_default, msg)
                .setGravity(Gravity.CENTER, 0, 0)
                .showLong();
    }

    //退出APP时调用
    public static void cancelAll() {
        DToast.cancel();
    }

    /**
     * 退出是调用
     * @param context
     */
    public static  void cancel(Context context){
        if (context instanceof Activity){
            DToast.cancelActivityToast((Activity) context);
        } else {
            DToast.cancel();
        }
    }
}
