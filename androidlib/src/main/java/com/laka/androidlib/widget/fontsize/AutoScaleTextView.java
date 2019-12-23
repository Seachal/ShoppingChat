package com.laka.androidlib.widget.fontsize;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.laka.androidlib.util.screen.ScreenUtils;

/**
 * 为了适配字体更改后，未读消息小红点不至于变扁（通过设置minHeight/minWidth来保持初始状态小红点的圆形，如果是99+，则呈现椭圆形）
 */
public class AutoScaleTextView extends android.support.v7.widget.AppCompatTextView {

    public AutoScaleTextView(Context context) {
        super(context);
    }

    public AutoScaleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScaleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //minHeight、minWidth 跟随字体大小变化
        int readMinHeight = (int) (getMinHeight() * ScreenUtils.getFontSizeScale());
        int readMinWidth = (int) (getMinWidth() * ScreenUtils.getFontSizeScale());
        setMinimumHeight(readMinHeight);
        setMinimumWidth(readMinWidth);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
