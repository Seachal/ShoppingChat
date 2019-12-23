package com.laka.shoppingchat.mvp.wallet.view.activity

import android.app.Dialog
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.widget.dialog.JAlertDialog
import com.laka.androidlib.widget.dialog.OnJAlertDialogClickListener
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.wallet.constract.IWalletConstract
import kotlinx.android.synthetic.main.activity_bank_card_detail.*

class BankCardDetailActivity : BaseMvpActivity<String>() {
    lateinit var mDialog: JAlertDialog
    override fun setContentView(): Int = R.layout.activity_bank_card_detail

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
            StatusBarUtil.setColorNoTranslucent(this, resources.getColor(R.color.color_gray_bg))
            StatusBarUtil.setLightMode(this)
        } else {
            super.setStatusBarColor(color)
        }

    }

    override fun initEvent() {
        clCard.onClick {
            showBottom()
        }
    }


    override fun createPresenter(): IBasePresenter<*>? {
        return null
    }

    override fun initData() {

    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    fun showBottom() {
        if (!::mDialog.isInitialized) {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_bank_detail, null)
            mDialog = JAlertDialog.Builder(this)
                .setFromBottom()
                .setCancelable(true)
                .setAnimation(R.style.bottom_menu_animation)
                .setContentView(view)
                .setOnClick(R.id.tv_unbind)
                .setOnClick(R.id.tv_cancel)
                .setWightPercent(1f)
                .setOnJAlertDialogCLickListener(object : OnJAlertDialogClickListener {
                    override fun onClick(dialog: Dialog, view: View?, position: Int) {
                        when (position) {

                        }
                        dialog.dismiss()
                    }

                })
                .create()
        }
        mDialog.show()
    }
}