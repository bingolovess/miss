package com.miss.base.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     * 空判断
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        if(str == null||str.length() == 0||"\'\'".equals(str)||"null".equalsIgnoreCase(str)){
            return true;
        }
        return false;
    }

    /**
     * join方法
     * @param delimiter
     * @param objects
     * @return 字符串
     */
    public static String join(@NonNull CharSequence delimiter, Object[] objects){
       return TextUtils.join(delimiter,objects);
    }
    public static String join(@NonNull CharSequence delimiter, List list){
        return TextUtils.join(delimiter,list);
    }

    /**
     * 字符串首字母大写
     * @param s
     * @return
     */
    public static String upperFirstLetter(final String s) {
        if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * 首字母小写
     * @param s
     * @return
     */
    public static String lowerFirstLetter(final String s) {
        if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }
    /**
     * 判断字符串是否纯数字
     * @param str
     * @return
     * 使用正则，（推荐）
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
    public static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim())){
            return s.matches("^[0-9]*$");
        } else {
            return false;
        }
    }

    /**
     * 判断字符串是否纯字母
     * @param str
     * @return
     */
    public boolean isLetter(String str){
        Pattern pattern = Pattern.compile("[a-zA-Z] + ");
        Matcher isLetter = pattern.matcher(str);
        if( !isLetter.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否手机号码
     * @param mobiles
     * @return
     */
    public boolean isMobilePhone(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断提取字符串中手机号码
     * @param num
     * @return
     */
    private static String getMobilePhoneNum(String num){
        if(num == null || num.length() == 0){return "";}
        Pattern pattern = Pattern.compile("(?<!\\d)(?:(?:1[358]\\d{9})|(?:861[358]\\d{9}))(?!\\d)");
        Matcher matcher = pattern.matcher(num);
        StringBuffer bf = new StringBuffer(64);
        while (matcher.find()) {
            bf.append(matcher.group()).append(",");
        }
        int len = bf.length();
        if (len > 0) {
            bf.deleteCharAt(len - 1);
        }
        return bf.toString();
    }

    /**
     * 手机号码隐藏中间四位
     * @param phone
     * @return
     */
    public static String getSafePhone(String phone){
        if(phone == null || phone.length() == 0) return "";
        String phoneNumber = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
        return phoneNumber;
    }

    /**
     * 银行卡号隐藏中间八位
     * @param bankCard
     * @return
     */
    public static String getSafeCardNum(String bankCard){
        if(bankCard == null || bankCard.length() == 0) return "";
        int hideLength = 8;//替换位数
        int sIndex = bankCard.length()/2 - hideLength/2;
        String replaceSymbol = "*";
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i<bankCard.length();i++){
            char number = bankCard.charAt(i);
            if (i >= sIndex-1 && i<sIndex+hideLength){
                sBuilder.append(replaceSymbol);
            }else {
                sBuilder.append(number);
            }
        }
        return sBuilder.toString();
    }

    /**
     * 默认保留两位小数 的字符串
     * @param number
     * @return
     */
    public static String getNumber(double number){
       return getNumberFormat(number,2);
    }
    public static String getNumber(float number){
        return getNumberFormat(number,2);
    }
    /**
     * 保留小数位数
     * @param number
     * @param unit
     * @return
     */
    public static String getNumberFormat(double number,int unit){
        return String.format(Locale.CHINA ,"%."+ unit +"f", number);
    }
    /**
     * 保留小数位数
     * @param number
     * @param unit
     * @return
     */
    public static String getNumberFormat(float number,int unit){
        return String.format(Locale.CHINA ,"%."+ unit +"f", number);
    }
}
