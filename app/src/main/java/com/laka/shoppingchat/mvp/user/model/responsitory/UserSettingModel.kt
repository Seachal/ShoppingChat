package com.laka.shoppingchat.mvp.user.model.responsitory

import com.laka.shoppingchat.common.dsl.RequestWrapper
import com.laka.shoppingchat.common.ext.excute
import com.laka.shoppingchat.mvp.user.constract.UserSettingConstract
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.util.*

/**
 * @Author:summer
 * @Date:2019/9/10
 * @Description:
 */
class UserSettingModel : UserSettingConstract.IUserSettingModel {
    override fun forgetPsw(requestWrapper: RequestWrapper<JSONObject>) {
        UserCenterRetrofitHelper.instance
            .forgetPsw(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun changeMobile(requestWrapper: RequestWrapper<JSONObject>) {
        UserCenterRetrofitHelper.instance
            .changeMobile(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun sendCode(requestWrapper: RequestWrapper<JSONObject>) {
        UserCenterRetrofitHelper.instance
            .sendCode(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun checkUpPay(requestWrapper: RequestWrapper<JSONObject>) {
        UserCenterRetrofitHelper.instance
            .checkUpPay(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun editPassword(requestWrapper: RequestWrapper<JSONObject>) {
        UserCenterRetrofitHelper.instance
            .editPassword(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }


    private lateinit var mView: UserSettingConstract.IUserSettingView
    private val mDisposableList = ArrayList<Disposable>()

    override fun setView(v: UserSettingConstract.IUserSettingView) {
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

    override fun settingPassword(requestWrapper: RequestWrapper<JSONObject>) {
        UserCenterRetrofitHelper.instance
            .settingPassword(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }


}