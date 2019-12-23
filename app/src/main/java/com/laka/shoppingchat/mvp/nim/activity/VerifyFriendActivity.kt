package com.laka.shoppingchat.mvp.nim.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.net.utils.parse.GsonUtil
import com.laka.androidlib.util.KeyboardHelper.hideKeyBoard
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.extraDelegate
import com.laka.shoppingchat.mvp.nim.const.NimConstant
import com.laka.shoppingchat.mvp.nim.model.bean.ExpansionBean
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nim.uikit.common.ToastHelper
import com.netease.nim.uikit.common.util.sys.NetworkUtil
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.constant.VerifyType
import com.netease.nimlib.sdk.friend.model.AddFriendData
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.activity_verify_friend.*
import org.jetbrains.anko.toast

class VerifyFriendActivity : BaseActivity() {
    val account by extraDelegate(NimConstant.ACCOUNT, "")

    companion object {
        fun start(context: Context, account: String) {
            var intent = Intent(context, VerifyFriendActivity::class.java)
            intent.putExtra(NimConstant.ACCOUNT, account)
            context.startActivity(intent)
        }
    }

    override fun setContentView(): Int = R.layout.activity_verify_friend

    override fun initIntent() {

    }

    override fun initViews() {
        title_bar
            .setBackGroundColor(R.color.color_gray_bg)
            .setTitleTextColor(R.color.color_2d2d2d)
            .setTitleTextSize(18)
            .setRightText("发送")
            .setLeftText("取消")
            .showDivider(false)
            .setRightTextColor(R.color.white)
            .setRightTextBg(R.drawable.bg_send_friend_bg)
            .setOnLeftClickListener {
                finish()
            }
            .setOnRightClickListener {
                val verify = et_verify.text.toString()
                hideKeyBoard(this@VerifyFriendActivity, et_verify)
                showLoading()
                val list = mutableListOf(account)
                NIMClient.getService(
                    UserService::class.java
                ).fetchUserInfo(list)
                    .setCallback(object : RequestCallback<MutableList<NimUserInfo>> {
                        override fun onSuccess(param: MutableList<NimUserInfo>?) {
                            param?.let {
                                var userInfo = it[0]
                                userInfo?.let {
                                    it.extension?.let {
                                        if (it.isEmpty()) {
                                            doAddFriend(verify, false)
                                        } else {
                                            var expansionBean = GsonUtil.convertJson2Bean(it, ExpansionBean::class.java)
                                            expansionBean?.let {
                                                doAddFriend(verify, !it.validation)
                                            }
                                        }
                                    } ?: doAddFriend(verify, false)
                                }
                            }
                        }

                        override fun onFailed(code: Int) {
                            toast("${code}")
                            dismissLoading()
                        }

                        override fun onException(exception: Throwable?) {
                            exception?.let {
                                toast("${it.toString()}")
                            }
                            dismissLoading()
                        }

                    })

            }

    }

    override fun setStatusBarColor(color: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColorNoTranslucent(this, resources.getColor(R.color.color_gray_bg))
        } else {
            super.setStatusBarColor(color)
        }
    }

    override fun initData() {
        val userInfo =
            NimUIKit.getUserInfoProvider().getUserInfo(UserUtils.getImAccount()) as NimUserInfo?
        userInfo?.let {
            val hint = "我是${userInfo.name}"
            et_verify.setText(hint)
            et_verify.setSelection(hint.length)
        }
    }

    override fun initEvent() {

    }

    private fun doAddFriend(msg: String?, addDirectly: Boolean) {

        if (!NetworkUtil.isNetAvailable(this)) {
            dismissLoading()
            ToastHelper.showToast(this@VerifyFriendActivity, R.string.network_is_not_available)
            return
        }
        if (!TextUtils.isEmpty(account) && account == UserUtils.getImAccount()) {
            dismissLoading()
            ToastHelper.showToast(this@VerifyFriendActivity, "不能加自己为好友")
            return
        }
        val verifyType = if (addDirectly) VerifyType.DIRECT_ADD else VerifyType.VERIFY_REQUEST
        NIMClient.getService(FriendService::class.java)
            .addFriend(AddFriendData(account, verifyType, msg))
            .setCallback(object : RequestCallback<Void> {
                override fun onSuccess(param: Void?) {
                    dismissLoading()
//                    updateUserOperatorView()
                    if (VerifyType.DIRECT_ADD == verifyType) {
                        ToastHelper.showToast(this@VerifyFriendActivity, "添加好友成功")
                    } else {
                        ToastHelper.showToast(this@VerifyFriendActivity, "添加好友请求发送成功")
                    }
                    finish()
                }

                override fun onFailed(code: Int) {
                    dismissLoading()
                    if (code == 408) {
                        ToastHelper.showToast(
                            this@VerifyFriendActivity,
                            R.string.network_is_not_available
                        )
                    } else {
                        ToastHelper.showToast(this@VerifyFriendActivity, "on failed:$code")
                    }
                }

                override fun onException(exception: Throwable) {
                    dismissLoading()
                }
            })
    }
}