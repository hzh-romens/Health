package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.romens.yjk.health.R;
import com.romens.yjk.health.model.PersonalInformationEntity;
import com.romens.yjk.health.ui.adapter.PersonalInformationAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HZH on 2015/10/24.
 */
public class PersonalInformationActivity extends BaseActivity{
    private ListView listView;
    private ImageView iv_back;

    private PersonalInformationAdapter inforAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initView();
        initData();

    }

    private void initData() {
        List<PersonalInformationEntity> data=new ArrayList<PersonalInformationEntity>();
        data.add(new PersonalInformationEntity("基本信息",0));
        data.add(new PersonalInformationEntity("姓名",1));
        data.add(new PersonalInformationEntity("性别",2));
        data.add(new PersonalInformationEntity("职业",1));
        data.add(new PersonalInformationEntity("出生年月",2));
        data.add(new PersonalInformationEntity("详细信息",0));
        data.add(new PersonalInformationEntity("有无遗传病史",2));
        data.add(new PersonalInformationEntity("有无病史",2));
        data.add(new PersonalInformationEntity("有无过敏",2));
        data.add(new PersonalInformationEntity("饮食偏好",2));
        data.add(new PersonalInformationEntity("作息习惯",1));
        data.add(new PersonalInformationEntity("其他",1));
        data.add(new PersonalInformationEntity("保存",3));
        inforAdapter=new PersonalInformationAdapter(data,this);
        listView.setAdapter(inforAdapter);
        inforAdapter.notifyDataSetChanged();

    }

    private void initView() {
        listView= (ListView) findViewById(R.id.listView);
        iv_back= (ImageView) findViewById(R.id.back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

}
