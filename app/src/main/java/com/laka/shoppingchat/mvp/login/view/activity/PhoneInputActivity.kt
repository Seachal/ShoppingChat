package com.laka.shoppingchat.mvp.login.view.activity

import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.MultiClickUtil
import com.laka.androidlib.util.SPHelper
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.contract.IPhoneLoginContract
import com.laka.shoppingchat.mvp.login.model.bean.UserBean
import com.laka.shoppingchat.mvp.login.model.bean.VerificationCodeDataBean
import com.laka.shoppingchat.mvp.login.presenter.PhoneLoginPresenter
import com.laka.shoppingchat.mvp.main.constant.HomeApiConstant
import com.laka.shoppingchat.mvp.main.constant.HomeConstant
import com.laka.shoppingchat.mvp.user.view.activity.PrivacyPolicyActivity
import kotlinx.android.synthetic.main.activity_phone_input.*
import org.jetbrains.anko.startActivity

/**
 * @Author:summer
 * @Date:2019/3/7
 * @Description:手机号输入（购聊项目，只有手机号输入等着一种登录方式）
 */
class PhoneInputActivity : BaseMvpActivity<UserBean>(), IPhoneLoginContract.ILoginView {

    private var mPhoneNumber: String = ""
    private lateinit var mPresenter: IPhoneLoginContract.ILoginPresenter

    override fun createPresenter(): IBasePresenter<*>? {
        mPresenter = PhoneLoginPresenter()
        return mPresenter
    }

    override fun setContentView(): Int {
        return R.layout.activity_phone_input
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_theme_bg), 0)
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun initIntent() {

    }

    override fun initViews() {

    }

    override fun initData() {
    }

    override fun initEvent() {
        btn_confirm_phone_code.setOnClickListener { onGetVerificationCode() }
        btn_confirm_phone_code.isEnabled = false
        iv_back.setOnClickListener { finish() }
        et_phone_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(et_phone_input.text.toString())) {
                    iv_txt_remove.visibility = View.GONE
                } else {
                    iv_txt_remove.visibility = View.VISIBLE
                }
                if (et_phone_input.text.toString().length == 11) {
                    btn_confirm_phone_code.isEnabled = true
                    btn_confirm_phone_code.setBackGroundColor(R.color.color_07C160)
                } else {
                    btn_confirm_phone_code.isEnabled = false
                }
            }
        })
        iv_txt_remove.setOnClickListener {
            et_phone_input.setText("")
        }
        tv_agreement.onClick {
            startActivity<PrivacyPolicyActivity>(
                HomeConstant.WEB_URL to HomeApiConstant.URL_USER_PRIVACY_POLICY,
                HomeConstant.WEB_TITLE to "隐私政策"
            )
        }
    }

    /**获取验证码*/
    private fun onGetVerificationCode() {
        if (!MultiClickUtil.checkClickValid(R.id.btn_confirm_phone_code)) {
            return
        }
        mPhoneNumber = et_phone_input?.text.toString().trim()
        if (TextUtils.isEmpty(mPhoneNumber)) {
            ToastHelper.showCenterToast("请输入手机号码")
            return
        }
        if (mPhoneNumber.length != 11) {
            ToastHelper.showCenterToast("请输入正确的手机号")
            return
        }
        mPresenter.onGetVerificationCode(
            mPhoneNumber,
            LoginConstant.VERIFICATION_TYPE_LOGIN
        )
    }

    //===================================== V层接口实现 =========================================

    override fun onGetVerificationCodeSuccess(verificationCode: VerificationCodeDataBean) {
        ToastHelper.showCenterToast("获取验证码成功")
        SPHelper.putString(LoginConstant.PHONE_NUMBER, mPhoneNumber)
        SPHelper.putLong(LoginConstant.PRE_GET_VERIFICATION_CODE_TIME, System.currentTimeMillis())
        LoginModuleNavigator.startVerificationCodeInputActivity(this, mPhoneNumber)
        finish()
    }

    //获取验证码太频繁
    override fun onGetVerificationCodeFrequently() {
        val bundle = Bundle()
        bundle.putString(LoginConstant.PHONE, mPhoneNumber)
        LoginModuleNavigator.startVerificationCodeInputActivity(this, mPhoneNumber)
        finish()
    }

    override fun showData(data: UserBean) {

    }

    override fun showErrorMsg(msg: String?) {

    }

}