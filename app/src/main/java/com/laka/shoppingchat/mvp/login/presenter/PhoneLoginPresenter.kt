package com.laka.shoppingchat.mvp.login.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.exception.BaseException
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.common.util.MetaDataUtils
import com.laka.androidlib.util.rx.customrx.RxSubscriber
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.contract.IPhoneLoginContract
import com.laka.shoppingchat.mvp.login.model.bean.UserBean
import com.laka.shoppingchat.mvp.login.model.bean.UserInfoBean
import com.laka.shoppingchat.mvp.login.model.bean.VerificationCodeDataBean
import com.laka.shoppingchat.mvp.login.model.repository.PhoneLoginModel
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import io.reactivex.disposables.Disposable

/**
 * @Author:summer
 * @Date:2019/1/7
 * @Description: 手机登录
 */
class PhoneLoginPresenter : IPhoneLoginContract.ILoginPresenter {

    private var mDisposableList: ArrayList<Disposable> = ArrayList()
    private lateinit var mView: IPhoneLoginContract.ILoginView
    private val mPhoneLoginModel: IPhoneLoginContract.ILoginModel = PhoneLoginModel()

    override fun onViewCreate() {

    }

    override fun onViewDestroy() {
        mDisposableList.forEach {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        mDisposableList.clear()
        mPhoneLoginModel.onViewDestory()
    }

    override fun onLifeCycleChange(owner: LifecycleOwner, event: Lifecycle.Event) {

    }

    override fun setView(view: IPhoneLoginContract.ILoginView) {
        this.mView = view
        mPhoneLoginModel.setView(view)
    }

    override fun onLogin(context: Context, phone: String, code: String, type: Int) {
        mView.showLoading()
        val registerId = JPushInterface.getRegistrationID(context)
        val channel = MetaDataUtils.getMateDataForApplicationInfo("UMENG_CHANNEL")
        LogUtils.info("registerId：$registerId")
        val params = HashMap<String, String>()
        params["mobile"] = phone
        params[LoginConstant.CODE] = code
        params[LoginConstant.OS_VERSION] = android.os.Build.VERSION.RELEASE
        params[LoginConstant.PLATFORM] = LoginConstant.ANDROID_PLATFROM
        params[LoginConstant.JPUSH_REGISTER_CHANNEL] = channel
        if (!TextUtils.isEmpty(registerId)) {
            params[LoginConstant.JPUSH_REGISTER_ID] = registerId
        }
        mPhoneLoginModel.onLogin(params, object : ResponseCallBack<UserBean> {
            override fun onSuccess(t: UserBean) {
                // 登陆的时候，更新UserUtils数据
                UserUtils.updateUserInfo(UserInfoBean(t, null))
                mView?.onLoginSuccess(t)
                mView?.dismissLoading()
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showCenterToast("${e?.errorMsg}")
            }
        })
    }

    override fun onGetVerificationCode(phone: String, type: Int) {
        val params = HashMap<String, String>()
        params["mobile"] = phone
        params["event"] = "login"
        mPhoneLoginModel?.onGetVerificationCode(params).subscribe(object :
            RxSubscriber<VerificationCodeDataBean, IPhoneLoginContract.ILoginView>(mView) {
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
                    t.code == 1000 -> {
                        mView?.onGetVerificationCodeFrequently()
                        ToastHelper.showToast("1分钟内只能获取一次手机验证码")
                    }
                    else -> ToastHelper.showCenterToast(t.msg + "")
                }
            }
        })
    }
}