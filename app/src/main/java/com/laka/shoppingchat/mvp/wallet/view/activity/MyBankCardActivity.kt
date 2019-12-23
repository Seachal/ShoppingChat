package com.laka.shoppingchat.mvp.wallet.view.activity

import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.dsl.RVWrapper
import com.laka.shoppingchat.common.dsl.refreshInit
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.wallet.view.adapter.BankCardAdater
import kotlinx.android.synthetic.main.activity_my_bank_card.*
import org.jetbrains.anko.startActivity

class MyBankCardActivity : BaseMvpActivity<String>() {
    private lateinit var mRVWrapper: RVWrapper
    lateinit var mAdapter: BankCardAdater
    val data = mutableListOf<String>()
    override fun setContentView(): Int = R.layout.activity_my_bank_card

    override fun initIntent() {

    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setTitle("我的银行卡")
            .setBackGroundColor(R.color.color_ededed)
            .setTitleTextColor(R.color.color_2d2d2d)
            .showDivider(false)
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(
                this,
                ContextCompat.getColor(this, R.color.color_ededed),
                0
            )
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
        mAdapter = BankCardAdater(data)
        val footerView = layoutInflater.inflate(
            R.layout.header_my_bank_card,
            mRvList as ViewGroup,
            false
        )
        footerView.onClick {
            startActivity<AddBankCardActivity>()
        }
        mAdapter.addFooterView(footerView)
        refreshInit {
            view = mRvList
            adapter = mAdapter
            enableRefresh = false
            adapterItemClick = { adapter, view, position ->
                startActivity<BankCardDetailActivity>()
            }
        }
    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }
}