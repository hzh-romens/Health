package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.gc.materialdesign.views.Switch;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/2/2.
 */
public class BillCell extends FrameLayout {
    private Switch switchBtn;
    private EditText editText;
    private SwitchChangeListener mListener;

    public BillCell(final Context context) {
        super(context);
        View view = View.inflate(context, R.layout.list_item_bill, null);
        switchBtn = (Switch) view.findViewById(R.id.btn_switch);
        editText = (EditText) view.findViewById(R.id.edit_bill);
        addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switchBtn.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch view, boolean check) {
                if (view != null && mListener != null) {
                    mListener.changeListener(view.isCheck());
                }
                if (check) {
                    editText.setFocusableInTouchMode(true);
                    editText.setFocusable(true);
                    editText.requestFocus();
                    //弹出软键盘
                } else {
                    editText.setFocusable(false);
                    editText.setFocusableInTouchMode(false);
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
}
