package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.io.image.ImageManager;
import com.romens.android.io.image.ImageUtils;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.components.CheckableFrameLayout;
import com.romens.yjk.health.ui.utils.DialogUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by AUSU on 2015/9/17.
 */
public class ShopAdapter extends BaseExpandableListAdapter {
    private List<ParentEntity> mFatherData;
    private HashMap<String, List<ShopCarEntity>> mChildData;
    private Context mContext;
    private SparseBooleanArray fatherStatus = new SparseBooleanArray();
    private SparseBooleanArray childStatus = new SparseBooleanArray();

    public double sumMoney = 0;


    //接口回调，用于数据刷新
    public interface AdapterCallBack {
        void UpdateData();

        void UpdateMoney(String money);
    }

    private AdapterCallBack mAdapterCallBack;

    public void AdapterListener(AdapterCallBack adapterCallBack) {
        this.mAdapterCallBack = adapterCallBack;
    }


    private HashMap<String, SparseBooleanArray> childStatusList = new HashMap<String, SparseBooleanArray>();

    public ShopAdapter(Context context) {
        this.mContext = context;
    }


    public HashMap<String, List<ShopCarEntity>> getChildData() {
        return mChildData;
    }

    public List<ParentEntity> getParentData() {
        return mFatherData;
    }
    public SparseBooleanArray getParentStatus(){
        return fatherStatus;
    }

    public void bindData(List<ParentEntity> fatherData, HashMap<String, List<ShopCarEntity>> childData, AdapterCallBack adapterCallBack) {
        this.mFatherData = fatherData;
        this.mChildData = childData;
        this.mAdapterCallBack = adapterCallBack;
        sumMoney=0;
        fatherStatus.clear();
       childStatusList.clear();
        for (int i = 0; i < mFatherData.size(); i++) {
            fatherStatus.append(i, true);
        }
        Iterator iter = childData.entrySet().iterator();
        while (iter.hasNext()) {
            ParentEntity fatherEntity = new ParentEntity();
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
            SparseBooleanArray childStatus = new SparseBooleanArray();
            for (int i = 0; i < child.size(); i++) {
                childStatus.append(i, true);
                sumMoney = sumMoney + child.get(i).getBUYCOUNT() * child.get(i).getGOODSPRICE();
            }
            childStatusList.put(key, childStatus);
        }
        updateData();
        updateMoney(sumMoney + "");
    }
    //获取childstatus的集合
    public HashMap<String, SparseBooleanArray> getChildStatusList(){
        return childStatusList;
    }

    //fatherItem的单个点击事件
    public void SwitchFatherItem(String storeId, boolean status) {

        for (int i = 0; i < mFatherData.size(); i++) {
            if (storeId.equals(mFatherData.get(i).getShopID())) {
                fatherStatus.append(i, status);
                SparseBooleanArray sparseBooleanArray = childStatusList.get(storeId);
                for (int m = 0; m < sparseBooleanArray.size(); m++) {
                    childStatusList.get(storeId).append(m, status);
                    mChildData.get(storeId).get(m).setCHECK(status + "");
                }
                List<ShopCarEntity> shopCarEntities = mChildData.get(mFatherData.get(i).getShopID());

                for (int j = 0; j < shopCarEntities.size(); j++) {
                    if (status) {
                        sumMoney = sumMoney + shopCarEntities.get(j).getBUYCOUNT() * shopCarEntities.get(j).getGOODSPRICE();
                    } else {
                        sumMoney = sumMoney - shopCarEntities.get(j).getBUYCOUNT() * shopCarEntities.get(j).getGOODSPRICE();
                    }
                }
            }
        }
        updateData();
        updateMoney(sumMoney + "");
    }

    //childItem的单个点击事件
    public void SwitchChildItem(int parentPosition, String parentId, int childPosition, boolean status) {
        double sMoney = sumMoney;
        ShopCarEntity entity = mChildData.get(parentId).get(childPosition);
        entity.setCHECK(status + "");
        childStatusList.get(parentId).put(childPosition, status);
        SparseBooleanArray sparseBooleanArray = childStatusList.get(parentId);
        if (sparseBooleanArray.indexOfValue(true) < 0) {
            SwitchFatherItem(parentId, false);
        } else {
            fatherStatus.append(parentPosition, true);
        }
        //   double allMoney=0;
        if (status) {
            sMoney = sMoney + entity.getBUYCOUNT() * entity.getGOODSPRICE();
        } else {
            sMoney = sMoney - entity.getBUYCOUNT() * entity.getGOODSPRICE();
        }
        //  sumMoney=sumMoney+allMoney;
        sumMoney = sMoney;
        updateData();
        updateMoney(sumMoney + "");
    }

    public double getSumMoney(HashMap<String, List<ShopCarEntity>> data) {
        return 0;
    }

    //全选
    public boolean isAllSelected() {
        SparseBooleanArray provisional = new SparseBooleanArray();
        for (int i = 0; i < mFatherData.size(); i++) {
            boolean b = childItemIsAllSelected(mFatherData.get(i).getShopID());
            provisional.append(i, b);
        }
        int index = fatherStatus.indexOfValue(false);
        int i = provisional.indexOfValue(false);
        return index < 0 && i < 0;
    }
    public boolean isAllNotSelected(){
        if(mFatherData!=null) {
            SparseBooleanArray provisional = new SparseBooleanArray();
            for (int i = 0; i < mFatherData.size(); i++) {
                boolean b = childItemIsAllSelected(mFatherData.get(i).getShopID());
                provisional.append(i, b);
            }
            int index = fatherStatus.indexOfValue(true);
            int i = provisional.indexOfValue(true);
            return index < 0 && i < 0;
        }else {
            return true;
        }
    }

    //某一个parent下面的childItem是否全选
    public boolean childItemIsAllSelected(String parentId) {
        SparseBooleanArray sparseBooleanArray = childStatusList.get(parentId);
        int index = sparseBooleanArray.indexOfValue(false);
        return index < 0;
    }

    //全选或者全不选
    public void switchAllSelect(boolean value) {
        for (int i = 0; i < fatherStatus.size(); i++) {
            fatherStatus.append(i, value);
            mFatherData.get(i).setCheck(value + "");
        }
        Iterator iter = mChildData.entrySet().iterator();
        double s = 0;
        while (iter.hasNext()) {
            ParentEntity fatherEntity = new ParentEntity();
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
            SparseBooleanArray childStatus = new SparseBooleanArray();
            double allMoney = 0;
            if (value) {
                for (int i = 0; i < child.size(); i++) {
                    childStatus.append(i, value);
                    Double goodsprice = child.get(i).getGOODSPRICE();
                    allMoney = allMoney + goodsprice * child.get(i).getBUYCOUNT();
                }
            } else {
                for (int i = 0; i < child.size(); i++) {
                    childStatus.append(i, value);
                }
            }
            s = s + allMoney;
            childStatusList.put(key, childStatus);
        }
        sumMoney = s;
        updateMoney(sumMoney + "");
        updateData();

    }

    public void updateMoney(String money) {
        if (mAdapterCallBack != null) {
            mAdapterCallBack.UpdateMoney(money);
        }
    }


    //数据刷新及回调
    public void updateData() {
        notifyDataSetChanged();
        if (mAdapterCallBack != null) {
            mAdapterCallBack.UpdateData();
        }
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final FatherHolder fatherHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_parent, null);
            fatherHolder = new FatherHolder();
            fatherHolder.empty_view = convertView.findViewById(R.id.empty_view);
            fatherHolder.checkableFrameLayout = (CheckableFrameLayout) convertView.findViewById(R.id.checkbox);
            fatherHolder.storeName = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(fatherHolder);
        } else {
            fatherHolder = (FatherHolder) convertView.getTag();
        }
        if (groupPosition == 0) {
            fatherHolder.empty_view.setVisibility(View.GONE);
        } else {
            fatherHolder.empty_view.setVisibility(View.VISIBLE);
        }
        convertView.setClickable(true);
        ParentEntity fatherEntity = mFatherData.get(groupPosition);
        fatherHolder.storeName.setText(fatherEntity.getShopName());
        fatherHolder.checkableFrameLayout.setChecked(fatherStatus.get(groupPosition));
        fatherHolder.checkableFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   List<ShopCarEntity> shopCarEntities = mChildData.get(mFatherData.get(groupPosition).getStoreID());
                // for (int i = 0; i < shopCarEntities.size(); i++) {
                //   shopCarEntities.get(i).setCHECK("true");
                //  }
                SwitchFatherItem(mFatherData.get(groupPosition).getShopID(),
                        !fatherHolder.checkableFrameLayout.isChecked());
            }
        });
        fatherHolder.checkableFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchFatherItem(mFatherData.get(groupPosition).getShopID(), !fatherHolder.checkableFrameLayout.isChecked());
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_shop, null);
            holder = new ChildHolder();
            holder.bt_add = (Button) convertView.findViewById(R.id.bt_add);
            holder.bt_reduce = (Button) convertView.findViewById(R.id.bt_reduce);
            holder.checkBox = (CheckableFrameLayout) convertView.findViewById(R.id.checkbox);
            holder.iv_detail = (ImageView) convertView.findViewById(R.id.iv_detail);
            holder.tv_num = (TextView) convertView.findViewById(R.id.et_num);
            holder.tv_infor = (TextView) convertView.findViewById(R.id.tv_shop_infor);
            holder.tv_realPrice = (TextView) convertView.findViewById(R.id.realPrice);
            holder.tv_discountPrice = (TextView) convertView.findViewById(R.id.discountPrice);
            holder.tv_store = (TextView) convertView.findViewById(R.id.tv_store);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        final ShopCarEntity entity = mChildData.get(mFatherData.get(groupPosition).getShopID()).get(childPosition);
        holder.tv_num.setText(entity.getBUYCOUNT()+"");
        holder.tv_discountPrice.setText("¥" + entity.getGOODSPRICE());
        holder.tv_realPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        holder.tv_infor.setText(entity.getNAME());
        holder.tv_store.setText(entity.getSPEC());

      //  holder.iv_detail.setImageBitmap(ImageUtils.bindLocalImage("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg"));
        Drawable defaultDrawables =  holder.iv_detail.getDrawable();
        ImageManager.loadForView(mContext, holder.iv_detail,entity.getGOODURL(), defaultDrawables, defaultDrawables);

        holder.checkBox.setChecked(childStatusList.get(mFatherData.get(groupPosition).getShopID()).get(childPosition));

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchChildItem(groupPosition, mFatherData.get(groupPosition).getShopID(), childPosition, !holder.checkBox.isChecked());
            }
        });
        holder.tv_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils dialogUtils = new DialogUtils();
                dialogUtils.show(holder.tv_num.getText().toString(), mContext, "", new DialogUtils.QuantityOfGoodsCallBack() {
                    @Override
                    public void getGoodsNum(int num) {
                        int startNum = entity.getBUYCOUNT();
                        if (holder.checkBox.isChecked()) {
                            if (startNum > num) {
                                int reduce = startNum - num;
                                double reduceMoney = reduce * entity.getGOODSPRICE();
                                sumMoney = sumMoney - reduceMoney;
                                updateMoney(sumMoney + "");
                            } else {
                                int add = num - startNum;
                                double addMoney = add * entity.getGOODSPRICE();
                                sumMoney = sumMoney + addMoney;
                                updateMoney(sumMoney + "");
                            }
                            entity.setBUYCOUNT(num);
                            holder.tv_num.setText(num + "");
                        } else {
                            entity.setBUYCOUNT(num);
                            holder.tv_num.setText(num + "");
                        }
                    }
                });
            }
        });

        //增加
        holder.bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(holder.tv_num.getText().toString());
                int startNum = num;
                if (holder.checkBox.isChecked()) {
                    num++;
                    entity.setBUYCOUNT(num);
                    double addMoney = entity.getGOODSPRICE() * (num - startNum);
                    sumMoney = sumMoney + addMoney;
                    holder.tv_num.setText(num + "");
                    updateMoney(sumMoney + "");
                    updateData();

                } else {
                    num++;
                    entity.setBUYCOUNT(num);
                    holder.tv_num.setText(num + "");
                    updateData();
                }
            }
        });

        //减少
        holder.bt_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(holder.tv_num.getText().toString());
                int startNum = num;
                if (num > 1) {
                    if (holder.checkBox.isChecked()) {
                        num--;
                        //    ShopCarEntity shopCarEntity = mChildData.get(mFatherData.get(groupPosition).getStoreID()).get(childPosition);
                        entity.setBUYCOUNT(num);
                        double addMoney = entity.getGOODSPRICE() * (startNum - num);
                        sumMoney = sumMoney - addMoney;
                        holder.tv_num.setText(num + "");
                        updateMoney(sumMoney + "");
                        updateData();
                    } else {
                        num--;
                        //   ShopCarEntity shopCarEntity = mChildData.get(mFatherData.get(groupPosition).getStoreID()).get(childPosition);
                        entity.setBUYCOUNT(num);
                        holder.tv_num.setText(num + "");
                        // updateData();
                    }
                }
            }
        });
        return convertView;
    }

    class ChildHolder {
        private CheckableFrameLayout checkBox;
        private Button bt_reduce, bt_add;
        private ImageView iv_detail;
        private TextView tv_realPrice, tv_discountPrice, tv_infor;
        private TextView tv_num, tv_store;
    }

    class FatherHolder {
        private CheckableFrameLayout checkableFrameLayout;
        private TextView storeName;
        private View empty_view;

    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
}
