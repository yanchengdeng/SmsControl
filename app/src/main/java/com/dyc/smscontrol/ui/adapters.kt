package com.dyc.smscontrol.ui

import android.view.View
import androidx.annotation.ColorRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.BankItem
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

        if (item.isShowUploadInfo){
            holder.setText(R.id.tv_upload_result,"${item.remark}")
            holder.setVisible(R.id.tv_upload_result, true)
            if (item.status==1){
                holder.setTextColor(R.id.tv_upload_result,context.resources.getColor(R.color.colorPrimary))
            }else{
                holder.setTextColor(R.id.tv_upload_result,context.resources.getColor(R.color.red))
            }
        }
    }

}



/**
 * 消息列表
 */
class BankAdapter(layoutResId: Int, data: MutableList<BankItem>?) :
    BaseQuickAdapter<BankItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(holder: BaseViewHolder, item: BankItem) {

        holder.setText(R.id.tv_bank_id,"银行卡id：${item.id}")
        holder.setText(R.id.tv_card_name,item.name)
        if (item.isSelected){
            holder.setImageResource(R.id.iv_selected,R.mipmap.checked)
        }else{
            holder.setImageResource(R.id.iv_selected,R.mipmap.unchecked)
        }
    }

}