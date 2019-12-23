package com.netease.nim.uikit.business.session.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.widget.SeparatedEditText
import com.laka.androidlib.widget.dialog.BaseDialog
import com.netease.nim.uikit.R
import com.netease.nim.uikit.business.session.utils.RoundUtils

/**
 * @Author:summer
 * @Date:2019/9/10
 * @Description:支付密码输入弹窗
 */
class PayPsdInputDialog : BaseDialog, View.OnClickListener {

    private lateinit var mIvDelete: ImageView
    private lateinit var mTvBuy: TextView
    private lateinit var mTvAmount: TextView
    private lateinit var mInputPsd: SeparatedEditText
    private lateinit var mInputFinishListener: ((content: String) -> Unit)
    private var mAmount: String = "0.00"

    constructor(context: Context?) : this(context, R.style.payDialogStyle)
    constructor(context: Context?, themeResId: Int) : super(context, themeResId)

    override fun getLayoutId(): Int {
        return R.layout.dialog_pay_psd_input
    }

    override fun initView() {
        mIvDelete = findViewById(R.id.iv_delete)
        mTvBuy = findViewById(R.id.tv_buy_msg)
        mTvAmount = findViewById(R.id.tv_buy_amount)
        mInputPsd = findViewById(R.id.input_psd)
        mTvAmount.text = RoundUtils.roundForHalf(mAmount)
    }

    override fun initData() {

    }

    override fun initEvent() {
        mIvDelete.setOnClickListener(this)
        mInputPsd.setTextChangedListener(object : SeparatedEditText.TextChangedListener {
            override fun textChanged(changeText: CharSequence?) {

            }

            override fun textCompleted(text: CharSequence?) {
                if (!StringUtils.isEmpty(text)) {
                    if (!::mInputFinishListener.isInitialized) {
                        mInputFinishListener.invoke("$text")
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_delete -> {
                dismiss()
            }
        }
    }

    fun setAmount(amount: String) {
        mAmount = amount
        if (::mTvAmount.isInitialized) {
            mTvAmount.text = RoundUtils.roundForHalf(mAmount)
        }
    }

    fun setInputFinishListener(listener: ((content: String) -> Unit)) {
        this.mInputFinishListener = listener
    }

}