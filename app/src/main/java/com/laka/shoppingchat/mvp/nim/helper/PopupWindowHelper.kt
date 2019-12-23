package com.laka.shoppingchat.mvp.nim.helper

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import com.laka.androidlib.util.PermissionUtils
import com.laka.androidlib.util.screen.ScreenUtils

import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.util.AnimUtil
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.nim.activity.AddFriendsActivity
import com.laka.shoppingchat.mvp.nim.fragment.SessionListFragment
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nim.uikit.business.team.helper.TeamHelper
import com.netease.nim.uikit.common.ToastHelper
import org.w3c.dom.Text
import kotlin.math.acos

class PopupWindowHelper(private val context: Context, private val iv_add: View) {

    private lateinit var mPopupWindow: PopupWindow

    private var animUtil: AnimUtil? = null
    private var bgAlpha = 1f
    private var bright = false
    private var addFriends: (() -> Unit)? = null
    private var chat: (() -> Unit)? = null

    init {
        init(context)
    }

    private fun init(context: Context) {
        mPopupWindow = PopupWindow(context)
        animUtil = AnimUtil()


    }

    fun showPop() {
        // 设置布局文件
        mPopupWindow.contentView = LayoutInflater.from(context).inflate(R.layout.pop_add, null)
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        mPopupWindow.width = ViewGroup.LayoutParams.WRAP_CONTENT
        mPopupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // 设置pop透明效果
        mPopupWindow.setBackgroundDrawable(ColorDrawable(0x0000))
        // 设置pop出入动画
        mPopupWindow.animationStyle = R.style.pop_add
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.isFocusable = true
        // 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.isTouchable = true
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPopupWindow.isOutsideTouchable = true


        mPopupWindow.contentView.findViewById<TextView>(R.id.tv_pop_chat).setOnClickListener {
            //            val option = TeamHelper.getCreateContactSelectOption(null, 50)
//            NimUIKit.startContactSelector(context, option, SessionListFragment.REQUEST_CODE_NORMAL)
            val advancedOption = TeamHelper.getCreateContactSelectOption(null, 50)
            NimUIKit.startContactSelector(
                context,
                advancedOption,
                SessionListFragment.REQUEST_CODE_ADVANCED
            )
            mPopupWindow.dismiss()
        }
        mPopupWindow.contentView.findViewById<TextView>(R.id.tv_pop_friends).setOnClickListener {
            AddFriendsActivity.start(context)
            mPopupWindow.dismiss()
        }
        mPopupWindow.contentView.findViewById<TextView>(R.id.tv_scanning).setOnClickListener {
            mPopupWindow.dismiss()
            if (!PermissionUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                PermissionUtils.requestPermission(
                    context as Activity, arrayOf(
                        Manifest.permission.CAMERA
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                return@setOnClickListener
            }
            LoginModuleNavigator.startScanQRCodeActivityForResult(
                context as Activity,
                LoginConstant.REQUEST_SCAN_QR_CODE
            )
            mPopupWindow.dismiss()
        }
        // 相对于 + 号正下面，同时可以设置偏移量
        mPopupWindow.showAsDropDown(iv_add, -100, 0, Gravity.RIGHT)
        // 设置pop关闭监听，用于改变背景透明度
        mPopupWindow.setOnDismissListener {
            //                toggleBright();
        }


    }

    private fun toggleBright() {
        // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
        animUtil!!.setValueAnimator(START_ALPHA, END_ALPHA, DURATION)
        animUtil!!.addUpdateListener { progress ->
            // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
            bgAlpha = if (bright) progress else START_ALPHA + END_ALPHA - progress
            backgroundAlpha(bgAlpha)
        }
        animUtil!!.addEndListner {
            // 在一次动画结束的时候，翻转状态
            bright = !bright
        }
        animUtil!!.startAnimator()
    }

    /**
     * 此方法用于改变背景的透明度，从而达到“变暗”的效果
     */
    private fun backgroundAlpha(bgAlpha: Float) {

        val lp = (context as Activity).window.attributes
        // 0.0-1.0
        lp.alpha = bgAlpha
        context.window.attributes = lp
        // everything behind this window will be dimmed.
        // 此方法用来设置浮动层，防止部分手机变暗无效
        context.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    companion object {

        private val DURATION: Long = 500
        private val START_ALPHA = 0.7f
        private val END_ALPHA = 1f
    }

    fun setAddFriendMethod(add: () -> Unit) {
        this.addFriends = add
    }

    fun setChatMethod(chat: () -> Unit) {
        this.chat = chat
    }
}
