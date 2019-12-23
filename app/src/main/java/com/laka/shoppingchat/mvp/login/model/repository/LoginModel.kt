package com.laka.shoppingchat.mvp.login.model.repository

import com.laka.androidlib.util.rx.RxSchedulerComposer
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.customrx.RxCustomSubscriber
import com.laka.shoppingchat.common.dsl.RequestWrapper
import com.laka.shoppingchat.common.ext.excute
import com.laka.shoppingchat.mvp.login.contract.ILoginContract
import com.laka.shoppingchat.mvp.login.model.bean.UserBean
import com.laka.shoppingchat.mvp.login.model.bean.VerificationCodeDataBean
import com.laka.shoppingchat.mvp.user.model.responsitory.UserCenterRetrofitHelper
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.util.ArrayList

/**
 * @Author:summer
 * @Date:2019/3/9
 * @Description:(微信登录/用户注册)
 */
class LoginModel : ILoginContract.ILoginModel {
    override fun onCheckCode(requestWrapper: RequestWrapper<JSONObject>) {
        UserCenterRetrofitHelper.instance
            .checkCode(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }


    private lateinit var mView: ILoginContract.ILoginView
    private val mDisposableList = ArrayList<Disposable>()

    override fun setView(v: ILoginContract.ILoginView) {
        this.mView = v
    }

    override fun onViewDestory() {
        mDisposableList.forEach {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        mDisposableList.clear()
    }

    override fun onGetVerificationCode(params: HashMap<String, String>): Observable<VerificationCodeDataBean> {
        return PhoneLoginRetrofitHelper.instance.onGetVerificationCode(params)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
    }

    override fun onPhoneLogin(
        params: HashMap<String, String>,
        callBack: ResponseCallBack<UserBean>
    ) {
        CustomLoginRetrofixHelper.instance
            .onPhoneLogin(params)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<UserBean, ILoginContract.ILoginView>(mView, callBack) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }
}