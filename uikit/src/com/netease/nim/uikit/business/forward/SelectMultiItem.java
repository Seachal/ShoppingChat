package com.netease.nim.uikit.business.forward;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.entity.SectionMultiEntity;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class SelectMultiItem extends SectionMultiEntity<MultiContactItem> implements MultiItemEntity {

    public static final int Contact = 1;
    public static final int MORE = 2;
    private int itemType;
    private boolean isMore;
    private boolean isMultipleSelect;
    private boolean isSelect = false;
    private MultiContactItem contactItem;

    public SelectMultiItem(boolean isHeader, String header, boolean isMore) {
        super(isHeader, header);
        this.isMore = isMore;
    }

    public SelectMultiItem(int itemType, MultiContactItem video, boolean select) {
        super(video);
        this.contactItem = video;
        this.itemType = itemType;
        this.isMultipleSelect = select;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isMultipleSelect() {
        return isMultipleSelect;
    }

    public void setMultipleSelect(boolean multipleSelect) {
        isMultipleSelect = multipleSelect;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public MultiContactItem getContactItem() {
        return contactItem;
    }

    public void setContactItem(MultiContactItem video) {
        this.contactItem = video;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
