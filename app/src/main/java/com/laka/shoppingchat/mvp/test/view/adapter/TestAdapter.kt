package com.laka.shoppingchat.mvp.test.view.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.laka.shoppingchat.R

/**
 * @Author:summer
 * @Date:2019/9/23
 * @Description:
 */
class TestAdapter : BaseQuickAdapter<String, BaseViewHolder> {

    constructor( data: MutableList<String>?) : super(R.layout.item_order, data)

    override fun convert(helper: BaseViewHolder, item: String?) {

    }
}