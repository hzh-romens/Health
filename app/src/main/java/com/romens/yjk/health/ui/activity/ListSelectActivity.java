package com.romens.yjk.health.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/10/28.
 */
public class ListSelectActivity extends BaseActionBarActivityWithAnalytics {
    public static final String ARGUMENTS_KEY_TITLE = "title";
    public static final String ARGUMENTS_KEY_NAME_LIST = "name_list";
    public static final String ARGUMENTS_KEY_VALUE_LIST = "value_list";
    public static final String ARGUMENTS_KEY_ONLY_SELECT = "only_select";
    public static final String RESULT_KEY_SELECTED_NAME = "selected_name";
    public static final String RESULT_KEY_SELECTED_VALUE = "selected_value";

    private String title;
    protected final List<String> nameList = new ArrayList<>();
    protected final List<String> valueList = new ArrayList<>();

    private boolean isOnlySelect = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        title = intent.getStringExtra(ARGUMENTS_KEY_TITLE);
        isOnlySelect = intent.getBooleanExtra(ARGUMENTS_KEY_ONLY_SELECT, true);
        ArrayList<String> nameListArg = intent.getStringArrayListExtra(ARGUMENTS_KEY_NAME_LIST);
        nameList.clear();
        if (nameListArg != null && nameListArg.size() > 0) {
            nameList.addAll(nameListArg);
        }
        ArrayList<String> valueListArg = intent.getStringArrayListExtra(ARGUMENTS_KEY_VALUE_LIST);
        valueList.clear();
        if (valueListArg != null && valueListArg.size() > 0) {
            valueList.addAll(valueListArg);
        }


        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBar.setTitle(title);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    cancelSelect();
                }
            }
        });
        setContentView(content, actionBar);

        ListView listView = new ListView(this);
        content.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelector(R.drawable.list_selector);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isOnlySelect) {
                    returnSelectedItem(position);
                } else {
                    onItemSelected(position);
                }
            }
        });

        ListAdapter adapter = new ListAdapter(this);
        listView.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelSelect();
    }

    protected void onItemSelected(int position) {
    }

    protected void returnSelectedItem(int position) {
        String name = nameList.get(position);
        String value = null;
        if (position >= 0 && position < valueList.size()) {
            value = valueList.get(position);
        }
        Intent data = new Intent();
        data.putExtra(RESULT_KEY_SELECTED_NAME, name);
        data.putExtra(RESULT_KEY_SELECTED_VALUE, value);
        setResult(RESULT_OK, data);
        finish();
    }

    private void cancelSelect() {
        setResult(RESULT_CANCELED);
        finish();
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
            return nameList.size();
        }

        @Override
        public String getItem(int i) {
            return nameList.get(i);
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
                    view = new TextSettingsCell(adapterContext);
                }
                TextSettingsCell cell = (TextSettingsCell) view;
                String name = getItem(position);
                cell.setText(name, true);
            }
            return view;
        }
    }
}
