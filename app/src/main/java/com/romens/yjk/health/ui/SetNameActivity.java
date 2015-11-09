package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2015/10/30.
 * 修改姓名
 */
public class SetNameActivity extends BaseActivity{
    private EditText edit_name;
    private ImageView btn_clear,btn_back;
    private String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setname);
        value=getIntent().getStringExtra("value");
        btn_back= (ImageView) findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.putExtra("namevalue", edit_name.getText().toString());
                setResult(1, it);
                finish();
            }
        });
        edit_name= (EditText) findViewById(R.id.edit_name);
        edit_name.setText(value);
        btn_clear= (ImageView) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_name.getText().toString().length()==0){
                    return ;
                }else{
                    edit_name.setText("");
                }
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent it = new Intent();
            it.putExtra("namevalue", edit_name.getText().toString());
            setResult(1, it);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
