package com.miss.http.interceptor;

import java.io.IOException;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Response originalResponse = chain.proceed(chain.request());
        List<String> cookies = originalResponse.headers("set-cookie");
        if (!cookies.isEmpty()) {
            String[] cookieArray = cookies.get(0).split(";");//取第一个cookie
            String cookie = cookieArray[0];//cookie数组的第一个元素
        }
        return originalResponse;
    }
}
