package com.dyc.smscontrol.ui

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R


/**
 * author : yanc
 * data : 2020/7/9
 * time : 0:03
 * desc : 启动页
 */
class WelcomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)

        val isLogin = SPUtils.getInstance().getBoolean(Constants.LOGINED_STATUS,false)
        if (isLogin){
            ActivityUtils.startActivity(MainActivity::class.java)
        }else{
            ActivityUtils.startActivity(LoginActivity::class.java)
        }
        finish()
    }

}