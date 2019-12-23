package com.netease.nim.uikit.business.session.listener;

import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderAudio;
import com.netease.nim.uikit.common.media.audioplayer.BaseAudioControl;
import com.netease.nim.uikit.common.media.audioplayer.Playable;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.lang.ref.WeakReference;

public class CustomAudioControlListener implements BaseAudioControl.AudioControlListener {

    private WeakReference<MsgViewHolderAudio> mMsgViewHolder;
    private IMMessage mMessage;

    public CustomAudioControlListener(MsgViewHolderAudio viewHolderAudio) {
        mMsgViewHolder = new WeakReference<>(viewHolderAudio);
    }

    public void setImMessage(IMMessage message) {
        mMessage = message;
    }

    /**
     * 显示播放过程中的进度条
     */
    @Override
    public void updatePlayingProgress(Playable playable, long curPosition) {
        MsgViewHolderAudio msgViewHolderAudio = mMsgViewHolder.get();
        if (msgViewHolderAudio == null) return;
        if (!msgViewHolderAudio.isTheSame(mMessage.getUuid())) {
            return;
        }
        if (curPosition > playable.getDuration()) {
            return;
        }
        msgViewHolderAudio.updateTime(curPosition);
    }

    /**
     * AudioControl准备就绪，已经postDelayed playRunnable，不等同于AudioPlayer已经开始播放
     */
    @Override
    public void onAudioControllerReady(Playable playable) {
        MsgViewHolderAudio msgViewHolderAudio = mMsgViewHolder.get();
        if (msgViewHolderAudio == null) return;
        if (!msgViewHolderAudio.isTheSame(mMessage.getUuid())) {
            return;
        }
        msgViewHolderAudio.play();
    }

    /**
     * 结束播放
     */
    @Override
    public void onEndPlay(Playable playable) {
        MsgViewHolderAudio msgViewHolderAudio = mMsgViewHolder.get();
        if (!msgViewHolderAudio.isTheSame(mMessage.getUuid())) {
            return;
        }
        msgViewHolderAudio.updateTime(playable.getDuration());
        msgViewHolderAudio.stop();
    }
}
