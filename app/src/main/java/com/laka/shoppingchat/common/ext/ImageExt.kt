package com.laka.shoppingchat.common.ext


import android.widget.ImageView
import com.laka.androidlib.util.imageload.GlideLoader


import com.laka.shoppingchat.R

/**
 * Created by aa on 2019-08-02.
 * @auto sming
 */


fun ImageView.loadImage(
    url: String,
    placeholderRes: Int = R.drawable.default_img,
    errorRes: Int = R.drawable.default_img
) {
    GlideLoader.loadImage(context, url, placeholderRes, this)
}

fun ImageView.loadCircleImage(targetUrl: Int) {
    GlideLoader.displayCircleImage(context, targetUrl, this)
}

fun ImageView.loadCircleImage(targetUrl: String) {
    GlideLoader.displayCircleImage(context, targetUrl, this)
}

fun ImageView.loadCircleImage(targetUrl: String, placeholderRes: Int) {
    GlideLoader.displayCircleImage(context, targetUrl, placeholderRes, this)
}