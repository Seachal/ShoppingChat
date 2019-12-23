package com.laka.shoppingchat.mvp.order.constract

import com.laka.androidlib.mvp.IBaseLoadingView
import com.laka.androidlib.mvp.IBaseModel
import com.laka.androidlib.mvp.IBasePresenter

/**
 * @Author:sming
 * @Date:2019/9/19
 * @Description:
 */
interface OrderConstract {
    interface IOrderView : IBaseLoadingView<String> {

    }

    interface IOrderPresenter : IBasePresenter<IOrderView> {

    }

    interface IOrderModel : IBaseModel<IOrderView> {

    }
}