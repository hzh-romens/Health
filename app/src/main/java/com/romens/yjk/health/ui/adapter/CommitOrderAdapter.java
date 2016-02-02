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

import com.romens.android.io.image.ImageManager;
import com.romens.android.ui.cells.EmptyCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.DeliverytypeEntity;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.cells.BillCell;
import com.romens.yjk.health.ui.cells.CouponItemCell;

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

    private List<String> mParentTypes;

    public void SetData(List<ParentEntity> fatherData, HashMap<String, List<ShopCarEntity>> childData, List<String> parentTypes) {
        this.mFatherData = fatherData;
        this.mChildData = childData;
        this.mParentTypes = parentTypes;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
//        if (mFatherData != null) {
//            return mFatherData.size() + 5;
//        }
//        return 0;
        if (mFatherData != null) {
            return mParentTypes.size() + 1;
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // if (groupPosition == sendFlag) {
        //    return 0;
        //}
        // List<ShopCarEntity> shopCarEntities = mChildData.get(mFatherData.get(getCurrentGroupPosition(groupPosition)).getShopID());
        // return shopCarEntities == null ? 0 : shopCarEntities.size();
        if (groupPosition < mFatherData.size()) {
            List<ShopCarEntity> shopCarEntities = mChildData.get(mFatherData.get(getCurrentGroupPosition(groupPosition)).getShopID());
            return shopCarEntities == null ? 0 : shopCarEntities.size();
        } else {
            return 0;
        }
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
                convertView = new TextSettingsCell(mContext);
                //   convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_group, null);
                // parentHolder = new ParentHolder();
                //parentHolder.parentName = (TextView) convertView.findViewById(R.id.group_name);
                //parentHolder.groupnameLayout = (FrameLayout) convertView.findViewById(R.id.group_name_layout);
                //convertView.setTag(parentHolder);
            }
            //else {
            //  parentHolder = (ParentHolder) convertView.getTag();
            //}
            convertView.setClickable(true);
            TextSettingsCell cell = new TextSettingsCell(mContext);
            cell.setText(mFatherData.get(getCurrentGroupPosition(groupPosition)).getShopName(), false);
            // parentHolder.parentName.setText(mFatherData.get(getCurrentGroupPosition(groupPosition)).getShopName());
            //parentHolder.groupnameLayout.setClickable(true);
        } else if (groupType == mFatherData.size()) {
            //  final SendHolder sendHolder;
            if (convertView == null) {
                //convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_child_radiogroup, null);
                convertView = new TextSettingsCell(mContext);
                //     sendHolder = new SendHolder();
                //   sendHolder.groupname = (TextView) convertView.findViewById(R.id.name);
                //  sendHolder.deliverytype = (TextView) convertView.findViewById(R.id.tv_deliverytype);
                //  convertView.setTag(sendHolder);
            }
            TextSettingsCell cell = (TextSettingsCell) convertView;
            cell.setText("测试", false);
            //else {
            //   sendHolder = (SendHolder) convertView.getTag();
            //  }
            // convertView.setClickable(true);
            //   sendHolder.groupname.setText("送药方式");
            //  sendHolder.groupname.setClickable(true);
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ListDialogFragment
//                            .createBuilder(mContext, mFragmentManger)
//                            .setTitle("配送方式")
//                            .setItems(choice)
//                            .setRequestCode(9)
//                            .show();
//                }
//            });

        } else if (groupType == mFatherData.size() + 1) {
            if (convertView == null) {
                convertView = new CouponItemCell(mContext);
            }
            convertView.setClickable(true);
            CouponItemCell cell = (CouponItemCell) convertView;
            cell.setValue("优惠券", "满120减10", R.drawable.ic_chevron_right_grey600_24dp, false);
        } else if (groupType == mFatherData.size() + 2 || groupType == mFatherData.size() + 4) {
            if (convertView == null) {
                convertView = new ShadowSectionCell(mContext);
            }
            //   convertView.setClickable(true);
            // Log.i("tag----", groupType + "");
            //  ShadowSectionCell cell = (ShadowSectionCell) convertView;
            //   cell.setClickable(true);
        } else if (groupType == mFatherData.size() + 3) {
            if (convertView == null) {
                convertView = new BillCell(mContext);
            }
            convertView.setClickable(true);
            BillCell cell = (BillCell) convertView;
            cell.setValue("测试");
        } else if (groupType == mFatherData.size() + 5) {
            if (convertView == null) {
                convertView = new TextSettingsCell(mContext);
            }
            convertView.setClickable(true);
            TextSettingsCell cell = (TextSettingsCell) convertView;
            if (groupType == mFatherData.size() + 5) {
                cell.setTextAndValue("药品金额", "¥120", false);
            } else if (groupType == mFatherData.size() + 6) {
                cell.setTextAndValue("药品优惠", "¥10", false);
            }
            // if (groupType == mFatherData.size() + 7)

        }else if (groupType == mFatherData.size() + 6){

        }else {
            if (convertView == null) {
                convertView = new EmptyCell(mContext);
            }
            convertView.setClickable(true);
            EmptyCell cell = (EmptyCell) convertView;
            cell.setBackgroundColor(0xeeeeee);
            cell.setHeight(36);
        }
        return convertView;
    }


    public void SetValue(String value) {
        DeliverytypeStr = value;
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
        int groupType = super.getGroupType(groupPosition);
        Log.i("-------", groupPosition + "===" + mFatherData.size());
        if (groupPosition < mFatherData.size()) {
            return 0;
        }
        //    Log.i("指针-----", mParentTypes.size())
        return Integer.parseInt(mParentTypes.get(groupPosition));
    }

    @Override
    public int getGroupTypeCount() {
        if (mParentTypes != null) {
            return mParentTypes.size() + 1;
        }
        return 0;
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
