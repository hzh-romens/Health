package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.ui.components.FlowCell;
import com.romens.yjk.health.ui.components.FlowLayout;
import com.romens.yjk.health.ui.components.FlowLayoutCallback;

import java.util.List;

/**
 * Created by siery on 15/9/14.
 */
public class ADDiseaseCell extends LinearLayout {
    private TextView titleView;
    private TextView subTitleView;
    private FlowLayout itemsLayout;

    public static class ADDisease {
        public final String id;
        public final String name;
        public final int type;

        public ADDisease(String _id, String _name, int _type) {
            this.id = _id;
            this.name = _name;
            this.type = _type;
        }
    }

    public ADDiseaseCell(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));
        titleView = new TextView(context);
        titleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        titleView.setTextColor(0xff616161);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        titleView.setSingleLine(true);
        titleView.setGravity(Gravity.CENTER);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(titleView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 8, 0, 8, 0));

        subTitleView = new TextView(context);
        subTitleView.setTextColor(0xff9e9e9e);
        subTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        subTitleView.setGravity(Gravity.CENTER);
        subTitleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(subTitleView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 8, 0, 8, 0));

        itemsLayout = new FlowLayout(context);
        itemsLayout.setHorizontalSpacing(AndroidUtilities.dp(4));
        itemsLayout.setVerticalSpacing(AndroidUtilities.dp(4));
        addView(itemsLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 8, 0, 0));
    }

    public void setValue(String title, String subTitle, final List<ADDisease> adDiseaseList) {
        titleView.setText(title);
        subTitleView.setText(subTitle);

        itemsLayout.setAdapter(new FlowLayoutCallback() {
            @Override
            public int getCount() {
                return adDiseaseList.size();
            }

            @Override
            public View getView(int position, ViewGroup container) {
                FlowCell cell = new FlowCell(container.getContext(), 0xff0f9d58);
                cell.setText(adDiseaseList.get(position).name);
                cell.setClickable(true);
                cell.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                return cell;
            }
        });
        itemsLayout.updateLayout();
        requestLayout();
    }
}
