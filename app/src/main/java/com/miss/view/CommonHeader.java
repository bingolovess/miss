package com.miss.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.miss.R;

public class CommonHeader extends RelativeLayout {
    private Context context;
    private boolean hideStatusView,hideToolbar;
    private int backgroundColor;
    private Toolbar toolbar;
    private View statusView;//默认占位的状态栏view
    private LinearLayout leftLinearLayout,centerLinearLayout,rightLinearLayout;
    private TextView leftTextView,centerTextView,rightTextView;

    public CommonHeader(Context context) {
        super(context);
    }

    public CommonHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public CommonHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        this.context = context;
        View.inflate(context, R.layout.layout_common_header, this);	//挂载view
        //加载自定义的属性
        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.CommonHeader);
        hideStatusView=a.getBoolean(R.styleable.CommonHeader_hide,true);
        hideToolbar=a.getBoolean(R.styleable.CommonHeader_empty,false);
        backgroundColor=a.getColor(R.styleable.CommonHeader_color,  getResources().getColor(R.color.colorPrimary));
        initView();
        //回收资源，这一句必须调用
        a.recycle();
    }
    private void initView(){
        statusView = findViewById(R.id.status_bar);
        toolbar= findViewById(R.id.toolbar);
        leftLinearLayout = findViewById(R.id.left);
        leftTextView= findViewById(R.id.left_text);

        centerLinearLayout = findViewById(R.id.center);
        centerTextView = findViewById(R.id.center_text);

        rightLinearLayout = findViewById(R.id.right);
        rightTextView= findViewById(R.id.right_text);

        setHideStatusView(hideStatusView);
        hideToolbar(hideToolbar);
        setThemeColor(backgroundColor);
        handleStatusView();
    }
    /**
     * 隐藏 toolbar
     */
    public void hideToolbar(boolean hide){
        if (toolbar!=null){
            if (hide){
                toolbar.setVisibility(GONE);
            } else {
                toolbar.setVisibility(VISIBLE);
            }
        }
    }
    /**
     * 此方法会在所有的控件都从xml文件中加载完成后调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //initView();
    }

    public void setHideStatusView(boolean hideStatusView) {
        this.hideStatusView = hideStatusView;
        if(statusView != null){
            if (hideStatusView){
                statusView.setVisibility(GONE);
            } else {
                statusView.setVisibility(VISIBLE);
            }
        }
    }

    public void setThemeColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        if (statusView != null){
            statusView.setBackgroundColor(backgroundColor);
        }
        if (toolbar != null){
            toolbar.setBackgroundColor(backgroundColor);
        }
    }

    public void setLeftText(String text){
        if (leftTextView != null){
            leftTextView.setText(text);
        }
    }

    public void setTitle(String text){
        if (centerTextView != null){
            centerTextView.setText(text);
        }
    }

    public void setRightText(String text){
        if (rightTextView != null){
            rightTextView.setText(text);
        }
    }

    public void setLeftOnClickListener(View.OnClickListener l){
        leftLinearLayout.setOnClickListener(l);
    }
    public void setRightOnClickListener(View.OnClickListener l){
        rightLinearLayout.setOnClickListener(l);
    }

    /**
     * 处理状态栏
     */
    private void handleStatusView(){
        int statusBarHeight = getStatusBarHeight(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusView.setLayoutParams(params);
    }

    /**
     * 获取状态栏高度
     *
     * @param context 上下文
     * @return 100
     */
    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
