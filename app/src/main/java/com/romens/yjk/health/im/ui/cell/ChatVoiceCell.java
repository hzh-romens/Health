package com.romens.yjk.health.im.ui.cell;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.romens.yjk.health.im.VoicePlayController;

/**
 * Created by siery on 15/9/1.
 */
public abstract class ChatVoiceCell extends ChatCell {
    protected  int buttonState;
    public ChatVoiceCell(Context context) {
        super(context);
    }


    protected void didPressedButton() {
        if (buttonState == 0) {
            boolean result = VoicePlayController.getInstance().playVoice(messageObject.messageOwner, new VoicePlayController.VoicePlayCallback() {
                @Override
                public void onStarted(String messageId, int totalTime) {
                    if (TextUtils.equals(messageId, messageObject.getDialogId())) {
                        messageObject.audioProgress = 0;
                        messageObject.audioProgressSec = 0;
                        updatePlayProgress();
                    }
                }

                @Override
                public void onStopped(String messageId) {
                    messageObject.audioProgress = 0;
                    messageObject.audioProgressSec = 1;
                    updateButtonState(0);
                }

                @Override
                public void onProgress(String messageId, int time, int progress) {
                    messageObject.audioProgress = progress;
                    messageObject.audioProgressSec = time;
                    updatePlayProgress();
                }
            });
            if (result) {
                if (!messageObject.isOut()) {
                    try {
                        if (!messageObject.isUnread()) {
                            messageObject.setAcked(true);
                            // 告知对方已读这条消息
                            if (messageObject.messageOwner.getChatType() != EMMessage.ChatType.GroupChat) {
                                EMChatManager.getInstance().ackMessageRead(messageObject.messageOwner.getFrom(), messageObject.getDialogId());
                            }
                        }
                    } catch (Exception e) {
                        messageObject.setAcked(false);
                    }
                    if (!messageObject.messageOwner.isListened()) {
                        // 隐藏自己未播放这条语音消息的标志
                        EMChatManager.getInstance().setMessageListened(messageObject.messageOwner);
                    }
                }
                updateButtonState(1);
            }
        }else if (buttonState == 1) {
            VoicePlayController.getInstance().stopPlayVoice();
            updateButtonState(0);
        } else if (buttonState == 2) {
            downloadAudioIfNeed();
            updateButtonState(3);
        } else if (buttonState == 3) {
            //FileLoader.getInstance().cancelLoadFile(currentMessageObject.messageOwner.media.audio);
            updateButtonState(2);
        } else if (buttonState == 4) {
            if (messageObject.isOut() && messageObject.isSending()) {
                tryCancelSend();
            }
        }
    }

    protected void downloadAudioIfNeed() {
        if (messageObject.isSendError()) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    EMChatManager.getInstance().asyncFetchMessage(messageObject.messageOwner);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    if (chatDelegate != null) {
                        chatDelegate.needRefreshAdapter(ChatVoiceCell.this);
                    }
                }

            }.execute();
        }
    }

    protected abstract void updatePlayProgress();

    protected abstract void updateButtonState(int state);

    protected abstract void tryCancelSend();
}
