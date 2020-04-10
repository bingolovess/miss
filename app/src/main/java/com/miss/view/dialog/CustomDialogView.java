package com.miss.view.dialog;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.miss.base.utils.LogUtils;
@Deprecated
public class CustomDialogView extends View implements OnGestureListener{
	private Paint paint = new Paint();
	private RectF rectF = new RectF();
	private GestureDetector mGestureDetector;
    //属性
	private String title;//标题
	private String message;//信息
	//确定按钮
	private boolean havePositive;//是否有确认按钮
	private String positiveText;//按钮上的文字
	private OnClickListener positiveListener;//监听器
	//取消按钮
	private boolean haveNegative;//是否有取消按钮
	private String negativeText;//按钮上的文字
	private OnClickListener negativeListener;//监听器 
	//字体大小
	private int titleTextSize = dip2px(this.getContext(), 16);
	private int messageTextSize = dip2px(this.getContext(), 15);
	private int buttonTextSize = dip2px(this.getContext(), 16);
	//字体颜色
	private int titleTextColor = 0xFF343434;
	private int messageTextColor = 0xFF686979;
	private int negativeNormalTextColor = 0xFF9192A0;	
	private int negativePressedTextColor = 0xFF9192A0;
	private int positiveNormalTextColor = 0xFF9192A0;	
	private int positivePressedTextColor = 0xFF9192A0;
	//按钮按下时的背景颜色
	private int negativePressedBgColor = 0xFFF2F2F2;
	private int positivePressedBgColor = 0xFFF2F2F2;
	//message的padding
	private int messagePaddingLeft = dip2px(this.getContext(), 15);	
	private int messagePaddingTop = dip2px(this.getContext(), 20);
	private int messagePaddingRight = dip2px(this.getContext(), 15);
	private int messagePaddingBottom = dip2px(this.getContext(), 20);
	private int lineWidth = 0;
	//其他属性
	private int cornerRadius = dip2px(this.getContext(), 5);//圆角半径
	private int separateLineWidth = createDefaultSeparateLineWidth();//分隔线宽度
	private int lineSpacing = dip2px(this.getContext(), 5);//行间距
	//运行时参数
	private int negativeCurrentTextColor = negativeNormalTextColor;
	private int positiveCurrentTextColor = positiveNormalTextColor;
	private int negativeCurrentBgColor = 0xFFFFFFFF;
	private int positiveCurrentBgColor = 0xFFFFFFFF;
	
	public CustomDialogView(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(this);
	}
	/**
	 * 测绘
	 */
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = measureHeight();
		LogUtils.getInstance().e("height = "+height);
		setMeasuredDimension(measureWidth(), height);
    }	
	/**
     * 计算组件宽度
     */
    private int measureWidth() {
		lineWidth = dip2px(this.getContext(), 310);
        return lineWidth;
    }
    /**
     * 计算组件高度
     */
    private int measureHeight() {
        return getTitleLayoutHeight() + getMessageLayoutHeight() + getButtonLayoutHeight();
    }
    /**
     * 计算title栏的高度
     */
    private int getTitleLayoutHeight(){
    	return dip2px(this.getContext(), 50);
    }
    /**
     * 计算按钮栏的高度
     */
    private int getButtonLayoutHeight(){
    	return dip2px(this.getContext(), 50);
    }
    /**
     * 计算message栏的高度
     */
    private int getMessageLayoutHeight(){
    	int height = 0;
    	String message = this.message == null ? "" : this.message.trim();
    	paint.setTextSize(this.messageTextSize);
    	FontMetricsInt fontMetrics = paint.getFontMetricsInt();
    	int txtHeight = fontMetrics.bottom - fontMetrics.ascent;
    	
    	List<String> lines = splitMessage(this.message, this.lineWidth - this.messagePaddingLeft - this.messagePaddingRight);
    	int lineCount = lines == null ? 0 : lines.size();//行数
    	int lineSpacingTotal = lineCount == 0 ? 0 : (lineCount-1)*lineSpacing;//总的行间距
		LogUtils.getInstance().e("lineCount= "+ lineCount);
    	height = this.messagePaddingTop + (txtHeight * lineCount) + lineSpacingTotal + this.messagePaddingBottom;
    	LogUtils.getInstance().e("messageHeight = "+height);
    	return height ;
    }
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//画组件轮廓
		drawBackground(canvas);
		drawTitle(canvas);
		drawMessage(canvas);
		drawButtons(canvas);
	}
	/**
	 * 画圆角背景
	 */
	private void drawBackground(Canvas canvas){
		rectF.left = 0;
		rectF.top = 0;
		rectF.right = this.getWidth();
		rectF.bottom = this.getHeight();
		paint.setStyle(Paint.Style.FILL);    
		paint.setColor(0xFFFFFFFF);
		canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
	}
	/**
	 * 画title
	 */
	private void drawTitle(Canvas canvas){
		paint.setColor(this.titleTextColor);
		paint.setTextSize(this.titleTextSize);
		String title = this.title == null ? "" : this.title.trim();
		int txtWidth = (int)this.paint.measureText(title);
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
    	int txtHeight = fontMetrics.bottom - fontMetrics.ascent;
		canvas.drawText(title, this.getWidth()/2-txtWidth/2, (getTitleLayoutHeight()-txtHeight)/2-fontMetrics.ascent, paint);	
	}
	/**
	 * 画message
	 */
	private void drawMessage(Canvas canvas){
		List<String> lines = splitMessage(this.message, this.lineWidth - this.messagePaddingLeft - this.messagePaddingRight);
    	int lineCount = lines == null ? 0 : lines.size();//行数
    	if(lineCount == 0)
    		return ;
    	paint.setColor(this.messageTextColor);
		paint.setTextSize(this.messageTextSize);
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		int txtHeight = fontMetrics.bottom - fontMetrics.ascent;
    	int top = this.getTitleLayoutHeight() + this.messagePaddingTop;
    	for(String line : lines){
    		canvas.drawText(line, this.messagePaddingLeft, top -fontMetrics.ascent, paint);
    		top += txtHeight;
    		top += lineSpacing;
    	}
	}
	/**
	 * 画按钮
	 */
	private void drawButtons(Canvas canvas){
		//画背景，用于显示分隔线
		RectF rectF = createButtonRectF();
		paint.setStyle(Paint.Style.FILL);    
		paint.setColor(0xFFDADADA);		
		Path path = new Path();
		float[] radii={0f,0f,0f,0f,cornerRadius,cornerRadius,cornerRadius,cornerRadius};
		path.addRoundRect(rectF, radii, Path.Direction.CW);
		canvas.drawPath(path,paint);
		
		//画取消按钮
		drawNegativeButtons(canvas);
		//画确认按钮
		drawPositiveButtons(canvas);
	}
	/**
	 * 画取消按钮
	 */
	private void drawNegativeButtons(Canvas canvas){
		RectF rectF = createNegativeRectF();
		if(rectF == null)
			return ;
		paint.setColor(negativeCurrentBgColor);
		Path path = new Path();
		float[] radii = new float[]{0f,0f,0f,0f,0f,0f,cornerRadius,cornerRadius};
		path.addRoundRect(rectF, radii, Path.Direction.CW);
		canvas.drawPath(path, paint);
		paint.setColor(negativeCurrentTextColor);
		paint.setTextSize(this.buttonTextSize);
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		String negativeText = this.negativeText == null ? "" : this.negativeText.trim();
		int txtWidth = (int)this.paint.measureText(negativeText);
		int txtHeight = fontMetrics.bottom - fontMetrics.ascent;
		canvas.drawText(negativeText, rectF.width()/2-txtWidth/2, rectF.top + (rectF.height()-txtHeight)/2-fontMetrics.ascent, paint);	
	}
	/**
	 * 画确定按钮
	 */
	private void drawPositiveButtons(Canvas canvas){
		paint.setColor(positiveCurrentBgColor);
        RectF rectF = createPositiveRectF();
        Path path = new Path();
        float[] radii = new float[]{0f,0f,0f,0f,cornerRadius,cornerRadius,0f,0f};
		path.addRoundRect(rectF, radii, Path.Direction.CW);
		canvas.drawPath(path, paint);
		paint.setColor(positiveCurrentTextColor);
		paint.setTextSize(this.buttonTextSize);
		String positiveText = this.positiveText == null ? "" : this.positiveText.trim();
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		int txtWidth = (int)this.paint.measureText(positiveText);
		int txtHeight = fontMetrics.bottom - fontMetrics.ascent;
		canvas.drawText(positiveText, rectF.left + (rectF.width()/2-txtWidth/2), rectF.top + (rectF.height()-txtHeight)/2-fontMetrics.ascent, paint);	
	}
	/**
	 * 生成button的RectF
	 */
	private RectF createButtonRectF(){
		int top = this.getHeight() - this.getButtonLayoutHeight();
		rectF.left = 0;
		rectF.top = top;
		rectF.right = this.getWidth();
		rectF.bottom = this.getHeight();
		return rectF;
	}
	/**
	 * 生成取消按钮的RectF
	 */
	private RectF createNegativeRectF(){
		if(!this.haveNegative)//未设置取消
			return null;
		int right = (this.getWidth()-separateLineWidth)/2;
		if(!this.havePositive)//没有确认按钮
			right = this.getWidth();
		int top = this.getHeight() - this.getButtonLayoutHeight() + separateLineWidth;
		rectF.left = 0;
		rectF.top = top;
		rectF.right = right;
		rectF.bottom = this.getHeight();
		return rectF;
	}
	/**
	 * 生成确认按钮的RectF
	 */
	private RectF createPositiveRectF(){
		if(!this.havePositive)//未设置确认按钮
			return null;
		int left = (this.getWidth()-separateLineWidth)/2 + separateLineWidth;
		if(!this.haveNegative)
			left = 0;
		int top = this.getHeight() - this.getButtonLayoutHeight() + separateLineWidth;
		rectF.left = left;
		rectF.top = top;
		rectF.right = this.getWidth();
		rectF.bottom = this.getHeight();
		return rectF;
	}
	/**
	 * 将message转为可以直接显示的一行
	 * lineWidth : 每行的宽度
	 */
	private List<String> splitMessage(String text, int lineWidth){
		if(text == null || text.trim().equals(""))
			return null;
		paint.setTextSize(this.messageTextSize);
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		List<String> lines = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i<text.length(); i++){
			String ch = text.substring(i, i+1);
			int txtWidth = (int)paint.measureText(sb.toString()+ch);
			if(txtWidth <= lineWidth){
				sb.append(ch);
				continue;
			}
			lines.add(sb.toString());
			sb = new StringBuffer();
			sb.append(ch);
		}
		if(sb.length() != 0)
			lines.add(sb.toString());
		return lines;
	}
	/**
	 * 画
	 */
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
    /** 
     * 从 sp 的单位 转成为 px(像素) 
     */  
    public static int sp2px(Context context, float spValue) {  
    	final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f); 
    }  
    /**
     * 创建默认的分隔线先宽度
     */
    private int createDefaultSeparateLineWidth(){
    	WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
    	int screenWidth = wm.getDefaultDisplay().getWidth();
    	DisplayMetrics metric = new DisplayMetrics();
    	wm.getDefaultDisplay().getMetrics(metric);
    	int densityDpi = metric.densityDpi;
    	
    	if(screenWidth >= 1400){//1440
    		return dip2px(this.getContext(), 1);
    	}
    	if(screenWidth >= 1000){//1080
    		if(densityDpi >=480)
        		return 3;
        	if(densityDpi >= 320)
        		return 2;
        	return 2;
    	}
    	if(screenWidth >= 700){//720
        	if(densityDpi >= 320)
        		return 2;
        	if(densityDpi >= 240)
        		return 2;
        	return 1;
    	}
    	return 1;
    }
    /**
	 * 触碰事件
	 */
	@Override
    public boolean onTouchEvent(MotionEvent event) {    	
		try {
    		mGestureDetector.onTouchEvent(event);
    		if(event.getAction() == MotionEvent.ACTION_UP){
    			upEventHandler((int)event.getX(), (int)event.getY());
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
    }
	//手势监听
	@Override
	public boolean onDown(MotionEvent event) {
		downEventHandler((int)event.getX(), (int)event.getY());
		return true;
	}
	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2, float arg2, float arg3) {
		return false;
	}
	@Override
	public void onLongPress(MotionEvent event) {		
	}
	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float arg2, float arg3) {
		return false;
	}
	@Override
	public void onShowPress(MotionEvent event) {
	}
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		clickEventHandler((int)event.getX(), (int)event.getY());
		return true;
	}		
	/**
	 * 按下事件
	 */
	private void downEventHandler(int x, int y){
		RectF rectF = createNegativeRectF();
		if(rectF != null){
			if(x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom){
				this.negativeCurrentTextColor = this.negativePressedTextColor;
				this.negativeCurrentBgColor = this.negativePressedBgColor;
			}
		}
		rectF = createPositiveRectF();
		if(rectF != null){
			if(x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom){
				this.positiveCurrentTextColor = this.positivePressedTextColor;
				this.positiveCurrentBgColor = this.positivePressedBgColor;
			}
		}
		this.invalidate();
	}
	/**
	 * 抬起事件
	 */
	private void upEventHandler(int x, int y){
		this.negativeCurrentTextColor = this.negativeNormalTextColor;
		this.negativeCurrentBgColor = 0xFFFFFFFF;
		
		this.positiveCurrentTextColor = this.positiveNormalTextColor;
		this.positiveCurrentBgColor = 0xFFFFFFFF;
		this.invalidate();
	}
	/**
	 * 单击事件
	 */
	private void clickEventHandler(int x, int y){
		RectF rectF = createNegativeRectF();
		if(rectF != null){
			if(x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom){
				if(this.negativeListener != null){
					this.negativeListener.onClick(this);
					return ;
				}
			}
		}
		rectF = createPositiveRectF();
		if(rectF != null){
			if(x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom){
				if(this.positiveListener != null){
					this.positiveListener.onClick(this);
					return ;
				}
			}
		}
	}
	

	public void setTitle(String title) {
		this.title = title;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setNegativeButton(String negativeText, OnClickListener negativeListener){
		this.haveNegative = true;
		this.negativeText = negativeText;
		this.negativeListener = negativeListener;
	}
	public void setPositiveButton(String positiveText, OnClickListener positiveListener){
		this.havePositive = true;
		this.positiveText = positiveText;
		this.positiveListener = positiveListener;
	}

	public void setPositiveListener(OnClickListener positiveListener) {
		this.positiveListener = positiveListener;
	}
	public void setNegativeListener(OnClickListener negativeListener) {
		this.negativeListener = negativeListener;
	}
	public void setTitleTextSize(int titleTextSize) {
		this.titleTextSize = titleTextSize;
	}
	public void setMessageTextSize(int messageTextSize) {
		this.messageTextSize = messageTextSize;
	}
	public void setButtonTextSize(int buttonTextSize) {
		this.buttonTextSize = buttonTextSize;
	}
	public void setTitleTextColor(int titleTextColor) {
		this.titleTextColor = titleTextColor;
	}
	public void setMessageTextColor(int messageTextColor) {
		this.messageTextColor = messageTextColor;
	}
	public void setNegativeNormalTextColor(int negativeNormalTextColor) {
		this.negativeNormalTextColor = negativeNormalTextColor;
	}
	public void setNegativePressedTextColor(int negativePressedTextColor) {
		this.negativePressedTextColor = negativePressedTextColor;
	}
	public void setPositiveNormalTextColor(int positiveNormalTextColor) {
		this.positiveNormalTextColor = positiveNormalTextColor;
		this.positiveCurrentTextColor = this.positiveNormalTextColor;
	}
	public void setPositivePressedTextColor(int positivePressedTextColor) {
		this.positivePressedTextColor = positivePressedTextColor;
	}
	public void setNegativePressedBgColor(int negativePressedBgColor) {
		this.negativePressedBgColor = negativePressedBgColor;
	}
	public void setPositivePressedBgColor(int positivePressedBgColor) {
		this.positivePressedBgColor = positivePressedBgColor;
	}
	public void setCornerRadius(int cornerRadius) {
		this.cornerRadius = cornerRadius;
	}
	public void setSeparateLineWidth(int separateLineWidth) {
		this.separateLineWidth = separateLineWidth;
	}
	public void setMessagePadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom){
		this.messagePaddingLeft = paddingLeft;
		this.messagePaddingTop = paddingTop;
		this.messagePaddingRight = paddingRight;
		this.messagePaddingBottom = paddingBottom;
	}
	public int getMessagePaddingLeft() {
		return messagePaddingLeft;
	}
	public void setMessagePaddingLeft(int messagePaddingLeft) {
		this.messagePaddingLeft = messagePaddingLeft;
	}
	public int getMessagePaddingTop() {
		return messagePaddingTop;
	}
	public void setMessagePaddingTop(int messagePaddingTop) {
		this.messagePaddingTop = messagePaddingTop;
	}
	public int getMessagePaddingRight() {
		return messagePaddingRight;
	}
	public void setMessagePaddingRight(int messagePaddingRight) {
		this.messagePaddingRight = messagePaddingRight;
	}
	public int getMessagePaddingBottom() {
		return messagePaddingBottom;
	}
	public void setMessagePaddingBottom(int messagePaddingBottom) {
		this.messagePaddingBottom = messagePaddingBottom;
	}
}
