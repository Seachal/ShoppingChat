package com.laka.shoppingchat.mvp.login.view.activity

import android.os.Handler
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Base64
import android.view.View
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.KeyboardHelper
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.RsaUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.contract.ILoginContract
import com.laka.shoppingchat.mvp.login.model.bean.UserBean
import com.laka.shoppingchat.mvp.login.model.bean.VerificationCodeDataBean
import com.laka.shoppingchat.mvp.login.model.event.UserEvent
import com.laka.shoppingchat.mvp.login.presenter.LoginPresenter
import com.laka.shoppingchat.mvp.main.view.activity.MainActivity
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import kotlinx.android.synthetic.main.activity_verification_code.*

/**
 * @Author:summer
 * @Date:2019/3/7
 * @Description:验证码页面
 */
class VerifiyCodeInputActivity : BaseMvpActivity<UserBean>(), ILoginContract.ILoginView {


    private var mPhoneNumber: String = ""//手机号

    private var mPreTime: Long = 0 //上一次获取验证码的时间戳，默认0
    private var mVerifyType: Int = LoginConstant.VERIFICATION_TYPE_LOGIN //上一次获取验证码的时间戳，默认0
    private lateinit var mPresenter: ILoginContract.ILoginPresenter
    private var mCountDownMillisecond = 60
    private var mHandler = Handler()
    private var mRunnable = object : Runnable {
        override fun run() {
            if (mCountDownMillisecond <= 0) {
                mCountDownMillisecond = 60
                tv_change_phone.visibility = View.GONE
                tv_get_verification_code.text = "重新获取验证码"
                tv_get_verification_code.isEnabled = true
                tv_get_verification_code.setTextColor(
                    ContextCompat.getColor(
                        this@VerifiyCodeInputActivity,
                        R.color.color_main
                    )
                )
                mHandler.removeCallbacks(this)
                return
            }
            mCountDownMillisecond--
            tv_get_verification_code.text = "重新获取（${mCountDownMillisecond}s）"
            mHandler.postDelayed(this, 1000)
        }
    }

    override fun finish() {
        super.finish()
        mHandler.removeCallbacks(mRunnable)
    }

    override fun showErrorMsg(msg: String?) {
    }

    override fun createPresenter(): IBasePresenter<*> {
        mPresenter = LoginPresenter()
        return mPresenter
    }

    override fun setContentView(): Int {
        return R.layout.activity_verification_code
    }

    override fun initIntent() {
        mPhoneNumber = intent.extras.getString(LoginConstant.PHONE)
        mPreTime = intent.extras.getLong(LoginConstant.PRE_TIME, 0)
        mVerifyType = intent.extras.getInt(LoginConstant.VERIFIY_TYPE, LoginConstant.VERIFICATION_TYPE_LOGIN)
    }

    override fun initViews() {
        val timeStamp = 1000 * 60 - (System.currentTimeMillis() - mPreTime)
        if (timeStamp > 0) {
            mCountDownMillisecond = (timeStamp / 1000).toInt()
        }
        tv_get_verification_code.text = "重新获取（${mCountDownMillisecond}s）"
        tv_get_verification_code.isEnabled = false
        val content = "购聊已将验证码已发送至手机 $mPhoneNumber"
        tv_alert_msg.text = content
        mHandler.postDelayed(mRunnable, 1000)
        if (mVerifyType == LoginConstant.VERIFICATION_TYPE_LOGIN) {
            tv_change_phone.visibility = View.VISIBLE
        }


    }

    override fun initData() {
        KeyboardHelper.openKeyBoard(this, et_verify.currEditText)
    }

    override fun initEvent() {
        cl_back.setOnClickListener { finish() }
        btn_confirm_verification_code.setOnClickListener { onSureVerificationCode() }
        tv_get_verification_code.setOnClickListener {
            //重新获取验证码
            mPresenter.onGetVerificationCode(
                mPhoneNumber,
                mVerifyType
            )
        }
        et_verify.setInputCompleteListener { _, content ->
            if (!TextUtils.isEmpty(content)) {
                onSureVerificationCode()
            }
        }
        tv_change_phone.setOnClickListener {
            LoginModuleNavigator.normalStartLoginActivity(this)
            finish()
        }
    }

    private fun onSureVerificationCode() {
        val verificationCode = et_verify.content
        if (TextUtils.isEmpty(verificationCode)) {
            ToastHelper.showCenterToast("请输入验证码")
            return
        }
        if (verificationCode.length != 4 && verificationCode.length != 6) {
            ToastHelper.showCenterToast("请输入正确的验证码")
            return
        }
        KeyboardHelper.hideKeyBoard(this, et_verify)
        when (mVerifyType) {
            LoginConstant.VERIFICATION_TYPE_LOGIN -> {
                //手机登录
                mPresenter.onPhoneLogin(
                    this,
                    mPhoneNumber,
                    verificationCode,
                    LoginConstant.LOGIN_TYPE_PHONE_LOGIN
                )
            }
            LoginConstant.VERIFICATION_FORGET_PAY -> {
                mPresenter.onCheckCode(verificationCode)
            }
        }
    }

    //======================================== V层接口实现 ========================================
    override fun showData(data: UserBean) {

    }

    override fun onCheckCode() {
        val verificationCode = et_verify.content
        UserCenterModuleNavigator.startForgetPayPsdActivity(this, verificationCode)
        finish()
    }


    override fun onGetVerificationCodeSuccess(result: VerificationCodeDataBean) {
        ToastHelper.showCenterToast("获取验证码成功")
        tv_change_phone.visibility = View.VISIBLE
        tv_get_verification_code.text = "重新获取（${mCountDownMillisecond}s）"
        tv_get_verification_code.isEnabled = false
        tv_get_verification_code.setTextColor(
            ContextCompat.getColor(
                this@VerifiyCodeInputActivity,
                R.color.color_aaaaaa
            )
        )
        mHandler.postDelayed(mRunnable, 1000)
    }

    //获取验证码太频繁
    override fun onGetVerificationCodeFrequently() {
        ToastHelper.showToast("获取验证码太频繁")
    }

    override fun onVerificationCodeFail(msg: String) {
        tv_verification_alert.visibility = View.VISIBLE
        tv_verification_alert.text = msg
    }

    override fun onPhoneLoginSuccess(userBean: UserBean) {
        val account = decrypt("${userBean.userImInfo.accid}")
        val token = decrypt("${userBean.userImInfo.token}")
        userBean.userImInfo.accid = account
        userBean.userImInfo.token = token
        LogUtils.info("login----------account=$account")
        LogUtils.info("login----------token=$token")
        UserUtils.getImAccount()
        mPresenter.onIMLogin(userBean)
    }

    //验证码错误
    override fun onPhoneLoginFail() {
        et_verify.clearContent()
        Handler().postDelayed({
            KeyboardHelper.openKeyBoard(this, et_verify.currEditText)
        }, 200)
    }

    private fun decrypt(origin: String): String {
        val inPrivate = resources.assets.open("rsa_public_key.pem")
        val publicKey = RsaUtils.loadPublicKey(inPrivate)
        // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
        val decryptByte = RsaUtils.decryptData(Base64.decode(origin, Base64.DEFAULT), publicKey)
        return String(decryptByte)
    }

    override fun onImLoginSuccess(userBean: UserBean) {
        EventBusManager.postEvent(UserEvent(UserConstant.LOGIN_EVENT))
        MainActivity.login(this, true)
        finish()
    }
}
