package com.netease.nim.uikit.business.contact.selector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.laka.androidlib.util.screen.ScreenUtils;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.core.model.IContact;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class GalleryItemViewHolder {
    HeadImageView imageView;
    View viewMask;
}

public class ContactSelectAvatarAdapter extends BaseAdapter {
    private Context context;

    private List<IContact> selectedContactItems;

    public ContactSelectAvatarAdapter(Context context) {
        this.context = context;
        this.selectedContactItems = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return selectedContactItems.size();
    }

    @Override
    public Object getItem(int position) {
        return selectedContactItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HeadImageView imageView;
        View viewMask;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nim_contact_select_area_item, null);
            imageView = (HeadImageView) convertView.findViewById(R.id.contact_select_area_image);
            viewMask = convertView.findViewById(R.id.view_mask);
            GalleryItemViewHolder holder = new GalleryItemViewHolder();
            holder.imageView = imageView;
            holder.viewMask = viewMask;
            //设置item的宽高
            int nodeSize = (int) (ScreenUtils.getFontSizeScale() * ScreenUtils.dp2px(30)) + ScreenUtils.dp2px(6);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(nodeSize, nodeSize);
            convertView.setLayoutParams(layoutParams);
            convertView.setTag(holder);
        } else {
            GalleryItemViewHolder holder = (GalleryItemViewHolder) convertView.getTag();
            imageView = holder.imageView;
            viewMask = holder.viewMask;
        }

        try {
            IContact item = selectedContactItems.get(position);
            if (item != null) {
                int contactType = item.getContactType();
                if (contactType == IContact.Type.Team) {
                    Team mTeam = NimUIKit.getTeamProvider().getTeamById(item.getContactId());
                    imageView.loadTeamIconByTeam(mTeam);
                } else {
                    imageView.loadBuddyAvatar(item.getContactId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public void addContact(IContact contact) {
        if (selectedContactItems.size() > 0) {
            IContact iContact = selectedContactItems.get(selectedContactItems.size() - 1);
            if (iContact == null) {
                selectedContactItems.remove(selectedContactItems.size() - 1);
            }
        }
        this.selectedContactItems.add(contact);
    }

    public void removeContact(IContact contact) {
        if (contact == null) {
            return;
        }
        for (Iterator<IContact> iterator = selectedContactItems.iterator(); iterator.hasNext(); ) {
            IContact iContact = iterator.next();
            if (iContact == null) {
                continue;
            }
            if (iContact.getContactId().equals(contact.getContactId())) {
                iterator.remove();
            }
        }
    }

    /**
     * 预删除最后一个元素
     */
    public void prepareDeleteLastItem() {

    }

    public IContact remove(int pos) {
        return this.selectedContactItems.remove(pos);
    }

    public List<IContact> getSelectedContacts() {
        return this.selectedContactItems.subList(0, selectedContactItems.size());
    }
}
