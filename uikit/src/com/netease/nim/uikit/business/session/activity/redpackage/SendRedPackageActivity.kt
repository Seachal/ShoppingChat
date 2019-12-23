package com.netease.nim.uikit.business.session.activity.redpackage

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.*
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.dialog.CommonConfirmDialog
import com.netease.nim.uikit.R
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nim.uikit.business.ChatBusinessNavigator
import com.netease.nim.uikit.business.ChatRouteManager
import com.netease.nim.uikit.business.session.constant.RedPackageConstant
import com.netease.nim.uikit.business.session.constract.RedPackageConstract
import com.netease.nim.uikit.business.session.dialog.RedPackageHelperDialog
import com.netease.nim.uikit.business.session.model.bean.RedPackageResponse
import com.netease.nim.uikit.business.session.model.bean.WalletBean
import com.netease.nim.uikit.business.session.presenter.RedPackagePresenter
import com.netease.nim.uikit.business.session.utils.RoundUtils
import com.netease.nim.uikit.business.session.weight.CashierInputFilter
import com.netease.nim.uikit.impl.NimUIKitImpl
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import kotlinx.android.synthetic.main.activity_send_redpackage_group.*
import org.json.JSONObject
import kotlin.math.abs

/**
 * @Author:summer
 * @Date:2019/9/6
 * @Description:私发红包
 */
class SendRedPackageActivity : BaseMvpActivity<RedPackageResponse>(),
    RedPackageConstract.IRedPackageView, View.OnClickListener {

    private var mDuration: Long = 300
    private var mMaxMargin: Int = ScreenUtils.dp2px(40f)
    private var mSessionType: Int = -1
    private var mAccount: String = "" //对方account
    private lateinit var mWalletBean: WalletBean
    private lateinit var mRedPackagePresenter: RedPackagePresenter
    private lateinit var mRedPackageHelperDialog: RedPackageHelperDialog

    override fun finish() {
        if (::mRedPackageHelperDialog.isInitialized
            && mRedPackageHelperDialog.isShowing
        ) {
            mRedPackageHelperDialog.dismiss()
        }
        super.finish()
    }

    override fun createPresenter(): IBasePresenter<*>? {
        mRedPackagePresenter = RedPackagePresenter()
        return mRedPackagePresenter
    }

    override fun setContentView(): Int {
        return R.layout.activity_send_redpackage_group
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(
                this,
                ContextCompat.getColor(this, R.color.color_ededed),
                0
            )
            StatusBarUtil.setLightMode(this)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mHideAnimator.isInitialized) {
            mHideAnimator.end()
        }
        if (::mShowAnimator.isInitialized) {
            mShowAnimator.end()
        }
    }

    override fun initIntent() {
        intent?.extras?.let {
            mSessionType = it.getInt("sessionType")
            mAccount = it.getString("account")
        }
    }

    override fun initViews() {
        et_amount_backup.isEnabled = false
        title_bar.setTitle("发红包")
            .setLeftText("取消")
            .showDivider(false)
            .setBackGroundColor(R.color.color_ededed)
            .setRightIcon(R.drawable.default_btn_top_more_seletor)
            .setTitleTextColor(R.color.black)
            .setTitleTextSize(16)
            .setOnRightClickListener {
                //红包记录、帮助中心、取消 弹窗
                if (!::mRedPackageHelperDialog.isInitialized) {
                    mRedPackageHelperDialog = RedPackageHelperDialog(this)
                    mRedPackageHelperDialog.setOnClickListener(this)
                }
                mRedPackageHelperDialog.show()
            }
        when (mSessionType) {
            SessionTypeEnum.P2P.value -> {
                tv_radpackage_switch.visibility = View.GONE
                tv_redpackage_type.visibility = View.GONE
                cl_redpackage_number.visibility = View.GONE
                tv_group_number.visibility = View.GONE
                //普通红包
                tv_redpackage_type.tag = RedPackageConstant.RED_PACKAGE_TYPE_NORMAL
            }
            SessionTypeEnum.Team.value -> {
                tv_radpackage_switch.visibility = View.VISIBLE
                tv_redpackage_type.visibility = View.VISIBLE
                cl_redpackage_number.visibility = View.VISIBLE
                tv_group_number.visibility = View.VISIBLE
                NimUIKit.getTeamProvider().fetchTeamById(mAccount) { success, result, _ ->
                    if (success && result != null) {
                        tv_group_number.text = "本群共 ${result.memberCount} 人"
                    } else {
                        tv_group_number.visibility = View.GONE
                    }
                }
                //群组红包，则默认为拼手气红包
                tv_redpackage_type.tag = RedPackageConstant.RED_PACKAGE_TYPE_RANDOM
                tv_redpackage_type.text = "改为普通红包"
                tv_radpackage_switch.text = "当前为拼手气红包，"
            }
        }
        //设置金额输入框过滤器
        val inputFiler = arrayOf(CashierInputFilter())
        et_amount.filters = inputFiler
    }

    override fun initData() {
        mRedPackagePresenter.onLoadMyWallet()
    }

    override fun initEvent() {
        et_amount.addTextChangedListener(mTextWatcher)
        et_redpackage_number.addTextChangedListener(mTextWatcher)
        sb_sure.setOnClickListener { onSure() }
        pay_psd_input.setBuyMsg("购聊红包")
        pay_psd_input.setClickListener(this)
        pay_psd_input.setInputFinishListener {
            pay_psd_input.hideForAnimator()
            pay_psd_input.clearText()
            //检验支付密码
            mRedPackagePresenter.onPayPsdCheck(it)
        }
        tv_redpackage_type.setOnClickListener(this)
    }

    private fun onVerificationInputContent() {
        if (!::mWalletBean.isInitialized) return
        var type = tv_redpackage_type.tag as? Int ?: RedPackageConstant.RED_PACKAGE_TYPE_RANDOM
        val amount = et_amount.text.toString().trim()
        val count = et_redpackage_number.text.toString().trim()
        val redPackageCount = if (!TextUtils.isEmpty(count)) count.toInt() else 0
        if (!StringUtils.isEmpty(amount) && amount.toDouble() > 0) {
            //判断金额是否超限
            if ((type == RedPackageConstant.RED_PACKAGE_TYPE_NORMAL
                        && amount.toDouble() <= RedPackageConstant.RED_PACKAGE_AMOUNT_MAX
                        && amount.toDouble() >= RedPackageConstant.RED_PACKAGE_AMOUNT_MIN)
                || (type == RedPackageConstant.RED_PACKAGE_TYPE_RANDOM
                        && redPackageCount <= 0
                        && amount.toDouble() <= RedPackageConstant.RED_PACKAGE_PAY_MAX)
                || (type == RedPackageConstant.RED_PACKAGE_TYPE_RANDOM
                        && redPackageCount > 0
                        && amount.toDouble().div(redPackageCount) <= RedPackageConstant.RED_PACKAGE_AMOUNT_MAX
                        && amount.toDouble().div(redPackageCount) >= RedPackageConstant.RED_PACKAGE_AMOUNT_MIN)
            ) {
                hideLimitItemAnim()
                et_amount.setTextColor(ContextCompat.getColor(this, R.color.color_2d2d2d))
                tv_amount_txt.setTextColor(ContextCompat.getColor(this, R.color.color_2d2d2d))
                tv_amount_yuan.setTextColor(ContextCompat.getColor(this, R.color.color_2d2d2d))
            } else {
                //超限
                showLimitItemAnim()
                sb_sure.isEnabled = false
                et_amount.setTextColor(ContextCompat.getColor(this, R.color.color_cd7346))
                tv_amount_txt.setTextColor(ContextCompat.getColor(this, R.color.color_cd7346))
                tv_amount_yuan.setTextColor(ContextCompat.getColor(this, R.color.color_cd7346))

                if (type == RedPackageConstant.RED_PACKAGE_TYPE_NORMAL) { //普通红包
                    when {
                        amount.toDouble() < RedPackageConstant.RED_PACKAGE_AMOUNT_MIN -> {
                            tv_amount_limit.text = "单个红包金额不可小于${RedPackageConstant.RED_PACKAGE_AMOUNT_MIN}"
                        }
                        amount.toDouble() > RedPackageConstant.RED_PACKAGE_PAY_MAX -> {
                            tv_amount_limit.text = "单笔支付总额不可超过${RedPackageConstant.RED_PACKAGE_PAY_MAX}元"
                        }
                        amount.toDouble() > RedPackageConstant.RED_PACKAGE_AMOUNT_MAX -> {
                            tv_amount_limit.text = "单个红包金额不可超过${RedPackageConstant.RED_PACKAGE_AMOUNT_MAX}元"
                        }
                        amount.toDouble().times(redPackageCount) > RedPackageConstant.RED_PACKAGE_PAY_MAX -> {
                            tv_amount_limit.text = "单笔支付总额不可超过${RedPackageConstant.RED_PACKAGE_PAY_MAX}元"
                        }
                    }
                } else {//拼手气红包
                    if (amount.toDouble() > RedPackageConstant.RED_PACKAGE_PAY_MAX) {
                        tv_amount_limit.text = "单笔支付总额不可超过${RedPackageConstant.RED_PACKAGE_PAY_MAX}元"
                    } else if (redPackageCount > 0) { //已经输入拼手气红包数量
                        if (amount.toDouble().div(redPackageCount) > RedPackageConstant.RED_PACKAGE_AMOUNT_MAX) {
                            tv_amount_limit.text = "单个红包金额不可超过${RedPackageConstant.RED_PACKAGE_AMOUNT_MAX}元"
                        } else if (amount.toDouble().div(redPackageCount) < RedPackageConstant.RED_PACKAGE_AMOUNT_MIN) {
                            tv_amount_limit.text = "单个红包金额不可小于${RedPackageConstant.RED_PACKAGE_AMOUNT_MIN}"
                        }
                    }
                }

                if (mSessionType == SessionTypeEnum.Team.value && type == RedPackageConstant.RED_PACKAGE_TYPE_NORMAL) {
                    tv_amount.text =
                        RoundUtils.roundForHalf("${amount.toDouble() * redPackageCount}")
                } else {
                    tv_amount.text = RoundUtils.roundForHalf(amount)
                }
                return
            }

            //金额不超限情况下的其他判断
            if (type == RedPackageConstant.RED_PACKAGE_TYPE_RANDOM) { //群组拼手气红包
                tv_amount.text = RoundUtils.roundForHalf(amount)
                if (redPackageCount > 0) { //红包数>0
                    sb_sure.isEnabled = true
                    sb_sure.setBackGroundColor(R.color.color_ea5f39)
                } else {
                    sb_sure.isEnabled = false
                }

            } else if (type == RedPackageConstant.RED_PACKAGE_TYPE_NORMAL
                && mSessionType == SessionTypeEnum.Team.value
            ) { //群组内普通红包
                tv_amount.text = RoundUtils.roundForHalf("${amount.toDouble().times(redPackageCount)}")
                if (redPackageCount > 0) {
                    sb_sure.isEnabled = true
                    sb_sure.setBackGroundColor(R.color.color_ea5f39)
                } else {
                    sb_sure.isEnabled = false
                }

            } else { //私发普通红包
                sb_sure.isEnabled = true
                sb_sure.setBackGroundColor(R.color.color_ea5f39)
                tv_amount.text = RoundUtils.roundForHalf(amount)
            }
        } else {
            tv_amount.text = "0.00"
            sb_sure.isEnabled = false
        }
    }

    private lateinit var mShowAnimator: ValueAnimator
    private lateinit var mHideAnimator: ValueAnimator

    private fun showLimitItemAnim() {
        if (::mShowAnimator.isInitialized && mShowAnimator.isRunning) {
            return
        }
        if (::mHideAnimator.isInitialized && mHideAnimator.isRunning) {
            mHideAnimator.removeAllUpdateListeners()
            mHideAnimator.end()
        }
        val layoutParams1 = tv_amount_limit.layoutParams as? LinearLayout.LayoutParams
        val topMargin = abs(layoutParams1?.topMargin ?: 0)
        if (topMargin == 0) {
            return
        }
        var animValue = topMargin
        tv_amount_limit.visibility = View.VISIBLE
        mShowAnimator = ValueAnimator.ofInt(animValue, 0)
        val duration = (topMargin / mMaxMargin.toDouble()) * mDuration
        mShowAnimator.duration = duration.toLong()
        mShowAnimator.addUpdateListener {
            val value = it.animatedValue as? Int
            val layoutParams = tv_amount_limit.layoutParams as? LinearLayout.LayoutParams
            layoutParams?.topMargin = -(value ?: 0)
            tv_amount_limit.layoutParams = layoutParams
            LogUtils.info("显示----animValue=$animValue-----duration=${mShowAnimator.duration}----value=$value")
        }
        mShowAnimator.start()
    }

    private fun hideLimitItemAnim() {
        if (::mHideAnimator.isInitialized && mHideAnimator.isRunning) {
            return
        }
        if (::mShowAnimator.isInitialized && mShowAnimator.isRunning) {
            //todo 终止后还会再回调一次 UpdateListener
            mShowAnimator.removeAllUpdateListeners()
            mShowAnimator.end()
        }
        val layoutParams1 = tv_amount_limit.layoutParams as? LinearLayout.LayoutParams
        var topMargin = abs(layoutParams1?.topMargin ?: 0)
        if (topMargin == mMaxMargin) {
            return
        }
        var animValue = topMargin
        mHideAnimator = ValueAnimator.ofInt(animValue, mMaxMargin)
        val duration = (1 - (topMargin / mMaxMargin.toDouble())) * mDuration
        mHideAnimator.duration = duration.toLong()
        mHideAnimator.addUpdateListener {
            val value = it.animatedValue as? Int
            val layoutParams = tv_amount_limit.layoutParams as? LinearLayout.LayoutParams
            layoutParams?.topMargin = -(value ?: 0)
            tv_amount_limit.layoutParams = layoutParams
            LogUtils.info("隐藏----animValue=$animValue----duration=${mHideAnimator.duration}-----value=$value")
        }
        mHideAnimator.start()
    }

    private fun onSure() {
        val amount = RoundUtils.roundForHalf(tv_amount.text.toString().trim())
        if (mSessionType == SessionTypeEnum.Team.value) {
            var count = et_redpackage_number.text.toString().trim()
            if (StringUtils.isEmpty(count)) {
                ToastHelper.showCenterToast("请输入红包个数")
                return
            }
        }
        if (StringUtils.isNotEmpty(amount)
            && ::mWalletBean.isInitialized
            && StringUtils.isNotEmpty(mWalletBean.amount)
            && amount.toDouble() > mWalletBean.amount.toDouble()
        ) {
            val confirmDialog = CommonConfirmDialog(this)
            confirmDialog.setOnClickSureListener {
                //参数一：activity
                ChatRouteManager.get(ChatRouteManager.ROUTE_WALLET_RECHARGE_ACTIVITY)?.onJump(this, HashMap())
            }
            confirmDialog.show()
            confirmDialog.setDefaultTitleTxt("余额不足，前往充值页面进行充值？")
            confirmDialog.setSureTxt("充值")
            return
        }
        pay_psd_input.setAmount(amount)
        pay_psd_input.showForAnimator()
    }

    private fun sendRedPackage(psd: String) {
        //隐藏键盘
        KeyboardHelper.hideKeyBoard(this, et_amount)
        //发红包
        val amount = et_amount.text.toString().trim()
        var title = et_des.text.toString().trim()
        if (StringUtils.isEmpty(title)) {
            title = "恭喜发财，大吉大利"
        }
        val jsonObject = JSONObject()
        val encryptPsd = EncryptUtils.encryptCustomMd5ToString(psd)
        jsonObject.put(
            "pay_token",
            encryptPsd
        )

        var type = tv_redpackage_type.tag as? Int ?: RedPackageConstant.RED_PACKAGE_TYPE_RANDOM
        if (mSessionType == SessionTypeEnum.P2P.value) {
            type = RedPackageConstant.RED_PACKAGE_TYPE_NORMAL
        }
        jsonObject.put("type", type) // 1:普通红包，2:拼手气红包
        jsonObject.put("user_id", NimUIKitImpl.getAccount())
        jsonObject.put("to_id", mAccount)
        jsonObject.put("title", title)
        jsonObject.put("amount", amount)
        jsonObject.put("ex", "{}")
        jsonObject.put("ope", mSessionType) //0:个人  1:群
        if (mSessionType == SessionTypeEnum.P2P.value) {
            jsonObject.put("quantity", 1) //红包个数
        } else if (mSessionType == SessionTypeEnum.Team.value) {
            var count = et_redpackage_number.text.toString().trim()
            jsonObject.put("quantity", count.toInt()) //红包个数
        }
        mRedPackagePresenter.sendRedPackage(jsonObject)
    }

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onVerificationInputContent()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_redpackage_record -> {
                mRedPackageHelperDialog.dismiss()
                ChatRouteManager.get(ChatRouteManager.ROUTE_REDPACKAGE_RECORD_ACTIVITY)
                    ?.onJump(this, HashMap())
            }
            R.id.tv_helper_center -> {
                mRedPackageHelperDialog.dismiss()
                ChatBusinessNavigator.startRedPackageHelperActivity(this)
            }
            R.id.iv_delete -> { //支付弹窗删除

            }
            R.id.tv_redpackage_type -> { //群发红包类型切换
                val tag =
                    tv_redpackage_type.tag as? Int ?: RedPackageConstant.RED_PACKAGE_TYPE_RANDOM
                if (tag == RedPackageConstant.RED_PACKAGE_TYPE_RANDOM) { //拼手气红包
                    tv_radpackage_switch.text = "当前为普通红包，"
                    tv_redpackage_type.text = "改为拼手气红包"
                    tv_redpackage_type.tag = RedPackageConstant.RED_PACKAGE_TYPE_NORMAL
                    tv_amount_txt_backup.text = "金额"
                    tv_amount_txt.text = "单个金额"
                } else if (tag == RedPackageConstant.RED_PACKAGE_TYPE_NORMAL) { //普通红包
                    tv_radpackage_switch.text = "当前为拼手气红包，"
                    tv_redpackage_type.text = "改为普通红包"
                    tv_redpackage_type.tag = RedPackageConstant.RED_PACKAGE_TYPE_RANDOM
                    tv_amount_txt_backup.text = "单个金额"
                    tv_amount_txt.text = "金额"
                }
                //重新计算金额
                onVerificationInputContent()
                sliderRightInLeftOut()
            }
        }
    }

    private fun sliderRightInLeftOut() {
        val leftOutAnim = AnimationUtils.loadAnimation(this, R.anim.slider_left_out)
        val rightInAnim = AnimationUtils.loadAnimation(this, R.anim.slider_right_in)
        cl_amount.animation = rightInAnim
        cl_amount_backup.animation = leftOutAnim
        rightInAnim.start()
        leftOutAnim.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {//充值页面返回
            showLoading()
            mRedPackagePresenter.onLoadMyWallet()
        }
    }


    //======================================= view 层接口 ==========================================

    override fun showData(data: RedPackageResponse) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun onLoadMyWalletSuccess(myWallet: WalletBean) {
        //把余额设置进自定义的editText
        mWalletBean = myWallet
        tv_balance.text = "钱包余额：¥${RoundUtils.roundForHalf(myWallet.amount)}"
    }

    override fun sendRedpackageFail(msg: String) {
        finish()
    }

    override fun sendRedpackageSuccess(response: RedPackageResponse) {
        finish()
    }

    override fun onPayPsdCheckSuccess(psd: String) {
        sendRedPackage(psd)
    }

    override fun onPayPsdCheckFail(code: Int, msg: String) {
        // 1603
        if (code == 1603) {//密码错误
            val commonConfirmDialog = CommonConfirmDialog(this)
            commonConfirmDialog.setOnClickSureListener {
                ChatRouteManager.get(ChatRouteManager.ROUTE_UPDATE_PASSWORD_ACTIVITY)?.onJump(this, HashMap())
            }
            commonConfirmDialog.show()
            commonConfirmDialog.setDefaultTitleTxt("支付密码错误")
            commonConfirmDialog.setSureTxt("忘记密码")
        } else {
            ToastHelper.showToast(msg)
        }
    }


}