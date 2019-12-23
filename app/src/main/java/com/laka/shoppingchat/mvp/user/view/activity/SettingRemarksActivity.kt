package com.laka.shoppingchat.mvp.user.view.activity

import android.os.Build
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.util.KeyboardHelper
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.filter.SketchLengthFilter
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum
import kotlinx.android.synthetic.main.activity_setting_remarks.*

class SettingRemarksActivity : BaseActivity(), View.OnClickListener {

    private var account: String? = ""
    private var marks: String? = ""

    override fun setContentView(): Int {
        return R.layout.activity_setting_remarks
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_ededed), 0)
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun initIntent() {
        account = intent.getStringExtra(UserConstant.KEY_IM_ACCOUNT)
        marks = intent.getStringExtra(UserConstant.KEY_IM_MARKS)
    }

    override fun initViews() {
        title_bar.setTitle("设置备注")
            .setLeftText("取消")
            .setRightText("完成")
            .showDivider(false)
            .setTitleTextColor(R.color.black)
            .setLeftTextColor(R.color.black)
            .setRightTextColor(R.color.black)
            .setBackGroundColor(R.color.color_ededed)
            .setOnRightClickListener {
                onSave()
            }
        et_input.setText("$marks")
        et_input.setSelection(marks?.length ?: 0)
        et_input.filters = arrayOf(SketchLengthFilter(16))
        Handler().postDelayed({
            KeyboardHelper.openKeyBoard(this, et_input)
        }, 200)
    }

    private fun onSave() {
        var marksName = et_input.text.toString().trim()
        if (StringUtil.isEmpty(marksName)) {
            marksName = ""
        }
        val map = mutableMapOf<FriendFieldEnum, Any>()
        map[FriendFieldEnum.ALIAS] = marksName
        NIMClient.getService(FriendService::class.java).updateFriendFields(account, map)
            .setCallback(object : RequestCallback<Void> {
                override fun onSuccess(param: Void?) {
                    EventBusManager.postEvent(UserConstant.EVENT_MODIFY_ALIAS)
                    ToastHelper.showToast("保存成功")
                    finish()
                }

                override fun onFailed(code: Int) {
                    ToastHelper.showToast("保存失败")
                }

                override fun onException(exception: Throwable?) {
                    ToastHelper.showToast("保存失败")
                }
            })
    }

    override fun initData() {

    }

    override fun initEvent() {
        iv_delete.setOnClickListener(this)
        et_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (StringUtil.isEmpty(et_input.text.toString())) {
                    iv_delete.visibility = View.GONE
                } else {
                    iv_delete.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        et_input.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                iv_delete.visibility = View.VISIBLE
            } else {
                iv_delete.visibility = View.GONE
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_delete -> {
                et_input.setText("")
            }
            else -> {

            }
        }
    }


}