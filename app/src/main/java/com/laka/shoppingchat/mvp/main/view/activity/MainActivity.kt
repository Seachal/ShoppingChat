package com.laka.shoppingchat.mvp.main.view.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.laka.androidlib.util.*
import com.laka.androidlib.widget.dialog.CommonConfirmDialog
import com.laka.androidlib.widget.dialog.LoadingDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.util.update.UpdateManager
import com.laka.shoppingchat.mvp.constant.MainConstant
import com.laka.shoppingchat.mvp.launch.constant.LaunchConstant
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.main.constant.HomeConstant
import com.laka.shoppingchat.mvp.main.helper.MagicTabHelper
import com.laka.shoppingchat.mvp.main.view.adapter.HomePagerAdapter
import com.laka.shoppingchat.mvp.main.view.fragment.HomeFragment
import com.laka.shoppingchat.mvp.nim.fragment.ContactListFragment
import com.laka.shoppingchat.mvp.nim.fragment.SessionListFragment
import com.laka.shoppingchat.mvp.nim.main.helper.SystemMessageUnreadManager
import com.laka.shoppingchat.mvp.nim.main.reminder.ReminderId
import com.laka.shoppingchat.mvp.nim.main.reminder.ReminderItem
import com.laka.shoppingchat.mvp.nim.main.reminder.ReminderManager
import com.laka.shoppingchat.mvp.nim.session.SessionHelper
import com.laka.shoppingchat.mvp.nim.team.TeamCreateHelper
import com.laka.shoppingchat.mvp.user.helper.AddFriendHelper
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.laka.shoppingchat.mvp.wallet.constract.IWalletConstract
import com.laka.shoppingchat.mvp.wallet.model.bean.MyWalletBean
import com.laka.shoppingchat.mvp.wallet.presenter.WalletPresenter
import com.netease.nim.uikit.api.model.main.LoginSyncDataStatusObserver
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity
import com.netease.nim.uikit.common.ToastHelper
import com.netease.nim.uikit.common.activity.UI
import com.netease.nim.uikit.common.ui.drop.DropManager
import com.netease.nim.uikit.impl.cache.DataCacheManager
import com.netease.nim.uikit.impl.cache.TeamDataCache
import com.netease.nim.uikit.support.permission.MPermission
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionDenied
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionGranted
import com.netease.nim.uikit.support.permission.annotation.OnMPermissionNeverAskAgain
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.NimIntent
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.friend.model.AddFriendNotify
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.SystemMessageObserver
import com.netease.nimlib.sdk.msg.SystemMessageService
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.constant.SystemMessageType
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.SystemMessage
import kotlinx.android.synthetic.main.activity_home.*
import net.lucode.hackware.magicindicator.MagicIndicator

/**
 * 主界面
 * Created by huangjun on 2015/3/25.
 */
class MainActivity : UI(), ReminderManager.UnreadNumChangedCallback {


    //    private var pager: NoScrollViewPager? = null
    private lateinit var magicIndicator: MagicIndicator
    private val scrollState: Int = 0
    //是否展示广告页
    private var isShow = false
    private var isFirstIn: Boolean = false
    //广告页是否在展示中
    private var advertIsShowing = false
    lateinit var magicTabHelper: MagicTabHelper
    private var currentIndex = 0
    private val fragmentList = ArrayList<Fragment>()
    lateinit var mAdapter: HomePagerAdapter
    private var mLogoutConfirmDialog: CommonConfirmDialog? = null
    private lateinit var mAddFriendHelper: AddFriendHelper

    private var mOnBackTime = 0L
    private val onClickTimeInterval = 1000
    private lateinit var loadding: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        StatusBarUtil.setTranslucentForImageView(this)
        StatusBarUtil.setLightMode(this)
        isFirstIn = true
        //不保留后台活动，从厂商推送进聊天页面，会无法退出聊天页面
        if (savedInstanceState == null && parseIntent()) {
            return
        }
        init()
    }

    private fun init() {
        mAddFriendHelper = AddFriendHelper(this)
        //DataCacheManager.buildDataCache()
        observerSyncDataComplete()
        initIntent()
        findViews()
        setupPager()
        registerMsgUnreadInfoObserver(true)
        registerSystemMessageObservers(true)
        requestSystemMessageUnreadCount()
        requestBasicPermission()
        rebuildData()
        initHttp()
    }

    private fun initHttp() {
        val walletPresenter = WalletPresenter()
        walletPresenter.setView(object : IWalletConstract.IWalletView {
            override fun showData(data: MyWalletBean) {

            }

            override fun showLoading() {

            }

            override fun dismissLoading() {

            }

            override fun showErrorMsg(msg: String?) {

            }
        })
        walletPresenter.onLoadMyWallet()
        //检查更新
        val updateManager = UpdateManager(this)
        updateManager.checkUpdate(false)
    }

    private fun rebuildData() {
        try {
            if (UserUtils.isLogin()) {
                DataCacheManager.clearDataCache()
                DataCacheManager.buildDataCache()
                TeamDataCache.getInstance().clear()
                TeamDataCache.getInstance().buildCache()
            }
        } catch (e: Exception) {

        }
    }

    /**
     * description:初始化主页Fragment
     */
    private fun initHomeFragment() {
        fragmentList.clear()
        fragmentList.add(HomeFragment.newInstance(MainConstant.HOMEPAGE_SHOPPING))
        fragmentList.add(HomeFragment.newInstance(MainConstant.HOMEPAGE_RECENT_CONTACT))
        fragmentList.add(HomeFragment.newInstance(MainConstant.HOMEPAGE_CONTRACT_LIST))
        fragmentList.add(HomeFragment.newInstance(MainConstant.HOMEPAGE_MINE))
        fragmentList.add(HomeFragment.newInstance(MainConstant.HOMEPAGE_ADVERT))
        mAdapter = HomePagerAdapter(supportFragmentManager, fragmentList)
        vp_home_container.adapter = mAdapter
        if (isShow) {
            vp_home_container!!.currentItem = 4
        } else {
            vp_home_container!!.currentItem = 0
        }
    }

    private fun initIntent() {
        intent?.extras?.let {
            isShow = it.getBoolean(LaunchConstant.ADVERT_IS_SHOW, false)
        }
    }

    private fun parseIntent(): Boolean {
        val intent = intent
        if (intent.hasExtra(EXTRA_APP_QUIT)) {
            intent.removeExtra(EXTRA_APP_QUIT)
            onLogout()
            return true
        }
        if (intent.hasExtra(EXTRA_APP_LOGIN)) {
            intent.removeExtra(EXTRA_APP_LOGIN)
            onLogin()
            return true
        }
        if (intent.hasExtra(EXTRA_APP_KICK_OUT)) {
            intent.removeExtra(EXTRA_APP_KICK_OUT)
            var code = intent.getIntExtra(EXTRA_APP_KICK_OUT_CODE, 0)
            onKickOut(code)
            return true
        }

        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            val message = intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT) as IMMessage
            intent.removeExtra(NimIntent.EXTRA_NOTIFY_CONTENT)
            when (message.sessionType) {
                SessionTypeEnum.P2P -> {
                    SessionHelper.startP2PSession(this, message.sessionId)
                }
                SessionTypeEnum.Team -> SessionHelper.startTeamSession(this, message.sessionId)
            }
            return true
        }
        return false
    }

    private fun observerSyncDataComplete() {
        val syncCompleted = LoginSyncDataStatusObserver.getInstance()
            .observeSyncDataCompletedEvent {
                if (::loadding.isInitialized) {
                    loadding.dismiss()
                }
            }
        //如果数据没有同步完成，弹个进度Dialog
        if (!syncCompleted) {
            if (!::loadding.isInitialized) {
                loadding = LoadingDialog(this)
            }
            loadding.show()
        }
    }

    private fun findViews() {
        magicIndicator = findView(R.id.magicIndicator)
        magicTabHelper = MagicTabHelper()
        magicTabHelper.initMagicIndicator(this, magicIndicator, vp_home_container!!)
        magicTabHelper.setOnSelectIndexListener(object : MagicTabHelper.onSelectIndexListener {
            override fun onSelectIndex(index: Int) {
                if (!advertIsShowing) {
                    currentIndex = index
                    vp_home_container!!.currentItem = index
                }
            }
        })
    }

    private fun setupPager() {
        initHomeFragment()
    }


    /**
     * 注册未读消息数量观察者
     */
    private fun registerMsgUnreadInfoObserver(register: Boolean) {
        if (register) {
            ReminderManager.getInstance().registerUnreadNumChangedCallback(this)
        } else {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(this)
        }
    }

    /**
     * 注册/注销系统消息未读数变化
     */
    private fun registerSystemMessageObservers(register: Boolean) {
        NIMClient.getService(SystemMessageObserver::class.java)
            .observeReceiveSystemMsg(mSystemMessageObserver, register)
    }

    private val mSystemMessageObserver = Observer<SystemMessage> {
        when (it.type) {
            SystemMessageType.AddFriend -> {
                addFriendDeal(it)
            }
            SystemMessageType.ApplyJoinTeam -> {
                applyJoinTeamDeal(it)
            }
            else -> {
                NIMClient.getService(SystemMessageService::class.java)
                    .deleteSystemMessage(it.messageId)
            }
        }
    }

    private fun applyJoinTeamDeal(it: SystemMessage) {
        SystemMessageUnreadManager.getInstance().sysMsgUnreadCount = 1
        magicTabHelper.showTip(true)
        ReminderManager.getInstance().updateContactUnreadNum(1)
        var number = SPHelper.getInt("${UserUtils.getImAccount()}", 0)
        number++
        SPHelper.putInt("${UserUtils.getImAccount()}", number)
    }

    private fun addFriendDeal(it: SystemMessage) {
        val attachData = it.attachObject
        if (attachData is AddFriendNotify) {
            when (attachData.event) {
                AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST -> {
                    SystemMessageUnreadManager.getInstance().sysMsgUnreadCount = 1
                    magicTabHelper.showTip(true)
                    ReminderManager.getInstance().updateContactUnreadNum(1)
                    var number = SPHelper.getInt("${UserUtils.getImAccount()}", 0)
                    number++
                    SPHelper.putInt("${UserUtils.getImAccount()}", number)
                }
                AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT -> {
                    val msg = MessageBuilder.createTextMessage(
                        it.fromAccount, SessionTypeEnum.P2P,
                        it.getContent()
                    )
                    msg.direct = MsgDirectionEnum.In
                    msg.status = MsgStatusEnum.success
                    msg.fromAccount = it.fromAccount
                    NIMClient.getService(MsgService::class.java).saveMessageToLocal(msg, true)
                }
                AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND -> {
//                    val msg = MessageBuilder.createTextMessage(
//                        it.fromAccount, SessionTypeEnum.P2P,
//                        "我通过了你的朋友验证请求，现在我们可以开始聊天了"
//                    )
//                    LogUtils.debug("it.targetId" + it.targetId + "----" + it.fromAccount)
//                    msg.direct = MsgDirectionEnum.In
//                    msg.status = MsgStatusEnum.success
//                    msg.fromAccount = it.fromAccount
//                    NIMClient.getService(MsgService::class.java).saveMessageToLocal(msg, true)
                    NIMClient.getService(SystemMessageService::class.java)
                        .deleteSystemMessage(it.messageId)
                }
                else -> NIMClient.getService(SystemMessageService::class.java)
                    .deleteSystemMessage(it.messageId)
            }
        }
    }

    /**
     * 查询系统消息未读数
     */
    private fun requestSystemMessageUnreadCount() {
        val unread = NIMClient.getService(SystemMessageService::class.java)
            .querySystemMessageUnreadCountBlock()
        SystemMessageUnreadManager.getInstance().sysMsgUnreadCount = unread
        ReminderManager.getInstance().updateContactUnreadNum(unread)
    }

    private fun requestBasicPermission() {
        MPermission.printMPermissionResult(true, this, BASIC_PERMISSIONS)
        MPermission.with(this@MainActivity)
            .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
            .permissions(*BASIC_PERMISSIONS)
            .request()
    }

    private fun onKickOut(code: Int) {
        SPHelper.clearByFileName(LoginConstant.USER_INFO_FILENAME)
        UserUtils.clearUserInfo()
        currentIndex = 0
        magicTabHelper.setMsgNums(0)
        vp_home_container.currentItem = currentIndex
        if (mLogoutConfirmDialog == null) {
            mLogoutConfirmDialog = CommonConfirmDialog(this)
            mLogoutConfirmDialog?.setOnClickSureListener {
                LoginModuleNavigator.startLoginActivity(this)
            }
        }
        mLogoutConfirmDialog?.show()
        mLogoutConfirmDialog?.setDefaultTitleTxt(createText(code))
    }

    private fun createText(code: Int): String {
        var statusCode = StatusCode.statusOfResCode(code)
        return when (statusCode) {
            StatusCode.FORBIDDEN -> "您的账号已被禁止登录"
            StatusCode.PWD_ERROR -> "用户名或密码错误"
            else -> "您的账号在别处登录，请重新登录"
        }
    }

    private fun onLogin() {
        DataCacheManager.clearDataCache()
        DataCacheManager.buildDataCache()
        TeamDataCache.getInstance().clear()
        TeamDataCache.getInstance().buildCache()
        currentIndex = 0
        magicTabHelper.setMsgNums(0)
        vp_home_container.currentItem = currentIndex
//        loadData()
    }

    public fun loadData() {
        var fragment = fragmentList[1]
        if (fragment is SessionListFragment) {
            fragment.reloadData()
        }
        var fragment2 = fragmentList[2]
        if (fragment2 is ContactListFragment) {
            fragment2.reloadData()
        }
    }

    private fun onLogout() {
        //切换到购物Fragment
        magicTabHelper.setMsgNums(0)
        currentIndex = 0
        vp_home_container.currentItem = currentIndex
        DataCacheManager.clearDataCache()
        //        Preferences.saveUserToken("");
        //        // 清理缓存&注销监听
        //        LogoutHelper.logout();
        //        // 启动登录
        //        LoginActivity.start(this);
    }


    /**
     * 设置最近联系人的消息为已读
     *
     *
     * account, 聊天对象帐号，或者以下两个值：
     * [MsgService.MSG_CHATTING_ACCOUNT_ALL] 目前没有与任何人对话，但能看到消息提醒（比如在消息列表界面），不需要在状态栏做消息通知
     * [MsgService.MSG_CHATTING_ACCOUNT_NONE] 目前没有与任何人对话，需要状态栏消息通知
     */
    private fun enableMsgNotification(enable: Boolean) {
//        val msg = vp_home_container.currentItem == 1
        if (enable) {
            NIMClient.getService(MsgService::class.java)
                .setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None)
        } else {
            NIMClient.getService(MsgService::class.java)
                .setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            HomeConstant.REQUEST_CODE_INTO_ACTIVITS_POPUP_DETAIL -> {
//                handleClipBoardNavigator()
//                clickPopup = false
            }
            SessionListFragment.REQUEST_CODE_ADVANCED -> {
                val selected = data?.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA)
                selected?.let {
                    if (selected.size == 1) {
                        SessionHelper.startP2PSession(this, selected[0])
                    } else {
                        TeamCreateHelper.createAdvancedTeam(this, it)
                    }
                }
            }
            LoginConstant.REQUEST_SCAN_QR_CODE -> { //扫描二维码加好友
                if (resultCode != LoginConstant.RESULT_SCAN_QR_CODE) return
                val scanCode = data?.getStringExtra(LoginConstant.RESULT_SCAN_QR)
                if (!StringUtils.isEmpty(scanCode)) {
                    mAddFriendHelper.handleAddFriend("$scanCode")
                } else {
                    ToastHelper.showToast(this, "扫描数据为空")
                }
            }
        }

        //从聊天页面返回会话列表页面，刷新列表
//        if (resultCode == SessionConstant.SESSION_PAGE_CLOSE_RESULT_EVENT) {//刷新会话列表
//            val fragment = fragmentList[1]
//            if (fragment is SessionListFragment) {
//                fragment.refreshSessionList()
//            }
//        }

        //将data传给CircleFragmnet处理
        //data?.putExtra(HomeConstant.KEY_REQUEST_CODE, requestCode)
        //data?.putExtra(HomeConstant.KEY_RESULT_CODE, resultCode)
        //EventBusManager.postEvent(HomeEventConstant.EVENT_BIND_RELEATION_ID_SUCCESS, data)
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        parseIntent()
    }

    public override fun onResume() {
        super.onResume()
        // 第一次 ， 三方通知唤起进会话页面之类的，不会走初始化过程
        val temp = isFirstIn
        isFirstIn = false
        if (::magicTabHelper.isInitialized && temp) {
            return
        }
        //如果不是第一次进 ， eg: 其他页面back
        if (!::magicTabHelper.isInitialized) {
            init()
        }
        enableMsgNotification(false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    public override fun onPause() {
        super.onPause()
        if (vp_home_container == null) {
            return
        }
        enableMsgNotification(true)
    }

    public override fun onDestroy() {
        try {
            registerMsgUnreadInfoObserver(false)
            registerSystemMessageObservers(false)
            DropManager.getInstance().destroy()
            mAddFriendHelper.onDestroy()
            super.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
            System.exit(0)
        }
    }

    //未读消息数量观察者实现
    override fun onUnreadNumChanged(item: ReminderItem) {
        if (item.id == ReminderId.SESSION) {
            magicTabHelper.setMsgNums(item.unread)
        } else if (item.id == ReminderId.CONTACT) {
            if (item.unread == 0) {
                magicTabHelper.showTip(false)
            } else {
                magicTabHelper.showTip(true)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    fun onBasicPermissionSuccess() {
        try {
            ToastHelper.showToast(this, "授权成功")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS)
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    fun onBasicPermissionFailed() {
        try {
            ToastHelper.showToast(this, "未全部授权，部分功能可能无法正常运行！")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS)
    }

    override fun displayHomeAsUpEnabled(): Boolean {
        return false
    }

    companion object {

        private const val EXTRA_APP_QUIT = "APP_QUIT"
        private const val EXTRA_APP_LOGIN = "APP_LOGIN"
        private const val EXTRA_APP_KICK_OUT = "APP_KICK_OUT"
        private const val EXTRA_APP_KICK_OUT_CODE = "EXTRA_APP_KICK_OUT_CODE"
        private const val REQUEST_CODE_NORMAL = 1
        private const val REQUEST_CODE_ADVANCED = 2
        private const val BASIC_PERMISSION_REQUEST_CODE = 100
        private val BASIC_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        @JvmOverloads
        fun start(context: Context, extras: Intent? = null) {
            val intent = Intent()
            intent.setClass(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            if (extras != null) {
                intent.putExtras(extras)
            }
            context.startActivity(intent)
        }

        fun login(context: Context, login: Boolean) {
            val extra = Intent()
            extra.putExtra(EXTRA_APP_LOGIN, login)
            start(context, extra)
        }

        // 注销
        fun logout(context: Context, quit: Boolean) {
            val extra = Intent()
            extra.putExtra(EXTRA_APP_QUIT, quit)
            start(context, extra)
        }

        fun kickOut(context: Context, code: Int, quit: Boolean) {
            val extra = Intent()
            extra.putExtra(EXTRA_APP_KICK_OUT, quit)
            extra.putExtra(EXTRA_APP_KICK_OUT_CODE, code)
            start(context, extra)
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mOnBackTime < onClickTimeInterval) {
            super.onBackPressed()
            //System.exit(0)
        } else {
            mOnBackTime = System.currentTimeMillis()
            ToastHelper.showToast(this, "再次点击退出程序")
        }
    }
}
