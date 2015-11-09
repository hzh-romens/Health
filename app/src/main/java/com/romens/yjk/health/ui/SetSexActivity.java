package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ChoiceEntity;
import com.romens.yjk.health.ui.adapter.PopAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HZH on 2015/10/30.
 */
public class SetSexActivity extends BaseActivity{
    private ListView listView;
    private ImageView btn_back;
    private PopAdapter popAdapter;
    private List<ChoiceEntity> result;
    private String sexValue;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setsex);
        value=getIntent().getStringExtra("value");
        sexValue=value;
        listView= (ListView) findViewById(R.id.listView);
        btn_back= (ImageView) findViewById(R.id.back);
        getData();
        popAdapter=new PopAdapter(this,result);
        listView.setAdapter(popAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < result.size(); i++) {
                    if (i != position) {
                        result.get(i).setFlag(false);
                    }
                }
                result.get(position).setFlag(!result.get(position).isFlag());
                popAdapter.notifyDataSetChanged();
                String choice = result.get(position).getChoice();
                sexValue=choice;
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.putExtra("sexvalue",sexValue);
                setResult(2, it);
                finish();
            }
        });
    }

    private void getData() {
        result=new ArrayList<ChoiceEntity>();
        ChoiceEntity ce1 = new ChoiceEntity();
        ChoiceEntity ce2 = new ChoiceEntity();
        ChoiceEntity ce3 = new ChoiceEntity();
        if ("男".equals(value)) {
            ce1.setChoice("男");
            ce1.setFlag(true);
            result.add(ce1);

            ce2.setChoice("女");
            ce2.setFlag(false);
            result.add(ce2);

            ce3.setChoice("保密");
            ce3.setFlag(false);
            result.add(ce3);
        }else if("女".equals(value)){
            ce1.setChoice("男");
            ce1.setFlag(false);
            result.add(ce1);

            ce2.setChoice("女");
            ce2.setFlag(true);
            result.add(ce2);

            ce3.setChoice("保密");
            ce3.setFlag(false);
            result.add(ce3);
        }else if("保密".equals(value)){
            ce1.setChoice("男");
            ce1.setFlag(false);
            result.add(ce1);

            ce2.setChoice("女");
            ce2.setFlag(false);
            result.add(ce2);

            ce3.setChoice("保密");
            ce3.setFlag(true);
            result.add(ce3);
        }else{
            ce1.setChoice("男");
            ce1.setFlag(false);
            result.add(ce1);

            ce2.setChoice("女");
            ce2.setFlag(false);
            result.add(ce2);

            ce3.setChoice("保密");
            ce3.setFlag(false);
            result.add(ce3);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent it = new Intent();
            it.putExtra("sexvalue",sexValue);
            setResult(2, it);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
