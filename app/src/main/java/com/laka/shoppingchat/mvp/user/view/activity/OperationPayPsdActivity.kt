package com.laka.shoppingchat.mvp.user.view.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.SeparatedEditText
import com.laka.androidlib.widget.dialog.LoadingDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.extraDelegate
import com.laka.shoppingchat.common.ext.onClick
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
class OperationPayPsdActivity : BaseMvpActivity<String>(), UserSettingConstract.IUserSettingView {

    private lateinit var mPresenter: UserSettingPresenter
    var status = "0"//密码转态
    var mPassword = ""
    private lateinit var mLoadingDialog: LoadingDialog
    val oldPsd by extraDelegate(UserCenterConstant.OLD_PSW, "")
    val newPsd by extraDelegate(UserCenterConstant.NEW_PSW, "")
    override fun setContentView(): Int {
        return R.layout.activity_operation_pay_psd
    }

    override fun initIntent() {
//        PayPsdInputDialog(this)
    }

    override fun initViews() {
        status = UserUtils.getPayPasswordStatus()
        if (status == "0") {
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
        } else {
            if (oldPsd.isEmpty()) {
                //已经设置过密码第一次进来
                tv_update_psd.text = "修改支付密码"
                tv_des.text = "请输入支付密码，以验证身份"
                tv_forget_psd.visibility = View.VISIBLE
                sb_finish.visibility = View.GONE
            } else {
                if (newPsd.isNotEmpty()) {
                    //已经设置过密码第二次进来
                    tv_update_psd.text = "设置新支付密码"
                    tv_des.text = "请再次填写以确认"
                    tv_forget_psd.visibility = View.GONE
                    sb_finish.visibility = View.VISIBLE
                } else {
                    //已经设置过密码第三次进来
                    tv_update_psd.text = "设置新支付密码"
                    tv_des.text = "请输入新支付密码"
                    tv_forget_psd.visibility = View.GONE
                    sb_finish.visibility = View.GONE
                }
            }

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
                if (oldPsd.isEmpty() && newPsd.isEmpty()) {
                    if (status == "0") {
                        //未设置密码，第一次进来后，密码输入完成跳转到第二个界面
                        startActivityForResult<OperationPayPsdActivity>(
                            0, UserCenterConstant.OLD_PSW to mPassword
                        )
                    } else {
                        //已设置密码，第一次进来后，密码输入完成检测密码是否正确，正确进入第二个界面
                        showLoading()
                        mPresenter.checkUpPaySuccess(mPassword)
                    }
                } else {
                    //已设置密码，检测密码正确后，输入新的密码界面
                    if (status != "0" && newPsd.isEmpty()) {
                        startActivityForResult<OperationPayPsdActivity>(
                            0,
                            UserCenterConstant.OLD_PSW to oldPsd,
                            UserCenterConstant.NEW_PSW to mPassword
                        )
                    }
                }
            }

        })
        sb_finish.onClick {
            onFinish()
        }
        tv_forget_psd.onClick {
            var mobile = UserUtils.getMobile()
            mPresenter.sendCode(mobile, "forgetpassword")
        }
    }

    override fun sendCodeSuccess() {
        ToastHelper.showToast("验证码发送成功")
        var mobile = UserUtils.getMobile()
        LoginModuleNavigator.startVerificationCodeInputActivity(
            this,
            mobile,
            LoginConstant.VERIFICATION_FORGET_PAY
        )
        finish()
    }

    private fun onFinish() {
        if (status == "0") {
            //未设置密码，第一次设置密码，按完成
            if (mPassword.equals(oldPsd)) {
                showLoading()
                mPresenter.settingPassword(mPassword)
            } else {
                ToastHelper.showToast("两次密码输入不一样")
            }
        } else {
            //第二次按密码
            if (newPsd.equals(mPassword)) {
                //对前后两次密码做检验
                if (mPassword.equals(oldPsd)) {
                    ToastHelper.showToast("旧密码与新密码不可一样")
                } else {
                    showLoading()
                    //修改密码
                    mPresenter.editPassword(oldPsd, mPassword)
                }
            } else {
                ToastHelper.showToast("两次密码输入不一样")
            }
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

    override fun checkUpPaySuccess() {
        finish()
        startActivity<OperationPayPsdActivity>(
            UserCenterConstant.OLD_PSW to mPassword
        )
    }

    override fun settingPasswordSuccess() {
        dismissLoading()
        if (status == "0") {
            ToastHelper.showToast("密码设置成功")
        } else {
            ToastHelper.showToast("密码修改成功")
        }
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