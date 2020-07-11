package com.dyc.smscontrol.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.Msg
import com.dyc.smscontrol.entity.Result
import com.dyc.smscontrol.entity.User
import com.dyc.smscontrol.http.RetrofitUtil
import com.dyc.smscontrol.ui.MessageAdapter
import com.dyc.smscontrol.utils.SystemLog
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions2.RxPermissions
import deng.yc.baseutils.SmsContentObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception

class HomeFragment : Fragment() {
    private var smsContentObserver: SmsContentObserver? = null
    private var page = 1
    private var pageSize = 15
    private val adadpter = MessageAdapter(R.layout.adapter_msg, mutableListOf())
    private val myHandler : Handler = Handler(Handler.Callback { msg ->
        if (msg.obj is Msg){
            Snackbar.make(recycle_view,(msg.obj as Msg).smsContent,Snackbar.LENGTH_SHORT).show()
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
                } else {
                    text_home.text = "服务未开启，请打开短信权限"
                }
            }

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



        getMessages()

        //短信数据列表
        activity?.let {
            recycle_view.layoutManager = LinearLayoutManager(it)
            recycle_view.addItemDecoration(SystemLog.getRecycleDiv(it.baseContext))
            recycle_view.adapter = adadpter
        }

        //下拉
        refresh.setOnRefreshListener {
            refresh.isRefreshing = true
            page =1
            getMessages()
        }

        //上拉加载更多

        adadpter.loadMoreModule.setOnLoadMoreListener {
            page++
            getMessages()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }



    private fun getMessages(){
        RetrofitUtil.getInstance().userService()
            .smsList(page.toString(),pageSize.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Result<List<Msg>>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(result: Result<List<Msg>>) {
                    SystemLog.log(result.toString())
                    refresh.isRefreshing = false
                    if (result.code== Constants.API_OK ){

                    }else{
                        ToastUtils.showShort(result.msg)
                    }
                }

                override fun onError(e: Throwable) {
                    SystemLog.log("${e.message}")

                    if (page==1) {
                        adadpter.setNewInstance(testMsgs())
                        adadpter.loadMoreModule.loadMoreComplete()
                    }else {
                        val datas = if (page>3) testMsgs(5) else testMsgs()
                        adadpter.data = datas
                        if (datas.size== pageSize){
                            adadpter.loadMoreModule.loadMoreComplete()
                        }else{
                            adadpter.loadMoreModule.loadMoreComplete()
                            adadpter.loadMoreModule.loadMoreEnd()
                        }
                    }
                    refresh.isRefreshing = false
                }
            })
    }

    private fun  testMsgs(index :Int = 15) : MutableList<Msg>{
         val lists = mutableListOf<Msg>()
        for (i in 1..index){
            lists.add(Msg(phone = "111110000",id = "$i",datetime = "2010-23-23 22:32",smsContent = "短信消息",status = 1,remark = "备注是啥"))
        }
        return lists
    }

    /**
     * 上传信息
     * BankcardIds=1,2,3&smsContent=短信内容
     */
    private fun uploadMsg(msg: Msg) {
        val maps = HashMap<String,String>()
        val timeTemp = System.currentTimeMillis()/1000
        maps.put("time",timeTemp.toString())
        maps.put("BankcardIds","1")
        maps.put("smsContent",timeTemp.toString())
        SystemLog.log(maps.toString())
        RetrofitUtil.getInstance().userService()
            .submitSms(maps)
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

                    }else{
                        ToastUtils.showShort(result.msg)
                    }
                }

                override fun onError(e: Throwable) {
                    SystemLog.log("${e.message}")
                }
            })

    }


    override fun onDestroy() {
        super.onDestroy()

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
}