package com.netease.nim.uikit.business.session.helper

import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout

object ViewAgainMeasureHelper {

    fun againMeasureViewSize(view: View) {
        when (view.layoutParams) {
            is RelativeLayout.LayoutParams -> {

            }
            is LinearLayout.LayoutParams -> {

            }
            is ConstraintLayout.LayoutParams -> {

            }
            is FrameLayout.LayoutParams -> {

            }
            else ->{
                view.layoutParams
            }
        }
    }

}