package com.miss.base.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * <p>Android Fragment 布局使用 fitsSystemWindows = true 无效解决方案</p>
 *
 * 当第一个Fragment添加到Activity中的时候，Activity寻找出有fitsSystemWindows的子布局为其预留出状态栏的空间，其实就是设置一个padding，
 * 而其他Fragment添加到Activity中的时候，因为状态栏空间的适配已经被消费过一次了，Activity并不会再次去添加这个padding。
 * 因此我们需要自定义一个FrameLayout，重写它的状态栏空间适配的时机和它的适配事件的分发
 *
 */
public class IFrameLayout extends FrameLayout {
    public IFrameLayout(Context context) {
        this(context, null);
    }

    public IFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                requestApplyInsets();
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++)
            getChildAt(index).dispatchApplyWindowInsets(insets);
        return insets;
    }
}
