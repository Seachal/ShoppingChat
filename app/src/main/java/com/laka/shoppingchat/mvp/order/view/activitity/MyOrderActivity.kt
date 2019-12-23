package com.laka.shoppingchat.mvp.order.view.activitity

import android.os.Build
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.dsl.RVWrapper
import com.laka.shoppingchat.common.dsl.refreshInit
import com.laka.shoppingchat.mvp.order.constract.OrderConstract
import com.laka.shoppingchat.mvp.order.view.adapter.OrderAdater
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_order.title_bar

class MyOrderActivity : BaseMvpActivity<String>(), OrderConstract.IOrderView {

    private lateinit var mRVWrapper: RVWrapper
    lateinit var mAdapter: OrderAdater
    val data = mutableListOf<String>()
    override fun setContentView(): Int = R.layout.activity_order

    override fun initIntent() {
    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setTitle("我的订单")
            .setBackGroundColor(R.color.color_ededed)
            .setTitleTextColor(R.color.color_2d2d2d)
    }
    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColorNoTranslucent(this, resources.getColor(R.color.color_ededed))
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            super.setStatusBarColor(color)
        }
    }
    override fun initEvent() {
    }

    override fun createPresenter(): IBasePresenter<*>? {
        return null
    }

    override fun initData() {
        data.add("")
        data.add("")
        data.add("")
        mAdapter = OrderAdater(data)
        refreshInit {
            view = mRvList
            adapter = mAdapter
            enableRefresh = false
        }
    }

    override fun showData(data: String) {
    }

    override fun showErrorMsg(msg: String?) {
    }

}