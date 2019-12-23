package com.laka.shoppingchat.mvp.nim.model.repository

import com.laka.androidlib.util.ApplicationUtils
import com.laka.androidlib.util.retrofit.RetrofitHelper
import com.laka.androidlib.util.rx.constant.ApiType
import com.laka.shoppingchat.BuildConfig

/**
 * @Author:sming
 * @Date:2019/8/8
 * @Description:
 */
class NimRetrofixHelper {

    // 双重验证同步锁单例模式
    companion object {

        val instance: NimService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            mainShopService
        }

        private var mainShopService: NimService =
            RetrofitHelper.getInstance(ApplicationUtils.getApplication())
                .setRequestHost(BuildConfig.SHOPPING_CHAT_HOST)
                .setNetWorkInterceptor(true)
                .setTokenRequest(true)
                .setApiType(ApiType.CUSTOM_API)
                .build()
                .create(NimService::class.java)
    }

}