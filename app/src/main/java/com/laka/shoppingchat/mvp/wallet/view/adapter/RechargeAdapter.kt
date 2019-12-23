package com.laka.shoppingchat.mvp.wallet.view.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.laka.androidlib.widget.SelectorButton
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.wallet.model.bean.RechargeBean

class RechargeAdapter(data: MutableList<RechargeBean>) :
    BaseQuickAdapter<RechargeBean, BaseViewHolder>(R.layout.item_recharge, data) {

    override fun convert(helper: BaseViewHolder, item: RechargeBean) {
        var view = helper.getView<SelectorButton>(R.id.recharge_item)
        helper.setText(R.id.recharge_item, "${item.price}元")
            .setTextColor(
                R.id.recharge_item, if (item.isSelect) {
                    mContext.resources.getColor(R.color.white)
                } else {
                    mContext.resources.getColor(R.color.color_2d2d2d)
                }
            )
        if (item.isSelect) {
            view.setBgaColor("#07C160")
        } else {
            view.setBgaColor("#FFFFFF")
        }
    }
}