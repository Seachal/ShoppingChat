package com.netease.nim.uikit.business

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.laka.androidlib.util.BaseActivityNavigator
import com.netease.nim.uikit.api.model.session.SessionCustomization
import com.netease.nim.uikit.business.session.activity.P2PMessageActivity
import com.netease.nim.uikit.business.session.activity.TeamMessageActivity
import com.netease.nim.uikit.business.session.activity.redpackage.RobRedPackageDetailActivity
import com.netease.nim.uikit.business.session.activity.redpackage.RedPackageHelperActivity
import com.netease.nim.uikit.business.session.activity.redpackage.RedPackageRecordActivity
import com.netease.nim.uikit.business.session.activity.redpackage.SendRedPackageActivity
import com.netease.nim.uikit.business.session.attachment.RobRedPackageAttachment
import com.netease.nim.uikit.business.session.constant.Extras
import com.netease.nim.uikit.business.session.model.bean.RedPackageDetailHeader
import com.netease.nim.uikit.business.session.model.bean.RedPackageTicketBean
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage

/**
 * @Author:summer
 * @Date:2019/9/2
 * @Description:聊天模块的导航跳转类
 */
object ChatBusinessNavigator {

    /**
     * @param context 上下文
     * @param contactId 联系人ID
     * @param customization 定制化聊天页面参数
     * @param anchor 消息实体
     * */
    @JvmStatic
    fun startP2pMessageActivity(
        context: Context,
        contactId: String,
        customization: SessionCustomization,
        anchor: IMMessage
    ) {
        val intent = Intent()
        intent.putExtra(Extras.EXTRA_ACCOUNT, contactId)
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization)
        intent.putExtra(Extras.EXTRA_ANCHOR, anchor)
        intent.setClass(context, P2PMessageActivity::class.java)
        //设置启动模式
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }

    /**
     *
     * @param context
     * @param tid 提示消息
     * @param customization 聊天页面定制化参数
     * @param backToClass
     * @param anchor 消息实体
     * */
    @JvmStatic
    fun starTeamMessageActivity(
        context: Context,
        tid: String,
        customization: SessionCustomization,
        backToClass: Class<out Activity>,
        anchor: IMMessage?
    ) {
        val intent = Intent()
        intent.putExtra(Extras.EXTRA_ACCOUNT, tid)
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization)
        intent.putExtra(Extras.EXTRA_BACK_TO_CLASS, backToClass)
        if (anchor != null) {
            intent.putExtra(Extras.EXTRA_ANCHOR, anchor)
        }
        intent.setClass(context, TeamMessageActivity::class.java)
        //设置启动模式
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }

    /**
     * 打开发红包页面
     * */
    @JvmStatic
    fun startSendRedPackageActivity(
        context: Activity,
        sessionType: SessionTypeEnum,
        account: String
    ) {
        val bundle = Bundle()
        bundle.putInt("sessionType", sessionType.value)
        bundle.putString("account", account)
        BaseActivityNavigator.startActivity(
            context,
            SendRedPackageActivity::class.java,
            bundle
        )
    }

    @JvmStatic
    fun startRedPackageRecordActivity(context: Context) {
        BaseActivityNavigator.startActivity(context, RedPackageRecordActivity::class.java)
    }

    @JvmStatic
    fun startRedPackageHelperActivity(context: Context) {
        BaseActivityNavigator.startActivity(context, RedPackageHelperActivity::class.java)
    }

    @JvmStatic
    fun startRedPackageDetailActivity(
        context: Context,
        attachment: RobRedPackageAttachment,
        detailHeader: RedPackageDetailHeader
    ) {
        val bundle = Bundle()
        bundle.putSerializable("attachment", attachment)
        bundle.putSerializable("detail_header", detailHeader)
        BaseActivityNavigator.startActivity(
            context,
            RobRedPackageDetailActivity::class.java,
            bundle
        )
    }

}