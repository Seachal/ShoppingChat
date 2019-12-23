package com.netease.nim.uikit.business.session.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.laka.androidlib.util.screen.ScreenUtils
import com.netease.nim.uikit.R

/**
 * @Author:summer
 * @Date:2019/10/22
 * @Description:通用分割线
 */
class CommonDivider : View {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var mPaint: Paint = Paint()

    init {
        mPaint.color = ContextCompat.getColor(context, R.color.color_gray_d8d8d8)
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = ScreenUtils.dp2px(1f).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var resultWidth = 0
        when (widthMode) {
            MeasureSpec.AT_MOST,
            MeasureSpec.EXACTLY,
            MeasureSpec.UNSPECIFIED -> {
                resultWidth = measureWidth
            }
        }

        var resultHeight = 0
        when (heightMode) {
            MeasureSpec.UNSPECIFIED,
            MeasureSpec.AT_MOST -> {
                resultHeight = ScreenUtils.dp2px(10f)
            }
            MeasureSpec.EXACTLY -> {
                resultHeight = measureHeight
            }
        }
        //setMeasuredDimension(resultWidth, ScreenUtils.dp2px(10f))
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        mPaint.color = ContextCompat.getColor(context, R.color.color_gray_d8d8d8)
        canvas?.drawLine(0.0f, 0.0f, width.toFloat(), 0.0f, mPaint)
        canvas?.drawLine(0.0f, height.toFloat(), width.toFloat(), height.toFloat(), mPaint)
        mPaint.color = ContextCompat.getColor(context, R.color.color_ededed)
        canvas?.drawRect(0.0f, 1.0f, width.toFloat(), height.toFloat() - 1, mPaint)
    }


}