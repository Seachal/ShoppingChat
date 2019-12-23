package com.laka.shoppingchat.mvp.order.model.responsitory

import com.laka.androidlib.util.ApplicationUtils
import com.laka.androidlib.util.retrofit.RetrofitHelper
import com.laka.androidlib.util.rx.constant.ApiType
import com.laka.shoppingchat.BuildConfig
import com.laka.shoppingchat.mvp.model.responsitory.OrderService

/**
 * @Author:sming
 * @Date:2019/9/19
 * @Description:
 */
class OrderRetrofitHelper {

    // 双重验证同步锁单例模式
    companion object {

        val instance: OrderService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            userCenterService
        }

        private var userCenterService: OrderService =
            RetrofitHelper.getInstance(ApplicationUtils.getApplication())
                .setRequestHost(BuildConfig.SHOPPING_CHAT_HOST)
                .setNetWorkInterceptor(true)
                .setTokenRequest(true)
                .setApiType(ApiType.CUSTOM_API)
                .build()
                .create(OrderService::class.java)
    }
}