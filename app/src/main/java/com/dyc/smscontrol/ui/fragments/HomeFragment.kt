package com.dyc.smscontrol.ui.fragments

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.JsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.utils.SystemLog
import com.tbruyelle.rxpermissions2.RxPermissions
import deng.yc.baseutils.SmsContentObserver
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private var smsContentObserver: SmsContentObserver? = null
    private val myHandler : Handler = Handler(object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            tvMsg.text = GsonUtils.toJson(msg.obj)
            return false
        }

    })

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val permissions = RxPermissions(this)
        permissions
            .request(Manifest.permission.READ_SMS)
            .subscribe { granted ->
                if (granted) {
                    text_home.text = "服务已开启..."
                } else {
                    text_home.text = "服务未开启，请打开短信权限"
                }
            }

        activity?.let {
            smsContentObserver = SmsContentObserver(it,handler = myHandler)
            smsContentObserver?.let { smsContentObserver ->
                activity?.contentResolver?.registerContentObserver(Uri.parse(Constants.SMS),true,smsContentObserver)
                SystemLog.log(msg = "启动信息监听")
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onDestroy() {
        super.onDestroy()

        activity?.let {
            smsContentObserver?.let {sms ->
                it.contentResolver.unregisterContentObserver(sms)
                SystemLog.log(msg = "关闭信息监听")
            }
        }
    }
}