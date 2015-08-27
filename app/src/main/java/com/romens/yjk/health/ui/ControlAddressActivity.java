package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.ControlAddressEntity;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/8/19.
 */
public class ControlAddressActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView listView;
    private ActionBar actionBar;
    private Button addAddress;
    private List<ControlAddressEntity> entitis;
    private ControlAddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        controlAddressActivity = this;
//        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
//        ActionBar actionBar = new ActionBar(this);
//        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
//
//        swipeRefreshLayout = new SwipeRefreshLayout(this);
//        UIHelper.setupSwipeRefreshLayoutProgress(swipeRefreshLayout);
//        FrameLayout content = new FrameLayout(this);
//        content.addView(swipeRefreshLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
//        container.addView(content, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
//
//        FrameLayout listLayout = new FrameLayout(this);
//        swipeRefreshLayout.addView(listLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
//
//        listView = new RecyclerView(this);
//        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        listView.setFocusable(true);
//        initDate();
//        adapter = new ControlAddressAdapter(this, entitis);
//        listView.setAdapter(adapter);
//        listLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        setContentView(R.layout.activity_shipping_address, R.id.action_bar);

        actionBar = getMyActionBar();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        UIHelper.setupSwipeRefreshLayoutProgress(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new refreshTask().execute(swipeRefreshLayout);
            }
        });
        listView = (RecyclerView) findViewById(R.id.control_address_recycler);
        listView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        //addAddress = (Button) findViewById(R.id.control_addres_addaddress);
        initData();
        adapter = new ControlAddressAdapter(this, entitis);
        listView.setAdapter(adapter);
        actionBar.setTitle("收获地址管理");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ControlAddressActivity.this, NewShippingAddressActivity.class));
            }
        });
    }
    class refreshTask extends AsyncTask<SwipeRefreshLayout,Void,Void>{

        private SwipeRefreshLayout layout;

        @Override
        protected Void doInBackground(SwipeRefreshLayout... params) {
            this.layout = params[0];
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            layout.setRefreshing(false);
        }
    }

    private void initData() {
        entitis = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ControlAddressEntity entity = new ControlAddressEntity();
            entity.setName("yaojk" + i);
            entity.setTel("1300000000" + i);
            entity.setAddress("市南区软件园青岛雨人公司");
            entity.setIsDefault(false);
            entitis.add(entity);
        }
    }

    class ControlAddressAdapter extends RecyclerView.Adapter<ControlAddressHolder> {

        private Context context;
        private List<ControlAddressEntity> data;
        private int currDefaultAddressIndex = -1;

        public void changeDefaultAddressIndex(int newIndex) {
            if (newIndex == currDefaultAddressIndex) {
                return;
            }
            if (currDefaultAddressIndex != -1) {
                data.get(currDefaultAddressIndex).setIsDefault(false);
            }
            currDefaultAddressIndex = newIndex;
            data.get(currDefaultAddressIndex).setIsDefault(true);
            notifyDataSetChanged();
        }

        public ControlAddressAdapter(Context context, List<ControlAddressEntity> date) {
            this.context = context;
            this.data = date;
        }

        @Override
        public int getItemViewType(int position) {
            return position % 2;
        }

        @Override
        public ControlAddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = LayoutInflater.from(context).inflate(R.layout.list_item_controladdress, null);
            } else if (viewType == 1) {
                view = new ShadowSectionCell(context);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ControlAddressHolder(view);
        }

        @Override
        public void onBindViewHolder(final ControlAddressHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == 0) {
                final int index=position/2;
                holder.nameView.setText(data.get(index).getName());
                holder.telView.setText(data.get(index).getTel());
                holder.addressview.setText(data.get(index).getAddress());
                setItemUpdate(holder.isDefault, data.get(index).isDefault());
                holder.del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(index);
                    }
                });
                holder.isDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeDefaultAddressIndex(holder.getLayoutPosition());
                    }
                });
//                ControlAddressCell cell = (ControlAddressCell) holder.itemView;
//                ControlAddressEntity entity = date.get(position);
//                if (currDefaultAddressIndex == -1 && entity.isDefault()) {
//                    currDefaultAddressIndex = position;
//                }
//                cell.setOnIsDefaultChangedListener(new ControlAddressCell.OnIsDefaultChangedListener() {
//                    @Override
//                    public void onChanged() {
//                        changeDefaultAddressIndex(position);
//                    }
//                });
//                cell.setDate(entity, controlAddressActivity);
            }
        }
        public void removeItem(int position) {
            data.remove(position);
            notifyDataSetChanged();
        }
        @Override
        public int getItemCount() {
            return data.size()*2;
        }

        public void setItemUpdate(View view, boolean flag) {
            view.setBackgroundResource(flag ? R.color.hint_text : R.color.theme_primary);
        }
    }

    class ControlAddressHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView telView;
        private TextView addressview;
        private Button isDefault;
        private TextView del;
        private TextView edit;

        public ControlAddressHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.control_address_name);
            telView = (TextView) itemView.findViewById(R.id.control_address_tel);
            addressview = (TextView) itemView.findViewById(R.id.control_address_address);
            isDefault = (Button) itemView.findViewById(R.id.control_address_isdefault);
            del = (TextView) itemView.findViewById(R.id.control_address_del);
            edit = (TextView) itemView.findViewById(R.id.control_address_edit);
        }
    }
}
