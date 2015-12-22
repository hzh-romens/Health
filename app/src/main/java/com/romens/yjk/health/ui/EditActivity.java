package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.ui.activity.LightActionBarActivity;
import com.romens.yjk.health.ui.cells.EditTextCell;

/**
 * Created by anlc on 2015/12/21.
 */
public class EditActivity extends LightActionBarActivity {

    private String formActivityName;
    private String result;
    private EditTextCell cell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        String title = data.getStringExtra("activity_title");
        formActivityName = data.getStringExtra("formActivityName");

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 0) {
                    result = cell.getEditText();
                    if (result == null || result.equals("")) {
                        Toast.makeText(EditActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
                    } else {
                        finishActivity();
                    }
                }
            }
        });
        setContentView(content, actionBar);
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        setActionBarTitle(actionBar, title);
        actionBarMenu.addItem(0, R.drawable.ic_done_grey600_24dp);

        cell = new EditTextCell(this);
        cell.setNeedDivider(true);
        content.addView(cell);
    }

    public void finishActivity() {
        Intent intent = new Intent(EditActivity.this, formActivityName.getClass());
        intent.putExtra("editActivityResult", result);
        setResult(UserGuidConfig.RESPONSE_EDITACTIVITY, intent);
        finish();
    }
}
