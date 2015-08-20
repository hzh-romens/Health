package com.romens.yjk.health.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Components.RecyclerListView;
import com.romens.android.ui.cells.TextCheckCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.EditTextCell;

/**
 * Created by siery on 15/8/20.
 */
public class NewShippingAddressActivity extends BaseActivity {
    private RecyclerListView listView;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);
        actionBar.setTitle("新增收货地址");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });
        listView = new RecyclerListView(this);
        listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == isDefaultRow) {
                    TextCheckCell cell = (TextCheckCell) view;
                    cell.setChecked(true);
                }
            }
        });
        listView.setLayoutManager(new LinearLayoutManager(this));
        content.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        adapter = new ListAdapter(this);
        listView.setAdapter(adapter);
        updateData();
    }

    private int rowCount;
    private int nameRow;
    private int phoneRow;
    private int locationAddressRow;
    private int detailAddressRow;
    private int isDefaultRow;

    private void updateData() {
        rowCount = 0;
        nameRow = rowCount++;
        phoneRow = rowCount++;
        locationAddressRow = rowCount++;
        detailAddressRow = rowCount++;
        isDefaultRow = rowCount++;
        adapter.notifyDataSetChanged();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View itemView) {
            super(itemView);
        }
    }

    class ListAdapter extends RecyclerView.Adapter<ItemHolder> {
        private Context adapterContext;

        public ListAdapter(Context context) {
            adapterContext = context;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == nameRow || position == phoneRow || position == detailAddressRow) {
                return 0;
            } else if (position == locationAddressRow) {
                return 1;
            } else if (position == isDefaultRow) {
                return 2;
            }
            return -1;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                EditTextCell cell = new EditTextCell(parent.getContext());
                cell.setLabelTextAlwaysShown(true);
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new ItemHolder(cell);
            } else if (viewType == 1) {
                TextSettingsCell cell = new TextSettingsCell(parent.getContext());
                cell.setBackgroundResource(R.drawable.list_selector);
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new ItemHolder(cell);
            } else if (viewType == 2) {
                TextCheckCell cell = new TextCheckCell(parent.getContext());
                cell.setBackgroundResource(R.drawable.list_selector);
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new ItemHolder(cell);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType == 0) {
                EditTextCell cell = (EditTextCell) holder.itemView;
                if (position == nameRow) {
                    cell.setInputType(InputType.TYPE_CLASS_TEXT);
                    cell.setLabelText("姓名");
                    cell.setNeedDivider(true);
                } else if (position == phoneRow) {
                    cell.setInputType(InputType.TYPE_CLASS_PHONE);
                    cell.setLabelText("联系电话");
                    cell.setNeedDivider(true);
                } else if (position == detailAddressRow) {
                    cell.setInputType(InputType.TYPE_CLASS_TEXT);
                    cell.setLabelText("详细地址");
                    cell.setNeedDivider(true);
                }
            } else if (viewType == 1) {
                TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                if (position == locationAddressRow) {
                    cell.setText("所在地区", true);
                }
            } else if (viewType == 2) {
                TextCheckCell cell = (TextCheckCell) holder.itemView;
                if (position == isDefaultRow) {
                    cell.setTextAndCheck("是否默认地址", false, true);
                }
            }
        }
    }
}
