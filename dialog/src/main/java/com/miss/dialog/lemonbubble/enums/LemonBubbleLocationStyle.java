package com.miss.dialog.lemonbubble.enums;

/**
 * 泡泡控件的位置的枚举
 */

public enum LemonBubbleLocationStyle {

    /**
     * 位于屏幕的顶部
     */
    TOP(0),
    /**
     * 位于屏幕的中间
     */
    CENTER(0),
    /**
     * 位于屏幕的底部
     */
    BOTTOM(1);

    private int value;

    public int getValue() {
        return value;
    }

    LemonBubbleLocationStyle(int value) {
        this.value = value;
    }

}
