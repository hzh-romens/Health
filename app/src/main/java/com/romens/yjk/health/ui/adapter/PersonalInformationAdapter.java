package com.romens.yjk.health.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ChoiceEntity;
import com.romens.yjk.health.model.PersonalInformationEntity;

import org.w3c.dom.Text;

import java.util.List;

import widget.OnWheelChangedListener;
import widget.WheelView;
import widget.adapters.ArrayWheelAdapter;

/**
 * Created by HZH on 2015/10/24.
 */
public class PersonalInformationAdapter extends BaseAdapter implements OnWheelChangedListener {
    private List<PersonalInformationEntity> mResult;
    private Context mContext;
   public PersonalInformationAdapter(List<PersonalInformationEntity> result,Context context){
        this.mContext=context;
        this.mResult=result;
   }
    @Override
    public int getCount() {
        return mResult==null?0:mResult.size();
    }

    @Override
    public Object getItem(int position) {
        return mResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(getItemViewType(position)==0){
                convertView=View.inflate(mContext, R.layout.list_item_personal_parent,null);
                TextView tv_parent= (TextView) convertView.findViewById(R.id.name);
                tv_parent.setText(mResult.get(position).getTitleName());
        } else if(getItemViewType(position)==1){
                convertView=View.inflate(mContext,R.layout.list_item_personal_child,null);
                TextView tv_child= (TextView) convertView.findViewById(R.id.childName);
                EditText editor= (EditText) convertView.findViewById(R.id.editor);
                tv_child.setText(mResult.get(position).getTitleName());
        }else if(getItemViewType(position)==2){
            convertView=View.inflate(mContext,R.layout.list_item_personal_child2,null);
            TextView tv_child= (TextView) convertView.findViewById(R.id.childName);
            final TextView tv_choice= (TextView) convertView.findViewById(R.id.tv_choice);
            tv_child.setText(mResult.get(position).getTitleName());
            tv_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开popwindow
                if ("性别".equals(mResult.get(position).getTitleName())) {
                    String[] data = new String[2];
                    data[0] = "男";
                    data[1] = "女";
                  //getPopWindowInstance(data, tv_choice);
                } else {
                    String[] data = new String[2];
                    data[0] = "有";
                    data[1] = "无";
                   // getPopWindowInstance(data, tv_choice);
                }
            }
        });
        }else if(getItemViewType(position)==3){
                convertView=View.inflate(mContext,R.layout.list_item_surebutton,null);
                 LinearLayout btn_save= (LinearLayout) convertView.findViewById(R.id.btn_save);
                //保存个人信息
                btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return mResult.get(position).getType();
    }







    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {

    }
}
