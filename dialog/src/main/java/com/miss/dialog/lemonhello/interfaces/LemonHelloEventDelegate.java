package com.miss.dialog.lemonhello.interfaces;

import com.miss.dialog.lemonhello.LemonHelloAction;
import com.miss.dialog.lemonhello.LemonHelloInfo;
import com.miss.dialog.lemonhello.LemonHelloView;

/**
 * LemonHello 事件代理
 * 处理Action、取消等事件
 */

public interface LemonHelloEventDelegate {

    /**
     * 事件被触发的回调代理
     *
     * @param helloView   触发的对话框控件
     * @param helloInfo   触发时显示的信息描对象
     * @param helloAction 触发的Action
     */
    void onActionDispatch(
            LemonHelloView helloView,
            LemonHelloInfo helloInfo,
            LemonHelloAction helloAction
    );

    /**
     * 对话框背景蒙版被触摸的回调代理
     *
     * @param helloView 触发的对话框控件
     * @param helloInfo 出发时显示的信息描述对象
     */
    void onMaskTouch(
            LemonHelloView helloView,
            LemonHelloInfo helloInfo
    );

}
