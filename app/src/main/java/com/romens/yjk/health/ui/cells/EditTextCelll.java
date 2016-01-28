package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/1/28.
 */
public class EditTextCelll extends FrameLayout {
    private EditText phoneNumber, phonePassword;
    private EditText cardNumber, cardPassword;
    private TextView send;
    private int mFlag;


    public void selectedMode(int flag) {
        this.mFlag = flag;
    }

    public EditTextCelll(Context context) {
        super(context);
        View view;
        if (mFlag == 1) {
            view = View.inflate(context, R.layout.phonenumber_layout, null);
            initPhoneView();
        } else if (mFlag == 2) {
            view = View.inflate(context, R.layout.cardnumber_layout, null);
            initCardView();
        }


    }

    private void initCardView() {
    }

    private void initPhoneView() {

    }
}
