package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/1/25.
 */
public class MemberCardView extends FrameLayout {
    private CardView cardView;
    private TextView numberView;
    private int flag = 1;

    public MemberCardView(Context context) {
        super(context);
        FrameLayout container = new FrameLayout(context);
        container.setBackgroundColor(context.getResources().getColor(R.color.progress_bgc));
        final ImageView image = new ImageView(context);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setBackgroundResource(R.drawable.ic_card_bgc);
        final ImageView bgcImage = new ImageView(context);
        bgcImage.setScaleType(ImageView.ScaleType.FIT_XY);
        bgcImage.setImageResource(R.drawable.ic_card_face);
        cardView = new CardView(context);
        cardView.setCardElevation(4);
        cardView.setRadius(10f);
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
                    bgcImage.setImageResource(R.drawable.ic_card_opposite);
                    numberView.setVisibility(GONE);
                } else if (flag == 2) {
                    flag = 1;
                    bgcImage.setImageResource(R.drawable.ic_card_face);
                    numberView.setVisibility(VISIBLE);
                }
            }
        });
    }
}
