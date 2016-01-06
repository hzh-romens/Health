package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.romens.android.io.image.ImageManager;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.DeliverytypeEntity;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by AUSU on 2015/9/20.
 * 订单提交的Adapter
 */

public class CommitOrderAdapter extends BaseExpandableListAdapter{
    private List<ParentEntity> mFatherData;
    private HashMap<String, List<ShopCarEntity>> mChildData;
    private Context mContext;
    private CheckDataCallBack checkDataCallBack;
    private int sendFlag;
    private String DeliverytypeStr = "";

    public void setCheckDataChangeListener(CheckDataCallBack checkDataCallBack) {
        this.checkDataCallBack = checkDataCallBack;
    }
    //接口回调，用于数据刷新
    public interface CheckDataCallBack {
        void getCheckData(String flag);
    }

    public CommitOrderAdapter(Context context, int realCoutn) {
        this.mContext = context;
        this.sendFlag = realCoutn;


    }

    public void SetData(List<ParentEntity> fatherData, HashMap<String, List<ShopCarEntity>> childData) {
        this.mFatherData = fatherData;
        this.mChildData = childData;

    }

    @Override
    public int getGroupCount() {
        int count = mFatherData == null ? 0 : mFatherData.size() + 1;
        return count;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == sendFlag) {
            return 0;
        }
        List<ShopCarEntity> shopCarEntities = mChildData.get(mFatherData.get(getCurrentGroupPosition(groupPosition)).getShopID());
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
        if (groupType == 0) {
            ParentHolder parentHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_group, null);
                parentHolder = new ParentHolder();
                parentHolder.parentName = (TextView) convertView.findViewById(R.id.group_name);
                parentHolder.groupnameLayout = (FrameLayout) convertView.findViewById(R.id.group_name_layout);
                convertView.setTag(parentHolder);
            } else {
                parentHolder = (ParentHolder) convertView.getTag();
            }
            convertView.setClickable(true);
            parentHolder.parentName.setText(mFatherData.get(getCurrentGroupPosition(groupPosition)).getShopName());
            parentHolder.groupnameLayout.setClickable(true);
        } else if (groupType == 1) {
            final SendHolder sendHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_child_radiogroup, null);
                sendHolder = new SendHolder();
                sendHolder.groupname = (TextView) convertView.findViewById(R.id.name);
                sendHolder.deliverytype = (TextView) convertView.findViewById(R.id.tv_deliverytype);
                convertView.setTag(sendHolder);
            } else {
                sendHolder = (SendHolder) convertView.getTag();
            }
            convertView.setClickable(true);
            sendHolder.groupname.setText("派送方式");
            sendHolder.groupname.setClickable(true);
            sendHolder.deliverytype.setText(DeliverytypeStr);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListDialogFragment
                            .createBuilder(mContext, mFragmentManger)
                            .setTitle("配送方式")
                            .setItems(choice)
                            .setRequestCode(9)
                            .show();
                }
            });

        }
        return convertView;
    }

    public void SetValue(String value) {
        DeliverytypeStr=value;
        notifyDataSetChanged();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_order2, null);
            childHolder = new ChildHolder();
            childHolder.imageView = (ImageView) convertView.findViewById(R.id.iv);
            childHolder.count = (TextView) convertView.findViewById(R.id.tv_count);
            childHolder.info = (TextView) convertView.findViewById(R.id.tv_infor);
            childHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
            childHolder.price = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        ShopCarEntity shopCarEntity = mChildData.get(mFatherData.get(getCurrentGroupPosition(groupPosition)).getShopID()).get(childPosition);
        childHolder.price.setText("¥" + shopCarEntity.getGOODSPRICE());
        childHolder.name.setText(shopCarEntity.getNAME());
        childHolder.info.setText(shopCarEntity.getSPEC());
        childHolder.count.setText(shopCarEntity.getBUYCOUNT() + "");
        Drawable defaultDrawables = childHolder.imageView.getDrawable();
        ImageManager.loadForView(mContext, childHolder.imageView, shopCarEntity.getGOODURL(), defaultDrawables, defaultDrawables);
        return convertView;
    }

    class ChildHolder {
        private ImageView imageView;
        private TextView name, info, price, count;
    }

    class SendHolder {
        private TextView groupname;
        private TextView deliverytype;
    }

    class ParentHolder {
        private TextView parentName;
        private FrameLayout groupnameLayout;
    }

    public int getCurrentGroupPosition(int groupPosition) {
        if (groupPosition < sendFlag - 1) {
            return groupPosition;
        }
        return 0;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public int getGroupType(int groupPosition) {
        if (groupPosition == mFatherData.size()) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getGroupTypeCount() {
        return 2;
    }

    private String[] choice;

    public void SetDeliverytypeData(List<DeliverytypeEntity> deliverytypeResult) {
        choice = new String[deliverytypeResult.size()];
        for (int i = 0; i < deliverytypeResult.size(); i++) {
            choice[i] = deliverytypeResult.get(i).getNAME();
        }

    }

    private FragmentManager mFragmentManger;

    public void setFragmentManger(FragmentManager fragmentManger) {
        this.mFragmentManger = fragmentManger;
    }


}
