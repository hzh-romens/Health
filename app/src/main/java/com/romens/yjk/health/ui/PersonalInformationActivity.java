package com.romens.yjk.health.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.model.ChoiceEntity;
import com.romens.yjk.health.model.PersonalEntity;
import com.romens.yjk.health.model.PersonalInformationEntity;
import com.romens.yjk.health.ui.adapter.PersonalInformationAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import widget.OnWheelChangedListener;
import widget.WheelView;
import widget.adapters.ArrayWheelAdapter;

/**
 * Created by HZH on 2015/10/24.
 */
public class PersonalInformationActivity extends BaseActivity implements View.OnClickListener, OnWheelChangedListener, View.OnFocusChangeListener {
    private ImageView iv_back;
    private TextView choiceSex, choiceBirthday, choiceHeredopathia, choiceDisease, choiceAllergy,editor_name,editor_food;
    private EditText editor_work, editor_rest, editor_other;
    private LinearLayout btn_save;
    private PersonalEntity personalEntity;
    private WheelView YearView, MonthView, DayView;
    private TextView btn_commit;

    private PersonalInformationAdapter inforAdapter;

    private String[] Year;
    private String[] Month;
    private String[] Day;

    private Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        needShowProgress("正在加载");
        cal = Calendar.getInstance();
        initView();
        initData();

    }

    private void setValue() {
        if ("".equals(personalEntity.getGENDER()) || personalEntity.getGENDER() == null) {
            choiceSex.setText("");
        } else if ("1".equals(personalEntity.getGENDER())) {
            choiceSex.setText("男");
        } else if ("2".equals(personalEntity.getGENDER())) {
            choiceSex.setText("女");
        }

        if ("".equals(personalEntity.getBIRTHDAY()) || personalEntity == null) {
            choiceBirthday.setText("");
        } else {
            choiceBirthday.setText(personalEntity.getBIRTHDAY());
        }

        if ("".equals(personalEntity.getHASINHERITED()) || personalEntity.getHASINHERITED() == null) {
            choiceHeredopathia.setText("");
        } else if ("1".equals(personalEntity.getHASINHERITED())) {
            choiceHeredopathia.setText("有");
        } else if ("0".equals(personalEntity.getHASINHERITED())) {
            choiceHeredopathia.setText("无");
        }

        if ("".equals(personalEntity.getHASSERIOUS()) || personalEntity.getHASSERIOUS() == null) {
            choiceDisease.setText("");
        } else if ("1".equals(personalEntity.getHASSERIOUS())) {
            choiceDisease.setText("有");
        } else if ("0".equals(personalEntity.getHASSERIOUS())) {
            choiceDisease.setText("无");
        }

        if ("".equals(personalEntity.getHASGUOMIN()) || personalEntity.getHASGUOMIN() == null) {
            choiceAllergy.setText("");
        } else if ("1".equals(personalEntity.getHASGUOMIN())) {
            choiceAllergy.setText("有");
        } else if ("0".equals(personalEntity.getHASGUOMIN())) {
            choiceAllergy.setText("无");
        }

        if ("".equals(personalEntity.getPERSONNAME()) || personalEntity.getPERSONNAME() == null) {
            editor_name.setText("");
        } else {
            editor_name.setText(personalEntity.getPERSONNAME());
        }
        if ("".equals(personalEntity.getJOB()) || personalEntity.getJOB() == null) {
            editor_work.setText("");
        } else {
            editor_work.setText(personalEntity.getJOB());
        }
        if ("".equals(personalEntity.getFOODHOBBY()) || personalEntity.getFOODHOBBY() == null) {
            editor_food.setText("");
        } else {
            editor_food.setText(personalEntity.getFOODHOBBY());
        }
        if ("".equals(personalEntity.getSLEEPHOBBY()) || personalEntity.getSLEEPHOBBY() == null) {
            editor_rest.setText("");
        } else {
            editor_rest.setText(personalEntity.getSLEEPHOBBY());
        }

        if ("".equals(personalEntity.getOTHER()) || personalEntity.getOTHER() == null) {
            editor_other.setText("");
        } else {
            editor_other.setText(personalEntity.getOTHER());
        }
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
                Log.e("个人信息", msg.msg);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    Gson gson = new Gson();
                    personalEntity = gson.fromJson(response, PersonalEntity.class);
                    setValue();
                } else {
                    Log.e("个人信息", errorMsg.msg);
                }
            }
        });

    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        choiceAllergy = (TextView) findViewById(R.id.choiceAllergy);
        choiceAllergy.setOnClickListener(this);
        choiceBirthday = (TextView) findViewById(R.id.choiceBirthday);
        choiceBirthday.setOnClickListener(this);
        choiceDisease = (TextView) findViewById(R.id.choiceDisease);
        choiceDisease.setOnClickListener(this);
        choiceHeredopathia = (TextView) findViewById(R.id.choiceHeredopathia);
        choiceHeredopathia.setOnClickListener(this);
        choiceSex = (TextView) findViewById(R.id.choiceSex);
        choiceSex.setOnClickListener(this);
        editor_name = (TextView) findViewById(R.id.editor_name);
        editor_work = (EditText) findViewById(R.id.editor_work);
        editor_food = (TextView) findViewById(R.id.editor_food);
        editor_rest = (EditText) findViewById(R.id.editor_rest);
        editor_other = (EditText) findViewById(R.id.editor_other);
        btn_save = (LinearLayout) findViewById(R.id.btn_save);
        editor_food.setOnClickListener(this);
        editor_other.setOnFocusChangeListener(this);
        editor_rest.setOnFocusChangeListener(this);
        editor_work.setOnFocusChangeListener(this);
        editor_name.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_commit= (TextView) findViewById(R.id.btn_commit);
        btn_commit.setText("保存");

    }

    private String[] has;
    private String[] sex;

    @Override
    public void onClick(View v) {

        has = new String[2];
        has[0] = "有";
        has[1] = "无";

        sex = new String[2];
        sex[0] = "男";
        sex[1] = "女";

        switch (v.getId()) {
            case R.id.choiceAllergy:
                btn_save.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_goorder));
                btn_commit.setText("提交");
                getPopWindowInstance(has, choiceAllergy, 0);
                break;
            case R.id.choiceBirthday:
                btn_save.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_goorder));
                btn_commit.setText("提交");
                getPopWindowInstance(has, choiceBirthday, 1);
                break;
            case R.id.choiceDisease:
             //   btn_save.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_goorder));
               // btn_commit.setText("提交");
                //跳转到病史选择页面
                //getPopWindowInstance(has, choiceDisease, 0);
                Intent dieaseIntent=new Intent(this,SetDiseaseActivity.class);
                startActivityForResult(dieaseIntent, 3);
                break;
            case R.id.choiceHeredopathia:
                btn_save.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_goorder));
                btn_commit.setText("提交");
                getPopWindowInstance(has, choiceHeredopathia, 0);

                break;
            case R.id.choiceSex:
               // btn_save.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_goorder));
                //btn_commit.setText("提交");
                //跳转到性别选择页面
               // getPopWindowInstance(sex, choiceSex, 0);
                Intent sexIntent=new Intent(this,SetSexActivity.class);
                startActivityForResult(sexIntent,2);
                break;
            case R.id.editor_name:
                Intent nameIntent=new Intent(this,SetNameActivity.class);
                startActivityForResult(nameIntent,1);
                break;
            case R.id.editor_food:
                Intent foodIntent=new Intent(this,SetDietActivity.class);
                startActivityForResult(foodIntent,4);
                break;
            case R.id.btn_save:
                try {
                    String resTypeName = getResources().getResourceTypeName(R.id.btn_save);
                    String resEntryName = getResources().getResourceEntryName(R.id.btn_save);
                    Context apk = createPackageContext(MyApplication.applicationContext.getPackageName(),
                            Context.CONTEXT_IGNORE_SECURITY);
                    int drawavleId = apk.getResources().getIdentifier(resEntryName, resTypeName,
                            apk.getPackageName());
                    if ("提交".equals(btn_commit.getText().toString())) {
                        //可以点击
                        SaveInfor();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    private void SaveInfor() {
        personalEntity.setPERSONNAME(editor_name.getText().toString());
        personalEntity.setBIRTHDAY(choiceBirthday.getText().toString());
        if ("男".equals(choiceSex.getText().toString())) {
            personalEntity.setGENDER("1");
        } else {
            personalEntity.setGENDER("2");
        }
        if ("有".equals(choiceDisease.getText().toString())) {
            personalEntity.setHASSERIOUS("1");
        } else {
            personalEntity.setHASSERIOUS("0");
        }
        if ("有".equals(choiceHeredopathia.getText().toString())) {
            personalEntity.setHASINHERITED("1");
        } else {
            personalEntity.setHASINHERITED("0");
        }

        if ("有".equals(choiceAllergy.getText().toString())) {
            personalEntity.setHASGUOMIN("1");
        } else {
            personalEntity.setHASGUOMIN("0");
        }
        personalEntity.setFOODHOBBY(editor_food.getText().toString());
        personalEntity.setJOB(editor_work.getText().toString());
        personalEntity.setOTHER(editor_other.getText().toString());
        personalEntity.setSLEEPHOBBY(editor_rest.getText().toString());
        Gson gson=new Gson();
        final String jsonData = gson.toJson(personalEntity);
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid())
                .put("JSONDATA",jsonData)
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
                        JSONObject jsonObject=new JSONObject(response);
                        if ("yes".equals(jsonObject.getString("success"))){
                            Toast.makeText(PersonalInformationActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("个人信息错误日志",errorMsg.msg);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                String namevalue = data.getStringExtra("namevalue");
                editor_name.setText(namevalue);
                break;
            case 2:
                String sexValue = data.getStringExtra("sexvalue");
                choiceSex.setText(sexValue);
                break;
            case 3:
                String dieasevalue = data.getStringExtra("dieasevalue");
                choiceDisease.setText(dieasevalue);
                break;
            case 4:
                String foodvalue = data.getStringExtra("foodvalue");
                editor_food.setText(foodvalue);
                break;
        }
    }

    // 获取PopWindow实例 保持一个实例
    private void getPopWindowInstance(String[] data, TextView showAsView, int flag) {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopWindow(data, showAsView, flag);
        }
    }

    private PopupWindow mPopupWindow;
    private int mScreenwidth;
    private int mScreenHeight;

    // 创建PopupWindow
    @SuppressWarnings("deprecation")
    private List<ChoiceEntity> choiceDatas;

    private void initPopWindow(final String[] data, final TextView showAsView, int flag) {
        mScreenwidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        // 创建一个PopupWindow 并设置宽高
        // 参数1：contentView 指定PopupWindow的内容
        // 参数2：width 指定PopupWindow的width
        // 参数3：height 指定PopupWindow的height
        if (flag == 0) {
            View view = View.inflate(this, R.layout.popwindows_selector, null);
            TextView popwindow_close = (TextView) view.findViewById(R.id.popwindow_close);
            TextView popwindow_complete = (TextView) view.findViewById(R.id.popwindow_complete);
            final WheelView wheelView = (WheelView) view.findViewById(R.id.wheelview);
            wheelView.addChangingListener(this);
            wheelView.setBackgroundColor(Color.WHITE);
            wheelView.setViewAdapter(new ArrayWheelAdapter<String>(this, data));
            wheelView.setVisibleItems(3);
            wheelView.setCurrentItem(0);
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.showAtLocation(showAsView, Gravity.BOTTOM, 0, 0);
            // #e0000000  半透明颜色
            mPopupWindow.setAnimationStyle(R.anim.choose_citys_anim);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(false);
            popwindow_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            });
            popwindow_complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectInfo = data[wheelView.getCurrentItem()];
                    showAsView.setText(selectInfo);
                    mPopupWindow.setAnimationStyle(R.anim.choose_citys_anim_exit);
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            });
            popwindow_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            });
            popwindow_complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showAsView == choiceDisease) {
                        choiceDisease.setText(has[wheelView.getCurrentItem()]);
                    } else if (showAsView == choiceAllergy) {
                        choiceAllergy.setText(has[wheelView.getCurrentItem()]);
                    } else if (showAsView == choiceHeredopathia) {
                        choiceHeredopathia.setText(has[wheelView.getCurrentItem()]);
                    } else if (showAsView == choiceSex) {
                        choiceSex.setText(sex[wheelView.getCurrentItem()]);
                    }
                    mPopupWindow.setAnimationStyle(R.anim.choose_citys_anim_exit);
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            });

        } else {
            //日期选择
            View view = View.inflate(this, R.layout.popwindow_selector_time, null);
            TextView popwindow_close = (TextView) view.findViewById(R.id.popwindow_close);
            TextView popwindow_complete = (TextView) view.findViewById(R.id.popwindow_complete);
            YearView = (WheelView) view.findViewById(R.id.YearView);
            YearView.setBackgroundColor(Color.WHITE);
            SetYearValue();

            MonthView = (WheelView) view.findViewById(R.id.MonthView);
            MonthView.setBackgroundColor(Color.WHITE);
           setMonthValue();

            DayView = (WheelView) view.findViewById(R.id.DayView);
            DayView.setBackgroundColor(Color.WHITE);
            setDayValue(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);

            YearView.addChangingListener(this);
            MonthView.addChangingListener(this);
            DayView.addChangingListener(this);

            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.showAtLocation(showAsView, Gravity.BOTTOM, 0, 0);
            mPopupWindow.setAnimationStyle(R.anim.choose_citys_anim);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(false);
            popwindow_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            });
            popwindow_complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectInfo = Year[YearView.getCurrentItem()] + Month[MonthView.getCurrentItem()] + Day[DayView.getCurrentItem()];
                    choiceBirthday.setText(selectInfo);
                    mPopupWindow.setAnimationStyle(R.anim.choose_citys_anim_exit);
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            });
        }

    }

    //设置日期数据
    private void setDayValue(int Year, int Month) {
       // cal.clear();    //在使用set方法之前，必须先clear一下，否则很多信息会继承自系统当前时间
        cal.set(Calendar.YEAR, Year);
        cal.set(Calendar.MONTH, Month - 1);  //Calendar对象默认一月为0
        int endday = cal.getActualMaximum(Calendar.DAY_OF_MONTH);//获得本月天数
        Day = new String[endday];
        for (int i = 0; i < Day.length; i++) {
            Day[i] = (i + 1) + "日";
        }
        DayView.setViewAdapter(new ArrayWheelAdapter<String>(this, Day));
        DayView.setVisibleItems(7);
    }

    //设置月
    private void setMonthValue() {
        Month = new String[12];
        for (int i = 0; i < 12; i++) {
            Month[i] = (i + 1) + "月";
        }
        MonthView.setViewAdapter(new ArrayWheelAdapter<String>(this, Month));
        MonthView.setVisibleItems(7);
        int month = cal.get(Calendar.MONTH);
        int CurrentMonth = 0;
        for (int i = 0; i < Month.length; i++) {
            if (Integer.parseInt(Month[i].substring(0, Month[i].indexOf("月"))) == month+1) {
                CurrentMonth = i;
            }
        }

        MonthView.setCurrentItem(CurrentMonth);
    }

    //设置年份(100年)
    private void SetYearValue() {
        Year = new String[121];
        int length = 120;
        for (int i = 0; i < Year.length; i++) {
            int num = length - i;
            Year[i] = (2015 - num) + "年";
        }
        YearView.setViewAdapter(new ArrayWheelAdapter<String>(this, Year));
        YearView.setVisibleItems(7);
        int year = cal.get(Calendar.YEAR);

        int CurrentYear = 0;
        for (int i = 0; i < Year.length; i++) {
            if (Integer.parseInt(Year[i].substring(0, Year[i].indexOf("年"))) == year) {
                CurrentYear = i;
            }
        }

        YearView.setCurrentItem(CurrentYear);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (YearView!=null&&MonthView!=null&&DayView!=null) {
            String choiceYear = Year[YearView.getCurrentItem()];
            String choiceMonth = Month[MonthView.getCurrentItem()];
            setDayValue(Integer.parseInt(choiceYear.substring(0, choiceYear.indexOf("年"))), Integer.parseInt(choiceMonth.substring(0, choiceMonth.indexOf("月"))));
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.editor_other:
                if (hasFocus) {
                    btn_save.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_goorder));
                    btn_commit.setText("提交");
                }
                break;
            case R.id.editor_rest:
                if (hasFocus) {
                    btn_save.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_goorder));
                    btn_commit.setText("提交");
                }
                break;
            case R.id.editor_work:
                if (hasFocus) {
                    btn_save.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_goorder));
                    btn_commit.setText("提交");
                }
                break;
        }
    }
}
