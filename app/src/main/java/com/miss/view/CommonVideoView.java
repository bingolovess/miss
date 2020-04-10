package com.miss.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * VideoView
 */
public class CommonVideoView extends VideoView {
    public CommonVideoView(Context context) {
        super(context);
    }

    public CommonVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(w, (int) (w / 0.56f));
    }
}
