package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * @author Zhou Lisi
 * @create 16/2/27
 * @description
 */
public class ShoppingCartUnLoginCell extends LinearLayout {

    public interface Delegate {
        void onNeedLogin();
    }

    private Delegate delegate;

    public ShoppingCartUnLoginCell(Context context) {
        super(context);
        init(context);
    }

    public ShoppingCartUnLoginCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShoppingCartUnLoginCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        TextView helperTextView = new TextView(context);
        helperTextView.setTextColor(0xff212121);
        helperTextView.setTextSize(20);
        helperTextView.setLineSpacing(AndroidUtilities.dp(4), 1.0f);
        helperTextView.setGravity(Gravity.CENTER);
        StringBuilder helperText = new StringBuilder();
        helperText.append("您可以在登录后同步购物车中的商品");
        helperTextView.setText(helperText);
        addView(helperTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 32, 96, 32, 16));


        TextView loginView = new TextView(context);
        loginView.setBackgroundResource(R.drawable.btn_primary);
        loginView.setTextColor(0xffffffff);
        loginView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        loginView.setLines(1);
        loginView.setMaxLines(1);
        loginView.setEllipsize(TextUtils.TruncateAt.END);
        loginView.setGravity(Gravity.CENTER);
        AndroidUtilities.setMaterialTypeface(loginView);
        loginView.setClickable(true);
        loginView.setText("登录");
        addView(loginView, LayoutHelper.createLinear(64, 40, 32, 32, 32, 8));
        loginView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delegate != null) {
                    delegate.onNeedLogin();
                }
            }
        });
    }

    public void setDelegate(Delegate d) {
        delegate = d;
    }
}
