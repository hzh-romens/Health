package com.romens.yjk.health.im.ui.cell;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.VoiceMessageBody;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.IMMessagesController;
import com.romens.yjk.health.db.entity.MessageObject;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.helper.MessageHelper;
import com.romens.yjk.health.im.VoicePlayController;

/**
 * Created by siery on 15/9/6.
 */
public class ChatVoiceMessageForToCell extends ChatVoiceCell {

    private FrameLayout messageContainer;

    private ImageView playView;
    private TextView playTimeView;

    private TextView timeView;


    private UserEntity currentUser;
    private int voiceTotalTime = 0;

    public ChatVoiceMessageForToCell(Context context) {
        super(context);

        messageContainer = new FrameLayout(context);
        messageContainer.setBackgroundResource(R.drawable.chat_to_selector);
        addView(messageContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                , Gravity.RIGHT, 58, 8, 8, 8));

        playView = new ImageView(context);
        playView.setClickable(true);
        playView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                didPressedButton();
            }
        });
        playView.setImageResource(R.mipmap.play_w);
        messageContainer.addView(playView, LayoutHelper.createFrame(40, 40, Gravity.LEFT | Gravity.CENTER_VERTICAL, 8, 8, 8, 8));

        playTimeView = new TextView(context);
        playTimeView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
        playTimeView.setMaxLines(1);
        playTimeView.setEllipsize(TextUtils.TruncateAt.END);
        playTimeView.setSingleLine(true);
        playTimeView.setTextColor(0xffffffff);
        playTimeView.setGravity(Gravity.LEFT);
        messageContainer.addView(playTimeView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 64, 8, 64, 8));

        timeView = new TextView(context);
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        timeView.setMaxLines(1);
        timeView.setEllipsize(TextUtils.TruncateAt.END);
        timeView.setSingleLine(true);
        timeView.setTextColor(0x8affffff);
        timeView.setGravity(Gravity.CENTER);
        messageContainer.addView(timeView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.BOTTOM, 64, 36, 8, 8));
    }

    @Override
    protected void updatePlayProgress() {
        int duration = voiceTotalTime;
        if (VoicePlayController.getInstance().isPlaying(messageObject.messageOwner)) {
            duration = duration - messageObject.audioProgressSec;
        }
        String timeString = String.format("%02d:%02d", duration / 60, duration % 60);
        playTimeView.setText(timeString);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (messageObject != null) {
            if (VoicePlayController.getInstance().isPlaying(messageObject.messageOwner)) {
                VoicePlayController.getInstance().stopPlayVoice();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateButtonState(0);
    }

    @Override
    protected void updateButtonState(int state) {
        buttonState = state;
        int playResId = R.mipmap.play_w;
        if (buttonState == 0) {
            playResId = R.mipmap.play_w;
        } else if (buttonState == 1) {
            playResId = R.mipmap.pause_w;
        } else if (buttonState == 2) {
            playResId = R.drawable.download_b;
        } else if (buttonState == 3) {
            playResId = R.drawable.cancel_b;
        } else if (buttonState == 4) {
            playResId = R.drawable.cancel_b;
        }
        playView.setImageResource(playResId);
        updatePlayProgress();
    }

    @Override
    protected void tryCancelSend() {

    }

    @Override
    public void setValue(MessageObject message) {
        super.setValue(message);
        currentUser = IMMessagesController.getInstance().getUser(messageObject.messageOwner.getFrom());
        VoiceMessageBody voiceBody = (VoiceMessageBody) messageObject.messageOwner.getBody();
        voiceTotalTime = voiceBody.getLength();
        String time = MessageHelper.formatterDay.format(messageObject.messageOwner.getMsgTime());
        timeView.setText(time);

        updateButtonState(0);
    }

    @Override
    public void updateCellState(boolean isNext, boolean showAvatar) {
        if (isNext) {
            messageContainer.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                    , Gravity.RIGHT, 58, 2, 8, 2));
        } else {
            messageContainer.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                    , Gravity.RIGHT, 58, 4, 8, 4));
        }
        requestLayout();
    }

    @Override
    protected UserEntity getCurrUser() {
        return currentUser;
    }
}
