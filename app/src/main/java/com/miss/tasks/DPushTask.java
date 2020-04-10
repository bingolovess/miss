package com.miss.tasks;

import com.domob.sdk.DPush;
import com.miss.launch.Task;

/**
 * 多盟广告 轻推送
 */
public class DPushTask extends Task {
    @Override
    public void run() {
        DPush.setDebugMode(true);//设置调试模式是否打印	log
        DPush.initWithAppKey(mContext.getApplicationContext(),"96AgVdhw0XOIAoOBUy");
    }
}
