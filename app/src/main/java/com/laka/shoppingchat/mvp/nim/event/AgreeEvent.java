package com.laka.shoppingchat.mvp.nim.event;

public class AgreeEvent {
    public String account;
    public boolean agree = true;

    public AgreeEvent(String account) {
        this.account = account;
    }

    public AgreeEvent(String account, boolean agree) {
        this.account = account;
        this.agree = agree;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
