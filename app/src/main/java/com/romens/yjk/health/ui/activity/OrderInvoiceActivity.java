package com.romens.yjk.health.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.TipCell;

/**
 * @author Zhou Lisi
 * @create 16/3/1
 * @description 提交订单发票信息
 */
public class OrderInvoiceActivity extends DarkActionBarActivity {
    public static final String ARGUMENTS_KEY_INVOICE_NAME = "key_invoice_name";

    private MaterialEditText inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String invoiceName;
        if (intent.hasExtra(ARGUMENTS_KEY_INVOICE_NAME)) {
            invoiceName = intent.getStringExtra(ARGUMENTS_KEY_INVOICE_NAME);
        } else {
            invoiceName = "";
        }

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        content.setBackgroundColor(0xffffffff);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);
        actionBar.setTitle("发票信息");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    onBackPressed();
                }
            }
        });

        TipCell tipCell = new TipCell(this);
        tipCell.setValue("注意:发票开具仅支持个人发票");
        content.addView(tipCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        inputEditText = new MaterialEditText(this);
        inputEditText.setBaseColor(0xff212121);
        inputEditText.setPrimaryColor(ResourcesConfig.textPrimary);
        inputEditText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
        inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        inputEditText.setMaxLines(1);
        inputEditText.setSingleLine(true);
        inputEditText.setHint("输入发票抬头");
        inputEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        content.addView(inputEditText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 56, 16, 32, 16, 8));

        TextView bindBtn = new TextView(this);
        bindBtn.setBackgroundResource(R.drawable.btn_primary);
        bindBtn.setTextColor(0xffffffff);
        bindBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        bindBtn.setClickable(true);
        bindBtn.setGravity(Gravity.CENTER);
        AndroidUtilities.setMaterialTypeface(bindBtn);
        bindBtn.setText("保存发票信息");
        content.addView(bindBtn, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 16, 32, 16, 16));
        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySaveInvoice();
            }
        });

        inputEditText.setText(invoiceName);
    }

    @Override
    public void onResume() {
        super.onResume();
        inputEditText.setSelection(inputEditText.getText().length());
        AndroidUtilities.showKeyboard(inputEditText);
    }

    private void trySaveInvoice() {
        String text = inputEditText.getText().toString().trim();
        onSaveInvoice(text);
    }

    @Override
    public void onBackPressed() {
        onCancel();
    }

    private void onSaveInvoice(String invoiceName) {
        Intent data = new Intent();
        data.putExtra(ARGUMENTS_KEY_INVOICE_NAME, invoiceName);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
