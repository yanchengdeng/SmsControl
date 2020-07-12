package com.dyc.smscontrol.entity

import java.io.Serializable

/**
 * use  : yanc
 * data : 2020/7/9
 * time : 0:38
 * desc :
 */


data class Result<T>(
    val code: Int,
    val msg: String,
    var data: T
)


data class User(val uid :String)


data class Msg(
    val phone: String = "",
    val smsContent: String = "",
    val datetime: String = "",


    val id : String ="",
    val status : Int = 0,// 1成功 2失败",
    val remark :String = ""
) : Serializable


 class PageInfo {
    var page = 1
    fun nextPage() {
        page++
    }

    fun reset() {
        page = 1
    }

    val isFirstPage: Boolean
        get() = page == 1
}

data class BankItem(
    val id :String,
    var isSelected :Boolean,
    val name :String
)

