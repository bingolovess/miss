package com.miss.tasks;

import com.miss.base.utils.TypefaceUtil;
import com.miss.launch.Task;

public class TypefaceTask extends Task {
    @Override
    public void run() {
        initTypeface();
    }
    private void initTypeface() {
        TypefaceUtil.replaceSystemDefaultFont(mContext, "fonts/fz.ttf");
    }
}
