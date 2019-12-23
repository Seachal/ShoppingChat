package com.laka.shoppingchat.mvp.user.view.activity

import android.content.Intent
import android.view.View
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.login.model.event.UserEvent
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.laka.shoppingchat.mvp.user.helper.ImUserInfoHelper
import com.laka.shoppingchat.mvp.user.helper.UserUpdateHelper
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum
import kotlinx.android.synthetic.main.activity_modify_sex.*

class ModifySexActivity : BaseMvpActivity<String>(), View.OnClickListener {

    private var mGender: GenderEnum = GenderEnum.MALE

    override fun setContentView(): Int = R.layout.activity_modify_sex

    override fun initIntent() {
        mGender = ImUserInfoHelper.getCurrentUserInfo()?.genderEnum ?: GenderEnum.MALE
    }

    override fun initViews() {
        title_bar
            .setLeftText("取消")
            .setTitle("设置性别")
            .setRightTextBg(R.drawable.bg_send_friend_bg)
            .setRightText("保存")
            .setRightTextColor(R.color.white)
            .setTitleTextColor(R.color.color_2d2d2d)
            .setOnRightClickListener {
                onSave()
            }
        //回显当前性别
        when (mGender.value) {
            1 -> { //男
                setMaleDrawableRight()
            }
            2 -> { //女
                setWomanDrawableRight()
            }
        }
    }

    private fun onSave() {
        UserUpdateHelper.update(
            UserInfoFieldEnum.GENDER,
            mGender.value,
            object : RequestCallbackWrapper<Void>() {
                override fun onResult(code: Int, p1: Void?, p2: Throwable?) {
                    if (code == ResponseCode.RES_SUCCESS.toInt()) {
                        EventBusManager.postEvent(UserEvent(UserConstant.EDIT_USER_INFO))
                        ToastHelper.showCenterToast("修改成功")
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        ToastHelper.showCenterToast("修改失败，请稍后重试")
                    }
                }
            })
    }

    override fun initData() {
    }

    override fun initEvent() {
        tv_male.setOnClickListener(this)
        tv_woman.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_male -> {
                mGender = GenderEnum.MALE
                setMaleDrawableRight()
            }
            R.id.tv_woman -> {
                mGender = GenderEnum.FEMALE
                setWomanDrawableRight()
            }
        }
    }

    private fun setWomanDrawableRight() {
        val rightDrawable = resources.getDrawable(R.mipmap.icon_sexual_select)
        tv_male.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        tv_woman.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            rightDrawable,
            null
        )
    }

    private fun setMaleDrawableRight() {
        val rightDrawable = resources.getDrawable(R.mipmap.icon_sexual_select)
        tv_male.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            rightDrawable,
            null
        )
        tv_woman.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
    }

    override fun showData(data: String) {
    }

    override fun showErrorMsg(msg: String?) {
    }

    override fun createPresenter(): IBasePresenter<*>? {
        return null
    }

}