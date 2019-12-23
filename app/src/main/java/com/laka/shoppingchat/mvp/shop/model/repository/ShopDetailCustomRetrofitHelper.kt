package com.laka.shoppingchat.mvp.shop.model.repository

import com.laka.androidlib.util.ApplicationUtils
import com.laka.androidlib.util.retrofit.RetrofitHelper
import com.laka.androidlib.util.rx.constant.ApiType
import com.laka.shoppingchat.mvp.shop.constant.ShopDetailConstant

/**
 * @Author:summer
 * @Date:2018/12/20
 * @Description:
 */
class ShopDetailCustomRetrofitHelper {


    // 双重验证同步锁单例模式
    companion object {

        val instance: ShopDetailService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            mainShopService
        }

        private var mainShopService: ShopDetailService = RetrofitHelper.getInstance(ApplicationUtils.getApplication())
                .setRequestHost(ShopDetailConstant.SHOPPING_BASE_HOST)
                .setNetWorkInterceptor(true)
                .setTokenRequest(true)
                .setApiType(ApiType.CUSTOM_API)
                .build()
                .create(ShopDetailService::class.java)
    }
}