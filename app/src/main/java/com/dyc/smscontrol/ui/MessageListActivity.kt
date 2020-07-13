package com.dyc.smscontrol.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.Msg
import com.dyc.smscontrol.entity.PageInfo
import com.dyc.smscontrol.entity.Result
import com.dyc.smscontrol.http.RetrofitUtil
import com.dyc.smscontrol.utils.SystemLog
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_message_list.*

/**
 * 短信列表
 */
class MessageListActivity : BaseActivity() {
    private val pageInfo = PageInfo()
    private val adadpter = MessageAdapter(R.layout.adapter_msg, mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        recycle_view.layoutManager = LinearLayoutManager(this)
        recycle_view.addItemDecoration(SystemLog.getRecycleDiv(this))
        recycle_view.adapter = adadpter

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


    private fun getMessages(){
        RetrofitUtil.getInstance().userService()
            .smsList(pageInfo.page.toString(), Constants.PAGE_SIZE.toString())
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
                    }else{
                        dealApiCode(result.code,result.msg)
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
}