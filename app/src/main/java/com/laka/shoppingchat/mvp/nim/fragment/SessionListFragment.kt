package com.laka.shoppingchat.mvp.nim.fragment

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo
import com.laka.shoppingchat.mvp.main.view.activity.MainActivity
import com.laka.shoppingchat.mvp.main.view.fragment.HomeFragment
import com.laka.shoppingchat.mvp.nim.activity.GlobalSearchActivity
import com.laka.shoppingchat.mvp.nim.constract.INimConstract
import com.laka.shoppingchat.mvp.nim.helper.PopupWindowHelper
import com.laka.shoppingchat.mvp.nim.main.reminder.ReminderManager
import com.laka.shoppingchat.mvp.nim.preference.Preferences
import com.laka.shoppingchat.mvp.nim.presenter.NimPresenter
import com.laka.shoppingchat.mvp.nim.session.SessionHelper
import com.netease.nim.uikit.business.recent.RecentContactsCallback
import com.netease.nim.uikit.business.recent.RecentContactsFragment
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.RecentContact
import kotlinx.android.synthetic.main.fragment_session_list.*
import java.util.*

class SessionListFragment : HomeFragment(), INimConstract.IBaseNimView {

    private lateinit var fragment: RecentContactsFragment
    lateinit var mHelper: PopupWindowHelper


    companion object {
        const val REQUEST_CODE_ADVANCED = 102
    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    private lateinit var mNimPresenter: NimPresenter
    override fun createPresenter(): IBasePresenter<*> {
        mNimPresenter = NimPresenter()
        return mNimPresenter
    }

    override fun setContentView(): Int = R.layout.fragment_session_list

    override fun initArgumentsData(arguments: Bundle?) {

    }

    override fun initView(rootView: View?, savedInstanceState: Bundle?) {
        title_bar
            .setBackGroundColor(R.color.color_gray_bg)
            .setTitleTextColor(R.color.color_2d2d2d)
            .setTitleTextSize(18)
            .showDivider(false)
            .setRightIcon(R.drawable.selector_session_list_add)
            .setOnRightClickListener {
                mHelper.showPop()
            }
        mHelper = PopupWindowHelper(activity!!, title_bar.rightView)
        registerObservers(true)
        addRecentContactsFragment()
        handleStatusBarOffset()
        llSearch.onClick {
            GlobalSearchActivity.start(context)
        }
    }

    private fun handleStatusBarOffset() {
        val layoutParams = cl_title_root.layoutParams as? LinearLayout.LayoutParams
        layoutParams?.let {
            val statusBarHeight = StatusBarUtil.getStatusBarHeight(activity)
            it.height = ScreenUtils.dp2px(44f) + statusBarHeight
        }
    }


    private fun addRecentContactsFragment() {
        fragment = RecentContactsFragment()
        fragment.containerId = R.id.messages_fragment

        // val activity = activity as UI
        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        // fragment = activity.addFragment(fragment) as RecentContactsFragment
        var transition = fragmentManager!!.beginTransaction()
        transition.add(R.id.flsContraint, fragment)
        transition.commitAllowingStateLoss()
        fragment.setCallback(object : RecentContactsCallback {
            override fun onRecentContactsLoaded() {
                // 最近联系人列表加载完毕
            }

            override fun onUnreadCountChange(unreadCount: Int) {
                ReminderManager.getInstance().updateSessionUnreadNum(unreadCount)
            }

            override fun onItemClick(recent: RecentContact) {
                // 回调函数，以供打开会话窗口时传入定制化参数，或者做其他动作
                when (recent.sessionType) {
                    SessionTypeEnum.P2P -> SessionHelper.startP2PSession(
                        activity,
                        recent.contactId
                    )
                    SessionTypeEnum.Team -> SessionHelper.startTeamSession(
                        activity,
                        recent.contactId
                    )
                    SessionTypeEnum.SUPER_TEAM -> ToastHelper.showToast("超大群开发者按需实现")
                    else -> {
                    }
                }
            }

            override fun getDigestOfAttachment(
                recentContact: RecentContact,
                attachment: MsgAttachment
            ): String? {
                return null
            }

            override fun getDigestOfTipMsg(recent: RecentContact): String? {
                val msgId = recent.recentMessageId
                val uuids = ArrayList<String>(1)
                uuids.add(msgId)
                val msgs =
                    NIMClient.getService(MsgService::class.java).queryMessageListByUuidBlock(uuids)
                if (msgs != null && !msgs.isEmpty()) {
                    val msg = msgs[0]
                    val content = msg.remoteExtension
                    if (content != null && !content.isEmpty()) {
                        return content["content"] as String
                    }
                }

                return null
            }
        })

    }

    private fun registerObservers(register: Boolean) {
        NIMClient.getService(AuthServiceObserver::class.java)
            .observeOnlineStatus(mOnLineStatusObserver, register)
    }

    private val mOnLineStatusObserver =
        Observer<StatusCode> { code ->
            if (code.wontAutoLogin()) {
                LogUtils.debug("wontAutoLogin" + code.value)
                kickOut(code)
            } else {
                if (code == StatusCode.NET_BROKEN) {
                    title_bar.setTitle(getString(R.string.net_broken))
                } else if (code == StatusCode.UNLOGIN) {
                    title_bar.setTitle(getString(R.string.nim_status_unlogin))
                } else if (code == StatusCode.CONNECTING) {
                    title_bar.setTitle(getString(R.string.nim_status_connecting))
                } else if (code == StatusCode.LOGINING) {
                    title_bar.setTitle(getString(R.string.nim_status_logining))
                } else if (code == StatusCode.LOGINED) {
                    title_bar.setTitle("购聊")
                    if (activity is MainActivity) {
                        (activity as MainActivity).loadData()
                    }
                } else {
                    title_bar.setTitle("购聊")
                }
            }
        }

    override fun initDataLazy() {

    }

    override fun initEvent() {

    }

    fun reloadData() {
        if (::fragment.isInitialized) {
            fragment.setMsgLoaded(false)
        }
    }

    private fun kickOut(code: StatusCode) {
        Preferences.saveUserToken("")
        if (code == StatusCode.PWD_ERROR) {
            LogUtil.e("Auth", "user password error")
            ToastHelper.showToast(R.string.login_failed)
        } else {
            LogUtil.i("Auth", "Kicked!")
        }
        onLogout(code)
    }

    override fun onDestroy() {
        registerObservers(false)//注销
        super.onDestroy()
    }

    // 注销
    private fun onLogout(code: StatusCode) {
        MainActivity.kickOut(context!!, code.value, true)
    }

    override fun getGroupIdForQrCodeSuccess(resp: QrCodeInfo) {

    }

}