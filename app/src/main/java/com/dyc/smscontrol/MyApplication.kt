package com.dyc.smscontrol

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 * use  : yc
 * data : 2020/7/8
 * time : 0:49
 * desc :
 */

class MyApplication  :Application(){

    override fun onCreate() {
        super.onCreate()

        Utils.init(this)
    }
}