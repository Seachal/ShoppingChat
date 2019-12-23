package com.netease.nim.uikit.business.session.weight

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.annotation.ColorRes
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.laka.androidlib.util.KeyboardHelper
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.widget.SeparatedEditText
import com.netease.nim.uikit.R
import com.netease.nim.uikit.business.session.utils.RoundUtils
import org.w3c.dom.Text

/**
 * @Author:summer
 * @Date:2019/9/12
 * @Description:
 */
class PayPsdView : RelativeLayout {

    private lateinit var mRootView: View
    private lateinit var mClContent: View
    private lateinit var mIvDelete: ImageView
    private lateinit var mTvBuyMsg: TextView
    private lateinit var mTvAmount: TextView
    private lateinit var mTvServiceCash: TextView
    private lateinit var mInputPsd: SeparatedEditText
    private lateinit var mClService: ConstraintLayout
    private lateinit var mClPayCash: ConstraintLayout
    private lateinit var mInputFinishListener: ((content: String) -> Unit)
    private lateinit var mOnClickListener: OnClickListener
    private var mAmount: String = "0.00"
    private var mBuyMsg: String = ""

    private var mServiceCash: String = ""

    @ColorRes
    private var mStatusBarColor: Int = R.color.color_ededed

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
        initEvent()
    }

    private fun initView(context: Context?) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_pay_psd_input, this)
        mClContent = findViewById(R.id.cl_content)
        mIvDelete = findViewById(R.id.iv_delete)
        mTvBuyMsg = findViewById(R.id.tv_buy_msg)
        mTvAmount = findViewById(R.id.tv_buy_amount)
        mInputPsd = findViewById(R.id.input_psd)
        mClService = findViewById(R.id.cl_service)
        mClPayCash = findViewById(R.id.cl_pay_way)
        mTvServiceCash = findViewById(R.id.tv_service_cash)
        mTvAmount.text = RoundUtils.roundForHalf(mAmount)
        mTvBuyMsg.text = mBuyMsg
        if (StringUtils.isNotEmpty(mServiceCash)) {
            mClService.visibility = View.VISIBLE
            mClPayCash.visibility = View.GONE
            mTvAmount.text = mAmount
        } else {
            mClService.visibility = View.GONE
            mClPayCash.visibility = View.VISIBLE
        }
    }

    fun setBuyMsg(msg: String) {
        mBuyMsg = msg
        if (::mTvBuyMsg.isInitialized) {
            mTvBuyMsg.text = mBuyMsg
        }
    }

    fun setAmount(amount: String) {
        mAmount = amount
        if (::mTvAmount.isInitialized) {
            mTvAmount.text = mAmount
        }
    }

    fun setServiceCash(cash: String) {
        mServiceCash = cash
        if (::mTvServiceCash.isInitialized
            && StringUtils.isNotEmpty(mServiceCash)
        ) {
            mClPayCash.visibility = View.GONE
            mClService.visibility = View.VISIBLE
            mTvServiceCash.text = "¥-$mServiceCash"
        } else {
            mClPayCash.visibility = View.VISIBLE
            mClService.visibility = View.GONE
        }
    }

    fun setLocalStatusBarColor(@ColorRes color: Int) {
        mStatusBarColor = color
    }

    fun clearText() {
        if (::mInputPsd.isInitialized) {
            mInputPsd.clearText()
        }
    }

    fun requestInputFocus() {
        if (::mInputPsd.isInitialized) {
            mInputPsd.requestFocus()
            KeyboardHelper.openKeyBoard(context, mInputPsd)
        }
    }

    private fun initEvent() {
        mIvDelete.setOnClickListener {
            hideForAnimator()
        }
        mInputPsd.setTextChangedListener(object : SeparatedEditText.TextChangedListener {
            override fun textChanged(changeText: CharSequence?) {

            }

            override fun textCompleted(text: CharSequence?) {
                if (::mInputFinishListener.isInitialized) {
                    mInputFinishListener.invoke("$text")
                }
            }
        })
    }

    fun showForAnimator() {
        this.visibility = View.VISIBLE
        val enterAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_enter_anim)
        mClContent.clearAnimation()
        mClContent.animation = enterAnim
        enterAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                this@PayPsdView.requestInputFocus()
            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })
        enterAnim.start()
        setStatusBarColor(R.color.color_99606060)
    }

    fun setStatusBarColor(color: Int) {
        val activity = context as Activity
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarUtil.setColor(
                    it,
                    ContextCompat.getColor(it, color),
                    0
                )
            } else {
                StatusBarUtil.setColor(it, ContextCompat.getColor(it, R.color.black), 0)
            }
        }
    }

    fun hideForAnimator() {
        val exitAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_exit_anim)
        mClContent.clearAnimation()
        mClContent.animation = exitAnim
        exitAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                LogUtils.info("repeat")
            }

            override fun onAnimationEnd(animation: Animation?) {
                visibility = View.GONE
                //动画跟软键盘一起弹出或者一起收起，页面会卡顿
                KeyboardHelper.hideKeyBoard(context, this@PayPsdView)
                //setStatusBarColor(color)
                setStatusBarColor(mStatusBarColor)
            }

            override fun onAnimationStart(animation: Animation?) {
                LogUtils.info("start")
            }
        })
        exitAnim.start()
    }

    fun setInputFinishListener(listener: ((content: String) -> Unit)) {
        this.mInputFinishListener = listener
    }

    fun setClickListener(clickListener: OnClickListener) {
        this.mOnClickListener = clickListener
    }

}