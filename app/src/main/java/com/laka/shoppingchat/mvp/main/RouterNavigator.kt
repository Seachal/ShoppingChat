package com.laka.shoppingchat.mvp.main

import android.app.Activity
import android.content.Context
import com.laka.androidlib.util.LogUtils
import com.laka.shoppingchat.mvp.main.constant.HomeConstant
import com.laka.shoppingchat.mvp.main.constant.HomeNavigatorConstant
import com.laka.shoppingchat.mvp.main.constant.HomeNavigatorConstant.NAV_ACTIVITY_WEB
import com.laka.shoppingchat.mvp.main.constant.HomeNavigatorConstant.NAV_GOOD_DETAIL
import com.laka.shoppingchat.mvp.main.constant.HomeNavigatorConstant.NAV_MY_ORDER
import com.laka.shoppingchat.mvp.main.constant.HomeNavigatorConstant.NAV_PRODUCT_SPECIAL
import com.laka.shoppingchat.mvp.main.constant.HomeNavigatorConstant.NAV_TMALL_SUPERMARKET
import com.laka.shoppingchat.mvp.main.constant.HomeNavigatorConstant.NAV_WEB
import com.laka.shoppingchat.mvp.order.OrderModuleNavigator
import com.laka.shoppingchat.mvp.shop.ShopDetailModuleNavigator
import com.laka.shoppingchat.mvp.shop.constant.ShopDetailConstant
import com.laka.shoppingchat.mvp.tmall.TmallModuleNavigator

/**
 * @Author:Rayman
 * @Date:2019/1/7
 * @Description:项目路由跳转类
 */
object RouterNavigator {

    /**
     * description:本地跳转映射表
     **/
    @JvmField
    open val bannerRouterReflectMap = HashMap<Int, String>()

    init {
        //1：邀请好友，2：其他消息，3：我的订单，4：商品详情，5：H5链接，6：补贴消息，7：IM推送，8：商品专题，9：天猫超市&聚划算等H5页面，10:0元购，11：活动页h5跳转
        //12：活动产品专题，13：
        bannerRouterReflectMap[1] = HomeNavigatorConstant.NAV_INVITATION
        bannerRouterReflectMap[2] = HomeNavigatorConstant.NAV_OTHER_MESSAGE
        bannerRouterReflectMap[3] = HomeNavigatorConstant.NAV_MY_ORDER
        bannerRouterReflectMap[4] = HomeNavigatorConstant.NAV_GOOD_DETAIL
        bannerRouterReflectMap[5] = HomeNavigatorConstant.NAV_WEB
        bannerRouterReflectMap[6] = HomeNavigatorConstant.NAV_COMMISSION_MESSAGE
        bannerRouterReflectMap[7] = HomeNavigatorConstant.NAV_IM_PUSH_MESSAGE
        bannerRouterReflectMap[8] = HomeNavigatorConstant.NAV_PRODUCT_SPECIAL
        bannerRouterReflectMap[9] = HomeNavigatorConstant.NAV_TMALL_SUPERMARKET
        bannerRouterReflectMap[10] = HomeNavigatorConstant.NAV_FREE_ADMISSION
        bannerRouterReflectMap[11] = HomeNavigatorConstant.NAV_ACTIVITY_WEB
        bannerRouterReflectMap[12] = HomeNavigatorConstant.NAV_ACTIVITY_PRODUCT
    }

    /**
     * description:处理常规App内路由跳转
     * @param requestCode:有传该参数，则使用startActivityForResult
     **/
    @JvmStatic
    fun handleAppInternalNavigator(
        context: Context,
        target: String,
        params: Map<String, String>,
        requestCode: Int = -1
    ) {
        when (target) {
            NAV_WEB -> {
                // 解析出params的数据
                var title = ""
                if (params.containsKey(HomeConstant.WEB_TITLE)) {
                    title = params[HomeConstant.WEB_TITLE].toString()
                }

                if (params.containsKey(HomeConstant.TITLE)) {
                    title = params[HomeConstant.TITLE].toString()
                }

                var url = ""
                if (params.containsKey(HomeConstant.WEB_URL)) {
                    url = params[HomeConstant.WEB_URL].toString()
                }

                // 假若只存在RouterValue
                if (params.containsKey(HomeNavigatorConstant.ROUTER_VALUE)) {
                    url = params[HomeNavigatorConstant.ROUTER_VALUE].toString()
                }

                if (requestCode != -1) {
                    HomeModuleNavigator.startWebActivityForResult(
                        context as Activity,
                        title,
                        url,
                        requestCode
                    )
                } else {
                    HomeModuleNavigator.startWebActivity(context, title, url)
                }
            }
            NAV_GOOD_DETAIL -> {
                var productId = ""
                if (params.containsKey(ShopDetailConstant.ITEM_ID)) {
                    productId = params[ShopDetailConstant.ITEM_ID].toString()
                }
                if (params.containsKey(HomeNavigatorConstant.ROUTER_VALUE)) {
                    productId = params[HomeNavigatorConstant.ROUTER_VALUE].toString()
                }
                LogUtils.info("输出productId：$productId")

                if (requestCode != -1) {
                    ShopDetailModuleNavigator.startShopDetailActivityForResult(
                        context as Activity,
                        productId,
                        requestCode
                    )
                } else {
                    ShopDetailModuleNavigator.startShopDetailActivity(context, productId)
                }
            }
            NAV_MY_ORDER -> {
                if (requestCode != -1) {
                    OrderModuleNavigator.startOrderActivity(context)
                } else {
                    OrderModuleNavigator.startOrderActivity(context)
                }
            }
            NAV_PRODUCT_SPECIAL -> { //商品专题
//                val title = "${params[HomeConstant.TITLE]}"
//                val topicId = "${params[HomeNavigatorConstant.ROUTER_VALUE]}"
//                val bigImageUrl = "${params[HomeConstant.TOPIC_BIG_IMAGE_URL]}"
//
//                if (requestCode != -1) {
//                    ShoppingModuleNavigator.startShoppingProductSpecialActivityForResult(
//                        context as Activity,
//                        title,
//                        topicId,
//                        bigImageUrl,
//                        requestCode
//                    )
//                } else {
//                    ShoppingModuleNavigator.startShoppingProductSpecialActivity(
//                        context,
//                        title,
//                        topicId,
//                        bigImageUrl
//                    )
//                }
            }
            NAV_TMALL_SUPERMARKET -> { //天猫超市&聚划算
                // 使用外部传入 webView 的方式打开
                val title = "${params[HomeConstant.TITLE]}"
                val url = "${params[HomeNavigatorConstant.ROUTER_VALUE]}"

                if (requestCode != -1) {
                    TmallModuleNavigator.startTallWebActivityForResult(
                        context as Activity,
                        title,
                        url,
                        requestCode
                    )
                } else {
                    TmallModuleNavigator.startTallWebActivity(context, title, url)
                }
            }

            NAV_ACTIVITY_WEB -> {  //活动H5页面
                val title = "${params[HomeConstant.TITLE]}"
                val url = "${params[HomeNavigatorConstant.ROUTER_VALUE]}"
                LogUtils.info("jpush---------url=$url")

//                if (requestCode != -1) {
//                    AdvertNavigator.startAdvertWebActivityForResult(context as Activity, title, url, requestCode)
//                } else {
//                    AdvertNavigator.startAdvertWebActivity(context, title, url)
//                }
            }
            else -> {
                // 假若不存在映射表中，不做任何跳转
            }
        }
    }

}