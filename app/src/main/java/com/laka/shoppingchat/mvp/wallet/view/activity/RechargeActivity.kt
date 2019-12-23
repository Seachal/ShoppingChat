package com.laka.shoppingchat.mvp.wallet.view.activity

import android.os.Handler
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.text.InputType
import android.view.LayoutInflater
import android.widget.Toast
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.eventbus.Event
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.KeyboardHelper
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.dialog.CommonConfirmDialog
import com.laka.androidlib.widget.dialog.JAlertDialog
import com.laka.shoppingchat.BuildConfig
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.shop.utils.BigDecimalUtils
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.laka.shoppingchat.mvp.wallet.constant.WalletConstant
import com.laka.shoppingchat.mvp.wallet.constract.IPayConstract
import com.laka.shoppingchat.mvp.wallet.helper.BottomDialogHelper
import com.laka.shoppingchat.mvp.wallet.helper.PayHelper
import com.laka.shoppingchat.mvp.wallet.model.bean.MyWalletBean
import com.laka.shoppingchat.mvp.wallet.model.bean.RechargeBean
import com.laka.shoppingchat.mvp.wallet.model.bean.WalletInitResponse
import com.laka.shoppingchat.mvp.wallet.presenter.PayPresenter
import com.laka.shoppingchat.mvp.wallet.view.adapter.RechargeAdapter
import com.netease.nim.uikit.business.session.weight.CashierInputFilter
import com.secure.pay.PayService
import kotlinx.android.synthetic.main.activity_recharge.*
import org.json.JSONObject


class RechargeActivity : BaseMvpActivity<WalletInitResponse>(), IPayConstract.IPayView {


    lateinit var mBottomDialogHelper: BottomDialogHelper
    lateinit var mAdapter: RechargeAdapter
    lateinit var mDialog: JAlertDialog
    private var mIsAutoGetSms = false
    private val mType: Int by lazy {
        if (BuildConfig.DEBUG) 1 /*测试*/ else 1 /*正式*/
    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun setContentView(): Int {
        return R.layout.activity_recharge
    }

    override fun initIntent() {

    }

    private lateinit var mPresenter: PayPresenter

    override fun createPresenter(): IBasePresenter<*> {
        mPresenter = PayPresenter()
        return mPresenter
    }

    override fun initViews() {
        titleBar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setTitle("充值")
            .setTitleTextSize(18)
            .setTitleTextColor(R.color.color_2d2d2d)
            .showDivider(false)
        initAdapter()
        if (!BuildConfig.DEBUG) {
            //设置金额输入框过滤器
            val inputFiler = arrayOf(CashierInputFilter())
            etRecharge.filters = inputFiler
        } else {
            etRecharge.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    private fun initAdapter() {
        val list = mutableListOf<RechargeBean>()
        list.add(RechargeBean(100, false))
        list.add(RechargeBean(500, false))
        list.add(RechargeBean(1000, false))
        list.add(RechargeBean(2000, false))
        list.add(RechargeBean(3000, false))
        list.add(RechargeBean(5000, false))
        mAdapter = RechargeAdapter(list)
        rlPrice.adapter = mAdapter
        rlPrice.layoutManager = GridLayoutManager(this, 3)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            list.forEach {
                it.isSelect = false
            }
            list[position].isSelect = true
            mAdapter.notifyDataSetChanged()
            etRecharge.setText(list[position].price.toString())
            etRecharge.setSelection(list[position].price.toString().length)
            KeyboardHelper.hideKeyBoard(this, etRecharge)
        }
    }

    private lateinit var confirmDialog: CommonConfirmDialog

    override fun initEvent() {
        btn_withdraw.onClick {
            val status = UserUtils.getPayPasswordStatus()
            if (status == "0") { //未设置支付密码
                if (!::confirmDialog.isInitialized) {
                    confirmDialog = CommonConfirmDialog(this)
                }
                confirmDialog.setDefaultTitleTxt("您未设置支付密码，前往设置？")
                confirmDialog.show()
                confirmDialog.setOnClickSureListener {
                    UserCenterModuleNavigator.startOperationPayPsdActivity(this)
                }
                return@onClick
            }
            val price = etRecharge.text.toString().trim()
            if (price.isEmpty()) {
                ToastHelper.showToast("请输入充值金额")
                return@onClick
            }
            if (price.toDouble() == 0.00) {
                ToastHelper.showToast("充值金额不能为零")
                return@onClick
            }
            //充值
            onRecharge()
        }
    }

    private fun onRecharge() {
        val amountStr = etRecharge.text.toString().trim()
        if (StringUtils.isEmpty(amountStr)) {
            ToastHelper.showToast("请输入提现金额")
            return
        }
        val amount = amountStr.toDouble()
        if (amount <= 0.0) {
            ToastHelper.showToast("充值金额不能小于0")
            return
        }
        mPresenter.onRecharge(amount)
    }

    override fun initData() {
        mPresenter.onLoadWallet()
    }

    fun showBottom() {
        KeyboardHelper.hideKeyBoard(this, etRecharge)
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

    override fun showData(data: WalletInitResponse) {

    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            //测试
            if ((Math.random() * 10).toInt() == 1) {
                throw NullPointerException("com.secure.pay.activity.PayListActivity e.subString() is null reference ")
            }

            if (msg.what == PayService.PAY) {  //支付
                if (msg.obj == null || (msg.obj as JSONObject).length() == 0) {
                    //返回空
                } else {
                    try {
                        val json = msg.obj as? JSONObject
                        json?.let {
                            if (it.has("ret_msg")) {
                                val msg = it.getString("ret_msg")
                                ToastHelper.showCenterToast(msg)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        EventBusManager.postEvent(Event(WalletConstant.EVENT_RECHARGE_SUCCESS, -1))
                        finish()
                    }
                }
            }
        }
    }

    override fun walletStatusFail(msg: String) {
        ToastHelper.showToast(msg)
    }

    override fun onRechargeSuccess(response: HashMap<String, Any>) {
        if (response != null) {
            val params = PayHelper.getParamsBean(response)
            PayHelper.onPay(this, mHandler, params.toString(), mType)
        } else {
            ToastHelper.showToast("返回参数错误")
        }
    }

    override fun onLoadWalletSuccess(response: MyWalletBean) {
        tv_wallet.text = "当前余额：¥${BigDecimalUtils.roundForHalf(response.amount).toFloat()}"
    }

}