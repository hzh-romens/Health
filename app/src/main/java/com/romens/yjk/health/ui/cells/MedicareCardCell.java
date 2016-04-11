package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.helper.ShoppingHelper;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class MedicareCardCell extends CardView {

    private CloudImageView avatarImageView;
    private AvatarDrawable avatarDrawable;

    private TextView nameTextView;
    private TextView descTextView;

    private TextView cardNoTextView;

    private Delegate delegate;

    public interface Delegate {
        void onCheckBalance();
    }

    public MedicareCardCell(Context context) {
        super(context);
        Resources resources = context.getResources();
        int medicareCard = resources.getColor(R.color.medicare_card);
        setCardBackgroundColor(medicareCard);
        setRadius(AndroidUtilities.dp(4));
        if (Build.VERSION.SDK_INT >= 21) {
            setElevation(0);
        }

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        addView(content, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        FrameLayout userContent = new FrameLayout(context);
        content.addView(userContent, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 64, 0, 16, 0, 16));
        avatarImageView = CloudImageView.create(context);
        avatarImageView.setRound(AndroidUtilities.dp(24));
        userContent.addView(avatarImageView, LayoutHelper.createFrame(48, 48, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 0, 0, 0));
        avatarDrawable = new AvatarDrawable();

        nameTextView = new TextView(context);
        nameTextView.setTextColor(0xff212121);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        nameTextView.setLines(1);
        nameTextView.setMaxLines(1);
        nameTextView.setSingleLine(true);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        userContent.addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 80, 10, 16, 0));

        descTextView = new TextView(context);
        descTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        descTextView.setLines(1);
        descTextView.setMaxLines(1);
        descTextView.setSingleLine(true);
        descTextView.setEllipsize(TextUtils.TruncateAt.END);
        descTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        descTextView.setTextColor(0xff212121);
        userContent.addView(descTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 80, 33, 16, 0));


        FrameLayout cardNoContent = new FrameLayout(context);
        cardNoContent.setBackgroundResource(R.color.medicare_card_dark);
        content.addView(cardNoContent, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 56, 0, 8, 0, 16));

        cardNoTextView = new TextView(context);
        cardNoTextView.setTextColor(0xff212121);
        cardNoTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        cardNoTextView.setLines(1);
        cardNoTextView.setMaxLines(1);
        cardNoTextView.setSingleLine(true);
        cardNoTextView.setEllipsize(TextUtils.TruncateAt.END);
        cardNoTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        cardNoContent.addView(cardNoTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 24, 0, 16, 0));

        TextView balanceBtn = new TextView(context);
        balanceBtn.setTextColor(0xff414141);
        balanceBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        balanceBtn.setLines(1);
        balanceBtn.setMaxLines(1);
        balanceBtn.setSingleLine(true);
        balanceBtn.setEllipsize(TextUtils.TruncateAt.END);
        balanceBtn.setGravity(Gravity.CENTER);
        balanceBtn.setText("查看余额");
        balanceBtn.setPadding(AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16), 0);
        balanceBtn.setBackgroundResource(R.drawable.list_selector);
        balanceBtn.setClickable(true);
        cardNoContent.addView(balanceBtn, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 40, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 16, 0, 16, 0));
        balanceBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delegate != null) {
                    delegate.onCheckBalance();
                }
            }
        });
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(176), View.MeasureSpec.EXACTLY));
    }

    public void setValue(String userName, String certNo, String cardNo) {

        avatarDrawable.setInfo(userName);
        avatarDrawable.setColor(getResources().getColor(R.color.medicare_card_avatar));
        avatarImageView.setPlaceholderImage(avatarDrawable);

        nameTextView.setText(userName);
//        StringBuilder certNoStr = new StringBuilder();
//        if (certNo != null && certNo.length() == 18) {
//            certNoStr.append(certNo.substring(0, 6));
//            certNoStr.append("********");
//            certNoStr.append(certNo.substring(14, 18));
//        } else {
//            certNoStr.append(certNo == null ? "" : certNo);
//        }
        String certNoText = ShoppingHelper.mixedString(certNo);
        descTextView.setText(certNoText);
        cardNoTextView.setText(cardNo);
    }
}
