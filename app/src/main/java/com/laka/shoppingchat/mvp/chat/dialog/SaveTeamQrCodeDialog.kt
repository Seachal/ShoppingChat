package com.laka.shoppingchat.mvp.chat.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.laka.androidlib.widget.dialog.BaseDialog
import com.laka.shoppingchat.R


/**
 * @Author:summer
 * @Date:2019/9/29
 * @Description:
 */
class SaveTeamQrCodeDialog(context: Context) : BaseDialog(context) {

    private lateinit var mTvSave: TextView
    private lateinit var mTvCancel: TextView
    private lateinit var mSaveImageClickListener: ((view: View) -> Unit)

    override fun getLayoutId(): Int {
        return R.layout.dialog_save_team_qr_code
    }

    override fun initView() {
        mTvSave = findViewById(R.id.tv_save_image)
        mTvCancel = findViewById(R.id.tv_cancel)
        gravityType = Gravity.BOTTOM
    }

    override fun initAnima() {
        window!!.setWindowAnimations(R.style.bottom_menu_animation)
    }

    override fun initData() {
        mTvSave.setOnClickListener {
            dismiss()
            if (::mSaveImageClickListener.isInitialized) {
                mSaveImageClickListener.invoke(it)
            }
        }
        mTvCancel.setOnClickListener { dismiss() }
    }

    override fun initEvent() {

    }

    fun setSaveImageClickListener(listener: ((view: View) -> Unit)) {
        this.mSaveImageClickListener = listener
    }

}