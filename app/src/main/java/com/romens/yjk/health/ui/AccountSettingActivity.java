package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.romens.android.ui.cells.LoadingCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextInfoPrivacyCell;
import com.romens.android.ui.cells.TextSettingSelectCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.PersonalEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by anlc on 2015/12/21.
 */
public class AccountSettingActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

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
        actionBarMenu.addItem(0, R.drawable.ic_done);
        actionBar.setTitle("个人信息");
        entity = new PersonalEntity();
        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setSelection(R.drawable.list_selector);
        listView.setVerticalScrollBarEnabled(false);
        content.addView(listView);
        adapter = new AccountAdapter(this);
        listView.setAdapter(adapter);

        updateData();
        initData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == nameRow) {
                    UIOpenHelper.openEditActivityForEditName(AccountSettingActivity.this);
                } else if (position == sexRow) {
                    chooseSex();
                } else if (position == professionRow) {
                    UIOpenHelper.openEditActivityForEditProfession(AccountSettingActivity.this);
                } else if (position == birthdayRow) {
                    showDatePickerDialog();
                } else if (position == detailRow) {
                    UIOpenHelper.openUserLabelsActivity(AccountSettingActivity.this);
                }
            }
        });
    }

    private void changeLoadingUserInfo(boolean progress) {
        loadingUserInfo = progress;
        updateData();
    }

    //获取数据
    private void initData() {
        changeLoadingUserInfo(true);
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserInfo", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol).build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {

            @Override
            public void onTokenTimeout(Message msg) {
                changeLoadingUserInfo(false);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                changeLoadingUserInfo(false);
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    Gson gson = new Gson();
                    entity = gson.fromJson(response, PersonalEntity.class);
                    updateData();
                } else {
                    Log.e("person message", errorMsg.msg);
                }
            }
        });

    }


    //保存信息
    private void SaveInfor() {
        needShowProgress("正在更新个人信息");
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
                        Toast.makeText(AccountSettingActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AccountSettingActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
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
        adapter.notifyDataSetChanged();
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
                    if (result.equals("")) {
                        entity.setFOODHOBBY(result);
                    } else {
                        entity.setFOODHOBBY(result.substring(0, result.length() - 1));
                    }
                } else if ("habit".equals(jsonObject.getString("key"))) {
                    String result = "";
                    for (int j = 0; j < valuesDesc.length(); j++) {
                        result += valuesDesc.getString(j) + ",";
                    }
                    if (result.equals("")) {
                        entity.setSLEEPHOBBY(result);
                    } else {
                        entity.setSLEEPHOBBY(result.substring(0, result.length() - 1));
                    }
                } else if ("other".equals(jsonObject.getString("key"))) {
                    String result = "";
                    for (int j = 0; j < valuesDesc.length(); j++) {
                        result += valuesDesc.getString(j) + ",";
                    }
                    if (result.equals("")) {
                        entity.setOTHER(result);
                    } else {
                        entity.setOTHER(result.substring(0, result.length() - 1));
                    }
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
                        updateData();
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
        updateData();
    }

    private int rowCount;
    private int nameRow;
    private int sexRow;
    private int professionRow;
    private int birthdayRow;
    private int lineRow;
    private int detailRow;
    private int detailIntroduceRow;

    private int loadingUserInfoRow;

    private boolean loadingUserInfo = false;

    private void updateData() {
        rowCount = 0;
        if (loadingUserInfo) {
            loadingUserInfoRow = rowCount++;
            nameRow = -1;
            sexRow = -1;
            professionRow = -1;
            birthdayRow = -1;
            lineRow = -1;
            detailRow = -1;
            detailIntroduceRow = -1;
        } else {
            loadingUserInfoRow = -1;
            nameRow = rowCount++;
            sexRow = rowCount++;
            professionRow = rowCount++;
            birthdayRow = rowCount++;
            lineRow = rowCount++;
            detailRow = rowCount++;
            detailIntroduceRow = rowCount++;
        }

        adapter.notifyDataSetChanged();
    }

    class AccountAdapter extends BaseAdapter {

        private Context context;

        public AccountAdapter(Context context) {
            this.context = context;
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
            if (position == lineRow || position == detailIntroduceRow || position == loadingUserInfoRow) {
                return false;
            }
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == lineRow) {
                return 0;
            } else if (position == detailIntroduceRow) {
                return 2;
            } else if (position == loadingUserInfoRow) {
                return 3;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(context);
                }
            } else if (type == 1) {
                if (convertView == null) {
                    convertView = new TextSettingSelectCell(context);
                }
                TextSettingSelectCell cell = (TextSettingSelectCell) convertView;
                cell.setValueTextColor(ResourcesConfig.bodyText2);
                if (position == nameRow) {
                    cell.setTextAndValue("姓名", entity == null ? "" : entity.getPERSONNAME(), true, true);
                } else if (position == sexRow) {
                    if (entity != null && entity.getGENDER() != null) {
                        String sex;
                        if (entity.getGENDER().equals("1")) {
                            sex = "男";
                        } else if (entity.getGENDER().equals("2")) {
                            sex = "女";
                        } else {
                            sex = "保密";
                        }
                        cell.setTextAndValue("性别", sex, true, true);
                    } else {
                        cell.setTextAndValue("性别", "请选择...", true, true);
                    }
                } else if (position == professionRow) {
                    cell.setTextAndValue("职业", entity == null ? "" : entity.getJOB(), true, true);
                } else if (position == birthdayRow) {
                    cell.setTextAndValue("出生日期", entity == null ? "" : entity.getBIRTHDAY(), true, true);
                } else if (position == detailRow) {
                    cell.setTextAndValue("详细信息", "", true, true);
                }
            } else if (type == 2) {
                if (convertView == null) {
                    convertView = new TextInfoPrivacyCell(context);
                }
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) convertView;
                if (position == detailIntroduceRow) {
                    cell.setText("完善详细信息我们更好的为您提供用药咨询和促销信息");
                }
            } else if (type == 3) {
                if (convertView == null) {
                    convertView = new LoadingCell(context);
                }
            }

            return convertView;
        }
    }
}
