package com.laka.shoppingchat.mvp.share

import android.content.Context
import android.os.Bundle
import com.laka.androidlib.util.BaseActivityNavigator
import com.laka.shoppingchat.mvp.main.RouterNavigator
import com.laka.shoppingchat.mvp.main.constant.HomeConstant
import com.laka.shoppingchat.mvp.share.constant.ShareConstant
import com.laka.shoppingchat.mvp.share.model.bean.ShareResponse
import com.laka.shoppingchat.mvp.share.view.activity.ShopShareActivity

/**
 * @Author:summer
 * @Date:2019/5/27
 * @Description:
 */
object ShopShareModuleNavigator {

    fun startShopShareActivity(context: Context, shareResponse: ShareResponse) {
        val bundle = Bundle()
        bundle.putSerializable(ShareConstant.SHARE_DATA_FOR_WECHAT, shareResponse)
        BaseActivityNavigator.startActivity(context, ShopShareActivity::class.java, bundle)
    }

    fun startWechatMomentCourseWebActivity(context: Context, url: String, title: String) {
        val target = RouterNavigator.bannerRouterReflectMap[5] //H5链接
        val params = HashMap<String, String>()
        params[HomeConstant.WEB_TITLE] = title
        params[HomeConstant.WEB_URL] = url
        RouterNavigator.handleAppInternalNavigator(context, "$target", params)
    }

}