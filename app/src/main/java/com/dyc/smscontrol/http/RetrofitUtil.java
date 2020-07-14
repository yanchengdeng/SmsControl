package com.dyc.smscontrol.http;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.dyc.smscontrol.Constants;
import com.dyc.smscontrol.utils.SystemLog;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.dyc.smscontrol.Constants.IS_DEBUG;

public class RetrofitUtil {
    private volatile static Retrofit retrofit;
    private static RetrofitUtil util;
    private UserService userService;

    public static RetrofitUtil getInstance() {
        if (util == null) {
            synchronized (RetrofitUtil.class) {
                if (util == null) {
                    util = new RetrofitUtil();
                }
            }
        }
        return util;
    }

    private RetrofitUtil() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(IS_DEBUG ? HttpLoggingInterceptor.Level.BODY:HttpLoggingInterceptor.Level.NONE);
//
//        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
//        httpClientBuilder.connectTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
//        httpClientBuilder.addInterceptor(interceptor);
//        Retrofit retrofit = new Retrofit.Builder()
//                .client(httpClientBuilder.build())
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .baseUrl(SPUtils.getInstance().getString(Constants.SAVE_BASE_URL))
//                .build();
        userService = getInstants().newBuilder().build().create(UserService.class);
        LogUtils.w("yancheng--RetrofitUtil",retrofit.baseUrl().toString());
    }


    @NonNull
    public static Retrofit getInstants() {
        if (retrofit == null) {
            synchronized (RetrofitUtil.class) {
                if (retrofit == null) {
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(IS_DEBUG ? HttpLoggingInterceptor.Level.BODY:HttpLoggingInterceptor.Level.NONE);

                    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
                    httpClientBuilder.connectTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                    httpClientBuilder.addInterceptor(interceptor);
                    retrofit = new Retrofit.Builder()
                            .client(httpClientBuilder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .baseUrl(SPUtils.getInstance().getString(Constants.SAVE_BASE_URL))
                            .build();
                }
            }
        }

        return retrofit;
    }



    public UserService userService() {
        return userService;
    }

    public void resetRetrofit(){
        retrofit =null;
        util = null;
    }

}
