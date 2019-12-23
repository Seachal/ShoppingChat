package com.laka.shoppingchat.mvp.chat.view.activity

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.*
import com.laka.androidlib.util.image.BitmapUtils
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.widget.dialog.JAlertDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.chat.ChatModuleNavigator
import com.laka.shoppingchat.mvp.chat.constact.ChatConstact
import com.laka.shoppingchat.mvp.chat.dialog.SaveTeamQrCodeDialog
import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo
import com.laka.shoppingchat.mvp.chat.presenter.ChatPresenter
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.nim.activity.UserInfoActivity
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nim.uikit.common.ToastHelper
import com.netease.nim.uikit.impl.NimUIKitImpl
import com.netease.nimlib.sdk.team.model.Team
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat_team_qr_code.*
import kotlinx.android.synthetic.main.activity_chat_team_qr_code.ivQrView
import kotlinx.android.synthetic.main.activity_chat_team_qr_code.iv_qr_code
import kotlinx.android.synthetic.main.activity_chat_team_qr_code.iv_user_avater
import kotlinx.android.synthetic.main.activity_chat_team_qr_code.title_bar
import kotlinx.android.synthetic.main.activity_chat_team_qr_code.tv_user_name
import kotlinx.android.synthetic.main.activity_user_qr_code.*
import org.jetbrains.anko.toast
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author:summer
 * @Date:2019/9/29
 * @Description:
 */
class ChatTeamQrCodeActivity : BaseMvpActivity<String>(), ChatConstact.IChatView {

    private var mAccountId: String = ""
    private lateinit var mTeam: Team
    private lateinit var mSaveQrCodeDialog: SaveTeamQrCodeDialog
    private lateinit var mPresenter: ChatConstact.IChatPresenter
    private lateinit var mDialog: JAlertDialog
    override fun createPresenter(): IBasePresenter<*> {
        mPresenter = ChatPresenter()
        return mPresenter
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_ededed), 0)
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            super.setStatusBarColor(color)
        }
    }

    override fun setContentView(): Int {
        return R.layout.activity_chat_team_qr_code
    }

    override fun initIntent() {
        intent?.let {
            mAccountId = intent.getStringExtra("account")
        }
    }

    override fun initViews() {
        title_bar.showDivider(false)
            .setBackGroundColor(R.color.color_ededed)
            .setRightIcon(R.drawable.default_btn_top_more_seletor)
            .setLeftIcon(R.drawable.selector_nav_btn_back)
            .setTitleTextColor(R.color.black)
            .setTitleTextSize(16)
            .setOnRightClickListener {
               showBottom()
            }
        mSaveQrCodeDialog = SaveTeamQrCodeDialog(this)
    }

    private fun showSaveImageDialog() {
        if (::mSaveQrCodeDialog.isInitialized) {
            mSaveQrCodeDialog.show()
        }
    }

    override fun initData() {
        loadTeamInfo()
        if (::mTeam.isInitialized) {
            mPresenter.getEncryQrCodeInfo(mAccountId) //获取群加密信息
            //mPresenter.getDecryptQrCodeInfo("ce1a535a325dfd51f4527c6c637d6cb3")  //获取群解密信息
        }
    }

    private fun loadTeamInfo() {
        val team = NimUIKit.getTeamProvider().getTeamById(mAccountId)
        if (team != null) {
            updateTeamInfo(team)
        } else {
            NimUIKit.getTeamProvider().fetchTeamById(
                mAccountId
            ) { success, result, _ ->
                if (success && result != null) {
                    updateTeamInfo(result)
                } else {
                    onGetTeamInfoFailed()
                }
            }
        }
    }

    private fun updateTeamInfo(team: Team) {
        this.mTeam = team
        if (team == null) {
            ToastHelper.showToast(this, getString(R.string.team_not_exist))
            finish()
            return
        }
        tv_user_name.text = mTeam.name
        iv_user_avater.loadAvatar(mTeam.icon)
    }

    private fun onGetTeamInfoFailed() {
        ToastHelper.showToast(this, getString(R.string.team_not_exist))
        finish()
    }

    override fun initEvent() {

    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun getEncryQrCodeInfoSuccess(info: QrCodeInfo) {
        //生成二维码
        Observable.create<Bitmap> {
            try {
                var bitmap = Glide.with(this)
                    .asBitmap()
                    .load(mTeam.icon)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(ScreenUtils.dp2px(10f))))//圆角半径
                    .submit()
                    .get()
                it.onNext(bitmap)
            } catch (e: Exception) {
                var drawable = resources.getDrawable(R.drawable.default_bg_hp)
                var bitmap = BitmapUtils.drawableToBitmap(drawable, 100, 100);
                it.onNext(bitmap)
            }
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                it?.let {
                    ivQrView.setImageBitmap(it)
                    var bitmap = ImageViewUtils.getCacheBitmapFromView(ivQrView)
                    var qr = QRCodeUtil.createQRCodeBitmap(
                        info.qr,
                        ScreenUtils.dp2px(270f),
                        0,
                        bitmap,
                        0.2F
                    )
                    if (qr != null) {
                        iv_qr_code.setImageBitmap(qr)
                    }
                    handleExpireTime()
                }
            }
    }

    private fun handleExpireTime() {
        var resultContent: String
        val nowCalendar = Calendar.getInstance()
        val expireCalendar = Calendar.getInstance()
        expireCalendar.timeInMillis = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7
        val nowYear = nowCalendar.get(Calendar.YEAR)
        val expireYear = expireCalendar.get(Calendar.YEAR)
        val expireMonth = expireCalendar.get(Calendar.MONTH) + 1
        val expireDay = expireCalendar.get(Calendar.DAY_OF_MONTH)
        resultContent = if (nowYear != expireYear) {
            "该二维码7天内(${expireYear}年${expireMonth}月${expireDay}日前)有效，重新进入将更新"
        } else {
            "该二维码7天内(${expireMonth}月${expireDay}日前)有效，重新进入将更新"
        }
        tv_expire_time.text = resultContent
    }

    override fun getDecryptQrCodeInfoSuccess(info: QrCodeInfo) {
        val account = decrypt(info.info)
        if (StringUtils.isNotEmpty(account)) {
            if (NimUIKitImpl.isPersonAccount(info.is_group)) {
                UserInfoActivity.start(this, account)
            } else {
                ChatModuleNavigator.startAddTeamActivity(this, account)
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
        ImageViewUtils.saveBitmapToSdCard(this, bitmap, System.currentTimeMillis().toString(),true)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LoginConstant.REQUEST_SCAN_QR_CODE -> { //扫描二维码加好友
                if (resultCode != LoginConstant.RESULT_SCAN_QR_CODE) return
                val result = data?.getStringExtra(LoginConstant.RESULT_SCAN_QR)

                if (StringUtils.isNotEmpty(result)) {
                    if (::mPresenter.isInitialized) {
                        mPresenter.getDecryptQrCodeInfo("$result")
                    }
                } else {
                    com.laka.androidlib.util.toast.ToastHelper.showToast("获取数据为空")
                }
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