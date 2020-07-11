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
        const val BASE_URL = "http://test1.yalalat.com/jttest/"
        //保存登录状态
        const val LOGINED_STATUS = "login_status"
        //保存登录名称
        const val LOGINED_NICKNAME = "login_nick_name"
        //保存登录token
        const val LOGINED_TOKEN = "login_token"
        //超时
        const val DEFAULT_TIMEOUT = 10
        //返回正确结果
        const val API_OK = 0
        //短信库
        const val SMS = "content://sms"
    }
}