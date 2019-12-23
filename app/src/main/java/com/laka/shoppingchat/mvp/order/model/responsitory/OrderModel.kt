package com.laka.shoppingchat.mvp.order.model.responsitory

import com.laka.shoppingchat.mvp.order.constract.OrderConstract
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * @Author:sming
 * @Date:2019/9/19
 * @Description:
 */
class OrderModel : OrderConstract.IOrderModel {



    private lateinit var mView: OrderConstract.IOrderView
    private val mDisposableList = ArrayList<Disposable>()

    override fun setView(v: OrderConstract.IOrderView) {
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




}