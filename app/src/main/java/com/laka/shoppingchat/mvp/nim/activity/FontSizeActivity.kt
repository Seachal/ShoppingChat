package com.laka.shoppingchat.mvp.nim.activity

import android.content.res.Resources
import android.os.Build
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.constant.AppConstant
import com.laka.androidlib.util.ActivityManager
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.SPHelper
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.widget.dialog.CommonConfirmDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.util.IntentUtils
import com.laka.shoppingchat.mvp.launch.view.activity.WelcomeActivity
import kotlinx.android.synthetic.main.activity_font_size.*

class FontSizeActivity : BaseActivity() {
    private var fontSizeScale = 0f
    private var isChange = false//用于监听字体大小是否有改动
    private var defaultPos = 0
    override fun setContentView(): Int = R.layout.activity_font_size

    override fun initIntent() {
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, resources.getColor(R.color.color_ededed), 0)
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            super.setStatusBarColor(color)
        }
    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setBackGroundColor(R.color.color_ededed)
            .showDivider(false)
            .setRightTextColor(R.color.white)
            .setRightTextBg(R.drawable.bg_send_friend_bg)
            .setRightText("完成")
            .setOnLeftClickListener {
                finish()
            }
            .setOnRightClickListener {
                if (isChange) run {
                    val commonConfirmDialog = CommonConfirmDialog(this)
                    commonConfirmDialog?.show()
                    commonConfirmDialog?.setDefaultTitleTxt("新的字体大小需要重启购聊才能生效")
                    commonConfirmDialog?.setOnClickSureListener {
                        ScreenUtils.setFontSizeScale(fontSizeScale)
                        SPHelper.putFloat(AppConstant.SP_FONTSCALE, fontSizeScale)
                        //重启应用
                        ActivityManager.getInstance().exitApp()
                        IntentUtils.toActivity(this@FontSizeActivity, WelcomeActivity::class.java, true)
                    }
                } else {
                    finish()
                }
            }
        fsvFontSize.setChangeCallbackListener { position ->
            val dimension = resources.getDimensionPixelSize(R.dimen.sp_stander).toFloat()
            //根据position 获取字体倍数
            fontSizeScale = (0.875 + 0.125 * position).toFloat()
            //放大后的sp单位
            val size = (fontSizeScale * ScreenUtils.px2sp(dimension)).toDouble()
            changeTextSize(size.toInt())
            isChange = position != defaultPos
            handleScaleImageView(fontSizeScale)
        }
        val scale = SPHelper.getFloat(AppConstant.SP_FONTSCALE, 0.0f)
        if (scale > 0.5) {
            defaultPos = ((scale - 0.875) / 0.125).toInt()
        } else {
            defaultPos = 1
        }
        //注意： 写在改变监听下面 —— 否则初始字体不会改变
        fsvFontSize.setDefaultPosition(defaultPos)
        //设置图片大小
        //handleScaleImageView(ScreenUtils.getFontSizeScale())
    }

    private fun handleScaleImageView(fontSize: Float) {
        val width = (ScreenUtils.dp2px(40f) * fontSize).toInt()
        val height = (ScreenUtils.dp2px(40f) * fontSize).toInt()
        LogUtils.info("fontSizeActivity------- wdith = $width --------- height = $height ------- fontSize =$fontSize")
        val ivFirstLayoutParams = iv_font_size.layoutParams
        ivFirstLayoutParams.width = width
        ivFirstLayoutParams.height = height
        iv_font_size.layoutParams = ivFirstLayoutParams

        val ivMiddleLayoutParams = iv_head_middle.layoutParams
        ivMiddleLayoutParams.width = width
        ivMiddleLayoutParams.height = height
        iv_head_middle.layoutParams = ivMiddleLayoutParams

        val lastLayoutParams = iv_head_last.layoutParams
        lastLayoutParams.width = width
        lastLayoutParams.height = height
        iv_head_last.layoutParams = lastLayoutParams
    }


    override fun initData() {
    }

    override fun initEvent() {
    }

    /**
     * 改变textsize 大小
     */
    fun changeTextSize(dimension: Int) {
        tv_font_size1.textSize = dimension.toFloat()
        tv_font_size2.textSize = dimension.toFloat()
        tv_font_size3.textSize = dimension.toFloat()
    }

    /**
     * 重新配置缩放系数
     * @return
     */

    override fun getResources(): Resources {
        val res = super.getResources()
        val config = res.configuration
        config.fontScale = 1f//1 设置正常字体大小的倍数
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }
}