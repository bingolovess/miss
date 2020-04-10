package com.miss.view.dialog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * 改方法不再推荐使用，待优化
 */
@Deprecated
public class CustomDialogBuilder{
    private Context context;
    private BackgroundLinearLayout backgroundLayout;
    private CustomDialogView dialogView;
    private int backgroundAlpha = 150;//背景透明度
    
	public CustomDialogBuilder(Context context){
    	this.context = context;
    	dialogView = new CustomDialogView(context);
    }
    /**
     * 显示dialog
     */
    public void show(){
		//创建背景布局
    	backgroundLayout = new BackgroundLinearLayout(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		backgroundLayout.setLayoutParams(layoutParams);
		backgroundLayout.setGravity(Gravity.CENTER);
		backgroundLayout.setBackgroundColor(0xFF000000);
		backgroundLayout.getBackground().setAlpha(backgroundAlpha);
		backgroundLayout.addView(dialogView);		

        //创建WindowManager
    	WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.format = PixelFormat.RGBA_8888;//PixelFormat.TRANSLUCENT;
		//wlp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;
		//wlp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//不获得焦点
		wm.addView(backgroundLayout, wlp);
    }
    /**
     * 关闭
     *
     */
    public void dismiss(){
    	if(backgroundLayout == null)
    		return ;
    	WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.removeView(backgroundLayout);
		backgroundLayout = null;
    }
    /**
     * 设置title
     */
    public void setTitle(String title){
    	dialogView.setTitle(title);
    }
    public void setMessage(String message) {
    	dialogView.setMessage(message);
	}
    public void setNegativeButton(String negativeText, OnClickListener negativeListener){
    	dialogView.setNegativeButton(negativeText, negativeListener);
	}
    public void setPositiveButton(String positiveText, OnClickListener positiveListener){
    	dialogView.setPositiveButton(positiveText, positiveListener);
	}
	public void setPositiveListener(OnClickListener positiveListener) {
		dialogView.setPositiveListener(positiveListener);
	}
	public void setNegativeListener(OnClickListener negativeListener) {
		dialogView.setNegativeListener(negativeListener);
	}
	public void setTitleTextSize(int titleTextSize) {
		dialogView.setTitleTextSize(titleTextSize);
	}
	public void setMessageTextSize(int messageTextSize) {
		dialogView.setMessageTextSize(messageTextSize);
	}
	public void setButtonTextSize(int buttonTextSize) {
		dialogView.setButtonTextSize(buttonTextSize);
	}
	public void setTitleTextColor(int titleTextColor) {
		dialogView.setTitleTextColor(titleTextColor);
	}
	public void setMessageTextColor(int messageTextColor) {
		dialogView.setMessageTextColor(messageTextColor);
	}
	public void setNegativeNormalTextColor(int negativeNormalTextColor) {
		dialogView.setNegativeNormalTextColor(negativeNormalTextColor);
	}
	public void setNegativePressedTextColor(int negativePressedTextColor) {
		dialogView.setNegativePressedTextColor(negativePressedTextColor);
	}
	public void setPositiveNormalTextColor(int positiveNormalTextColor) {
		dialogView.setPositiveNormalTextColor(positiveNormalTextColor);
	}
	public void setPositivePressedTextColor(int positivePressedTextColor) {
		dialogView.setPositivePressedTextColor(positivePressedTextColor);
	}
	public void setNegativePressedBgColor(int negativePressedBgColor) {
		dialogView.setNegativePressedBgColor(negativePressedBgColor);
	}
	public void setPositivePressedBgColor(int positivePressedBgColor) {
		dialogView.setPositivePressedBgColor(positivePressedBgColor);
	}
	public void setCornerRadius(int cornerRadius) {
		dialogView.setCornerRadius(cornerRadius);
	}
	public void setSeparateLineWidth(int separateLineWidth) {
		dialogView.setSeparateLineWidth(separateLineWidth);
	}
	public void setMessagePadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom){
		dialogView.setMessagePadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}
	public int getMessagePaddingLeft() {
		return dialogView.getMessagePaddingLeft();
	}
	public void setMessagePaddingLeft(int messagePaddingLeft) {
		dialogView.setMessagePaddingLeft(messagePaddingLeft);
	}
	public int getMessagePaddingTop() {
		return dialogView.getMessagePaddingTop();
	}
	public void setMessagePaddingTop(int messagePaddingTop) {
		dialogView.setMessagePaddingTop(messagePaddingTop);
	}
	public int getMessagePaddingRight() {
		return dialogView.getMessagePaddingRight();
	}
	public void setMessagePaddingRight(int messagePaddingRight) {
		dialogView.setMessagePaddingRight(messagePaddingRight);
	}
	public int getMessagePaddingBottom() {
		return dialogView.getMessagePaddingBottom();
	}
	public void setMessagePaddingBottom(int messagePaddingBottom) {
		dialogView.setMessagePaddingBottom(messagePaddingBottom);
	}
    public void setBackgroundAlpha(int backgroundAlpha) {
		this.backgroundAlpha = backgroundAlpha;
	}
    
    /**
	 * 显示背景的容器
	 * @author pc	 *
	 */
	private class BackgroundLinearLayout extends LinearLayout {
		public BackgroundLinearLayout(Context context) {
            super(context);
        }
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
		}
		@Override
        public boolean dispatchKeyEvent(KeyEvent event) {
			if(event.getKeyCode() == KeyEvent.KEYCODE_BACK )
			    dismiss();
			return super.dispatchKeyEvent(event);
        }
		@Override
	    public boolean onTouchEvent(MotionEvent event) {    	
			if (event.getAction() == MotionEvent.ACTION_UP){
				dismiss();				
			}
			return true;
	    }
	}
}
