package com.laka.shoppingchat.common.dsl

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.laka.androidlib.widget.refresh.BaseListBean
import com.laka.androidlib.widget.refresh.OnResultListener
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.widget.refresh.FrogRefreshRecyclerView

class RVWrapper {
    var view: FrogRefreshRecyclerView? = null
    var enableTopPadding = 0f
    var decoration: RecyclerView.ItemDecoration? = null
    var isEnableMultiClick = false
    var layoutManager: RecyclerView.LayoutManager? = null
    var resultListener: OnResultListener? = null
    var enableClickLoadMore = true
    var loadingView: View? = null
    var enableRefresh = true
    var enableLoadMore = true
    var mResultListener: OnResultListener? = null

    var adapter: BaseQuickAdapter<*, BaseViewHolder>? = null
    var onRequest: ((page: Int, resultListener: OnResultListener) -> Unit)? = null
    var adapterItemChildClick: ((adapter: BaseQuickAdapter<*, BaseViewHolder>, view: View, position: Int) -> Unit)? =
        null//(BaseQuickAdapter adapter, View view, int position
    var adapterItemClick: ((adapter: BaseQuickAdapter<*, BaseViewHolder>, view: View, position: Int) -> Unit)? =
        null//(BaseQuickAdapter adapter, View view, int position

    fun onResponse(response: BaseListBean<*>) {
        mResultListener?.let {
            it.onResponse(response)
        }
    }

    fun onFailure() {
        mResultListener?.let {
            it.onFailure(-1, "")
        }
    }
}

fun refreshInit(init: RVWrapper.() -> Unit): RVWrapper {
    val refreshLayout = RVWrapper()
    refreshLayout.init()
    execute(refreshLayout)
    return refreshLayout
}

fun execute(wrap: RVWrapper) {
    wrap.view?.let { refresh ->
        refresh.enableTopPadding(wrap.enableTopPadding)
        refresh.enableClickLoadMore(wrap.enableClickLoadMore)
        refresh.enableRefresh(wrap.enableRefresh)
        refresh.enableLoadMore(wrap.enableLoadMore)
        refresh.setEnableMultiClick(wrap.isEnableMultiClick)
            .setLayoutManager(
                if (wrap.layoutManager == null) {
                    LinearLayoutManager(refresh.context)
                } else {
                    wrap.layoutManager
                }
            )

        wrap.decoration?.let {
            refresh.addItemDecoration(it)
        }
        wrap.resultListener?.let {
            refresh.setOnResultListener(it)
        }
        wrap.adapter?.let {
            refresh.adapter = it
        }
        wrap.loadingView?.let {
            refresh.setLoadingView(
                LayoutInflater.from(refresh.context).inflate(
                    R.layout.view_list_loading,
                    null
                )
            )
        }
        wrap.onRequest?.let { method ->
            refresh.setOnRequestListener { page, resultListener ->
                wrap.mResultListener = resultListener
                method(page, resultListener)
                ""
            }
        }
        wrap.adapterItemChildClick?.let {
            wrap.adapter?.setOnItemChildClickListener { adapter, view, position ->
                it(wrap.adapter!!, view, position)
            }
        }
        wrap.adapterItemClick?.let {
            wrap.adapter?.setOnItemClickListener { adapter, view, position ->
                it(wrap.adapter!!, view, position)
            }
        }
    }
}