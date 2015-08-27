package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Components.SimpleTextView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;


public class SendLocationCell extends FrameLayout {

    private SimpleTextView accurateTextView;
    private SimpleTextView titleTextView;

    public SendLocationCell(Context context) {
        super(context);

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.attach_location);
        addView(imageView, LayoutHelper.createFrame(50, 50, Gravity.CENTER_VERTICAL | Gravity.LEFT, 16, 0, 0, 0));

        titleTextView = new SimpleTextView(context);
        titleTextView.setTextSize(16);
        titleTextView.setTextColor(ResourcesConfig.primaryColor);
        titleTextView.setGravity(Gravity.LEFT);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(titleTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, Gravity.TOP | (Gravity.LEFT), 73, 12, 16, 0));

        accurateTextView = new SimpleTextView(context);
        accurateTextView.setTextSize(14);
        accurateTextView.setTextColor(0xff999999);
        accurateTextView.setGravity(Gravity.LEFT);
        addView(accurateTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, Gravity.TOP | (Gravity.LEFT), 73, 37, 16, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66), MeasureSpec.EXACTLY));
    }

    public void setText(String title, String text) {
        titleTextView.setText(title);
        accurateTextView.setText(text);
    }
}
