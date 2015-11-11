package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.db.entity.DiscoveryCollection;

import java.util.ArrayList;
import java.util.List;

public class AttachView extends FrameLayout {

    public interface AttachViewDelegate {
        void didPressedButton(int button);
    }

    private List<View> views = new ArrayList<>();

    private AttachViewDelegate delegate;

    public static class AttachButton extends FrameLayout {

        public final TextView textView;
        public final ImageView imageView;

        public AttachButton(Context context) {
            super(context);

            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(imageView, LayoutHelper.createFrame(48, 48, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 8, 0, 0));

            textView = new TextView(context);
            textView.setLines(1);
            textView.setSingleLine(true);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextColor(0xff757575);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 64, 0, 0));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(95), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100), MeasureSpec.EXACTLY));
        }

        public void setTextAndIcon(CharSequence text, int icon) {
            textView.setText(text);
            imageView.setBackgroundResource(icon);
        }
    }

    public AttachView(Context context) {
        super(context);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void bindData(int[] itemIcons, final CharSequence[] items) {
        views.clear();
        int size = itemIcons.length;
        for (int a = 0; a < size; a++) {
            AttachButton attachButton = new AttachButton(getContext());
            attachButton.setTextAndIcon(items[a], itemIcons[a]);
            addView(attachButton, LayoutHelper.createFrame(85, 90, Gravity.LEFT | Gravity.TOP));
            attachButton.setTag(a);
            views.add(attachButton);
            attachButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (delegate != null) {
                        delegate.didPressedButton((Integer) v.getTag());
                    }
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int count = views.size();
        int row = (count == 0) ? 1 : (int) Math.ceil((double) count / 4);
        super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(row * 105), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int diff = (width - AndroidUtilities.dp(85 * 4 + 20)) / 3;
        int size = views.size();
        View childTemp;
        int t = AndroidUtilities.dp(8);
        for (int a = 0; a < size; a++) {
            int y = AndroidUtilities.dp(t + 95 * (a / 4));
            int x = AndroidUtilities.dp(8) + (a % 4) * (AndroidUtilities.dp(85) + diff);
            childTemp = views.get(a);
            childTemp.layout(x, y, x + childTemp.getMeasuredWidth(), y + childTemp.getMeasuredHeight());
        }
    }

    public void setDelegate(AttachViewDelegate attachViewDelegate) {
        delegate = attachViewDelegate;
    }

}
