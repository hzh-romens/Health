package com.romens.yjk.health.ui.activity.dev;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;
import com.romens.yjk.health.pay.PayPrepareBaseActivity;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.activity.ShoppingCartActivity;
import com.romens.yjk.health.ui.components.logger.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Zhou Lisi
 * @create 16/2/23
 * @description 开发者模式
 */
public class DevelopModeActivity extends DarkActionBarActivity {
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);
        actionBar.setTitle("开发者模式");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });
        ListView listView = new ListView(this);
        container.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setSelector(R.drawable.list_selector);


        adapter = new ListAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == testYBZFRow) {
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName(getPackageName(), getPackageName() + ".ui.activity.MedicarePayActivity");
                    intent.setComponent(componentName);
                    Bundle arguments = new Bundle();
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    String orderNo = format.format(Calendar.getInstance().getTime());
                    arguments.putString(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_NO, orderNo);
                    arguments.putDouble(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_AMOUNT, 0.01);
                    arguments.putDouble(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_PAY_AMOUNT, 0.01);
                    intent.putExtras(arguments);
                    startActivity(intent);
                } else if (position == testShoppingCartRow) {
                    Intent intent = new Intent(DevelopModeActivity.this, ShoppingCartActivity.class);
                    startActivity(intent);
                } else if (position == testAlipayRow) {
                    Intent intent = new Intent(DevelopModeActivity.this, DevTextAlipay.class);
                    startActivity(intent);
                } else if (position == testYBZF_CXYERow) {
                    onTestYBZF_CXYERow();
                }
            }
        });
        updateAdapter();
    }

    private void onTestYBZF_CXYERow() {
        ComponentName componentName = new ComponentName("com.yitong.hrb.people.android",
                "com.yitong.hrb.people.android.activity.GifViewActivity");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("cardNo", "11204688X");
        bundle.putString("O2TRSN", "C067");
        bundle.putString("certNo", "230102198506053415");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = format.format(Calendar.getInstance().getTime());
        bundle.putString("transferFlowNo","C067"+date);
        bundle.putString("hrbbType","1");
        String packageName = MyApplication.applicationContext.getPackageName();
        bundle.putString("packageName", packageName);
        bundle.putString("activityPath", packageName + ".pay.YBPayResult");
        bundle.putString("appName", MyApplication.applicationContext.getString(R.string.app_name));
        intent.putExtra("queryResult", bundle);

        Bundle extBundle=new Bundle();
        extBundle.putString("userId","aaa");
        intent.putExtra("extBundle", extBundle);


        intent.setComponent(componentName);
        startActivityForResult(intent, 0);
    }

    private void onTestYBZFRow() {
        ComponentName componentName = new ComponentName("com.yitong.hrb.people.android",
                "com.yitong.hrb.people.android.activity.GifViewActivity");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("flowno", "");
        bundle.putString("orderno", "");
        bundle.putString("EndDate", "");
        bundle.putString("MerType", "0");
        bundle.putString("acctFlag", "1");
        bundle.putString("payAmount", "");
        bundle.putString("agtname", "");
        bundle.putString("cardNo", "");
        bundle.putString("O2TRSN", "C067");
        bundle.putString("transferFlowNo", "");
        bundle.putString("BusiPeriod", "20150000");
        bundle.putString("hrbbType", "1");
        intent.putExtra("bundle", bundle);
        intent.setComponent(componentName);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DevelopMode", "requestCode=" + requestCode + ";resultCode=" + resultCode);
    }

    private void updateAdapter() {
        rowCount = 0;
        testYBZFRow = rowCount++;
        testYBZF_CXYERow = rowCount++;
        testShoppingCartRow = rowCount++;
        testAlipayRow = rowCount++;
        adapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int testYBZFRow;
    private int testYBZF_CXYERow;
    private int testShoppingCartRow;
    private int testAlipayRow;

    @Override
    protected String getActivityName() {
        return "开发者模式";
    }

    class ListAdapter extends BaseAdapter {

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
            if (convertView == null) {
                convertView = new TextSettingsCell(DevelopModeActivity.this);
            }
            TextSettingsCell cell = (TextSettingsCell) convertView;
            if (position == testYBZFRow) {
                cell.setText("医保支付测试", true);
            } else if (position == testShoppingCartRow) {
                cell.setText("购物车测试", true);
            } else if (position == testAlipayRow) {
                cell.setText("支付宝支付测试", true);
            } else if (position == testYBZF_CXYERow) {
                cell.setText("医保查询余额测试", true);
            }
            return convertView;
        }
    }
}
