package com.miss.base.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对外提供MD5方法
 *
 * @author Hejinzhu
 */
public class MD5 {

    public static final int MD5_LENGTH_16 = 1;
    public static final int MD5_LENGTH_24 = 2;
    public static final int MD5_LENGTH_32 = 3;
    public static final int MD5_TYPE_NOMAL = 1;
    public static final int MD5_TYPE_FINGER = 2;
    public static final int MD5_TYPE_DEFAULT = 1;

    /**
     * 获取MD5字符串
     *
     * @param val 待加密的字符串
     * @return
     */
    public static String getMD5(String val) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        md5.update(val.getBytes());
        byte[] m = md5.digest();// 加密
        return getString(m);
    }

    private static String getString(byte[] b) {
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            i &= 0xff;
            String hex = Integer.toHexString(i);
            if (hex.length() == 1) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }

    /**
     * @param val       待加密字符串
     * @param leghtType 加密字符长度
     * @param md5TYPE   md5的类型可以为finger类型 A0:B3:B9:30:40:A3:D5:D5
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(byte[] val, int leghtType, int md5TYPE) throws NoSuchAlgorithmException {
        String result = "";
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val);
        byte[] b = md5.digest();// 加密
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            i &= 0xff;
            String hex = Integer.toHexString(i);
            if (offset != 0 && md5TYPE == MD5_TYPE_FINGER) {
                buf.append(":");
            }
            if (hex.length() == 1) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }
        switch (leghtType) {
            case MD5_LENGTH_16:
                result = buf.toString().substring(8, 24);
                break;
            case MD5_LENGTH_24:
                result = buf.toString().substring(0, 24);
                break;
            case MD5_LENGTH_32:
                result = buf.toString();
                break;
            default:
                result = buf.toString();
        }
        return result;
    }

    /**
     * 获取MD5字符串
     *
     * @param val 待加密的byte数组
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(byte[] val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val);
        byte[] m = md5.digest();// 加密
        return getString(m);
    }
}
