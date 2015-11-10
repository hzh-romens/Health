package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ChoiceEntity;
import com.romens.yjk.health.ui.adapter.PopAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HZH on 2015/10/30.
 */
public class SetDiseaseActivity extends BaseActivity {
    private ListView listView;
    private ImageView btn_back;
    private PopAdapter popAdapter;
    private List<ChoiceEntity> result;
    private EditText edit_disease;
    private String dieaseValue;
    private String flag;
    private String value;
    private boolean choiceFlag;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_disease);
        flag = getIntent().getStringExtra("flag");
        value = getIntent().getStringExtra("value");
        dieaseValue = value;
        tv_title = (TextView) findViewById(R.id.title);
        btn_back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listView);
        edit_disease = (EditText) findViewById(R.id.edit_disease);
        setData();
        popAdapter = new PopAdapter(this, result);
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
                if ("无".equals(choice)) {
                    edit_disease.setEnabled(false);
                    choiceFlag = false;
                    dieaseValue = choice;
                } else {
                    choiceFlag = true;
                    edit_disease.setEnabled(true);
                    if (edit_disease.getText().toString().length() == 0) {
                        dieaseValue = choice;
                    } else {
                        dieaseValue = choice + "," + edit_disease.getText().toString();
                    }
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choiceFlag) {
                    if (edit_disease.getText().toString().length() != 0) {
                        dieaseValue = dieaseValue + "(" + edit_disease.getText().toString() + ")";
                    }
                }
                if ("".equals(dieaseValue) || dieaseValue == null) {
                    dieaseValue = "无";
                }
                if ("allergy".equals(flag)) {
                    Intent it = new Intent();
                    it.putExtra("allergyvalue", dieaseValue);
                    setResult(8, it);
                    finish();

                } else if ("Heredopathia".equals(flag)) {
                    Intent it = new Intent();
                    it.putExtra("Heredopathiavalue", dieaseValue);
                    setResult(7, it);
                    finish();
                } else if ("diease".equals(flag)) {
                    Intent it = new Intent();
                    it.putExtra("dieasevalue", dieaseValue);
                    setResult(3, it);
                    finish();
                }

            }
        });
    }

    private void setData() {
        result = new ArrayList<ChoiceEntity>();
        ChoiceEntity ce1 = new ChoiceEntity();
        ChoiceEntity ce2 = new ChoiceEntity();
        if ("allergy".equals(flag)) {
            tv_title.setText("是否过敏");
            edit_disease.setHint("若过敏请详情描述");
        } else if ("Heredopathia".equals(flag)) {
            tv_title.setText("有无遗传病");
        } else if ("diease".equals(flag)) {
            tv_title.setText("有无疾病");
        }
        if ("allergy".equals(flag)) {
            if ("".equals(value)) {
                ce1.setChoice("是");
                ce1.setFlag(false);
                result.add(ce1);

                ce2.setChoice("否");
                ce2.setFlag(false);
                result.add(ce2);
            } else if ("是".equals(value)) {
                ce1.setChoice("是");
                ce1.setFlag(true);
                result.add(ce1);

                ce2.setChoice("否");
                ce2.setFlag(false);
                result.add(ce2);
                edit_disease.setEnabled(true);
            } else {
                ce1.setChoice("是");
                ce1.setFlag(false);
                result.add(ce1);

                ce2.setChoice("否");
                ce2.setFlag(true);
                result.add(ce2);
            }


        } else {
            if ("".equals(value)) {

                ce1.setChoice("有");
                ce1.setFlag(false);
                result.add(ce1);

                ce2.setChoice("无");
                ce2.setFlag(false);
                result.add(ce2);
            } else if ("有".equals(value)) {
                ce1.setChoice("有");
                ce1.setFlag(true);
                result.add(ce1);

                ce2.setChoice("无");
                ce2.setFlag(false);
                result.add(ce2);

                edit_disease.setEnabled(true);
            } else {
                ce1.setChoice("有");
                ce1.setFlag(false);
                result.add(ce1);

                ce2.setChoice("无");
                ce2.setFlag(true);
                result.add(ce2);
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if ("allergy".equals(flag)) {
                Intent it = new Intent();
                it.putExtra("allergyvalue", dieaseValue);
                setResult(8, it);
                finish();

            } else if ("Heredopathia".equals(flag)) {
                Intent it = new Intent();
                it.putExtra("Heredopathiavalue", dieaseValue);
                setResult(7, it);
                finish();
            } else if ("diease".equals(flag)) {
                Intent it = new Intent();
                it.putExtra("dieasevalue", dieaseValue);
                setResult(3, it);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
