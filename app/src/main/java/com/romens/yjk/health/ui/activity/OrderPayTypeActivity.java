package com.romens.yjk.health.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.EmptyCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.DoubleTextCheckCell;
import com.romens.yjk.health.ui.cells.H3HeaderCell;
import com.romens.yjk.health.ui.cells.TextCheckCell;

/**
 * @author Zhou Lisi
 * @create 16/3/1
 * @description
 */
public class OrderPayTypeActivity extends BaseActionBarActivityWithAnalytics {
    private boolean supportMedicareCardPay = false;
    private ListView listView;
    private ListAdapter listAdapter;

    private int selectPayType = 0;
    private int selectDelivery = 0;

    public static final String[] payType = new String[]{"在线支付", "收货付款", "医保支付"};
    public static final String[] deliveryType = new String[]{"到店自提", "送货上门", "快递送货"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        supportMedicareCardPay = intent.getBooleanExtra("SupportMedicareCard", false);
        selectPayType = intent.getIntExtra("PayType", supportMedicareCardPay ? 2 : 0);
        selectDelivery = intent.getIntExtra("DeliveryType", 0);

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        content.setBackgroundColor(0xffffffff);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);
        actionBar.setTitle("选择支付和配送方式");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    onBackPressed();
                }
            }
        });
        listView = new ListView(this);
        content.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelector(R.drawable.list_selector);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == payTypeForOnlineRow) {
                    selectPayType = 0;
                    updateAdapter();
                } else if (position == payTypeForAfterRow) {
                    selectPayType = 1;
                    updateAdapter();
                } else if (position == payTypeForMedicareRow) {
                    selectPayType = 2;
                    updateAdapter();
                } else if (position == deliveryForToStoreRow) {
                    selectDelivery = 0;
                    updateAdapter();
                } else if (position == deliveryForToHomeRow) {
                    selectDelivery = 1;
                    updateAdapter();
                } else if (position == deliveryForExpressRow) {
                    selectDelivery = 2;
                    updateAdapter();
                } else if (position == saveActionRow) {
                    onSavePayAndDeliveryType();
                }
            }
        });

        listAdapter = new ListAdapter(this);
        listView.setAdapter(listAdapter);
        updateAdapter();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onSavePayAndDeliveryType() {
        Intent data = new Intent();
        data.putExtra("PayType", selectPayType);
        data.putExtra("PayTypeName", payType[selectPayType]);
        data.putExtra("DeliveryType", selectDelivery);
        data.putExtra("DeliveryTypeName", deliveryType[selectDelivery]);
        setResult(RESULT_OK, data);
        finish();
    }

    private void updateAdapter() {
        rowCount = 0;
        payTypeSection = rowCount++;
        payTypeForOnlineRow = supportMedicareCardPay ? -1 : rowCount++;
        payTypeForAfterRow = supportMedicareCardPay ? -1 : rowCount++;
        payTypeForMedicareRow = supportMedicareCardPay ? rowCount++ : -1;
        deliverySection = rowCount++;
        deliverySection1 = rowCount++;

        if (selectPayType == 1) {
            deliveryForToStoreRow = rowCount++;
            deliveryForToHomeRow = rowCount++;
            deliveryForExpressRow = -1;
            if (selectDelivery == 2) {
                selectDelivery = 0;
            }
        } else {
            deliveryForToStoreRow = rowCount++;
            deliveryForToHomeRow = rowCount++;
            deliveryForExpressRow = rowCount++;
        }

        saveSection = rowCount++;
        saveActionRow = rowCount++;

        listAdapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int payTypeSection;
    private int payTypeForOnlineRow;
    private int payTypeForAfterRow;
    private int payTypeForMedicareRow;

    private int deliverySection;
    private int deliverySection1;
    private int deliveryForToStoreRow;
    private int deliveryForToHomeRow;
    private int deliveryForExpressRow;

    private int saveSection;
    private int saveActionRow;


    private class ListAdapter extends BaseAdapter {
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
        public View getView(int position, View convertView, ViewGroup parent) {
            int viewType = getItemViewType(position);
            if (viewType == 0) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(adapterContext);
                }
            } else if (viewType == 1) {
                if (convertView == null) {
                    convertView = new H3HeaderCell(adapterContext);
                }
                H3HeaderCell cell = (H3HeaderCell) convertView;
                cell.setTextSize(18);
                cell.setTextColor(ResourcesConfig.primaryColor);
                if (position == payTypeSection) {
                    cell.setText("付款方式");
                } else if (position == deliverySection1) {
                    cell.setText("配送方式");
                }
            } else if (viewType == 2) {
                if (convertView == null) {
                    convertView = new TextCheckCell(adapterContext);
                }
                TextCheckCell cell = (TextCheckCell) convertView;
                if (position == payTypeForOnlineRow) {
                    cell.setValue(payType[0], selectPayType == 0, true);
                } else if (position == payTypeForAfterRow) {
                    cell.setValue(payType[1], selectPayType == 1, false);
                } else if (position == payTypeForMedicareRow) {
                    cell.setValue(payType[2], selectPayType == 2, false);
                }
            } else if (viewType == 3) {
                if (convertView == null) {
                    convertView = new DoubleTextCheckCell(adapterContext);
                }
                DoubleTextCheckCell cell = (DoubleTextCheckCell) convertView;
                if (position == deliveryForToStoreRow) {
                    cell.setValue(deliveryType[0], "推荐到店自提,有机会得到积分加倍累计", selectDelivery == 0, true);
                } else if (position == deliveryForToHomeRow) {
                    cell.setValue(deliveryType[1], "优先由最近的门店派送,支持现金、微信或支付宝当面付", selectDelivery == 1, true);
                } else if (position == deliveryForExpressRow) {
                    cell.setValue(deliveryType[2], "可能会收取部分配送费用", selectDelivery == 2, false);
                }
            } else if (viewType == 4) {
                if (convertView == null) {
                    convertView = new EmptyCell(adapterContext);
                }
                EmptyCell cell = (EmptyCell) convertView;
                cell.setHeight(AndroidUtilities.dp(16));
            } else if (viewType == 5) {
                if (convertView == null) {
                    convertView = new ActionCell(adapterContext);
                }
                ActionCell cell = (ActionCell) convertView;
                cell.setValue("保存支付和配送方式");
            }
            return convertView;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            if (position == payTypeForOnlineRow ||
                    position == payTypeForAfterRow ||
                    position == deliveryForToStoreRow ||
                    position == deliveryForToHomeRow ||
                    position == deliveryForExpressRow ||
                    position == saveActionRow) {
                return true;
            }
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == payTypeSection || position == deliverySection1) {
                return 1;
            } else if (position == payTypeForOnlineRow || position == payTypeForAfterRow || position == payTypeForMedicareRow) {
                return 2;
            } else if (position == deliveryForToStoreRow || position == deliveryForToHomeRow || position == deliveryForExpressRow) {
                return 3;
            } else if (position == saveSection) {
                return 4;
            } else if (position == saveActionRow) {
                return 5;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 6;
        }
    }
}
