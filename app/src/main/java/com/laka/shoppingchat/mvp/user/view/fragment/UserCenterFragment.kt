package com.laka.shoppingchat.mvp.user.view.fragment

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import com.laka.androidlib.base.fragment.BaseFragment
import com.laka.androidlib.util.PermissionUtils
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.model.event.UserEvent
import com.laka.shoppingchat.mvp.order.OrderModuleNavigator
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.laka.shoppingchat.mvp.user.helper.ImUserInfoHelper
import com.laka.shoppingchat.mvp.wallet.MyWalletNavigator
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.fragment_user_center.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Author:summer
 * @Date:2019/9/9
 * @Description:
 */
class UserCenterFragment : BaseFragment(), View.OnClickListener {

    private var mUserInfo: NimUserInfo? = null

    override fun setContentView(): Int {
        return R.layout.fragment_user_center
    }

    override fun initArgumentsData(arguments: Bundle?) {

    }

    override fun initView(rootView: View?, savedInstanceState: Bundle?) {
    }


    override fun initData() {
        handleUserData()
    }

    override fun onResume() {
        super.onResume()
        handleUserData()
    }

    private fun handleUserData() {
        mUserInfo = ImUserInfoHelper.getCurrentUserInfo()
        mUserInfo?.let {
            iv_head_portrait.loadAvatar(it.avatar)
            //iv_head_portrait.loadImage(it.avatar, R.drawable.default_bg_hp, R.drawable.default_bg_hp)
            tv_username.text = "${it.name}"
        }
    }

    override fun initEvent() {
        pnb_scan.setOnClickListener(this)
        pnb_wallet.setOnClickListener(this)
        pnb_order.setOnClickListener(this)
        pnb_card.setOnClickListener(this)
        pnb_password.setOnClickListener(this)
        cl_user_info.setOnClickListener(this)
        iv_head_portrait.setOnClickListener(this)
        nim_setting.setOnClickListener(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UserEvent) {
        when (event.type) {
            UserConstant.LOGIN_EVENT,
            UserConstant.LOGOUT_EVENT,
            UserConstant.EDIT_USER_INFO -> {
                handleUserData()
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pnb_scan -> {
                if (!PermissionUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    PermissionUtils.requestPermission(
                        context as Activity, arrayOf(
                            Manifest.permission.CAMERA
                            , Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                    return
                }
                LoginModuleNavigator.startScanQRCodeActivityForResult(
                    context as Activity,
                    LoginConstant.REQUEST_SCAN_QR_CODE
                )
            }
            R.id.pnb_wallet -> {
                MyWalletNavigator.startMyWalletActivity(context!!)
            }
            R.id.pnb_order -> {
                OrderModuleNavigator.startOrderActivity(context!!)
//                OrderModuleNavigator.startOrderActivity(context!!)
            }
            R.id.pnb_card -> {
                MyWalletNavigator.startMyBankCardActivity(context!!)
            }
            R.id.pnb_password -> {
                UserCenterModuleNavigator.startOperationPayPsdActivity(context!!)
            }
            R.id.cl_user_info -> {
                UserCenterModuleNavigator.startUserSettingActivity(context!!)
            }
            R.id.iv_head_portrait -> {
                UserCenterModuleNavigator.startUserSettingActivity(context!!)
            }
            R.id.nim_setting -> {
                UserCenterModuleNavigator.startNimSettingActivity(context!!)
            }
        }
    }
}