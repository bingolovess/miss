package com.miss.dialog.lemonbubble.interfaces;


import com.miss.dialog.lemonbubble.LemonBubbleInfo;
import com.miss.dialog.lemonbubble.LemonBubbleView;

/**
 * 柠檬泡泡控件的蒙版被触摸的回调上下文
 */

public interface LemonBubbleMaskOnTouchContext {

    void onTouch(LemonBubbleInfo bubbleInfo, LemonBubbleView bubbleView);

}
