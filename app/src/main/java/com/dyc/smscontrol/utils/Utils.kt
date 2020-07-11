package com.dyc.smscontrol.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.blankj.utilcode.util.LogUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R

/**
 * use  : yanc
 * data : 2020/7/9
 * time : 1:43
 * desc :
 */


class SystemLog {
    companion object{

        //日志打印
        fun log( tag :String ,msg: String){
            if (Constants.IS_DEBUG){
                LogUtils.w(tag,msg)
            }
        }

        //日志打印
        fun log( msg: String){
            log(  "yancheng",msg)
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

        fun getRecycleDiv(context: Context) :DividerItemDecoration{
           val itemDecoration =  DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            itemDecoration.setDrawable( ColorDrawable(ContextCompat.getColor(context,R.color.color_222222)));
            return  itemDecoration
        }
    }
}