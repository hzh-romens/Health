package com.romens.yjk.health.ui.activity.medicare;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.cells.DividerCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.pay.PayAppManager;
import com.romens.yjk.health.pay.PayMode;
import com.romens.yjk.health.pay.PayModeEnum;
import com.romens.yjk.health.pay.PayParamsForYBHEB;
import com.romens.yjk.health.pay.PayPrepareBaseActivity;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.H3HeaderCell;
import com.romens.yjk.health.ui.cells.PayInfoCell;
import com.romens.yjk.health.ui.cells.PayModeCell;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public abstract class MedicarePayBaseActivity extends PayPrepareBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("选择医保支付方式");
        actionBar.setBackButtonImage(R.drawable.ic_close_white_24dp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == payActionRow) {
                    tryPayRequest();
                } else if (position >= payModeStartRow && position <= payModeEndRow) {
                    selectedPayModeKey = position - payModeStartRow;
                    updateAdapter();
                }
            }
        });
        onInitPayMode(medicarePayModes);
        updateAdapter();
    }

    @Override
    protected void needFinish() {
        finish();
    }

    protected void tryPayRequest() {
        if (selectedPayModeKey == 0) {
            if (!PayAppManager.isSetupYBHEB(this)) {
                new AlertDialog.Builder(this)
                        .setTitle("医保支付")
                        .setMessage("检测手机没有安装 哈尔滨银行 所需的支付客户端,是否跳转到银行官方页面下载?")
                        .setPositiveButton("现在去下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                PayAppManager.needDownloadPayApp(MedicarePayBaseActivity.this, medicarePayModes.get(0).mode);
                            }
                        }).setNegativeButton("取消", null)
                        .create().show();
                return;
            }
        }
        needSelectMedicareCardPay();
    }

    private void needSelectMedicareCardPay() {
        Intent intent = new Intent(MedicarePayBaseActivity.this, MedicareCardListActivity.class);
        intent.putExtra(MedicareCardListActivity.ARGUMENTS_KEY_ONLY_SELECT, true);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String medicareCardNo = data.getStringExtra("MEDICARENO");
                trySendPayPrepareRequest(medicareCardNo);
            }
        }
    }

    private void trySendPayPrepareRequest(String medicareCardNo) {
        Map<String, String> args = new HashMap<>();
        args.put("BILLNO", orderNo);
        args.put("MEDICARECARD", medicareCardNo);

        String payMode = medicarePayModes.get(selectedPayModeKey).getPayModeDesc();
        args.put("PAYMODE", payMode);
        doPayPrepareRequest(args);
    }

    protected abstract void onInitPayMode(SparseArray<PayMode> payModes);
}
