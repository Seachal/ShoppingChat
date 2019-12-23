package com.laka.shoppingchat.mvp.wallet.view.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.laka.androidlib.util.StringUtils
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.shopping.center.model.bean.ProductWithCoupon
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecord
import com.netease.nim.uikit.business.session.utils.RoundUtils
import com.netease.nim.uikit.business.uinfo.UserInfoHelper
import java.text.SimpleDateFormat
import java.util.*

class RedPackAdapter(var mData: MutableList<RedPackageRecord>) :
    BaseQuickAdapter<RedPackageRecord, BaseViewHolder>(R.layout.item_redpack, mData) {

    override fun convert(helper: BaseViewHolder, item: RedPackageRecord?) {
        helper?.setText(R.id.tv_price, RoundUtils.roundForHalf("${item?.amount}") + "元")
        val sdf = SimpleDateFormat("MM-dd")
        helper?.getView<TextView>(com.netease.nim.uikit.R.id.tv_time)?.text =
            sdf.format(Date((item?.record_time ?: 0) * 1000))
        if (StringUtils.isEmpty(item?.show_msg)) { //收到的红包记录
            helper?.setText(R.id.tv_name, UserInfoHelper.getUserDisplayName(item?.sender_user_id))
            helper?.getView<TextView>(R.id.tv_msg)?.visibility = View.GONE
            when (item?.hongbao_type) {
                1 -> { //普通红包
                    helper?.getView<ImageView>(R.id.iv_icon)?.visibility = View.GONE
                }
                2 -> { // 拼手气红包
                    helper?.getView<ImageView>(R.id.iv_icon)?.visibility = View.VISIBLE
                }
                else -> {
                    helper?.getView<ImageView>(R.id.iv_icon)?.visibility = View.GONE
                }
            }
        } else { //发出的红包记录
            helper?.getView<ImageView>(R.id.iv_icon)?.visibility = View.GONE
            helper?.getView<TextView>(R.id.tv_msg)?.visibility = View.VISIBLE
            helper?.setText(R.id.tv_msg, "${item?.show_msg}")
            when (item?.hongbao_type) {
                1 -> { //普通红包
                    helper?.setText(R.id.tv_name, "普通红包")
                }
                2 -> { // 拼手气红包
                    helper?.setText(R.id.tv_name, "拼手气红包")
                }
                else -> {
                    helper?.setText(R.id.tv_name, "普通红包")
                }
            }
        }
    }
}