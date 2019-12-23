package com.laka.shoppingchat.mvp.user.view.activity

import android.view.View
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.util.ResourceUtils
import com.laka.androidlib.util.SystemUtils
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.main.constant.HomeApiConstant
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import kotlinx.android.synthetic.main.activity_about_us.*

class AboutUsActivity : BaseActivity(), View.OnClickListener {


    override fun setContentView(): Int {
        return R.layout.activity_about_us
    }

    override fun initIntent() {

    }

    override fun initViews() {
        title_bar.setTitle(ResourceUtils.getString(R.string.about_us))
            .showDivider(false)
            .setLeftIcon(R.drawable.seletor_nav_btn_back)
            .setBackGroundColor(R.color.white)
            .setTitleTextColor(R.color.black)
        tv_app_version.text = ResourceUtils.getStringWithArgs(R.string.app_version, SystemUtils.getVersionName())
    }

    override fun initData() {

    }

    override fun initEvent() {
        cl_privacy.setOnClickListener(this)
        cl_about_us.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cl_privacy -> { //隐私政策
                UserCenterModuleNavigator.startPrivacyPolicyActivity(this)
            }
            R.id.cl_about_us -> { //关于我们

            }
            else -> {

            }
        }
    }
}