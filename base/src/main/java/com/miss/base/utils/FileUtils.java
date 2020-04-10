package com.miss.base.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.content.ContentValues.TAG;

public class FileUtils {
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteFile(String filename) {
        return new File(filename).delete();
    }

    public static void deleteFileByDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }
    }
    /**
     * 判断SD卡上的文件是否存在
     */
    public static boolean isFileExist(String filePath) {
        return new File(filePath).exists();
    }

    public static boolean writeFile(String filename, String content, boolean append) {
        boolean isSuccess = false;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(filename, append));
            bufferedWriter.write(content);
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(bufferedWriter);
        }
        return isSuccess;
    }

    public static String readFile(String filename) {
        File file = new File(filename);
        BufferedReader bufferedReader = null;
        String str = null;
        try {
            if (file.exists()) {
                bufferedReader = new BufferedReader(new FileReader(filename));
                str = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(bufferedReader);
        }
        return str;
    }

    public static StringBuilder readFile(File file, String charsetName) {
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeIO(reader);
        }
    }

    public static void copyFile(InputStream in, OutputStream out) {
        try {
            byte[] b = new byte[2 * 1024 * 1024]; //2M memory
            int len = -1;
            while ((len = in.read(b)) > 0) {
                out.write(b, 0, len);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(in, out);
        }
    }

    public static void copyFileFast(File in, File out) {
        FileChannel filein = null;
        FileChannel fileout = null;
        try {
            filein = new FileInputStream(in).getChannel();
            fileout = new FileOutputStream(out).getChannel();
            filein.transferTo(0, filein.size(), fileout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(filein, fileout);
        }
    }

    public static void shareFile(Context context, String title, String filePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse("file://" + filePath);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, title));
    }

    public static void zip(InputStream is, OutputStream os) {
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(os);
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                gzip.write(buf, 0, len);
                gzip.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is, gzip);
        }
    }

    public static void unzip(InputStream is, OutputStream os) {
        GZIPInputStream gzip = null;
        try {
            gzip = new GZIPInputStream(is);
            byte[] buf = new byte[1024];
            int len;
            while ((len = gzip.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(gzip, os);
        }
    }

    public static String formatFileSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    public static void Stream2File(InputStream is, File file) {
        byte[] b = new byte[1024];
        int len;
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is, os);
        }
    }

    public static boolean createFolder(String filePath) {
        return createFolder(filePath, false);
    }

    public static boolean createFolder(String filePath, boolean recreate) {
        String folderName = getFolderName(filePath);
        if (folderName == null || folderName.length() == 0 || folderName.trim().length() == 0) {
            return false;
        }
        File folder = new File(folderName);
        if (folder.exists()) {
            if (recreate) {
                deleteFile(folderName);
                return folder.mkdirs();
            } else {
                return true;
            }
        } else {
            return folder.mkdirs();
        }
    }

    public static String getFileName(String filePath) {
        if (StringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    public static long getFileSize(String filepath) {
        if (TextUtils.isEmpty(filepath)) {
            return -1;
        }
        File file = new File(filepath);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    public static boolean rename(String filepath, String newName) {
        File file = new File(filepath);
        return file.exists() && file.renameTo(new File(newName));
    }

    public static String getFolderName(String filePath) {
        if (filePath == null || filePath.length() == 0 || filePath.trim().length() == 0) {
            return filePath;
        }
        int filePos = filePath.lastIndexOf(File.separator);
        return (filePos == -1) ? "" : filePath.substring(0, filePos);
    }

    public static ArrayList<File> getFilesArray(String path) {
        File file = new File(path);
        File files[] = file.listFiles();
        ArrayList<File> listFile = new ArrayList<File>();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    listFile.add(files[i]);
                }
                if (files[i].isDirectory()) {
                    listFile.addAll(getFilesArray(files[i].toString()));
                }
            }
        }
        return listFile;
    }

    public static boolean deleteFiles(String folder) {
        if (folder == null || folder.length() == 0 || folder.trim().length() == 0) {
            return true;
        }
        File file = new File(folder);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    public static void openImage(Context mContext, String imagePath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(imagePath));
        intent.setDataAndType(uri, "image/*");
        mContext.startActivity(intent);
    }

    public static void openVideo(Context mContext, String videoPath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(videoPath));
        intent.setDataAndType(uri, "video/*");
        mContext.startActivity(intent);
    }

    public static void openURL(Context mContext, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(intent);
    }

    public static void downloadFile(Context context, String fileurl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileurl));
        request.setDestinationInExternalPublicDir("/Download/", fileurl.substring(fileurl.lastIndexOf("/") + 1));
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }


    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getAppExternalPath(Context context) {
/*        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append("Android/data/");
        sb.append(packageName);
        return sb.toString();*/
        return context.getObbDir().getAbsolutePath();
    }

    @Deprecated
    public static String getExtraPath(String folder) {
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folder;
        File file = new File(storagePath);
        if (!file.exists()) {
            file.mkdir();
        }
        return storagePath;
    }


    /**
     * 判断文件MimeType 类型
     *
     * @param f
     * @return
     */
    public static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if (end.equalsIgnoreCase("m4a") || end.equalsIgnoreCase("mp3") || end.equalsIgnoreCase("mid") || end.equalsIgnoreCase("xmf")
                || end.equalsIgnoreCase("ogg") || end.equalsIgnoreCase("wav")) {
            type = "audio";
        } else if (end.equalsIgnoreCase("3gp") || end.equalsIgnoreCase("mp4")) {
            type = "video";
        } else if (end.equalsIgnoreCase("jpg") || end.equalsIgnoreCase("gif") || end.equalsIgnoreCase("png")
                || end.equalsIgnoreCase("jpeg") || end.equalsIgnoreCase("bmp")) {
            type = "image";
        } else if (end.equalsIgnoreCase("apk")) {
            /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        } else if (end.equalsIgnoreCase("txt") || end.equalsIgnoreCase("java")) {
            /* android.permission.INSTALL_PACKAGES */
            type = "text";
        } else {
            type = "*";
        }
        /* 如果无法直接打开，就跳出软件列表给用户选择 */
        if (end.equalsIgnoreCase("apk")) {
        } else {
            type += "/*";
        }
        return type;
    }
    /**
     * 得到所有文件
     *
     * @param dir
     * @return
     */
    public static ArrayList<File> getAllFiles(File dir) {
        ArrayList<File> allFiles = new ArrayList<File>();
        // 递归取得目录下的所有文件及文件夹
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            allFiles.add(file);
            if (file.isDirectory()) {
                getAllFiles(file);
            }
        }
        return allFiles;
    }
    /**
     * 改变文件大小显示的内容
     *
     * @param size
     * @return
     */
    public static String changeFileSize(String size) {
        if (Integer.parseInt(size) > 1024) {
            size = Integer.parseInt(size) / 1024 + "K";
        } else if (Integer.parseInt(size) > (1024 * 1024)) {
            size = Integer.parseInt(size) / (1024 * 1024) + "M";
        } else if (Integer.parseInt(size) > (1024 * 1024 * 1024)) {
            size = Integer.parseInt(size) / (1024 * 1024 * 1024) + "G";
        } else {
            size += "B";
        }
        return size;
    }
    /**
     * 获取文件目录下文件数量
     * @param path
     * @return
     */
    public static int getFileNums(String path){
        try {
            File file = new File(path);
            return file.listFiles().length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 递归求取目录文件个数
     *
     * @param f
     * @return
     */
    public static long getFileNums2(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        size = flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileNums2(flist[i]);
                size--;
            }
        }
        return size;
    }
    /**
     * 在根目录下搜索文件
     *
     * @param keyword
     * @return
     */
    public static String searchFile(String keyword) {
        String result = "";
        File[] files = new File("/").listFiles();
        for (File file : files) {
            if (file.getName().indexOf(keyword) >= 0) {
                result += file.getPath() + "\n";
            }
        }
        if (result.equals("")) {
            result = "找不到文件!!";
        }
        return result;
    }
    /**
     * @detail 搜索sdcard文件
     * @param file 需要进行文件搜索的目录
     * @param ext 过滤搜索文件类型
     * */
    public static List<String> search(File file, String[] ext) {
        List<String> list = new ArrayList<String>();
        if (file != null) {
            if (file.isDirectory()) {
                File[] listFile = file.listFiles();
                if (listFile != null) {
                    for (int i = 0; i < listFile.length; i++) {
                        search(listFile[i], ext);
                    }
                }
            } else {
                String filename = file.getAbsolutePath();
                for (int i = 0; i < ext.length; i++) {
                    if (filename.endsWith(ext[i])) {
                        list.add(filename);
                        break;
                    }
                }
            }
        }
        return list;
    }
    /**
     * 查询文件
     *
     * @param file
     * @param keyword
     * @return
     */
    public static List<File> FindFile(File file, String keyword) {
        List<File> list = new ArrayList<File>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File tempf : files) {
                    if (tempf.isDirectory()) {
                        if (tempf.getName().toLowerCase().lastIndexOf(keyword) > -1) {
                            list.add(tempf);
                        }
                        list.addAll(FindFile(tempf, keyword));
                    } else {
                        if (tempf.getName().toLowerCase().lastIndexOf(keyword) > -1) {
                            list.add(tempf);
                        }
                    }
                }
            }
        }
        return list;
    }
    /**
     * searchFile 查找文件并加入到ArrayList 当中去
     *
     * @param context
     * @param keyword
     * @param filepath
     * @return
     */
    public static List<Map<String, Object>> searchFile(Context context, String keyword, File filepath) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> rowItem = null;
        int index = 0;
        // 判断SD卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File[] files = filepath.listFiles();
            if (files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        if (file.getName().toLowerCase().lastIndexOf(keyword) > -1) {
                            rowItem = new HashMap<String, Object>();
                            rowItem.put("number", index); // 加入序列号
                            rowItem.put("fileName", file.getName());// 加入名称
                            rowItem.put("path", file.getPath()); // 加入路径
                            rowItem.put("size", file.length() + ""); // 加入文件大小
                            list.add(rowItem);
                        }
                        // 如果目录可读就执行（一定要加，不然会挂掉）
                        if (file.canRead()) {
                            list.addAll(searchFile(context, keyword, file)); // 如果是目录，递归查找
                        }
                    } else {
                        // 判断是文件，则进行文件名判断
                        try {
                            if (file.getName().indexOf(keyword) > -1 || file.getName().indexOf(keyword.toUpperCase()) > -1) {
                                rowItem = new HashMap<String, Object>();
                                rowItem.put("number", index); // 加入序列号
                                rowItem.put("fileName", file.getName());// 加入名称
                                rowItem.put("path", file.getPath()); // 加入路径
                                rowItem.put("size", file.length() + ""); // 加入文件大小
                                list.add(rowItem);
                                index++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Toast.makeText(context, "查找发生错误!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        return list;
    }
    /**
     * 从sd卡取文件
     *
     * @param filename
     * @return
     */
    public String getFileFromSdcard(String filename) {
        ByteArrayOutputStream outputStream = null;
        FileInputStream fis = null;
        try {
            outputStream = new ByteArrayOutputStream();
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                fis = new FileInputStream(file);
                int len = 0;
                byte[] data = new byte[1024];
                while ((len = fis.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
                fis.close();
            } catch (IOException e) {
            }
        }
        return new String(outputStream.toByteArray());
    }
    /**
     * 保存文件到sd
     *
     * @param filename
     * @param content
     * @return
     */
    public static boolean saveContentToSdcard(String filename, String content) {
        boolean flag = false;
        FileOutputStream fos = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                fos = new FileOutputStream(file);
                fos.write(content.getBytes());
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
            }
        }
        return flag;
    }
    /**
     * 取得文件大小
     *
     * @param f
     * @return
     * @throws
     */
    @SuppressWarnings("resource")
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            size = fis.available();
        } else {
            f.createNewFile();
        }
        return size;
    }
    /**
     * 递归取得文件夹大小
     *
     * @param dir
     * @return
     * @throws
     */
    public static long getFileSize(File dir) throws Exception {
        long size = 0;
        File flist[] = dir.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
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
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
    /**
     * 解压zip到指定的路径
     *
     * @param zipFileString ZIP的名称 压缩文件的路径
     * @param outPathString 要解压缩路径
     * @throws
     */
    public static void unZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                //Log.e(TAG, outPathString + File.separator + szName);
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    Log.e(TAG, "Create the file:" + outPathString + File.separator + szName);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }
    /**
     * 根据后缀得到文件类型
     *
     * @param fileName
     * @param pointIndex
     * @return
     */
    public static String getFileType(String fileName, int pointIndex) {
        String type = fileName.substring(pointIndex + 1).toLowerCase();
        if ("m4a".equalsIgnoreCase(type) || "xmf".equalsIgnoreCase(type) || "ogg".equalsIgnoreCase(type) || "wav".equalsIgnoreCase(type)
                || "m4a".equalsIgnoreCase(type) || "aiff".equalsIgnoreCase(type) || "midi".equalsIgnoreCase(type)
                || "vqf".equalsIgnoreCase(type) || "aac".equalsIgnoreCase(type) || "flac".equalsIgnoreCase(type)
                || "tak".equalsIgnoreCase(type) || "wv".equalsIgnoreCase(type)) {
            type = "ic_file_audio";
        } else if ("mp3".equalsIgnoreCase(type) || "mid".equalsIgnoreCase(type)) {
            type = "ic_file_mp3";
        } else if ("avi".equalsIgnoreCase(type) || "mp4".equalsIgnoreCase(type) || "dvd".equalsIgnoreCase(type)
                || "mid".equalsIgnoreCase(type) || "mov".equalsIgnoreCase(type) || "mkv".equalsIgnoreCase(type)
                || "mp2v".equalsIgnoreCase(type) || "mpe".equalsIgnoreCase(type) || "mpeg".equalsIgnoreCase(type)
                || "mpg".equalsIgnoreCase(type) || "asx".equalsIgnoreCase(type) || "asf".equalsIgnoreCase(type)
                || "flv".equalsIgnoreCase(type) || "navi".equalsIgnoreCase(type) || "divx".equalsIgnoreCase(type)
                || "rm".equalsIgnoreCase(type) || "rmvb".equalsIgnoreCase(type) || "dat".equalsIgnoreCase(type)
                || "mpa".equalsIgnoreCase(type) || "vob".equalsIgnoreCase(type) || "3gp".equalsIgnoreCase(type)
                || "swf".equalsIgnoreCase(type) || "wmv".equalsIgnoreCase(type)) {
            type = "ic_file_video";
        } else if ("bmp".equalsIgnoreCase(type) || "pcx".equalsIgnoreCase(type) || "tiff".equalsIgnoreCase(type)
                || "gif".equalsIgnoreCase(type) || "jpeg".equalsIgnoreCase(type) || "tga".equalsIgnoreCase(type)
                || "exif".equalsIgnoreCase(type) || "fpx".equalsIgnoreCase(type) || "psd".equalsIgnoreCase(type)
                || "cdr".equalsIgnoreCase(type) || "raw".equalsIgnoreCase(type) || "eps".equalsIgnoreCase(type)
                || "gif".equalsIgnoreCase(type) || "jpg".equalsIgnoreCase(type) || "jpeg".equalsIgnoreCase(type)
                || "png".equalsIgnoreCase(type) || "hdri".equalsIgnoreCase(type) || "ai".equalsIgnoreCase(type)) {
            type = "ic_file_image";
        } else if ("ppt".equalsIgnoreCase(type) || "doc".equalsIgnoreCase(type) || "xls".equalsIgnoreCase(type)
                || "pps".equalsIgnoreCase(type) || "xlsx".equalsIgnoreCase(type) || "xlsm".equalsIgnoreCase(type)
                || "pptx".equalsIgnoreCase(type) || "pptm".equalsIgnoreCase(type) || "ppsx".equalsIgnoreCase(type)
                || "maw".equalsIgnoreCase(type) || "mdb".equalsIgnoreCase(type) || "pot".equalsIgnoreCase(type)
                || "msg".equalsIgnoreCase(type) || "oft".equalsIgnoreCase(type) || "xlw".equalsIgnoreCase(type)
                || "wps".equalsIgnoreCase(type) || "rtf".equalsIgnoreCase(type) || "ppsm".equalsIgnoreCase(type)
                || "potx".equalsIgnoreCase(type) || "potm".equalsIgnoreCase(type) || "ppam".equalsIgnoreCase(type)) {
            type = "ic_file_office";
        } else if ("txt".equalsIgnoreCase(type) || "text".equalsIgnoreCase(type) || "chm".equalsIgnoreCase(type)
                || "hlp".equalsIgnoreCase(type) || "pdf".equalsIgnoreCase(type) || "doc".equalsIgnoreCase(type)
                || "docx".equalsIgnoreCase(type) || "docm".equalsIgnoreCase(type) || "dotx".equalsIgnoreCase(type)) {
            type = "ic_file_text";
        } else if ("ini".equalsIgnoreCase(type) || "sys".equalsIgnoreCase(type) || "dll".equalsIgnoreCase(type)
                || "adt".equalsIgnoreCase(type)) {
            type = "ic_file_system";
        } else if ("rar".equalsIgnoreCase(type) || "zip".equalsIgnoreCase(type) || "arj".equalsIgnoreCase(type)
                || "gz".equalsIgnoreCase(type) || "z".equalsIgnoreCase(type) || "7Z".equalsIgnoreCase(type) || "GZ".equalsIgnoreCase(type)
                || "BZ".equalsIgnoreCase(type) || "ZPAQ".equalsIgnoreCase(type)) {
            type = "ic_file_rar";
        } else if ("html".equalsIgnoreCase(type) || "htm".equalsIgnoreCase(type) || "java".equalsIgnoreCase(type)
                || "php".equalsIgnoreCase(type) || "asp".equalsIgnoreCase(type) || "aspx".equalsIgnoreCase(type)
                || "jsp".equalsIgnoreCase(type) || "shtml".equalsIgnoreCase(type) || "xml".equalsIgnoreCase(type)) {
            type = "ic_file_web";
        } else if ("exe".equalsIgnoreCase(type) || "com".equalsIgnoreCase(type) || "bat".equalsIgnoreCase(type)
                || "iso".equalsIgnoreCase(type) || "msi".equalsIgnoreCase(type)) {
            type = "ic_file_exe";
        } else if ("apk".equalsIgnoreCase(type)) {
            type = "ic_file_apk";
        } else {
            type = "ic_file_normal";
        }
        return type;
    }
    //===========================================文件操作=========================================//
    /**
     * 拷贝文件
     *
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    public static void copyFile(File fromFile, String toFile) throws IOException {
        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = from.read(buffer)) != -1)
                to.write(buffer, 0, bytesRead); // write
        } finally {
            if (from != null)
                try {
                    from.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            if (to != null)
                try {
                    to.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
        }
    }
    /**
     * 创建文件
     *
     * @param file
     * @return
     */
    public static File createNewFile(File file) {
        try {
            if (file.exists()) {
                return file;
            }
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            Log.e(TAG, "", e);
            return null;
        }
        return file;
    }
    /**
     * 创建文件
     *
     * @param path
     */
    public static File createNewFile(String path) {
        File file = new File(path);
        return createNewFile(file);
    }// end method createText()

    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }
    /**
     * 向Text文件中写入内容
     *
     * @param path
     * @param content
     * @return
     */
    public static boolean write(String path, String content) {
        return write(path, content, false);
    }
    public static boolean write(String path, String content, boolean append) {
        return write(new File(path), content, append);
    }
    public static boolean write(File file, String content) {
        return write(file, content, false);
    }
    /**
     * 写入文件
     *
     * @param file
     * @param content
     * @param append
     * @return
     */
    public static boolean write(File file, String content, boolean append) {
        if (file == null || StringUtil.isEmpty(content)) {
            return false;
        }
        if (!file.exists()) {
            file = createNewFile(file);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            fos.write(content.getBytes());
        } catch (Exception e) {
            return false;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }
            fos = null;
        }
        return true;
    }

    /**
     * 读取文件内容，从第startLine行开始，读取lineCount行
     *
     * @param file
     * @param startLine
     * @param lineCount
     * @return 读到文字的list,如果list.size<lineCount则说明读到文件末尾了
     */
    public static List<String> readFile(File file, int startLine, int lineCount) {
        if (file == null || startLine < 1 || lineCount < 1) {
            return null;
        }
        if (!file.exists()) {
            return null;
        }
        FileReader fileReader = null;
        List<String> list = null;
        try {
            list = new ArrayList<String>();
            fileReader = new FileReader(file);
            LineNumberReader lineReader = new LineNumberReader(fileReader);
            boolean end = false;
            for (int i = 1; i < startLine; i++) {
                if (lineReader.readLine() == null) {
                    end = true;
                    break;
                }
            }
            if (end == false) {
                for (int i = startLine; i < startLine + lineCount; i++) {
                    String line = lineReader.readLine();
                    if (line == null) {
                        break;
                    }
                    list.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
    /**
     * 创建文件夹
     *
     * @param dir
     * @return
     */
    public static boolean createDir(File dir) {
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "create dir error", e);
            return false;
        }
    }
    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public static File creatSDDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }
    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public static File write2SDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            creatSDDir(path);
            file = createNewFile(path + "/" + fileName);
            output = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int len = -1;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    /**
     * 读取文件内容 从文件中一行一行的读取文件
     *
     * @param file
     * @return
     */
    public static String readFile(File file) {
        Reader read = null;
        String content = "";
        String result = "";
        BufferedReader br = null;
        try {
            read = new FileReader(file);
            br = new BufferedReader(read);
            while ((content = br.readLine().toString().trim()) != null) {
                result += content + "\r\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                read.close();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * 将图片保存到本地时进行压缩, 即将图片从Bitmap形式变为File形式时进行压缩,
     * 特点是: File形式的图片确实被压缩了, 但是当你重新读取压缩后的file为 Bitmap是,它占用的内存并没有改变
     *
     * @param bmp
     * @param file
     */
    public static void compressBmpToFile(Bitmap bmp, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;// 个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 将图片从本地读到内存时,进行压缩 ,即图片从File形式变为Bitmap形式
     * 特点: 通过设置采样率, 减少图片的像素, 达到对内存中的Bitmap进行压缩
     * @param srcPath
     * @return
     */
    public static Bitmap compressImageFromFile(String srcPath, float pixWidth, float pixHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        //float pixWidth = 800f;//
        //float pixHeight = 480f;//
        int scale = 1;
        if (w > h && w > pixWidth) {
            scale = (int) (options.outWidth / pixWidth);
        } else if (w < h && h > pixHeight) {
            scale = (int) (options.outHeight / pixHeight);
        }
        if (scale <= 0)
            scale = 1;
        options.inSampleSize = scale;// 设置采样率
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;// 该模式是默认的,可不设
        options.inPurgeable = true;// 同时设置才会有效
        options.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
        bitmap = BitmapFactory.decodeFile(srcPath, options);
        // return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        // 其实是无效的,大家尽管尝试
        return bitmap;
    }
    /**
     *  指定分辨率和清晰度的图片压缩
     */
    public void transImage(String fromFile, String toFile, int width, int height, int quality) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 缩放图片的尺寸
            float scaleWidth = (float) width / bitmapWidth;
            float scaleHeight = (float) height / bitmapHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 产生缩放后的Bitmap对象
            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            // save file
            File myCaptureFile = new File(toFile);
            FileOutputStream out = new FileOutputStream(myCaptureFile);
            if(resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)){
                out.flush();
                out.close();
            }
            if(!bitmap.isRecycled()){
                bitmap.recycle();//记得释放资源，否则会内存溢出
            }
            if(!resizeBitmap.isRecycled()){
                resizeBitmap.recycle();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}



