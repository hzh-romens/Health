package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.ShopCarActivity;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import static android.view.ViewGroup.*;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by AUSU on 2015/9/20.
 * 订单提交的Adapter
 */

public class CommitOrderAdapter extends BaseExpandableListAdapter {
    private List<ParentEntity> mFatherData;
    private HashMap<String, List<ShopCarEntity>> mChildData;
    private Context mContext;
    private CheckDataCallBack checkDataCallBack;

    public void setCheckDataChangeListener(CheckDataCallBack checkDataCallBack) {
        this.checkDataCallBack = checkDataCallBack;
    }

    //接口回调，用于数据刷新
    public interface CheckDataCallBack {
        void getCheckData(String flag);
    }

    public CommitOrderAdapter(Context context) {
        this.mContext = context;
    }

    public void SetData(List<ParentEntity> fatherData, HashMap<String, List<ShopCarEntity>> childData) {
        this.mFatherData = fatherData;
        this.mChildData = childData;
    }

    @Override
    public int getGroupCount() {
        return mFatherData == null ? 0 : mFatherData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<ShopCarEntity> shopCarEntities = mChildData.get(mFatherData.get(groupPosition).getShopID());
        return shopCarEntities == null ? 0 : shopCarEntities.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        int groupType = getGroupType(groupPosition);
        // switch (groupType){
        //   case 1:
        if (groupType == 1) {
            SendHolder sendHolder = null;
            if (convertView == null) {
                sendHolder = new SendHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_child_radiogroup, null);
                sendHolder.tv_groupname = (TextView) convertView.findViewById(R.id.name);
                sendHolder.rg = (RadioGroup) convertView.findViewById(R.id.rg);
                sendHolder.rb1 = (RadioButton) convertView.findViewById(R.id.rb1);
                sendHolder.rb2 = (RadioButton) convertView.findViewById(R.id.rb2);
                convertView.setTag(sendHolder);
            } else {
                sendHolder = (SendHolder) convertView.getTag();
            }
            sendHolder.tv_groupname.setText("派送方式");
            sendHolder.tv_groupname.setClickable(true);
            sendHolder.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    //写一个回调
                    int checkedRadioButtonId = group.getCheckedRadioButtonId();
                    if (checkedRadioButtonId == R.id.rb1) {
                        checkDataCallBack.getCheckData("药店派送");
                    } else if (checkedRadioButtonId == R.id.rb2) {
                        checkDataCallBack.getCheckData("到店自取");
                    }
                }
            });
            //break;
            // case 0:
        } else {
            ParentHolder parentHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_group, null);
                parentHolder = new ParentHolder();
                parentHolder.tv_groupname = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(parentHolder);
            } else {
                parentHolder = (ParentHolder) convertView.getTag();
            }
            parentHolder.tv_groupname.setText(mFatherData.get(groupPosition).getShopName());
            parentHolder.tv_groupname.setClickable(true);
        }
        //  break;
        //   }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_order2, null);
            childHolder = new ChildHolder();
            childHolder.iv = (ImageView) convertView.findViewById(R.id.iv);
            childHolder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            childHolder.tv_infor = (TextView) convertView.findViewById(R.id.tv_infor);
            childHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            childHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        ShopCarEntity shopCarEntity = mChildData.get(mFatherData.get(groupPosition).getShopID()).get(childPosition);
        childHolder.tv_price.setText(shopCarEntity.getGOODSPRICE() + "");
        childHolder.tv_name.setText(shopCarEntity.getNAME());
        childHolder.tv_infor.setText(shopCarEntity.getSPEC());
        childHolder.tv_count.setText(shopCarEntity.getBUYCOUNT() + "");
        return convertView;
    }

    class ChildHolder {
        private ImageView iv;
        private TextView tv_name, tv_infor, tv_price, tv_count;
    }

    class SendHolder {
        private TextView tv_groupname;
        private RadioGroup rg;
        private RadioButton rb1, rb2;
    }

    class ParentHolder {
        private TextView tv_groupname;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        if ("-1".equals(mFatherData.get(groupPosition).getShopID())) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getGroupType(int groupPosition) {
        if ("-1".equals(mFatherData.get(groupPosition).getShopID())) {
            return 1;
        }
        return 0;
    }

}
