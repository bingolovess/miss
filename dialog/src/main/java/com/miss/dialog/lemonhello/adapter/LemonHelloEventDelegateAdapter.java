package com.miss.dialog.lemonhello.adapter;

import com.miss.dialog.lemonhello.LemonHelloAction;
import com.miss.dialog.lemonhello.LemonHelloInfo;
import com.miss.dialog.lemonhello.LemonHelloView;
import com.miss.dialog.lemonhello.interfaces.LemonHelloEventDelegate;

/**
 * LemonHello 事件代理适配器
 */

public abstract class LemonHelloEventDelegateAdapter implements LemonHelloEventDelegate {

    @Override
    public void onActionDispatch(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {

    }

    @Override
    public void onMaskTouch(LemonHelloView helloView, LemonHelloInfo helloInfo) {

    }
    
}
