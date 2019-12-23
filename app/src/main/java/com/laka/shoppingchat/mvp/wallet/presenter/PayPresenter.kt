package com.laka.shoppingchat.mvp.wallet.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.common.dsl.request
import com.laka.shoppingchat.common.ext.toSign
import com.laka.shoppingchat.mvp.wallet.constract.IPayConstract
import com.laka.shoppingchat.mvp.wallet.model.responsitory.PayModel

class PayPresenter : IPayConstract.IPayPresenter {

    private lateinit var mView: IPayConstract.IPayView
    private var mModel: PayModel = PayModel()

    override fun setView(view: IPayConstract.IPayView) {
        mView = view
        mModel.setView(view)
    }

    override fun onViewCreate() {

    }

    override fun onViewDestroy() {
        mModel.onViewDestory()
    }

    override fun onLifeCycleChange(owner: LifecycleOwner, event: Lifecycle.Event) {

    }

    override fun walletInit() {
        mModel.walletInit(request {
            params { }
            onSuccess {
                mView.walletInitSuccess(it)
            }
            onFail {
                mView.walletInitFail(it.errorMsg)
            }
        })
    }

    override fun walletManagerInit(flagPara: String) {
        /**
         *   必传参数，可以传空字符串
         *   操作标识
         *   0：支付密码修改
         *   1：绑定手机号码
         *   2：基本信息修改
         *   3：绑定银行卡修改
         *   4：收支明细查询
         *   5：实名认证
         *   6：账户提现
         *   7：钱包充值
         *   不传 默认展示所有功能
         * */
        mModel.walletManagerInit(request {
            params {
                "flag_para" to flagPara
            }
            onSuccess {
                mView.walletManagerInitSuccess(it)
            }
            onFail {
                mView.walletManagerInitFail(it.errorMsg)
            }
        })
    }

    override fun initCallBack() {
        mModel.initCallBack(request {
            params { }
            onSuccess {
                mView.initCallBackSuccess()
            }
            onFail {
                mView.initCallBackFail()
            }
        })
    }

    override fun walletStatus() {
        mModel.walletStatus(request {
            params { }
            onSuccess {
                val walletStatus = it["wallet_status"].toString()
                if (!StringUtils.isEmpty(walletStatus)) {
                    mView.walletStatusSuccess(walletStatus.toDouble())
                } else {
                    mView.walletStatusFail("获取钱包状态失败")
                }
            }
            onFail { mView.walletStatusFail(it.errorMsg) }
        })
    }

    /**
     * 充值
     * */
    override fun onRecharge(amount: Double) {
        mModel.onRecharge(request {
            params { "amount" to amount.toString() }
            onSuccess {
                mView.onRechargeSuccess(it)
            }
            onFail {
                ToastHelper.showToast("充值失败")
            }
        })
    }

    /**
     * 提现
     * */
    override fun onWithdraw(amount: String, psd: String) {
        mModel.onWithdraw(request {
            params {
                "amount" to amount
                "password" to psd.toSign()
            }
            onSuccess {
                mView.onWithdrawSuccess(it)
            }
            onFail {
                mView.onWithdrawFail(it?.code ?: -1, it?.errorMsg)
            }
        })
    }


    override fun onLoadWallet() {
        mModel.onLoadWallet(request {
            params { }
            onSuccess {
                mView.onLoadWalletSuccess(it)
            }
            onFail { }
        })
    }


    override fun onPsdVerifly(psd: String) {
        mModel.onPsdVerifly(request {
            params { "password" to psd.toSign() }
            onSuccess {
                mView.onPsdVeriflySuccess(it)
            }
            onFail {
                ToastHelper.showToast("密码错误")
            }
        })
    }

    override fun onLoadServiceCharge(amount: String) {
        mModel.onLoadServiceCharge(request {
            params { "amount" to amount }
            onSuccess { mView.onLoadServiceChargeSuccess(it) }
            onFail {
                if (it.code == 1) {
                    ToastHelper.showToast("${it.errorMsg}")
                } else {
                    mView.onLoadServiceChargeFail(it.errorMsg)
                    ToastHelper.showToast("计算提现服务费失败")
                }
            }
        })
    }

    override fun onLoadServiceChargeRate() {
        mModel.onLoadServiceChargeRate(request {
            params { }
            onSuccess { mView.onLoadServiceChargeRateSuccess(it) }
            onFail {
                mView.onLoadServiceChargeRateFail("${it.errorMsg}")
                ToastHelper.showToast("获取提现服务费费率失败：${it.errorMsg}")
            }
        })
    }
}