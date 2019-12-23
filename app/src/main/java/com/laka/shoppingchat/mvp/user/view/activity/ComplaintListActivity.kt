package com.laka.shoppingchat.mvp.user.view.activity

import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.View
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import kotlinx.android.synthetic.main.activity_complaint_list.*

class ComplaintListActivity : BaseActivity(), View.OnClickListener {

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_ededed), 0)
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            super.setStatusBarColor(color)
        }
    }

    override fun setContentView(): Int {
        return R.layout.activity_complaint_list
    }

    override fun initIntent() {

    }

    override fun initViews() {
        title_bar.setTitle("投诉")
            .showDivider(false)
            .setTitleTextColor(R.color.black)
            .setLeftIcon(R.drawable.ic_delete)
            .setBackGroundColor(R.color.color_ededed)
            .setOnLeftClickListener { finish() }
    }

    override fun initData() {

    }

    override fun initEvent() {
        pnb_harass.setOnClickListener(this)
        pnb_cheat.setOnClickListener(this)
        pnb_theft.setOnClickListener(this)
        pnb_tort.setOnClickListener(this)
        pnb_counterfeit_info.setOnClickListener(this)
        pnb_counterfeit_others.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pnb_harass,
            R.id.pnb_cheat,
            R.id.pnb_theft,
            R.id.pnb_tort,
            R.id.pnb_counterfeit_info,
            R.id.pnb_counterfeit_others -> {
                UserCenterModuleNavigator.startComplaintDetailActivity(this)
            }
            else -> {

            }
        }
    }
}