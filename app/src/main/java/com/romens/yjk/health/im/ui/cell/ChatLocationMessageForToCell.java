package com.romens.yjk.health.im.ui.cell;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.IMMessagesController;
import com.romens.yjk.health.db.entity.MessageObject;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.helper.MessageHelper;

/**
 * Created by siery on 15/8/31.
 */
public class ChatLocationMessageForToCell extends ChatLocationMessageCell {

    private FrameLayout messageContainer;

    private BackupImageView locationView;
    private TextView messageView;
    private TextView timeView;

    private TextView stateView;


    private UserEntity currentUser;

    public ChatLocationMessageForToCell(Context context) {
        super(context);

        messageContainer = new FrameLayout(context);
        messageContainer.setBackgroundResource(R.drawable.chat_to_selector);
        addView(messageContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                , Gravity.RIGHT, 58, 8, 8, 8));

        locationView = new BackupImageView(context);
        locationView.setRoundRadius(AndroidUtilities.dp(4));
        locationView.setClickable(true);
        registerLocationClickListener(locationView);
        messageContainer.addView(locationView, LayoutHelper.createFrame(88, 88, Gravity.LEFT | Gravity.TOP, 4, 4, 8, 4));

        messageView = new TextView(context);
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        messageView.setTextColor(0xffffffff);
        messageView.setLines(3);
        messageView.setMaxLines(3);
        messageView.setEllipsize(TextUtils.TruncateAt.END);
        messageContainer.addView(messageView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 106, 8, 8, 32));

        LinearLayout bottom = new LinearLayout(context);
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        messageContainer.addView(bottom, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT, 106, 8, 8, 8));
        timeView = new TextView(context);
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        timeView.setMaxLines(1);
        timeView.setEllipsize(TextUtils.TruncateAt.END);
        timeView.setSingleLine(true);
        timeView.setTextColor(0x8affffff);
        timeView.setGravity(Gravity.CENTER);
        bottom.addView(timeView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        stateView = new TextView(context);
        stateView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        stateView.setMaxLines(1);
        stateView.setEllipsize(TextUtils.TruncateAt.END);
        stateView.setSingleLine(true);
        stateView.setTextColor(0x8affffff);
        stateView.setGravity(Gravity.CENTER);
        bottom.addView(stateView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 8, 0, 0, 0));
    }

    @Override
    public void setValue(MessageObject message) {
        super.setValue(message);
        currentUser = IMMessagesController.getInstance().getUser(messageObject.messageOwner.getFrom());

        String locationUrl = createLocationUrl(messageObject.messageOwner, 100, 100);
        locationView.setImage(locationUrl, "100_100", null);

        String locationAddress = getLocationAddress(messageObject.messageOwner);
        messageView.setText(locationAddress);

        String time = MessageHelper.formatterDay.format(messageObject.messageOwner.getMsgTime());
        timeView.setText(time);
        stateView.setText(messageObject.getStateDesc());
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
