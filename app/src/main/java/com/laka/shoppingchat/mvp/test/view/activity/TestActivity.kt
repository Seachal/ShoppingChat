package com.laka.shoppingchat.mvp.test.view.activity

import android.view.View
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.dsl.params
import com.netease.nim.uikit.business.session.constract.RedPackageConstract
import com.netease.nim.uikit.business.session.model.bean.RedPackageResponse
import com.netease.nim.uikit.business.session.presenter.RedPackagePresenter


/**
 * @Author:summer
 * @Date:2019/1/17 
 * @Description:测试 Activity
 */
class TestActivity : BaseMvpActivity<RedPackageResponse>(), RedPackageConstract.IRedPackageView,
    View.OnClickListener {

    private lateinit var mRedPackagePresenter: RedPackagePresenter

    override fun showErrorMsg(msg: String?) {
    }

    override fun createPresenter(): IBasePresenter<*> {
        mRedPackagePresenter = RedPackagePresenter()
        return mRedPackagePresenter
    }

    override fun setContentView(): Int {
        return R.layout.activity_test
    }

    override fun initIntent() {

    }

    override fun initViews() {
//        val list = ArrayList<String>()
//        for (i in 0 until 20) {
//            list.add("")
//        }
//        refresh_layout.setLayoutManager(
//            LinearLayoutManager(
//                this,
//                LinearLayoutManager.VERTICAL,
//                false
//            )
//        )
//        refresh_layout.setLayoutManager(
//            GridLayoutManager(
//                this,
//                1,
//                GridLayoutManager.VERTICAL,
//                false
//            )
//        )
//        refresh_layout.adapter = TestAdapter(list)

//        findViewById<Button>(R.id.btn).setOnClickListener {
//            LianLianPayManager.instance.pay(this)
//        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        params { "" to "" }
    }

    override fun onClick(v: View?) {

    }

    override fun showData(data: RedPackageResponse) {

    }

    override fun sendRedpackageSuccess(response: RedPackageResponse) {

    }

    override fun sendRedpackageFail(msg: String) {

    }
}