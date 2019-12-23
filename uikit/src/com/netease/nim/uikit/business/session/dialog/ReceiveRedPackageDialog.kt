package com.netease.nim.uikit.business.session.dialog

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import com.laka.androidlib.util.imageload.GlideLoader
import com.laka.androidlib.widget.dialog.BaseDialog
import com.netease.nim.uikit.R
import com.netease.nim.uikit.business.session.anim.Rotate3DAnimation
import com.netease.nim.uikit.business.session.attachment.RedPackageAttachment
import com.netease.nim.uikit.business.session.constant.RedPackageConstant
import com.netease.nim.uikit.business.session.model.bean.RedPackageTicketBean
import com.netease.nim.uikit.business.uinfo.UserInfoHelper
import com.netease.nim.uikit.impl.NimUIKitImpl
import com.netease.nimlib.sdk.msg.model.IMMessage

/**
 * @Author:summer
 * @Date:2019/9/6
 * @Description:领取红包dialog
 */
class ReceiveRedPackageDialog(context: Context) : BaseDialog(context), View.OnClickListener {

    private lateinit var mIvPortrait: ImageView
    private lateinit var mTvUserName: TextView
    private lateinit var mTvDes: TextView
    private lateinit var mTvYuan: TextView
    private lateinit var mTvAmount: TextView
    private lateinit var mIvOpen: ImageView
    private lateinit var mTvDetail: TextView
    private lateinit var mIvClose: ImageView
    private lateinit var mClAmount: View
    private lateinit var mRotateAnim: Animation

    private var mIsShowDetailEntrance = true
    private var mMessage: IMMessage? = null
    private var mRedPackageTicket: RedPackageTicketBean? = null
    private lateinit var mOnOpenButtonClickListener: ((view: View) -> Unit)
    private lateinit var mOnDetailClickListener: ((view: View) -> Unit)

    override fun getLayoutId(): Int {
        return R.layout.dialog_radpackage_notopen
    }

    override fun initView() {
        mIvPortrait = findViewById(R.id.iv_portrait)
        mTvUserName = findViewById(R.id.tv_user_name)
        mTvDes = findViewById(R.id.tv_des)
        mTvYuan = findViewById(R.id.tv_yuan_txt)
        mTvAmount = findViewById(R.id.tv_redpackage_amount)
        mIvOpen = findViewById(R.id.iv_open)
        mTvDetail = findViewById(R.id.tv_detail)
        mIvClose = findViewById(R.id.iv_close)
        mClAmount = findViewById(R.id.cl_amount)

        setLayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun initData() {
        mMessage?.let {
            GlideLoader.loadImage(
                context,
                UserInfoHelper.getUserHeadImage(mMessage?.fromAccount),
                mIvPortrait
            )
            mTvUserName.text = "${mMessage?.fromNick}的红包"
            mTvDetail.visibility = View.VISIBLE
            mTvAmount.visibility = View.VISIBLE
            mTvYuan.visibility = View.VISIBLE
            mTvDes.visibility = View.VISIBLE
            mTvUserName.visibility = View.VISIBLE
            mIvOpen.visibility = View.VISIBLE
            mClAmount.visibility = View.VISIBLE

            if (!mIsShowDetailEntrance) {
                mTvDetail.visibility = View.GONE
            }

            if (mRedPackageTicket == null) { //红包已过期
                mClAmount.visibility = View.GONE
                mIvOpen.visibility = View.GONE
                //mTvDetail.visibility = View.GONE
                mTvDes.text = "该红包已超过24小时。 如已领取，可在“红包记录”中查看。"
                return
            }

            val attachment = mMessage?.attachment as? RedPackageAttachment
            attachment?.let {
                mTvDes.text = it.getmTitle()
                mTvAmount.text = "${mRedPackageTicket?.amount}"
            }

            when (mRedPackageTicket?.result) { // 1:可以抢 2:抢完了  3:已抢过  4:拒绝自己领普通红包 5:无效红包
                RedPackageConstant.RED_PACKAGE_STATUS_ENABLE -> {
                    mTvAmount.visibility = View.GONE
                    mTvYuan.visibility = View.GONE
                    mTvDetail.visibility = View.GONE
                }
                RedPackageConstant.RED_PACKAGE_STATUS_ROBBED -> {
                    mTvAmount.visibility = View.GONE
                    mTvYuan.visibility = View.GONE
                    mIvOpen.visibility = View.GONE
                    //判断发红包的人
                    if (NimUIKitImpl.getAccount() == attachment?.getmUserId()) {
                        mTvDes.text = "你的红包已被领取"
                    } else {
                        mTvDes.text = "手慢了，红包派完了"
                    }
                }
                RedPackageConstant.RED_PACKAGE_STATUS_ALREADY_ROB -> {
                    mTvDes.visibility = View.GONE
                    mIvOpen.visibility = View.GONE
                }
                RedPackageConstant.RED_PACKAGE_STATUS_SELF -> {
                    //不能领自己发的普通红包
                }
                RedPackageConstant.RED_PACKAGE_STATUS_INVALID -> {
                    mTvAmount.visibility = View.GONE
                    mTvYuan.visibility = View.GONE
                    mIvOpen.visibility = View.GONE
                    //mTvDetail.visibility = View.GONE
                    mTvDes.text = "该红包已超过24小时。如已领取，可在“红包记录”中查看。"
                }
                else -> {
                    mClAmount.visibility = View.GONE
                    mIvOpen.visibility = View.GONE
                    //mTvDetail.visibility = View.GONE
                    mTvDes.text = "该红包已超过24小时。 如已领取，可在“红包记录”中查看。"
                }
            }
        }
    }

    override fun initEvent() {
        mIvClose.setOnClickListener(this)
        mIvOpen.setOnClickListener(this)
        mTvDetail.setOnClickListener(this)
        this.setOnDismissListener {

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close -> {
                dismiss()
            }
            R.id.iv_open -> {
                if (::mOnOpenButtonClickListener.isInitialized) {
                    mOnOpenButtonClickListener.invoke(v)
                    startRotateAnim()
                }
            }
            R.id.tv_detail -> {
                if (::mOnDetailClickListener.isInitialized) {
                    mOnDetailClickListener.invoke(v)
                }
                dismiss()
            }
            else -> {
                if (::mRotateAnim.isInitialized) {
                    mIvOpen.clearAnimation()
                }
            }
        }
    }

    override fun initAnima() {
        //设置弹出收起动画
        window!!.setWindowAnimations(R.style.redPackageDialogAnim)
    }

    /**
     * 开启动画
     * */
    private fun startRotateAnim() {
        mRotateAnim = Rotate3DAnimation()
        mRotateAnim.repeatCount = Animation.INFINITE
        mIvOpen.setImageResource(R.drawable.hb_bg_gold)
        mIvOpen.startAnimation(mRotateAnim)
    }

    fun stopRotateAnim() {
        mIvOpen.setImageResource(R.drawable.selector_packet_icon_open)
        mIvOpen.clearAnimation()
        dismiss()
    }

    fun bindMessage(message: IMMessage): ReceiveRedPackageDialog {
        mMessage = message
        return this
    }

    fun bindRedPackageTicket(ticket: RedPackageTicketBean?): ReceiveRedPackageDialog {
        mRedPackageTicket = ticket
        return this
    }

    fun isShowDetailEntrance(b: Boolean): ReceiveRedPackageDialog {
        mIsShowDetailEntrance = b
        return this
    }

    fun setOpenButtonClickListener(listener: (view: View) -> Unit) {
        this.mOnOpenButtonClickListener = listener
    }

    fun setOnDetailClickListener(listener: (view: View) -> Unit) {
        this.mOnDetailClickListener = listener
    }


}