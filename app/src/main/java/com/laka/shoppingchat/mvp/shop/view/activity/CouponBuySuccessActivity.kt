package com.laka.shoppingchat.mvp.shop.view.activity

import android.os.Build
import android.support.v4.content.ContextCompat
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import kotlinx.android.synthetic.main.activity_coupon_buy_success.*

/**
 * @Author:summer
 * @Date:2019/9/20
 * @Description:购买成功
 */
class CouponBuySuccessActivity : BaseActivity() {

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
        return R.layout.activity_coupon_buy_success
    }

    override fun initIntent() {

    }

    override fun initViews() {
        title_bar.showDivider(false)
            .setBackGroundColor(R.color.color_ededed)
            .setLeftIcon(R.drawable.seletor_nav_btn_back)
            .setTitleTextColor(R.color.black)
            .setTitleTextSize(16)
    }

    override fun initData() {

    }

    override fun initEvent() {

    }
}