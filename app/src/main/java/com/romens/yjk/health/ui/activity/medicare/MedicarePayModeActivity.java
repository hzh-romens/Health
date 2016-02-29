package com.romens.yjk.health.ui.activity.medicare;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.DividerCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.pay.PayMode;
import com.romens.yjk.health.pay.PayAppManager;
import com.romens.yjk.health.pay.PayBaseActivity;
import com.romens.yjk.health.pay.PayModeEnum;
import com.romens.yjk.health.pay.PayParamsForYBHEB;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.H3HeaderCell;
import com.romens.yjk.health.ui.cells.PayInfoCell;
import com.romens.yjk.health.ui.cells.PayModeCell;
import com.romens.yjk.health.ui.components.ToastCell;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class MedicarePayModeActivity extends PayBaseActivity<PayParamsForYBHEB> {
    public static final String ARGUMENTS_KEY_NEED_REDIRECT = "key_need_redirect";
    private ProgressBar progressBar;
    private TextView emptyTextView;

    private ListAdapter adapter;
    private int selectedPayModeKey;
    private final SparseArray<PayMode> medicarePayModes = new SparseArray<>();

    private boolean needRedirect = false;
    private String billNo;
    private BigDecimal billAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        needRedirect = intent.getBooleanExtra(ARGUMENTS_KEY_NEED_REDIRECT, false);
        billNo = createDate("MMddHHmmss");
        billAmount = new BigDecimal(0.1);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    needFinish();
                }
            }
        });

        actionBar.setTitle("选择医保支付方式");
        actionBar.setBackButtonImage(R.drawable.ic_close_white_24dp);
        FrameLayout listContainer = new FrameLayout(this);
        content.addView(listContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ListView listView = new ListView(this);
        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setSelector(R.drawable.list_selector);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        listContainer.addView(progressBar, LayoutHelper.createFrame(48, 48, Gravity.CENTER));
        emptyTextView = new TextView(this);
        emptyTextView.setTextColor(0xff808080);

        emptyTextView.setTextSize(20);

        emptyTextView.setLineSpacing(AndroidUtilities.dp(4), 1.0f);
        emptyTextView.setGravity(Gravity.CENTER);
        listContainer.addView(emptyTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 16, 0, 16, 0));

        adapter = new ListAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == payActionRow) {
                    tryPayRequest();
                }
            }
        });
        initPayMode();
        updateAdapter();
    }

    @Override
    public void onBackPressed() {
        needFinish();
    }

    private void needFinish() {
        if (needRedirect) {

        } else {
            finish();
        }
    }

    private void tryPayRequest() {
        if (selectedPayModeKey == 0) {
            if (!PayAppManager.isSetupYBHEB(this)) {
                new AlertDialog.Builder(this)
                        .setTitle("医保支付")
                        .setMessage("检测手机没有安装 哈尔滨银行 所需的支付客户端,是否跳转到银行官方页面下载?")
                        .setPositiveButton("现在去下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                PayAppManager.needDownloadPayApp(MedicarePayModeActivity.this, medicarePayModes.get(0).mode);
                            }
                        }).setNegativeButton("取消", null)
                        .create().show();
                return;
            }
        }
        needSelectMedicareCardPay();
    }

    private void needSelectMedicareCardPay() {
        Intent intent = new Intent(MedicarePayModeActivity.this, MedicareCardListActivity.class);
        intent.putExtra(MedicareCardListActivity.ARGUMENTS_KEY_ONLY_SELECT, true);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String medicareCardNo = data.getStringExtra("MEDICARENO");
                tryPayRequest(medicareCardNo);
            }
        }
    }

    private void tryPayRequest(String medicareCardNo) {
        needShowProgress("正在请求支付,请稍候...");
        Map<String, Object> args = new HashMap<>();
        args.put("BILLNO", billNo);
        args.put("MEDICARECARD", medicareCardNo);

        String payMode = medicarePayModes.get(selectedPayModeKey).getPayModeDesc();
        args.put("PAYMODE", payMode);

        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetBillPayRequestParams", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(BindUserMedicareNoActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        needHideProgress();
                        String error;
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (response.has("ERROR")) {
                                error = response.get("ERROR").asText();
                            } else {
                                onPayRequestSuccess(response);
                                return;
                            }
                        } else {
                            error = "请求支付失败,请稍后重试!";
                        }
                        ToastCell.toast(MedicarePayModeActivity.this, error);
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    private void onPayRequestSuccess(JsonNode response) {
        String payMode = response.get("PayMode").asText();
        JsonNode payParamsNode = response.get("PayParams");

        PayParamsForYBHEB payParams = new PayParamsForYBHEB();
        Iterator<String> fieldNames = payParamsNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            payParams.put(fieldName, payParamsNode.get(fieldName).asText());
        }
        if (sendPayRequest(payParams)) {
            finish();
        }
    }

    private void initPayMode() {
        medicarePayModes.clear();
        medicarePayModes.put(0, new PayMode.Builder(0)
                .withIconResId(R.drawable.medicare_pay_haerbin)
                .withName("哈尔滨银行")
                .withDesc("支持使用哈尔滨银行账户进行医保支付")
                .withMode(PayModeEnum.YB_HEB).build());
        selectedPayModeKey = 0;
    }

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

        adapter.notifyDataSetChanged();
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

    @Override
    protected boolean sendPayRequest(PayParamsForYBHEB payParams) {
        ComponentName componentName = new ComponentName("com.yitong.hrb.people.android",
                "com.yitong.hrb.people.android.activity.GifViewActivity");
        Intent intent = new Intent();
        Bundle bundle = payParams.toBundle();
        intent.putExtra("bundle", bundle);
        intent.setComponent(componentName);
        startActivity(intent);
        return true;
    }

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
                    cell.setTextAndValue("订单编号", billNo, true);
                } else if (position == payAmountRow) {
                    cell.setValueTextColor(ResourcesConfig.priceFontColor);
                    cell.setTextAndValue("支付金额", ShoppingHelper.formatPrice(billAmount), false);
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
                int key = payModeStartRow - position;
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

    private String createDate(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(Calendar.getInstance().getTime());
    }
}
