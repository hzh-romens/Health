package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.romens.yjk.health.R;

/**
 * Created by AUSU on 2015/10/31.
 */
public class SetDietActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private CheckBox cb_dy,cb_dz,cb_dt,cb_gy,cb_gz,cb_gt;
    private ImageView btn_back;
    private String[] hobby;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setdiet);
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
                if (!sb.toString().equals("")) {
                    result = sb.toString();
                  //  result = result.substring(0, result.lastIndexOf(","));
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
            if (!sb.toString().equals("")) {
                result = sb.toString();
                //  result = result.substring(0, result.lastIndexOf(","));
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
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
