package com.romens.yjk.health.ui.activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.romens.android.AndroidUtilities;
import com.romens.android.PhoneFormat.PhoneFormat;
import com.romens.android.log.FileLog;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.im.IMHXSDKHelper;
import com.romens.yjk.health.ui.BaseActivity;
import com.romens.yjk.health.ui.components.SlideView;
import com.romens.yjk.health.ui.components.TypefaceSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by siery on 15/6/24.
 */
public class LoginActivity extends BaseActivity {
    private final static int PAGER_COUNT = 3;
    private int currentViewNum = 0;
    private SlideView[] views = new SlideView[PAGER_COUNT];
    private ProgressDialog progressDialog;

    private final static int done_button = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserConfig.loadConfig();
        ActionBarLayout.LinearLayoutContainer contentView = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        contentView.addView(actionBar);

        ScrollView fragmentView = new ScrollView(this);
        ScrollView scrollView = fragmentView;
        scrollView.setFillViewport(true);
        contentView.addView(fragmentView);

        FrameLayout frameLayout = new FrameLayout(this);
        scrollView.addView(frameLayout);
        ScrollView.LayoutParams layoutParams = (ScrollView.LayoutParams) frameLayout.getLayoutParams();
        layoutParams.width = ScrollView.LayoutParams.MATCH_PARENT;
        layoutParams.height = ScrollView.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        frameLayout.setLayoutParams(layoutParams);

        //views[0] = new OrganizationCodeView(this);
        views[0] = new PhoneView(this);
        views[1] = new LoginActivityPasswordView(this);
        views[2] = new LoginActivitySmsView(this);

        for (int a = 0; a < PAGER_COUNT; a++) {
            views[a].setVisibility(a == 0 ? View.VISIBLE : View.GONE);
            frameLayout.addView(views[a]);
            FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) views[a].getLayoutParams();
            layoutParams1.width = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams1.height = a == 0 ? FrameLayout.LayoutParams.WRAP_CONTENT : FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams1.leftMargin = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26 : 18);
            layoutParams1.rightMargin = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26 : 18);
            layoutParams1.topMargin = AndroidUtilities.dp(30);
            layoutParams1.gravity = Gravity.TOP | Gravity.LEFT;
            views[a].setLayoutParams(layoutParams1);
        }

        setContentView(contentView, actionBar);
        actionBar = getMyActionBar();
        actionBar.setTitle(getString(R.string.app_name));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == done_button) {
                    views[currentViewNum].onNextPressed();
                } else if (id == -1) {
                    onBackPressed();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56));

        currentViewNum = 0;
        actionBar.setTitle(views[currentViewNum].getHeaderName());
        for (int a = 0; a < views.length; a++) {
            if (currentViewNum == a) {
                actionBar.setBackButtonImage(views[a].needBackButton() ? R.drawable.ic_ab_back : 0);
                views[a].setVisibility(View.VISIBLE);
                views[a].onShow();
            } else {
                views[a].setVisibility(View.GONE);
            }
        }

        if (UserConfig.isClientActivated()) {
            //登出后,不初始化会报异常
            Bundle params = new Bundle();
            params.putString(OrganizationCodeView.PARAM_ORGAN_CODE, UserConfig.getOrgCode());
            params.putString(OrganizationCodeView.PARAM_ORGAN_NAME, UserConfig.getOrgName());
            params.putString(PhoneView.PARAM_PHONE, UserConfig.getClientUserPhone());
            setPage(0, true, params, false);
        }
    }

    @Override
    protected boolean enableResetNotifier() {
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        for (SlideView v : views) {
            if (v != null) {
                v.onDestroyActivity();
            }
        }
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e("romens", e);
            }
            progressDialog = null;
        }
    }

    private void putBundleToEditor(Bundle bundle, SharedPreferences.Editor editor, String prefix) {
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            Object obj = bundle.get(key);
            if (obj instanceof String) {
                if (prefix != null) {
                    editor.putString(prefix + "_|_" + key, (String) obj);
                } else {
                    editor.putString(key, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (prefix != null) {
                    editor.putInt(prefix + "_|_" + key, (Integer) obj);
                } else {
                    editor.putInt(key, (Integer) obj);
                }
            } else if (obj instanceof Bundle) {
                putBundleToEditor((Bundle) obj, editor, key);
            }
        }
    }

    @Override
    public void onBackPressed() {
        boolean isFinish = false;
        if (currentViewNum == 0 || currentViewNum == 2) {
            for (SlideView v : views) {
                if (v != null) {
                    v.onDestroyActivity();
                }
            }
            isFinish = true;
        } else if (currentViewNum == 1) {
            views[currentViewNum].onBackPressed();
        }
        if (isFinish) {
            needFinishActivity();
            finish();
        }
    }

    public void needShowAlert(String title, String text) {
        if (text == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton("确定", null);
        showDialog(builder.create());
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        if (Build.VERSION.SDK_INT > 13) {
            final SlideView outView = views[currentViewNum];
            final SlideView newView = views[page];
            currentViewNum = page;
            ActionBar actionBar = getMyActionBar();
            actionBar.setBackButtonImage(newView.needBackButton() ? R.drawable.ic_ab_back : 0);

            newView.setParams(params);
            actionBar.setTitle(newView.getHeaderName());
            newView.onShow();
            newView.setX(back ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x);
            outView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    outView.setVisibility(View.GONE);
                    outView.setX(0);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            }).setDuration(300).translationX(back ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x).start();
            newView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    newView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            }).setDuration(300).translationX(0).start();
        } else {
            ActionBar actionBar = getMyActionBar();
            actionBar.setBackButtonImage(views[page].needBackButton() ? R.drawable.ic_ab_back : 0);
            views[currentViewNum].setVisibility(View.GONE);
            currentViewNum = page;
            views[page].setParams(params);
            views[page].setVisibility(View.VISIBLE);
            actionBar.setTitle(views[page].getHeaderName());
            views[page].onShow();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSelfArgs(outState);
    }

    public void saveSelfArgs(Bundle outState) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("currentViewNum", currentViewNum);
            for (int a = 0; a <= currentViewNum; a++) {
                SlideView v = views[a];
                if (v != null) {
                    v.saveStateParams(bundle);
                }
            }
            SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences("logininfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            putBundleToEditor(bundle, editor, null);
            editor.commit();
        } catch (Exception e) {
            FileLog.e("romens", e);
        }
    }

    public void needFinishActivity() {
    }

    protected void onLoginCallback(boolean isSuccess) {
        if (isSuccess) {
            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.loginSuccess);
        }
        finish();
    }

    public class OrganizationCodeView extends SlideView {

        private EditText organizationCodeField;
        private boolean nextPressed = false;

        public OrganizationCodeView(Context context) {
            super(context);
            setOrientation(VERTICAL);

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(VERTICAL);
            addView(linearLayout);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.topMargin = AndroidUtilities.dp(20);
            linearLayout.setLayoutParams(layoutParams);

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
            linearLayout.addView(organizationCodeField);
            layoutParams = (LinearLayout.LayoutParams) organizationCodeField.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = AndroidUtilities.dp(36);
            organizationCodeField.setLayoutParams(layoutParams);
            organizationCodeField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_NEXT) {
                        onNextPressed();
                        return true;
                    }
                    return false;
                }
            });

            TextView textView = new TextView(context);
            textView.setText("输入注册的公司组织机构代码.如果未注册请联系青岛雨人软件科技有限公司");
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

        public static final String PARAM_ORGAN_CODE = "OrganizationCode";
        public static final String PARAM_ORGAN_NAME = "OrganizationName";

        @Override
        public void onNextPressed() {
            if (nextPressed) {
                return;
            }
            final String organizationCode = organizationCodeField.getText().toString();
            if (TextUtils.isEmpty(organizationCode)) {
                needShowAlert(getString(R.string.app_name), "请输入组织机构代码");
                return;
            }
            final Bundle params = new Bundle();
            params.putString(PARAM_ORGAN_CODE, organizationCode);
            nextPressed = true;
            needShowProgress("正在验证组织机构代码...");
            Map<String, String> args = new HashMap<>();
            args.put("ORGGUID", organizationCode);
            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "unloadhandle", "CheckOrgCode", args);
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol)
                    .withParser(new JsonParser(new TypeToken<LinkedTreeMap<String, String>>() {
                    }))
                    .build();
            FacadeClient.request(LoginActivity.this, message, new FacadeClient.FacadeCallback() {
                @Override
                public void onTokenTimeout(Message msg) {

                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    nextPressed = false;
                    if (errorMsg == null) {
                        ResponseProtocol<LinkedTreeMap<String, String>> response = (ResponseProtocol) msg.protocol;
                        LinkedTreeMap<String, String> result = response.getResponse();
                        if (result != null && result.size() > 0) {
                            String isValidity = result.get("ISVALIDITY");
                            if (TextUtils.equals("1", isValidity)) {
                                String orgName = result.get("COMNAME");
                                String appKey = result.get("appkey");
                                //UserConfig.setHXAppId(appKey);
                                params.putString(PARAM_ORGAN_NAME, orgName);
                                setPage(1, true, params, false);
                            } else {
                                String message = result.get("MESSAGE");
                                needShowAlert(getString(R.string.app_name), message);
                            }
                        }
                    } else {
                        if (errorMsg.code != 0) {
                            needShowAlert(getString(R.string.app_name), errorMsg.msg);
                        }
                    }
                    needHideProgress();
                }
            });
        }

        @Override
        public void onShow() {
            super.onShow();
            if (organizationCodeField != null) {
                organizationCodeField.requestFocus();
                organizationCodeField.setSelection(organizationCodeField.length());
            }
        }

        @Override
        public String getHeaderName() {
            return "组织机构代码";
        }

        @Override
        public void saveStateParams(Bundle bundle) {
            String organizationCode = organizationCodeField.getText().toString();
            if (organizationCode != null && organizationCode.length() != 0) {
                bundle.putString("organization_code", organizationCode);
            }
        }

        @Override
        public void restoreStateParams(Bundle bundle) {
            String organizationCode = bundle.getString("organization_code");
            if (organizationCode != null) {
                organizationCodeField.setText(organizationCode);
            }
        }
    }

    public class LoginActivitySmsView extends SlideView {

        private String requestPhone;
        private String requestFlag;
        private MaterialEditText codeField;
        private TextView confirmTextView;
        private TextView timeText;
        private TextView problemText;
        private Bundle currentParams;

        private Timer timeTimer;
        private final Object timerSync = new Object();
        private volatile int time = 60000;
        private double lastCurrentTime;
        private boolean waitingForSms = false;
        private boolean nextPressed = false;
        private String lastError = "";

        public LoginActivitySmsView(Context context) {
            super(context);

            setOrientation(VERTICAL);

            confirmTextView = new TextView(context);
            confirmTextView.setTextColor(0xff757575);
            confirmTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            confirmTextView.setGravity(Gravity.LEFT);
            confirmTextView.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            addView(confirmTextView);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) confirmTextView.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.LEFT;
            confirmTextView.setLayoutParams(layoutParams);

            codeField = new MaterialEditText(context);
            codeField.setBaseColor(0xff212121);
            codeField.setPrimaryColor(ResourcesConfig.textPrimary);
            codeField.setHint("随机密码");

            AndroidUtilities.clearCursorDrawable(codeField);
            codeField.setHintTextColor(0xff979797);
            codeField.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            codeField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            codeField.setInputType(InputType.TYPE_CLASS_PHONE);
            codeField.setMaxLines(1);
            addView(codeField);
            layoutParams = (LinearLayout.LayoutParams) codeField.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams.topMargin = AndroidUtilities.dp(20);
            codeField.setLayoutParams(layoutParams);
            codeField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_NEXT) {
                        onNextPressed();
                        return true;
                    }
                    return false;
                }
            });

            timeText = new TextView(context);
            timeText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            timeText.setTextColor(0xff757575);
            timeText.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            timeText.setGravity(Gravity.LEFT);
            addView(timeText);
            layoutParams = (LinearLayout.LayoutParams) timeText.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.LEFT;
            layoutParams.topMargin = AndroidUtilities.dp(30);
            timeText.setLayoutParams(layoutParams);

            problemText = new TextView(context);
            problemText.setText("没有收到短信,重新发送?");
            problemText.setVisibility(time < 1000 ? VISIBLE : GONE);
            problemText.setGravity(Gravity.LEFT);
            problemText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            problemText.setTextColor(getResources().getColor(R.color.text_primary));
            problemText.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            problemText.setPadding(0, AndroidUtilities.dp(2), 0, AndroidUtilities.dp(12));
            addView(problemText);
            layoutParams = (LinearLayout.LayoutParams) problemText.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.LEFT;
            layoutParams.topMargin = AndroidUtilities.dp(20);
            problemText.setLayoutParams(layoutParams);
            problemText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSMS();
                }
            });

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
            addView(linearLayout);
            layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            linearLayout.setLayoutParams(layoutParams);

            TextView wrongNumber = new TextView(context);
            wrongNumber.setGravity(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);
            wrongNumber.setTextColor(getResources().getColor(R.color.text_primary));
            wrongNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            wrongNumber.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            wrongNumber.setPadding(0, AndroidUtilities.dp(24), 0, 0);
            linearLayout.addView(wrongNumber);
            layoutParams = (LinearLayout.LayoutParams) wrongNumber.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
            layoutParams.bottomMargin = AndroidUtilities.dp(10);
            wrongNumber.setLayoutParams(layoutParams);
            wrongNumber.setText("更改手机号码?");
            wrongNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                    setPage(0, true, currentParams, true);
                }
            });
        }

        @Override
        public String getHeaderName() {
            return "短信随机密码";
        }

        public static final String PARAM_REQUEST_FLAG = "request_flag";

        @Override
        public void setParams(Bundle params) {
            if (params == null) {
                return;
            }
            codeField.setText("");
            currentParams = params;
            waitingForSms = true;
            requestPhone = params.getString(PhoneView.PARAM_PHONE);
            requestFlag = params.getString(PARAM_REQUEST_FLAG, "0");
            time = 60000;

            if (TextUtils.isEmpty(requestPhone)) {
                return;
            }
            PhoneFormat.getInstance().init("86");
            String number = PhoneFormat.getInstance().format(requestPhone);
            String str = String.format("我们已经发送随机密码到你的手机 %s", number);
            try {
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(str);
                TypefaceSpan span = new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                int idx = str.indexOf(number);
                stringBuilder.setSpan(span, idx, idx + number.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                confirmTextView.setText(stringBuilder);
            } catch (Exception e) {
                FileLog.e("romens", e);
                confirmTextView.setText(str);
            }

            AndroidUtilities.showKeyboard(codeField);
            codeField.requestFocus();

            destroyTimer();
            clearTimeText();

            problemText.setVisibility(GONE);
            sendSMS();
        }

        private void initTimeText(int minutes, int seconds) {
            time = 60000;
            lastCurrentTime = System.currentTimeMillis();
            timeText.setText(String.format("在 %1$d:%2$02d 之后可以重新发送", minutes, seconds));
        }

        private void clearTimeText() {
            timeText.setText("");
        }

        private void sendSMS() {
            final Bundle params = currentParams;
            waitingForSms = true;
            needShowProgress("正在请求发送随机密码...");
            Map<String, String> args = new HashMap<>();
            String orgCode = params.getString(OrganizationCodeView.PARAM_ORGAN_CODE);
            args.put("ORGGUID", orgCode);
            String userName = params.getString(PhoneView.PARAM_HX_ID);
            args.put("USERNAME", userName);
            args.put("PHONENUMBER", requestPhone);
            args.put("FLAG", requestFlag);

            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "sendsms", args);
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol)
                    .withParser(new JsonParser(new TypeToken<LinkedTreeMap<String, String>>() {
                    }))
                    .build();
            FacadeClient.request(LoginActivity.this, message, new FacadeClient.FacadeCallback() {
                @Override
                public void onTokenTimeout(Message msg) {
                    waitingForSms = false;
                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    waitingForSms = false;
                    needHideProgress();
                    String error;
                    if (errorMsg == null) {
                        ResponseProtocol<LinkedTreeMap<String, String>> response = (ResponseProtocol) msg.protocol;
                        LinkedTreeMap<String, String> result = response.getResponse();
                        if (result != null && result.size() > 0) {
                            String smsResult = result.get("SMSRESULT");
                            if (TextUtils.equals("1", smsResult)) {
                                destroyTimer();
                                initTimeText(1, 0);
                                createTimer();
                                problemText.setVisibility(View.GONE);
                                return;
                            } else {
                                error = smsResult;
                            }
                        } else {
                            error = "未知";
                        }
                    } else {
                        error = "内部错误";
                    }
                    Toast.makeText(getContext(), String.format("请求发送随机密码发生未知问题,请稍候再试\n[%s]", error), Toast.LENGTH_SHORT).show();
                    clearTimeText();
                    destroyTimer();
                    problemText.setVisibility(View.VISIBLE);
                }
            });
        }

        private void createTimer() {
            if (timeTimer != null) {
                return;
            }
            timeTimer = new Timer();
            timeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    double currentTime = System.currentTimeMillis();
                    double diff = currentTime - lastCurrentTime;
                    time -= diff;
                    lastCurrentTime = currentTime;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (time > 0) {
                                int minutes = time / 1000 / 60;
                                int seconds = time / 1000 - minutes * 60;
                                timeText.setText(String.format("在 %1$d:%2$02d 之后可以重新发送", minutes, seconds));
                                problemText.setVisibility(GONE);
                            } else {
                                timeText.setText("");
                                problemText.setVisibility(View.VISIBLE);
                                destroyTimer();
                            }
                        }
                    });
                }
            }, 0, 1000);
        }

        private void destroyTimer() {
            try {
                synchronized (timerSync) {
                    if (timeTimer != null) {
                        timeTimer.cancel();
                        timeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e("romens", e);
            }
        }

        private void onPassCodeError(boolean clear) {
            Vibrator v = (Vibrator) LoginActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(200);
            }
            if (clear) {
                codeField.setText("");
            }
            AndroidUtilities.shakeTextView(codeField, 2, 0);
        }

        @Override
        public void onNextPressed() {
            if (waitingForSms) {
                return;
            }
            if (nextPressed) {
                return;
            }
            final String userName = currentParams.getString(PhoneView.PARAM_HX_ID);
            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(LoginActivity.this, "请求的账号解析异常", Toast.LENGTH_SHORT).show();
                return;
            }
            String code = codeField.getText().toString();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(LoginActivity.this, "请输入随即密码", Toast.LENGTH_SHORT).show();
                onPassCodeError(false);
                return;
            }
            needShowProgress("正在验证用户信息...");
            final String passCode = UserConfig.formatCode(code);
            nextPressed = true;
            waitingForSms = false;
            EMChatManager.getInstance().login(userName, passCode, new EMCallBack() {

                @Override
                public void onSuccess() {
                    nextPressed = false;
                    // 登陆成功，保存用户名密码
                    IMHXSDKHelper.getInstance().setHXId(userName);
                    IMHXSDKHelper.getInstance().setPassword(passCode);

                    UserConfig.clearUser();
                    UserConfig.Data userConfigData = new UserConfig.Data();
                    String orgCode = currentParams.getString(OrganizationCodeView.PARAM_ORGAN_CODE);
                    String orgName = currentParams.getString(OrganizationCodeView.PARAM_ORGAN_NAME);
                    userConfigData.setOrg(orgCode, orgName);
                    String phoneNumber = currentParams.getString(PhoneView.PARAM_PHONE);
                    userConfigData.setPhoneNumber(phoneNumber);
                    userConfigData.setLogin(userName, passCode);
                    UserConfig.saveConfig(userConfigData);
                    UserConfig.loadConfig();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            needHideProgress();
                            needFinishActivity();
                            onLoginCallback(true);
                        }
                    });
                    //登录成功重新刷新一下购物车图标的数量
                    requestShopCarCountData();
                }

                @Override
                public void onProgress(int progress, String status) {
                }

                @Override
                public void onError(final int code, final String message) {
                    nextPressed = false;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            needHideProgress();
                            Toast.makeText(getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        @Override
        public void onBackPressed() {
            destroyTimer();
            currentParams = null;
            waitingForSms = false;
        }

        @Override
        public void onDestroyActivity() {
            super.onDestroyActivity();
            destroyTimer();
            waitingForSms = false;
        }

        @Override
        public void onShow() {
            super.onShow();
            if (codeField != null) {
                codeField.requestFocus();
                codeField.setSelection(codeField.length());
            }
        }

        @Override
        public void saveStateParams(Bundle bundle) {
            String code = codeField.getText().toString();
            if (code != null && code.length() != 0) {
                bundle.putString("smsview_code", code);
            }
            if (currentParams != null) {
                bundle.putBundle("smsview_params", currentParams);
            }
            if (time != 0) {
                bundle.putInt("time", time);
            }
        }

        @Override
        public void restoreStateParams(Bundle bundle) {
            currentParams = bundle.getBundle("smsview_params");
            if (currentParams != null) {
                setParams(currentParams);
            }
            String code = bundle.getString("smsview_code");
            if (code != null) {
                codeField.setText(code);
            }
            Integer t = bundle.getInt("time");
            if (t != 0) {
                time = t;
            }
        }
    }

    public class LoginActivityPasswordView extends SlideView {

        private MaterialEditText codeField;
        private TextView confirmTextView;
        private TextView resetAccountButton;
        private TextView resetAccountText;

        private Bundle currentParams;
        private boolean nextPressed;

        public LoginActivityPasswordView(Context context) {
            super(context);
            setOrientation(VERTICAL);

            confirmTextView = new TextView(context);
            confirmTextView.setTextColor(0xff757575);
            confirmTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            confirmTextView.setGravity(Gravity.LEFT);
            confirmTextView.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            confirmTextView.setText("输入账户密码,验证用户安全性");
            addView(confirmTextView);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) confirmTextView.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.LEFT;
            confirmTextView.setLayoutParams(layoutParams);

            codeField = new MaterialEditText(context);
            codeField.setBaseColor(0xff212121);
            codeField.setPrimaryColor(ResourcesConfig.textPrimary);
            codeField.setHint("密码");
            codeField.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            codeField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            codeField.setMaxLines(1);
            codeField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            codeField.setTypeface(Typeface.DEFAULT);
            addView(codeField);
            layoutParams = (LinearLayout.LayoutParams) codeField.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams.topMargin = AndroidUtilities.dp(20);
            codeField.setLayoutParams(layoutParams);
            codeField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_NEXT) {
                        onNextPressed();
                        return true;
                    }
                    return false;
                }
            });
            AndroidUtilities.clearCursorDrawable(codeField);

            TextView cancelButton = new TextView(context);
            cancelButton.setGravity(Gravity.LEFT | Gravity.TOP);
            cancelButton.setTextColor(getResources().getColor(R.color.text_primary));
            cancelButton.setText("忘记密码?");
            cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            cancelButton.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            cancelButton.setPadding(0, AndroidUtilities.dp(14), 0, 0);
            addView(cancelButton);
            layoutParams = (LinearLayout.LayoutParams) cancelButton.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            cancelButton.setLayoutParams(layoutParams);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("我们将向注册的手机号码发送一条包含随即密码的短信");
                    builder.setTitle("忘记密码");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Bundle params = currentParams;
                            params.putString(LoginActivitySmsView.PARAM_REQUEST_FLAG, "1");
                            setPage(2, true, params, true);
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    showDialog(builder.create());
                }
            });

            resetAccountButton = new TextView(context);
            resetAccountButton.setGravity(Gravity.LEFT | Gravity.TOP);
            resetAccountButton.setTextColor(0xffff6666);
            resetAccountButton.setVisibility(GONE);
            resetAccountButton.setText("点击激活账户");
            resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            resetAccountButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            resetAccountButton.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            resetAccountButton.setPadding(0, AndroidUtilities.dp(14), 0, 0);
            addView(resetAccountButton);
            layoutParams = (LinearLayout.LayoutParams) resetAccountButton.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            layoutParams.topMargin = AndroidUtilities.dp(34);
            resetAccountButton.setLayoutParams(layoutParams);
            resetAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("我们将向注册的手机号码发送一条包含初始密码的短信");
                    builder.setTitle("激活账户");
                    builder.setPositiveButton("激活", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Bundle params = currentParams;
                            params.putString(LoginActivitySmsView.PARAM_REQUEST_FLAG, "0");
                            setPage(3, true, params, true);
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    showDialog(builder.create());
                }
            });
            resetAccountText = new TextView(context);
            resetAccountText.setGravity(Gravity.LEFT | Gravity.TOP);
            resetAccountText.setVisibility(GONE);
            resetAccountText.setTextColor(0xff757575);
            resetAccountText.setText(String.format("为什么出现激活账户?用户首次登录%s,账户状态未未激活状态.需要激活才可以正常使用某些功能.", getString(R.string.app_name)));
            resetAccountText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            resetAccountText.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            addView(resetAccountText);
            layoutParams = (LinearLayout.LayoutParams) resetAccountText.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            layoutParams.bottomMargin = AndroidUtilities.dp(14);
            layoutParams.topMargin = AndroidUtilities.dp(7);
            resetAccountText.setLayoutParams(layoutParams);
        }

        @Override
        public String getHeaderName() {
            return "密码";
        }

        @Override
        public void setParams(Bundle params) {
            if (params == null) {
                return;
            }
            if (!params.getBoolean("IsValidityUser", false)) {
                resetAccountButton.setVisibility(VISIBLE);
                resetAccountText.setVisibility(VISIBLE);
                codeField.setHint("密码不可用,点击激活账户");
                codeField.setEnabled(false);
                codeField.clearFocus();
                AndroidUtilities.hideKeyboard(codeField);
            } else {
                resetAccountButton.setVisibility(GONE);
                resetAccountText.setVisibility(GONE);
                codeField.setHint("密码");
                codeField.setEnabled(true);
                codeField.requestFocus();
                AndroidUtilities.showKeyboard(codeField);
            }
            codeField.setText("");
            currentParams = params;
        }

        private void onPasscodeError(boolean clear) {
            Vibrator v = (Vibrator) LoginActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(200);
            }
            if (clear) {
                codeField.setText("");
            }
            AndroidUtilities.shakeTextView(confirmTextView, 2, 0);
        }

        @Override
        public void onNextPressed() {
            if (nextPressed) {
                return;
            }

            String oldPassword = codeField.getText().toString();
            if (oldPassword.length() == 0) {
                onPasscodeError(false);
                return;
            }
            nextPressed = true;
            needShowProgress("正在验证用户信息...");
            final String userName = currentParams.getString(PhoneView.PARAM_HX_ID);
            final String password = UserConfig.formatCode(oldPassword);
            EMChatManager.getInstance().login(userName, password, new EMCallBack() {

                @Override
                public void onSuccess() {
                    nextPressed = false;
                    // 登陆成功，保存用户名密码
                    IMHXSDKHelper.getInstance().setHXId(userName);
                    IMHXSDKHelper.getInstance().setPassword(password);

                    UserConfig.clearUser();
                    UserConfig.Data userConfigData = new UserConfig.Data();
                    String orgCode = currentParams.getString(OrganizationCodeView.PARAM_ORGAN_CODE);
                    String orgName = currentParams.getString(OrganizationCodeView.PARAM_ORGAN_NAME);
                    userConfigData.setOrg(orgCode, orgName);
                    String phoneNumber = currentParams.getString(PhoneView.PARAM_PHONE);
                    String userGuid = currentParams.getString("UserGuid");
                    userConfigData.setPhoneNumber(phoneNumber);
                    userConfigData.setLogin(userName, password);
                    userConfigData.setUserGuid(userGuid);
                    UserConfig.saveConfig(userConfigData);
                    UserConfig.loadConfig();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            needHideProgress();
                            needFinishActivity();
                            onLoginCallback(true);
                        }
                    });
                    //登录成功重新刷新一下购物车图标的数量
                    requestShopCarCountData();
                }

                @Override
                public void onProgress(int progress, String status) {
                }

                @Override
                public void onError(final int code, final String message) {
                    nextPressed = false;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            needHideProgress();
                            Toast.makeText(getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        @Override
        public boolean needBackButton() {
            return true;
        }

        @Override
        public void onBackPressed() {
            setPage(0, true, currentParams, false);
            currentParams = null;
        }

        @Override
        public void onShow() {
            super.onShow();
            if (codeField != null) {
                codeField.requestFocus();
                codeField.setSelection(codeField.length());
            }
        }

        @Override
        public void saveStateParams(Bundle bundle) {
            String code = codeField.getText().toString();
            if (code != null && code.length() != 0) {
                bundle.putString("passview_code", code);
            }
            if (currentParams != null) {
                bundle.putBundle("passview_params", currentParams);
            }
        }

        @Override
        public void restoreStateParams(Bundle bundle) {
            currentParams = bundle.getBundle("passview_params");
            if (currentParams != null) {
                setParams(currentParams);
            }
            String code = bundle.getString("passview_code");
            if (code != null) {
                codeField.setText(code);
            }
        }
    }

    public class LoginActivityRegisterView extends SlideView {

        private EditText firstNameField;
        private EditText lastNameField;
        private String requestPhone;
        private String phoneHash;
        private String phoneCode;
        private Bundle currentParams;
        private boolean nextPressed = false;

        public LoginActivityRegisterView(Context context) {
            super(context);

            setOrientation(VERTICAL);

            TextView textView = new TextView(context);
            textView.setText("输入你的姓名");
            textView.setTextColor(0xff757575);
            textView.setGravity(Gravity.LEFT);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            addView(textView);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.topMargin = AndroidUtilities.dp(8);
            layoutParams.gravity = Gravity.LEFT;
            textView.setLayoutParams(layoutParams);

            firstNameField = new EditText(context);
            firstNameField.setHintTextColor(0xff979797);
            firstNameField.setTextColor(0xff212121);
            AndroidUtilities.clearCursorDrawable(firstNameField);
            firstNameField.setHint("");
            firstNameField.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            firstNameField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            firstNameField.setMaxLines(1);
            firstNameField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            addView(firstNameField);
            layoutParams = (LinearLayout.LayoutParams) firstNameField.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = AndroidUtilities.dp(36);
            layoutParams.topMargin = AndroidUtilities.dp(26);
            firstNameField.setLayoutParams(layoutParams);
            firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_NEXT) {
                        lastNameField.requestFocus();
                        return true;
                    }
                    return false;
                }
            });

            lastNameField = new EditText(context);
            lastNameField.setHint("");
            lastNameField.setHintTextColor(0xff979797);
            lastNameField.setTextColor(0xff212121);
            AndroidUtilities.clearCursorDrawable(lastNameField);
            lastNameField.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            lastNameField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            lastNameField.setMaxLines(1);
            lastNameField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            addView(lastNameField);
            layoutParams = (LinearLayout.LayoutParams) lastNameField.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = AndroidUtilities.dp(36);
            layoutParams.topMargin = AndroidUtilities.dp(10);
            lastNameField.setLayoutParams(layoutParams);

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
            addView(linearLayout);
            layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            linearLayout.setLayoutParams(layoutParams);

            TextView wrongNumber = new TextView(context);
            wrongNumber.setText("");
            wrongNumber.setGravity(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);
            wrongNumber.setTextColor(0xff4d83b3);
            wrongNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            wrongNumber.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            wrongNumber.setPadding(0, AndroidUtilities.dp(24), 0, 0);
            linearLayout.addView(wrongNumber);
            layoutParams = (LinearLayout.LayoutParams) wrongNumber.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
            layoutParams.bottomMargin = AndroidUtilities.dp(10);
            wrongNumber.setLayoutParams(layoutParams);
            wrongNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
//                    builder.setMessage(LocaleController.getString("AreYouSureRegistration", R.string.AreYouSureRegistration));
//                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            onBackPressed();
//                            setPage(0, true, null, true);
//                        }
//                    });
//                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
//                    showDialog(builder.create());
                }
            });
        }

        @Override
        public void onBackPressed() {
            currentParams = null;
        }

        @Override
        public String getHeaderName() {
            return "";
        }

        @Override
        public void onShow() {
            super.onShow();
            if (firstNameField != null) {
                firstNameField.requestFocus();
                firstNameField.setSelection(firstNameField.length());
            }
        }

        @Override
        public void setParams(Bundle params) {
            if (params == null) {
                return;
            }
            firstNameField.setText("");
            lastNameField.setText("");
            requestPhone = params.getString("phoneFormated");
            phoneHash = params.getString("phoneHash");
            phoneCode = params.getString("code");
            currentParams = params;
        }

        @Override
        public void onNextPressed() {
            if (nextPressed) {
                return;
            }
            nextPressed = true;
            needShowProgress("");
            setPage(2, true, null, false);
            return;
        }

        @Override
        public void saveStateParams(Bundle bundle) {
            String first = firstNameField.getText().toString();
            if (first != null && first.length() != 0) {
                bundle.putString("registerview_first", first);
            }
            String last = lastNameField.getText().toString();
            if (last != null && last.length() != 0) {
                bundle.putString("registerview_last", last);
            }
            if (currentParams != null) {
                bundle.putBundle("registerview_params", currentParams);
            }
        }

        @Override
        public void restoreStateParams(Bundle bundle) {
            currentParams = bundle.getBundle("registerview_params");
            if (currentParams != null) {
                setParams(currentParams);
            }
            String first = bundle.getString("registerview_first");
            if (first != null) {
                firstNameField.setText(first);
            }
            String last = bundle.getString("registerview_last");
            if (last != null) {
                lastNameField.setText(last);
            }
        }
    }


    public class LoginActivitySignInView extends SlideView {

        private EditText userNameField;
        private EditText userPasswordField;
        private String orgCode;
        private String orgName;
        private Bundle currentParams;
        private boolean nextPressed = false;

        public LoginActivitySignInView(Context context) {
            super(context);

            setOrientation(VERTICAL);

            TextView textView = new TextView(context);
            textView.setText("");
            textView.setTextColor(0xff757575);
            textView.setGravity(Gravity.LEFT);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            addView(textView);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.topMargin = AndroidUtilities.dp(8);
            layoutParams.gravity = Gravity.LEFT;
            textView.setLayoutParams(layoutParams);

            userNameField = new EditText(context);
            userNameField.setHintTextColor(0xff979797);
            userNameField.setTextColor(0xff212121);
            AndroidUtilities.clearCursorDrawable(userNameField);
            userNameField.setHint("");
            userNameField.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            userNameField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            userNameField.setMaxLines(1);
            userNameField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            addView(userNameField);
            layoutParams = (LinearLayout.LayoutParams) userNameField.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = AndroidUtilities.dp(36);
            layoutParams.topMargin = AndroidUtilities.dp(26);
            userNameField.setLayoutParams(layoutParams);
            userNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_NEXT) {
                        userPasswordField.requestFocus();
                        return true;
                    }
                    return false;
                }
            });

            userPasswordField = new EditText(context);
            userPasswordField.setTextColor(0xff212121);
            AndroidUtilities.clearCursorDrawable(userPasswordField);
            userPasswordField.setHintTextColor(0xff979797);
            userPasswordField.setHint("");
            userPasswordField.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            userPasswordField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            userPasswordField.setMaxLines(1);
            userPasswordField.setPadding(0, 0, 0, 0);
            userPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            userPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            userPasswordField.setTypeface(Typeface.DEFAULT);
            addView(userPasswordField);
            layoutParams = (LinearLayout.LayoutParams) userPasswordField.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = AndroidUtilities.dp(36);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams.topMargin = AndroidUtilities.dp(20);
            userPasswordField.setLayoutParams(layoutParams);
            userPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_NEXT) {
                        onNextPressed();
                        return true;
                    }
                    return false;
                }
            });

            TextView cancelButton = new TextView(context);
            cancelButton.setGravity(Gravity.LEFT | Gravity.TOP);
            cancelButton.setTextColor(0xff4d83b3);
            cancelButton.setText("");
            cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            cancelButton.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            cancelButton.setPadding(0, AndroidUtilities.dp(14), 0, 0);
            addView(cancelButton);
            layoutParams = (LinearLayout.LayoutParams) cancelButton.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            cancelButton.setLayoutParams(layoutParams);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
            addView(linearLayout);
            layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            linearLayout.setLayoutParams(layoutParams);

            TextView wrongNumber = new TextView(context);
            wrongNumber.setText("变更组织机构代码");
            wrongNumber.setGravity(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);
            wrongNumber.setTextColor(0xff4d83b3);
            wrongNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            wrongNumber.setLineSpacing(AndroidUtilities.dp(2), 1.0f);
            wrongNumber.setPadding(0, AndroidUtilities.dp(24), 0, 0);
            linearLayout.addView(wrongNumber);
            layoutParams = (LinearLayout.LayoutParams) wrongNumber.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
            layoutParams.bottomMargin = AndroidUtilities.dp(10);
            wrongNumber.setLayoutParams(layoutParams);
            wrongNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
//                    builder.setMessage(LocaleController.getString("AreYouSureRegistration", R.string.AreYouSureRegistration));
//                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            onBackPressed();
//                            setPage(0, true, null, true);
//                        }
//                    });
//                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
//                    showDialog(builder.create());
                }
            });
        }

        @Override
        public void onBackPressed() {
            currentParams = null;
        }

        @Override
        public String getHeaderName() {
            return "";//LocaleController.getString("YourName", R.string.YourName);
        }

        @Override
        public void onShow() {
            super.onShow();
            if (userNameField != null) {
                userNameField.requestFocus();
                userNameField.setSelection(userNameField.length());
            }
        }

        @Override
        public void setParams(Bundle params) {
            if (params == null) {
                return;
            }
//            firstNameField.setText("");
//            lastNameField.setText("");
//            requestPhone = params.getString("phoneFormated");
//            phoneHash = params.getString("phoneHash");
//            phoneCode = params.getString("code");
            currentParams = params;
        }

        @Override
        public void onNextPressed() {
            if (nextPressed) {
                return;
            }
            nextPressed = true;
//            TLRPC.TL_auth_signUp req = new TLRPC.TL_auth_signUp();
//            req.phone_code = phoneCode;
//            req.phone_code_hash = phoneHash;
//            req.phone_number = requestPhone;
//            req.first_name = firstNameField.getText().toString();
//            req.last_name = lastNameField.getText().toString();
            needShowProgress("");
            setPage(2, true, null, false);
            return;
//            ConnectionsManager.getInstance().performRpc(req, new RPCRequest.RPCRequestDelegate() {
//                @Override
//                public void run(final TLObject response, final TLRPC.TL_error error) {
//                    AndroidUtilities.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            nextPressed = false;
//                            needHideProgress();
//                            if (error == null) {
//                                final TLRPC.TL_auth_authorization res = (TLRPC.TL_auth_authorization) response;
//                                TLRPC.TL_userSelf user = (TLRPC.TL_userSelf) res.user;
//                                UserConfig.clearConfig();
//                                MessagesController.getInstance().cleanUp();
//                                UserConfig.setCurrentUser(user);
//                                UserConfig.saveConfig(true);
//                                MessagesStorage.getInstance().cleanUp(true);
//                                ArrayList<TLRPC.User> users = new ArrayList<>();
//                                users.add(user);
//                                MessagesStorage.getInstance().putUsersAndChats(users, null, true, true);
//                                //MessagesController.getInstance().uploadAndApplyUserAvatar(avatarPhotoBig);
//                                MessagesController.getInstance().putUser(res.user, false);
//                                ContactsController.getInstance().checkAppAccount();
//                                MessagesController.getInstance().getBlockedUsers(true);
//                                needFinishActivity();
//                                ConnectionsManager.getInstance().initPushConnection();
//                                Utilities.stageQueue.postRunnable(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ConnectionsManager.getInstance().updateDcSettings(0);
//                                    }
//                                });
//                            } else {
//                                if (error.text.contains("PHONE_NUMBER_INVALID")) {
//                                    needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
//                                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
//                                    needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidCode", R.string.InvalidCode));
//                                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
//                                    needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("CodeExpired", R.string.CodeExpired));
//                                } else if (error.text.contains("FIRSTNAME_INVALID")) {
//                                    needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidFirstName", R.string.InvalidFirstName));
//                                } else if (error.text.contains("LASTNAME_INVALID")) {
//                                    needShowAlert(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("InvalidLastName", R.string.InvalidLastName));
//                                } else {
//                                    needShowAlert(LocaleController.getString("AppName", R.string.AppName), error.text);
//                                }
//                            }
//                        }
//                    });
//                }
//            }, true, RPCRequest.RPCRequestClassGeneric | RPCRequest.RPCRequestClassWithoutLogin | RPCRequest.RPCRequestClassFailOnServerErrors);
        }

        @Override
        public void saveStateParams(Bundle bundle) {
            String name = userNameField.getText().toString();
            if (name != null && name.length() != 0) {
                bundle.putString("sign_in_user_name", name);
            }
            String password = userPasswordField.getText().toString();
            if (password != null && password.length() != 0) {
                bundle.putString("sign_in_user_password", password);
            }
            if (currentParams != null) {
                bundle.putBundle("sign_in_params", currentParams);
            }
        }

        @Override
        public void restoreStateParams(Bundle bundle) {
            currentParams = bundle.getBundle("sign_in_params");
            if (currentParams != null) {
                setParams(currentParams);
            }
            String name = bundle.getString("sign_in_user_name");
            if (name != null) {
                userNameField.setText(name);
            }
            String password = bundle.getString("sign_in_user_password");
            if (password != null) {
                userPasswordField.setText(password);
            }
        }
    }

    public class PhoneView extends SlideView {
        private MaterialEditText phoneField;
        private TextView orgField;
        private boolean ignoreOnPhoneChange = false;
        private boolean nextPressed = false;

        public PhoneView(Context context) {
            super(context);

            setOrientation(VERTICAL);

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(HORIZONTAL);
            addView(linearLayout);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.topMargin = AndroidUtilities.dp(20);
            linearLayout.setLayoutParams(layoutParams);

            phoneField = new MaterialEditText(context);
            phoneField.setBaseColor(0xff212121);
            phoneField.setPrimaryColor(ResourcesConfig.textPrimary);
            phoneField.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
            phoneField.setInputType(InputType.TYPE_CLASS_PHONE);
            AndroidUtilities.clearCursorDrawable(phoneField);
            phoneField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            phoneField.setMaxLines(1);
            phoneField.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            phoneField.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            linearLayout.addView(phoneField);
            layoutParams = (LinearLayout.LayoutParams) phoneField.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            phoneField.setLayoutParams(layoutParams);
            phoneField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (ignoreOnPhoneChange) {
                        return;
                    }
                    if (count == 1 && after == 0 && s.length() > 1) {
                        String phoneChars = "0123456789";
                        String str = s.toString();
                        String substr = str.substring(start, start + 1);
                        if (!phoneChars.contains(substr)) {
                            ignoreOnPhoneChange = true;
                            StringBuilder builder = new StringBuilder(str);
                            int toDelete = 0;
                            for (int a = start; a >= 0; a--) {
                                substr = str.substring(a, a + 1);
                                if (phoneChars.contains(substr)) {
                                    break;
                                }
                                toDelete++;
                            }
                            builder.delete(Math.max(0, start - toDelete), start + 1);
                            str = builder.toString();
                            if (PhoneFormat.strip(str).length() == 0) {
                                phoneField.setText("");
                            } else {
                                phoneField.setText(str);
                                updatePhoneField();
                            }
                            ignoreOnPhoneChange = false;
                        }
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ignoreOnPhoneChange) {
                        return;
                    }
                    updatePhoneField();
                }
            });
            phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_NEXT) {
                        onNextPressed();
                        return true;
                    }
                    return false;
                }
            });

            TextView textView = new TextView(context);
            textView.setText("使用手机号码快捷登录要健康.未注册用户,赶快加入我们吧,一起开启健康之旅.");
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

            orgField = new TextView(context);
            orgField.setTextColor(0xff000000);
            orgField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            orgField.setGravity(Gravity.LEFT);
            orgField.setLineSpacing(AndroidUtilities.dp(1), 1.0f);
            addView(orgField);
            layoutParams = (LinearLayout.LayoutParams) orgField.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.topMargin = AndroidUtilities.dp(10);
            layoutParams.bottomMargin = AndroidUtilities.dp(10);
            layoutParams.gravity = Gravity.LEFT;
            orgField.setLayoutParams(layoutParams);


            linearLayout = new LinearLayout(context);
            linearLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
            addView(linearLayout);
            layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            linearLayout.setLayoutParams(layoutParams);

            AndroidUtilities.showKeyboard(phoneField);
            phoneField.requestFocus();
        }

        @Override
        public void setParams(Bundle params) {
            if (params == null) {
                return;
            }
            String phone = "";
            if (params.containsKey(PhoneView.PARAM_PHONE)) {
                phone = params.getString(PhoneView.PARAM_PHONE);
            }
            phoneField.setText(phone);
        }

        private void updatePhoneField() {
            ignoreOnPhoneChange = true;
            try {
                String codeText = "86";
                PhoneFormat.getInstance().init("86");
                String phone = PhoneFormat.getInstance().format("+" + codeText + phoneField.getText().toString());
                int idx = phone.indexOf(" ");
                if (idx != -1) {
                    phoneField.setText(phone.substring(idx).trim());
                    phoneField.setSelection(phoneField.length());
                } else {
                    phoneField.setSelection(phoneField.length());
                }
            } catch (Exception e) {
                FileLog.e("romens", e);
            }
            ignoreOnPhoneChange = false;
        }

        public static final String PARAM_PHONE = "PhoneNumber";
        public static final String PARAM_HX_ID = "HXId";

        @Override
        public void onNextPressed() {
            if (nextPressed) {
                return;
            }
            if (phoneField.length() == 0) {
                needShowAlert(getString(R.string.app_name), "请输入手机号码");
                return;
            }
            //TODO 正则表达式的修改
            //final String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
            final String regExp = "^[1][0-9]{2}[0-9]{8}$";

            Pattern p = Pattern.compile(regExp);
            String phone = PhoneFormat.stripExceptNumbers(PhoneFormat.stripExceptNumbers(phoneField.getText().toString()));
            Matcher m = p.matcher(phone);
            if (!m.find()) {
                needShowAlert(getString(R.string.app_name), "手机号码格式错误");
                return;
            }

            final Bundle params = new Bundle();
            UserConfig.AppChannel appChannel = UserConfig.loadAppChannel();
            params.putString(OrganizationCodeView.PARAM_ORGAN_CODE, appChannel.orgCode);
            params.putString(OrganizationCodeView.PARAM_ORGAN_NAME, appChannel.orgName);
            params.putString(PARAM_PHONE, phone);
            nextPressed = true;
            needShowProgress("验证手机号码...");
            Map<String, String> args = new HashMap<>();

            args.put("PHONENUMBER", phone);
            args.put("ORGGUID", appChannel.orgCode);
            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "CheckPhoneNumber", args);
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol)
                   .withParser(new JsonParser(new TypeToken<LinkedTreeMap<String, String>>() {
                    }))
                    .build();
            FacadeClient.request(LoginActivity.this, message, new FacadeClient.FacadeCallback() {
                @Override
                public void onTokenTimeout(Message msg) {

                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    nextPressed = false;
                    needHideProgress();
                  //  Log.i("msg",((ResponseProtocol) msg.protocol).getResponse()+"");
                    if (errorMsg == null) {
                        ResponseProtocol<LinkedTreeMap<String, String>> response = (ResponseProtocol) msg.protocol;
                        LinkedTreeMap<String, String> result = response.getResponse();
                        if (result != null && result.size() > 0) {
                            String isValidity = result.get("ISVALIDITY");
                            if (!TextUtils.equals("0", isValidity)) {
                                boolean value = TextUtils.equals("2", isValidity);
                                params.putBoolean("IsValidityUser", value);
                                params.putString(PARAM_HX_ID, result.get("NAME"));
                                params.putString("UserGuid",result.get("USERGUID"));
                                setPage(value ? 1 : 2, true, params, false);
                            } else {
                                needShowAlert(getString(R.string.app_name), "手机号码异常");
                            }
                        }
                    } else {
                        if (errorMsg.code != 0) {
                         //   ResponseProtocol<String> error = (ResponseProtocol) msg.protocol;
                           // Log.i("错误信息是否为空----",(error.getResponse()==null)+"");
                            needShowAlert(getString(R.string.app_name), errorMsg.msg);
                           // Log.i("登录错误日志----",errorMsg.msg);
                        }
                    }
                }
            });
        }

        @Override
        public void onShow() {
            super.onShow();
            if (phoneField != null) {
                phoneField.requestFocus();
                phoneField.setSelection(phoneField.length());
            }
        }

        @Override
        public String getHeaderName() {
            return "手机号码";
        }

        @Override
        public void onBackPressed() {
        }

        @Override
        public void saveStateParams(Bundle bundle) {
            String phone = phoneField.getText().toString();
            if (phone != null && phone.length() != 0) {
                bundle.putString("phoneview_phone", phone);
            }
        }

        @Override
        public void restoreStateParams(Bundle bundle) {
            String phone = bundle.getString("phoneview_phone");
            if (phone != null) {
                phoneField.setText(phone);
            }
        }
    }



    //获取购物车数量
    private void requestShopCarCountData() {
        if (UserConfig.isClientLogined()) {

            Map<String, String> args = new FacadeArgs.MapBuilder()
                    .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).build();
            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetBuyCarCount", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol).build();
            FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {

                @Override
                public void onTokenTimeout(Message msg) {
                    needHideProgress();
                    Log.e("GetBuyCarCount", "ERROR");
                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    needHideProgress();
                    if (errorMsg == null) {
                        ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                        try {
                            JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                            String buycount = jsonObject.getString("BUYCOUNT");
                            //shoppingCartItem.setIcon(Integer.parseInt(buycount));
                            int sumCount = Integer.parseInt(buycount);
                            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, sumCount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("GetBuyCarCount", "ERROR");
                    }
                }
            });
        }else{

        }
    }
 }
