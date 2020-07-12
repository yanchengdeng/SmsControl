package com.dyc.smscontrol.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.Result
import com.dyc.smscontrol.entity.User
import com.dyc.smscontrol.http.RetrofitUtil
import com.dyc.smscontrol.ui.BankListActivity
import com.dyc.smscontrol.ui.LoginActivity
import com.dyc.smscontrol.utils.SystemLog
import com.dyc.smscontrol.utils.SystemLog.Companion.getCommonMaps
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_me.*

class MineFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tv_user_name.text = SPUtils.getInstance().getString(Constants.LOGINED_NICKNAME)

        tv_banks_list.setOnClickListener {
            ActivityUtils.startActivity(BankListActivity::class.java)
        }

        tv_login_out.setOnClickListener {
            activity?.let {
                MaterialDialog(it).show {
                    title(text = "温馨提示")
                    message(text = "确定要退出登录吗？")
                    positiveButton(R.string.sure) { dialog ->
                        doLoginOut(dialog)
                    }
                    negativeButton(R.string.cancel) { dialog ->
                       dismiss()
                    }
                }
            }
        }

    }

    /***
     * 退出登录
     */
    private fun doLoginOut(dialog: MaterialDialog) {
        val maps = HashMap<String,String>()
        RetrofitUtil.getInstance().userService()
            .loginOut(getCommonMaps(maps))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Result<User>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(result: Result<User>) {
                    SystemLog.log(result.toString())
                    if (result.code== Constants.API_OK ){
                        SPUtils.getInstance().remove(Constants.LOGINED_STATUS)
                        SPUtils.getInstance().remove(Constants.LOGINED_NICKNAME)
                        SPUtils.getInstance().remove(Constants.LOGINED_TOKEN)
                        ActivityUtils.startActivity(LoginActivity::class.java)
                        dialog.dismiss()
                        activity?.finish()
                    }else{
                        ToastUtils.showShort(result.msg)
                    }
                }

                override fun onError(e: Throwable) {
                    SystemLog.log("${e.message}")
                }
            })

    }
}