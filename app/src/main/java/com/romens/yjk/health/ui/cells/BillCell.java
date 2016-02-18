package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.components.SwitchButton;

/**
 * Created by HZH on 2016/2/2.
 */
public class BillCell extends FrameLayout {
    private SwitchButton switchBtn;
    private EditText editText;
    private LinearLayout linearLayout;
    private SwitchChangeListener mListener;
    private InputMethodManager manager;

    public BillCell(final Context context) {
        super(context);
        manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = View.inflate(context, R.layout.list_item_bill, null);
        switchBtn = (SwitchButton) view.findViewById(R.id.btn_switch);
        editText = (EditText) view.findViewById(R.id.edit_bill);
        linearLayout = (LinearLayout) view.findViewById(R.id.linelayout);
        addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView != null && mListener != null) {
                    mListener.changeListener(buttonView.isChecked());
                }
                if (isChecked) {
                    editText.setFocusableInTouchMode(true);
                    editText.setFocusable(true);
                    editText.requestFocus();
                    linearLayout.setVisibility(VISIBLE);
                    //弹出软键盘
                    showInputManager();
                } else {
                    linearLayout.setVisibility(GONE);
                    editText.setFocusable(false);
                    editText.setFocusableInTouchMode(false);
                    hideInputManager();
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditListener.textChangeListenr(editText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setChangeListener(SwitchChangeListener listener) {
        this.mListener = listener;
    }

    private EditTextChangeListener mEditListener;

    public void setEditTextChangeListener(EditTextChangeListener listener) {
        this.mEditListener = listener;
    }

    public interface SwitchChangeListener {
        void changeListener(boolean isChecked);
    }

    public interface EditTextChangeListener {
        void textChangeListenr(String value);
    }

    //手动显示键盘
    public void showInputManager() {
        manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //手动隐藏键盘
    public void hideInputManager() {
        manager.hideSoftInputFromWindow(getWindowToken(), 0);
    }


}
