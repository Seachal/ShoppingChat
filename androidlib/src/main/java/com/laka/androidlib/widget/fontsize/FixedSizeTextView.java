package com.laka.androidlib.widget.fontsize;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.laka.androidlib.util.screen.ScreenUtils;

/**
 * 为了适配字体更改后，未读消息小红点不至于变扁（通过设置minHeight/minWidth来保持初始状态小红点的圆形，如果是99+，则呈现椭圆形）
 */
public class FixedSizeTextView extends android.support.v7.widget.AppCompatTextView {

    public FixedSizeTextView(Context context) {
        this(context, null);
    }

    public FixedSizeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FixedSizeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float textSize = getTextSize();
        float fontSizeScale = ScreenUtils.getFontSizeScale();
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize / fontSizeScale); //不随应用设置的字体大小改变而改变
    }

}
