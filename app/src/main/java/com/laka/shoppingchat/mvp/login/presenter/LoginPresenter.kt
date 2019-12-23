package com.laka.shoppingchat.mvp.login.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.SPHelper
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.customrx.RxSubscriber
import com.laka.androidlib.util.rx.exception.BaseException
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.common.dsl.request
import com.laka.shoppingchat.common.util.MetaDataUtils
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.contract.ILoginContract
import com.laka.shoppingchat.mvp.login.model.bean.UserBean
import com.laka.shoppingchat.mvp.login.model.bean.UserInfoBean
import com.laka.shoppingchat.mvp.login.model.bean.VerificationCodeDataBean
import com.laka.shoppingchat.mvp.login.model.event.UserEvent
import com.laka.shoppingchat.mvp.login.model.repository.LoginModel
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import io.reactivex.disposables.Disposable

/**
 * @Author:summer
 * @Date:2019/3/9
 * @Description:微信登录 、手机注册
 */
class LoginPresenter : ILoginContract.ILoginPresenter {
    override fun onCheckCode(code: String) {
        mModel.onCheckCode(
            request {
                params {
                    "code" to code
                }
                onSuccess {
                    mView.onCheckCode()
                }
                onFail {
                    ToastHelper.showToast(it.errorMsg)
                }
            }
        )
    }



    private var mDisposableList: ArrayList<Disposable> = ArrayList()
    private lateinit var mView: ILoginContract.ILoginView
    private var mModel: ILoginContract.ILoginModel = LoginModel()

    override fun setView(view: ILoginContract.ILoginView) {
        this.mView = view
        mModel.setView(view)
    }

    override fun onViewCreate() {
    }

    override fun onViewDestroy() {
        mModel.onViewDestory()
    }

    override fun onLifeCycleChange(owner: LifecycleOwner, event: Lifecycle.Event) {
    }

    override fun onGetVerificationCode(phone: String, type: Int) {
        val params = HashMap<String, String>()
        params["mobile"] = phone
        when(type){
            LoginConstant.VERIFICATION_TYPE_LOGIN->  params["event"] = "login"
            LoginConstant.VERIFICATION_FORGET_PAY ->   params["event"] = "forgetpassword"
        }

        mModel?.onGetVerificationCode(params).subscribe(object :
            RxSubscriber<VerificationCodeDataBean, ILoginContract.ILoginView>(mView) {
            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                mDisposableList?.add(d)
            }

            override fun onNext(t: VerificationCodeDataBean) {
                super.onNext(t)
                when {
                    t.code == 0 -> mView?.onGetVerificationCodeSuccess(t)
                    //获取验证码太频繁
                    t.code == 203 -> mView?.onGetVerificationCodeFrequently()
                    else -> ToastHelper.showCenterToast(t.msg + "")
                }
            }
        })
    }

    /**
     * 手机登录
     * */
    override fun onPhoneLogin(
        context: Context,
        phone: String,
        verificationCode: String,
        type: Int
    ) {
        mView.showLoading()
        val registerId = JPushInterface.getRegistrationID(context)
        val channel = MetaDataUtils.getMateDataForApplicationInfo("UMENG_CHANNEL")
        val params = HashMap<String, String>()
        params["mobile"] = phone
        params[LoginConstant.CODE] = verificationCode
        params[LoginConstant.OS_VERSION] = android.os.Build.VERSION.RELEASE
        params[LoginConstant.PLATFORM] = LoginConstant.ANDROID_PLATFROM
        params[LoginConstant.JPUSH_REGISTER_CHANNEL] = channel
        if (!TextUtils.isEmpty(registerId)) {
            params[LoginConstant.JPUSH_REGISTER_ID] = registerId
        }
        mModel.onPhoneLogin(params, object : ResponseCallBack<UserBean> {
            override fun onSuccess(t: UserBean) {
                mView.onPhoneLoginSuccess(t)
                mView.dismissLoading()
            }

            override fun onFail(e: BaseException?) {
                clearVerifySpData()
                mView.onPhoneLoginFail()
                ToastHelper.showCenterToast("${e?.errorMsg}")
            }
        })
    }

    //登录网易云信
    override fun onIMLogin(userBean: UserBean) {
        NIMClient.getService(AuthService::class.java)
            .login(LoginInfo(userBean.userImInfo.accid, userBean.userImInfo.token))
            .setCallback(object : RequestCallback<Any> {
                override fun onSuccess(param: Any) {
                    clearVerifySpData()
                    ToastHelper.showToast("登录成功")
                    EventBusManager.postEvent(UserEvent(UserConstant.LOGIN_EVENT))
                    // 登陆的时候，更新UserUtils数据
                    UserUtils.updateUserInfo(UserInfoBean(userBean, null))
                    mView.onImLoginSuccess(userBean)
                }

                override fun onFailed(code: Int) {
                    clearVerifySpData()
                    LogUtils.info("$code---")
                    ToastHelper.showCenterToast("IM登录失败")
                }

                override fun onException(exception: Throwable) {
                    clearVerifySpData()
                    LogUtils.info("twc", exception.toString())
                    ToastHelper.showCenterToast("IM登录异常")
                }
            })
    }

    fun clearVerifySpData() {
        SPHelper.putString(LoginConstant.PHONE_NUMBER, "")
        SPHelper.putLong(LoginConstant.PRE_GET_VERIFICATION_CODE_TIME, 0)
    }

}