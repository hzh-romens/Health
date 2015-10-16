package com.romens.yjk.health.im.ui.cell;

import android.content.Context;
import android.view.View;

import com.easemob.chat.EMMessage;
import com.easemob.chat.LocationMessageBody;
import com.romens.yjk.health.core.LocationHelper;

/**
 * Created by siery on 15/8/31.
 */
public abstract class ChatLocationMessageCell extends ChatCell {
    protected  ChatLocationDelegate chatLocationDelegate;
    public ChatLocationMessageCell(Context context) {
        super(context);
    }

    protected String createLocationUrl(EMMessage message, int width, int height) {

        LocationMessageBody locBody = (LocationMessageBody) message.getBody();
        double lat = locBody.getLatitude();
        double lon = locBody.getLongitude();
        String url = LocationHelper.createUrl(lon, lat, width, height);
        return url;
    }

    protected String getLocationAddress(EMMessage message){
        LocationMessageBody locBody = (LocationMessageBody) message.getBody();
        String locationAddress = locBody.getAddress();
        return locationAddress;
    }

    public void setChatLocationDelegate(ChatLocationDelegate delegate) {
        chatLocationDelegate = delegate;
    }

    protected void registerLocationClickListener(View locationView) {
        if (locationView != null) {
            locationView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chatLocationDelegate != null) {
                        chatLocationDelegate.onLocationClick(ChatLocationMessageCell.this);
                    }
                }
            });

        }
    }

    public interface ChatLocationDelegate {
        void onLocationClick(ChatLocationMessageCell cell);
    }
}
