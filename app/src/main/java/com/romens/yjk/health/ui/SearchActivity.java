package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.components.FlowLayout;
import com.romens.yjk.health.ui.utils.UIUtils;

/**
 * Created by anlc on 2015/8/17.
 */
public class SearchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);

        ActionBarMenu actionBarMenu=actionBar.createMenu();
        ActionBarMenuItem searchItem=actionBarMenu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true, true);
        searchItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {

            @Override
            public boolean canCollapseSearch() {
                return false;
            }

            @Override
            public void onTextChanged(EditText var1) {
                Log.e("tag", "search->" + var1.getText());
            }
        });


        FlowLayout layout = new FlowLayout(this);
        layout.setHorizontalSpacing(UIUtils.dip2px(20));
        layout.setVerticalSpacing(UIUtils.dip2px(10));
        layout.setPadding(UIUtils.dip2px(10), UIUtils.dip2px(20), UIUtils.dip2px(10), UIUtils.dip2px(4));
        layout.setMaxLines(2);

        for (int i = 0; i < 7; i++) {
            TextView textView = new TextView(this);
            textView.setMaxLines(1);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(UIUtils.dip2px(4), UIUtils.dip2px(10), UIUtils.dip2px(4), UIUtils.dip2px(10));
            textView.setBackgroundResource(R.drawable.btn_primary);
            textView.setText("item" + i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SearchActivity.this,"-->"+v.getId(),Toast.LENGTH_SHORT).show();
                }
            });
            layout.addView(textView);
        }

        container.addView(layout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        actionBar.setTitle("搜索");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 0) {
                }
            }
        });

        actionBar.openSearchField("");

    }
}
