package com.miss.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.adapter.recyclerview.CommonAdapter;
import com.miss.base.adapter.recyclerview.MultiItemTypeAdapter;
import com.miss.base.adapter.recyclerview.base.ViewHolder;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.base.utils.ResourceUtils;
import com.miss.bean.WebLink;
import com.miss.promise.Promise;
import com.miss.view.CommonHeader;

import java.util.List;

public class WebLinkActivity extends BaseActivity {

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
        commonHeader.setTitle("web链接资源");
        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        String data = ResourceUtils.readAssets2String(this, "webLink.json", "utf-8");
        WebLink webLink = new Gson().fromJson(data, WebLink.class);
        List<WebLink.DataBean> list = webLink.getData();
        CommonAdapter adapter = new CommonAdapter<WebLink.DataBean>(this, R.layout.layout_common_item,list) {
            @Override
            public void convert(ViewHolder holder, WebLink.DataBean o, int pos) {
                holder.setText(R.id.title_tv,o.getName());
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(WebLinkActivity.this,WebActivity.class);
                intent.putExtra("title",list.get(position).getName());
                intent.putExtra("url",list.get(position).getLink());
                startActivity(intent);
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
