package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;

/**
 * Created by anlc on 2015/12/21.
 */
public class EditActivity extends DarkActionBarActivity {

    private String formActivityName;
    private String result;
    private OrganizationCodeView cell;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        title = data.getStringExtra("activity_title");
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
        actionBar.setTitle(title);
        actionBarMenu.addItem(0, R.drawable.ic_done);

        cell = new OrganizationCodeView(this);
        content.addView(cell);
    }

    public void finishActivity() {
        Intent intent = new Intent(EditActivity.this, formActivityName.getClass());
        intent.putExtra("editActivityResult", result);
        setResult(UserGuidConfig.RESPONSE_EDITACTIVITY, intent);
        finish();
    }

    @Override
    protected String getActivityName() {
        return String.format("录入信息[%s]", title);
    }

    public class OrganizationCodeView extends LinearLayout {

        private EditText organizationCodeField;

        public OrganizationCodeView(Context context) {
            super(context);
            setOrientation(VERTICAL);

            LinearLayout.LayoutParams layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
            layoutParams.topMargin = AndroidUtilities.dp(20);
            layoutParams.leftMargin = AndroidUtilities.dp(16);
            layoutParams.rightMargin = AndroidUtilities.dp(16);
            setLayoutParams(layoutParams);

            organizationCodeField = new EditText(context);
            organizationCodeField.setInputType(InputType.TYPE_CLASS_TEXT);
            organizationCodeField.setTextColor(0xff212121);
            organizationCodeField.setHintTextColor(0xff979797);
            organizationCodeField.setPadding(0, 0, 0, 0);
            AndroidUtilities.clearCursorDrawable(organizationCodeField);
            organizationCodeField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            organizationCodeField.setMaxLines(1);
            organizationCodeField.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            organizationCodeField.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            addView(organizationCodeField);
            layoutParams = (LinearLayout.LayoutParams) organizationCodeField.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = AndroidUtilities.dp(36);
            organizationCodeField.setLayoutParams(layoutParams);

            TextView textView = new TextView(context);
            textView.setText("完善详细信息我们更好的为您提供用药咨询和促销信息");
            textView.setTextColor(0xff757575);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            textView.setGravity(Gravity.LEFT);
            textView.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            addView(textView);
            layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.topMargin = AndroidUtilities.dp(28);
            layoutParams.bottomMargin = AndroidUtilities.dp(10);
            layoutParams.gravity = Gravity.LEFT;
            textView.setLayoutParams(layoutParams);

            AndroidUtilities.showKeyboard(organizationCodeField);
            organizationCodeField.requestFocus();
        }

        public String getEditText() {
            return organizationCodeField.getText().toString().trim();
        }
    }
}
