package com.miss.tasks;

import com.miss.http.BaseApi;
import com.miss.http.NetConfig;
import com.miss.launch.Task;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Interceptor;

public class RetrofitTask extends Task {
    @Override
    public void run() {
        initRetrofit();
    }
    private void initRetrofit() {
        BaseApi.registerConfig(new NetConfig() {
            @Override
            public String configBaseUrl() {
                return "http://10.168.1.225:3000/";//https://mock.cangdu.org/mock/5ddcc047e86fe16ca6ae7eb4/api/
            }

            @Override
            public Interceptor[] configInterceptors() {
                List<Interceptor> interceptorList = new ArrayList<>();
                Interceptor[] interceptors =  new Interceptor[interceptorList.size()];
                boolean debug = false;
                if (debug){

                }
                return interceptorList.toArray(interceptors);
            }

            @Override
            public long configConnectTimeoutMills() {
                return 45 * 1000;
            }

            @Override
            public long configReadTimeoutMills() {
                return 45 * 1000;
            }

            @Override
            public boolean configLogEnable() {
                return true;
            }
        });
    }
}
