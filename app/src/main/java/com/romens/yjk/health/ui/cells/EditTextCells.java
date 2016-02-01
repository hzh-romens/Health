package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/1/28.
 */
public class EditTextCells extends FrameLayout {
    public static EditText phoneNumber, phonePassword;
    private static EditText cardNumber, cardPassword;
    private TextView send;
    // private int mFlag;
    private buttonClickListener mListener;


//    public void selectedMode(int flag) {
//        this.mFlag = flag;
//    }

    public void setListener(buttonClickListener listener) {
        this.mListener = listener;
    }

    public EditTextCells(Context context, int flag) {
        super(context);
        View view = null;
        if (flag == 1) {
            view = View.inflate(context, R.layout.phonenumber_layout, null);
            initPhoneView(view);
        } else if (flag == 2) {
            view = View.inflate(context, R.layout.cardnumber_layout, null);
            initCardView(view);
        }
        addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    private void initCardView(View view) {
        cardNumber = (EditText) view.findViewById(R.id.cardNumber);
        cardPassword = (EditText) view.findViewById(R.id.cardPassword);
    }

    private void initPhoneView(View view) {
        phoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        phonePassword = (EditText) view.findViewById(R.id.phonePassword);
        send = (TextView) view.findViewById(R.id.send);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMessage();
            }
        });
    }

    public interface buttonClickListener {
        void sendMessage();
    }
}
