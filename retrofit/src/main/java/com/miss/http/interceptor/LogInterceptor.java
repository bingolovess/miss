package com.miss.http.interceptor;

import android.util.Log;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LogInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        log("request:" + request.toString());
        long t1 = System.nanoTime();
        Response response = chain.proceed(chain.request());
        long t2 = System.nanoTime();
        /*LogUtils.e(String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));*/
        log(response.request().url()+"--"+((t2 - t1) / 1e6d+"ms\n"+response.headers()));
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        log("response body:" + content);
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }
    private void log(String msg){
        Log.e("LogInterceptor>>>", msg);
    }
}
