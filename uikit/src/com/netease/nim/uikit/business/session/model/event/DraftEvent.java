package com.netease.nim.uikit.business.session.model.event;

public class DraftEvent {
    public String account;
    public String content;
    public long time;

    public DraftEvent(String account, String content, long time) {
        this.account = account;
        this.content = content;
        this.time = time;
    }
}
