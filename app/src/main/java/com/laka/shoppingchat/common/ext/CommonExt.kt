package com.laka.shoppingchat.common.ext

import android.content.Context
import android.view.View
import com.laka.androidlib.mvp.IBaseView
import com.laka.androidlib.util.EncryptUtils
import com.laka.androidlib.util.retrofit.AuthorizationInterceptor
import com.laka.androidlib.util.rx.RxSchedulerComposer
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.widget.refresh.BaseListBean
import com.laka.androidlib.util.rx.customrx.RxCustomSubscriber
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.ArrayList

/*
    扩展点击事件，参数为方法
 */
fun View.onClick(method: () -> Unit): View {
    setOnClickListener { method() }
    return this
}


fun <T, D : IBaseView<*>> Observable<T>.excute(
    add: ArrayList<Disposable>,
    callBack: ResponseCallBack<T>,
    mView: D
) {
    this.compose(RxSchedulerComposer.normalSchedulersTransformer())
        .subscribe(object :
            RxCustomSubscriber<T, D>(mView, callBack) {
            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                add.add(d)
            }
        })
}


fun <T> MutableList<T>.convertRefresh(page: Int): BaseListBean<T> {
    var list = this
    return object : BaseListBean<T>() {
        override fun getList(): MutableList<T> {
            return list
        }

        override fun getPageTotalCount(): Int {
            return if (list.size > 0) {
                page + 1
            } else {
                page
            }
        }
    }
}

fun Context.loginHandle(method: () -> Unit) {
    return if (!UserUtils.isLogin()) {
        LoginModuleNavigator.startLoginActivity(this)
    } else {
        method()
    }
}

fun String.toSign(): String {
    return String(EncryptUtils.encryptMD5("${AuthorizationInterceptor.LAKA_APP_SECRET}${this}${AuthorizationInterceptor.LAKA_APP_SECRET}")).toLowerCase()
}