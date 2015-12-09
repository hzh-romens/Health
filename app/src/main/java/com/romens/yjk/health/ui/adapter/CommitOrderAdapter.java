package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;
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

public class CommitOrderAdapter extends BaseExpandableListAdapter implements IListDialogListener{
    private List<ParentEntity> mFatherData;
    private HashMap<String, List<ShopCarEntity>> mChildData;
    private Context mContext;
    private CheckDataCallBack checkDataCallBack;
    private int sendFlag;
    private String DeliverytypeStr = "";

    public void setCheckDataChangeListener(CheckDataCallBack checkDataCallBack) {
        this.checkDataCallBack = checkDataCallBack;
    }

    @Override
    public void onListItemSelected(CharSequence value, int number, int requestCode) {
        if (requestCode == 1 ){
            Toast.makeText(mContext, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }
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
                parentHolder.tv_groupname = (TextView) convertView.findViewById(R.id.group_name);
                parentHolder.group_name_layout = (FrameLayout) convertView.findViewById(R.id.group_name_layout);
                convertView.setTag(parentHolder);
            } else {
                parentHolder = (ParentHolder) convertView.getTag();
            }
            convertView.setClickable(true);
            parentHolder.tv_groupname.setText(mFatherData.get(getCurrentGroupPosition(groupPosition)).getShopName());
            parentHolder.group_name_layout.setClickable(true);
        } else if (groupType == 1) {
            final SendHolder sendHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_child_radiogroup, null);
                sendHolder = new SendHolder();
                sendHolder.tv_groupname = (TextView) convertView.findViewById(R.id.name);
                sendHolder.tv_deliverytype = (TextView) convertView.findViewById(R.id.tv_deliverytype);
                convertView.setTag(sendHolder);
            } else {
                sendHolder = (SendHolder) convertView.getTag();
            }
            convertView.setClickable(true);
            sendHolder.tv_groupname.setText("派送方式");
            sendHolder.tv_groupname.setClickable(true);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListDialogFragment
                            .createBuilder(mContext, mFragmentManger)
                            .setTitle("配送方式")
                            .setItems(choice)
                            .setRequestCode(9)
                            .show();

                    Log.i("000000000====", DeliverytypeStr);
                }
            });

        }
        return convertView;
    }

    public void SetView(TextView view) {

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
        ShopCarEntity shopCarEntity = mChildData.get(mFatherData.get(getCurrentGroupPosition(groupPosition)).getShopID()).get(childPosition);
        childHolder.tv_price.setText("¥" + shopCarEntity.getGOODSPRICE());
        childHolder.tv_name.setText(shopCarEntity.getNAME());
        childHolder.tv_infor.setText(shopCarEntity.getSPEC());
        childHolder.tv_count.setText(shopCarEntity.getBUYCOUNT() + "");
        Drawable defaultDrawables = childHolder.iv.getDrawable();
        ImageManager.loadForView(mContext, childHolder.iv, shopCarEntity.getGOODURL(), defaultDrawables, defaultDrawables);
        return convertView;
    }

    class ChildHolder {
        private ImageView iv;
        private TextView tv_name, tv_infor, tv_price, tv_count;
    }

    class SendHolder {
        private TextView tv_groupname;
        private TextView tv_deliverytype;
    }

    class ParentHolder {
        private TextView tv_groupname;
        private FrameLayout group_name_layout;
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
