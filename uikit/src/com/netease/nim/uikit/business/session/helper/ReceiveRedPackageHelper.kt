package com.netease.nim.uikit.business.session.helper

import android.app.Activity
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.widget.dialog.LoadingDialog
import com.netease.nim.uikit.R
import com.netease.nim.uikit.business.ChatBusinessNavigator
import com.netease.nim.uikit.business.session.attachment.RedPackageAttachment
import com.netease.nim.uikit.business.session.attachment.RobRedPackageAttachment
import com.netease.nim.uikit.business.session.constant.RedPackageConstant
import com.netease.nim.uikit.business.session.constract.RedPackageConstract
import com.netease.nim.uikit.business.session.dialog.ReceiveRedPackageDialog
import com.netease.nim.uikit.business.session.model.bean.RedPackageDetailHeader
import com.netease.nim.uikit.business.session.model.bean.RedPackageResponse
import com.netease.nim.uikit.business.session.model.bean.RedPackageTicketBean
import com.netease.nim.uikit.business.session.presenter.RedPackagePresenter
import com.netease.nim.uikit.common.ToastHelper
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.impl.NimUIKitImpl
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.model.IMMessage
import org.json.JSONObject
import java.util.HashMap

/**
 * @Author:summer
 * @Date:2019/10/12
 * @Description:抢红包helper
 */
class ReceiveRedPackageHelper : RedPackageConstract.IRedPackageView {

    private lateinit var mAttachment: RobRedPackageAttachment
    private lateinit var mCurrentMessage: IMMessage
    private var mActivity: Activity
    private var mLoadingDialog: LoadingDialog? = null
    private lateinit var receiveRedpackageDialog: ReceiveRedPackageDialog
    private var mPresenter: RedPackageConstract.IRedPackagePresenter = RedPackagePresenter()
    private lateinit var mRefreshListListener: (() -> Unit)

    constructor(activity: Activity) {
        mActivity = activity
        mPresenter.setView(this)
        mLoadingDialog = LoadingDialog(mActivity)
    }

    fun setRefreshListListener(listener: (() -> Unit)) {
        mRefreshListListener = listener
    }

    private fun showReceiveRedPackageDialog(bean: RedPackageTicketBean?) {
        receiveRedpackageDialog = ReceiveRedPackageDialog(mActivity)
        receiveRedpackageDialog.bindMessage(mCurrentMessage)
        receiveRedpackageDialog.bindRedPackageTicket(bean)
        receiveRedpackageDialog.show()
        receiveRedpackageDialog.setOpenButtonClickListener { view ->
            if (view.id == R.id.iv_open) {
                val json = JSONObject()
                try {
                    json.put("hongbao_no", mAttachment.getmHongBaoNo())
                    json.put("user_id", mAttachment.getmUserId())
                    json.put("to_id", mAttachment.getmToId())
                    json.put("op_user", NimUIKitImpl.getAccount())//领红包的人
                    json.put("ope", mAttachment.ope) //0:个人  1:群
                    mPresenter.robRedPackage(json)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ToastHelper.showToast(mActivity, "查询红包失败，请稍后重试")
                }
            }
            null
        }
        receiveRedpackageDialog.setOnDetailClickListener {
            //详情入口
            //todo 加载红包头部
            onLoadRedPackageDetailHeader()
            null
        }
    }

    //点击抢红包进入
    fun onReceiveRedPackageStart(message: IMMessage) {
        mAttachment = message.attachment as RobRedPackageAttachment
        mCurrentMessage = message
        //过期时间验证
        mPresenter.onLoadUnixtime()
    }

    //抢红包通知点击进入
    fun onRedPackageTicketStart(message: IMMessage) {
        mAttachment = message.attachment as RobRedPackageAttachment
        mCurrentMessage = message
        onLoadRedPackageDetailHeader()
    }

    //红包记录item点击进入
    fun onRedPackageRecordClickStart(attachment: RobRedPackageAttachment) {
        mAttachment = attachment
        onLoadRedPackageDetailHeader()
    }


    override fun onLoadUnixtimeSuccess(time: String) {
        LogUtils.info("time---------$time")
        if (!StringUtil.isEmpty(time)) {
            val attachment = mAttachment as RedPackageAttachment
            LogUtils.info("redpackage_time---------${attachment.getmExpiredAt()}")
            val unitise = time.toLong() - attachment.getmExpiredAt()
            LogUtils.info("unitise_time---------$unitise")
            if (unitise > 0) {
                LogUtils.info("time---------已过期")
                //已过期
                dissLoadingDialog()
                showReceiveRedPackageDialog(null)
                //更新用户item
                val params = HashMap<String, Any>()
                params[RedPackageConstant.KEY_RED_PACKAGE_CLIENT_STATUS] =
                    RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_OVERDUE
                mCurrentMessage.localExtension = params
                NIMClient.getService(MsgService::class.java).updateIMMessage(mCurrentMessage)
                refreshMessageList()
            } else {
                LogUtils.info("time---------未过期")
                //未过期
                onPullDownRedPackageStart(mCurrentMessage)
            }
        }
    }

    //拆红包start
    private fun onPullDownRedPackageStart(message: IMMessage) {
        val attachment = message.attachment as RedPackageAttachment
        val json = JSONObject()
        try {
            json.put("hongbao_no", attachment.getmHongBaoNo())
            json.put("user_id", attachment.getmUserId())
            json.put("to_id", attachment.getmToId())
            json.put("op_user", NimUIKitImpl.getAccount())//领红包的人
            json.put("ope", attachment.ope) //0:个人  1:群
            //拆红包前的一个接口
            mPresenter.redPackageTicket(json)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastHelper.showToast(mActivity, "查询红包失败，请稍后重试")
        }
    }

    override fun redPackageTicketSuccess(bean: RedPackageTicketBean) {
        val params = HashMap<String, Any>()
        params[RedPackageConstant.KEY_RED_PACKAGE_CLIENT_STATUS] =
            bean.client_status.toString() + ""
        mCurrentMessage.localExtension = params
        NIMClient.getService(MsgService::class.java).updateIMMessage(mCurrentMessage)
        if (bean.result != RedPackageConstant.RED_PACKAGE_STATUS_SELF) {//可以抢
            dissLoadingDialog()
            refreshMessageList()
            showReceiveRedPackageDialog(bean)
        } else {//不可抢（自己发的普通红包不可抢）
            //todo 加载红包头部
            onLoadRedPackageDetailHeader()
        }
    }

    override fun redPackageTicketFail(msg: String) {

    }

    //拆红包成功
    override fun onRobRedPackageSuccess(response: RedPackageTicketBean) {
        val params = HashMap<String, Any>()
        params[RedPackageConstant.KEY_RED_PACKAGE_CLIENT_STATUS] =
            response.client_status.toString() + ""
        mCurrentMessage.localExtension = params
        NIMClient.getService(MsgService::class.java).updateIMMessage(mCurrentMessage)
        refreshMessageList()
        receiveRedpackageDialog.stopRotateAnim()
        //todo 加载红包头部
        onLoadRedPackageDetailHeader()
    }

    override fun onRobRedPackageFail(msg: String) {
        receiveRedpackageDialog.dismiss()
    }

    private fun onLoadRedPackageDetailHeader() {
        val json = JSONObject()
        json.put("hongbao_no", mAttachment?.getmHongBaoNo())
        json.put("user_id", mAttachment?.getmUserId())
        json.put("to_id", mAttachment?.getmToId())
        json.put("op_user", NimUIKitImpl.getAccount())
        json.put("ope", mAttachment?.ope)
        mPresenter.onLoadRedPackageDetailHeader(json)
    }

    override fun onLoadRedPackageDetailHeaderSuccess(header: RedPackageDetailHeader) {
        ChatBusinessNavigator.startRedPackageDetailActivity(mActivity, mAttachment, header)
    }


    private fun refreshMessageList() {
        if (::mRefreshListListener.isInitialized) {
            mRefreshListListener.invoke()
        }
    }


    override fun onLoadRedPackageDetailHeaderFail(msg: String) {

    }

    override fun showData(data: RedPackageResponse) {

    }

    override fun showLoading() {

    }

    override fun dismissLoading() {

    }

    override fun showLoadingDialog() {
        if (mLoadingDialog != null && !mActivity.isFinishing && !mActivity.isDestroyed
        ) {
            mLoadingDialog?.show()
        }
    }

    override fun dissLoadingDialog() {
        if (mLoadingDialog != null && !mActivity.isFinishing && !mActivity.isDestroyed) {
            mLoadingDialog?.dismiss()
        }
    }

    fun finish() {
        dismissLoading()
        mLoadingDialog = null
    }

    fun onDestory() {
        mPresenter.onViewDestroy()
    }


    override fun showErrorMsg(msg: String?) {

    }
}