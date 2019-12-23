package com.netease.nim.uikit.business.session.attachment;


import com.alibaba.fastjson.JSONObject;

/**
 * @Author:summer
 * @Date:2019/9/6
 * @Description:红包消息attachment
 */
public class RedPackageAttachment extends RobRedPackageAttachment {

    private int mType;  //红包类型  1：普通红包 2：拼手气红包
    private String mTitle; //红包标题
    private String mExtendData; //扩展消息
    private long mExpiredAt;  //时间戳，过期时间

    public RedPackageAttachment() {
        super(CustomAttachmentType.redPackage);
    }

    @Override
    protected void parseData(JSONObject data) {
        try {
            mHongBaoNo = data.getString("hongbao_no");
            mUserId = data.getString("user_id");
            mToId = data.getString("to_id");
            ope = data.getInteger("ope");
            mType = data.getInteger("type");
            mTitle = data.getString("title");
            mExtendData = data.getString("ex");
            mExpiredAt = data.getLong("expired_at");
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
        json.put("ope", ope);
        json.put("type", mType);
        json.put("title", mTitle);
        json.put("ex", mExtendData);
        json.put("expired_at", mExpiredAt);
        return json;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmExtendData() {
        return mExtendData;
    }

    public void setmExtendData(String mExtendData) {
        this.mExtendData = mExtendData;
    }

    public long getmExpiredAt() {
        return mExpiredAt;
    }

    public void setmExpiredAt(long mExpiredAt) {
        this.mExpiredAt = mExpiredAt;
    }

    @Override
    public String toString() {
        return "RedPackageAttachment{" +
                "mHongBaoNo='" + mHongBaoNo + '\'' +
                ", mUserId='" + mUserId + '\'' +
                ", mToId='" + mToId + '\'' +
                ", ope=" + ope +
                ", mType=" + mType +
                ", mTitle='" + mTitle + '\'' +
                ", mExtendData='" + mExtendData + '\'' +
                ", mExpiredAt=" + mExpiredAt +
                '}';
    }
}
