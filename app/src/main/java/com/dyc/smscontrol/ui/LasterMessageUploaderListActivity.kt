package com.dyc.smscontrol.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.Msg
import com.dyc.smscontrol.utils.SystemLog
import kotlinx.android.synthetic.main.activity_message_list.*
import kotlinx.android.synthetic.main.common_header.*

/**
*@author : yanc ->
*@Create : 2020/7/16
*@Time : 10:23
*@Describe ：最近10条短信上传记录
**/
class LasterMessageUploaderListActivity : AppCompatActivity() {

    private val adadpter = MessageAdapter(R.layout.adapter_msg, mutableListOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laster_message_uploader_list)

        tv_title.text=getString(R.string.sms_record)
        recycle_view.layoutManager = LinearLayoutManager(this)
        recycle_view.addItemDecoration(SystemLog.getRecycleDiv(this))
        recycle_view.adapter = adadpter

        val datas = SystemLog.getSearchHistroy<Msg>(Constants.SAVE_SMS_UPLOAD_MSG)
        adadpter.setList(datas)

        iv_back.setOnClickListener { finish() }
    }
}