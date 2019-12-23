package com.netease.nim.uikit.business.session.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
public class RedPackageHelperDialog extends Dialog {

    private View mContentView;
    private TextView mTvRedpackageRecord;
    private TextView mTvHelperCenter;
    private TextView mTvCancel;

    public RedPackageHelperDialog(@NonNull Context context) {
        this(context, R.style.commonDialog);
    }

    public RedPackageHelperDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置对话框布局，可以通过外部设置进来
        mContentView = getLayoutInflater().inflate(R.layout.dialog_redpackage_helper, null);
        initView();
        initAnima();
        setContentView(mContentView);
        // 设置LayoutParams的属性
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER | Gravity.BOTTOM;
    }

    private void initAnima() {
        getWindow().setWindowAnimations(R.style.bottom_menu_animation);
    }

    private void initView() {
        mTvRedpackageRecord = mContentView.findViewById(R.id.tv_redpackage_record);
        mTvHelperCenter = mContentView.findViewById(R.id.tv_helper_center);
        mTvCancel = mContentView.findViewById(R.id.tv_cancel);
        mTvRedpackageRecord.setOnClickListener(mOnClickListener);
        mTvHelperCenter.setOnClickListener(mOnClickListener);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private View.OnClickListener mOnClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }


}
