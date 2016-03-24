package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/1/25.
 */
public class MemberCardView extends FrameLayout {
    private CardView cardView;
    private TextView numberView;
    private int flag = 1;
    private final CloudImageView bgcImage;

    public MemberCardView(Context context) {
        super(context);
        FrameLayout container = new FrameLayout(context);
        container.setBackgroundColor(context.getResources().getColor(R.color.progress_bgc));
        final ImageView image = new ImageView(context);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setBackgroundResource(R.drawable.ic_card_bgc);

        bgcImage = new CloudImageView(context);
        bgcImage.setScaleType(ImageView.ScaleType.FIT_XY);

        cardView = new CardView(context);
        cardView.setCardElevation(4);
        cardView.setRadius(10.0f);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            cardView.setUseCompatPadding(false);
        } else {
            cardView.setPreventCornerOverlap(false);
            cardView.setRadius(10.0f);
        }
        numberView = new TextView(context);
        numberView.setText("测试12345");
        numberView.setTextSize(18);
        numberView.setTextColor(Color.WHITE);
        cardView.addView(bgcImage, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        cardView.addView(numberView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT, 8, 8, 8, 8));
        container.addView(image, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 96, Gravity.TOP));
        container.addView(cardView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.NO_GRAVITY, 16, 16, 16, 16));
        addView(container, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 220));
        cardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1) {
                    flag = 2;
                    bgcImage.setImagePath(opposeUrl);
                    numberView.setVisibility(GONE);
                } else if (flag == 2) {
                    flag = 1;
                    bgcImage.setImagePath(faceUrl);
                    numberView.setVisibility(VISIBLE);
                }
            }
        });
    }

    public void setCardNumber(String idNumber) {
        numberView.setText(idNumber);
    }

    private String faceUrl, opposeUrl;

    public void setCarImage(String face, String oppose) {
        this.faceUrl = face;
        this.opposeUrl = oppose;
        if (faceUrl != null || !TextUtils.isEmpty(faceUrl)) {
            bgcImage.setImagePath(faceUrl);
        }
    }
}
