package com.netease.nim.uikit.business.session.weight

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.laka.androidlib.util.LogUtils
import com.netease.nim.uikit.R

/**
 * @Author:summer
 * @Date:2019/9/21
 * @Description:
 */
class AudioRecordWaveView : LinearLayout {

    private lateinit var mRootView: View
    private lateinit var mView1: View
    private lateinit var mView2: View
    private lateinit var mView3: View
    private lateinit var mView4: View
    private lateinit var mView5: View
    private lateinit var mView6: View
    private lateinit var mView7: View
    private lateinit var mView8: View

    private var mMaxAmplitude = 12800 //处理的最大振幅
    private var mAmplitudePart = 1600 //每格两千振幅

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        mRootView = LayoutInflater.from(context).inflate(R.layout.layout_audio_record, this)
        mView1 = findViewById(R.id.view1)
        mView2 = findViewById(R.id.view2)
        mView3 = findViewById(R.id.view3)
        mView4 = findViewById(R.id.view4)
        mView5 = findViewById(R.id.view5)
        mView6 = findViewById(R.id.view6)
        mView7 = findViewById(R.id.view7)
        mView8 = findViewById(R.id.view8)
    }


    fun setAudioAmplitude(amplitude: Int) {
        LogUtils.info("audio-------------amplitude-$amplitude")
        if (amplitude > mMaxAmplitude) {
            handlePartView(8)
        } else if (amplitude <= mAmplitudePart) {
            handlePartView(1)
        } else {
            if (amplitude % mAmplitudePart == 0) {
                handlePartView(amplitude / mAmplitudePart)
            } else {
                handlePartView(amplitude / mAmplitudePart + 1)
            }
        }
    }

    private fun handlePartView(number: Int) {
        LogUtils.info("audio-------------number=$number")
        if (number >= 8) {
            mView1.isSelected = true
            mView2.isSelected = true
            mView3.isSelected = true
            mView4.isSelected = true
            mView5.isSelected = true
            mView6.isSelected = true
            mView7.isSelected = true
            mView8.isSelected = true
            return
        }

        if (number <= 1) {
            mView1.isSelected = true
            mView2.isSelected = false
            mView3.isSelected = false
            mView4.isSelected = false
            mView5.isSelected = false
            mView6.isSelected = false
            mView7.isSelected = false
            mView8.isSelected = false
            return
        }

        when (number) {
            7 -> {
                mView2.isSelected = true
                mView3.isSelected = true
                mView4.isSelected = true
                mView5.isSelected = true
                mView6.isSelected = true
                mView7.isSelected = true
                mView8.isSelected = false
            }
            6 -> {
                mView2.isSelected = true
                mView3.isSelected = true
                mView4.isSelected = true
                mView5.isSelected = true
                mView6.isSelected = true
                mView7.isSelected = false
                mView8.isSelected = false
            }
            5 -> {
                mView2.isSelected = true
                mView3.isSelected = true
                mView4.isSelected = true
                mView5.isSelected = true
                mView6.isSelected = false
                mView7.isSelected = false
                mView8.isSelected = false
            }
            4 -> {
                mView2.isSelected = true
                mView3.isSelected = true
                mView4.isSelected = true
                mView5.isSelected = false
                mView6.isSelected = false
                mView7.isSelected = false
                mView8.isSelected = false
            }
            3 -> {
                mView2.isSelected = true
                mView3.isSelected = true
                mView4.isSelected = false
                mView5.isSelected = false
                mView6.isSelected = false
                mView7.isSelected = false
                mView8.isSelected = false
            }
            2 -> {
                mView2.isSelected = true
                mView3.isSelected = false
                mView4.isSelected = false
                mView5.isSelected = false
                mView6.isSelected = false
                mView7.isSelected = false
                mView8.isSelected = false
            }
        }
    }

}