package com.dyc.smscontrol.http;


import com.dyc.smscontrol.entity.Msg;
import com.dyc.smscontrol.entity.Result;
import com.dyc.smscontrol.entity.User;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

    //登录
    @POST("api/default/login")
    @FormUrlEncoded
    Observable<Result<User>> login(@FieldMap HashMap<String,String> maps);


    /**
     * BankcardIds=1,2,3&smsContent=短信内容
     * @return
     */
    //提交监听数据
    @POST("api/default/listen")
    @FormUrlEncoded
    Observable<Result<User>> submitSms(@FieldMap HashMap<String,String> maps);


    /**
     * page=1&pageSize=10
     * @param page
     * @param page
     * @return
     */
    //监听记录
    @POST("api/default/listenLog")
    @FormUrlEncoded
    Observable<Result<List<Msg>>> smsList(@Field("page") String page, @Field("pageSize") String pageSize);

}
