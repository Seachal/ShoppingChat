package com.netease.nim.uikit.business.contact.core.item;

public class ContactCustomItem {
    private String contactId;
    private int contactType;

    public ContactCustomItem(String contactId, int contactType) {
        this.contactId = contactId;
        this.contactType = contactType;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }
}
