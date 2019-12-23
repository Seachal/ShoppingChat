package com.laka.shoppingchat.mvp.login.contract

import android.content.Context
import com.laka.androidlib.mvp.IBaseLoadingView
import com.laka.androidlib.mvp.IBaseModel
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.shoppingchat.common.dsl.RequestWrapper
import com.laka.shoppingchat.mvp.login.model.bean.UserBean
import com.laka.shoppingchat.mvp.login.model.bean.VerificationCodeDataBean
import io.reactivex.Observable
import org.json.JSONObject

/**
 * @Author:summer
 * @Date:2019/3/9
 * @Description:微信登录/用户注册登录
 */
interface ILoginContract {

    interface ILoginView : IBaseLoadingView<UserBean> {
        fun onGetVerificationCodeSuccess(result: VerificationCodeDataBean)
        fun onGetVerificationCodeFrequently()
        fun onVerificationCodeFail(msg: String)
        fun onPhoneLoginSuccess(userBean: UserBean)
        fun onPhoneLoginFail()
        fun onImLoginSuccess(userBean: UserBean)
        fun onCheckCode()
    }

    interface ILoginPresenter : IBasePresenter<ILoginView> {
        fun onGetVerificationCode(phone: String, type: Int)
        fun onPhoneLogin(context: Context, phone: String, verificationCode: String, type: Int)
        fun onIMLogin(userBean: UserBean)
        fun onCheckCode(code: String)
    }

    interface ILoginModel : IBaseModel<ILoginView> {
        fun onGetVerificationCode(params: HashMap<String, String>): Observable<VerificationCodeDataBean>
        fun onPhoneLogin(params: HashMap<String, String>, callBack: ResponseCallBack<UserBean>)
        fun onCheckCode(requestWrapper: RequestWrapper<JSONObject>)
    }

}