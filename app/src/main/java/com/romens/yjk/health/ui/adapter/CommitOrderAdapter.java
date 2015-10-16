package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;

import java.util.HashMap;
import java.util.List;

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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_group, null);
        TextView tv = (TextView) convertView.findViewById(R.id.name);
        if ("-1".equals(mFatherData.get(groupPosition).getShopID())) {
            tv.setText("派送方式");
        } else {
            tv.setText(mFatherData.get(groupPosition).getShopName());
            tv.setClickable(true);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder = null;
        SendHolder sendHolder = null;
        int childType = getChildType(groupPosition, childPosition);
        Log.i("类型---", childType + "");
        if (convertView == null) {
            switch (childType) {
                case 1:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_child_radiogroup, null);
                    sendHolder = new SendHolder();
                    sendHolder.rg = (RadioGroup) convertView.findViewById(R.id.rg);
                    sendHolder.rb1 = (RadioButton) convertView.findViewById(R.id.rb1);
                    sendHolder.rb2 = (RadioButton) convertView.findViewById(R.id.rb2);
                    convertView.setTag(sendHolder);
                    break;
                case 0:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_order2, null);
                    childHolder = new ChildHolder();
                    childHolder.iv = (ImageView) convertView.findViewById(R.id.iv);
                    childHolder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
                    childHolder.tv_infor = (TextView) convertView.findViewById(R.id.tv_infor);
                    childHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    childHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                    convertView.setTag(childHolder);
                    break;

            }
        } else {
            switch (childType) {
                case 1:
                    sendHolder = (SendHolder) convertView.getTag();
                    break;
                case 0:
                    childHolder = (ChildHolder) convertView.getTag();
                    break;
            }
        }
        //数据添加:小于0,添加派送方式的数据。大于0，添加正常数据
        switch (childType){
            case 1:
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
            break;
            case 0:
                ShopCarEntity shopCarEntity = mChildData.get(mFatherData.get(groupPosition).getShopID()).get(childPosition);
                childHolder.tv_price.setText(shopCarEntity.getGOODSPRICE() + "");
                childHolder.tv_name.setText(shopCarEntity.getNAME());
                childHolder.tv_infor.setText(shopCarEntity.getSPEC());
                childHolder.tv_count.setText(shopCarEntity.getBUYCOUNT() + "");
            break;
        }
//        if (!("-1".equals(mFatherData.get(groupPosition).getShopID()))) {
//
//        }
//        if (("-1".equals(mFatherData.get(groupPosition).getShopID()))) {
//            if (sendHolder != null) {
//                sendHolder.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(RadioGroup group, int checkedId) {
//                        //写一个回调
//                        int checkedRadioButtonId = group.getCheckedRadioButtonId();
//                        if (checkedRadioButtonId == R.id.rb1) {
//                            checkDataCallBack.getCheckData("药店派送");
//                        } else if (checkedRadioButtonId == R.id.rb2) {
//                            checkDataCallBack.getCheckData("到店自取");
//                        }
//                    }
//                });
//            }
      //  }
        return convertView;
    }

    class ChildHolder {
        private ImageView iv;
        private TextView tv_name, tv_infor, tv_price, tv_count;
    }

    class SendHolder {
        private RadioGroup rg;
        private RadioButton rb1, rb2;
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
}
