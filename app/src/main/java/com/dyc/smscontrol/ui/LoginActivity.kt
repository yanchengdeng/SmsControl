package com.dyc.smscontrol.ui

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.bigkoo.svprogresshud.SVProgressHUD
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.User
import com.dyc.smscontrol.entity.Result
import com.dyc.smscontrol.http.RetrofitUtil
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*


/**
 * author : yanc
 * data : 2020/7/9
 * time : 0:04
 * desc : 登录页
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener {
            if (TextUtils.isEmpty(et_account.editableText.toString())){
                ToastUtils.showShort(getString(R.string.please_input_account))
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(et_pwd.editableText.toString())){
                ToastUtils.showShort(getString(R.string.please_input_pwd))
                return@setOnClickListener
            }

            KeyboardUtils.hideSoftInput(this)
            doLoginAction(et_account.editableText.toString(),et_pwd.editableText.toString())
        }
    }

    /**
     * 登录
     */
    private fun doLoginAction(account: String, password: String) {
        SVProgressHUD(this).showWithStatus("登录中....")
        RetrofitUtil.getInstance().userService()
            .login(account,password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object :Observer<Result<User>>{
                override fun onComplete() {
                    SVProgressHUD(this@LoginActivity).dismissImmediately()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(result: Result<User>) {
                    SVProgressHUD(this@LoginActivity).dismissImmediately()
                    if (result.status== Constants.API_OK){
                        SPUtils.getInstance().put(Constants.LOGINED_STATUS,true)
                        //TODO  后续根据接口保存用户信息
                        ActivityUtils.startActivity(MainActivity::class.java)
                        finish()
                    }else{
                        ToastUtils.showShort(result.msg)
                    }
                }

                override fun onError(e: Throwable) {
                    SVProgressHUD(this@LoginActivity).dismissImmediately()
                    ToastUtils.showShort("登录失败")
                    //TODO  模拟 后期会删除
                    SPUtils.getInstance().put(Constants.LOGINED_STATUS,true)
                    ActivityUtils.startActivity(MainActivity::class.java)
                    finish()
                }

            })


    }
}