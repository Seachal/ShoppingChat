package com.laka.shoppingchat.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.laka.shoppingchat.R
import com.netease.nim.uikit.common.util.string.StringUtil

/**
 * @Author:summer
 * @Date:2019/9/9
 * @Description:用户中心功能条目
 */
class PersonalNavigatiorBar : RelativeLayout {

    private lateinit var mRootView: ViewGroup
    private lateinit var mIvIcon: ImageView
    private lateinit var mTvText: TextView
    private lateinit var mIvMore: ImageView
    private lateinit var mTvMarks: TextView
    private var mLeftIconRes: Int = R.drawable.ic_search
    private var mRightIconRes: Int = R.drawable.ic_more
    private var isShowRightArrow: Boolean = false
    private var mContent: String? = ""
    private var mMarks: String? = ""

    private var mOnItemCLickListener: ((id: Int) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, -1)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initView(context, attributeSet)
        initEvent()
    }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        mRootView = LayoutInflater.from(context).inflate(
            R.layout.item_personal_horizontal,
            this
        ) as ViewGroup
        mIvIcon = findViewById(R.id.iv_icon)
        mTvText = findViewById(R.id.tv_des)
        mIvMore = findViewById(R.id.iv_more)
        mTvMarks = findViewById(R.id.tv_marks)

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PersonalNavigatiorBar)
        typedArray?.let {
            mLeftIconRes = it.getResourceId(R.styleable.PersonalNavigatiorBar_left_icon, -1)
            mRightIconRes = it.getResourceId(R.styleable.PersonalNavigatiorBar_right_icon, -1)
            isShowRightArrow = it.getBoolean(R.styleable.PersonalNavigatiorBar_is_show_right_arrow, false)
            mContent = it.getString(R.styleable.PersonalNavigatiorBar_text_content)
            mMarks = it.getString(R.styleable.PersonalNavigatiorBar_text_marks)
        }
        if (mLeftIconRes != -1) {
            mIvIcon.setImageResource(mLeftIconRes)
        } else {
            mIvIcon.visibility = View.GONE
        }
        if (mRightIconRes != -1) {
            mIvMore.setImageResource(mRightIconRes)
        } else {
            if (isShowRightArrow) {
                mIvMore.visibility = View.VISIBLE
            }else{
                mIvMore.visibility = View.GONE
            }
        }
        if (!StringUtil.isEmpty(mMarks)) {
            mTvMarks.visibility = View.VISIBLE
            mTvMarks.text = mMarks
        } else {
            mTvMarks.visibility = View.GONE
        }
        mTvText.text = mContent
    }

    private fun initEvent() {

    }

    fun setRightText(rightTxt: String) {
        mTvMarks?.text = rightTxt
        mTvMarks?.visibility = View.VISIBLE
    }

    fun setOnItemClickListener(listener: ((id: Int) -> Unit)) {
        this.mOnItemCLickListener = listener
    }


}