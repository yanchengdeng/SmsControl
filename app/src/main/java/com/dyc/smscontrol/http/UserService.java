package com.dyc.smscontrol.http;


import com.dyc.smscontrol.entity.Result;
import com.dyc.smscontrol.entity.User;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

    @POST("login/check")
    @FormUrlEncoded
    Observable<Result<User>> login(@Field("app_user") String app_user, @Field("app_pass") String app_pass);

    @POST("company/payCallback")
    @FormUrlEncoded
    Observable<Result> callback(@Field("sign") String sign);

}
