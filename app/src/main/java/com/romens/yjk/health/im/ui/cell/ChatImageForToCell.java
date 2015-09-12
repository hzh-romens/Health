package com.romens.yjk.health.im.ui.cell;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.FrameLayoutFixed;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.db.IMMessagesController;
import com.romens.yjk.health.db.entity.MessageObject;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.helper.MessageHelper;
import com.romens.yjk.health.ui.components.RoundImageView;

/**
 * Created by siery on 15/8/30.
 */
public class ChatImageForToCell extends ChatImageCell {

    private FrameLayout messageContainer;
    private RoundImageView messageView;
    private TextView timeView;

    private UserEntity currentUser;

    public ChatImageForToCell(Context context) {
        super(context);

        messageContainer = new FrameLayoutFixed(context);
        messageContainer.setMinimumWidth(AndroidUtilities.dp(64));
        messageContainer.setMinimumHeight(AndroidUtilities.dp(64));
        addView(messageContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                , Gravity.RIGHT, 58, 8, 8, 8));

        messageView = new RoundImageView(context);
        messageView.setBorderRadius(8);
        messageView.setEnabled(true);
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
        String time = MessageHelper.formatterDay.format(messageObject.messageOwner.getMsgTime());
        timeView.setText(time);
        handleMessage(messageObject);
        invalidate();
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
