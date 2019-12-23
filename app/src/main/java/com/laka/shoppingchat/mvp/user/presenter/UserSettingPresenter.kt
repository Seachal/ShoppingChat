package com.laka.shoppingchat.mvp.user.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Intent
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.util.SPHelper
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.common.dsl.request
import com.laka.shoppingchat.common.ext.toSign
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.model.event.UserEvent
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.laka.shoppingchat.mvp.user.constract.UserSettingConstract
import com.laka.shoppingchat.mvp.user.helper.UserUpdateHelper
import com.laka.shoppingchat.mvp.user.model.responsitory.UserSettingModel
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum

/**
 * @Author:summer
 * @Date:2019/9/9
 * @Description:
 */
class UserSettingPresenter : UserSettingConstract.IUserSettingPresenter {


    override fun forgetPsw(psw: String, code: String) {
        mModel.forgetPsw(
            request {
                params {
                    "password" to psw.toSign()
                    "code" to code
                }
                onSuccess {
                    mView.forgetPswSuccess()
                    //onLogout()
                }
                onFail {
                    ToastHelper.showToast(it.errorMsg)
                }
            }
        )
    }

    override fun changeMobile(mobile: String, code: String) {
        mModel.changeMobile(
            request {
                params {
                    "mobile" to mobile
                    "code" to code
                }
                onSuccess {
                    mView.modifyPhoneSuccess()
                    //onLogout()
                }
                onFail {
                    ToastHelper.showToast(it.errorMsg)
                }
            }
        )
    }

    override fun sendCode(phone: String, event: String) {

        mModel.sendCode(
            request {
                params {
                    "mobile" to phone
                    "event" to event
                }
                onSuccess {
                    mView.sendCodeSuccess()
                }
                onFail {
                    ToastHelper.showToast(it.errorMsg)
                }
            }
        )
    }

    override fun editPassword(oldPsd: String, psd: String) {
        mModel.editPassword(
            request {
                params {
                    "old_password" to oldPsd.toSign()
                    "password" to psd.toSign()
                }
                onSuccess {
                    mView.settingPasswordSuccess()
                }
                onFail {
                    ToastHelper.showToast(it?.errorMsg)
                }
            })
    }

    override fun checkUpPaySuccess(psd: String) {
        mModel.checkUpPay(
            request {
                params {
                    "password" to psd.toSign()
                }
                onSuccess {
                    mView.checkUpPaySuccess()
                }
                onFail {
                    ToastHelper.showToast(it?.errorMsg)
                }
            })
    }


    private lateinit var mView: UserSettingConstract.IUserSettingView
    private var mModel: UserSettingConstract.IUserSettingModel = UserSettingModel()

    override fun setView(view: UserSettingConstract.IUserSettingView) {
        this.mView = view
        mModel.setView(mView)
    }

    override fun onViewCreate() {

    }

    override fun onViewDestroy() {
        mModel.onViewDestory()
    }

    override fun onLifeCycleChange(owner: LifecycleOwner, event: Lifecycle.Event) {

    }

    override fun onLogout() {
        NIMClient.getService(AuthService::class.java).logout()
        EventBusManager.postEvent(UserEvent(UserConstant.LOGOUT_EVENT))
        SPHelper.clearByFileName(LoginConstant.USER_INFO_FILENAME)
        UserUtils.clearUserInfo()
        ToastHelper.showCenterToast("退出登录")
        mView.onLogoutSuccess()
    }

    override fun settingPassword(psd: String) {
        mModel.settingPassword(
            request {
                params {
                    "password" to psd.toSign()
                }
                onSuccess {
                    mView.settingPasswordSuccess()
                }
                onFail {
                    ToastHelper.showToast(it?.errorMsg)
                }
            })
    }
}