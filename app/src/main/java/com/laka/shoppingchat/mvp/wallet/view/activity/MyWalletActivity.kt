package com.laka.shoppingchat.mvp.wallet.view.activity

import android.os.Handler
import android.os.Message
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.eventbus.Event
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.network.NetworkUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.BuildConfig
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.shop.utils.BigDecimalUtils
import com.laka.shoppingchat.mvp.wallet.constant.WalletConstant
import com.laka.shoppingchat.mvp.wallet.constract.IWalletConstract
import com.laka.shoppingchat.mvp.wallet.helper.PayHelper
import com.laka.shoppingchat.mvp.wallet.model.bean.MyWalletBean
import com.laka.shoppingchat.mvp.wallet.model.bean.WalletInitResponse
import com.laka.shoppingchat.mvp.wallet.presenter.WalletPresenter
import com.lianlian.wallet.WalletService
import com.lianlian.wallet.regist.RegistService
import kotlinx.android.synthetic.main.activity_my_wallet.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class MyWalletActivity : BaseMvpActivity<MyWalletBean>(), IWalletConstract.IWalletView {

    private val TYPE_WALLET_MANAGER: Int = 1 //钱包管理
    private val TYPE_WITHDRAW: Int = 2  // 提现
    private val TYPE_RECHARGE: Int = 3  // 充值

    private val mType: Int by lazy {
        if (BuildConfig.DEBUG) 1 /*测试*/ else 1 /*正式*/
    }

    override fun showData(data: MyWalletBean) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    lateinit var mPresenter: WalletPresenter

    override fun setContentView(): Int {
        return R.layout.activity_my_wallet
    }

    override fun initIntent() {

    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            //商户需将处理结果送至商户的服务器端进行验签
            if (msg.what == RegistService.OPEN_ACCOUNT) { //钱包开会初始化
                if (msg.obj == null || (msg.obj as JSONObject).length() == 0) {
                    //返回空
                } else {
                    val result = msg.obj.toString()
                }
            } else if (msg.what == WalletService.WALLET_MANAGER.toInt()) { //钱包管理初始化
                if (msg.obj == null || (msg.obj as JSONObject).length() == 0) {
                    //返回空
                } else {
                    val result = msg.obj.toString()
                }
            }
        }
    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setRightText("红包明细")
            .setRightTextColor(R.color.color_2d2d2d)
            .showDivider(false)
            .setOnRightClickListener {
                startActivity<RedPackageListActivity>()
            }
    }

    override fun initEvent() {
        btn_recharge.onClick {
            showLoading()
            mPresenter.walletStatus(TYPE_RECHARGE)
//            startActivity<RechargeActivity>()
        }
        btn_withdraw.onClick {
            showLoading()
            mPresenter.walletStatus(TYPE_WITHDRAW)
//            startActivity<ApplyWithdrawActivity>()
        }
        tv_wallet_manager.onClick {
            showLoading()
            mPresenter.walletStatus(TYPE_WALLET_MANAGER)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onEvent(event: Event?) {
        when (event?.name) {
            WalletConstant.EVENT_WITHDRAW_SUCCESS,
            WalletConstant.EVENT_RECHARGE_SUCCESS -> {
                initData()
            }
        }
    }

    override fun createPresenter(): IBasePresenter<*> {
        mPresenter = WalletPresenter()
        return mPresenter
    }

    override fun initData() {
        if (NetworkUtils.isNetworkAvailable()) {
            mPresenter.onLoadMyWallet()
        } else {
            ToastHelper.showToast(R.string.network_is_not_available)
        }
    }

    override fun onLoadMyWalletSuccess(myWallet: MyWalletBean) {
        tv_wallet.withNumber(BigDecimalUtils.roundForHalf(myWallet.amount).toFloat()).start()
    }

    //钱包开户初始化
    override fun walletInitSuccess(response: WalletInitResponse) {
        if (response?.sdkInit != null) {
            val paramsBean = PayHelper.getParamsBean(response.sdkInit)
            PayHelper.open(this, mHandler, paramsBean.toString(), mType)
        }
    }

    //钱包管理初始化
    override fun walletManagerInitSuccess(response: WalletInitResponse) {
        if (response?.sdkInit != null) {
            val paramsBean = PayHelper.getParamsBean(response.sdkInit)
            PayHelper.walletManager(this, mHandler, paramsBean.toString(), mType)
        }
    }

    override fun walletStatusSuccess(status: Double, type: Int) {
        // 0-未开通 1-已开通 2-异常
        when (status) {
            0.0 -> { //未开户
                showLoading()
                mPresenter.walletInit() //钱包开户初始化
            }
            1.0 -> { //已开户
                when (type) {
                    TYPE_WITHDRAW -> startActivity<ApplyWithdrawActivity>()
                    TYPE_RECHARGE -> startActivity<RechargeActivity>()
                    TYPE_WALLET_MANAGER -> {
                        showLoading()
                        mPresenter.walletManagerInit("")
                    }
                }
            }
            2.0 -> {
                ToastHelper.showToast("获取钱包状态异常")
            }
        }
    }

}