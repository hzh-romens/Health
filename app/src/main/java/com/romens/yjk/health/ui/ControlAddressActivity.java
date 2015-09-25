package com.romens.yjk.health.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.CitysDao;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.db.entity.CitysEntity;
import com.romens.yjk.health.ui.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/8/19.
 * 用户地址管理页面
 */
public class ControlAddressActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView listView;
    private ActionBar actionBar;
    private Button addAddress;
    private List<AddressEntity> addressListEntitis;
    private ControlAddressAdapter adapter;

    private LinearLayout havaAddressLayout;
    private LinearLayout noHaveAddressLayout;

    private String userGuid = "2222";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address, R.id.action_bar);
        havaAddressLayout = (LinearLayout) findViewById(R.id.control_address_hava_address);
        noHaveAddressLayout = (LinearLayout) findViewById(R.id.control_address_no_address);
        actionBarEven();
        initData();
        if (addressListEntitis != null && addressListEntitis.size() > 0) {
            setHaveAddressView();
        } else {
            setNoHaveAddressView();
        }
        queryDb();
        if (entities == null || entities.size() < 1) {
            requestCityDataChanged();
        }
    }

    private void setNoHaveAddressView() {
        noHaveAddressLayout.setVisibility(View.VISIBLE);
        havaAddressLayout.setVisibility(View.GONE);
        listView = (RecyclerView) findViewById(R.id.control_address_recycler);
        listView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        adapter = new ControlAddressAdapter(this, addressListEntitis);
        listView.setAdapter(adapter);
        addAddress = (Button) findViewById(R.id.control_address_add_address);
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ControlAddressActivity.this, NewShoppingAddressActivity.class));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        requestDataChanged(userGuid, "0");
    }

    private void setHaveAddressView() {
        listView = (RecyclerView) findViewById(R.id.control_address_recycler);
        adapter = new ControlAddressAdapter(this, addressListEntitis);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        UIHelper.setupSwipeRefreshLayoutProgress(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new refreshTask().execute(swipeRefreshLayout);
            }
        });
        noHaveAddressLayout.setVisibility(View.GONE);
        havaAddressLayout.setVisibility(View.VISIBLE);
//        addAddress = (Button) findViewById(R.id.control_address_add_newaddress);
//        addAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ControlAddressActivity.this, NewShoppingAddressActivity.class));
//            }
//        });
    }

    //请求收货地址列表
    private void requestDataChanged(String userGuid, String defaultFlag) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("DEFAULTFLAG", defaultFlag);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserAddressList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(ControlAddressActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    setAddressListData(responseProtocol.getResponse());
                }
                if (errorMsg == null) {
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    private void setAddressListData(List<LinkedTreeMap<String, String>> response) {
        int count = response == null ? 0 : response.size();
        if (count <= 0) {
            return;
        }
        addressListEntitis = new ArrayList<>();
        for (LinkedTreeMap<String, String> item : response) {
            AddressEntity entity = AddressEntity.mapToEntity(item);
            addressListEntitis.add(entity);
        }
        if (addressListEntitis != null && addressListEntitis.size() > 0) {
            setHaveAddressView();
        } else {
            setNoHaveAddressView();
        }
        adapter.setData(addressListEntitis);
        adapter.notifyDataSetChanged();
    }

    private void actionBarEven() {
        actionBar = getMyActionBar();
        ActionBarMenu actionBarMenu = actionBar.createMenu();
//        actionBarMenu.addItem(0, R.drawable.addcontact_blue);
//        TextView textView = new TextView(this);
//        textView.setText("新增");
//        textView.setGravity(Gravity.CENTER);
//        actionBarMenu.addView(textView, 0);
        actionBarMenu.addItem(0, R.drawable.addcontact_blue);

        actionBar.setTitle("收货地址管理");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                    startActivity(new Intent(ControlAddressActivity.this, NewShoppingAddressActivity.class));
                }
            }
        });
    }

    class refreshTask extends AsyncTask<SwipeRefreshLayout, Void, Void> {

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
        addressListEntitis = new ArrayList<>();
        requestDataChanged(userGuid, "0");
    }


    class ControlAddressAdapter extends RecyclerView.Adapter<ControlAddressHolder> {

        private Context context;
        private List<AddressEntity> data;
        private int currDefaultAddressIndex = -1;

        public void setData(List<AddressEntity> data) {
            this.data = data;
        }

        public void changeDefaultAddressIndex(int newIndex) {
            if (newIndex == currDefaultAddressIndex) {
                return;
            }
            if (currDefaultAddressIndex != -1) {
                data.get(currDefaultAddressIndex).setISDEFAULT("0");
            }
            currDefaultAddressIndex = newIndex;
            data.get(currDefaultAddressIndex).setISDEFAULT("1");
            notifyDataSetChanged();
        }

        public ControlAddressAdapter(Context context, List<AddressEntity> date) {
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
        public void onBindViewHolder(final ControlAddressHolder holder, final int position) {
            int type = getItemViewType(position);
            if (type == 0) {
                final int index = position / 2;
                holder.nameView.setText(data.get(index).getRECEIVER());
                holder.telView.setText(data.get(index).getCONTACTPHONE());
                holder.addressview.setText(data.get(index).getADDRESS());
                holder.del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(index);
                    }
                });
                holder.defaultImg.setImageResource(R.drawable.control_address_undeafult);
                AddressEntity entity = data.get(index);
                if (entity.getISDEFAULT().equals("1")) {
                    currDefaultAddressIndex = index;
                    holder.defaultImg.setImageResource(R.drawable.control_address_deafult);
                }
                holder.isDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(context, "click-->" + data.get(index).getADDRESSID(), Toast.LENGTH_SHORT).show();
                        requestDefaultChanged(userGuid, data.get(index).getADDRESSID(), index);
                    }
                });
            }
        }

        public void removeItem(final int position) {
            new AlertDialog.Builder(context).setTitle("真的要删除吗？").setNegativeButton("不了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("是的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestDeleteDataChanged(data.get(position).getADDRESSID());
                }
            }).show();
        }

        @Override
        public int getItemCount() {
            return data.size() * 2;
        }
    }

    //请求修改默认
    private void requestDefaultChanged(final String userGuid, String addressId, final int index) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("ADDRESSID", addressId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "setDefaultAddress", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(ControlAddressActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String requestCode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        requestCode = jsonObject.getString("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("ok")) {
                        requestDataChanged(userGuid, "0");
                    } else {
                        Toast.makeText(ControlAddressActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", msg.msg);
                }
            }
        });
    }

    //请求删除接口
    private void requestDeleteDataChanged(String addressId) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("ADDRESSID", addressId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "deleteAddress", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(ControlAddressActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String requestCode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        requestCode = jsonObject.getString("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("ok")) {
                        requestDataChanged(userGuid, "0");
                    } else {
                        Toast.makeText(ControlAddressActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", msg.msg);
                }
            }
        });
    }


    class ControlAddressHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView telView;
        private TextView addressview;
        private Button isDefault;
        private ImageView del;
        private ImageView defaultImg;

        public ControlAddressHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.control_address_name);
            telView = (TextView) itemView.findViewById(R.id.control_address_tel);
            addressview = (TextView) itemView.findViewById(R.id.control_address_address);
            isDefault = (Button) itemView.findViewById(R.id.control_address_isdefault);
            del = (ImageView) itemView.findViewById(R.id.control_address_del);
            defaultImg = (ImageView) itemView.findViewById(R.id.controladdress_deafult_img);
        }
    }

    //请求省市县
    private void requestCityDataChanged() {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetAllDistrict", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(ControlAddressActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    setQueryData(responseProtocol.getResponse());
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    private List<CitysEntity> entities;

    private void setQueryData(List<LinkedTreeMap<String, String>> response) {
        int count = response == null ? 0 : response.size();
        if (count <= 0) {
            return;
        }
        for (LinkedTreeMap<String, String> item : response) {
            CitysEntity entity = CitysEntity.mapToEntity(item);
            entities.add(entity);
            CitysDao dao = DBInterface.instance().openReadableDb().getCitysDao();
            dao.insert(entity);
        }
    }

    public void queryDb() {
        CitysDao dao = DBInterface.instance().openReadableDb().getCitysDao();
        entities = dao.queryBuilder().list();
    }
}
