package com.netease.nim.uikit.business.team.model;

public class TeamMemberBean {
    private String account;
    private String tid;
    private boolean isAdd;
    private boolean isSubtract;

    public TeamMemberBean(boolean isAdd, boolean isSubtract) {
        this.isAdd = isAdd;
        this.isSubtract = isSubtract;
    }

    public TeamMemberBean(String account, String tid) {
        this.account = account;
        this.tid = tid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public boolean isSubtract() {
        return isSubtract;
    }

    public void setSubtract(boolean subtract) {
        isSubtract = subtract;
    }
}
