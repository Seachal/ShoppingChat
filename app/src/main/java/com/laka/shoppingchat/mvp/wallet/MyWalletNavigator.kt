package com.laka.shoppingchat.mvp.wallet

import android.app.Activity
import android.content.Context
import com.laka.androidlib.util.BaseActivityNavigator
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.laka.shoppingchat.mvp.wallet.view.activity.*

/**
 * @Author:summer
 * @Date:2019/9/9
 * @Description:
 */
object MyWalletNavigator {

    private fun loginHandle(context: Context): Boolean {
        return if (!UserUtils.isLogin()) {
            LoginModuleNavigator.startLoginActivity(context)
            false
        } else {
            true
        }
    }

    fun startMyWalletActivity(context: Context) {
        if (loginHandle(context)) {
            BaseActivityNavigator.startActivity(context, MyWalletActivity::class.java)
        }
    }

    fun startMyBankCardActivity(context: Context) {
        if (loginHandle(context)) {
            BaseActivityNavigator.startActivity(context, MyBankCardActivity::class.java)
        }
    }

    fun startMyPayPsdActivity(context: Context) {
        if (loginHandle(context)) {
            BaseActivityNavigator.startActivity(context, MyPayPsdActivity::class.java)
        }
    }

    fun startRedPackageListActivity(context: Context) {
        if (loginHandle(context)) {
            BaseActivityNavigator.startActivity(context, RedPackageListActivity::class.java)
        }
    }

    fun startRechargeActivityForResult(context: Activity, requestCode: Int) {
        if (loginHandle(context)) {
            BaseActivityNavigator.startActivityForResult(context, RechargeActivity::class.java, requestCode)
        }
    }

}