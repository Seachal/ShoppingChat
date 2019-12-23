package com.laka.shoppingchat.mvp.shopping.center.view.adapter

import android.animation.ObjectAnimator
import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.laka.androidlib.base.adapter.MultipleAdapterItem
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.shop.model.bean.TitleTypeBean

/**
 * @Author:summer
 * @Date:2019/4/28
 * @Description:
 */
class ProductDetailMoreItem : MultipleAdapterItem<TitleTypeBean> {

    override fun convert(helper: BaseViewHolder, item: TitleTypeBean?) {
        item?.let {
            val ivMore = helper.getView<ImageView>(R.id.image_view_more)
            if (it.open == 1) {
                val anim = ObjectAnimator.ofFloat(ivMore, "rotation", 90f)
                anim.duration = 300
                anim.start()
            }
        }
    }
}