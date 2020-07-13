package com.dyc.smscontrol.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.BankItem
import com.dyc.smscontrol.entity.Msg
import com.dyc.smscontrol.entity.Result
import com.dyc.smscontrol.entity.User
import com.dyc.smscontrol.http.RetrofitUtil
import com.dyc.smscontrol.utils.SystemLog
import com.dyc.smscontrol.utils.SystemLog.Companion.getCommonMaps
import com.kaopiz.kprogresshud.KProgressHUD
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bank_list.*
import kotlinx.android.synthetic.main.activity_bank_list.recycle_view
import kotlinx.android.synthetic.main.common_header.*
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * author : yanc
 * data : 2020/7/9
 * time : 0:05
 * desc : 银行卡列表
 */
class BankListActivity : AppCompatActivity() {

    private val adapter = BankAdapter(R.layout.adapter_bank, mutableListOf())
    lateinit var progressBar : KProgressHUD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_list)


        //   返回键
        iv_back.setOnClickListener {
            finish()
        }

        //提交银行卡验证
        tv_vertify_cards.setOnClickListener {
            if (adapter.data.any { it.isSelected }) {
                vertifyCards(adapter.data.filter { it.isSelected })
            }else{
                ToastUtils.showShort("请选择银行卡")
            }
        }


        //选择银行卡
//        tv_function.setOnClickListener {
//
//        }

        tv_title.text = getString(R.string.vertify_banks)
//        tv_function.visibility = View.VISIBLE

        progressBar = KProgressHUD.create(this).setLabel("加载中...")
        recycle_view.layoutManager = LinearLayoutManager(this)
        recycle_view.addItemDecoration(SystemLog.getRecycleDiv(this))
        recycle_view.adapter = adapter


        //选择验证的卡
        adapter.setOnItemClickListener { _, view, position ->
            adapter.data[position].isSelected =  !adapter.data[position].isSelected
            adapter.notifyDataSetChanged()
        }

        getBanks()
    }

    /**
     * 验证银行卡
     * bankcardIds=1,2,3
     */
    private fun vertifyCards(filter: List<BankItem>) {
        progressBar.setLabel("验证中...").show()
        val maps =  HashMap<String,String>()
        var ids  = java.lang.StringBuilder()
        filter.forEachIndexed { index, bankItem ->
            if (index < filter.size-1){
                ids.append(bankItem.id).append(",")
            }else{
                ids.append(bankItem.id)
            }
        }

        maps.put("bankcardIds",ids.toString())
        RetrofitUtil.getInstance().userService()
            .vertifyBankCards(getCommonMaps( maps))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Result<User>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(result: Result<User>) {
                    SystemLog.log(result.toString())
                    progressBar.dismiss()
                    if (result.code== Constants.API_OK ){
                        getBanks()
                        ToastUtils.showShort(result.msg)
                    }else{
                        ToastUtils.showShort(result.msg)
                    }
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    ToastUtils.showShort("${e.message}")

                }
            })

    }

    private fun getBanks() {
        progressBar.show()
        val maps =  HashMap<String,String>()
        RetrofitUtil.getInstance().userService()
            .bankList(getCommonMaps( maps))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Result<List<BankItem>>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(result: Result<List<BankItem>>) {
                    SystemLog.log(result.toString())
                    progressBar.dismiss()
                    if (result.code== Constants.API_OK ){
                        adapter.setList(result.data)
                    }
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    SystemLog.log("${e.message}")

                }
            })
    }
}