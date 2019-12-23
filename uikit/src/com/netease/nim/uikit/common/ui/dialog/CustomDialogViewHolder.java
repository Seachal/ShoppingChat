package com.netease.nim.uikit.common.ui.dialog;

import android.util.Pair;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.adapter.TViewHolder;

public class CustomDialogViewHolder extends TViewHolder {

    private TextView itemView;

    @Override
    protected int getResId() {
        return R.layout.nim_custom_dialog_list_item;
    }

    @Override
    protected void inflate() {
        itemView = view.findViewById(R.id.custom_dialog_text_view);
    }

    @Override
    protected void refresh(Object item) {
        if (position == 0) {
            if (position == getAdapter().getCount() - 1) {
                //只有一项
                itemView.setBackgroundResource(R.drawable.selector_dialog_item_only);
            } else {
                itemView.setBackgroundResource(R.drawable.selector_bg_take_phone);
            }
        } else if (position == getAdapter().getCount() - 1) {
            itemView.setBackgroundResource(R.drawable.selector_bg_select_photo);
        } else {
            itemView.setBackgroundResource(R.drawable.selector_dialog_item_center);
        }
        if (item instanceof Pair<?, ?>) {
            Pair<String, Integer> pair = (Pair<String, Integer>) item;
            itemView.setText(pair.first);
            itemView.setTextColor(context.getResources().getColor(pair.second));
        }
    }

}
