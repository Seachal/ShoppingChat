package com.laka.shoppingchat.mvp.login.model.repository

import com.laka.androidlib.util.ApplicationUtils
import com.laka.androidlib.util.retrofit.RetrofitHelper
import com.laka.androidlib.util.rx.constant.ApiType
import com.laka.shoppingchat.BuildConfig

/**
 * @Author:summer
 * @Date:2019/1/7
 * @Description:
 */
class PhoneLoginRetrofitHelper {

    // 双重验证同步锁单例模式
    companion object {

        val instance: PhoneLoginService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            phoneLoginService
        }

        private var phoneLoginService: PhoneLoginService = RetrofitHelper.getInstance(ApplicationUtils.getApplication())
                //.setRequestHost(LoginConstant.LOGIN_BASE_HOST)
                .setRequestHost(BuildConfig.SHOPPING_CHAT_HOST)
                .setNetWorkInterceptor(true)
                .setTokenRequest(true)
                .setApiType(ApiType.TAOBAO_API)
                .build()
                .create(PhoneLoginService::class.java)
    }
}