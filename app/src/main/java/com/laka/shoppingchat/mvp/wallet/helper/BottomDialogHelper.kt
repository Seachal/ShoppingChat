package com.laka.shoppingchat.mvp.wallet.helper

import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.laka.shoppingchat.R
import cn.jzvd.JZUtils.getWindow


class BottomDialogHelper {
    lateinit var popupWindow: PopupWindow
    fun init(context: Context, view: View) {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.bottom_pay, null)
        //给popwindow加上动画效果
        val layoutPop = view.findViewById<LinearLayout>(R.id.pop_root)
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_fade_in))
        layoutPop.startAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_in))
        //设置popwindow的宽高，这里我直接获取了手机屏幕的宽，高设置了600DP
        val dm = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
        popupWindow = PopupWindow(view, dm.widthPixels, 600)

// 使其聚集
        popupWindow.isFocusable = true
        // 设置允许在外点击消失
        popupWindow.isOutsideTouchable = true

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        backgroundAlpha(context, 120f)  //透明度
        popupWindow.setOnDismissListener { backgroundAlpha(context, 1f) };
        //弹出的位置
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        popupWindow.showAtLocation(view, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    fun backgroundAlpha(context: Context, bgAlpha: Float) {
        val lp = (context as Activity).window.attributes
        // 0.0-1.0
        lp.alpha = bgAlpha
        context.window.attributes = lp
        // everything behind this window will be dimmed.
        // 此方法用来设置浮动层，防止部分手机变暗无效
        context.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
}