package com.laka.androidlib.widget.fontsize;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.laka.androidlib.util.screen.ScreenUtils;

/**
 * 适配图片 随应用内设置文字大小 变化而变化，
 * */
public class AutoScaleImageView extends android.support.v7.widget.AppCompatImageView {

    public AutoScaleImageView(Context context) {
        super(context);
    }

    public AutoScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int readHeight = (int) (getMeasuredHeight() * ScreenUtils.getFontSizeScale());
        int readWidth = (int) (getMeasuredWidth() * ScreenUtils.getFontSizeScale());
        setMeasuredDimension(readWidth, readHeight);
    }


}
