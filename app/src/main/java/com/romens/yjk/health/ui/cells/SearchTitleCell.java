package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/10/27.
 */
public class SearchTitleCell extends LinearLayout implements View.OnClickListener {

    private ImageView backImageView;
    private EditText searchEditText;
    private ImageView rightImageView;
    private boolean needDivider;
    private Paint paint;

    private onViewClickLinstener onViewClickLinstener;

    public void setOnViewClickLinstener(SearchTitleCell.onViewClickLinstener onViewClickLinstener) {
        this.onViewClickLinstener = onViewClickLinstener;
    }

    public interface onViewClickLinstener {
        void onBackViewClick();

        void onEditViewChange(EditText view);

        void onEditViewClick();

        void onRightViewClick();
    }

    public SearchTitleCell(final Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        backImageView = new ImageView(context);
        backImageView.setScaleType(ImageView.ScaleType.CENTER);
        backImageView.setImageResource(R.drawable.ic_ab_back);
        backImageView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(8), AndroidUtilities.dp(16), AndroidUtilities.dp(8));
        LayoutParams backImgParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT);
        backImgParams.gravity = Gravity.CENTER_VERTICAL;
        backImageView.setLayoutParams(backImgParams);
        addView(backImageView);
        backImageView.setOnClickListener(this);

        searchEditText = new EditText(context);
        searchEditText.setBackgroundColor(Color.TRANSPARENT);
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        searchEditText.setSingleLine(true);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.WHITE);
        searchEditText.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams editViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        editViewParams.weight = 1;
        editViewParams.gravity = Gravity.CENTER_VERTICAL;
        addView(searchEditText, editViewParams);
        searchEditText.setOnClickListener(this);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onViewClickLinstener != null) {
                    onViewClickLinstener.onEditViewChange(searchEditText);
                }
            }
        });

        rightImageView = new ImageView(context);
        rightImageView.setScaleType(ImageView.ScaleType.CENTER);
        rightImageView.setVisibility(GONE);
        LayoutParams rightImgParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT);
        rightImageView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(16), AndroidUtilities.dp(8));
        rightImgParams.gravity = Gravity.CENTER_VERTICAL;
        rightImageView.setLayoutParams(rightImgParams);
        addView(rightImageView);
        rightImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onViewClickLinstener != null) {
            if (v == backImageView) {
                onViewClickLinstener.onBackViewClick();
            } else if (v == rightImageView) {
                onViewClickLinstener.onRightViewClick();
            } else if (v == searchEditText) {
                onViewClickLinstener.onEditViewClick();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void setBackImage(int imageResource) {
        backImageView.setImageResource(imageResource);
    }

    public void setHintTextAndRightImg(String hintText, int rightImageResource, boolean needDivider) {
        this.needDivider = needDivider;
        searchEditText.setHint(hintText);
        if (rightImageResource != 0) {
            rightImageView.setVisibility(VISIBLE);
            rightImageView.setImageResource(rightImageResource);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(56), getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }
}
