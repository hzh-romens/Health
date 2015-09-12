package com.romens.yjk.health.im.ui.cell;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.romens.android.AndroidUtilities;
import com.romens.yjk.health.db.entity.MessageObject;
import com.romens.yjk.health.db.entity.UserEntity;

/**
 * Created by siery on 15/8/30.
 */
public abstract class ChatCell extends FrameLayout {

    protected ChatDelegate chatDelegate;

    protected MessageObject messageObject;

    public ChatCell(Context context) {
        super(context);
    }

    public void setValue(MessageObject message) {
        messageObject = message;
    }

    protected abstract UserEntity getCurrUser();

    public abstract void updateCellState(boolean isNext, boolean showAvatar);

    public void setChatDelegate(ChatDelegate delegate) {
        chatDelegate = delegate;
    }

    public interface ChatDelegate {
        void onCellClick(ChatCell cell);

        void onCellLongClick(ChatCell cell);

        void onAvatarClick(UserEntity userEntity);

        void needRefreshAdapter(ChatCell cell);
    }

    public void tryHandleMessageForCreate(final MessageObject message) {
        final long start = System.currentTimeMillis();
        // 调用sdk发送异步发送方法
        EMChatManager.getInstance().sendMessage(message.messageOwner, new EMCallBack() {

            @Override
            public void onSuccess() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        setValue(message);
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        setValue(message);
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
            }

        });
    }

    public MessageObject getMessageObject() {
        return messageObject;
    }

    protected void registerAvatarClickListener(View avatarView) {
        if (avatarView != null) {
            avatarView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserEntity user = getCurrUser();
                    if (user != null && chatDelegate != null) {
                        chatDelegate.onAvatarClick(user);
                    }
                }
            });

        }
    }

    protected void registerCellClickListener(View cellView) {
        if (cellView != null) {
            cellView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chatDelegate != null) {
                        chatDelegate.onCellClick(ChatCell.this);
                    }
                }
            });

        }
    }

    protected void registerCellLongClickListener(View cellView) {
        if (cellView != null) {
            cellView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (chatDelegate != null) {
                        chatDelegate.onCellLongClick(ChatCell.this);
                        return true;
                    }
                    return false;
                }
            });

        }
    }
}
