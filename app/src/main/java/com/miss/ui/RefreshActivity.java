package com.miss.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.miss.R;
import com.miss.adapter.AdapterDiffCallback;
import com.miss.adapter.MultipleItemAdapter;
import com.miss.base.BaseActivity;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.base.toast.ToastUtil;
import com.miss.base.utils.LogUtils;
import com.miss.base.utils.NetUtils;
import com.miss.bean.MultipleItem;
import com.miss.refresh.ItemDecoration.DividerDecoration;
import com.miss.refresh.interfaces.OnItemClickListener;
import com.miss.refresh.interfaces.OnItemLongClickListener;
import com.miss.refresh.interfaces.OnLoadMoreListener;
import com.miss.refresh.interfaces.OnNetWorkErrorListener;
import com.miss.refresh.interfaces.OnRefreshListener;
import com.miss.refresh.recyclerview.LRecyclerView;
import com.miss.refresh.recyclerview.LRecyclerViewAdapter;
import com.miss.refresh.recyclerview.ProgressStyle;
import com.miss.view.CommonHeader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RefreshActivity extends BaseActivity {
    /**服务器端一共多少条数据*/
    private static final int TOTAL_COUNTER = 64;

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private MultipleItemAdapter mMultipleItemAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(this);

    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.colorPrimary));
        CommonHeader commonHeader = findViewById(R.id.common_header);
        commonHeader.setTitle("刷新");
        mRecyclerView = findViewById(R.id.recycle_view);
        mMultipleItemAdapter = new MultipleItemAdapter(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mMultipleItemAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.split)
                .build();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        //LinearLayout sampleHeader = (LinearLayout) getLayoutInflater().inflate( R.layout.sample_header, null);
        //sampleHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mLRecyclerViewAdapter.addHeaderView(new SampleHeader(this));

        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mMultipleItemAdapter.clear();
                //mLRecyclerViewAdapter.notifyDataSetChanged();//fix bug:crapped or attached views may not be recycled. isScrap:false isAttached:true
                mCurrentCounter = 0;
                requestData();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mCurrentCounter < TOTAL_COUNTER) {
                    // loading more
                    requestData();
                } else {
                    //the end
                    mRecyclerView.setNoMore(true);
                }
            }
        });

        mRecyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {

            @Override
            public void onScrollUp() {
            }

            @Override
            public void onScrollDown() {
            }


            @Override
            public void onScrolled(int distanceX, int distanceY) {
            }

            @Override
            public void onScrollStateChanged(int state) {

            }

        });

        mRecyclerView.refresh();//强制刷新 mRecyclerView.forceToRefresh();

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MultipleItem item = mMultipleItemAdapter.getDataList().get(position);
                ToastUtil.show(RefreshActivity.this, item.getTitle());
            }
        });

        mLRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                MultipleItem item = mMultipleItemAdapter.getDataList().get(position);
                ToastUtil.show(RefreshActivity.this, "onItemLongClick - " + item.getTitle());
            }
        });
    }
    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<MultipleItem> list) {
        mMultipleItemAdapter.addAll(list);
        mCurrentCounter += list.size();
    }
    private class PreviewHandler extends Handler {

        private WeakReference<RefreshActivity> ref;

        PreviewHandler(RefreshActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final RefreshActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case -1:
                    if (mCurrentCounter == 0){
                        mMultipleItemAdapter.clear();
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    int currentSize = activity.mMultipleItemAdapter.getItemCount();
                    //模拟组装10个数据
                    ArrayList<MultipleItem> newList = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        if (newList.size() + currentSize >= TOTAL_COUNTER) {
                            break;
                        }
                        MultipleItem item ;
                        if(i == 2){
                            item = new MultipleItem(MultipleItem.IMG);
                        }else {
                            item = new MultipleItem(MultipleItem.TEXT);
                        }
                        item.setTitle("item"+(currentSize+i));
                        newList.add(item);
                    }
                    /*List<MultipleItem> oldList = mMultipleItemAdapter.getDataList();
                    LogUtils.getInstance().e("size = "+oldList.size());
                    if (oldList!=null&&oldList.size() == 0 ){
                        activity.addItems(newList);
                        activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                        activity.notifyDataSetChanged();
                    } else {
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AdapterDiffCallback(oldList,newList));
                        diffResult.dispatchUpdatesTo(mMultipleItemAdapter);
                        activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                        activity.notifyDataSetChanged();
                    }*/
                    activity.addItems(newList);
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    activity.notifyDataSetChanged();
                    break;
                case -2:
                    activity.notifyDataSetChanged();
                    break;
                case -3:
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    activity.notifyDataSetChanged();
                    activity.mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
                        @Override
                        public void reload() {
                            requestData();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 模拟请求网络
     */
    private void requestData() {
        LogUtils.getInstance().e( "requestData");
        new Thread() {

            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //模拟一下网络请求失败的情况
                if(NetUtils.isNetworkAvailable(RefreshActivity.this)) {
                    mHandler.sendEmptyMessage(-1);
                } else {
                    mHandler.sendEmptyMessage(-3);
                }
            }
        }.start();
    }


    /**
     * RecyclerView的HeaderView，简单的展示一个TextView
     */
    public class SampleHeader extends RelativeLayout {

        public SampleHeader(Context context) {
            super(context);
            init(context);
        }

        public SampleHeader(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public SampleHeader(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        public void init(Context context) {
            inflate(context, R.layout.sample_header, this);
        }
    }
}
