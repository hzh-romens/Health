package com.romens.yjk.health.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.helper.UIOpenHelper;

import java.util.Map;


public class ChangePasswordActivity extends BaseActionBarActivityWithAnalytics {

    private MaterialEditText oldPasswordField;
    private MaterialEditText newPasswordField;
    private MaterialEditText newConfirmPasswordField;
    private View headerLabelView;
    private View doneButton;

    private final static int done_button = 1;

    //private FacadesEntity currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //currUser = FacadeManager.getInstance().getFacadesEntity(AppFacadeConfig.KEY);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);

        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar);
        LinearLayout.LayoutParams contentLayoutParams = (LinearLayout.LayoutParams) actionBar.getLayoutParams();
        contentLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        contentLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        actionBar.setLayoutParams(contentLayoutParams);
        setContentView(content, actionBar);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("更改密码");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == done_button) {
                    tryChangePassword();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56));

        Context context = this;
        LinearLayout fragmentView = new LinearLayout(context);
        fragmentView.setOrientation(LinearLayout.VERTICAL);
        content.addView(fragmentView);
        contentLayoutParams = (LinearLayout.LayoutParams) fragmentView.getLayoutParams();
        contentLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        contentLayoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        fragmentView.setLayoutParams(contentLayoutParams);

        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        TextView textView = new TextView(context);
        textView.setText("原密码");
        textView.setTextColor(0xff4d83b3);
        textView.setGravity(Gravity.LEFT);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        fragmentView.addView(textView,
                LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 24, 24, 0));

        oldPasswordField = new MaterialEditText(context);
        oldPasswordField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        oldPasswordField.setBaseColor(0xff212121);
        oldPasswordField.setPrimaryColor(ResourcesConfig.textPrimary);
        oldPasswordField.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
        oldPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        oldPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        oldPasswordField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        oldPasswordField.setMaxLines(1);
        oldPasswordField.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        oldPasswordField.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        oldPasswordField.setHint("原密码");
        oldPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    newPasswordField.requestFocus();
                    newPasswordField.setSelection(newPasswordField.length());
                    return true;
                }
                return false;
            }
        });
        fragmentView.addView(oldPasswordField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 24, 24, 0));

        textView = new TextView(context);
        textView.setText("新密码");
        textView.setTextColor(0xff4d83b3);
        textView.setGravity(Gravity.LEFT);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        fragmentView.addView(textView,
                LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 24, 24, 0));

        newPasswordField = new MaterialEditText(context);
        newPasswordField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        newPasswordField.setBaseColor(0xff212121);
        newPasswordField.setPrimaryColor(ResourcesConfig.textPrimary);
        newPasswordField.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
        newPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPasswordField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        newPasswordField.setMaxLines(1);
        newPasswordField.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        newPasswordField.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        newPasswordField.setHint("新密码(最少六个字符)");
        newPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    newConfirmPasswordField.requestFocus();
                    newConfirmPasswordField.setSelection(newConfirmPasswordField.length());
                    return true;
                }
                return false;
            }
        });
        fragmentView.addView(newPasswordField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 10, 24, 0));

        newConfirmPasswordField = new MaterialEditText(context);
        newConfirmPasswordField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        newConfirmPasswordField.setBaseColor(0xff212121);
        newConfirmPasswordField.setPrimaryColor(ResourcesConfig.textPrimary);
        newConfirmPasswordField.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
        newConfirmPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newConfirmPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newConfirmPasswordField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        newConfirmPasswordField.setMaxLines(1);
        newConfirmPasswordField.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        newConfirmPasswordField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        newConfirmPasswordField.setHint("确认新密码");
        fragmentView.addView(newConfirmPasswordField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 10, 24, 0));

        textView = new TextView(context);
        textView.setText("注意:更改密码后,将会注销用户重新登录");
        textView.setTextColor(0xff757575);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setGravity(Gravity.LEFT);
        textView.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
        fragmentView.addView(textView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 24, 28, 24, 10));
    }

    @Override
    public void onResume() {
        super.onResume();
        oldPasswordField.requestFocus();
        AndroidUtilities.showKeyboard(oldPasswordField);
    }

    private void tryChangePassword() {
        String oldPassword = oldPasswordField.getText().toString();
        String s = UserConfig.formatCode(oldPassword);
        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "请输入原密码", Toast.LENGTH_SHORT).show();
            return;
        }
        String newPassword = newPasswordField.getText().toString();
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        String newConfirmPassword = newConfirmPasswordField.getText().toString();
        if (TextUtils.isEmpty(newConfirmPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "请输入确认新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPassword.length() < 6) {
            Toast.makeText(ChangePasswordActivity.this, "新密码最少六个字符", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(newPassword, newConfirmPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }


        String userId = null;
        String userPassCode = null;
        if (UserConfig.isClientLogined()) {
            // UserConfig.getClientUser()
//            Pair<String, String> token = currUser.handleToken();
//            userId = token == null ? null : token.first;
//            userPassCode = token == null ? null : token.second;
            userId = UserConfig.getClientUserEntity().getPhone();
            userPassCode = UserConfig.getPassCode();

        }

        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(ChangePasswordActivity.this, "获取当前用户信息异常,请稍后重试!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.equals(userPassCode, s)) {
            Toast.makeText(ChangePasswordActivity.this, "原密码不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        //修改密码请求服务器方法
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("NEWPWD", UserConfig.formatCode(newPassword))
                .put("PHONE", userId)
                .put("USERNAME", UserConfig.getClientUserId())
                .put("OLDPWD", oldPassword)
                .put("ORGGUID", UserConfig.getOrgCode())
                .build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "handle", "Changepwd", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
//                .withParser(new JsonParser(new TypeToken<LinkedTreeMap<String, String>>() {
//                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                needHideProgress();
                Toast.makeText(ChangePasswordActivity.this, "请求被拒绝", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                ResponseProtocol<String> response = (ResponseProtocol) msg.protocol;
                if (msg.msg == null) {
                    Toast.makeText(ChangePasswordActivity.this, "修改密码成功，请重新登录", Toast.LENGTH_SHORT).show();
                    UserSession.getInstance().needLoginOut();
                    UIOpenHelper.openLoginActivity(ChangePasswordActivity.this);
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
                }
//                if (errorMsg == null) {
//                    String error = null;
//                    ResponseProtocol<LinkedTreeMap<String, String>> response = (ResponseProtocol) msg.protocol;
//                    LinkedTreeMap<String, String> result = response.getResponse();
//                    if (result != null && result.size() > 0) {
//                        error = result.containsKey("ERROR") ? result.get("ERROR") : null;
//                    }
//                    if (TextUtils.isEmpty(error)) {
//                        UIOpenHelper.openLoginActivity(ChangePasswordActivity.this);
//                        finish();
//                    } else {
//                        Toast.makeText(ChangePasswordActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(ChangePasswordActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }


}
