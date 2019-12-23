package com.netease.nim.uikit.business.session.adapter

import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.laka.androidlib.util.imageload.GlideLoader
import com.netease.nim.uikit.R
import com.netease.nim.uikit.business.session.model.bean.RedPackageDetailList
import com.netease.nim.uikit.business.session.model.bean.RedPackageDetailListBean
import com.netease.nim.uikit.business.session.model.bean.RedPackageListBean
import com.netease.nim.uikit.business.session.utils.RoundUtils
import com.netease.nim.uikit.business.uinfo.UserInfoHelper
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author:summer
 * @Date:2019/9/19
 * @Description:
 */
class RobRedPackageListAdapter : BaseQuickAdapter<RedPackageDetailListBean, BaseViewHolder> {

    constructor(layoutResId: Int, data: MutableList<RedPackageDetailListBean>?) : super(
        layoutResId,
        data
    )

    override fun convert(helper: BaseViewHolder, item: RedPackageDetailListBean?) {
        R.layout.item_redpackage_record_list
        GlideLoader.loadImage(
            mContext,
            UserInfoHelper.getUserHeadImage(item?.userId),
            helper.getView<ImageView>(R.id.iv_avatar)
        )
        helper.getView<TextView>(R.id.tv_user_name)?.text =
            UserInfoHelper.getUserName(item?.userId)
        val sdf = SimpleDateFormat("MM-dd HH:mm")
        helper.getView<TextView>(R.id.tv_time)?.text =
            sdf.format(Date((item?.recordTime ?: 0) * 1000))
        helper.getView<TextView>(R.id.tv_royalty_commission)?.text =
            "${RoundUtils.roundForHalf(item?.amount ?: "0")}元"

        val tvRoyalty = helper.getView<TextView>(R.id.tv_royalty_txt)
        when (item?.luckiest) { //int. 0:普通红包 1:拼手气红包 2:最佳手气
            0,
            1 -> {
                tvRoyalty?.visibility = View.GONE
            }
            2 -> {
                tvRoyalty?.visibility = View.VISIBLE
            }
            else -> {
                tvRoyalty?.visibility = View.GONE
            }
        }

    }
}