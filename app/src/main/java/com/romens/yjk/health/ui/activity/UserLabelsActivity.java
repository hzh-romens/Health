package com.romens.yjk.health.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.UserAttributeEntity;
import com.romens.yjk.health.helper.LabelHelper;
import com.romens.yjk.health.ui.cells.TextDetailSelectCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/12/19.
 */
public class UserLabelsActivity extends LightActionBarActivity {

    private ListView listView;
    private ListAdapter adapter;

    private final List<UserAttributeEntity> userAttributes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });
        setContentView(content, actionBar);

        ActionBarMenu actionBarMenu = actionBar.createMenu();
        setActionBarTitle(actionBar, "个人信息");

        listView = new ListView(this);
        content.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelector(R.drawable.list_selector);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        listView.setAdapter(adapter = new ListAdapter(this));
        bindData();
    }

    public void bindData() {
        userAttributes.clear();
        userAttributes.add(new UserAttributeEntity("a", "有无遗传病史").addValue("1", "有"));
        userAttributes.add(new UserAttributeEntity("a", "饮食偏好").addValue("0", "低盐").addValue("1", "低脂"));
        adapter.notifyDataSetChanged();
    }

    class ListAdapter extends BaseFragmentAdapter {
        private Context adapterContext;

        public ListAdapter(Context context) {
            adapterContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public int getCount() {
            return userAttributes.size();
        }

        @Override
        public UserAttributeEntity getItem(int i) {
            return userAttributes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            int type = getItemViewType(position);
            if (type == 0) {
                if (view == null) {
                    view = new TextDetailSelectCell(adapterContext);
                }
                TextDetailSelectCell cell = (TextDetailSelectCell) view;
                cell.setMultilineDetail(true);
                UserAttributeEntity entity = getItem(position);
                CharSequence labels = LabelHelper.createChipForUserInfoLabels(entity.valuesDesc);
                cell.setTextAndValue(entity.name, labels, true);
            }
            return view;
        }
    }
}
