package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.romens.android.library.datetimepicker.date.DatePickerDialog;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.model.PersonalEntity;
import com.romens.yjk.health.ui.activity.LightActionBarActivity;
import com.romens.yjk.health.ui.activity.UserLabelsActivity;
import com.romens.yjk.health.ui.cells.AccountCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by anlc on 2015/12/21.
 */
public class AccountSettingActivity extends LightActionBarActivity implements DatePickerDialog.OnDateSetListener {

    private ListView listView;
    private AccountAdapter adapter;

    private PersonalEntity entity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 0) {
                    if (entity.getPERSONNAME() == null) {
                        Toast.makeText(AccountSettingActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                    } else {
                        SaveInfor();
                    }
                }
            }
        });
        setContentView(content, actionBar);
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.ic_done_grey600_24dp);
        setActionBarTitle(actionBar, "个人信息");
        content.setBackgroundColor(getResources().getColor(R.color.title_background_grey));
        setRow();

        entity = new PersonalEntity();
        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setSelection(R.drawable.list_selector);
        listView.setVerticalScrollBarEnabled(false);
        content.addView(listView);
        adapter = new AccountAdapter(this);
        listView.setAdapter(adapter);
        initData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == nameRow) {
                    Intent intent = new Intent(AccountSettingActivity.this, EditActivity.class);
                    intent.putExtra("activity_title", "编辑姓名");
                    intent.putExtra("formActivityName", "AccountSettingActivity");
                    startActivityForResult(intent, UserGuidConfig.REQUEST_ACCOUNTSETTING_TO_EDITACTIVITY);
                } else if (position == sexRow) {
                    chooseSex();
                } else if (position == professionRow) {
                    Intent intent = new Intent(AccountSettingActivity.this, EditActivity.class);
                    intent.putExtra("activity_title", "编辑职业");
                    intent.putExtra("formActivityName", "AccountSettingActivity");
                    startActivityForResult(intent, UserGuidConfig.REQUEST_ACCOUNTSETTING2_TO_EDITACTIVITY);
                } else if (position == birthdayRow) {
                    showDatePickerDialog();
                } else if (position == detailRow || position == detailIntroduceRow) {
//                    UIOpenHelper.openUserLabelsActivity(AccountSettingActivity.this);
                    Intent intent = new Intent(AccountSettingActivity.this, UserLabelsActivity.class);
                    intent.putExtra("personEntity", entity);
                    startActivityForResult(intent, UserGuidConfig.REQUEST_ACCOUNTSETTING_TO_USERLABELS);
                }
            }
        });
    }

    //获取数据
    private void initData() {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserInfo", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol).build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {

            @Override
            public void onTokenTimeout(Message msg) {
                needHideProgress();
                Log.e("person message", msg.msg);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    Gson gson = new Gson();
                    entity = gson.fromJson(response, PersonalEntity.class);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("person message", errorMsg.msg);
                }
            }
        });

    }


    //保存信息
    private void SaveInfor() {
        Gson gson = new Gson();
        final String jsonData = gson.toJson(entity);
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid())
                .put("JSONDATA", jsonData)
                .build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "SaveUserInfo", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol).build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {

            @Override
            public void onTokenTimeout(Message msg) {
                needHideProgress();
                Log.e("个人信息", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("yes".equals(jsonObject.getString("success"))) {
                            Toast.makeText(AccountSettingActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("个人信息错误日志", errorMsg.msg);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UserGuidConfig.REQUEST_ACCOUNTSETTING_TO_EDITACTIVITY
                && resultCode == UserGuidConfig.RESPONSE_EDITACTIVITY) {
            entity.setPERSONNAME(data.getStringExtra("editActivityResult"));
        } else if (requestCode == UserGuidConfig.REQUEST_ACCOUNTSETTING2_TO_EDITACTIVITY
                && resultCode == UserGuidConfig.RESPONSE_EDITACTIVITY) {
            entity.setJOB(data.getStringExtra("editActivityResult"));
        } else if (requestCode == UserGuidConfig.REQUEST_ACCOUNTSETTING_TO_USERLABELS
                && resultCode == UserGuidConfig.RESPONSE_USERLABELS_TO_ACCOUNTSETTING) {
            String jsonStr = data.getStringExtra("userLabelResultJson");
            if (jsonStr == null) {
                return;
            }
            entitySetFromJson(jsonStr);
        }
    }

    private void entitySetFromJson(String jsonStr) {
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray value = jsonObject.getJSONArray("values");
                JSONArray valuesDesc = jsonObject.getJSONArray("valuesDesc");
                if ("heredity".equals(jsonObject.getString("key"))) {
                    entity.setHASINHERITED(value.getString(0));
                } else if ("history".equals(jsonObject.getString("key"))) {
                    entity.setHASSERIOUS(value.getString(0));
                } else if ("allergy".equals(jsonObject.getString("key"))) {
                    entity.setHASGUOMIN(value.getString(0));
                } else if ("preference".equals(jsonObject.getString("key"))) {
                    String result = "";
                    for (int j = 0; j < valuesDesc.length(); j++) {
                        result += valuesDesc.getString(j) + ",";
                    }
                    entity.setFOODHOBBY(result.substring(0, result.length() - 1));
                } else if ("habit".equals(jsonObject.getString("key"))) {
                    String result = "";
                    for (int j = 0; j < valuesDesc.length(); j++) {
                        result += valuesDesc.getString(j) + ",";
                    }
                    entity.setSLEEPHOBBY(result.substring(0, result.length() - 1));
                } else if ("other".equals(jsonObject.getString("key"))) {
                    String result = "";
                    for (int j = 0; j < valuesDesc.length(); j++) {
                        result += valuesDesc.getString(j) + ",";
                    }
                    entity.setOTHER(result.substring(0, result.length() - 1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chooseSex() {
        new AlertDialog.Builder(this)
                .setTitle("选择性别")
                .setSingleChoiceItems(new String[]{"男", "女", "保密"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 2) {
                            entity.setGENDER("");
                        } else {
                            entity.setGENDER((which + 1) + "");
                        }
                        dialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setPositiveButton("取 消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    //选择日期的dialog
    public void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
        datePickerDialog.setVibrate(true);
        datePickerDialog.setYearRange(1985, 2028);
        datePickerDialog.setCloseOnSingleTapDay(false);
        datePickerDialog.show(getSupportFragmentManager(), "birthday");
        datePickerDialog.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        entity.setBIRTHDAY(year + "年" + (month + 1) + "月" + day + "日");
        adapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int nameRow;
    private int sexRow;
    private int professionRow;
    private int birthdayRow;
    private int lineRow;
    private int detailRow;
    private int detailIntroduceRow;

    private void setRow() {
        nameRow = rowCount++;
        sexRow = rowCount++;
        professionRow = rowCount++;
        birthdayRow = rowCount++;
        lineRow = rowCount++;
        detailRow = rowCount++;
        detailIntroduceRow = rowCount++;
    }

    class AccountAdapter extends BaseAdapter {

        private Context context;
        private String[] dataStr;

        public AccountAdapter(Context context) {
            this.context = context;
            dataStr = new String[]{"姓名", "性别", "职业",
                    "出生日期", "", "详细信息",
                    "完善详细信息我们更好的为您提供用药咨询和促销信息"};
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            return position != lineRow && position != detailIntroduceRow;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == lineRow) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            String key = dataStr[position];
            if (type == 0) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(context);
                }
            } else if (type == 1) {
                if (convertView == null) {
                    convertView = new AccountCell(context);
                }
                AccountCell cell = (AccountCell) convertView;
                cell.setBackgroundColor(Color.WHITE);

                if (position == nameRow) {
                    cell.setKeyAndValue(key, entity.getPERSONNAME(), R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == sexRow) {
                    if (entity.getGENDER() != null) {
                        String sex;
                        if (entity.getGENDER().equals("1")) {
                            sex = "男";
                        } else if (entity.getGENDER().equals("2")) {
                            sex = "女";
                        } else {
                            sex = "保密";
                        }
                        cell.setKeyAndValue(key, sex, R.drawable.ic_chevron_right_grey600_24dp, true);
                    } else {
                        cell.setKeyAndValue(key, R.drawable.ic_chevron_right_grey600_24dp, true);
                    }
                } else if (position == professionRow) {
                    cell.setKeyAndValue(key, entity.getJOB(), R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == birthdayRow) {
                    cell.setKeyAndValue(key, entity.getBIRTHDAY(), R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == detailRow) {
                    cell.setKeyAndValue(key, R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == detailIntroduceRow) {
                    cell.setKey(key, true);
                    cell.setKeyTxtColor(R.color.theme_sub_title);
                }
            }

            return convertView;
        }
    }
}
