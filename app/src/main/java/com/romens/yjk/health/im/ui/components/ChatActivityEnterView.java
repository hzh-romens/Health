package com.romens.yjk.health.im.ui.components;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.util.VoiceRecorder;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.yjk.health.im.NotificationCenter;
import com.romens.android.log.FileLog;
import com.romens.android.ui.AnimationCompat.AnimatorListenerAdapterProxy;
import com.romens.android.ui.AnimationCompat.AnimatorSetProxy;
import com.romens.android.ui.AnimationCompat.ObjectAnimatorProxy;
import com.romens.android.ui.AnimationCompat.ViewProxy;
import com.romens.android.ui.Components.FrameLayoutFixed;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.SystemConfig;
import com.romens.yjk.health.im.Emoji;
import com.romens.yjk.health.ui.components.EmojiView;
import com.romens.yjk.health.ui.components.VoiceFabButton;
import com.romens.yjk.health.ui.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivityEnterView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {

    public ChatActivityEnterView(Context context) {
        super(context);
        init(context);
    }

    public ChatActivityEnterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatActivityEnterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public interface ChatActivityEnterViewDelegate {
        void onMessageSend(String message);

        void onVoiceMessageSend(String filePath, String fileName, int length, boolean isResend);

        void needClearVoicePlay();

        void needSendTyping();

        void onTextChanged(CharSequence text, boolean bigChange);

        void onAttachButtonHidden();

        void onAttachButtonShow();
    }

    private EditText messageEditText;
    private ImageView sendButton;
    private ImageView emojiButton;
    private EmojiView emojiView;
    private TextView recordTimeText;
    private ImageView audioSendButton;
    private FrameLayout attachButton;
    private LinearLayout textFieldContainer;
    private View topView;

    private PowerManager.WakeLock mWakeLock;
    private AnimatorSetProxy runningAnimation;
    private AnimatorSetProxy runningAnimation2;
    private ObjectAnimatorProxy runningAnimationAudio;
    private int runningAnimationType;
    private int audioInterfaceState;

    private boolean sendByEnter;
    private long lastTypingTimeSend;
    private boolean recordingAudio;
    private boolean forceShowSendButton;

    //private Activity parentActivity;
    private String chatId;
    private boolean ignoreTextChange;
    private boolean messageWebPageSearch;
    private ChatActivityEnterViewDelegate delegate;

    private float topViewAnimation;
    private boolean topViewShowed;
    private boolean needShowTopView;
    private boolean allowShowTopView;
    private AnimatorSetProxy currentTopViewAnimation;

    private FrameLayout recordContainer;
    private TextView recordLabel;
    private VoiceFabButton recordButton;
    private VoiceRecorder voiceRecorder;

    private void init(Context context) {
        boolean isChat = true;
        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.compose_panel);
        setFocusable(true);
        setFocusableInTouchMode(true);

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        //NotificationCenter.getInstance().addObserver(this, NotificationCenter.hideEmojiKeyboard);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioRouteChanged);
        sendByEnter = SystemConfig.getInstance().enableSendMessageUserEnterKey();
        textFieldContainer = new LinearLayout(context);
        textFieldContainer.setBackgroundColor(0xffffffff);
        textFieldContainer.setOrientation(LinearLayout.HORIZONTAL);
        addView(textFieldContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 2, 0, 0));

        FrameLayoutFixed frameLayout = new FrameLayoutFixed(context);
        textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f));

        emojiButton = new ImageView(context);
        emojiButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        emojiButton.setPadding(AndroidUtilities.dp(4), AndroidUtilities.dp(1), 0, 0);
        emojiButton.setImageResource(R.drawable.ic_msg_panel_smiles);
        frameLayout.addView(emojiButton, LayoutHelper.createFrame(48, 48, Gravity.BOTTOM));
        emojiButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPopupShowing() || currentPopupContentType != 0) {
                    showPopup(1, 0);
                } else {
                    openKeyboardInternal();
                }
            }
        });

        messageEditText = new EditText(context);
        messageEditText.setHint("输入...");
        messageEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        messageEditText.setInputType(messageEditText.getInputType() | EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        messageEditText.setSingleLine(false);
        messageEditText.setMaxLines(4);
        messageEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        messageEditText.setGravity(Gravity.BOTTOM);
        messageEditText.setPadding(0, AndroidUtilities.dp(11), 0, AndroidUtilities.dp(12));
        messageEditText.setBackgroundDrawable(null);
        AndroidUtilities.clearCursorDrawable(messageEditText);
        messageEditText.setTextColor(0xff000000);
        messageEditText.setHintTextColor(0xffb2b2b2);
        frameLayout.addView(messageEditText, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM, 52, 0, isChat ? 50 : 2, 0));
        messageEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK && isPopupShowing()) {
                    if (keyEvent.getAction() == 1) {
                        showPopup(0, 0);
                    }
                    return true;
                } else if (i == KeyEvent.KEYCODE_ENTER && sendByEnter && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });
        messageEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPopupShowing()) {
                    showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 0);
                } else if (isVoicePopupShowing()) {
                    showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 1);
                }
            }
        });
        messageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    return true;
                } else if (sendByEnter) {
                    if (keyEvent != null && i == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        sendMessage();
                        return true;
                    }
                }
                return false;
            }
        });
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String message = getTrimmedString(charSequence.toString());
                checkSendButton(true);

                if (delegate != null) {
                    if (before > count || count > 1) {
                        messageWebPageSearch = true;
                    }
                    delegate.onTextChanged(charSequence, before > count || count > 1);
                }

                if (message.length() != 0 && lastTypingTimeSend < System.currentTimeMillis() - 5000 && !ignoreTextChange) {
//                    int currentTime = ConnectionsManager.getInstance().getCurrentTime();
//                    TLRPC.User currentUser = null;
//                    if ((int) dialog_id > 0) {
//                        currentUser = MessagesController.getInstance().getUser((int) dialog_id);
//                    }
//                    if (currentUser != null && (currentUser.key == UserConfig.getClientUserId() || currentUser.status != null && currentUser.status.expires < currentTime)) {
//                        return;
//                    }
                    lastTypingTimeSend = System.currentTimeMillis();
                    if (delegate != null) {
                        delegate.needSendTyping();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (sendByEnter && editable.length() > 0 && editable.charAt(editable.length() - 1) == '\n') {
                    sendMessage();
                }
                int i = 0;
                ImageSpan[] arrayOfImageSpan = editable.getSpans(0, editable.length(), ImageSpan.class);
                int j = arrayOfImageSpan.length;
                while (true) {
                    if (i >= j) {
                        Emoji.replaceEmoji(editable, messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20), false);
                        return;
                    }
                    editable.removeSpan(arrayOfImageSpan[i]);
                    i++;
                }
            }
        });

        messageEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hidePopup();
                    hideVoicePopup();
                }
            }
        });

        if (isChat) {
            attachButton = new FrameLayout(context);
            attachButton.setEnabled(false);
            ViewProxy.setPivotX(attachButton, AndroidUtilities.dp(48));
            frameLayout.addView(attachButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 48, Gravity.BOTTOM | Gravity.RIGHT));
        }

        FrameLayout frameLayout1 = new FrameLayout(context);
        textFieldContainer.addView(frameLayout1, LayoutHelper.createLinear(48, 48, Gravity.BOTTOM));

        audioSendButton = new ImageView(context);
        audioSendButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        audioSendButton.setImageResource(R.drawable.mic_button_states);
        audioSendButton.setBackgroundColor(0xffffffff);
        audioSendButton.setSoundEffectsEnabled(false);
        audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4), 0);
        frameLayout1.addView(audioSendButton, LayoutHelper.createFrame(48, 48));
        audioSendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVoicePopupShowing() || currentPopupContentType != 1) {
                    showPopup(1, 1);
                } else {
                    openKeyboardInternal();
                }
            }
        });

        sendButton = new ImageView(context);
        sendButton.setVisibility(View.INVISIBLE);
        sendButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        sendButton.setImageResource(R.drawable.ic_send);
        sendButton.setSoundEffectsEnabled(false);
        ViewProxy.setScaleX(sendButton, 0.1f);
        ViewProxy.setScaleY(sendButton, 0.1f);
        ViewProxy.setAlpha(sendButton, 0.0f);
        sendButton.clearAnimation();
        frameLayout1.addView(sendButton, LayoutHelper.createFrame(48, 48));
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        LinearLayout extendContainer = new LinearLayout(context);
        extendContainer.setBackgroundColor(0xffffffff);
        extendContainer.setOrientation(LinearLayout.VERTICAL);
        addView(extendContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        emojiView = new EmojiView(context);
        emojiView.setVisibility(GONE);
        emojiView.setListener(new EmojiView.Listener() {
            public boolean onBackspace() {
                if (messageEditText.length() == 0) {
                    return false;
                }
                messageEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                return true;
            }

            public void onEmojiSelected(String symbol) {
                int i = messageEditText.getSelectionEnd();
                if (i < 0) {
                    i = 0;
                }
                try {
                    CharSequence localCharSequence = Emoji.replaceEmoji(symbol, messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20), false);
                    messageEditText.setText(messageEditText.getText().insert(i, localCharSequence));
                    int j = i + localCharSequence.length();
                    messageEditText.setSelection(j, j);
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });

        extendContainer.addView(emojiView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));


        recordContainer = new FrameLayout(context);
        recordContainer.setVisibility(View.GONE);

        recordButton = new VoiceFabButton(context);
        recordButton.setMaxProgress(15);
        recordButton.setClickable(true);
        recordButton.setFocusable(true);
        recordButton.setFocusableInTouchMode(true);
        recordButton.setCircleColor(getResources().getColor(R.color.md_red_500));
        recordButton.setProgressColor(getResources().getColor(R.color.md_red_100));
        recordButton.setIcon(R.drawable.ic_keyboard_voice_white_48dp);
        recordContainer.addView(recordButton,
                LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
        recordLabel = new TextView(context);
        recordLabel.setText("触摸并按住开始录音");
        recordLabel.setTextColor(0xff999999);
        recordLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        recordContainer.addView(recordLabel,
                LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                        Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 10, 0, 0));

        recordTimeText = new TextView(context);
        recordTimeText.setText("00:00");
        recordTimeText.setTextColor(0xffe51c23);
        recordTimeText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        recordTimeText.setVisibility(View.INVISIBLE);
        recordContainer.addView(recordTimeText,
                LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                        Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 32, 0, 0));
        recordButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!UIUtils.isExitsSdcard()) {
                        Toast.makeText(getContext(), "无法存储录音", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        view.setPressed(true);
                        clearVoicePlay();
                        try {
                            Vibrator v = (Vibrator) ApplicationLoader.applicationContext.getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(20);
                        } catch (Exception e) {
                            FileLog.e("tmessages", e);
                        }
                        voiceRecorder.startRecording(null, chatId, MyApplication.applicationContext);
                        recordingAudio = true;
                        audioInterfaceState = 0;
                        updateAudioRecordIntefrace();
                        destroyRecordTimer();
                        createRecordTimer();
                    } catch (Exception e) {
                        e.printStackTrace();
                        view.setPressed(false);
                        recordingAudio = false;
                        audioInterfaceState = 1;
                        updateAudioRecordIntefrace();
                        cancelRecord();
                        return false;
                    }
                    recordButton.getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    view.setPressed(false);
                    if (motionEvent.getY() < 0) {
                        cancelRecord();
                    } else {
                        // stop recording and send voice file
                        try {
                            final int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
//                                parentActivity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (delegate != null) {
//                                            delegate.onVoiceMessageSend(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(chatId),
//                                                    length, false);
//                                        }
//                                    }
//                                });
                                Map<String, String> record = new HashMap<String, String>();
                                record.put("file_path", voiceRecorder.getVoiceFilePath());
                                record.put("file_name", voiceRecorder.getVoiceFileName(chatId));
                                record.put("file_length", String.valueOf(length));
                                android.os.Message.obtain(recordHandler, HANDLER_RECORD_SENDING, record).sendToTarget();

                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getContext(), "无录音权限", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "发送失败", Toast.LENGTH_SHORT).show();
                        }
                        destroyRecordTimer();
                    }
                    recordingAudio = false;
                    audioInterfaceState = 1;
                    updateAudioRecordIntefrace();
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && recordingAudio) {
                    if (motionEvent.getY() < 0) {
                        recordLabel.setText("松开取消发送");
                    } else {
                        recordLabel.setText("向上滑动取消发送");
                    }
                }
                return true;
            }
        });
        extendContainer.addView(recordContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        checkSendButton(false);
        voiceRecorder = new VoiceRecorder(new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                setVoiceRecorderProgress(msg.what);
            }
        });
    }

    public void hidePopup() {
        if (isPopupShowing()) {
            showPopup(0, 0);
        }
    }

    public void hideVoicePopup() {
        if (isVoicePopupShowing()) {
            showPopup(0, 1);
        }
    }

    public void onPause() {

    }

    public void onResume() {
//        messageEditText.requestFocus();
//        AndroidUtilities.showKeyboard(messageEditText);
    }

    private void openKeyboardInternal() {
        showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 0);
        messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(messageEditText);
    }

    private void clearVoicePlay() {
        if (delegate != null) {
            delegate.needClearVoicePlay();
        }
    }


    private void cancelRecord() {
        destroyRecordTimer();
        if (voiceRecorder != null) {
            voiceRecorder.discardRecording();
        }
    }

    public void addTopView(View view, int height) {
        if (view == null) {
            return;
        }
        addView(view, 0);
        topView = view;
        topView.setVisibility(GONE);
        needShowTopView = false;
        LayoutParams layoutParams = (LayoutParams) topView.getLayoutParams();
        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = height;
        layoutParams.topMargin = AndroidUtilities.dp(2);
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        topView.setLayoutParams(layoutParams);
    }

    public void setTopViewAnimation(float progress) {
        topViewAnimation = progress;
        LayoutParams layoutParams2 = (LayoutParams) textFieldContainer.getLayoutParams();
        layoutParams2.topMargin = AndroidUtilities.dp(2) + (int) (topView.getLayoutParams().height * progress);
        textFieldContainer.setLayoutParams(layoutParams2);
    }

    public float getTopViewAnimation() {
        return topViewAnimation;
    }

    public void setForceShowSendButton(boolean value, boolean animated) {
        forceShowSendButton = value;
        checkSendButton(animated);
    }

    public void showTopView(boolean animated) {
        if (topView == null || topViewShowed) {
            return;
        }
        needShowTopView = true;
        topViewShowed = true;
        if (allowShowTopView) {
            topView.setVisibility(VISIBLE);
            float resumeValue = 0.0f;
            if (currentTopViewAnimation != null) {
                resumeValue = topViewAnimation;
                currentTopViewAnimation.cancel();
                currentTopViewAnimation = null;
            }
            if (animated) {
                if (isEmojiShowing()) {
                    currentTopViewAnimation = new AnimatorSetProxy();
                    currentTopViewAnimation.playTogether(
                            ObjectAnimatorProxy.ofFloat(ChatActivityEnterView.this, "topViewAnimation", 1.0f)
                    );
                    currentTopViewAnimation.addListener(new AnimatorListenerAdapterProxy() {
                        @Override
                        public void onAnimationEnd(Object animation) {
                            if (animation == currentTopViewAnimation) {
                                setTopViewAnimation(1.0f);
                                if (!forceShowSendButton) {
                                    openKeyboard();
                                }
                                currentTopViewAnimation = null;
                            }
                        }
                    });
                    currentTopViewAnimation.setDuration(200);
                    currentTopViewAnimation.start();
                } else {
                    setTopViewAnimation(1.0f);
                    if (!forceShowSendButton) {
                        openKeyboard();
                    }
                }
            } else {
                setTopViewAnimation(1.0f);
            }
        }
    }

    public void hideTopView(final boolean animated) {
        if (topView == null || !topViewShowed) {
            return;
        }

        topViewShowed = false;
        needShowTopView = false;
        if (allowShowTopView) {
            float resumeValue = 1.0f;
            if (currentTopViewAnimation != null) {
                resumeValue = topViewAnimation;
                currentTopViewAnimation.cancel();
                currentTopViewAnimation = null;
            }
            if (animated) {
                currentTopViewAnimation = new AnimatorSetProxy();
                currentTopViewAnimation.playTogether(
                        ObjectAnimatorProxy.ofFloat(ChatActivityEnterView.this, "topViewAnimation", resumeValue, 0.0f)
                );
                currentTopViewAnimation.addListener(new AnimatorListenerAdapterProxy() {
                    @Override
                    public void onAnimationEnd(Object animation) {
                        if (animation == currentTopViewAnimation) {
                            topView.setVisibility(GONE);
                            setTopViewAnimation(0.0f);
                            currentTopViewAnimation = null;
                        }
                    }
                });
                currentTopViewAnimation.setDuration(200);
                currentTopViewAnimation.start();
            } else {
                topView.setVisibility(GONE);
                setTopViewAnimation(0.0f);
            }
        }
    }

    public boolean isTopViewVisible() {
        return topView != null && topView.getVisibility() == VISIBLE;
    }

    public void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        //NotificationCenter.getInstance().removeObserver(this, NotificationCenter.hideEmojiKeyboard);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioRouteChanged);
        if (mWakeLock != null) {
            try {
                mWakeLock.release();
                mWakeLock = null;
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }
        if (recordingAudio) {
            cancelRecord();
        }
    }

    public void setDialogId(String id) {
        chatId = id;
    }

    private void sendMessage() {
//        if (parentFragment != null) {
//            String action = null;
//            TLRPC.Chat currentChat = null;
//            if ((int) dialog_id < 0) {
//                currentChat = MessagesController.getInstance().getChat(-(int) dialog_id);
//                if (currentChat != null && currentChat.participants_count > MessagesController.getInstance().groupBigSize) {
//                    action = "bigchat_message";
//                } else {
//                    action = "chat_message";
//                }
//            } else {
//                action = "pm_message";
//            }
//            if (!MessagesController.isFeatureEnabled(action, parentFragment)) {
//                return;
//            }
//        }
        String message = messageEditText.getText().toString();
        if (processSendingText(message)) {
            messageEditText.setText("");
            lastTypingTimeSend = 0;
            if (delegate != null) {
                delegate.onMessageSend(message);
            }
        } else if (forceShowSendButton) {
            if (delegate != null) {
                delegate.onMessageSend(null);
            }
        }
    }

    public boolean processSendingText(String text) {
        text = getTrimmedString(text);
        if (text.length() != 0) {
            int count = (int) Math.ceil(text.length() / 4096.0f);
            for (int a = 0; a < count; a++) {
                String mess = text.substring(a * 4096, Math.min((a + 1) * 4096, text.length()));
                //SendMessagesHelper.getInstance().sendMessage(mess, dialog_id, replyingMessageObject, messageWebPage, messageWebPageSearch);
            }
            return true;
        }
        return false;
    }

    private String getTrimmedString(String src) {
        String result = src.trim();
        if (result.length() == 0) {
            return result;
        }
        while (src.startsWith("\n")) {
            src = src.substring(1);
        }
        while (src.endsWith("\n")) {
            src = src.substring(0, src.length() - 1);
        }
        return src;
    }

    private void checkSendButton(final boolean animated) {
        String message = getTrimmedString(messageEditText.getText().toString());
        if (message.length() > 0 || forceShowSendButton) {
            if (audioSendButton.getVisibility() == View.VISIBLE) {
                if (animated) {
                    if (runningAnimationType == 1) {
                        return;
                    }
                    if (runningAnimation != null) {
                        runningAnimation.cancel();
                        runningAnimation = null;
                    }
                    if (runningAnimation2 != null) {
                        runningAnimation2.cancel();
                        runningAnimation2 = null;
                    }

                    if (attachButton != null) {
                        runningAnimation2 = new AnimatorSetProxy();
                        runningAnimation2.playTogether(
                                ObjectAnimatorProxy.ofFloat(attachButton, "alpha", 0.0f),
                                ObjectAnimatorProxy.ofFloat(attachButton, "scaleX", 0.0f)
                        );
                        runningAnimation2.setDuration(100);
                        runningAnimation2.addListener(new AnimatorListenerAdapterProxy() {
                            @Override
                            public void onAnimationEnd(Object animation) {
                                if (runningAnimation2.equals(animation)) {
                                    attachButton.setVisibility(View.GONE);
                                    attachButton.clearAnimation();
                                }
                            }
                        });
                        runningAnimation2.start();

                        if (messageEditText != null) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) messageEditText.getLayoutParams();
                            layoutParams.rightMargin = AndroidUtilities.dp(0);
                            messageEditText.setLayoutParams(layoutParams);
                        }

                        delegate.onAttachButtonHidden();
                    }

                    sendButton.setVisibility(View.VISIBLE);
                    runningAnimation = new AnimatorSetProxy();
                    runningAnimationType = 1;

                    runningAnimation.playTogether(
                            ObjectAnimatorProxy.ofFloat(audioSendButton, "scaleX", 0.1f),
                            ObjectAnimatorProxy.ofFloat(audioSendButton, "scaleY", 0.1f),
                            ObjectAnimatorProxy.ofFloat(audioSendButton, "alpha", 0.0f),
                            ObjectAnimatorProxy.ofFloat(sendButton, "scaleX", 1.0f),
                            ObjectAnimatorProxy.ofFloat(sendButton, "scaleY", 1.0f),
                            ObjectAnimatorProxy.ofFloat(sendButton, "alpha", 1.0f)
                    );

                    runningAnimation.setDuration(150);
                    runningAnimation.addListener(new AnimatorListenerAdapterProxy() {
                        @Override
                        public void onAnimationEnd(Object animation) {
                            if (runningAnimation.equals(animation)) {
                                sendButton.setVisibility(View.VISIBLE);
                                audioSendButton.setVisibility(View.GONE);
                                audioSendButton.clearAnimation();
                                runningAnimation = null;
                                runningAnimationType = 0;
                            }
                        }
                    });
                    runningAnimation.start();
                } else {
                    ViewProxy.setScaleX(audioSendButton, 0.1f);
                    ViewProxy.setScaleY(audioSendButton, 0.1f);
                    ViewProxy.setAlpha(audioSendButton, 0.0f);
                    ViewProxy.setScaleX(sendButton, 1.0f);
                    ViewProxy.setScaleY(sendButton, 1.0f);
                    ViewProxy.setAlpha(sendButton, 1.0f);
                    sendButton.setVisibility(View.VISIBLE);
                    audioSendButton.setVisibility(View.GONE);
                    audioSendButton.clearAnimation();
                    if (attachButton != null) {
                        attachButton.setVisibility(View.GONE);
                        attachButton.clearAnimation();
                        delegate.onAttachButtonHidden();
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) messageEditText.getLayoutParams();
                        layoutParams.rightMargin = AndroidUtilities.dp(0);
                        messageEditText.setLayoutParams(layoutParams);
                    }
                }
            }
        } else if (sendButton.getVisibility() == View.VISIBLE) {
            if (animated) {
                if (runningAnimationType == 2) {
                    return;
                }

                if (runningAnimation != null) {
                    runningAnimation.cancel();
                    runningAnimation = null;
                }
                if (runningAnimation2 != null) {
                    runningAnimation2.cancel();
                    runningAnimation2 = null;
                }

                if (attachButton != null) {
                    attachButton.setVisibility(View.VISIBLE);
                    runningAnimation2 = new AnimatorSetProxy();
                    runningAnimation2.playTogether(
                            ObjectAnimatorProxy.ofFloat(attachButton, "alpha", 1.0f),
                            ObjectAnimatorProxy.ofFloat(attachButton, "scaleX", 1.0f)
                    );
                    runningAnimation2.setDuration(100);
                    runningAnimation2.start();

                    if (messageEditText != null) {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) messageEditText.getLayoutParams();
                        layoutParams.rightMargin = AndroidUtilities.dp(50);
                        messageEditText.setLayoutParams(layoutParams);
                    }

                    delegate.onAttachButtonShow();
                }

                audioSendButton.setVisibility(View.VISIBLE);
                runningAnimation = new AnimatorSetProxy();
                runningAnimationType = 2;

                runningAnimation.playTogether(
                        ObjectAnimatorProxy.ofFloat(sendButton, "scaleX", 0.1f),
                        ObjectAnimatorProxy.ofFloat(sendButton, "scaleY", 0.1f),
                        ObjectAnimatorProxy.ofFloat(sendButton, "alpha", 0.0f),
                        ObjectAnimatorProxy.ofFloat(audioSendButton, "scaleX", 1.0f),
                        ObjectAnimatorProxy.ofFloat(audioSendButton, "scaleY", 1.0f),
                        ObjectAnimatorProxy.ofFloat(audioSendButton, "alpha", 1.0f)
                );

                runningAnimation.setDuration(150);
                runningAnimation.addListener(new AnimatorListenerAdapterProxy() {
                    @Override
                    public void onAnimationEnd(Object animation) {
                        if (runningAnimation.equals(animation)) {
                            sendButton.setVisibility(View.GONE);
                            sendButton.clearAnimation();
                            audioSendButton.setVisibility(View.VISIBLE);
                            runningAnimation = null;
                            runningAnimationType = 0;
                        }
                    }
                });
                runningAnimation.start();
            } else {
                ViewProxy.setScaleX(sendButton, 0.1f);
                ViewProxy.setScaleY(sendButton, 0.1f);
                ViewProxy.setAlpha(sendButton, 0.0f);
                ViewProxy.setScaleX(audioSendButton, 1.0f);
                ViewProxy.setScaleY(audioSendButton, 1.0f);
                ViewProxy.setAlpha(audioSendButton, 1.0f);
                sendButton.setVisibility(View.GONE);
                sendButton.clearAnimation();
                audioSendButton.setVisibility(View.VISIBLE);
                if (attachButton != null) {
                    delegate.onAttachButtonShow();
                    attachButton.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) messageEditText.getLayoutParams();
                    layoutParams.rightMargin = AndroidUtilities.dp(50);
                    messageEditText.setLayoutParams(layoutParams);
                }
            }
        }
    }

    private Timer recordTimeTimer;
    private final Object recordTimerSync = new Object();
    private int recordTime;
    private long recordLastCurrentTime;

    private void createRecordTimer() {
        if (recordTimeTimer != null) {
            return;
        }
        recordLastCurrentTime = System.currentTimeMillis();
        recordTimeTimer = new Timer();
        recordTimeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                double currentTime = System.currentTimeMillis();
                recordTime = (int) (currentTime - recordLastCurrentTime);
                android.os.Message.obtain(recordHandler, HANDLER_RECORD_PROGRESS).sendToTarget();
            }
        }, 0, 1000);
    }

    private void destroyRecordTimer() {
        try {
            synchronized (recordTimerSync) {
                if (recordTimeTimer != null) {
                    recordTimeTimer.cancel();
                    recordTimeTimer = null;
                }
                recordTimeText.setText("00:00");
                recordButton.setProgress(0);
                recordButton.onProgressCompleted();
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    private void setVoiceRecorderProgress(int progress) {
        if (isVoicePopupShowing()) {
            recordButton.setProgress(progress);
        }
    }

    private void updateAudioRecordIntefrace() {
        if (recordingAudio) {
            if (audioInterfaceState == 1) {
                return;
            }
            audioInterfaceState = 1;
            try {
                if (mWakeLock == null) {
                    PowerManager pm = (PowerManager) ApplicationLoader.applicationContext.getSystemService(Context.POWER_SERVICE);
                    mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "audio record lock");
                    mWakeLock.acquire();
                }
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
            recordLabel.setText("向上滑动取消发送");
            recordTimeText.setVisibility(View.VISIBLE);
            recordTimeText.setText("00:00");
            recordButton.setProgress(0);
            recordButton.onProgressCompleted();
        } else {
            if (mWakeLock != null) {
                try {
                    mWakeLock.release();
                    mWakeLock = null;
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }

            if (audioInterfaceState == 0) {
                return;
            }
            audioInterfaceState = 0;
            recordLabel.setText("触摸并按住开始录音");
            recordTimeText.setVisibility(View.INVISIBLE);
            recordTimeText.setText("00:00");
            recordButton.setProgress(0);
            recordButton.onProgressCompleted();

        }
    }

    public void openKeyboard() {
        setFieldFocused(true);
        AndroidUtilities.showKeyboard(messageEditText);
    }

    public void setDelegate(ChatActivityEnterViewDelegate delegate) {
        this.delegate = delegate;
    }

    public void setFieldText(String text) {
        if (messageEditText == null) {
            return;
        }
        ignoreTextChange = true;
        messageEditText.setText(text);
        messageEditText.setSelection(messageEditText.getText().length());
        ignoreTextChange = false;
        if (delegate != null) {
            delegate.onTextChanged(messageEditText.getText(), true);
        }
    }

    public int getCursorPosition() {
        if (messageEditText == null) {
            return 0;
        }
        return messageEditText.getSelectionStart();
    }

    public void replaceWithText(int start, int len, String text) {
        try {
            StringBuilder builder = new StringBuilder(messageEditText.getText());
            builder.replace(start, start + len, text);
            messageEditText.setText(builder);
            messageEditText.setSelection(start + text.length());
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    public void setFieldFocused(boolean focus) {
        if (messageEditText == null) {
            return;
        }
        if (focus) {
            if (!messageEditText.isFocused()) {
                messageEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (messageEditText != null) {
                            try {
                                messageEditText.requestFocus();
                            } catch (Exception e) {
                                FileLog.e("tmessages", e);
                            }
                        }
                    }
                }, 600);
            }
        } else {
            if (messageEditText.isFocused()) {
                messageEditText.clearFocus();
            }
        }
    }

    public boolean hasText() {
        return messageEditText != null && messageEditText.length() > 0;
    }

    public String getFieldText() {
        if (messageEditText != null && messageEditText.length() > 0) {
            return messageEditText.getText().toString();
        }
        return null;
    }

    public boolean isEmojiShowing() {
        return emojiView != null && emojiView.getVisibility() == View.VISIBLE;
    }

    public void addToAttachLayout(View view) {
        if (attachButton == null) {
            return;
        }
        if (view.getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            viewGroup.removeView(view);
        }
        attachButton.addView(view);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = AndroidUtilities.dp(48);
        layoutParams.height = LayoutParams.MATCH_PARENT;
        view.setLayoutParams(layoutParams);
    }

    private int keyboardHeight;

    private void onWindowSizeChanged() {
        int size = 0;
        if (topView != null) {
            if (size < AndroidUtilities.dp(72) + AndroidUtilities.getCurrentActionBarHeight()) {
                if (allowShowTopView) {
                    allowShowTopView = false;
                    if (needShowTopView) {
                        topView.setVisibility(View.GONE);
                        setTopViewAnimation(0.0f);
                    }
                }
            } else {
                if (!allowShowTopView) {
                    allowShowTopView = true;
                    if (needShowTopView) {
                        topView.setVisibility(View.VISIBLE);
                        setTopViewAnimation(1.0f);
                    }
                }
            }
        }
    }

    public void onListScrollChanged() {
        hidePopup();
        hideVoicePopup();
        setFieldFocused(false);
        AndroidUtilities.hideKeyboard(messageEditText);
    }

    public boolean isPopupShowing() {
        return emojiView != null && emojiView.getVisibility() == VISIBLE;
    }

    public boolean isVoicePopupShowing() {
        return recordContainer != null && recordContainer.getVisibility() == VISIBLE;
    }

    private int currentPopupContentType = -1;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;

    private void showPopup(int show, int contentType) {
        if (show == 1) {
            View currentView = null;
            if (contentType == 0) {
                emojiView.setVisibility(VISIBLE);
                if (recordContainer != null && recordContainer.getVisibility() != GONE) {
                    recordContainer.setVisibility(GONE);
                }
                currentView = emojiView;
            } else if (contentType == 1) {
                if (emojiView != null && emojiView.getVisibility() != GONE) {
                    emojiView.setVisibility(GONE);
                }
                recordContainer.setVisibility(VISIBLE);
                currentView = recordContainer;
            }
            currentPopupContentType = contentType;
            keyboardHeight = AndroidUtilities.dp(268);
            int currentHeight = keyboardHeight;
            LayoutParams layoutParams = (LayoutParams) currentView.getLayoutParams();
            layoutParams.width = AndroidUtilities.displaySize.x;
            layoutParams.height = currentHeight;
            currentView.setLayoutParams(layoutParams);
            AndroidUtilities.hideKeyboard(messageEditText);
            if (contentType == 0) {
                emojiButton.setImageResource(R.drawable.ic_msg_panel_kb);
            } else if (contentType == 1) {
                emojiButton.setImageResource(R.drawable.ic_msg_panel_smiles);
            }
            onWindowSizeChanged();
        } else {
            if (emojiButton != null) {
                emojiButton.setImageResource(R.drawable.ic_msg_panel_smiles);
            }
            if (emojiView != null) {
                emojiView.setVisibility(GONE);
            }
            if (recordContainer != null) {
                recordContainer.setVisibility(GONE);
            }
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            if (emojiView != null) {
                emojiView.invalidateViews();
            }
        } else if (id == NotificationCenter.closeChats) {
            if (messageEditText != null && messageEditText.isFocused()) {
                AndroidUtilities.hideKeyboard(messageEditText);
            }
        } else if (id == NotificationCenter.audioDidSent) {
            if (delegate != null) {
                delegate.onMessageSend(null);
            }
        }
//        else if (key == NotificationCenter.hideEmojiKeyboard) {
//            hideEmojiPopup();
//        }
    }

    private static final int HANDLER_RECORD_PROGRESS = 3;
    private static final int HANDLER_RECORD_SENDING = 4;
    private Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {
            final int what = message.what;
            if (what == HANDLER_RECORD_PROGRESS) {
                int minutes = recordTime / 1000 / 60;
                int seconds = recordTime / 1000 - minutes * 60;
                recordTimeText.setText(String.format("%1$d:%2$02d", minutes, seconds));
            } else if (what == HANDLER_RECORD_SENDING) {
                Map<String, String> record = (Map) message.obj;
                String filePath = record.get("file_path");
                String fileName = record.get("file_name");
                int length = Integer.parseInt(record.get("file_length"));
                if (delegate != null) {
                    delegate.onVoiceMessageSend(filePath, fileName, length, false);
                }
            }
        }
    };
}
