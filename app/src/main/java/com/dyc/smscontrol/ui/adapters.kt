package com.dyc.smscontrol.ui

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.Msg

/**
 * use  : yanc
 * data : 2020/7/11
 * time : 22:32
 * desc :
 */

/**
 * 消息列表
 */
class MessageAdapter(layoutResId: Int, data: MutableList<Msg>?) :
    BaseQuickAdapter<Msg, BaseViewHolder>(layoutResId, data),LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: Msg) {

        holder.setText(R.id.tv_phone,item.phone)
        holder.setText(R.id.tv_time,item.datetime)
        holder.setText(R.id.tv_content,item.smsContent)


    }

}