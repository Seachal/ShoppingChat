package com.laka.shoppingchat.mvp.user.helper

import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import com.netease.nimlib.sdk.uinfo.model.UserInfo
import com.netease.nimlib.sdk.friend.FriendService


/**
 * @Author:summer
 * @Date:2019/9/10
 * @Description:直接通过SDK获取用户信息
 */
object ImUserInfoHelper {


    /**
     * 移除黑名单
     * */
    fun removeFromBlackList(acid: String, callBackSuccess: (() -> Unit), callBackFailed: ((Int) -> Unit)) {
        NIMClient.getService(FriendService::class.java).removeFromBlackList(acid)
            .setCallback(object : RequestCallback<Void> {
                override fun onSuccess(param: Void?) {
                    callBackSuccess.invoke()
                }

                override fun onFailed(code: Int) {
                    callBackFailed.invoke(code)
                }

                override fun onException(exception: Throwable?) {
                    callBackFailed.invoke(-1)
                }
            })
    }

    /**
     * 加入黑名单
     * */
    fun addToBlackList(acid: String, callBackSuccess: (() -> Unit), callBackFailed: ((Int) -> Unit)) {
        NIMClient.getService(FriendService::class.java).addToBlackList(acid)
            .setCallback(object : RequestCallback<Void> {
                override fun onSuccess(param: Void?) {
                    callBackSuccess.invoke()
                }

                override fun onFailed(code: Int) {
                    callBackFailed.invoke(code)
                }

                override fun onException(exception: Throwable?) {
                    callBackFailed.invoke(-1)
                }
            })
    }

    /**
     * 是否在黑名单内
     * */
    fun isInBlackList(acid: String): Boolean {
        return NIMClient.getService(FriendService::class.java).isInBlackList(acid)
    }

    fun getDispalyNickname(acid: String): String {
        var alias = NimUIKit.getContactProvider().getAlias(acid)
        if (StringUtil.isEmpty(alias)) {
            alias = getUserInfo(acid)?.name
        }
        if (StringUtil.isEmpty(alias)) {
            return ""
        }
        return alias
    }

    fun getUserAlias(acid: String): String? {
        return NimUIKit.getContactProvider().getAlias(acid)
    }

    private fun getUserInfo(acid: String): UserInfo? {
        return NimUIKit.getUserInfoProvider().getUserInfo(acid)
    }

    fun getCurrentUserInfo(): NimUserInfo? {
        return NIMClient.getService(UserService::class.java).getUserInfo(UserUtils.getImAccount())
    }

    fun getAllUserInfo(): List<NimUserInfo> {
        return NIMClient.getService(UserService::class.java).allUserInfo
    }

}