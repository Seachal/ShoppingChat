package com.netease.nim.uikit.business.session.listener

import android.view.MotionEvent
import android.view.View
import com.laka.androidlib.util.LogUtils

class DoubleClickListener : View.OnTouchListener {

    private var count: Int = 0
    private var firstClick: Long = 0
    private var secondClick: Long = 0
    private var interval: Long = 200
    private lateinit var mCallback: ((View) -> Unit)

    constructor(mCallback: (View) -> Unit) {
        this.mCallback = mCallback
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if (MotionEvent.ACTION_DOWN == event?.action) {
            count++
            if (1 == count) {
                firstClick = System.currentTimeMillis()
            } else if (2 == count) {
                secondClick = System.currentTimeMillis()
                if (secondClick - firstClick < interval) {
                    if (::mCallback.isInitialized) {
                        mCallback.invoke(view!!)
                    } else {
                        LogUtils.info("mCallback is null!")
                    }
                    count = 0
                    firstClick = 0
                } else {
                    firstClick = secondClick
                    count = 1
                }
                secondClick = 0
                return true
            }
        }
        return false
    }

    fun setCallBack(callback: ((View) -> Unit)) {
        this.mCallback = callback
    }

}