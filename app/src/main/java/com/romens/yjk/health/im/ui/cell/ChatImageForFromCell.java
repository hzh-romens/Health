package com.romens.yjk.health.im.ui.cell;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.db.IMMessagesController;
import com.romens.yjk.health.db.entity.MessageObject;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.helper.MessageHelper;
import com.romens.yjk.health.ui.components.RoundImageView;

/**
 * Created by siery on 15/8/30.
 */
public class ChatImageForFromCell extends ChatImageCell {

    private BackupImageView iconView;
    private TextView nameView;
    private FrameLayout messageContainer;
    private RoundImageView messageView;
    private TextView timeView;
    private UserEntity currentUser;

    public ChatImageForFromCell(Context context) {
        super(context);

        iconView = new BackupImageView(context);
        iconView.setRoundRadius(AndroidUtilities.dp(21));
        iconView.setFocusable(true);
        iconView.setClickable(true);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatDelegate != null) {
                    chatDelegate.onAvatarClick(currentUser);
                }
            }
        });
        addView(iconView, LayoutHelper.createFrame(42, 42, Gravity.LEFT | Gravity.TOP, 8, 8, 8, 4));

        nameView = new TextView(context);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameView.setMaxLines(1);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setSingleLine(true);
        nameView.setTextColor(0x60000000);
        nameView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        addView(nameView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.LEFT | Gravity.TOP, 62, 8, 58, 4));

        messageContainer = new FrameLayout(context);
        messageContainer.setMinimumWidth(AndroidUtilities.dp(64));
        messageContainer.setMinimumHeight(AndroidUtilities.dp(64));
        addView(messageContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                , Gravity.LEFT | Gravity.TOP, 58, 32, 58, 4));

        messageView = new RoundImageView(context);
        messageView.setBorderRadius(8);
        messageView.setClickable(true);
        registerImageClickListener(messageView);
        registerImageLongClickListener(messageView);
        messageContainer.addView(messageView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        timeView = new TextView(context);
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        timeView.setMaxLines(1);
        timeView.setEllipsize(TextUtils.TruncateAt.END);
        timeView.setSingleLine(true);
        timeView.setTextColor(0x8affffff);
        timeView.setBackgroundColor(0x80000000);
        timeView.setGravity(Gravity.CENTER);
        timeView.setPadding(AndroidUtilities.dp(4), AndroidUtilities.dp(2), AndroidUtilities.dp(4), AndroidUtilities.dp(2));
        messageContainer.addView(timeView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT, 8, 8, 8, 8));

    }

    @Override
    protected void changeMessageState(int state) {

    }

    @Override
    protected void setImageViewBitmap(Bitmap bitmap) {
        super.setImageViewBitmap(bitmap);
        messageView.setImageBitmap(bitmap);
    }

    @Override
    public void setValue(MessageObject message) {
        super.setValue(message);
        setImageViewBitmap(null);
        currentUser = IMMessagesController.getInstance().getUser(messageObject.messageOwner.getFrom());

        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setInfo(currentUser.getId(),currentUser.getName());
        iconView.setImage(currentUser.getAvatar(), "42_42", avatarDrawable);

        int nameTextColor = AvatarDrawable.getColorForId(currentUser.getId());
        nameView.setTextColor(nameTextColor);
        nameView.setText(currentUser.getName());

        String time = MessageHelper.formatterDay.format(messageObject.messageOwner.getMsgTime());
        timeView.setText(time);

        handleMessage(messageObject);
        invalidate();
    }

    @Override
    protected UserEntity getCurrUser() {
        return currentUser;
    }

    @Override
    public void updateCellState(boolean isNext, boolean showAvatar) {

        if (isNext) {
            if (showAvatar) {
                iconView.setVisibility(View.INVISIBLE);
                nameView.setVisibility(View.GONE);
                messageContainer.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                        , Gravity.LEFT | Gravity.TOP, 58, 8, 58, 8));
            } else {
                iconView.setVisibility(View.GONE);
                nameView.setVisibility(View.GONE);
                messageContainer.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                        , Gravity.LEFT | Gravity.TOP, 8, 8, 58, 8));
            }
        } else {
            if (showAvatar) {
                iconView.setVisibility(View.VISIBLE);
                nameView.setVisibility(View.VISIBLE);
                messageContainer.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                        , Gravity.LEFT | Gravity.TOP, 58, 32, 58, 8));
            } else {
                iconView.setVisibility(View.GONE);
                nameView.setVisibility(View.GONE);
                messageContainer.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                        , Gravity.LEFT | Gravity.TOP, 8, 8, 58, 8));
            }
        }
        requestLayout();
    }


}
