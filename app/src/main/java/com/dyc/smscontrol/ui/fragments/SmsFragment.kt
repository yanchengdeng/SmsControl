package com.dyc.smscontrol.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.Msg
import com.dyc.smscontrol.entity.PageInfo
import com.dyc.smscontrol.entity.Result
import com.dyc.smscontrol.entity.User
import com.dyc.smscontrol.http.RetrofitUtil
import com.dyc.smscontrol.ui.BankListActivity
import com.dyc.smscontrol.ui.LoginActivity
import com.dyc.smscontrol.ui.MessageAdapter
import com.dyc.smscontrol.utils.SystemLog
import com.dyc.smscontrol.utils.SystemLog.Companion.getCommonMaps
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions2.RxPermissions
import deng.yc.baseutils.SmsContentObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.IOException

/**
 * author : yanc
 * data : 2020/7/12
 * time : 16:09
 * desc : 短信监听功能
 */
class SmsFragment : Fragment() {
    private var smsContentObserver: SmsContentObserver? = null

    private val myHandler : Handler = Handler(Handler.Callback { msg ->
        if (msg.obj is Msg){
            Snackbar.make(ll_root,(msg.obj as Msg).smsContent,Snackbar.LENGTH_SHORT).show()
            uploadMsg(msg.obj as Msg)
        }
        false
    })



    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val permissions = RxPermissions(this)
        permissions
            .request(Manifest.permission.READ_SMS)
            .subscribe { granted ->
                if (granted) {
                    text_home.text = "短信服务已开启..."
                    addSmsObserver()
                } else {
                    text_home.text = "服务未开启，请打开短信权限"
                }
            }


        btn_sms_record.setOnClickListener {
            ToastUtils.showShort("暂未开放")
        }

        btn_change_cards.setOnClickListener {
            //更改监听卡信息需要先关闭短信服务
            activity?.let {
                MaterialDialog(it).show {
                    title(text = "温馨提示")
                    message(text = "更改监听卡信息需要先关闭短信服务")
                    positiveButton(R.string.sure) { dialog ->
                        cancelSmsObserver()
                        ActivityUtils.startActivity(BankListActivity::class.java)
                        dismiss()
                        it.finish()
                    }
                    negativeButton(R.string.cancel) { dialog ->
                        dismiss()
                    }
                }
            }
        }
    }


    //添加sms监听
    private fun addSmsObserver(){
        try {
            activity?.let {
                smsContentObserver = SmsContentObserver(it,handler = myHandler)
                smsContentObserver?.let { smsContentObserver ->
                    activity?.contentResolver?.registerContentObserver(Uri.parse(Constants.SMS),true,smsContentObserver)
                    SystemLog.log(msg = "启动信息监听")
                }
            }
        }catch (e:Exception){

        }
    }


    //取消sms监听
    private fun  cancelSmsObserver(){
        try {
            activity?.let {
                smsContentObserver?.let {sms ->
                    it.contentResolver.unregisterContentObserver(sms)
                    SystemLog.log(msg = "关闭信息监听")
                }
            }
        }catch (e:Exception){

        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }








    /**
     * 上传信息
     * bankcardIds=1,2,3&smsContent=短信内容&datetime=短信接收时间&mobile=短信发送手机号
     */
    private fun uploadMsg(msg: Msg) {
        val maps = HashMap<String,String>()
        maps.put("bankcardIds",SPUtils.getInstance().getString(Constants.CARDS_ID))
        maps.put("smsContent",msg.smsContent)
        maps.put("datetime",msg.datetime)
        maps.put("mobile",msg.phone)
        SystemLog.log(maps.toString())
        RetrofitUtil.getInstance().userService()
            .submitSms(getCommonMaps(maps))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Result<User>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(result: Result<User>) {
                    SystemLog.log(result.toString())
                    if (result.code== Constants.API_OK){
//                        ToastUtils.showShort(result.msg)
                    }else{
                        if (result.code==Constants.API_TIPS){
                            ToastUtils.showShort(result.msg)
                            playMp3()
                        }else if (result.code == Constants.API_USER_ERROR){
                            ActivityUtils.finishAllActivitiesExceptNewest()
                            SPUtils.getInstance().remove(Constants.LOGINED_STATUS)
                            SPUtils.getInstance().remove(Constants.LOGINED_NICKNAME)
                            SPUtils.getInstance().remove(Constants.LOGINED_TOKEN)
                            ActivityUtils.startActivity(LoginActivity::class.java)
                            activity?.let {
                                it.finish()
                            }
                        }


                    }
                }

                override fun onError(e: Throwable) {
                    SystemLog.log("${e.message}")
                }
            })

    }


    override fun onDestroy() {
        super.onDestroy()
        cancelSmsObserver()
    }


    private fun playMp3(){
        activity?.let {
            var player = MediaPlayer()
            val assetManager: AssetManager = it.assets
            try {
                val fileDescriptor =
                    assetManager.openFd("mongo.mp3")
                player.setDataSource(
                    fileDescriptor.fileDescriptor,
                    fileDescriptor.startOffset,
                    fileDescriptor.length
                )
                player.prepare()
                player.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}