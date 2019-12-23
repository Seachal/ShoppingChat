package com.netease.nim.uikit.business.session.activity.redpackage

import android.os.Build
import android.support.v4.content.ContextCompat
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.toast.ToastHelper
import com.netease.nim.uikit.R
import kotlinx.android.synthetic.main.activity_repackage_detail.*

/**
 * @Author:summer
 * @Date:2019/9/9
 * @Description:红包记录（收到的红包，发出的红包等）
 */
class RedPackageRecordActivity : BaseMvpActivity<String>() {

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(
                this,
                ContextCompat.getColor(this, R.color.color_f35543),
                0
            )
            StatusBarUtil.setLightModeNotFullScreen(this, false)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun setContentView(): Int {
        return R.layout.activity_repackage_detail
    }

    override fun initIntent() {

    }

    override fun initViews() {
        title_bar.setTitle("收到的红包")
            .setLeftText("关闭")
            .showDivider(false)
            .setBackGroundColor(R.color.color_f35543)
            .setRightIcon(R.drawable.default_btn_top_more_seletor)
            .setTitleTextColor(R.color.color_ffe2b1)
            .setLeftTextColor(R.color.color_ffe2b1)
            .setTitleTextSize(16)
            .setOnRightClickListener {
                ToastHelper.showToast("right")
            }
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun createPresenter(): IBasePresenter<*>? {
        return null
    }
}