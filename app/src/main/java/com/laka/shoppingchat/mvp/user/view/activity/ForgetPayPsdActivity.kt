package com.laka.shoppingchat.mvp.user.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.MD5Utils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.SeparatedEditText
import com.laka.androidlib.widget.dialog.LoadingDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.extraDelegate
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.common.ext.toSign
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.view.activity.VerifiyCodeInputActivity
import com.laka.shoppingchat.mvp.user.constant.UserCenterConstant
import com.laka.shoppingchat.mvp.user.constract.UserSettingConstract
import com.laka.shoppingchat.mvp.user.presenter.UserSettingPresenter
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import kotlinx.android.synthetic.main.activity_operation_pay_psd.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

/**
 * @Author:summer
 * @Date:2019/9/10
 * @Description:
 */
class ForgetPayPsdActivity : BaseMvpActivity<String>(), UserSettingConstract.IUserSettingView {

    private lateinit var mPresenter: UserSettingPresenter
    var status = "0"//密码转态
    var mPassword = ""
    private lateinit var mLoadingDialog: LoadingDialog
    val oldPsd by extraDelegate(UserCenterConstant.OLD_PSW, "")
    val forgetCode by extraDelegate(UserCenterConstant.FORGET_CODE, "")
    override fun setContentView(): Int {
        return R.layout.activity_operation_pay_psd
    }

    override fun initIntent() {

    }

    override fun initViews() {
        tv_forget_psd.visibility = View.GONE
        if (oldPsd.isEmpty()) {
            //未设置密码，第一次进来的时候设置的转态
            tv_update_psd.text = "设置支付密码"
            tv_des.text = "请设置支付密码，用于支付验证"
            tv_forget_psd.visibility = View.GONE
            sb_finish.visibility = View.GONE
        } else {
            //未设置密码，第二次进来的时候设置的转态
            tv_update_psd.text = "设置支付密码"
            tv_des.text = "请再次填写以确认"
            tv_forget_psd.visibility = View.GONE
            sb_finish.visibility = View.VISIBLE
        }

    }

    override fun initData() {
        tv_cancel.setOnClickListener { finish() }
    }

    override fun initEvent() {
        input_psd.setTextChangedListener(object : SeparatedEditText.TextChangedListener {

            override fun textChanged(changeText: CharSequence) {
                mPassword = changeText.toString()
                sb_finish.setBgaColor("#9CE6BF")
            }

            override fun textCompleted(text: CharSequence) {
                sb_finish.setBgaColor("#07C160")
                mPassword = text.toString()
                if (oldPsd.isBlank()) {
                    sb_finish.setBgaColor("#07C160")
                    startActivityForResult<ForgetPayPsdActivity>(
                        0,
                        UserCenterConstant.OLD_PSW to mPassword,
                        UserCenterConstant.FORGET_CODE to forgetCode
                    )
                } else {

                }
            }

        })
        sb_finish.onClick {
            onFinish()
        }
    }


    private fun onFinish() {
        if (mPassword.equals(oldPsd)) {
            showLoading()
            mPresenter.forgetPsw(mPassword, forgetCode)
        } else {
            ToastHelper.showToast("两次密码输入不一样")
        }
    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun createPresenter(): IBasePresenter<*>? {
        mPresenter = UserSettingPresenter()
        return mPresenter
    }


    override fun forgetPswSuccess() {
        dismissLoading()
        ToastHelper.showToast("密码设置成功")
        UserUtils.updatePayPasswordStatus(true)
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showLoading() {
        if (!::mLoadingDialog.isInitialized) {
            mLoadingDialog = LoadingDialog(this)
            mLoadingDialog.setOnCancelListener {

            }
        }
        mLoadingDialog.show()
    }

    override fun dismissLoading() {
        if (::mLoadingDialog.isInitialized) {
            mLoadingDialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        finish()
    }
}