package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/9/12.
 */
public class LoginCell extends FrameLayout {
    private LoginCellDelegate cellDelegate;

    public LoginCell(Context context) {
        super(context);
        Button loginBtn = new Button(context);
        loginBtn.setTextColor(getResources().getColor(R.color.btn_primary_text));
        loginBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        loginBtn.setText("开启健康之旅");
        loginBtn.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(8), AndroidUtilities.dp(16), AndroidUtilities.dp(8));
        loginBtn.setBackgroundResource(R.drawable.btn_primary_simple);
        addView(loginBtn, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 40, Gravity.CENTER));
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cellDelegate != null) {
                    cellDelegate.onLoginClick();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(96), MeasureSpec.EXACTLY));
    }

    public void setLoginCellDelegate(LoginCellDelegate delegate) {
        cellDelegate = delegate;
    }

    public interface LoginCellDelegate {
        void onLoginClick();
    }
}
