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
    protected int selectedPayModeKey;
    private final SparseArray<PayMode> medicarePayModes = new SparseArray<>();

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

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ListAdapter(this);
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

    private void updateAdapter() {
        rowCount = 0;
        billSection = rowCount++;
        billNoRow = rowCount++;
        payAmountRow = rowCount++;

        dividerRow = rowCount++;
        payModeSection = rowCount++;
        payModeStartRow = rowCount;
        rowCount += medicarePayModes.size();
        payModeEndRow = rowCount - 1;
        payModeDividerRow = rowCount++;
        payActionRow = rowCount++;

        listAdapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int billSection;
    private int billNoRow;
    private int payAmountRow;

    private int dividerRow;
    private int payModeSection;
    private int payModeStartRow;
    private int payModeEndRow;
    private int payModeDividerRow;
    private int payActionRow;

    class ListAdapter extends BaseAdapter {

        private Context adapterContext;

        public ListAdapter(Context context) {
            adapterContext = context;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            if (position >= payModeStartRow && position <= payModeEndRow) {
                return true;
            } else if (position == payActionRow) {
                return true;
            }
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == billNoRow || position == payAmountRow) {
                return 1;
            } else if (position == billSection || position == payModeSection) {
                return 2;
            } else if (position >= payModeStartRow && position <= payModeEndRow) {
                return 3;
            } else if (position == payModeDividerRow) {
                return 4;
            } else if (position == payActionRow) {
                return 5;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 6;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int viewType = getItemViewType(position);
            if (viewType == 0) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(adapterContext);
                }
            } else if (viewType == 1) {
                if (convertView == null) {
                    convertView = new PayInfoCell(adapterContext);
                }
                PayInfoCell cell = (PayInfoCell) convertView;
                cell.setTextSize(16);
                cell.setValueTextSize(18);
                cell.setTextColor(0xff212121);
                if (position == billNoRow) {
                    cell.setValueTextColor(0xff757575);
                    cell.setTextAndValue("订单编号", orderNo, true);
                } else if (position == payAmountRow) {
                    cell.setValueTextColor(ResourcesConfig.priceFontColor);
                    cell.setTextAndValue("支付金额", ShoppingHelper.formatPrice(orderPayAmount), false);
                }
            } else if (viewType == 2) {
                if (convertView == null) {
                    convertView = new H3HeaderCell(adapterContext);
                }
                H3HeaderCell cell = (H3HeaderCell) convertView;
                cell.setTextSize(16);
                cell.setTextColor(ResourcesConfig.textPrimary);
                if (position == billSection) {
                    cell.setText("待支付订单");
                } else if (position == payModeSection) {
                    cell.setText("支付方式");
                }
            } else if (viewType == 3) {
                if (convertView == null) {
                    convertView = new PayModeCell(adapterContext);
                }

                PayModeCell cell = (PayModeCell) convertView;
                int key = position - payModeStartRow;
                PayMode mode = medicarePayModes.get(key);
                cell.setValue(mode.iconResId, mode.name, mode.desc, key == selectedPayModeKey, position != payModeEndRow);
            } else if (viewType == 4) {
                if (convertView == null) {
                    convertView = new DividerCell(adapterContext);
                }
            } else if (viewType == 5) {
                if (convertView == null) {
                    convertView = new ActionCell(adapterContext);
                }

                ActionCell cell = (ActionCell) convertView;
                if (position == payActionRow) {
                    cell.setValue("支付");
                }
            }
            return convertView;
        }
    }
}
