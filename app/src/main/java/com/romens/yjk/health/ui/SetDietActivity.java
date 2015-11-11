package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.yjk.health.R;

/**
 * Created by AUSU on 2015/10/31.
 */
public class SetDietActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private CheckBox cb_dy,cb_dz,cb_dt,cb_gy,cb_gz,cb_gt;
    private ImageView btn_back;
    private String[] hobby;
    private  String flag;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setdiet);
        flag = getIntent().getStringExtra("flag");
        hobby=new String[6];
        initView();
    }

    private void initView() {
        cb_dy= (CheckBox) findViewById(R.id.cb1);
        cb_dz= (CheckBox) findViewById(R.id.cb2);
        cb_dt=(CheckBox) findViewById(R.id.cb3);
        cb_gy= (CheckBox) findViewById(R.id.cb4);
        cb_gz= (CheckBox) findViewById(R.id.cb5);
        cb_gt= (CheckBox) findViewById(R.id.cb6);
        tv_title= (TextView) findViewById(R.id.title);
        //Distinguishing mark(flag)
        if("diet".equals(flag)){
            cb_dy.setText("低盐");
            cb_dz.setText("低脂");
            cb_dt.setText("低糖");
            cb_gy.setText("高盐");
            cb_gz.setText("高脂");
            cb_gt.setText("高糖");
            tv_title.setText("饮食习惯选择");

        }else if("rest".equals(flag)){
            cb_dy.setText("早起早睡");
            cb_dz.setText("早起晚睡");
            cb_dt.setText("晚起早睡");
            cb_gy.setText("晚起晚睡");
            cb_gz.setText("熬夜经常");
            cb_gt.setText("作息不规律");
            tv_title.setText("作息习惯选择");
        }else if("other".equals(flag)){
            tv_title.setText("其他习惯选择");
            cb_dy.setText("抽烟");
            cb_dz.setText("喝酒");
            cb_dt.setText("不吃早餐");
            cb_gy.setText("饱食");
            cb_gz.setVisibility(View.GONE);
            cb_gt.setVisibility(View.GONE);
        }
        cb_dt.setOnCheckedChangeListener(this);
        cb_dy.setOnCheckedChangeListener(this);
        cb_dz.setOnCheckedChangeListener(this);
        cb_gy.setOnCheckedChangeListener(this);
        cb_gt.setOnCheckedChangeListener(this);
        cb_gz.setOnCheckedChangeListener(this);
        btn_back= (ImageView) findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < hobby.length; i++) {
                    if(hobby[i]!=null){
                        sb.append(hobby[i]+",");
                        hobby[i]=null;
                    }
                }
                String result = null;
                if("diet".equals(flag)) {
                    if (!sb.toString().equals("")) {
                        result = sb.toString();
                        Intent it = new Intent();
                        it.putExtra("foodvalue", result);
                        setResult(4, it);
                        finish();
                    } else {
                        Intent it = new Intent();
                        it.putExtra("foodvalue", "");
                        setResult(4, it);
                        finish();
                    }
                }else if("rest".equals(flag)){
                    if (!sb.toString().equals("")) {
                        result = sb.toString();
                        Intent it = new Intent();
                        it.putExtra("restvalue", result);
                        setResult(5, it);
                        finish();
                    } else {
                        Intent it = new Intent();
                        it.putExtra("restvalue", "");
                        setResult(5, it);
                        finish();
                    }
                }else if("other".equals(flag)){
                    if (!sb.toString().equals("")) {
                        result = sb.toString();
                        Intent it = new Intent();
                        it.putExtra("othervalue", result);
                        setResult(6, it);
                        finish();
                    } else {
                        Intent it = new Intent();
                        it.putExtra("othervalue", "");
                        setResult(6, it);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cb1:
                if(isChecked){
                    hobby[0]=cb_dy.getText().toString();
                }else{
                    hobby[0]=null;
                }
                break;
            case R.id.cb2:
                if(isChecked){
                    hobby[1]=cb_dz.getText().toString();
                }else{
                    hobby[1]=null;
                }
                break;
            case R.id.cb3:
                if(isChecked){
                    hobby[2]=cb_dt.getText().toString();
                }else{
                    hobby[2]=null;
                }
                break;
            case R.id.cb4:
                if(isChecked){
                    hobby[3]=cb_gy.getText().toString();
                }else{
                    hobby[3]=null;
                }
                break;
            case R.id.cb5:
                if(isChecked){
                    hobby[4]=cb_gz.getText().toString();
                }else{
                    hobby[4]=null;
                }
                break;
            case R.id.cb6:
                if(isChecked){
                    hobby[5]=cb_gt.getText().toString();
                }else{
                    hobby[5]=null;
                }
                break;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hobby.length; i++) {
                if(hobby[i]!=null){
                    sb.append(hobby[i]+",");
                    hobby[i]=null;
                }
            }
            String result = null;
            if("diet".equals(flag)) {
                if (!sb.toString().equals("")) {
                    result = sb.toString();
                    Intent it = new Intent();
                    it.putExtra("foodvalue", result);
                    setResult(4, it);
                    finish();
                } else {
                    Intent it = new Intent();
                    it.putExtra("foodvalue", "");
                    setResult(4, it);
                    finish();
                }
            }else if("rest".equals(flag)){
                if (!sb.toString().equals("")) {
                    result = sb.toString();
                    Intent it = new Intent();
                    it.putExtra("restvalue", result);
                    setResult(5, it);
                    finish();
                } else {
                    Intent it = new Intent();
                    it.putExtra("restvalue", "");
                    setResult(5, it);
                    finish();
                }
            }else if("other".equals(flag)){
                if (!sb.toString().equals("")) {
                    result = sb.toString();
                    Intent it = new Intent();
                    it.putExtra("othervalue", result);
                    setResult(6, it);
                    finish();
                } else {
                    Intent it = new Intent();
                    it.putExtra("othervalue", "");
                    setResult(6, it);
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
