package com.dyc.smscontrol.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.utils.SystemLog

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

     fun  dealApiCode(code :Int,msg :String){

         if (code==Constants.API_TIPS) {
             ToastUtils.showShort(msg)
         }else if (code==Constants.API_USER_ERROR){
             if (this is LoginActivity){
                 // 不处理
             }else{
                 ActivityUtils.finishAllActivitiesExceptNewest()
                 SPUtils.getInstance().remove(Constants.LOGINED_STATUS)
                 SPUtils.getInstance().remove(Constants.LOGINED_NICKNAME)
                 SPUtils.getInstance().remove(Constants.LOGINED_TOKEN)
                 ActivityUtils.startActivity(LoginActivity::class.java)
                 finish()
             }
         }else{
             SystemLog.log("其他异常")
         }
    }
}