package com.miss.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.adapter.recyclerview.CommonAdapter;
import com.miss.base.adapter.recyclerview.MultiItemTypeAdapter;
import com.miss.base.adapter.recyclerview.base.ViewHolder;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.base.utils.LogUtils;
import com.miss.base.utils.ResourceUtils;
import com.miss.bean.WebLink;
import com.miss.promise.Promise;
import com.miss.view.CommonHeader;

import java.util.List;

public class WebActivity extends BaseActivity {

    private CommonHeader commonHeader;
    private WebView webView;
    @Override
    public boolean isFullScreen() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.colorPrimary));//设置背景颜色
        commonHeader = findViewById(R.id.common_header);
        initWebView();
        initView();
    }

    private void initWebView() {
        webView = findViewById(R.id.webview);
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
               // return super.shouldOverrideUrlLoading(view, request);
            }
        });
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);//支持插件
        webSettings.setDomStorageEnabled(true); // 开启DOM缓存,默认状态下是不支持LocalStorage的
        webSettings.setDatabaseEnabled(true); // 开启数据库缓存
        //webSettings.setPluginsEnabled(true);//设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSavePassword(false);// 关闭密码保存提醒功能
        webSettings.setLoadsImagesAutomatically(true); // 支持自动加载图片

        //webSettings.setUserAgentString(""); // 设置 UserAgent 属性
        webSettings.setAllowFileAccess(true); // 允许加载本地 html 文件/false
        // 允许通过 file url 加载的 Javascript 读取其他的本地文件,Android 4.1 之前默认是true，在 Android 4.1 及以后默认是false,也就是禁止
        //webSettings.setAllowFileAccessFromFileURLs(false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源，
        // Android 4.1 之前默认是true，在 Android 4.1 及以后默认是false,也就是禁止
        // 如果此设置是允许，则 setAllowFileAccessFromFileURLs 不起做用
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        // 缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        //优先使用缓存
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
        //不使用缓存
        //webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        //注入js交互
        webView.addJavascriptInterface(new JsBridgeInterface(),"Android");
        // 本地 html alert来调试数据时，一直没有作用。
        //通过查API，知道 有个setWebChromeClient的方法，官方解释为Sets the chrome handler. This is an implementation of WebChromeClient for use in handling JavaScript dialogs, favicons, titles, and the progress.This will replace the current handler.
        // 大体意思是这个方法是来处理 javascript 方法中的对话框，收藏夹，标题和进度条之类的
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                LogUtils.getInstance().e(url+"-----------"+message);
                Uri uri = Uri.parse(message);
                boolean handle = message.startsWith("js:");
                if(handle){
                    result.confirm("trigger"); // 有客户端直接返回结果，不会吊起input
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent!=null){
            String title = intent.getStringExtra("title");
            String url = intent.getStringExtra("url");
            if (!TextUtils.isEmpty(title)){
                commonHeader.setTitle(title);
            }
            if (!TextUtils.isEmpty(url)){
                webView.loadUrl(url);
            }
        }
    }
    //Android调用JS
    public void callJs(View view){
        webView.loadUrl("javascript:callJS()");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView!=null){
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再destory()
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }

            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();

            try {
                webView.destroy();
            } catch (Throwable ex) {

            }
        }
    }
    /**
     * 调用Web 页面的接口
     */
    private class JsBridgeInterface{
        //此方法要求4.2版本以上，4.2以下不能使用
        @JavascriptInterface
        public void show(String msg){
            Toast.makeText(WebActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
