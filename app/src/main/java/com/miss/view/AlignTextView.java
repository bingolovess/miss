package com.miss.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AlignTextView extends TextView {
    private static final String TAG = AlignTextView.class.getSimpleName();
    /**
     * 绘制文字的起始Y坐标
     */
    private float mLineY;

    /**
     * 文字的宽度
     */
    private int mViewWidth;

    /**
     * 段落间距
     */
    private int paragraphSpacing = dipToPx(getContext(), 15);

    /**
     * 当前所有行数的集合
     */
    private ArrayList<List<List<String>>> mParagraphLineList;

    /**
     * 当前所有的行数
     */
    private int mLineCount;

    /**
     * 每一段单词的内容集合
     */
    private ArrayList<List<String>> mParagraphWordList;

    /**
     * 空格字符
     */
    private static final String BLANK = " ";

    /**
     * 英语单词元音字母
     */
    private String[] vowel = {"a", "e", "i", "o", "u"};

    /**
     * 英语单词元音字母集合
     */
    private List<String> vowels = Arrays.asList(vowel);

    public AlignTextView(Context context) {
        this(context, null);
    }

    public AlignTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlignTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }


    public AlignTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mParagraphLineList = null;
        mParagraphWordList = null;
        mLineY = 0;
        mViewWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        getParagraphList();
        for (List<String> frontList : mParagraphWordList) {
            mParagraphLineList.add(getLineList(frontList));
        }
        setMeasuredDimension(mViewWidth, (mParagraphLineList.size() - 1) * paragraphSpacing + mLineCount * getLineHeight());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        mLineY = 0;
        float textSize = getTextSize();
        mLineY += textSize + getPaddingTop();
        Layout layout = getLayout();
        if (layout == null) {
            return;
        }
        adjust(canvas, paint);
    }

    /**
     * @param frontList
     * @return 计算每一段绘制的内容
     */
    private synchronized List<List<String>> getLineList(List<String> frontList) {
        StringBuilder sb = new StringBuilder();
        List<List<String>> lineLists = new ArrayList<>();
        List<String> lineList = new ArrayList<>();
        float width = 0;
        String temp = "";
        String front = "";
        for (int i = 0; i < frontList.size(); i++) {
            front = frontList.get(i);
            if (!TextUtils.isEmpty(temp)) {
                sb.append(temp);
                lineList.add(temp);
                if (!isCN(temp)) {
                    sb.append(BLANK);
                }
                temp = "";
            }

            if (isCN(front)) {
                sb.append(front);
            } else {
                if ((i + 1) < frontList.size()) {
                    String nextFront = frontList.get(i + 1);
                    if (isCN(nextFront)) {
                        sb.append(front);
                    } else {
                        sb.append(front).append(BLANK);
                    }
                } else {
                    sb.append(front);
                }

            }

            lineList.add(front);
            width = StaticLayout.getDesiredWidth(sb.toString(), getPaint());
            if (width > mViewWidth) {
                // 先判断最后一个单词是否是英文的，是的话则切割，否则的话就移除最后一个
                int lastIndex = lineList.size() - 1;
                String lastWord = lineList.get(lastIndex);
                String lastTemp = "";
                lineList.remove(lastIndex);
                if (isCN(lastWord)) {
                    addLines(lineLists, lineList);
                    lastTemp = lastWord;
                } else {
                    // 否则的话则截取字符串
                    String substring = sb.substring(0, sb.length() - lastWord.length() - 1);
                    sb.delete(0, sb.toString().length());
                    sb.append(substring).append(BLANK);
                    String tempLastWord = "";
                    int length = lastWord.length();
                    if (length <= 3) {
                        addLines(lineLists, lineList);
                        lastTemp = lastWord;
                    } else {
                        int cutoffIndex = 0;
                        for (int j = 0; j < length; j++) {
                            tempLastWord = String.valueOf(lastWord.charAt(j));
                            sb.append(tempLastWord);
                            if (vowels.contains(tempLastWord)) {
                                // 根据元音字母来进行截断
                                if (j + 1 < length) {
                                    String nextTempLastWord = String.valueOf(lastWord.charAt(j + 1));
                                    sb.append(nextTempLastWord);
                                    width = StaticLayout.getDesiredWidth(sb.toString(), getPaint());
                                    cutoffIndex = j;
                                    if (width > mViewWidth) {
                                        if (j > 2 && j <= length - 2) {
                                            // 单词截断后，前面的字符小于2个时，则不进行截断
                                            String lastFinalWord = lastWord.substring(0, cutoffIndex + 2) + "-";
                                            lineList.add(lastFinalWord);
                                            addLines(lineLists, lineList);
                                            lastTemp = lastWord.substring(cutoffIndex + 2, length);
                                        } else {
                                            addLines(lineLists, lineList);
                                            lastTemp = lastWord;
                                        }
                                        break;
                                    }
                                } else {
                                    addLines(lineLists, lineList);
                                    lastTemp = lastWord;
                                    break;
                                }
                            }
                            width = StaticLayout.getDesiredWidth(sb.toString(), getPaint());
                            // 找不到元音，则走默认的逻辑
                            if (width > mViewWidth) {
                                if (j > 2 && j <= length - 2) {
                                    // 单词截断后，前面的字符小于2个时，则不进行截断
                                    String lastFinalWord = lastWord.substring(0, j) + "-";
                                    lineList.add(lastFinalWord);
                                    addLines(lineLists, lineList);
                                    lastTemp = lastWord.substring(j, length);
                                } else {
                                    addLines(lineLists, lineList);
                                    lastTemp = lastWord;
                                }
                                break;
                            }
                        }
                    }
                }
                sb.delete(0, sb.toString().length());
                temp = lastTemp;
            }
            if (lineList.size() > 0 && i == frontList.size() - 1) {
                addLines(lineLists, lineList);
            }
        }

        if (!TextUtils.isEmpty(temp)) {
            lineList.add(temp);
            addLines(lineLists, lineList);
        }

        mLineCount += lineLists.size();
        return lineLists;
    }

    /**
     * 添加一行到单词内容
     *
     * @param lineLists 总单词集合
     * @param lineList 当前要添加的集合
     */
    private void addLines(List<List<String>> lineLists, List<String> lineList) {
        if (lineLists == null || lineList == null) {
            return;
        }
        List<String> tempLines = new ArrayList<>(lineList);
        lineLists.add(tempLines);
        lineList.clear();
    }

    /**
     * 获取段落
     */

    private void getParagraphList() {
        String text = getText().toString().replaceAll("  ", "").replaceAll("   ", "").replaceAll("\\r", "").trim();
        mLineCount = 0;
        String[] items = text.split("\\n");
        mParagraphLineList = new ArrayList<>();
        mParagraphWordList = new ArrayList<>();
        for (String item : items) {
            if (item.length() != 0) {
                mParagraphWordList.add(getWordList(item));
            }
        }
    }

    /**
     * 截取每一段内容的每一个单词
     *
     * @param text
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private synchronized List<String> getWordList(String text) {
        if (TextUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        List<String> frontList = new ArrayList<>();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String charAt = String.valueOf(text.charAt(i));
            if (!Objects.equals(charAt, BLANK)) {
                if (checkIsSymbol(charAt)) {
                    boolean isEmptyStr = str.length() == 0;
                    str.append(charAt);
                    if (!isEmptyStr) {
                        // 中英文都需要将字符串添加到这里；
                        frontList.add(str.toString());
                        str.delete(0, str.length());
                    }
                } else {
                    if (isCN(str.toString())){
                        frontList.add(str.toString());
                        str.delete(0, str.length());
                    }
                    str.append(charAt);
                }
            } else {
                if (!TextUtils.isEmpty(str.toString())) {
                    frontList.add(str.toString().replaceAll(BLANK, ""));
                    str.delete(0, str.length());
                }
            }
        }

        if (str.length() != 0) {
            frontList.add(str.toString());
            str.delete(0, str.length());
        }

        return frontList;
    }


    /**
     * 中英文排版效果
     *
     * @param canvas
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private synchronized void adjust(Canvas canvas, TextPaint paint) {

        int size = mParagraphWordList.size();

        for (int j = 0; j < size; j++) { // 遍历每一段
            List<List<String>> lineList = mParagraphLineList.get(j);
            for (int i = 0; i < lineList.size(); i++) { // 遍历每一段的每一行
                List<String> lineWords = lineList.get(i);
                if (i == lineList.size() - 1) {
                    drawScaledEndText(canvas, lineWords, paint);
                } else {
                    drawScaledText(canvas, lineWords, paint);
                }
                mLineY += getLineHeight();
            }
            mLineY += paragraphSpacing;
        }
    }

    /**
     * 绘制最后一行文字
     *
     * @param canvas
     * @param lineWords
     * @param paint
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void drawScaledEndText(Canvas canvas, List<String> lineWords, TextPaint paint) {
        if (canvas == null || lineWords == null || paint == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String aSplit : lineWords) {
            if (isCN(aSplit)) {
                sb.append(aSplit);
            } else {
                sb.append(aSplit).append(BLANK);
            }
        }
        /**
         * 最后一行适配布局方向
         * android:gravity=""
         * android:textAlignment=""
         * 默认不设置则为左边
         * 如果同时设置gravity和textAlignment属性，则以textAlignment的属性为准
         * 也就是说textAlignment的属性优先级大于gravity的属性
         *
         */
        final int layoutDirection = getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(getGravity(), layoutDirection);
        int lastGravity = absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        int textAlignment = getTextAlignment();

        if (TEXT_ALIGNMENT_TEXT_START == textAlignment
                || TEXT_ALIGNMENT_VIEW_START == textAlignment
                || Gravity.LEFT == lastGravity) {
            canvas.drawText(sb.toString(), 0, mLineY, paint);
        } else if (TEXT_ALIGNMENT_TEXT_END == textAlignment
                || TEXT_ALIGNMENT_VIEW_END == textAlignment
                || Gravity.RIGHT == lastGravity) {
            float width = StaticLayout.getDesiredWidth(sb.toString(), getPaint());
            canvas.drawText(sb.toString(), mViewWidth - width, mLineY, paint);
        } else {
            float width = StaticLayout.getDesiredWidth(sb.toString(), getPaint());
            canvas.drawText(sb.toString(), (mViewWidth - width) / 2, mLineY, paint);
        }
    }

    /**
     * 绘制左右对齐效果
     *
     * @param canvas
     * @param line
     * @param paint
     */
    private void drawScaledText(Canvas canvas, List<String> line, TextPaint paint) {
        if (canvas == null || line == null || paint == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String aSplit : line) {
            sb.append(aSplit);
        }

        float lineWidth = StaticLayout.getDesiredWidth(sb, getPaint());
        float cw = 0;
        float d = (mViewWidth - lineWidth) / (line.size() - 1);
        for (String aSplit : line) {
            canvas.drawText(aSplit, cw, mLineY, getPaint());
            cw += StaticLayout.getDesiredWidth(aSplit + "", paint) + d;
        }
    }

    /**
     * 功能：判断字符串是否有中文
     *
     * @param str
     * @return
     */
    public boolean isCN(String str) {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            if (bytes.length == str.length()) {
                return false;
            } else {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static int dipToPx(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int) (var1 * var2 + 0.5F);
    }

    /**
     * 判断是否包含标点符号等内容
     *
     * @param s
     * @return
     */
    public boolean checkIsSymbol(String s) {
        boolean b = false;

        String tmp = s;
        tmp = tmp.replaceAll("\\p{P}", "");
        if (s.length() != tmp.length()) {
            b = true;
        }

        return b;
    }
}