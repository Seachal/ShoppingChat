package com.netease.nim.uikit.business.session.attachment;


import com.alibaba.fastjson.JSONObject;

/**
 * @Author:summer
 * @Date:2019/9/6
 * @Description:红包被抢通知消息（自定义）
 */
public class RobRedPackageAttachment extends CustomAttachment {

    protected String mHongBaoNo; //红包编号
    protected String mUserId; //发红包人的account
    protected String mToId;  //收红包的用户account，可能是个人也可能是群
    protected int ope; // 0:个人  1:群
    private String mOpUser; //抢红包的人
    private boolean mLast;  //是否是最后一个红包

    public RobRedPackageAttachment() {
        super(CustomAttachmentType.robRedPackage);
    }

    public RobRedPackageAttachment(int type) {
        super(type);
    }

    @Override
    protected void parseData(JSONObject data) {
        try {
            mHongBaoNo = data.getString("hongbao_no");
            mUserId = data.getString("user_id");
            mToId = data.getString("to_id");
            ope = data.getInteger("ope");
            mOpUser = data.getString("op_user");
            mLast = data.getBoolean("last");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject packData() {
        JSONObject json = new JSONObject();
        json.put("hongbao_no", mHongBaoNo);
        json.put("user_id", mUserId);
        json.put("to_id", mToId);
        json.put("op_user", mOpUser);
        json.put("last", mLast);
        return json;
    }

    public String getmHongBaoNo() {
        return mHongBaoNo;
    }

    public void setmHongBaoNo(String mHongBaoNo) {
        this.mHongBaoNo = mHongBaoNo;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmToId() {
        return mToId;
    }

    public void setmToId(String mToId) {
        this.mToId = mToId;
    }

    public String getmOpUser() {
        return mOpUser;
    }

    public void setmOpUser(String mOpUser) {
        this.mOpUser = mOpUser;
    }

    public boolean ismLast() {
        return mLast;
    }

    public void setmLast(boolean mLast) {
        this.mLast = mLast;
    }

    public int getOpe() {
        return ope;
    }

    public void setOpe(int ope) {
        this.ope = ope;
    }

    @Override
    public String toString() {
        return "RobRedPackageAttachment{" +
                "mHongBaoNo='" + mHongBaoNo + '\'' +
                ", mUserId='" + mUserId + '\'' +
                ", mToId='" + mToId + '\'' +
                ", mOpUser='" + mOpUser + '\'' +
                ", mLast=" + mLast +
                ", ope=" + ope +
                '}';
    }
}
