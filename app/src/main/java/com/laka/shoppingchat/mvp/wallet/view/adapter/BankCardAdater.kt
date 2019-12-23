package com.laka.shoppingchat.mvp.wallet.view.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.laka.shoppingchat.R

class BankCardAdater(var mData: MutableList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_bank_card, mData) {
    override fun convert(helper: BaseViewHolder, item: String?) {

    }
}