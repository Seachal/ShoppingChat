package com.laka.shoppingchat.mvp.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.laka.androidlib.util.BaseActivityNavigator
import com.laka.shoppingchat.mvp.main.constant.HomeConstant
import com.laka.shoppingchat.mvp.main.view.activity.X5WebActivity

/**
 * @Author:Rayman
 * @Date:2018/12/24
 * @Description:项目主程跳转类
 */
object HomeModuleNavigator {

    fun startWebActivity(activity: Context, title: String = "", url: String) {
        var bundle = Bundle()
        bundle.putString(HomeConstant.WEB_TITLE, title)
        bundle.putString(HomeConstant.WEB_URL, url)
        BaseActivityNavigator.startActivity(activity, X5WebActivity::class.java, bundle)
    }

    fun startWebActivityForResult(activity: Activity, title: String = "", url: String, requestCode: Int) {
        var bundle = Bundle()
        bundle.putString(HomeConstant.WEB_TITLE, title)
        bundle.putString(HomeConstant.WEB_URL, url)
        BaseActivityNavigator.startActivityForResult(activity, X5WebActivity::class.java, bundle, requestCode)
    }

}