package com.laka.androidlib.widget.refresh;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

/**
 * @Author:summer
 * @Date:2019/9/24
 * @Description:loading 头部
 */
public class CustomClassicsFooter extends ClassicsFooter {

    public CustomClassicsFooter(Context context) {
        super(context);
    }

    public CustomClassicsFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mNoMoreData != noMoreData) {
            mNoMoreData = noMoreData;
            View arrowView = mArrowView;
            if (noMoreData) {
                mTitleText.setText(mTextNothing);
                arrowView.setVisibility(View.GONE);
                mProgressView.setVisibility(View.GONE);
            } else {
                mTitleText.setText(mTextPulling);
                arrowView.setVisibility(View.VISIBLE);
                mProgressView.setVisibility(View.VISIBLE);
            }
        }
        return true;
    }
}