package com.laka.shoppingchat.mvp.user

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import com.alibaba.baichuan.trade.biz.login.AlibcLogin
import com.laka.androidlib.util.BaseActivityNavigator
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.main.constant.HomeApiConstant
import com.laka.shoppingchat.mvp.main.constant.HomeConstant
import com.laka.shoppingchat.mvp.nim.activity.GeneralActivity
import com.laka.shoppingchat.mvp.nim.activity.MessageSettingActivity
import com.laka.shoppingchat.mvp.nim.activity.NimSettingActivity
import com.laka.shoppingchat.mvp.user.constant.UserCenterConstant
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.laka.shoppingchat.mvp.user.view.activity.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

/**
 * @Author:summer
 * @Date:2019/9/9
 * @Description:
 */
object UserCenterModuleNavigator {

    private fun loginHandle(context: Context): Boolean {
        return if (!UserUtils.isLogin()) {
            LoginModuleNavigator.startLoginActivity(context)
            false
        } else {
            true
        }
    }

    fun startLoginActivity(context: Context) {
        loginHandle(context)
    }

    fun startUserSettingActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<UserSettingActivity>()
        }
    }

    fun startGeneralActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<GeneralActivity>()
        }
    }

    fun startNimSettingActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<NimSettingActivity>()
        }
    }

    fun startMessageSettingActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<MessageSettingActivity>()
        }
    }

    fun startModifyNameActivity(context: Context, originName: String) {
        if (loginHandle(context)) {
            context.startActivity<ModifyNameActivity>(
                "nickname" to originName
            )
        }
    }

    fun startModifyPhoneActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<ModifyPhoneActivity>()
        }
    }

    fun startModifySexActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<ModifySexActivity>()
        }
    }

    fun startOperationPayPsdActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<OperationPayPsdActivity>()
        }
    }

    fun startForgetPayPsdActivity(context: Context, verificationCode: String) {
        if (loginHandle(context)) {
            context.startActivity<ForgetPayPsdActivity>(
                UserCenterConstant.FORGET_CODE to verificationCode
            )
        }
    }

    fun startPrivacyPolicyActivity(context: Context, url: String) {
        if (loginHandle(context)) {
            context.startActivity<PrivacyPolicyActivity>(
                HomeConstant.WEB_URL to url,
                HomeConstant.WEB_TITLE to "隐私政策"
            )
        }
    }

    fun startPrivacyPolicyActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<PrivacyPolicyActivity>(
                HomeConstant.WEB_URL to HomeApiConstant.URL_USER_PRIVACY_POLICY,
                HomeConstant.WEB_TITLE to "隐私政策"
            )
        }
    }

    fun startComplaintListActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<ComplaintListActivity>()
        }
    }

    fun startComplaintDetailActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<ComplaintDetailActivity>()
        }
    }

    fun startAboutUsActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<AboutUsActivity>()
        }
    }

    fun startUserImInfoSettingActivity(context: Context, account: String) {
        if (loginHandle(context)) {
            context.startActivity<UserImInfoSettingActivity>(
                UserConstant.KEY_IM_ACCOUNT to account
            )
        }
    }

    fun startSettingRemarksActivity(context: Context, account: String, marks: String) {
        if (loginHandle(context)) {
            context.startActivity<SettingRemarksActivity>(
                UserConstant.KEY_IM_ACCOUNT to account,
                UserConstant.KEY_IM_MARKS to marks
            )
        }
    }

    fun startSettingRemarksActivityForResult(context: Activity, account: String, marks: String, requestCode: Int) {
        if (loginHandle(context)) {
            context.startActivityForResult<SettingRemarksActivity>(
                requestCode,
                UserConstant.KEY_IM_ACCOUNT to account,
                UserConstant.KEY_IM_MARKS to marks
            )
        }
    }


    /**进入淘宝联盟授权url页面*/
    fun startBindUnionCodeActivityForResult(context: Activity, url: String, code: Int) {
        if (loginHandle(context) && AlibcLogin.getInstance().isLogin) {
            val bundle = Bundle()
            bundle.putString(HomeConstant.WEB_URL, url)
            BaseActivityNavigator.startActivityForResult(context, BindUnionCodeWebActivity::class.java, bundle, code)
        }
    }

    /**在Fragment 中使用startActivityForResult ，不能用activity来调用，否则无法收到相应的回调，为了兼容这个问题，使用
     * 当前的fragment 来调用，所以扩展了此方法*/
    fun startBindUnionCodeActivityForResultOnFragment(context: Activity, fragment: Fragment, url: String, code: Int) {
        if (loginHandle(context) && AlibcLogin.getInstance().isLogin) {
            val bundle = Bundle()
            bundle.putString(HomeConstant.WEB_URL, url)
            BaseActivityNavigator.startActivityForResultOnFragment(
                context,
                fragment,
                BindUnionCodeWebActivity::class.java,
                bundle,
                code
            )
        }
    }

}