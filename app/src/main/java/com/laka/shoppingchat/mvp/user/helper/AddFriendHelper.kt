package com.laka.shoppingchat.mvp.user.helper

import android.app.Activity
import android.util.Base64
import com.laka.androidlib.util.RsaUtils
import com.laka.androidlib.util.StringUtils
import com.laka.shoppingchat.mvp.chat.ChatModuleNavigator
import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo
import com.laka.shoppingchat.mvp.nim.activity.UserInfoActivity
import com.laka.shoppingchat.mvp.nim.constract.INimConstract
import com.laka.shoppingchat.mvp.nim.presenter.NimPresenter
import com.netease.nim.uikit.impl.NimUIKitImpl

class AddFriendHelper {

    private lateinit var mActivity: Activity
    private var mPresenter: NimPresenter = NimPresenter()

    constructor(activity: Activity) {
        mActivity = activity
    }

    init {
        mPresenter.setView(object : INimConstract.IBaseNimView {
            override fun showData(data: String) {

            }

            override fun showLoading() {

            }

            override fun dismissLoading() {

            }

            override fun showErrorMsg(msg: String?) {

            }

            override fun getGroupIdForQrCodeSuccess(resp: QrCodeInfo) {
                val account = decrypt(resp.info)
                if (StringUtils.isNotEmpty(account)) {
                    if (NimUIKitImpl.isPersonAccount(resp.is_group)) {
                        UserInfoActivity.start(mActivity, account)
                    } else {
                        ChatModuleNavigator.startAddTeamActivity(mActivity, account)
                    }
                }
            }
        })
    }

    private fun decrypt(origin: String): String {
        val inPrivate = mActivity.resources.assets.open("rsa_public_key.pem")
        val publicKey = RsaUtils.loadPublicKey(inPrivate)
        // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
        val decryptByte = RsaUtils.decryptData(Base64.decode(origin, Base64.DEFAULT), publicKey)
        return String(decryptByte)
    }

    fun handleAddFriend(account: String) {
        mPresenter.getGroupIdForQrCode("$account")
    }

    fun onDestroy() {
        mPresenter.onViewDestroy()
    }

}