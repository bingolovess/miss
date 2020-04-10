package com.miss.tasks;

import android.app.Activity;
import android.content.Context;

import com.miss.launch.Task;
import com.rxjava.rxlife.RxLife;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;
import rxhttp.wrapper.param.Method;
import rxhttp.wrapper.param.Param;
import rxhttp.wrapper.param.RxHttp;
import rxhttp.wrapper.ssl.SSLSocketFactoryImpl;
import rxhttp.wrapper.ssl.X509TrustManagerImpl;

public class RxHttpTask extends Task {
    @Override
    public void run() {
        initRxHttp();
    }

    /**
     * 参考鸿洋大神的博客
     * https://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650829703&idx=1&sn=da1a5e8404e7687ec75c387fbc6293bc&chksm=80b7a719b7c02e0f5edd78375a5b7c645275ba107f9b200e1b40fa9c8371cabd383c07cdee90&mpshare=1&scene=23&srcid=&sharer_sharetime=1578532703449&sharer_shareid=5360a14681bbf75cb107eaf8ad86786a#rd
     */
    private void initRxHttp() {
        //首次添加依赖，需要运行rebuild 生成RxHttp
        OkHttpClient okHttpClient = getDefaultOkHttpClient();
        RxHttp.init(okHttpClient,true);
    }
    private static OkHttpClient getDefaultOkHttpClient() {
        X509TrustManager trustAllCert = new X509TrustManagerImpl();
        SSLSocketFactory sslSocketFactory = new SSLSocketFactoryImpl(trustAllCert);
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory, trustAllCert) //添加信任证书
                .hostnameVerifier((hostname, session) -> true) //忽略host验证
                .build();
    }

    /**
     * 添加公共参数示例
     */
    private void setCommonHeader(){
        RxHttp.setOnParamAssembly(new Function<Param, Param>() {
            @Override
            public Param apply(Param p) { //此方法在子线程中执行，即请求发起线程
                Method method = p.getMethod();
                if (method.isGet()) {     //可根据请求类型添加不同的参数
                } else if (method.isPost()) {
                }
                return p.add("versionName", "1.0.0")//添加公共参数
                        .addHeader("deviceType", "android"); //添加公共请求头
            }
        });
    }

    /**
     * 设置不添加公共参数/请求头 setAssemblyEnabled(false)
     */
    private void requestWithoutHeaderParams(){
        RxHttp.get("/service/...")       //get请求
                .setAssemblyEnabled(false)   //设置是否添加公共参数/头部，默认为true
                .asString()                  //返回字符串数据
                .subscribe(s -> {            //这里的s为String类型
                    //请求成功
                }, throwable -> {
                    //请求失败
                });
    }

    /**
     * 结合RxLife  自动关闭请求
     * @param activity
     */
    private void autoClose(Context activity){
        //以下代码均在FragmentActivty/Fragment中调用
        RxHttp.postForm("/service/...")
                .asString()
//                .as(RxLife.as(activity)) //页面销毁、自动关闭请求
                .subscribe();
        //或者
        RxHttp.postForm("/service/...")
                .asString()
//                .as(RxLife.asOnMain(activity)) //页面销毁、自动关闭请求 并且在主线程回调观察者
                .subscribe();
    }

    /**
     * 手动关闭请求
     */
    private void doCloceRequest(){
        //订阅回调，可以拿到Disposable对象
        Disposable disposable = RxHttp.get("/service/...")
                .asString()
                .subscribe(s -> {
                    //成功回调
                }, throwable -> {
                    //失败回调
                });
        if (!disposable.isDisposed()) {  //判断请求有没有结束
            disposable.dispose();       //没有结束，则关闭请求
        }
    }

    /**
     * 文件上传示例
     */
    private void upload(){
        RxHttp.postForm("/service/...") //发送Form表单形式的Post请求
                .addFile("file1", new File("xxx/1.png"))  //添加单个文件
                .addFile("fileList", new ArrayList<>())   //通过List对象，添加多个文件
                .asString()
                .subscribe(s -> {
                    //上传成功
                }, throwable -> {
                    //上传失败
                });
    }

    /**
     * 上传带进度
     */
    private void uploadProgress(){
        RxHttp.postForm("/service/...") //发送Form表单形式的Post请求
                .addFile("file1", new File("xxx/1.png"))
                .addFile("file2", new File("xxx/2.png"))
                .asUpload(progress -> {
                    //上传进度回调,0-100，仅在进度有更新时才会回调
                    int currentProgress = progress.getProgress(); //当前进度 0-100
                    long currentSize = progress.getCurrentSize(); //当前已上传的字节大小
                    long totalSize = progress.getTotalSize();     //要上传的总字节大小
                }, AndroidSchedulers.mainThread())   //指定回调(进度/成功/失败)线程,不指定,默认在请求所在线程回调
                .subscribe(s -> {
                    //上传成功
                }, throwable -> {
                    //上传失败
                });
    }

    /**
     * 文件下载 使用的是asDownload操作符
     * @param context
     */
    private void downloadFile(Context context){
        //文件存储路径
        String destPath = context.getExternalCacheDir() + "/" + System.currentTimeMillis() + ".apk";
        RxHttp.get("http://update.9158.com/miaolive/Miaolive.apk")
                .asDownload(destPath) //注意这里使用download操作符，并传入本地路径
                //.as(RxLife.asOnMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {
                    //下载成功,回调文件下载路径
                }, throwable -> {
                    //下载失败
                });
    }

    /**
     * 文件下载进度监听
     * @param context
     */
    private void downloadFileProgress(Context context){
//文件存储路径
        String destPath = context.getExternalCacheDir() + "/" + System.currentTimeMillis() + ".apk";
        RxHttp.get("http://update.9158.com/miaolive/Miaolive.apk")
                .asDownload(destPath, progress -> {
                    //下载进度回调,0-100，仅在进度有更新时才会回调，最多回调101次，最后一次回调文件存储路径
                    int currentProgress = progress.getProgress(); //当前进度 0-100
                    long currentSize = progress.getCurrentSize(); //当前已下载的字节大小
                    long totalSize = progress.getTotalSize();     //要下载的总字节大小
                }, AndroidSchedulers.mainThread())//指定主线程回调
               // .as(RxLife.as(this)) //感知生命周期
                .subscribe(s -> {//s为String类型，这里为文件存储路径
                    //下载完成，处理相关逻辑
                }, throwable -> {
                    //下载失败，处理相关逻辑
                });
    }
}
