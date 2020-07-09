package com.dyc.smscontrol.utils

import android.content.Context
import android.net.Uri
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

        //日志打印
        fun log( tag :String = "yancheng",msg: String){
            if (Constants.IS_DEBUG){
                LogUtils.w(tag,msg)
            }
        }

        //TODO 如果需要读取联系人 需要添加 对应权限 及 动态获取权限
        //获取联系人信息
        fun getContactNameByNum(context: Context,phoneNum :String) :String{
            val uri =
                Uri.parse("content://com.android.contacts/data/phones/filter/$phoneNum")
            val resolver = context.contentResolver
            val cursor =
                resolver.query(uri, arrayOf("display_name"), null, null, null)
            var name  = "通讯录未备注"
            cursor?.let {
                if (it.moveToFirst()) {
                    name = it.getString(0)
                    LogUtils.d("dyc", "名字:$name")
                }
                it.close()
            }
            return name

        }
    }
}