package com.netease.nim.uikit.business.contact.core.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.TextItem;
import com.netease.nim.uikit.business.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.common.util.string.StringUtil;

public class TextHolder extends AbsContactViewHolder<TextItem> {
    private TextView textView;
    private View divder;

    public void refresh(ContactDataAdapter contactAdapter, int position, TextItem item) {

        //分割线
        String group = item.belongsGroup();
        if (position > 0 && !StringUtil.isEmpty(group)) {
            AbsContactItem preItem = (AbsContactItem) contactAdapter.getItem(position - 1);
            String preGroup = preItem.belongsGroup();
            if (!group.equals(preGroup)) {
                divder.setVisibility(View.GONE);
            } else {
                divder.setVisibility(View.VISIBLE);
            }
        } else {
            divder.setVisibility(View.GONE);
        }

        textView.setText(item.getText());
    }

    @Override
    public View inflate(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.nim_contact_text_item, null);
        textView = view.findViewById(R.id.text);
        divder = view.findViewById(R.id.line);
        return view;
    }
}
