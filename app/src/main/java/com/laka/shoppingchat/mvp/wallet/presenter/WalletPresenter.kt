package com.laka.shoppingchat.mvp.wallet.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.exception.BaseException
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.common.dsl.request
import com.laka.shoppingchat.common.ext.toSign
import com.laka.shoppingchat.mvp.wallet.constract.IWalletConstract
import com.laka.shoppingchat.mvp.wallet.model.bean.MyWalletBean
import com.laka.shoppingchat.mvp.wallet.model.responsitory.WalletModel
import org.json.JSONObject

/**
 * @Author:summer
 * @Date:2019/9/10
 * @Description:
 */
class WalletPresenter : IWalletConstract.IWalletPresenter {

    override fun onLoadPayToken(token: String) {
        mModel.onLoadPayToken(request {
            params {
                "token" to token
            }
            onSuccess {
                mView.onLoadPayTokenSuccess("")
            }
            onFail {
                mView.onLoadPayTokenFail("${it.errorMsg}")
                ToastHelper.showToast("${it.errorMsg}")
            }
        })
    }

    override fun onRecharge(amount: String, password: String) {
        mModel.onRecharge(
            request {
                params {
                    "amount" to amount
                    "password" to password.toSign()
                }
                onSuccess {
                    mView.onRechargeSuccess(it.gateway_url)
                }
                onFail {
                    ToastHelper.showToast(it.errorMsg)
                }
            }
        )
    }

    private lateinit var mView: IWalletConstract.IWalletView
    private var mModel: IWalletConstract.IWalletModel = WalletModel()

    override fun setView(view: IWalletConstract.IWalletView) {
        this.mView = view
        mModel.setView(mView)
    }

    override fun onViewCreate() {

    }

    override fun onViewDestroy() {

    }

    override fun onLifeCycleChange(owner: LifecycleOwner, event: Lifecycle.Event) {

    }

    override fun onLoadMyWallet() {
        mModel.onLoadMyWallet(request {
            params { }
            onSuccess {
                mView.onLoadMyWalletSuccess(it)
            }
            onFail { }
        })
    }

    //验证密码
    override fun onPayPsdCheck(psd: String) {
        mView.showLoading()
        mModel.onPayPsdCheck(request {
            params { "password" to psd.toSign() }
            onSuccess {
                mView.onPayPsdCheckSuccess()
            }
            onFail {
                ToastHelper.showToast("密码错误")
            }
        })
    }

    override fun walletInit() {
        mModel.walletInit(request {
            params { }
            onSuccess {
                mView.walletInitSuccess(it)
            }
            onFail {
                mView.walletInitFail(it.errorMsg)
                ToastHelper.showToast(it.errorMsg)
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
                ToastHelper.showToast(it.errorMsg)
            }
        })
    }

    override fun walletStatus(type: Int) {
        mModel.walletStatus(request {
            params { }
            onSuccess {
                val walletStatus = it["wallet_status"].toString()
                if (!StringUtils.isEmpty(walletStatus)) {
                    mView.walletStatusSuccess(walletStatus.toDouble(), type)
                } else {
                    ToastHelper.showToast("获取钱包状态失败：walletStatus=$walletStatus")
                    mView.walletStatusFail("获取钱包状态失败")
                }
            }
            onFail {
                ToastHelper.showToast("${it.errorMsg}")
                mView.walletStatusFail(it.errorMsg)
            }
        })
    }

}