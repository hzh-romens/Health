package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.romens.yjk.health.model.CuoponEntity;
import com.romens.yjk.health.ui.cells.CuoponCardCell;

import java.util.List;

/**
 * Created by HZH on 2016/1/18.
 */
public class CuoponAdapter extends BaseAdapter {

    private Context mContext;
    private List<CuoponEntity> mResult;
    private SparseBooleanArray mChoiceArray = new SparseBooleanArray();

    public CuoponAdapter(Context context) {
        this.mContext = context;
    }

    public void bindData(List<CuoponEntity> result) {
        if (mResult != null) {
            mResult.clear();
        }
        this.mResult = result;
        notifyDataSetChanged();
    }

    public void bindChoiceData(SparseBooleanArray choiceArray) {
        if (mChoiceArray != null) {
            mChoiceArray.clear();
        }
        this.mChoiceArray = choiceArray;
        notifyDataSetChanged();
    }

    private int mChoiceID = -1;
    private String mType;

    public void setChoice(int choiceID, String requestType) {
        this.mChoiceID = choiceID;
        this.mType = requestType;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (mResult != null) {
            return mResult.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new CuoponCardCell(mContext);
        }
        CuoponCardCell cell = (CuoponCardCell) convertView;
        CuoponEntity entity = mResult.get(position);
        cell.setValue(entity.getIsused(), entity.getName(), entity.getEnddate(), entity.getLimitamount(), entity.getShuoming(), entity.getAmount(), entity.getStartdate());
        cell.setStatus("1");
        if ("GetCoupon".equals(mType)) {
            if (mChoiceID >= 0 && mChoiceID == position) {
                if (mChoiceArray.get(position)) {
                    cell.setShapeColor(true);
                    mChoiceArray.append(position, true);
                } else {
                    cell.setShapeColor(false);
                    mChoiceArray.append(position, false);
                }
            }
        }
        return convertView;
    }

    public SparseBooleanArray getChoiceArray() {
        return mChoiceArray;
    }


    private int mPosition;
    private boolean mValue;

    public void setChoiceItem(int position, boolean value) {
        this.mPosition = position;
        this.mValue = value;
    }
}
