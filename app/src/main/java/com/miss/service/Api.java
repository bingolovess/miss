package com.miss.service;


import com.miss.bean.User;
import com.miss.http.bean.BaseResponse;

import java.util.Map;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {
    @POST("login")
    @FormUrlEncoded
    Observable<BaseResponse<User>> login(@FieldMap Map<String, String> map);
    /**
     * 单图上传
     */
    @Multipart
    @POST("/upload")
    Observable<String> uploadImage(@Part("fileName") String description, @Part("file\"; filename=\"image.png\"") RequestBody imgs);

    /**
     * 多图上传
     * @param description
     * @return
     */
    @Multipart
    @POST("/upload")
    Observable<String> uploadImage(@Part("fileName") String description,
                             @Part("file\"; filename=\"image.png\"")RequestBody imgs,
                             @Part("file\"; filename=\"image.png\"")RequestBody imgs1,
                             @Part("file\"; filename=\"image.png\"")RequestBody imgs3);
}
