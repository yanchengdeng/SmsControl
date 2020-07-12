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
import com.blankj.utilcode.util.ToastUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.Msg
import com.dyc.smscontrol.entity.PageInfo
import com.dyc.smscontrol.entity.Result
import com.dyc.smscontrol.entity.User
import com.dyc.smscontrol.http.RetrofitUtil
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

/**
 * author : yanc
 * data : 2020/7/12
 * time : 16:09
 * desc : 短信监听功能
 */
class SmsFragment : Fragment() {
    private var smsContentObserver: SmsContentObserver? = null
    private val pageInfo = PageInfo()
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





        //短信数据列表
        activity?.let {
            recycle_view.layoutManager = LinearLayoutManager(it)
            recycle_view.addItemDecoration(SystemLog.getRecycleDiv(it.baseContext))
            recycle_view.adapter = adadpter
        }

        // 进入页面，刷新数据
        refresh.isRefreshing = true
        getMessages()

        //下拉
        refresh.setOnRefreshListener {
            refresh.isRefreshing = true
            pageInfo.reset()
            refresh()
        }

        //上拉加载更多

        adadpter.loadMoreModule.setOnLoadMoreListener {
            loadMore()
        }
    }


    /**
     * 刷新
     */
    private fun refresh() {
        // 这里的作用是防止下拉刷新的时候还可以上拉加载
        adadpter.loadMoreModule.isEnableLoadMore = false
        // 下拉刷新，需要重置页数
        pageInfo.reset()
        getMessages()
    }

    /**
     * 加载更多
     */
    private fun loadMore() {
        getMessages()
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
            .smsList(pageInfo.page.toString(),Constants.PAGE_SIZE.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Result<List<Msg>>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(result: Result<List<Msg>>) {
                    SystemLog.log(result.toString())
                    if (result.code== Constants.API_OK ){
                        loadSuccess(result.data)
                    }
                }

                override fun onError(e: Throwable) {
                    SystemLog.log("${e.message}")
                        loadError()
                }
            })
    }


    private fun loadSuccess(data: List<Msg>) {
        refresh.isRefreshing  =false
        adadpter.loadMoreModule.isEnableLoadMore = true

        if (pageInfo.isFirstPage) {
            //如果是加载的第一页数据，用 setData()
            adadpter.setList(data)
        } else {
            //不是第一页，则用add
            adadpter.addData(data)
        }

        if (data.size < Constants.PAGE_SIZE) {
            //如果不够一页,显示没有更多数据布局
            adadpter.loadMoreModule.loadMoreEnd()
        } else {
            adadpter.loadMoreModule.loadMoreComplete()
        }

        // page加一
        pageInfo.nextPage()
    }

    private fun loadError(){
        refresh.isRefreshing = false
        adadpter.loadMoreModule.isEnableLoadMore = true
        adadpter.loadMoreModule.loadMoreFail()
    }



//    private fun  testMsgs(index :Int = 15) : MutableList<Msg>{
//         val lists = mutableListOf<Msg>()
//        for (i in 1..index){
//            lists.add(Msg(phone = "111110000",id = "$i",datetime = "2010-23-23 22:32",smsContent = "短信消息",status = 1,remark = "备注是啥"))
//        }
//        return lists
//    }

    /**
     * 上传信息
     * bankcardIds=1,2,3&smsContent=短信内容&datetime=短信接收时间&mobile=短信发送手机号
     */
    private fun uploadMsg(msg: Msg) {
        val maps = HashMap<String,String>()
        maps.put("bankcardIds","1")
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
                        ToastUtils.showShort(result.msg)
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