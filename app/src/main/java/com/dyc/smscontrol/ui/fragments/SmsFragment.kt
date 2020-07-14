package com.dyc.smscontrol.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
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
        if (msg.obj is Msg ){
            SystemLog.log(msg.data.toString())
            if (SPUtils.getInstance().getString(Constants.CARDS_ID).isNotEmpty()) {
                Snackbar.make(ll_root, (msg.obj as Msg).smsContent, Snackbar.LENGTH_SHORT).show()
                uploadMsg(msg.obj as Msg)
            }
        }
        false
    })



    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        SystemLog.log("---onActivityCreated")
        val permissions = RxPermissions(this)
        permissions
            .request(Manifest.permission.READ_SMS)
            .subscribe { granted ->
                if (granted) {
                    //未选择卡
                    // 1.提示服务未开启  2. 监听按钮处 改为：请选择监听银行卡
                    if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CARDS_ID))){
                        initNoListener()
                    }else{
                        //未选择卡
                        // 1.提示服务已开启...  2. 监听按钮处 改为：关闭监听更改银行卡  3显示银行卡信息
                        text_home.text = getString(R.string.opening_sms_listener)
                        btn_change_cards.text = getString(R.string.close_listener_change_bank)
                        tv_banks_name.text = SPUtils.getInstance().getString(Constants.CARDS_NAME)
                    }
                    addSmsObserver()
                } else {
                    text_home.text = getString(R.string.no_sms_permission)
                }
            }


        btn_sms_record.setOnClickListener {
            ToastUtils.showShort(getString(R.string.waiting_open))
        }

        btn_change_cards.setOnClickListener {
            //更改监听卡信息需要先关闭短信服务
            activity?.let {
//                MaterialDialog(it).show {
//                    title(text = "温馨提示")
//                    message(text = "更改监听卡信息需要先关闭短信服务")
//                    positiveButton(R.string.sure) { dialog ->
//                        cancelSmsObserver()
//                        ActivityUtils.startActivity(BankListActivity::class.java)
//                        dismiss()
//                        it.finish()
//                    }
//                    negativeButton(R.string.cancel) { dialog ->
//                        dismiss()
//                    }
//                }
                cancelSmsObserver()
                if (SPUtils.getInstance().getString(Constants.CARDS_ID).isNotEmpty()) {
                    SPUtils.getInstance().remove(Constants.CARDS_ID)
                    SPUtils.getInstance().remove(Constants.CARDS_NAME)
                    initNoListener()
                }
                ActivityUtils.startActivity(BankListActivity::class.java)
            }
        }
    }

    //初始化 无监听状态显示
    private fun initNoListener() {
        text_home.text = getString(R.string.not_open_sms_listener)
        btn_change_cards.text = getString(R.string.choose_banks_for_listening)
        tv_banks_name.text = SPUtils.getInstance().getString(Constants.CARDS_NAME)
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
            SystemLog.log(msg = "启动信息监听失败-------------------")
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
            SystemLog.log(msg = "关闭信息监听失败-------------------")
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SystemLog.log("---onCreateView")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemLog.log("---onCreate")
    }

    override fun onAttach(context: Context) {
        SystemLog.log("---onAttach")
        super.onAttach(context)
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