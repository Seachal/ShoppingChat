package com.laka.androidlib.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/** 加载布局,查找控件和绑定控件
 * Created by sming on 2018/07/02
 */

public class JDialogViewHelper {
    private Context mContext;
    private View mContentView;
    private SparseArray<WeakReference<View>> mViews;
    public OnJAlertDialogClickListener mOnJAlertDialogClickListener;
    private Dialog mDialog;
    public JDialogViewHelper(Context context, int viewLayoutResId) {
        mViews = new SparseArray<>();
        mContentView = LayoutInflater.from(context).inflate(viewLayoutResId, null);
    }

    public JDialogViewHelper(Context context, View view) {
        mViews = new SparseArray<>();
        mContentView = view;
    }

    public void setText(@IdRes int viewId, CharSequence charSequence) {
        TextView textView = getView(viewId);
        if (null != textView) {
            textView.setText(charSequence);
        }
    }

    public void setOnClick(final int position, @IdRes int viewId) {
        View view = getView(viewId);
        if (null != view) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnJAlertDialogClickListener){
                        mOnJAlertDialogClickListener.onClick(mDialog,v,position);
                    }
                }
            });
        }
    }


    public View getContentView() {
        return mContentView;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = null;
        WeakReference<View> viewWeakReference = mViews.get(viewId);
        if (null != viewWeakReference) {
            view = viewWeakReference.get();
        }
        if (null == view) {
            view = mContentView.findViewById(viewId);
            if (null != view)
                mViews.put(viewId, new WeakReference<View>(view));
        }
        return (T) view;
    }


    public void setOnJAlertDialogClickListener(OnJAlertDialogClickListener onJAlertDialogClickListener) {
        mOnJAlertDialogClickListener = onJAlertDialogClickListener;
    }


    public void setDialog(Dialog dialog) {
        mDialog = dialog;
    }
}
