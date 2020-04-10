package com.miss.tasks;

import com.map.track.utils.BaiduMapHelper;
import com.miss.launch.Task;

/**
 * 百度地图全局初始化
 */
public class MapTask extends Task {
    @Override
    public void run() {
        BaiduMapHelper.init(mContext);
    }
}
