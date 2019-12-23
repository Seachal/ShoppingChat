package com.laka.shoppingchat.mvp.order.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.laka.shoppingchat.mvp.order.constract.OrderConstract
import com.laka.shoppingchat.mvp.order.model.responsitory.OrderModel

/**
 * @Author:sming
 * @Date:2019/9/19
 * @Description:
 */
class OrderPresenter : OrderConstract.IOrderPresenter {


    private lateinit var mView: OrderConstract.IOrderView
    private var mModel = OrderModel()

    override fun setView(view: OrderConstract.IOrderView) {
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

}