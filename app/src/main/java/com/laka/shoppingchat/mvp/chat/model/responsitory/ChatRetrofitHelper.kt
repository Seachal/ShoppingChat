package com.laka.shoppingchat.mvp.user.model.responsitory

import com.laka.androidlib.util.ApplicationUtils
import com.laka.androidlib.util.retrofit.RetrofitHelper
import com.laka.androidlib.util.rx.constant.ApiType
import com.laka.shoppingchat.BuildConfig

/**
 * @Author:summer
 * @Date:2019/1/12
 * @Description:
 */
class ChatRetrofitHelper {

    // 双重验证同步锁单例模式
    companion object {

        val instance: ChatService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            chatService
        }

        private var chatService: ChatService =
            RetrofitHelper.getInstance(ApplicationUtils.getApplication())
                .setRequestHost(BuildConfig.SHOPPING_CHAT_HOST)
                .setNetWorkInterceptor(true)
                .setTokenRequest(true)
                .setApiType(ApiType.CUSTOM_API)
                .build()
                .create(ChatService::class.java)
    }
}