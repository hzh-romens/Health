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
import com.romens.yjk.health.pay.Pay;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.DoubleTextCheckCell;
import com.romens.yjk.health.ui.cells.H3HeaderCell;
import com.romens.yjk.health.ui.cells.TextCheckCell;
import com.romens.yjk.health.ui.cells.TipCell;

/**
 * @author Zhou Lisi
 * @create 16/3/1
 * @description
 */
public class OrderPayTypeActivity extends DarkActionBarActivity {
    private boolean supportMedicareCardPay = false;
    private ListView listView;
    private ListAdapter listAdapter;

    private int selectPayType = Pay.PAY_TYPE_ONLINE;
    private int selectDelivery = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        supportMedicareCardPay = intent.getBooleanExtra("SupportMedicareCard", false);
        selectPayType = intent.getIntExtra("PayType", supportMedicareCardPay ? Pay.PAY_TYPE_YB_ONLINE : Pay.PAY_TYPE_ONLINE);
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
                    selectPayType = Pay.PAY_TYPE_ONLINE;
                    updateAdapter();
                } else if (position == payTypeForAfterRow) {
                    selectPayType = Pay.PAY_TYPE_OFFLINE;
                    updateAdapter();
                } else if (position == payTypeForMedicareRow) {
                    selectPayType = Pay.PAY_TYPE_YB_ONLINE;
                    updateAdapter();
                } else if (position >= deliveryBeginRow && position <= deliveryEndRow) {
                    Pay.DeliveryMode mode = Pay.getInstance().getSupportDeliveryModeForIndex(position - deliveryBeginRow);
                    selectDelivery = mode.id;
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
        data.putExtra("PayTypeName", Pay.getInstance().getPayType(selectPayType));
        data.putExtra("DeliveryType", selectDelivery);
        Pay.DeliveryMode deliveryMode = Pay.getInstance().getSupportDeliveryMode(selectDelivery);
        data.putExtra("DeliveryTypeName", deliveryMode.name);
        setResult(RESULT_OK, data);
        finish();
    }

    private void updateAdapter() {
        rowCount = 0;
        tipRow = rowCount++;
        payTypeSection = rowCount++;
        payTypeForOnlineRow = supportMedicareCardPay ? -1 : rowCount++;
        payTypeForMedicareRow = supportMedicareCardPay ? rowCount++ : -1;
        payTypeForAfterRow = rowCount++;

        deliverySection = rowCount++;
        deliverySection1 = rowCount++;

        int deliveryCount = Pay.getInstance().getSupportDeliveryModesCount();
        if (deliveryCount > 0) {
            deliveryBeginRow = rowCount;
            rowCount += deliveryCount - 1;
            deliveryEndRow = rowCount++;
        }

        saveSection = rowCount++;
        saveActionRow = rowCount++;

        listAdapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int tipRow;
    private int payTypeSection;
    private int payTypeForOnlineRow;
    private int payTypeForAfterRow;
    private int payTypeForMedicareRow;

    private int deliverySection;
    private int deliverySection1;
    private int deliveryBeginRow;
    private int deliveryEndRow;

    private int saveSection;
    private int saveActionRow;

    @Override
    protected String getActivityName() {
        return "选择支付和配送方式";
    }


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
                    cell.setValue(Pay.getInstance().getPayType(Pay.PAY_TYPE_ONLINE), selectPayType == Pay.PAY_TYPE_ONLINE, true);
                } else if (position == payTypeForAfterRow) {
                    cell.setValue(Pay.getInstance().getPayType(Pay.PAY_TYPE_OFFLINE), selectPayType == Pay.PAY_TYPE_OFFLINE, false);
                } else if (position == payTypeForMedicareRow) {
                    cell.setValue(Pay.getInstance().getPayType(Pay.PAY_TYPE_YB_ONLINE) + "(支持现金在线支付)", selectPayType == Pay.PAY_TYPE_YB_ONLINE, false);
                }
            } else if (viewType == 3) {
                if (convertView == null) {
                    convertView = new DoubleTextCheckCell(adapterContext);
                }
                DoubleTextCheckCell cell = (DoubleTextCheckCell) convertView;
                Pay.DeliveryMode mode = Pay.getInstance().getSupportDeliveryModeForIndex(position - deliveryBeginRow);
                cell.setValue(mode.name, mode.desc, selectDelivery == mode.id, true);
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
            } else if (viewType == 6) {
                if (convertView == null) {
                    convertView = new TipCell(adapterContext);
                }

                TipCell cell = (TipCell) convertView;
                if (supportMedicareCardPay && selectPayType != Pay.PAY_TYPE_OFFLINE) {
                    cell.setValue("医保支付的药品,支持使用医保卡在线支付或者其他现金在线支付方式");
                } else {
                    cell.setValue("配送方式选择药店派送或者到店自提时，在您的商品准备好后，我们会有专人电话联系您!");
                }
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
                    position == payTypeForMedicareRow ||
                    position == payTypeForAfterRow ||
                    position >= deliveryBeginRow && position <= deliveryEndRow ||
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
            } else if (position >= deliveryBeginRow && position <= deliveryEndRow) {
                return 3;
            } else if (position == saveSection) {
                return 4;
            } else if (position == saveActionRow) {
                return 5;
            } else if (position == tipRow) {
                return 6;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 7;
        }
    }
}
