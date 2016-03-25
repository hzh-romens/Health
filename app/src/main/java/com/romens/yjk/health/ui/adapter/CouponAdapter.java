package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.romens.yjk.health.model.CuoponEntity;
import com.romens.yjk.health.ui.cells.CuoponCardCell;
import com.romens.yjk.health.ui.cells.CuoponEmptyCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HZH on 2016/1/18.
 */
public class CouponAdapter extends BaseAdapter {

    private Context mContext;
    private final List<CuoponEntity> mResult = new ArrayList<>();
    private final List<String> mChoiceArray = new ArrayList<>();

    public CouponAdapter(Context context) {
        this.mContext = context;
    }

    public void bindData(List<CuoponEntity> result) {
        mResult.clear();
        mChoiceArray.clear();
        if (result != null && result.size() > 0) {
            mResult.addAll(result);
        }
        notifyDataSetChanged();
    }

    private String mType;

    public void setChoice(String choiceCouponGuid, String requestType) {
        mChoiceArray.clear();
        mChoiceArray.add(choiceCouponGuid);
        this.mType = requestType;
        notifyDataSetChanged();
    }

    /**
     * 现阶段只支持使用一张优惠券
     * @param choiceCouponGuid
     */
    public void switchCheck(String choiceCouponGuid) {
//        boolean isChecked=mChoiceArray.contains(choiceCouponGuid);
//        if (isChecked) {
//            mChoiceArray.clear();
//        } else {
//            mChoiceArray.add(choiceCouponGuid);
//        }
        mChoiceArray.clear();
        mChoiceArray.add(choiceCouponGuid);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (mResult != null) {
            if (mResult.size() == 0) {
                return 1;
            } else {
                return mResult.size();
            }
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == 0) {
            if (convertView == null) {
                convertView = new CuoponEmptyCell(mContext);
            }
            CuoponEmptyCell cuoponEmptyCell = (CuoponEmptyCell) convertView;
            // cuoponEmptyCell.
        } else {
            if (convertView == null) {
                convertView = new CuoponCardCell(mContext);
            }
            CuoponCardCell cell = (CuoponCardCell) convertView;
            CuoponEntity entity = mResult.get(position);
            cell.setValue(entity.getIsused(), entity.getName(), entity.getEnddate(), entity.getLimitamount(), entity.getDescription(), entity.getAmount(), entity.getStartdate());

            if ("GetCoupon".equals(mType)) {
                cell.setStatus("0");
                final String couponGuid = entity.getGuid();
                if (mChoiceArray.contains(couponGuid)) {
                    cell.setShapeColor(true);
                } else {
                    cell.setShapeColor(false);
                }
            } else {
                cell.setStatus("1");
            }
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (mResult.size() == 0) {
            return 0;
        } else {
            return 1;
        }

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
