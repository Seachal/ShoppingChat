package com.laka.shoppingchat.mvp.user.constract

import com.laka.androidlib.mvp.IBaseLoadingView
import com.laka.androidlib.mvp.IBaseModel
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.shoppingchat.common.dsl.RequestWrapper
import org.json.JSONObject

/**
 * @Author:summer
 * @Date:2019/9/9
 * @Description:
 */
interface UserSettingConstract {
    interface IUserSettingView : IBaseLoadingView<String> {
        fun onLogoutSuccess() {}
        fun settingPasswordSuccess() {}
        fun checkUpPaySuccess() {}
        fun sendCodeSuccess() {}
        fun modifyPhoneSuccess() {}
        fun forgetPswSuccess() {}
    }

    interface IUserSettingPresenter : IBasePresenter<IUserSettingView> {
        fun onLogout()
        fun settingPassword(psd: String)
        fun checkUpPaySuccess(psd: String)
        fun editPassword(oldPsd: String, psd: String)
        fun sendCode(phone: String, event: String)
        fun changeMobile(mobile: String, code: String)
        fun forgetPsw(psw: String, code: String)
    }

    interface IUserSettingModel : IBaseModel<IUserSettingView> {
        fun settingPassword(requestWrapper: RequestWrapper<JSONObject>)
        fun checkUpPay(requestWrapper: RequestWrapper<JSONObject>)
        fun editPassword(requestWrapper: RequestWrapper<JSONObject>)
        fun sendCode(requestWrapper: RequestWrapper<JSONObject>)
        fun changeMobile(requestWrapper: RequestWrapper<JSONObject>)
        fun forgetPsw(requestWrapper: RequestWrapper<JSONObject>)
    }
}