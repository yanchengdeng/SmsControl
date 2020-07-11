package com.dyc.smscontrol.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.dyc.smscontrol.Constants
import com.dyc.smscontrol.R
import com.dyc.smscontrol.ui.LoginActivity
import com.dyc.smscontrol.utils.SystemLog
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

        tv_login_out.setOnClickListener {
            activity?.let {
                MaterialDialog(it).show {
                    title(text = "温馨提示")
                    message(text = "确定要退出登录吗？")
                    positiveButton(R.string.sure) { dialog ->
                        SPUtils.getInstance().remove(Constants.LOGINED_STATUS)
                        SPUtils.getInstance().remove(Constants.LOGINED_NICKNAME)
                        SPUtils.getInstance().remove(Constants.LOGINED_TOKEN)
                        dismiss()
                        ActivityUtils.startActivity(LoginActivity::class.java)
                        it.finish()
                    }
                    negativeButton(R.string.cancel) { dialog ->
                       dismiss()
                    }
                }
            }


        }

    }
}