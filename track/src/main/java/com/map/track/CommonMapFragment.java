package com.map.track;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.map.track.utils.BaiduMapHelper;

public class CommonMapFragment extends Fragment {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private BaiduMapHelper mapHelper;

    public BaiduMapHelper getMapHelper() {
        return mapHelper;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_baidu,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(getContext(), savedInstanceState);
        mBaiduMap = mMapView.getMap();
        mapHelper = new BaiduMapHelper(getContext(),mMapView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBaiduMap.clear();
        mMapView.onDestroy();
    }
    private void toast(String msg){
        Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
    }
}
