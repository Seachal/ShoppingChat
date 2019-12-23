package com.netease.nim.uikit.business.session.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.laka.androidlib.R;

/**
 * @Author:summer
 * @Date:2019/1/12
 * @Description:红包记录、帮助中心等弹窗
 */
public class TextMessageDialog extends Dialog {

    private View mContentView;

    public TextMessageDialog(@NonNull Context context) {
        this(context, R.style.commonDialog);
    }

    public TextMessageDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置对话框布局，可以通过外部设置进来
        mContentView = getLayoutInflater().inflate(R.layout.nim_txt_message_dialog, null);
        initView();
        initAnim();
        setContentView(mContentView);
        // 设置LayoutParams的属性
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER | Gravity.BOTTOM;
    }

    private void initAnim() {
        //getWindow().setWindowAnimations(R.style.bottom_menu_animation);
    }

    private void initView() {

    }

    private View.OnClickListener mOnClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }


}
