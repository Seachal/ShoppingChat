package com.laka.shoppingchat.mvp.user.view.activity

import android.content.Intent
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.util.filter.SketchLengthFilter
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.login.model.event.UserEvent
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.laka.shoppingchat.mvp.user.helper.UserUpdateHelper
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum
import kotlinx.android.synthetic.main.activity_modify_name.*

class ModifyNameActivity : BaseMvpActivity<String>() {

    override fun setContentView(): Int = R.layout.activity_modify_name
    private lateinit var mNickname: String

    override fun initIntent() {
        mNickname = intent.getStringExtra("nickname")
    }

    override fun initViews() {
        title_bar
            .setLeftText("取消")
            .setTitle("设置昵称")
            .setRightTextBg(R.drawable.bg_send_friend_bg)
            .setRightText("保存")
            .setRightTextColor(R.color.white)
            .setTitleTextColor(R.color.color_2d2d2d)
            .setOnRightClickListener {
                onSave()
            }
        et_nickname.setText(mNickname)
        et_nickname.setSelection(et_nickname.text.toString().trim().length)
        et_nickname.filters = arrayOf(SketchLengthFilter(16))
    }

    private fun onSave() {
        mNickname = et_nickname.text.toString().trim()
        if (StringUtils.isEmpty(mNickname)) {
            ToastHelper.showCenterToast("请输入昵称")
            return
        }
        if (mNickname.length > 12) {
            ToastHelper.showCenterToast("昵称只能设置1-16个字符内")
            return
        }
        UserUpdateHelper.update(
            UserInfoFieldEnum.Name,
            mNickname,
            object : RequestCallbackWrapper<Void>() {
                override fun onResult(code: Int, p1: Void?, p2: Throwable?) {
                    if (code == ResponseCode.RES_SUCCESS.toInt()) {
                        EventBusManager.postEvent(UserEvent(UserConstant.EDIT_USER_INFO))
                        ToastHelper.showCenterToast("修改成功")
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        ToastHelper.showCenterToast("修改昵称失败")
                    }
                }
            })
    }

    override fun initData() {
    }

    override fun initEvent() {
    }

    override fun showData(data: String) {
    }

    override fun showErrorMsg(msg: String?) {
    }

    override fun createPresenter(): IBasePresenter<*>? {
        return null
    }


}