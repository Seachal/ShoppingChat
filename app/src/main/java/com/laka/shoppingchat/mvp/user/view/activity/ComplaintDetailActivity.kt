package com.laka.shoppingchat.mvp.user.view.activity

import android.os.Handler
import android.view.View
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.util.KeyboardHelper
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.dialog.LoadingDialog
import com.laka.shoppingchat.R
import kotlinx.android.synthetic.main.activity_complaint_detail.*

class ComplaintDetailActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mCommonLoadingDialog: LoadingDialog

    override fun setContentView(): Int {
        return R.layout.activity_complaint_detail
    }

    override fun initIntent() {

    }

    override fun initViews() {
        mCommonLoadingDialog = LoadingDialog(this)
        KeyboardHelper.openKeyBoard(this, et_input)
    }

    override fun initData() {

    }

    override fun initEvent() {
        iv_back.setOnClickListener(this)
        tv_finish.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.tv_finish -> {
                val content = et_input.text.toString().trim()
                if (StringUtils.isEmpty(content)) {
                    ToastHelper.showToast("请输入投诉内容")
                    return
                }
                if (::mCommonLoadingDialog.isInitialized) {
                    mCommonLoadingDialog.show()
                }
                Handler().postDelayed({
                    if (::mCommonLoadingDialog.isInitialized) {
                        mCommonLoadingDialog.dismiss()
                    }
                    ToastHelper.showToast("提交成功")
                }, 300)
            }
        }
    }
}