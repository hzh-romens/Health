package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.GoodsListEntity;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/15.
 */
public class CollectFragment extends AppFragment {

    private Context mContext;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private CollectFragmentAdapter adapter;
    private List<GoodsListEntity> entities;


    public CollectFragment(Context context, int type) {
        this.mContext = context;
        entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
//            if (type == 0) {
//                entities.add("药品--》" + i);
//            } else if (type == 1) {
//                entities.add("药店--》" + i);
//            }
        }
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(mContext);
        refreshLayout = new SwipeRefreshLayout(mContext);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        frameLayout.addView(refreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(mContext);
        refreshLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        adapter = new CollectFragmentAdapter(mContext, entities);
        listView.setAdapter(adapter);
        return frameLayout;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }

    public class CollectFragmentAdapter extends BaseAdapter {

        private Context context;
        private List<GoodsListEntity> entities;

        public CollectFragmentAdapter(Context context, List<GoodsListEntity> entities) {
            this.context = context;
            this.entities = entities;
        }

        @Override
        public int getCount() {
            return entities.size();
        }

        @Override
        public Object getItem(int position) {
            return entities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_collect, null);
            CollectViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_collect, null);
                holder = new CollectViewHolder(convertView);
                convertView.setTag(holder);
            }
            holder = (CollectViewHolder) convertView.getTag();
            holder.imageView.setImageUrl(entities.get(position).getGoodsUrl(), "64_64", null);
            holder.drugNameTextView.setText(entities.get(position).getName());
            holder.moneyTextView.setText(entities.get(position).getGoodsPrice());
            holder.specTextView.setText(entities.get(position).getSpec());
            holder.sellCountTextView.setText(entities.get(position).getBuyCount());
            holder.addShopCarImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "addShopCarClick", Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }

    class CollectViewHolder {

        private BackupImageView imageView;
        private TextView drugNameTextView;
        private TextView moneyTextView;
        private TextView specTextView;
        private TextView sellCountTextView;
        private ImageView addShopCarImgView;

        public CollectViewHolder(View view) {
            imageView = (BackupImageView) view.findViewById(R.id.collect_img);
            drugNameTextView = (TextView) view.findViewById(R.id.collect_title);
            moneyTextView = (TextView) view.findViewById(R.id.collect_money);
            specTextView = (TextView) view.findViewById(R.id.collect_spec);
            sellCountTextView = (TextView) view.findViewById(R.id.collect_sell_count);
            addShopCarImgView = (ImageView) view.findViewById(R.id.collect_add_shopcar);
        }
    }
}
