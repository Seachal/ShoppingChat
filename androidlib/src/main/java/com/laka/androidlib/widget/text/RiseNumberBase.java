package com.laka.androidlib.widget.text;

/**
 * @Author:summer
 * @Date:2019/10/16
 * @Description:
 */
public interface RiseNumberBase {
    //开始动画
    void start();
    //设置动画结束监听
    void setOnEnd(RiseNumberTextView.EndListener callback);

    /**
     * 设置显示的数值
     * */
    RiseNumberTextView withNumber(float number);

    /**
     * 设置显示的数值
     * */
    RiseNumberTextView withNumber(float number, boolean flag);

    /**
     * 设置显示的数值
     * */
    RiseNumberTextView withNumber(int number);

    /**
     * 动画执行时间
     * */
    RiseNumberTextView setDuration(long duration);

}
