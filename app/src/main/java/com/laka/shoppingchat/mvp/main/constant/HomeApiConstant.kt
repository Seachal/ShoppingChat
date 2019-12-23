package com.laka.shoppingchat.mvp.main.constant

import com.laka.shoppingchat.BuildConfig
import com.laka.shoppingchat.mvp.main.model.bean.PerfixDetailUrl

/**
 * @Author:Rayman
 * @Date:2019/1/22
 * @Description:主页接口常量
 */
object HomeApiConstant {

    const val HOME_API_HOST = BuildConfig.ERGOU_BASE_HOST
    //购聊api host
    const val SHOPPING_CHAT_API_HOST:String = BuildConfig.SHOPPING_CHAT_HOST
    //天猫超市产品详情匹配地址
    var URL_TMALL_PREFIX_LIST = ArrayList<PerfixDetailUrl>()
    //微信分享教程H5页面
    var URL_WECHAT_MOMENT = "http://ergou-app.test.lm1314.xyz/shareIntro.html"
    var URL_USER_PRIVACY_POLICY = "https://user.api.njgutan.com/help/privacyPolicy.html"

    // APP更新
    const val API_UPDATE = "system/update"

    /**
     * description:参数定义
     **/
    const val VERSION = "version"
    const val APP_CHANNEL = "channel"
    const val PRODUCT_ID = "item_id"
    const val PLATFORM = "platform"
}