package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.common.io.Resources;
import com.romens.android.AndroidUtilities;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.components.ModifyRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AUSU on 2015/9/26.
 */
public class PersonAdapter extends BaseAdapter{
    private Context mContext;
    public PersonAdapter(Context context){
        this.mContext=context;
    }
    @Override
    public int getCount() {
        return 1;
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
        convertView= LayoutInflater.from(mContext).inflate(R.layout.list_item_person, null);
        getData();
        int size = datas.size();
        int totalHeight = parent.getHeight();
        int x;
        if(size%3==0){
            x=size/3;
        }else{
            x=(size)/3+1;
        }
        int height = totalHeight / x;

        ModifyRecyclerView gridview= (ModifyRecyclerView) convertView.findViewById(R.id.gridView);
        gridview.setNumColumns(3);
        gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        //添加假数据，制造一个假的九宫格
        if(size%3==0){
            ModifyViewAdapter modifyViewAdapter=new ModifyViewAdapter(datas,mContext);
            gridview.setAdapter(modifyViewAdapter);
        }else if(size%3==1){
            datas.add("空数据1");
            datas.add("空数据2");
            ModifyViewAdapter modifyViewAdapter=new ModifyViewAdapter(datas,mContext);
            gridview.setAdapter(modifyViewAdapter);
        }else{
            datas.add("空数据1");
            ModifyViewAdapter modifyViewAdapter=new ModifyViewAdapter(datas,mContext);
            gridview.setAdapter(modifyViewAdapter);
        }
        convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return convertView;
    }
    private List<String> datas=new ArrayList<String>();
    private void getData() {
        if(datas!=null){
            datas.clear();
        }
        datas.add("用药提醒");
        datas.add("退换货记录");
        datas.add("收藏");
        datas.add("浏览历史");
        datas.add("收货地址管理");
        datas.add("账号管理");
        datas.add("帮助");
        datas.add("意见反馈");
    }
}
