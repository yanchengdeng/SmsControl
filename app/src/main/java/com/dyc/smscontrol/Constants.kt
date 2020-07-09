package com.dyc.smscontrol

/**
 * use  : yanc
 * data : 2020/7/9
 * time : 0:23
 * desc :常量
 */

 class Constants{
    companion object{
        const val   IS_DEBUG = true
        //接口api
        const val BASE_URL = "https://www.baidu.com/?tn=21002492_30_hao_pg"
        //保存登录状态
        const val LOGINED_STATUS = "login_status"
        //超时
        const val DEFAULT_TIMEOUT = 10
        //返回正确结果
        const val API_OK = 200
        //短信库
        const val SMS = "content://sms"
    }
}