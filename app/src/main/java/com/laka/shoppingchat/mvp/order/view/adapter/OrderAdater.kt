package com.laka.shoppingchat.mvp.order.view.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.laka.shoppingchat.R

class OrderAdater(var mData: MutableList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_order, mData) {
    override fun convert(helper: BaseViewHolder, item: String?) {

    }
}