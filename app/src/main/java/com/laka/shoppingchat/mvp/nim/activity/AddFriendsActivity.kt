package com.laka.shoppingchat.mvp.nim.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.KeyboardHelper.hideKeyBoard
import com.laka.androidlib.util.RsaUtils
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.nim.constract.INimConstract
import com.laka.shoppingchat.mvp.nim.model.bean.FriendDataResp
import com.laka.shoppingchat.mvp.nim.presenter.NimPresenter
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nim.uikit.api.model.SimpleCallback
import com.netease.nim.uikit.common.ToastHelper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.activity_add_friend.*


class AddFriendsActivity : BaseMvpActivity<String>(), INimConstract.IBaseNimView {

    override fun onSearchFriend(resp: FriendDataResp) {
        UserInfoActivity.start(
            this@AddFriendsActivity, RsaUtils.decryptData(
                this,
                resp.accid
            )
        )
    }

    lateinit var mPresenter: NimPresenter
    override fun createPresenter(): IBasePresenter<*> {
        mPresenter = NimPresenter()
        mPresenter.setView(this)
        return mPresenter
    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    companion object {
        fun start(context: Context) {
            var intent = Intent(context, AddFriendsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun setContentView(): Int = R.layout.activity_add_friend

    override fun initIntent() {

    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.seletor_nav_btn_back)
            .setBackGroundColor(R.color.color_gray_bg)
            .setTitleTextColor(R.color.color_2d2d2d)
            .setTitleTextSize(18)
            .setOnLeftClickListener {
                finish()
            }

        etAddFriend.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val key = etAddFriend.text.toString().trim()
                    when {
                        TextUtils.isEmpty(key) -> {
                            ToastHelper.showToast(this@AddFriendsActivity, "请输入手机号码")
                            return true
                        }
                        key.length != 11 -> {
                            ToastHelper.showToast(
                                this@AddFriendsActivity,
                                "请输入正确的手机号码"
                            )
                            return true
                        }
                        key == UserUtils.getImAccount() -> {
                            ToastHelper.showToast(
                                this@AddFriendsActivity,
                                R.string.add_friend_self_tip
                            )
                            return true
                        }
                        //                    query(key)
                        //  这里记得一定要将键盘隐藏了
                        else -> {
                            mPresenter.searchFriend(key)
                            //                    query(key)
                            //  这里记得一定要将键盘隐藏了
                            hideKeyBoard(this@AddFriendsActivity, etAddFriend)
                            return true
                        }
                    }
                }
                return false
            }

        })
    }

    private fun query(account: String) {
        NimUIKit.getUserInfoProvider().getUserInfoAsync(account,
            SimpleCallback<NimUserInfo> { success, result, code ->
                if (success) {
                    if (result == null) {
                        ToastHelper.showToast(
                            this@AddFriendsActivity,
                            R.string.user_not_exsit
                        )
                    } else {
                        UserInfoActivity.start(this@AddFriendsActivity, account)
                    }
                } else if (code == 408) {
                    ToastHelper.showToast(
                        this@AddFriendsActivity,
                        R.string.network_is_not_available
                    )
                } else if (code == ResponseCode.RES_EXCEPTION.toInt()) {
                    ToastHelper.showToast(this@AddFriendsActivity, "on exception")
                } else {
                    ToastHelper.showToast(this@AddFriendsActivity, "on failed:$code")
                }
            })
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, resources.getColor(R.color.color_ededed), 0)
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            super.setStatusBarColor(color)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

}