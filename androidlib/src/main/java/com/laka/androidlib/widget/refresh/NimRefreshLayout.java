package com.laka.androidlib.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

/**
 * @Author:summer
 * @Date:2019/9/23
 * @Description:刷新layout，套在Rv外部
 */
public class NimRefreshLayout extends SmartRefreshLayout {

    private int mPageNumber = 1;

    public NimRefreshLayout(Context context) {
        super(context);
        initAttrs();
    }

    public NimRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs();
    }

    private void initAttrs() {
        setEnableNestedScroll(true);
        setEnableAutoLoadMore(true);
        setEnableOverScrollBounce(true);
        setEnableFooterFollowWhenNoMoreData(true);
        setEnableLoadMoreWhenContentNotFull(false);
        setDisableContentWhenRefresh(false);
        setDisableContentWhenLoading(false);
        setClipToPadding(false);
        setRefreshFooter(new CustomClassicsFooter(getContext()));
        setRefreshHeader(new ClassicsHeader(getContext()));
    }

    public int loadCurrentPage() {
        return mPageNumber++;
    }

    public void resetCurrentPage() {
        mPageNumber = 1;
    }

}