package com.netease.nim.uikit.business.session.activity.redpackage

import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.netease.nim.uikit.R

/**
 * @Author:summer
 * @Date:2019/9/12
 * @Description:帮助中心
 */
class RedPackageHelperActivity : BaseMvpActivity<String>() {

    override fun setContentView(): Int {
        return R.layout.activity_redpackage_helper
    }

    override fun initIntent() {

    }

    override fun initViews() {

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