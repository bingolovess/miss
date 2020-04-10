package com.miss.dialog.lemonhello.interfaces;

import com.miss.dialog.lemonhello.LemonHelloAction;
import com.miss.dialog.lemonhello.LemonHelloInfo;
import com.miss.dialog.lemonhello.LemonHelloView;

/**
 * LemonHello - 事件回调代理
 */

public interface LemonHelloActionDelegate {

    void onClick(
            LemonHelloView helloView,
            LemonHelloInfo helloInfo,
            LemonHelloAction helloAction
    );

}
