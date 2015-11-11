package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by anlc on 2015/11/9.
 */
public class KeyAndViewCell extends LinearLayout {

    private TextView keyTextView;

    private MaterialEditText editText;
    private ImgAndValueCell radioViewMan;
    private ImgAndValueCell radioViewWomen;
    private ImageView rightImgView;
    private TextView rightTextView;

    private boolean flag = true;

    private boolean needDivider = false;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private static Paint paint;

    public KeyAndViewCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        keyTextView = new TextView(context);
        keyTextView.setSingleLine(true);
        keyTextView.setGravity(Gravity.CENTER_VERTICAL);
        keyTextView.setTextColor(getResources().getColor(R.color.theme_primary));
        keyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LayoutParams titleParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT);
        keyTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(0));
        keyTextView.setLayoutParams(titleParams);
        addView(keyTextView);

        editText = new MaterialEditText(context);
        editText.setHideUnderline(true);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        editText.setBaseColor(0xff212121);
        editText.setPrimaryColor(ResourcesConfig.textPrimary);
        editText.setMaxLines(1);
        editText.setLines(1);
        editText.setSingleLine(true);
        editText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setTypeface(Typeface.DEFAULT);
        LayoutParams editTextParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        editTextParams.setMargins(AndroidUtilities.dp(16), AndroidUtilities.dp(0), AndroidUtilities.dp(8), AndroidUtilities.dp(0));
        editText.setLayoutParams(editTextParams);
        addView(editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (onEditViewChangeListener != null) {
                        onEditViewChangeListener.editViewChange(s.toString());
                    }
                }
            }
        });

        radioViewMan = new ImgAndValueCell(context);
        radioViewMan.setData(R.drawable.check_choice, "男", false);
        radioViewMan.setLeftImgPadding(16, 16);
        radioViewMan.setFocusable(true);
        LayoutParams radioViewManParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT);
        radioViewManParams.gravity = Gravity.CENTER_VERTICAL;
        radioViewMan.setLayoutParams(radioViewManParams);
        addView(radioViewMan);
        radioViewWomen = new ImgAndValueCell(context);
        radioViewWomen.setFocusable(true);
        radioViewWomen.setLeftImgPadding(16, 16);
        radioViewWomen.setData(R.drawable.check_unchoice, "女", false);
        LayoutParams radioViewWomenParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT);
        radioViewWomenParams.gravity = Gravity.CENTER_VERTICAL;
        radioViewWomen.setLayoutParams(radioViewWomenParams);
        addView(radioViewWomen);

        rightImgView = new ImageView(context);
        rightImgView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        LayoutParams imgParams = new LayoutParams(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(40));
        imgParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        rightImgView.setLayoutParams(imgParams);
        addView(rightImgView);
        rightImgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRightImgViewClickListener != null) {
                    onRightImgViewClickListener.imgViewClick();
                }
            }
        });

        rightTextView = new TextView(context);
        rightTextView.setSingleLine(true);
        rightTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        rightTextView.setTextColor(getResources().getColor(R.color.theme_sub_title));
        rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LayoutParams rightTextViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        rightTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(0));
        rightTextView.setLayoutParams(rightTextViewParams);
        addView(rightTextView);
        hideView("");

        rightTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTextViewClickListener != null) {
                    onTextViewClickListener.textViewClick();
                }
            }
        });
    }

    public void hideView(String key) {
        keyTextView.setText(key);
        editText.setVisibility(GONE);
        radioViewWomen.setVisibility(GONE);
        radioViewMan.setVisibility(GONE);
        rightImgView.setVisibility(GONE);
        rightTextView.setVisibility(GONE);
    }

    public void setKeyAndRightText(String key, String rightText, boolean needDivider) {
        hideView(key);
        rightTextView.setVisibility(VISIBLE);
        rightTextView.setText(rightText);
        this.needDivider = needDivider;
        setWillNotDraw(!needDivider);
    }

    public void setKeyAndRightImg(String key, int rightImg, boolean needDivider) {
        hideView(key);
        LayoutParams imgParams = new LayoutParams(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(40));
        imgParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        rightImgView.setLayoutParams(imgParams);
        rightImgView.setVisibility(VISIBLE);
        rightImgView.setImageResource(rightImg);
        this.needDivider = needDivider;
        setWillNotDraw(!needDivider);
    }

    public void setKeyAndEditHint(String key, String editHint, boolean needDivider) {
        hideView(key);
        editText.setHint(editHint);
        editText.setVisibility(VISIBLE);
        this.needDivider = needDivider;
        setWillNotDraw(!needDivider);
    }

    public void setKeyAndLeftText(String key, String text, boolean needDivider) {
        hideView(key);
        rightTextView.setVisibility(VISIBLE);
        rightTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        rightTextView.setText(text);
        this.needDivider = needDivider;
        setWillNotDraw(!needDivider);
    }

    public void changeRadioState(boolean flag) {
        if (flag) {
            radioViewMan.setImgViewSource(R.drawable.check_choice);
            radioViewWomen.setImgViewSource(R.drawable.check_unchoice);
        } else {
            radioViewMan.setImgViewSource(R.drawable.check_unchoice);
            radioViewWomen.setImgViewSource(R.drawable.check_choice);
        }
    }

    public void setKeyAndRadio(String key, int radioSelectIndex, boolean needDivider) {
        hideView(key);
        radioViewMan.setVisibility(VISIBLE);
        radioViewWomen.setVisibility(VISIBLE);
        if (radioSelectIndex == 0) {
            flag = true;
            changeRadioState(flag);
        } else if (radioSelectIndex == 1) {
            flag = false;
            changeRadioState(false);
        }
        radioViewMan.setOnViewClickLinstener(new ImgAndValueCell.OnViewClickLinstener() {
            @Override
            public void click() {
                flag = true;
                changeRadioState(flag);
                if (onRadioViewClickListener != null) {
                    onRadioViewClickListener.radioSelect("男");
                }
            }
        });
        radioViewWomen.setOnViewClickLinstener(new ImgAndValueCell.OnViewClickLinstener() {
            @Override
            public void click() {
                flag = false;
                changeRadioState(false);
                if (onRadioViewClickListener != null) {
                    onRadioViewClickListener.radioSelect("女");
                }
            }
        });
        this.needDivider = needDivider;
        setWillNotDraw(!needDivider);
    }

    private String getRadioValue() {
        if (flag) {
            return "男";
        } else {
            return "女";
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(dividerLeftPadding, getHeight() - 1, getWidth() - dividerRightPadding, getHeight() - 1, paint);
        }
    }

    public void setDivider(boolean divider, int leftPadding, int rightPadding) {
        needDivider = divider;
        dividerLeftPadding = leftPadding;
        dividerRightPadding = rightPadding;
        setWillNotDraw(!divider);
    }

    private OnEditViewChangeListener onEditViewChangeListener;
    private OnRadioViewClickListener onRadioViewClickListener;
    private OnTextViewClickListener onTextViewClickListener;
    private OnRightImgViewClickListener onRightImgViewClickListener;

    public void setOnRightImgViewClickListener(OnRightImgViewClickListener onRightImgViewClickListener) {
        this.onRightImgViewClickListener = onRightImgViewClickListener;
    }

    public void setOnEditViewChangeListener(OnEditViewChangeListener onEditViewChangeListener) {
        this.onEditViewChangeListener = onEditViewChangeListener;
    }

    public void setOnRadioViewClickListener(OnRadioViewClickListener onRadioViewClickListener) {
        this.onRadioViewClickListener = onRadioViewClickListener;
    }

    public void setOnTextViewClickListener(OnTextViewClickListener onTextViewClickListener) {
        this.onTextViewClickListener = onTextViewClickListener;
    }

    public interface OnEditViewChangeListener {
        void editViewChange(String editResult);
    }

    public interface OnRadioViewClickListener {
        void radioSelect(String result);
    }

    public interface OnTextViewClickListener {
        void textViewClick();
    }

    public interface OnRightImgViewClickListener {
        void imgViewClick();
    }
}
