package com.laka.shoppingchat.mvp.nim.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.*
import com.laka.androidlib.util.image.BitmapUtils
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.dialog.JAlertDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.chat.ChatModuleNavigator
import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.nim.const.NimConstant
import com.laka.shoppingchat.mvp.nim.constract.INimConstract
import com.laka.shoppingchat.mvp.nim.presenter.NimPresenter
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nim.uikit.impl.NimUIKitImpl
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_qr_code.*

class UserQRCodeActivity : BaseMvpActivity<String>(), INimConstract.IBaseNimView {
    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    private lateinit var mNimPresenter: NimPresenter
    override fun createPresenter(): IBasePresenter<*> {
        mNimPresenter = NimPresenter()
        return mNimPresenter
    }

    companion object {
        @JvmStatic
        fun start(context: Context, account: String) {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra(NimConstant.ACCOUNT, account)
            intent.putExtra(NimConstant.IS_FORM_ADD, false)
            context.startActivity(intent)
        }
    }

    private lateinit var mDialog: JAlertDialog
    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(
                this,
                ContextCompat.getColor(this, R.color.color_ededed),
                0
            )
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun setContentView(): Int = R.layout.activity_user_qr_code

    override fun initIntent() {
    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setBackGroundColor(R.color.color_ededed)
            .showDivider(false)
            .setRightIcon(R.drawable.default_btn_top_more_seletor)
            .setOnLeftClickListener {
                finish()
            }
            .setOnRightClickListener {
                showBottom()
            }
    }

    override fun initData() {

    }

    override fun initEvent() {
        iv_qr_code.setOnLongClickListener {
            showBottom()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserInfo()
    }

    private fun updateUserInfo() {
        if (NimUIKit.getUserInfoProvider().getUserInfo(UserUtils.getImAccount()) != null) {
            updateUserInfoView()
            return
        }

        NimUIKit.getUserInfoProvider().getUserInfoAsync(
            UserUtils.getImAccount()
        ) { success, result, code -> updateUserInfoView() }
    }

    private fun updateUserInfoView() {
        val userInfo =
            NimUIKit.getUserInfoProvider().getUserInfo(UserUtils.getImAccount()) as NimUserInfo?
        if (userInfo == null) {
            ToastHelper.showToast(R.string.user_not_exsit)
            finish()
            return
        }
        iv_user_avater.loadAvatar(userInfo.avatar)
        tv_user_name.text = userInfo.name
        iv_user_gender.setImageResource(
            if (userInfo.genderEnum == GenderEnum.MALE) {
                R.mipmap.default_icon_male
            } else {
                R.mipmap.default_icon_female
            }
        )

        val content = UserUtils.getQR()
        Observable.create<Bitmap> {
            try {
                var bitmap = Glide.with(this)
                    .asBitmap()
                    .load(userInfo.avatar)
                    .submit()
                    .get()
                if (bitmap != null && !bitmap.isRecycled) {
                    it.onNext(bitmap)
                }
            } catch (e: Exception) {
                var drawable = resources.getDrawable(R.drawable.default_bg_hp)
                var bitmap = BitmapUtils.drawableToBitmap(drawable, 100, 100)
                if (bitmap != null && !bitmap.isRecycled) {
                    it.onNext(bitmap)
                }
            }
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it != null) {
                    ivQrView.setImageBitmap(it)
                    var bitmap = ImageViewUtils.getCacheBitmapFromView(ivQrView)
                    var qr = QRCodeUtil.createQRCodeBitmap(
                        content,
                        ScreenUtils.dp2px(270f),
                        0,
                        bitmap,
                        0.2F
                    )
                    if (qr != null) {
                        iv_qr_code.setImageBitmap(qr)
                    }
                }
            }
    }

    private fun showBottom() {
        if (!::mDialog.isInitialized) {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_user_qr, null)
            mDialog = JAlertDialog.Builder(this)
                .setFromBottom()
                .setCancelable(true)
                .setAnimation(R.style.bottom_menu_animation)
                .setContentView(view)
                .setOnClick(R.id.tv_save)
                .setOnClick(R.id.tv_scanning)
                .setOnClick(R.id.tv_cancel)
                .setWightPercent(1f)
                .setOnJAlertDialogCLickListener { dialog, _, position ->
                    when (position) {
                        0 -> saveImg() //收到的红包记录
                        1 -> scan() //发出的红包记录
//                        2 -> mDialog.dismiss()
                    }
                    dialog.dismiss()
                }
                .create()
        }
        mDialog.show()
    }

    private fun scan() {

        if (!PermissionUtils.checkPermission(
                Manifest.permission.CAMERA
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            PermissionUtils.requestPermission(
                this, arrayOf(
                    Manifest.permission.CAMERA
                    , Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            return
        }
        LoginModuleNavigator.startScanQRCodeActivityForResult(
            this,
            LoginConstant.REQUEST_SCAN_QR_CODE
        )
    }

    private fun saveImg() {
        var bitmap = ImageViewUtils.getCacheBitmapFromView(iv_qr_code)
        ImageViewUtils.saveBitmapToSdCard(this, bitmap, System.currentTimeMillis().toString(), true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LoginConstant.REQUEST_SCAN_QR_CODE -> { //扫描二维码加好友
                if (resultCode != LoginConstant.RESULT_SCAN_QR_CODE) return
                val result = data?.getStringExtra(LoginConstant.RESULT_SCAN_QR)

                if (StringUtils.isNotEmpty(result)) {
                    if (::mNimPresenter.isInitialized) {
                        mNimPresenter.getGroupIdForQrCode("$result")
                    }
                } else {
                    ToastHelper.showToast("获取数据为空")
                }
            }
        }
    }

    override fun getGroupIdForQrCodeSuccess(resp: QrCodeInfo) {
        val account = decrypt(resp.info)
        if (StringUtils.isNotEmpty(account)) {
            if (NimUIKitImpl.isPersonAccount(resp.is_group)) {
                UserInfoActivity.start(this, account)
            } else {
                ChatModuleNavigator.startAddTeamActivity(this, account)
            }
        }
    }

    private fun decrypt(origin: String): String {
        val inPrivate = resources.assets.open("rsa_public_key.pem")
        val publicKey = RsaUtils.loadPublicKey(inPrivate)
        // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
        val decryptByte = RsaUtils.decryptData(Base64.decode(origin, Base64.DEFAULT), publicKey)
        return String(decryptByte)
    }
}