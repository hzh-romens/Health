package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ChoiceEntity;

import java.util.List;

/**
 * Created by HZH on 2015/10/30.
 */
public class PopAdapter extends BaseAdapter{
    private Context mContext;
    private List<ChoiceEntity> mDdatas;

    public PopAdapter(Context context,List<ChoiceEntity> datas) {
        this.mContext = context;
        this.mDdatas=datas;
    }

    @Override
    public int getCount() {
        if (mDdatas != null) {
            return mDdatas.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(mContext, R.layout.list_item_checktext, null);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        ImageView iv = (ImageView) convertView.findViewById(R.id.iv);
        tv_name.setText(mDdatas.get(position).getChoice());
        if (mDdatas.get(position).isFlag()) {
            iv.setBackground(mContext.getResources().getDrawable(R.drawable.choice_icon));
        }
       // LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(36));
        //onvertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return convertView;
    }

}
