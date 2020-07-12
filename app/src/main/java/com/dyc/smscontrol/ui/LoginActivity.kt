package com.dyc.smscontrol.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
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
class LoginActivity : AppCompatActivity() {

    lateinit var progressBar : KProgressHUD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBar = KProgressHUD.create(this).setLabel("登录中...")

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
        SystemLog.log("rsa加密：$rsaStr")
//        val base64Bytes = Base64.encode(rsaStr.toByteArray(), Base64.DEFAULT)
//        val base64Str = String(base64Bytes)
//        SystemLog.log("base64加密：$base64Str")
//        val base64Decoder = Base64.decode(base64Bytes,Base64.DEFAULT)
//        SystemLog.log("base64解密：${String(base64Decoder)}")
        maps.put("passwd", rsaStr)
//        maps.put("passwd",password)
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
                        ActivityUtils.startActivity(MainActivity::class.java)
                        finish()
                    }else{
                        ToastUtils.showShort(result.msg)
                    }
                }

                override fun onError(e: Throwable) {
                    SystemLog.log("${e.message}")
                    progressBar.dismiss()
                }
            })


    }
}