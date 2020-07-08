package com.dyc.smscontrol.entity

import java.io.Serializable

/**
 * use  : yanc
 * data : 2020/7/9
 * time : 0:38
 * desc :
 */

data class User(val account: String)


data class Msg(
    val phone :String,
    val body : String,
    val date : String
):Serializable