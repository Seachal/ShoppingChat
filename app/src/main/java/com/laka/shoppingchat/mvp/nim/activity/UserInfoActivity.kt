package com.laka.shoppingchat.mvp.nim.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.widget.dialog.CommonConfirmDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.extraDelegate
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.nim.const.NimConstant
import com.laka.shoppingchat.mvp.nim.event.AgreeEvent
import com.laka.shoppingchat.mvp.nim.event.BlackEvent
import com.laka.shoppingchat.mvp.nim.preference.UserPreferences
import com.laka.shoppingchat.mvp.nim.session.SessionHelper
import com.laka.shoppingchat.mvp.share.weight.GlideSimpleLoader
import com.laka.shoppingchat.mvp.share.weight.imagewatcher.ImageWatcher
import com.laka.shoppingchat.mvp.share.weight.imagewatcher.ImageWatcherHelper
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import com.laka.shoppingchat.mvp.user.helper.ImUserInfoHelper
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nim.uikit.common.ToastHelper
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.impl.NimUIKitImpl
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.activty_user_info.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast


/**
 * 聊天界面用户信息页
 * */
class UserInfoActivity : BaseActivity(), ImageWatcher.OnPictureLongPressListener {

    private var iwHelper: ImageWatcherHelper? = null
    override fun onPictureLongPress(v: ImageView?, uri: Uri?, pos: Int) {

    }

    val account by extraDelegate(NimConstant.ACCOUNT, "")//用户账号
    val isFormAdd by extraDelegate(NimConstant.IS_FORM_ADD, false)//从系统消息进入好友申请
    val isTeam by extraDelegate(NimConstant.IS_TEAM, false)////从系统消息进入群申请
    val status by extraDelegate(NimConstant.SYSTEMMESSAGE_STATUS, false)//系统消息消息是否被处理的状态

    companion object {
        @JvmStatic
        fun start(context: Context, account: String) {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra(NimConstant.ACCOUNT, account)
            intent.putExtra(NimConstant.IS_FORM_ADD, false)
            context.startActivity(intent)
        }

        @JvmStatic
        fun start(
            context: Context,
            account: String,
            isFormAdd: Boolean = false,
            status: Boolean,
            isTeam: Boolean
        ) {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra(NimConstant.ACCOUNT, account)
            intent.putExtra(NimConstant.IS_FORM_ADD, isFormAdd)
            intent.putExtra(NimConstant.IS_TEAM, isTeam)
            intent.putExtra(NimConstant.SYSTEMMESSAGE_STATUS, status)
            context.startActivity(intent)
        }
    }

    override fun setContentView(): Int = R.layout.activty_user_info

    override fun initIntent() {
        if (TextUtils.isEmpty(account)) {
            ToastHelper.showToast(this@UserInfoActivity, "传入的帐号为空")
            finish()
            return
        }
    }

    override fun initViews() {
        //黑名单判断
        val isBlack = NIMClient.getService(FriendService::class.java).isInBlackList(account)
        if (NimUIKitImpl.getAccount() == account || isBlack) {
            view_line.visibility = View.GONE
            ll_add.visibility = View.GONE
        }
        initTile()
        initImageWatcher()
    }

    private fun initTile() {
        title_bar
            .setTitle(getTitleText())
            .setLeftIcon(R.drawable.selector_nav_btn_back)
            .showDivider(false)
            .setOnLeftClickListener {
                finish()
            }
        if (!StringUtil.isEmpty(account)) {
            val isMyFriend = NIMClient.getService(FriendService::class.java).isMyFriend(account)
            if (isMyFriend) {
                title_bar.setRightIcon(R.drawable.default_btn_top_more_seletor)
                    .setOnRightClickListener {
                        UserCenterModuleNavigator.startUserImInfoSettingActivity(this, account)
                    }
            }
        }
    }

    private fun getTitleText(): String {
        var title = ""
        if (isTeam && !status) {
            //群申请且消息未处理过
            title = "申请加群通知"
        } else if (isFormAdd && !status) {
            //好友申请且消息未处理过
            title = "新的好友"
        }
        return title
    }

    private fun initImageWatcher() {
        iwHelper =
            ImageWatcherHelper.with(this, GlideSimpleLoader()) // 一般来讲， ImageWatcher 需要占据全屏的位置
                .setTranslucentStatus(ScreenUtils.getStatusBarHeight()) // 如果不是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确
                .setErrorImageRes(R.drawable.default_img) // 配置error图标 如果不介意使用lib自带的图标，并不一定要调用这个API
    }

    override fun initData() {

    }

    override fun initEvent() {
        iv_user_avatar.onClick {
            val userInfo = NimUIKit.getUserInfoProvider().getUserInfo(account) as NimUserInfo?
            userInfo?.let {
                iv_user_avatar.loadAvatar(userInfo.avatar)
                var imageList = SparseArray<ImageView>()
                imageList.put(0, iv_user_avatar)
                if (!StringUtils.isEmpty(userInfo.avatar)) {
                    iwHelper?.show(iv_user_avatar, imageList, listOf(Uri.parse(userInfo.avatar)))
                } else {
                    //防止 Uri.parse 出现空指针异常
                    iwHelper?.show(iv_user_avatar, imageList, listOf(Uri.parse("......")))
                }
            }
        }
        ll_add.onClick {
            if (isTeam) {
                if (!status) {
                    //群申请且消息未处理
                    EventBusManager.postEvent(AgreeEvent(account))
                    finish()
                } else {
                    treatedClick()
                }
            } else {
                treatedClick()
            }
        }
        tv_remove.onClick {
            if (NIMClient.getService(FriendService::class.java).isMyFriend(account)) {
                val confirmDialog = CommonConfirmDialog(this)
                confirmDialog.setDefaultTitleTxt("您确定要删除好友？")
                confirmDialog.setOnClickSureListener {
                    showLoading()
                    val deleteAlias = UserPreferences.isDeleteFriendAndDeleteAlias()
                    NIMClient.getService(FriendService::class.java)
                        .deleteFriend(account, deleteAlias).setCallback(object :
                            RequestCallback<Void> {
                            override fun onSuccess(param: Void?) {
                                dismissLoading()
                                ToastHelper.showToast(
                                    this@UserInfoActivity,
                                    R.string.remove_friend_success
                                )
                                finish()
                            }

                            override fun onFailed(code: Int) {
                                dismissLoading()
                                if (code == 408) {
                                    ToastHelper.showToast(
                                        this@UserInfoActivity,
                                        R.string.network_is_not_available
                                    )
                                } else {
                                    ToastHelper.showToast(this@UserInfoActivity, "on failed:$code")
                                }
                            }

                            override fun onException(exception: Throwable) {
                                dismissLoading()
                            }
                        })
                }
                confirmDialog.show()
            }
        }

        tv_reject.onClick {
            EventBusManager.postEvent(AgreeEvent(account, false))
            finish()
        }
    }

    /**
     * 是好友跳转到聊天
     * 申请好友处理
     * 添加到通讯录
     */
    private fun treatedClick() {
        if (NIMClient.getService(FriendService::class.java).isMyFriend(account)) {
            SessionHelper.startP2PSession(this, account)
        } else {
            if (isFormAdd && !status) {
                EventBusManager.postEvent(AgreeEvent(account))
                finish()
            } else {
                if (account == UserUtils.getImAccount()) {
                    toast("不能添加自己为好友")
                } else {
                    VerifyFriendActivity.start(this, account)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserInfo()
    }

    private fun updateUserInfo() {
        if (NimUIKit.getUserInfoProvider().getUserInfo(account) != null) {
            updateUserInfoView()
            return
        }

        NimUIKit.getUserInfoProvider().getUserInfoAsync(account) { success, result, code ->
            updateUserInfoView()
        }
    }

    private fun updateUserInfoView() {
        if (isTeam) {
            if (!status) {
                tv_add.text = "同意"
                tv_reject.visibility = View.VISIBLE
            } else {
                treated()
            }
        } else {
            treated()
        }

        val userInfo = NimUIKit.getUserInfoProvider().getUserInfo(account) as NimUserInfo?
        if (userInfo == null) {
            com.laka.androidlib.util.toast.ToastHelper.showToast(R.string.user_not_exsit)
            finish()
            return
        }
        iv_user_avatar.loadAvatar(userInfo.avatar)
        //显示备注
        tv_nickname_backup.text = "昵称：${userInfo.name}"
        tv_user_name.text = ImUserInfoHelper.getDispalyNickname(account)
        val alias = ImUserInfoHelper.getUserAlias(account)
        if (StringUtil.isEmpty(alias)) {
            tv_nickname_backup.visibility = View.GONE
        } else {
            tv_nickname_backup.visibility = View.VISIBLE
        }
        iv_user_gender.setImageResource(
            if (userInfo.genderEnum == GenderEnum.MALE) {
                R.mipmap.default_icon_male
            } else {
                R.mipmap.default_icon_female
            }
        )
    }

    private fun treated() {
        if (NIMClient.getService(FriendService::class.java).isMyFriend(account)
            && NimUIKitImpl.getAccount() != account
        ) {
            val drawableLeft = resources.getDrawable(
                R.drawable.selector_default_btn_message
            )
            tv_add.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null)
            tv_add.compoundDrawablePadding = 4
            tv_add.text = "发消息"
            tv_remove.visibility = View.VISIBLE
            view_divider.visibility = View.VISIBLE
            view_bottom_line.visibility = View.VISIBLE
        } else {
            if (isFormAdd && !status) {
                tv_add.text = "同意"
                tv_reject.visibility = View.VISIBLE
            } else {
                tv_add.text = "添加到通讯录"
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: BlackEvent) {
        if (event.isAddBlack) {
            ll_add.visibility = View.GONE
            view_line.visibility = View.GONE
        } else {
            ll_add.visibility = View.VISIBLE
            view_line.visibility = View.VISIBLE
        }
    }
}