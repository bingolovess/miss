package com.miss.base.utils;

import android.text.TextUtils;

import com.miss.base.enums.FileSizeEnum;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * 计算文件大小的工具类,视频文件大小等
 */
public class FileSizeUtil {
    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeEnum 获取大小的类型 FileSizeEnum (1为B、2为KB、3为MB、4为GB)
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, FileSizeEnum sizeEnum) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize, sizeEnum);
    }
    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize);
    }
    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }
    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }
    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    public static double FormetFileSize(long fileS, FileSizeEnum sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }


    /**
     *  校验 格式是否异常
     *  教训：切记DecimalFormat是和语言环境有关
     *  <p>
     *      在开发中遇到在系统语言是英语和汉语时DecimalFormat没有问题，
     *      但是在西班牙语时出现了java.lang.NumberFormatException: Invalid float: "19,980"，
     *      查看原始数据发现在系统语言为西班牙语下小数点变成了逗号
     *  </p>
     * @return
     */
    public static String checkDecimalFormat(String num){
        if (!TextUtils.isEmpty(num)){
            num = num.replace(",",".");
        }
        return num;
    }
}
