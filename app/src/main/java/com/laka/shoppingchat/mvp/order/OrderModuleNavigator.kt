package com.laka.shoppingchat.mvp.order

import android.content.Context
import com.laka.androidlib.util.BaseActivityNavigator
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.order.view.activitity.MyOrderActivity
import com.laka.shoppingchat.mvp.user.utils.UserUtils

/**
 * @Author:summer
 * @Date:2019/1/22
 * @Description:
 */
object OrderModuleNavigator {

    fun startOrderActivity(context: Context) {
        if (loginHandle(context)) {
            BaseActivityNavigator.startActivity(context, MyOrderActivity::class.java)
        }
    }

    private fun loginHandle(context: Context): Boolean {
        return if (!UserUtils.isLogin()) {
            LoginModuleNavigator.startLoginActivity(context)
            false
        } else {
            true
        }
    }

}