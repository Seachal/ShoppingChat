package com.laka.shoppingchat.mvp.user.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.CacheUtil
import com.laka.androidlib.util.PermissionUtils
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.util.imageload.GlideLoader
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.dialog.CommonConfirmDialog
import com.laka.androidlib.widget.dialog.LoadingDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.loadImage
import com.laka.shoppingchat.common.ext.loginHandle
import com.laka.shoppingchat.mvp.login.model.event.UserEvent
import com.laka.shoppingchat.mvp.main.view.activity.MainActivity
import com.laka.shoppingchat.mvp.nim.activity.UserQRCodeActivity
import com.laka.shoppingchat.mvp.share.weight.GlideSimpleLoader
import com.laka.shoppingchat.mvp.share.weight.imagewatcher.ImageWatcherHelper
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import com.laka.shoppingchat.mvp.user.constant.UserCenterConstant
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.laka.shoppingchat.mvp.user.constract.UserSettingConstract
import com.laka.shoppingchat.mvp.user.helper.ImUserInfoHelper
import com.laka.shoppingchat.mvp.user.helper.UserUpdateHelper
import com.laka.shoppingchat.mvp.user.presenter.UserSettingPresenter
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nim.uikit.business.session.actions.PickImageAction
import com.netease.nim.uikit.common.media.imagepicker.Constants
import com.netease.nim.uikit.common.media.imagepicker.ImagePickerLauncher
import com.netease.nim.uikit.common.media.model.GLImage
import com.netease.nimlib.sdk.AbortableFuture
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.nos.NosService
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.activity_user_setting.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.io.File
import java.util.*

class UserSettingActivity : BaseMvpActivity<String>(), UserSettingConstract.IUserSettingView,
    View.OnClickListener {

    private var mUserInfo: NimUserInfo? = null
    private lateinit var mPresenter: UserSettingPresenter

    private var iwHelper: ImageWatcherHelper? = null
    override fun setContentView(): Int = R.layout.activity_user_setting

    override fun initIntent() {

    }

    override fun createPresenter(): IBasePresenter<*> {
        mPresenter = UserSettingPresenter()
        return mPresenter
    }

    override fun initViews() {
        initImageWatcher()
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setTitle("个人信息")
            .setTitleTextColor(R.color.color_2d2d2d)
        mUserInfo = ImUserInfoHelper.getCurrentUserInfo()
    }

    override fun initData() {
        if (isFinishing || isDestroyed) return
        mUserInfo?.let {
            ivAvatar.loadAvatar(
                it.avatar
            )
            tv_nickname.text = "${mUserInfo?.name}"
            tv_mobile.text = "${mUserInfo?.mobile}"
            when (mUserInfo?.genderEnum) {
                GenderEnum.MALE -> {
                    tv_sex.text = "男"
                }
                GenderEnum.FEMALE -> {
                    tv_sex.text = "女"
                }
                GenderEnum.UNKNOWN -> {
                    tv_sex.text = "其他"
                }
            }
        }
    }

    private fun initImageWatcher() {
        iwHelper =
            ImageWatcherHelper.with(this, GlideSimpleLoader()) // 一般来讲， ImageWatcher 需要占据全屏的位置
                .setTranslucentStatus(ScreenUtils.getStatusBarHeight()) // 如果不是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确
                .setErrorImageRes(R.drawable.default_img) // 配置error图标 如果不介意使用lib自带的图标，并不一定要调用这个API
    }

    override fun initEvent() {
        ivAvatar.setOnClickListener(this)
        ll_update_head_image.setOnClickListener(this)
        ll_update_nickname.setOnClickListener(this)
        ll_update_sex.setOnClickListener(this)
        ll_update_mobile.setOnClickListener(this)
        ll_qr_code.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_update_nickname -> {
//                UserCenterModuleNavigator.startModifyNameActivity(
//                    this,
//                    tv_nickname.text.toString().trim()
//                )
                loginHandle {
                    startActivityForResult<ModifyNameActivity>(
                        UserCenterConstant.MODIFY_INFO_REQUEST_CODE,
                        "nickname" to tv_nickname.text.toString().trim()
                    )
                }
            }
            R.id.ll_update_sex -> {
                //UserCenterModuleNavigator.startModifySexActivity(this)
                loginHandle {
                    startActivityForResult<ModifySexActivity>(
                        UserCenterConstant.MODIFY_INFO_REQUEST_CODE
                    )
                }
            }
            R.id.ll_update_head_image -> {
                ImagePickerLauncher.pickImage(
                    this,
                    UserCenterConstant.PICK_AVATAR_REQUEST_CODE,
                    R.string.set_head_image
                )
            }
            R.id.ll_update_mobile -> {
                loginHandle {
                    startActivity<CurrentPhoneActivity>(
                        "mobile" to "${mUserInfo?.mobile}"
                    )
                }
            }
            R.id.ll_qr_code -> {
                loginHandle {
                    startActivity<UserQRCodeActivity>()
                }
            }
            R.id.ivAvatar -> {
                val userInfo = NimUIKit.getUserInfoProvider().getUserInfo(UserUtils.getImAccount()) as NimUserInfo?
                userInfo?.let {
                    ivAvatar.loadAvatar(
                        "${it.avatar}"
                    )
                    var imageList = SparseArray<ImageView>()
                    imageList.put(0, ivAvatar)
                    if (!StringUtils.isEmpty(userInfo.avatar)) {
                        iwHelper?.show(ivAvatar, imageList, listOf(Uri.parse("${userInfo.avatar}")))
                    } else {
                        //防止 Uri.parse 出现异常
                        iwHelper?.show(ivAvatar, imageList, listOf(Uri.parse("......")))
                    }
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UserEvent) {
        when (event.type) {
            UserConstant.EDIT_USER_INFO -> {
                initData()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            UserCenterConstant.PICK_AVATAR_REQUEST_CODE -> {
                onPicked(data)
            }
            UserCenterConstant.MODIFY_INFO_REQUEST_CODE -> {
                asyncUserInfo()
            }
        }
    }

    private fun onPicked(data: Intent?) {
        if (data == null) {
            return
        }
        val images = data.getSerializableExtra(Constants.EXTRA_RESULT_ITEMS) as ArrayList<GLImage>
        if (images == null || images.isEmpty()) {
            return
        }
        val image = images[0]
        uploadAvatar(image.path)
    }

    private var mHandler = Handler()
    private val outimeTask = Runnable { cancelUpload(R.string.user_info_update_failed) }
    private var uploadAvatarFuture: AbortableFuture<String>? = null
    private lateinit var mUploadDialog: LoadingDialog

    /**
     * 上传头像
     */
    private fun uploadAvatar(path: String) {
        if (TextUtils.isEmpty(path)) {
            return
        }
        val file = File(path) ?: return
        if (!::mUploadDialog.isInitialized) {
            mUploadDialog = LoadingDialog(this)
            mUploadDialog.setOnCancelListener { cancelUpload(R.string.user_info_update_cancel) }
        }
        mUploadDialog.show()

        mHandler.removeCallbacksAndMessages(null)
        mHandler.postDelayed(outimeTask, 20000) //超时时间
        uploadAvatarFuture =
            NIMClient.getService(NosService::class.java).upload(file, PickImageAction.MIME_JPEG)
        uploadAvatarFuture?.setCallback(object : RequestCallbackWrapper<String>() {
            override fun onResult(code: Int, url: String?, exception: Throwable?) {
                uploadAvatarFuture = null
                mHandler.removeCallbacks(outimeTask)
                if (code == ResponseCode.RES_SUCCESS.toInt() && !TextUtils.isEmpty(url)) {
                    updateAvatar(url)
                } else {
                    dismissLoadDialog()
                    ToastHelper.showCenterToast(getString(R.string.user_info_update_failed))
                }
            }
        })
    }

    private fun dismissLoadDialog() {
        if (::mUploadDialog.isInitialized) {
            mUploadDialog.dismiss()
        }
    }

    /**
     * 更改头像
     * */
    private fun updateAvatar(url: String?) {
        UserUpdateHelper.update(
            UserInfoFieldEnum.AVATAR,
            "$url",
            object : RequestCallbackWrapper<Void>() {
                override fun onResult(code: Int, result: Void?, exception: Throwable?) {
                    if (code == ResponseCode.RES_SUCCESS.toInt()) {
                        EventBusManager.postEvent(UserEvent(UserConstant.EDIT_USER_INFO))
                        ToastHelper.showCenterToast(getString(R.string.head_update_success))
                    } else {
                        ToastHelper.showToast(getString(R.string.head_update_failed))
                    }
                    onUpdateDone()
                }
            })
    }

    private fun cancelUpload(resId: Int) {
        if (uploadAvatarFuture != null) {
            uploadAvatarFuture?.abort()
            ToastHelper.showCenterToast(getString(resId))
        }
    }

    private fun onUpdateDone() {
        uploadAvatarFuture = null
        dismissLoadDialog()
        asyncUserInfo()
    }

    /**
     * 刷新本地用户信息，如果通过同步方法获取为null，则异步获取
     * */
    private fun asyncUserInfo() {
        mUserInfo = ImUserInfoHelper.getCurrentUserInfo()
        if (mUserInfo == null) {
            NimUIKit.getUserInfoProvider().getUserInfoAsync(
                UserUtils.getImAccount()
            ) { success, result, code ->
                if (success) {
                    mUserInfo = result as NimUserInfo
                    initData()
                } else {
                    ToastHelper.showCenterToast("刷新用户信息失败:$code")
                }
            }
        } else {
            initData()
        }
    }

    override fun onDestroy() {
        mHandler.removeCallbacks(outimeTask)
        uploadAvatarFuture?.abort()
        super.onDestroy()
    }


    //====================================== View 层接口 ===========================================

    private fun onLogout() {
        mPresenter.onLogout()
    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun onLogoutSuccess() {
        MainActivity.logout(this@UserSettingActivity, false)
        finish()
    }
}