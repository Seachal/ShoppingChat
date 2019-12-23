package com.laka.shoppingchat.mvp.shopping.center.helper

import android.content.Context
import android.graphics.Typeface
import android.support.v4.view.ViewPager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.laka.shoppingchat.mvp.shopping.center.weight.ScaleTransitionPagerTitleView
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class HomeTabLayoutHelper {

    fun bindCommonNavigator(context: Context, titleList: MutableList<String>, magicIndicator: MagicIndicator, mVpContainer: ViewPager, normalColor: Int, selectedColor: Int, indicatorColor: Int): HomeTabLayoutHelper {
        var commonNavigator = CommonNavigator(context)
        commonNavigator?.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return if (titleList == null) 0 else titleList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.text = titleList[index]
                simplePagerTitleView.typeface = Typeface.defaultFromStyle(Typeface.BOLD);
                simplePagerTitleView.normalColor = context.resources.getColor(normalColor)
                simplePagerTitleView.selectedColor = context.resources.getColor(selectedColor)
                simplePagerTitleView.setOnClickListener {
                    mVpContainer.currentItem = index
                }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                var indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
                indicator.lineWidth = UIUtil.dip2px(context, 20.0).toFloat()
                indicator.roundRadius = UIUtil.dip2px(context, 3.0).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(context.resources.getColor(indicatorColor))
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, mVpContainer)
        return this
    }
}