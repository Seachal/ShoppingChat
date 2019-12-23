package com.laka.shoppingchat.mvp.wallet.model.responsitory

import com.laka.androidlib.util.ApplicationUtils
import com.laka.androidlib.util.retrofit.RetrofitHelper
import com.laka.androidlib.util.rx.constant.ApiType
import com.netease.nim.uikit.BuildConfig

/**
 * @Author:summer
 * @Date:2019/9/11
 * @Description:红包接口，普通接口和红包接口地址不一致
 */
class RedPackageRecordRetrofitHelper {

    // 双重验证同步锁单例模式
    companion object {
        val instance: RedPackageRecordService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            redPackageRecordService
        }

        private var redPackageRecordService: RedPackageRecordService =
            RetrofitHelper.getInstance(ApplicationUtils.getApplication())
                .setRequestHost(BuildConfig.SHOPPING_CHAT_PACKAGE_HOST)
                .setNetWorkInterceptor(true)
                .setTokenRequest(true)
                .setApiType(ApiType.CUSTOM_API)
                .build()
                .create(RedPackageRecordService::class.java)
    }
}