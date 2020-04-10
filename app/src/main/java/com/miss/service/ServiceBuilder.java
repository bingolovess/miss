package com.miss.service;

import com.miss.http.BaseApi;

public class ServiceBuilder {
    /**
     * 接口服务
     */
    private static Api apiService;

    /**
     * 获取用户服务
     */
    public static synchronized Api getApiService() {
        if (null == apiService) {
            apiService = BaseApi.createRetrofit().create(Api.class);
        }
        return apiService;
    }
}
