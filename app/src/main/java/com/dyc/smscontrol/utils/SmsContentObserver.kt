package deng.yc.baseutils

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.Telephony
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.dyc.smscontrol.entity.Msg
import com.dyc.smscontrol.utils.SystemLog


/**
 *@Author : yancheng
 *@Date : 2020/7/8
 *@Time : 14:30
 *@Describe ：
 **/
class SmsContentObserver constructor(context:Context,handler:Handler) : ContentObserver(handler){
    private val smsInboxUri = "content://sms/inbox"
    private val ctx: Context? = context
    private val sortOrder: String = Telephony.Sms.DATE+" desc"
    private val mHandler :Handler = handler
    //用作去重
    private var msg_id = "0"
    private var mUri: Uri? = null


    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        /**
         * // 第一遍 先执行 content://sms/raw
        // 第二遍则是 content://sms/inbox
        if (uri.toString().equals("content://sms/inbox")) {
        // return 后就不会执行发送数据到手环的代码了
        return;
        }
         */

        SystemLog.log( msg = " 变化=$selfChange, 路径地址： uri = $uri")
        /**
         * 适配某些较旧的设备，可能只会触发onChange(boolean selfChange)方法，没有传回uri参数，
         * 此时只能通过"content://sms/inbox"来查询短信
         */
        mUri = uri ?:  Uri.parse("content://sms/inbox")
        /**
         * 06-15 11:45:48.706 D/SmsContent: onChange(boolean selfChange, Uri uri). selfChange=false, uri=content://sms/raw
         * 06-15 11:45:49.466 D/SmsContent: onChange(boolean selfChange, Uri uri). selfChange=false, uri=content://sms/387
         *
         * Generally onChange will be triggered twice, first time is triggered by uri "content://sms/raw"(sms received,
         * but have not written into inbox), second time is triggered by uri "content://sms/387"(number is sms id)
         */

        if (mUri.toString().contains("content://sms/raw") || mUri.toString() == "content://sms") {
            return
        }


        val cursor = ctx?.contentResolver?.query(mUri!!,null,null,null,sortOrder)
        if (cursor!=null){
            if (cursor.moveToFirst()){
                val _id = cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID))
                val phone = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS))
                val date = cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE))
    //          val subject = cursor.getString(cursor.getColumnIndex(Telephony.Sms.SUBJECT))
                val smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY))
//              val status = cursor.getString(cursor.getColumnIndex(Telephony.Sms.STATUS))
                /**
                 * type
                ALL    = 0;
                INBOX  = 1;
                SENT   = 2;
                DRAFT  = 3;
                OUTBOX = 4;
                FAILED = 5;
                QUEUED = 6;
                 */
//              val type = cursor.getString(cursor.getColumnIndex(Telephony.Sms.TYPE))
                val dateString = TimeUtils.millis2String(date.toLong(),"yyyy-MM-dd HH:mm:ss")
                cursor.close()
                if (msg_id != _id){
                    msg_id = _id
                    val message = mHandler.obtainMessage()
                    message.obj = Msg(phone = phone,smsContent = smsBody,datetime = dateString)
                    mHandler.sendMessage(message)
                }else{
                    SystemLog.log("重复数据msg_id = $msg_id")
                    return
                }
            }
        }
    }
}