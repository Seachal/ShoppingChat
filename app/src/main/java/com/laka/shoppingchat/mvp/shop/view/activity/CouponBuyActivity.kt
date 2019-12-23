package com.laka.shoppingchat.mvp.shop.view.activity

import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.widget.dialog.JAlertDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.shop.ShopDetailModuleNavigator
import kotlinx.android.synthetic.main.activity_coupon_buy.*

/**
 * @Author:summer
 * @Date:2019/9/20
 * @Description:优惠券购买
 */
class CouponBuyActivity : BaseMvpActivity<String>(), View.OnClickListener {

    private lateinit var mDialog: JAlertDialog

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(
                this,
                ContextCompat.getColor(this, R.color.color_ededed),
                0
            )
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun setContentView(): Int {
        return R.layout.activity_coupon_buy
    }

    override fun initIntent() {
        title_bar.showDivider(false)
            .setBackGroundColor(R.color.color_ededed)
            .setLeftIcon(R.drawable.seletor_nav_btn_back)
            .setTitleTextColor(R.color.black)
            .setTitleTextSize(16)
    }

    override fun createPresenter(): IBasePresenter<*>? {
        return null
    }

    override fun initViews() {

    }

    override fun initData() {

    }

    override fun initEvent() {
        pnb_card_pay.setOnClickListener(this)
        pnb_wallet_pay.setOnClickListener(this)
    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pnb_wallet_pay -> {
                showBottomPay()
            }
            R.id.pnb_card_pay -> {
                //showBottomPay()
                //测试
                ShopDetailModuleNavigator.startCouponBuySuccessActivity(this)
            }
        }
    }

    private fun showBottomPay() {
        if (!::mDialog.isInitialized) {
            val view = LayoutInflater.from(this).inflate(R.layout.bottom_pay, null)
            mDialog = JAlertDialog.Builder(this)
                .setFromBottom()
                .setCancelable(true)
                .setAnimation(R.style.bottom_menu_animation)
                .setContentView(view)
                .setOnClick(R.id.tv_cancel)
                .setWightPercent(1f)
                .setOnJAlertDialogCLickListener { dialog, view, position -> dialog.dismiss() }
                .create()
        }
        mDialog.show()
    }


}