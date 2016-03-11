package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.primitives.Chars;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * @author Zhou Lisi
 * @create 16/3/11
 * @description
 */
public class TextInputCell extends LinearLayout {
    private TextView captionTextView;
    private MaterialEditText valueInputView;

    public TextInputCell(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        captionTextView = new TextView(context);
        captionTextView.setTextColor(ResourcesConfig.primaryColor);
        captionTextView.setGravity(Gravity.LEFT);
        captionTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        addView(captionTextView,
                LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 8, 16, 0));

        valueInputView = new MaterialEditText(context);
        valueInputView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        valueInputView.setBaseColor(0xff212121);
        valueInputView.setHelperTextColor(0xff8a8a8a);
        valueInputView.setHelperTextAlwaysShown(true);
        valueInputView.setPrimaryColor(ResourcesConfig.textPrimary);
        valueInputView.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
        valueInputView.setInputType(InputType.TYPE_CLASS_TEXT);
        valueInputView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        valueInputView.setMaxLines(1);
        valueInputView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        valueInputView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        addView(valueInputView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 4));
    }

    public void setValue(CharSequence caption, CharSequence value) {
        captionTextView.setText(caption);
        valueInputView.setText(value);
    }

    public void setValueHint(CharSequence hint) {
        valueInputView.setHint(hint);
    }

    public void setHelpText(CharSequence text) {
        valueInputView.setHelperText(text);
    }

    public String getValue() {
        return valueInputView.getText().toString();
    }

    public boolean isEmpty() {
        String value = valueInputView.getText().toString();
        value = value.trim().replace(" ", "");
        return TextUtils.isEmpty(value);
    }

    public MaterialEditText getInputView() {
        return valueInputView;
    }

    public void selectValue() {
        valueInputView.selectAll();
    }
}
