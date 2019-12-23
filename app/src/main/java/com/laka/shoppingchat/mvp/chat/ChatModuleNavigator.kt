package com.laka.shoppingchat.mvp.chat

import android.content.Context
import android.os.Bundle
import com.laka.shoppingchat.mvp.chat.view.activity.AddTeamActivity
import com.laka.shoppingchat.mvp.chat.view.activity.ChatTeamQrCodeActivity
import com.laka.shoppingchat.mvp.nim.activity.UserInfoActivity
import org.jetbrains.anko.startActivity

/**
 * @Author:summer
 * @Date:2019/9/29
 * @Description:
 */
object ChatModuleNavigator {

    fun startChatTeamQrCodeActivity(context: Context, account: String) {
        context.startActivity<ChatTeamQrCodeActivity>(
            "account" to account
        )
    }

    fun startUserInfoActivity(context: Context, account: String) {
        UserInfoActivity.start(context, account)
    }

    fun startAddTeamActivity(context: Context, tId: String) {
        context.startActivity<AddTeamActivity>(
            "tId" to tId
        )
    }
}