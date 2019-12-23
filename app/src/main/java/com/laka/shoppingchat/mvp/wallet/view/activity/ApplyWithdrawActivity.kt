package com.laka.shoppingchat.mvp.wallet.view.activity

import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.eventbus.Event
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.dialog.CommonConfirmDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.shop.utils.BigDecimalUtils
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import com.laka.shoppingchat.mvp.wallet.constant.WalletConstant
import com.laka.shoppingchat.mvp.wallet.constract.IPayConstract
import com.laka.shoppingchat.mvp.wallet.model.bean.MyWalletBean
import com.laka.shoppingchat.mvp.wallet.model.bean.WalletInitResponse
import com.laka.shoppingchat.mvp.wallet.presenter.PayPresenter
import com.netease.nim.uikit.business.ChatRouteManager
import com.netease.nim.uikit.business.session.weight.CashierInputFilter
import kotlinx.android.synthetic.main.activity_apply_withdraw.*
import kotlinx.android.synthetic.main.activity_my_wallet.title_bar
import org.json.JSONObject

class ApplyWithdrawActivity : BaseMvpActivity<WalletInitResponse>(), IPayConstract.IPayView, View.OnClickListener {

    private lateinit var mPresenter: PayPresenter
    private var mWallet: Double = 0.0

    override fun setContentView(): Int = R.layout.activity_apply_withdraw
    private lateinit var mCashInputFilter: CashierInputFilter

    override fun createPresenter(): IBasePresenter<*>? {
        mPresenter = PayPresenter()
        return mPresenter
    }

    override fun showData(data: WalletInitResponse) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_ededed), 0)
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            super.setStatusBarColor(color)
        }
    }

    override fun initIntent() {
        title_bar.setTitle("申请提现")
            .showDivider(false)
            .setTitleTextColor(R.color.color_333333)
            .setBackGroundColor(R.color.color_ededed)
            .setLeftIcon(R.drawable.selector_nav_btn_back)
    }

    override fun initData() {
        mPresenter.onLoadWallet()
        mPresenter.onLoadServiceChargeRate()
    }

    override fun initViews() {
        //设置金额输入框过滤器
        mCashInputFilter = CashierInputFilter()
        et_withdraw.filters = arrayOf(mCashInputFilter)
    }

    override fun initEvent() {
        tv_all_amount.setOnClickListener(this)
        btn_sure.setOnClickListener(this)
        pay_psd_input.setBuyMsg("提现")
        pay_psd_input.setLocalStatusBarColor(R.color.color_ededed)
        pay_psd_input.setInputFinishListener {
            pay_psd_input.hideForAnimator()
            pay_psd_input.clearText()
            //提现
            showLoading()
            val amount = et_withdraw.text.toString()
            mPresenter.onWithdraw(amount, it)
        }
        et_withdraw.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val withdrawCashStr = et_withdraw.text.toString().trim()
                if (StringUtils.isNotEmpty(withdrawCashStr)) {
                    val withdrawCash = withdrawCashStr.toDouble()
                    if (withdrawCash > mWallet) {
                        et_withdraw.setTextColor(ContextCompat.getColor(this@ApplyWithdrawActivity, R.color.red))
                    } else {
                        et_withdraw.setTextColor(
                            ContextCompat.getColor(
                                this@ApplyWithdrawActivity,
                                R.color.color_333333
                            )
                        )
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_sure -> {
                onWithdraw()
            }
            R.id.tv_all_amount -> {
                et_withdraw.setText("$mWallet")
                et_withdraw.setSelection("$mWallet".length)
            }
            else -> {

            }
        }
    }

    private fun onWithdraw() {
        val withdrawCashStr = et_withdraw.text.toString().trim()
        if (StringUtils.isEmpty(withdrawCashStr)) {
            ToastHelper.showToast("请输入提现金额")
            return
        }
        val withdrawCash = withdrawCashStr.toDouble()
        if (withdrawCash <= 0) {
            ToastHelper.showToast("提现金额不能小于等于0")
            return
        }
        if (withdrawCash > mWallet) {
            ToastHelper.showToast("余额不足")
            return
        }
        //获取服务费
        showLoading()
        mPresenter.onLoadServiceCharge("$withdrawCash")
    }

    override fun onWithdrawSuccess(response: WalletInitResponse) {
        ToastHelper.showToast("提现成功")
        EventBusManager.postEvent(Event(WalletConstant.EVENT_WITHDRAW_SUCCESS, -1))
        finish()
    }

    override fun onWithdrawFail(code: Int, msg: String) {
        if (code == 1603) {//支付密码错误
            val commonConfirmDialog = CommonConfirmDialog(this)
            commonConfirmDialog.setOnClickSureListener {
                UserCenterModuleNavigator.startOperationPayPsdActivity(this)
            }
            commonConfirmDialog.show()
            commonConfirmDialog.setDefaultTitleTxt("支付密码错误")
            commonConfirmDialog.setSureTxt("忘记密码")
        } else {
            ToastHelper.showToast(msg)
        }
    }

    override fun onLoadWalletSuccess(response: MyWalletBean) {
        mWallet = BigDecimalUtils.roundForHalf(response.amount).toDouble()
        et_withdraw.hint = "当前余额：¥$mWallet"
        if (::mCashInputFilter.isInitialized) {
            mCashInputFilter.setMaxCash(mWallet)
        }
    }

    override fun onPsdVeriflySuccess(response: JSONObject) {

    }

    override fun onLoadServiceChargeSuccess(response: HashMap<String, Any>) {
        if (response != null) {
            val withdrawService = response["serviceCash"].toString()
            if (StringUtils.isNotEmpty(withdrawService)) {
                val withdrawCash = getWithdrawCash()
                pay_psd_input.setAmount("$withdrawCash")
                pay_psd_input.setServiceCash(withdrawService)
                pay_psd_input.showForAnimator()
            }
        }
    }

    private fun getWithdrawCash(): Double {
        val amountStr = et_withdraw.text.toString().trim()
        if (StringUtils.isEmpty(amountStr)) {
            ToastHelper.showToast("请输入提现金额")
            return -1.0
        }
        val amount = amountStr.toDouble()
        if (amount <= 0) {
            ToastHelper.showToast("提现金额不能小于等于0")
            return -1.0
        }
        return amount
    }

    override fun onLoadServiceChargeFail(msg: String) {

    }

    override fun onLoadServiceChargeRateSuccess(response: HashMap<String, Any>) {
        if (response != null) {
            val rate = response["rate"].toString()
            if (StringUtils.isNotEmpty(rate)) {
                tv_service_withdraw_rate.text = "$rate%"
            }
        }
    }

    override fun onLoadServiceChargeRateFail(msg: String) {

    }

}