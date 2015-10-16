package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.List;

/**
 * Created by anlc on 2015/10/15.
 */
public class CollectFragment extends BaseFragment {

    private Context mContext;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;


    public CollectFragment(Context context) {
        this.mContext = context;
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(mContext);
        refreshLayout = new SwipeRefreshLayout(mContext);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        frameLayout.addView(refreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(mContext);
        refreshLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return frameLayout;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }

    class CollectFragmentAdapter extends BaseAdapter {

        private Context context;
        private List<AllOrderEntity> entities;

        public CollectFragmentAdapter(Context context, List<AllOrderEntity> entities) {
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
            return null;
        }
    }

    class CollectViewHolder{
    }
}
