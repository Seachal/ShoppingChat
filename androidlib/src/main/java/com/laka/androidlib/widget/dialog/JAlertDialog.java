package com.laka.androidlib.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.laka.androidlib.R;


/**
 * Created by sming on 2018/07/02
 */

public class JAlertDialog extends Dialog {

    private JAlertController mAlert;
    private Context mContext;
    private boolean isOnBackFinish;

    public JAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        mAlert = new JAlertController(this, getWindow());
    }


    public static class Builder {
        private final JAlertController.AlertParams mAlertParams;


        public Builder(Context context) {
            this(context, R.style.JDialogStyle);
        }

        public Builder(Context context, @StyleRes int themeRes) {
            mAlertParams = new JAlertController.AlertParams(context, themeRes);
        }

        public Builder setContentView(View view) {
            mAlertParams.mView = view;
            mAlertParams.mViewLayoutResId = 0;
            return this;
        }

        public Builder setContentView(int layoutResId) {
            mAlertParams.mView = null;
            mAlertParams.mViewLayoutResId = layoutResId;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mAlertParams.mCancelable = cancelable;
            return this;
        }
        public Builder setIsOnBackFinish(boolean isOnBackFinish) {
            mAlertParams.isOnBackFinish = isOnBackFinish;
            return this;
        }


        public Builder setText(@IdRes int viewId, CharSequence text) {
            mAlertParams.mTextArr.put(viewId,text);
            return this;
        }


        public Builder setFromBottom() {
            mAlertParams.mGravity = Gravity.BOTTOM;
            return this;
        }

        public Builder setAnimation(@StyleRes int styleAnim) {
            mAlertParams.mAnimation = styleAnim;
            return this;
        }

        public Builder setHasAnimation(boolean hasAnimation) {
            mAlertParams.mHasAnimation = hasAnimation;
            return this;
        }

        public Builder setFullWidth() {
            mAlertParams.mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        public Builder setWightPercent(float percent){
            Window window = ((Activity)mAlertParams.mContext).getWindow();
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            WindowManager windowManager = window.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            mAlertParams.mWidth = (int) (percent * display.getWidth());
            return this;
        }

        public Builder setHeightPercent(float percent){
            Window window = ((Activity)mAlertParams.mContext).getWindow();
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            WindowManager windowManager = window.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            mAlertParams.mHeight = (int) (percent * display.getHeight());
            return this;
        }

        public Builder setWidthAndHeight(int width,int height) {
            mAlertParams.mWidth = width;
            mAlertParams.mHeight = height;
            return this;
        }

        public Builder setOnClick(@IdRes int viewId) {
            mAlertParams.mClickArr.put(mAlertParams.mClickArr.size(),viewId);
            return this;
        }

        public Builder setOnJAlertDialogCLickListener(OnJAlertDialogClickListener onJAlertDialogClickListener) {
            mAlertParams.mOnJAlertDialogClickListener = onJAlertDialogClickListener;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            mAlertParams.mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnOnDismissListener(OnDismissListener onDismissListener) {
            mAlertParams.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            mAlertParams.mOnKeyListener = onKeyListener;
            return this;
        }


        public JAlertDialog create() {
            final JAlertDialog dialog = new JAlertDialog(mAlertParams.mContext, mAlertParams.mThemeRes);
            mAlertParams.apply(dialog.mAlert);
            dialog.setIsOnBackFinish(mAlertParams.isOnBackFinish);
            dialog.setCancelable(mAlertParams.mCancelable);
            if (mAlertParams.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(mAlertParams.mOnCancelListener);
            dialog.setOnDismissListener(mAlertParams.mOnDismissListener);
            if (mAlertParams.mOnKeyListener != null) {
                dialog.setOnKeyListener(mAlertParams.mOnKeyListener);
            }
            return dialog;
        }

        public JAlertDialog show() {
            JAlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }
    private void setIsOnBackFinish(boolean isOnBackFinish){
        this.isOnBackFinish = isOnBackFinish;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isOnBackFinish){
            Activity a = (Activity) mContext;
            a.finish();
        }
    }
}
