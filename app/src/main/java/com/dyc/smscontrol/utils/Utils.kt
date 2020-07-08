package com.dyc.smscontrol.utils

import com.blankj.utilcode.util.LogUtils
import com.dyc.smscontrol.Constants

/**
 * use  : yanc
 * data : 2020/7/9
 * time : 1:43
 * desc :
 */


class SystemLog {
    companion object{
        public fun log( tag :String = "yancheng",msg: String){
            if (Constants.IS_DEBUG){
                LogUtils.w(tag,msg)
            }
        }
    }
}