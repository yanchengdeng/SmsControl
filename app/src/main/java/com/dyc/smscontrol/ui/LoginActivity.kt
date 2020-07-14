package com.dyc.smscontrol.ui

import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.*
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.entity.Result
import com.dyc.smscontrol.entity.User
import com.dyc.smscontrol.http.RetrofitUtil
import com.dyc.smscontrol.utils.Rsa
import com.dyc.smscontrol.utils.SystemLog
import com.kaopiz.kprogresshud.KProgressHUD
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
class LoginActivity : BaseActivity() {

    lateinit var progressBar : KProgressHUD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBar = KProgressHUD.create(this).setLabel("登录中...")

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.SAVE_BASE_URL))){
            et_api.setText(SPUtils.getInstance().getString(Constants.SAVE_BASE_URL).dropLast(1))
        }

        btn_login.setOnClickListener {


            if (TextUtils.isEmpty(et_api.editableText.toString())){
                ToastUtils.showShort(getString(R.string.please_input_api))
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(et_account.editableText.toString())){
                ToastUtils.showShort(getString(R.string.please_input_account))
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(et_pwd.editableText.toString())){
                ToastUtils.showShort(getString(R.string.please_input_pwd))
                return@setOnClickListener
            }

            val baseAPI = "${et_api.editableText.toString().trim()}/"

            //每次登录 重置 baseurl
            //保存api 地址至本地
            SPUtils.getInstance().put(Constants.SAVE_BASE_URL,baseAPI)
            RetrofitUtil.getInstance().resetRetrofit()
            KeyboardUtils.hideSoftInput(this)
            doLoginAction(et_account.editableText.toString(),et_pwd.editableText.toString())

        }
    }

    /**
     * username=q10000&passwd=rsa公钥加密字符串&time=请求当前时间戳秒级
     * 登录
     */
    private fun doLoginAction(account: String, password: String) {
        progressBar.show()

        val maps = HashMap<String,String>()
        val timeTemp = System.currentTimeMillis()/1000
        maps.put("username",account)
//        val rsaStr = EncryptionUtils.encrypt("$timeTemp$password", EncryptionUtils.publicKeyString)
        val rsaStr = Rsa().encryptByPublicKey("$timeTemp$password")
        maps.put("passwd", rsaStr)
        maps.put("time",timeTemp.toString())
        SystemLog.log(maps.toString())
        RetrofitUtil.getInstance().userService()
            .login(maps)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object :Observer<Result<User>>{
                override fun onComplete() {
                    progressBar.dismiss()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(result: Result<User>) {
                    SystemLog.log(result.toString())
                    progressBar.dismiss()
                    if (result.code== Constants.API_OK && !TextUtils.isEmpty(result.data.uid)){
                        SPUtils.getInstance().put(Constants.LOGINED_STATUS,true)
                        SPUtils.getInstance().put(Constants.LOGINED_NICKNAME,account)
                        SPUtils.getInstance().put(Constants.LOGINED_TOKEN,result.data.uid)
                        SPUtils.getInstance().remove(Constants.CARDS_ID)
                        SPUtils.getInstance().remove(Constants.CARDS_NAME)
                        var bundle = Bundle()
                        bundle.putBoolean(Constants.IS_FROM_LOGIN,true)
                        ActivityUtils.startActivity(bundle,BankListActivity::class.java)
                        finish()
                    }else{
                        dealApiCode(result.code,result.msg)
                    }
                }

                override fun onError(e: Throwable) {
                    SystemLog.log("${e.message}")
                    ToastUtils.showShort("请检查输入地址")
                    progressBar.dismiss()
                }
            })
    }
}