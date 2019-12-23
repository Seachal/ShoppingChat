package com.laka.shoppingchat.mvp.main.helper

import android.content.Context
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.laka.androidlib.util.ResourceUtils
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.netease.nim.uikit.common.ui.drop.DropFake
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView

class MagicTabHelper {


    private var isInit = false

    var listener: onSelectIndexListener? = null
    private lateinit var mMagicIndicator: MagicIndicator
    private lateinit var mContext: Context
    var imgSelect = intArrayOf(
        R.mipmap.index_bar_home_h,
        R.mipmap.index_bar_chat_h,
        R.mipmap.index_bar_friend_h,
        R.mipmap.index_bar_mine_h
    )
    var imgDefault = intArrayOf(
        R.mipmap.index_bar_home_n,
        R.mipmap.index_bar_chat_n,
        R.mipmap.index_bar_friend_n,
        R.mipmap.index_bar_mine_n
    )
    private var mTitle = arrayOf(
        ResourceUtils.getString(R.string.module_home),
        ResourceUtils.getString(R.string.module_chat),
        ResourceUtils.getString(R.string.module_address_book),
        ResourceUtils.getString(R.string.module_user)
    )
    val DURATION = 30
    var mContactTip: DropFake? = null
    //    var mDropFake: DropFake? = null
    var mTvTip: TextView? = null

    fun initMagicIndicator(
        context: Context,
        magicIndicator: MagicIndicator,
        mViewPager: ViewPager
    ) {
        this.mMagicIndicator = magicIndicator
        mContext = context
        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mTitle.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val commonPagerTitleView = CommonPagerTitleView(context)

                // load custom layout
                val customLayout =
                    LayoutInflater.from(context).inflate(R.layout.item_home_tab, null)
                val titleImg = customLayout.findViewById(R.id.title_img) as ImageView
                val titleText = customLayout.findViewById(R.id.title_text) as TextView
                val contactTip = customLayout.findViewById(R.id.contract_tip) as DropFake
                val unreadNumberTip = customLayout.findViewById<TextView>(R.id.tv_msg_count)
                if (index == 1) {
                    mTvTip = unreadNumberTip
                }
                if (index == 2) {
                    mContactTip = contactTip
                }
                titleText.text = mTitle[index]
                commonPagerTitleView.setContentView(customLayout)
                commonPagerTitleView.onPagerTitleChangeListener =
                    object : CommonPagerTitleView.OnPagerTitleChangeListener {

                        override fun onSelected(index: Int, totalCount: Int) {
                            titleText.setTextColor(context.resources.getColor(R.color.color_07C160))
                        }

                        override fun onDeselected(index: Int, totalCount: Int) {
                            titleText.setTextColor(context.resources.getColor(R.color.color_2d2d2d))
                        }

                        override fun onLeave(
                            index: Int,
                            totalCount: Int,
                            leavePercent: Float,
                            leftToRight: Boolean
                        ) {
                            titleImg.setImageResource(imgDefault[index])
                        }

                        override fun onEnter(
                            index: Int,
                            totalCount: Int,
                            enterPercent: Float,
                            leftToRight: Boolean
                        ) {
                            titleImg.setImageResource(imgSelect[index])
                        }
                    }

                commonPagerTitleView.setOnClickListener {
                    if (index != 0 && !UserUtils.isLogin()) {
                        LoginModuleNavigator.startLoginActivity(context)
                    } else {
                        listener?.let {
                            it.onSelectIndex(index)
                        }
                    }
                }

                return commonPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator? {
                return null
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, mViewPager)
    }

    fun setMsgNums(unReadCount: Int) {
//        mDropFake?.text = "$unReadCount"
        if (unReadCount > 0) {
            mTvTip?.visibility = View.VISIBLE
            if (unReadCount >= 100) {
                mTvTip?.text = "99+"
            } else {
                mTvTip?.text = unReadCount.toString()
            }
        } else {
            mTvTip?.visibility = View.GONE
        }
    }

    fun showTip(show: Boolean) {
        if (show) {
            mContactTip?.visibility = View.VISIBLE
        } else {
            mContactTip?.visibility = View.GONE
        }
    }

    fun setOnSelectIndexListener(listener: onSelectIndexListener) {
        this.listener = listener
    }

    interface onSelectIndexListener {
        fun onSelectIndex(index: Int)
    }
}