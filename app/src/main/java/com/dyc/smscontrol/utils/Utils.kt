package com.dyc.smscontrol.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.Msg
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

/**
 * use  : yanc
 * data : 2020/7/9
 * time : 1:43
 * desc :
 */


class SystemLog {
    companion object{

        //日志打印
        @JvmStatic
        fun log( tag :String ,msg: String){
            if (Constants.IS_DEBUG){
                LogUtils.w(tag,msg)
            }
        }

        //日志打印
        @JvmStatic
        fun log( msg: String){
            log(  "yancheng",msg)
        }

        //TODO 如果需要读取联系人 需要添加 对应权限 及 动态获取权限
        //获取联系人信息
        @JvmStatic
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

        /**
         * recyclerview 分割线
         */
        @JvmStatic
        fun getRecycleDiv(context: Context) :DividerItemDecoration{
           val itemDecoration =  DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            itemDecoration.setDrawable( ColorDrawable(ContextCompat.getColor(context,R.color.color_222222)));
            return  itemDecoration
        }

        /**
         * 公共参数
         */
        @JvmStatic
        fun getCommonMaps (maps :HashMap<String,String>): HashMap<String,String> {
            val timeTemp = System.currentTimeMillis()/1000
            val timeUid = "$timeTemp${SPUtils.getInstance().getString(Constants.LOGINED_TOKEN)}"
            val rsaStr = Rsa().encryptByPublicKey(timeUid)
            maps.put("time",timeTemp.toString())
            maps.put("uid",rsaStr)
            SystemLog.log(maps.toString())
            return maps

        }

        /**
         * 保存搜索记录 至
         * @param keyWords  本地保存关键字
         */
        @JvmStatic
        inline fun <reified T> saveSearchHistory(keyWords: String, info: T) {
            val datas = getSearchHistroy<T>(keyWords)
            datas.add(info)
            if (datas.size>10){
                datas.removeAt(0)
            }

            SPUtils.getInstance()
                .put(keyWords, Gson().toJson(datas))
        }


        /**
         * 获取保存记录
         */
        @JvmStatic
        inline fun <reified T> getSearchHistroy(address: String): MutableList<T> {
            val infos =
                SPUtils.getInstance().getString(address)
            return if (TextUtils.isEmpty(infos)) {
                mutableListOf()
            } else {

                val datas = mutableListOf<T>()
                val jsonArray = JsonParser().parse(infos).asJsonArray
                for (jssonElemet in jsonArray){
                    datas.add(Gson().fromJson(jssonElemet,T::class.java))
                }
                datas
            }
        }

        /**
         * 清除记录
         */ @JvmStatic
        fun clearSearchHistroy(key: String) {
            return SPUtils.getInstance().remove(key)
        }
    }
}