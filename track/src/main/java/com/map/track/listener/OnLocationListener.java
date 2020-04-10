package com.map.track.listener;

import com.map.track.model.LocationModel;

/**
 * 定位坐标回调
 */
public interface OnLocationListener {
    /**
     * @param model
     */
    void onLocation(LocationModel model);
}
