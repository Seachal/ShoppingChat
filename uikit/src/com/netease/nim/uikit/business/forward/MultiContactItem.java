package com.netease.nim.uikit.business.forward;

import com.netease.nim.uikit.business.contact.core.item.ContactCustomItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.model.IContact;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

public class MultiContactItem {
    public static final String CHAT_TEAM = "_chatTeam"; // 返回结果
    public static final String CHAT_P2P = "_chatP2P"; // 返回结果
    RecentContact recentContact;
    ContactItem contactItem;
    ContactCustomItem contactCustomItem;

    public MultiContactItem() {

    }

    public MultiContactItem(ContactCustomItem contactCustomItem) {
        this.contactCustomItem = contactCustomItem;
    }

    public MultiContactItem(ContactItem contactItem) {
        this.contactItem = contactItem;
    }

    public MultiContactItem(RecentContact recentContact) {
        this.recentContact = recentContact;
    }

    public static String spliceContactId(int type, String contactId) {
        if (type == IContact.Type.Team) {
            return contactId + CHAT_TEAM;
        } else if (type == IContact.Type.Friend) {
            return contactId + CHAT_P2P;
        }
        return contactId;
    }

    public static MultiContactItem multipleContactItem(String id) {
        MultiContactItem multiContactItem = new MultiContactItem();
        if (isP2P(id)) {
            multiContactItem.setContactCustomItem(new ContactCustomItem(decodeContactId(id), IContact.Type.Friend));
        } else {
            multiContactItem.setContactCustomItem(new ContactCustomItem(decodeContactId(id), IContact.Type.Team));
        }
        return multiContactItem;
    }

    public static String decodeContactId(String contactId) {
        int index = contactId.indexOf(CHAT_TEAM);
        if (index != -1) {
            return contactId.substring(0, index);
        } else {
            int index1 = contactId.indexOf(CHAT_P2P);
            if (index1 != -1) {
                return contactId.substring(0, index1);
            }
        }
        return contactId;
    }

    public static boolean isP2P(String contactId) {
        int index = contactId.indexOf(CHAT_P2P);
        if (index != -1) {
            return true;
        }
        return false;
    }

    public static boolean isTeam(String contactId) {
        int index = contactId.indexOf(CHAT_TEAM);
        if (index != -1) {
            return true;
        }
        return false;
    }

    public ContactCustomItem getContactCustomItem() {
        return contactCustomItem;
    }

    public void setContactCustomItem(ContactCustomItem contactCustomItem) {
        this.contactCustomItem = contactCustomItem;
    }

    public ContactItem getContactItem() {
        return contactItem;
    }

    public void setContactItem(ContactItem contactItem) {
        this.contactItem = contactItem;
    }

    public RecentContact getRecentContact() {
        return recentContact;
    }

    public void setRecentContact(RecentContact recentContact) {
        this.recentContact = recentContact;
    }

    public String getContactId() {
        if (recentContact != null) {
            return recentContact.getContactId();
        } else if (contactItem != null) {
            return contactItem.getContact().getContactId();
        } else if (contactCustomItem != null) {
            return contactCustomItem.getContactId();
        }
        return "";
    }

    public int getContactType() {
        if (recentContact != null) {
            if (recentContact.getSessionType() == SessionTypeEnum.P2P) {
                return IContact.Type.Friend;
            } else {
                return IContact.Type.Team;
            }
        } else if (contactItem != null) {
            return contactItem.getContact().getContactType();
        } else if (contactCustomItem != null) {
            return contactCustomItem.getContactType();
        }
        return IContact.Type.Friend;
    }

    public ContactCustomItem ceateContact() {
        return new ContactCustomItem(getContactId(), getContactType());
    }
}
