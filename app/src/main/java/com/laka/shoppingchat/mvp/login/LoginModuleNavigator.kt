package com.laka.shoppingchat.mvp.login


import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.laka.androidlib.util.BaseActivityNavigator
import com.laka.androidlib.util.SPHelper
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.view.activity.PhoneInputActivity
import com.laka.shoppingchat.mvp.login.view.activity.ScanQRCodeActivity
import com.laka.shoppingchat.mvp.login.view.activity.VerifiyCodeInputActivity
import com.laka.shoppingchat.mvp.user.utils.UserUtils

/**
 * @Author:Rayman
 * @Date:2018/12/19
 * @Description:HOME模块页面跳转类
 */
object LoginModuleNavigator {

    fun startLoginActivity(activity: Context) {
        val preTime = SPHelper.getLong(
            LoginConstant.PRE_GET_VERIFICATION_CODE_TIME,
            0
        )
        if (System.currentTimeMillis() - preTime > 1000 * 60) {
            startPhoneInputActivity(
                activity,
                LoginConstant.LOGIN_TYPE_PHONE
            )
        } else {//未超一分钟，则直接进入验证码页面
            val phone = SPHelper.getString(LoginConstant.PHONE_NUMBER, "")
            startVerificationCodeInputActivity(activity, phone, LoginConstant.VERIFICATION_TYPE_LOGIN, preTime)
        }
    }

    /**
     * 打开登录界面，不做验证码时间判断
     * */
    fun normalStartLoginActivity(activity: Context) {
        //清除上一次发验证码的时间和手机
        SPHelper.putLong(LoginConstant.PRE_GET_VERIFICATION_CODE_TIME, 0)
        SPHelper.putString(LoginConstant.PHONE_NUMBER, "")
        startPhoneInputActivity(
            activity,
            LoginConstant.LOGIN_TYPE_PHONE
        )
    }

    private fun startPhoneInputActivity(activity: Context, loginType: Int) {
        val bundle = Bundle()
        bundle.putInt(LoginConstant.LOGIN_TYPE, loginType)
        BaseActivityNavigator.startActivity(activity, PhoneInputActivity::class.java, bundle)
    }

    /**进入验证码输入页面*/
    fun startVerificationCodeInputActivity(
        activity: Context,
        phone: String,
        verifiyType: Int = LoginConstant.VERIFICATION_TYPE_LOGIN,
        preTime: Long = 0
    ) {
        val bundle = Bundle()
        bundle.putString(LoginConstant.PHONE, phone)
        bundle.putLong(LoginConstant.PRE_TIME, preTime)
        bundle.putInt(LoginConstant.VERIFIY_TYPE, verifiyType)
        BaseActivityNavigator.startActivity(
            activity,
            VerifiyCodeInputActivity::class.java,
            bundle
        )
    }

    fun startScanQRCodeActivityForResult(activity: Activity, requestCode: Int) {
        BaseActivityNavigator.startActivityForResult(
            activity,
            ScanQRCodeActivity::class.java,
            requestCode
        )
    }

    fun loginHandle(context: Context): Boolean {
        return if (!UserUtils.isLogin()) {
            startLoginActivity(context)
            false
        } else {
            true
        }
    }
}