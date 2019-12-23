package com.laka.shoppingchat.mvp.user.helper

import com.laka.androidlib.util.LogUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum
import java.util.HashMap

/**
 * @Author:summer
 * @Date:2019/9/10
 * @Description:
 */
object UserUpdateHelper {

    /**
     * 更新用户资料
     */
    @JvmStatic
    fun update(field: UserInfoFieldEnum, value: Any, callback: RequestCallbackWrapper<Void>) {
        val fields = HashMap<UserInfoFieldEnum, Any>(1)
        fields[field] = value
        update(fields, callback)
    }

    @JvmStatic
    private fun update(
        fields: Map<UserInfoFieldEnum, Any>,
        callback: RequestCallbackWrapper<Void>
    ) {
        NIMClient.getService(UserService::class.java).updateUserInfo(fields).setCallback(object :
            RequestCallbackWrapper<Void>() {
            override fun onResult(code: Int, result: Void?, exception: Throwable?) {
                if (code == ResponseCode.RES_SUCCESS.toInt()) {
                    LogUtils.info("更改用户信息成功")
                } else {
                    exception?.let {
                        LogUtils.info("更改用户信息失败, exception=${exception?.message}")
                    }
                }
                callback.onResult(code, result, exception)
            }
        })
    }

}