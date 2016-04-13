package com.romens.yjk.health.hyrmtt.pay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.fasterxml.jackson.databind.JsonNode;
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
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.activity.medicare.BindUserMedicareNoActivity;
import com.romens.yjk.health.ui.cells.PayInfoCell;
import com.romens.yjk.health.ui.cells.TipCell;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/3/11
 * @description
 */
public class BalanceInfoActivity extends DarkActionBarActivity {
    public static final String ARGUMENTS_KEY_PAY_AMOUNT = "YJK_PAY_AMOUNT";
    public static final String ARGUMENTS_KEY_MEDICARE_USER = "YJK_MEDICARE_USER";
    public static final String ARGUMENTS_KEY_MEDICARE_CARDNO = "YJK_MEDICARE_CARDNO";

    private ListView listView;
    private ListAdapter listAdapter;

    private boolean fromCheckBalance = false;
    private BigDecimal payAmount = BigDecimal.ZERO;
    private String userName;
    private String medicareCardNo;
    private BigDecimal medicareBalance = BigDecimal.ZERO;
    private CheckStatus checkStatus = CheckStatus.PROCESSING;

    @Override
    protected String getActivityName() {
        return "医保余额查询";
    }

    private enum CheckStatus {
        PROCESSING, COMPLETED, ERROR
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(ARGUMENTS_KEY_MEDICARE_CARDNO)) {
            fromCheckBalance = true;
            double amount = intent.getDoubleExtra(ARGUMENTS_KEY_PAY_AMOUNT, 0);
            payAmount = new BigDecimal(amount);
            userName = intent.getStringExtra(ARGUMENTS_KEY_MEDICARE_USER);
            medicareCardNo = intent.getStringExtra(ARGUMENTS_KEY_MEDICARE_CARDNO);
        } else {
            fromCheckBalance = false;
        }
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);
        actionBar.setTitle("余额查询");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_white_24dp);
        FrameLayout listContainer = new FrameLayout(this);
        content.addView(listContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(this);
        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setSelector(R.drawable.list_selector);
        listAdapter = new ListAdapter(this);
        listView.setAdapter(listAdapter);
        updateAdapter();
        if (!fromCheckBalance) {
            processBalanceResponse(intent);
        } else {
            trySendMedicareCardBalanceRequest();
        }
    }

    private void trySendMedicareCardBalanceRequest() {
        needShowProgress("正在请求余额查询...");
        Map<String, String> args = new HashMap<>();
        args.put("APPTYPE", "ANDROID");
        args.put("MEDICARECARD", medicareCardNo);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetMedicareCardBalance", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(BindUserMedicareNoActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        needHideProgress();
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (!response.has("ERROR")) {
                                sendMedicareCardBalanceRequest(response);
                                return;
                            }
                        }
                        checkStatus = CheckStatus.ERROR;
                        updateAdapter();
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    private void sendMedicareCardBalanceRequest(JsonNode response) {
        ComponentName componentName = new ComponentName("com.yitong.hrb.people.android",
                "com.yitong.hrb.people.android.activity.GifViewActivity");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("cardNo", response.get("cardNo").asText());
        bundle.putString("O2TRSN", response.get("O2TRSN").asText());
        bundle.putString("certNo", response.get("certNo").asText());
        bundle.putString("transferFlowNo", response.get("transferFlowNo").asText());
        bundle.putString("hrbbType", response.get("hrbbType").asText());
        String packageName = MyApplication.applicationContext.getPackageName();
        bundle.putString("packageName", packageName);
        bundle.putString("activityPath", packageName + ".pay.BalanceInfoActivity");
        bundle.putString("appName", MyApplication.applicationContext.getString(R.string.app_name));
        intent.putExtra("queryResult", bundle);

        Bundle extBundle = new Bundle();
        extBundle.putDouble(ARGUMENTS_KEY_PAY_AMOUNT, payAmount.doubleValue());
        extBundle.putString(ARGUMENTS_KEY_MEDICARE_USER, userName);
        extBundle.putString(ARGUMENTS_KEY_MEDICARE_CARDNO, medicareCardNo);
        intent.putExtra("extBundle", extBundle);


        intent.setComponent(componentName);
        startActivity(intent);
        updateAdapter();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processBalanceResponse(intent);
    }

    private void processBalanceResponse(Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundle");
        String status = bundle.getString("status");
        if (TextUtils.equals("1", status)) {
            checkStatus = CheckStatus.COMPLETED;
            String balance = bundle.getString("balance", "0");
            medicareBalance = new BigDecimal(balance);
        } else {
            checkStatus = CheckStatus.ERROR;
        }
        updateAdapter();
    }

    protected void updateAdapter() {
        rowCount = 0;
        if (fromCheckBalance) {
            if (payAmount.compareTo(BigDecimal.ZERO) > 0) {
                tipRow = rowCount++;
            } else {
                tipRow = -1;
            }

            userNameRow = rowCount++;
            medicareCardNoRow = rowCount++;
        } else {
            tipRow = -1;
            userNameRow = -1;
            userNameRow = -1;
            medicareCardNoRow = -1;
        }
        if (checkStatus == CheckStatus.PROCESSING || checkStatus == CheckStatus.ERROR) {
            medicareCardStatuesRow = rowCount++;
            medicareCardBalanceRow = -1;
        } else {
            medicareCardBalanceRow = rowCount++;
            medicareCardStatuesRow = rowCount++;
        }

        listAdapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int tipRow;
    private int userNameRow;
    private int medicareCardNoRow;
    private int medicareCardBalanceRow;
    private int medicareCardStatuesRow;

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
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == tipRow) {
                return 1;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int viewType = getItemViewType(position);
            if (viewType == 1) {
                if (convertView == null) {
                    convertView = new TipCell(adapterContext);
                }
                TipCell cell = (TipCell) convertView;
                SpannableStringBuilder tip = new SpannableStringBuilder();
                tip.append("本次交易的待支付金额为");
                tip.append(ShoppingHelper.formatPrice(payAmount));
                cell.setValue(tip);
            } else if (viewType == 0) {
                if (convertView == null) {
                    convertView = new PayInfoCell(adapterContext);
                }
                PayInfoCell cell = (PayInfoCell) convertView;
                cell.setTextSize(16);
                cell.setValueTextSize(18);
                cell.setTextColor(0xff212121);
                if (position == userNameRow) {
                    cell.setValueTextColor(0xff757575);
                    cell.setTextAndValue("姓名", userName, true);
                } else if (position == medicareCardNoRow) {
                    cell.setValueTextColor(0xff757575);
                    cell.setTextAndValue("社会保障卡号", medicareCardNo, true);
                } else if (position == medicareCardBalanceRow) {
                    cell.setValueTextColor(ResourcesConfig.priceFontColor);
                    CharSequence balance = ShoppingHelper.formatPrice(medicareBalance);
                    cell.setTextAndValue("社会保障卡号余额", balance, true);
                } else if (position == medicareCardStatuesRow) {
                    cell.setValueTextColor(0xff757575);
                    if (checkStatus == CheckStatus.ERROR) {
                        cell.setTextAndValue("社会保障卡号余额", "查询失败", true);
                    } else if (checkStatus == CheckStatus.COMPLETED) {
                        boolean enablePay = false;
                        if (medicareBalance != null && medicareBalance.compareTo(BigDecimal.ZERO) > 0) {
                            enablePay = (medicareBalance.compareTo(payAmount) >= 0);
                        }
                        cell.setTextAndValue("", enablePay ? "余额充足" : "余额不足", true);
                    } else {
                        cell.setTextAndValue("社会保障卡号余额", "正在查询中...", true);
                    }
                }
            }
            return convertView;
        }
    }

}
