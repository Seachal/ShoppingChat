package com.netease.nim.uikit.business.session.model.respository

import com.laka.androidlib.util.ApplicationUtils
import com.laka.androidlib.util.retrofit.RetrofitHelper
import com.laka.androidlib.util.rx.constant.ApiType
import com.netease.nim.uikit.BuildConfig

/**
 * @Author:summer
 * @Date:2019/9/11
 * @Description:普通接口
 */
class NormalRetrofitHelper {

    // 双重验证同步锁单例模式
    companion object {
        val instance: RedPackageService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            redPackageService
        }

        private var redPackageService: RedPackageService =
            RetrofitHelper.getInstance(ApplicationUtils.getApplication())
                .setRequestHost(BuildConfig.SHOPPING_CHAT_HOST)
                .setNetWorkInterceptor(true)
                .setTokenRequest(true)
                .setApiType(ApiType.CUSTOM_API)
                .build()
                .create(RedPackageService::class.java)
    }
}