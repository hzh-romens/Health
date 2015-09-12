/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.romens.yjk.health.im.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.applib.model.GroupRemoveListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.core.ImageReceiver;
import com.romens.android.core.NotificationCenter;
import com.romens.android.log.FileLog;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.ActionBar.BottomSheet;
import com.romens.android.ui.AnimationCompat.AnimatorListenerAdapterProxy;
import com.romens.android.ui.AnimationCompat.ObjectAnimatorProxy;
import com.romens.android.ui.AnimationCompat.ViewProxy;
import com.romens.android.ui.Components.FrameLayoutFixed;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.IMMessagesController;
import com.romens.yjk.health.db.entity.MessageObject;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.im.IMHXSDKHelper;
import com.romens.yjk.health.im.VoicePlayController;
import com.romens.yjk.health.im.ui.cell.ChatCell;
import com.romens.yjk.health.im.ui.cell.ChatImageCell;
import com.romens.yjk.health.im.ui.cell.ChatImageForFromCell;
import com.romens.yjk.health.im.ui.cell.ChatImageForToCell;
import com.romens.yjk.health.im.ui.cell.ChatLocationMessageCell;
import com.romens.yjk.health.im.ui.cell.ChatLocationMessageForFromCell;
import com.romens.yjk.health.im.ui.cell.ChatLocationMessageForToCell;
import com.romens.yjk.health.im.ui.cell.ChatTextForFromCell;
import com.romens.yjk.health.im.ui.cell.ChatTextForToCell;
import com.romens.yjk.health.im.ui.cell.ChatVoiceMessageForFromCell;
import com.romens.yjk.health.im.ui.cell.ChatVoiceMessageForToCell;
import com.romens.yjk.health.im.ui.components.ChatActivityEnterView;
import com.romens.yjk.health.im.ui.components.ChatAttachView;
import com.romens.yjk.health.ui.BaseActivity;
import com.romens.yjk.health.ui.LocationActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天页面
 */
public class IMChatActivity extends BaseActivity implements NotificationCenter.NotificationCenterDelegate {
    public static final String ARGUMENT_CHAT_TYPE = "argument_chat_type";
    public static final String ARGUMENT_CHAT_ID = "argument_chat_id";

    private static final String TAG = "ChatActivity";
    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_PICK_VIDEO = 12;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;

    public static final String COPY_IMAGE = "EASEMOBIMG";

    ///

    private ActionBarMenuItem menuItem;
    private ActionBarMenuItem attachItem;
    private ActionBarMenuItem headerItem;

    private FrameLayout avatarContainer;
    private BackupImageView avatarImageView;
    private TextView nameTextView;
    private TextView onlineTextView;

    private RecyclerView chatListView;
    private LinearLayoutManager chatLayoutManager;
    private ChatAdapter chatAdapter;

    private FrameLayout emptyViewContainer;

    private FrameLayout progressView;


    private ChatActivityEnterView chatActivityEnterView;

    private BackupImageView replyImageView;
    private TextView replyNameTextView;
    private TextView replyObjectTextView;
    private ImageView replyIconImageView;


    private TextView bottomOverlayText;
    private FrameLayout bottomOverlay;

    private TextView bottomOverlayChatText;
    private FrameLayout bottomOverlayChat;

    private ImageView pagedownButton;


    private ArrayList<View> actionModeViews = new ArrayList<>();

    private final static int id_chat_compose_panel = 1000;


    private int chatType;
    private String chatId;
    private EMConversation conversation;

    private UserEntity chatUser;
    private EMGroup chatGroup;
    private GroupListener groupListener;
    private EMChatRoom chatRoom;

    private List<MessageObject> messages = new ArrayList<>();


    private final static int copy = 10;
    private final static int forward = 11;
    private final static int delete = 12;
    private final static int chat_enc_timer = 13;
    private final static int chat_menu_attach = 14;
    private final static int clear_history = 15;
    private final static int delete_chat = 16;
    private final static int share_contact = 17;
    private final static int mute = 18;
    private final static int reply = 19;

    private final static int bot_help = 30;
    private final static int bot_settings = 31;

    private final static int attach_photo = 0;
    private final static int attach_gallery = 1;
    private final static int attach_video = 2;
    private final static int attach_audio = 3;
    private final static int attach_document = 4;
    private final static int attach_contact = 5;
    private final static int attach_location = 6;

    private final static int search = 40;
    private final static int search_up = 41;
    private final static int search_down = 42;


    private static final int pagesize = 20;
    private boolean loading = false;
    private boolean hasMore = true;

    private ChatAttachView chatAttachView;
    private BottomSheet chatAttachViewSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;
        setContentView(R.layout.im_chat_layout, R.id.action_bar);
        ActionBar actionBar = getMyActionBar();

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(final int id) {
                if (id == -1) {
                    finish();
                } else {
                    handleActionBarMenuClick(id);
                }
            }
        });

        avatarContainer = new FrameLayoutFixed(context);
        avatarContainer.setBackgroundResource(R.drawable.bar_selector);
        avatarContainer.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        actionBar.addView(avatarContainer,
                LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT,
                        Gravity.TOP | Gravity.LEFT, 56, 0, 40, 0));
        avatarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(21));
        avatarContainer.addView(avatarImageView, LayoutHelper.createFrame(42, 42, Gravity.CENTER_VERTICAL | Gravity.LEFT, 0, 0, 0, 0));


        nameTextView = new TextView(context);
        nameTextView.setTextColor(0xffffffff);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        nameTextView.setLines(1);
        nameTextView.setMaxLines(1);
        nameTextView.setSingleLine(true);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setGravity(Gravity.LEFT);
        nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4));
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        avatarContainer.addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.BOTTOM, 54, 0, 0, 22));

        onlineTextView = new TextView(context);
        onlineTextView.setTextColor(0xffd7e8f7);
        onlineTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        onlineTextView.setLines(1);
        onlineTextView.setMaxLines(1);
        onlineTextView.setSingleLine(true);
        onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
        onlineTextView.setGravity(Gravity.LEFT);
        avatarContainer.addView(onlineTextView,
                LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                        Gravity.LEFT | Gravity.BOTTOM, 54, 0, 0, 4));

        ActionBarMenu menu = actionBar.createMenu();


        headerItem = menu.addItem(0, R.drawable.ic_ab_other);
//        if (searchItem != null) {
//            headerItem.addSubItem(search, LocaleController.getString("Search", R.string.Search), 0);
//        }
//        if (currentUser != null) {
//            addContactItem = headerItem.addSubItem(share_contact, "", 0);
//        }
//        if (currentEncryptedChat != null) {
//            timeItem2 = headerItem.addSubItem(chat_enc_timer, LocaleController.getString("SetTimer", R.string.SetTimer), 0);
//        }
        headerItem.addSubItem(clear_history, "清除历史信息", 0);
//        if (currentChat != null && !isBroadcast) {
//            headerItem.addSubItem(delete_chat, LocaleController.getString("DeleteAndExit", R.string.DeleteAndExit), 0);
//        } else {
//            headerItem.addSubItem(delete_chat, LocaleController.getString("DeleteChatUser", R.string.DeleteChatUser), 0);
//        }
//        muteItem = headerItem.addSubItem(mute, null, 0);
//        if (currentUser != null && currentEncryptedChat == null && (currentUser.flags & TLRPC.USER_FLAG_BOT) != 0) {
//            headerItem.addSubItem(bot_settings, LocaleController.getString("BotSettings", R.string.BotSettings), 0);
//            headerItem.addSubItem(bot_help, LocaleController.getString("BotHelp", R.string.BotHelp), 0);
//            updateBotButtons();
//        }

//        attachItem = menu.addItem(chat_menu_attach, R.drawable.ic_ab_other).setAllowCloseAnimation(false);
//        attachItem.addSubItem(attach_photo, "拍照", R.drawable.ic_attach_photo);
//        attachItem.addSubItem(attach_gallery, "图片", R.drawable.ic_attach_gallery);
//        attachItem.addSubItem(attach_video, "视频", R.drawable.ic_attach_video);
//        attachItem.addSubItem(attach_document, "文件", R.drawable.ic_ab_doc);
//        attachItem.addSubItem(attach_location, "位置", R.drawable.ic_attach_location);
//        attachItem.setVisibility(View.GONE);

        attachItem = menu.addItem(chat_menu_attach, R.drawable.ic_ab_other).setOverrideMenuClick(true).setAllowCloseAnimation(false);
        attachItem.setVisibility(View.GONE);

        menuItem = menu.addItem(chat_menu_attach, R.drawable.ic_ab_attach).setAllowCloseAnimation(false);

        menuItem.setBackgroundDrawable(null);

        actionModeViews.clear();

        final ActionBarMenu actionMode = actionBar.createActionMode();
        actionModeViews.add(actionMode.addItem(-2, R.drawable.ic_ab_back_grey, R.drawable.bar_selector_mode, null, AndroidUtilities.dp(54)));


        actionModeViews.add(actionMode.addItem(copy, R.drawable.ic_ab_fwd_copy, R.drawable.bar_selector_mode, null, AndroidUtilities.dp(54)));
        actionModeViews.add(actionMode.addItem(reply, R.drawable.ic_ab_reply, R.drawable.bar_selector_mode, null, AndroidUtilities.dp(54)));
        actionModeViews.add(actionMode.addItem(forward, R.drawable.ic_ab_fwd_forward, R.drawable.bar_selector_mode, null, AndroidUtilities.dp(54)));
        actionModeViews.add(actionMode.addItem(delete, R.drawable.ic_ab_fwd_delete, R.drawable.bar_selector_mode, null, AndroidUtilities.dp(54)));
        //actionMode.getItem(copy).setVisibility(selectedMessagesCanCopyIds.size() != 0 ? View.VISIBLE : View.GONE);
        if (actionMode.getItem(reply) != null) {
            //actionMode.getItem(reply).setVisibility(selectedMessagesIds.size() == 1 ? View.VISIBLE : View.GONE);
        }
        View layoutContent = findViewById(R.id.layout_content);
        layoutContent.setBackgroundColor(Color.WHITE);

        FrameLayout contentView = (FrameLayout) this.findViewById(R.id.chat_content);

        emptyViewContainer = new FrameLayout(context);
        emptyViewContainer.setPadding(0, 0, 0, AndroidUtilities.dp(48));
        emptyViewContainer.setVisibility(View.INVISIBLE);
        contentView.addView(emptyViewContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        emptyViewContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        TextView emptyView = new TextView(context);
        emptyView.setText("无消息");
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextColor(0xffffffff);
        emptyView.setBackgroundResource(R.drawable.system_black);
        emptyView.setPadding(AndroidUtilities.dp(7), AndroidUtilities.dp(1), AndroidUtilities.dp(7), AndroidUtilities.dp(1));
        emptyViewContainer.addView(emptyView,
                new FrameLayout.LayoutParams(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                        Gravity.CENTER));

        chatListView = new RecyclerView(context);
        chatListView.setAdapter(chatAdapter = new ChatAdapter(context));
        chatListView.setClipToPadding(false);
        chatListView.setPadding(0, AndroidUtilities.dp(4), 0, AndroidUtilities.dp(3));
        chatLayoutManager = new LinearLayoutManager(context);
        chatLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatLayoutManager.setStackFromEnd(true);
        chatListView.setLayoutManager(chatLayoutManager);
        contentView.addView(chatListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        chatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (chatActivityEnterView != null) {
                    chatActivityEnterView.onListScrollChanged();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = chatLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem == RecyclerView.NO_POSITION ? 0 : Math.abs(chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                if (firstVisibleItem != 0) {
                    showPageDownButton(true, true);
                }

                if (visibleItemCount > 0) {
                    int totalItemCount = chatAdapter.getItemCount();
                    if (firstVisibleItem <= 10) {
                        if (hasMore && !loading) {
                            loading = true;
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    String msgId = null;
                                    if (messages.size() != 0) {
                                        msgId = messages.get(0).getDialogId();
                                    }
                                    List<EMMessage> messages;
                                    try {
                                        if (chatType == CHATTYPE_SINGLE) {
                                            messages = conversation.loadMoreMsgFromDB(msgId, pagesize);
                                        } else {
                                            messages = conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
                                        }
                                    } catch (Exception e1) {
                                        //updateEmpty(loading = false);
                                        return;
                                    }

                                    if (messages.size() > 0) {
                                        if (messages.size() != pagesize) {
                                            hasMore = false;
                                        }
                                        refreshWithLoadMessages();
                                    } else {
                                        hasMore = false;
                                    }
                                    loading = false;
                                }
                            });
                            //updateEmpty(loading = false);
                        }
                    }

                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        showPageDownButton(false, true);
                    }
                    updateMessagesVisisblePart();
                }
            }
        });

        progressView = new FrameLayout(context);
        progressView.setVisibility(View.INVISIBLE);
        contentView.addView(progressView,
                LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                        Gravity.TOP | Gravity.LEFT));

        View view = new View(context);
        view.setBackgroundResource(ApplicationLoader.isCustomTheme() ? R.drawable.system_loader2 : R.drawable.system_loader1);
        progressView.addView(view, LayoutHelper.createFrame(36, 36, Gravity.CENTER));

        ProgressBar progressBar = new ProgressBar(context);
        try {
            progressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.loading_animation));
        } catch (Exception e) {
            //don't promt
        }
        progressBar.setIndeterminate(true);
        AndroidUtilities.setProgressBarAnimationDuration(progressBar, 1500);
        progressView.addView(progressBar, LayoutHelper.createFrame(32, 32, Gravity.CENTER));

        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        chatActivityEnterView = (ChatActivityEnterView) findViewById(R.id.chat_input);
        //chatActivityEnterView.setDialogId(dialog_id);
        chatActivityEnterView.addToAttachLayout(menuItem);
        //chatActivityEnterView.setId(id_chat_compose_panel);
        chatActivityEnterView.setDialogId(chatId);
        chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() {
            @Override
            public void onMessageSend(String message) {
                sendMessageForText(message);
            }

            @Override
            public void onVoiceMessageSend(final String filePath, String fileName, final int length, boolean isResend) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (!(new File(filePath).exists())) {
                            return;
                        }
                        try {
                            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
                            // 如果是群聊，设置chattype,默认是单聊
                            if (chatType == CHATTYPE_GROUP) {
                                message.setChatType(EMMessage.ChatType.GroupChat);
                            } else if (chatType == CHATTYPE_CHATROOM) {
                                message.setChatType(EMMessage.ChatType.ChatRoom);
                            }
                            message.setReceipt(chatId);
                            VoiceMessageBody body = new VoiceMessageBody(new File(filePath), length);
                            message.addBody(body);
                            conversation.addMessage(message);
                            refreshSelectLast();
                            // send file
                            // sendVoiceSub(filePath, fileName, message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void needClearVoicePlay() {
                VoicePlayController.getInstance().stopPlayVoice();
            }

            @Override
            public void onTextChanged(final CharSequence text, boolean bigChange) {

//                if (waitingForCharaterEnterRunnable != null) {
//                    AndroidUtilities.cancelRunOnUIThread(waitingForCharaterEnterRunnable);
//                    waitingForCharaterEnterRunnable = null;
//                }
//                if (bigChange) {
//                    searchLinks(text, true);
//                } else {
//                    waitingForCharaterEnterRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            if (this == waitingForCharaterEnterRunnable) {
//                                searchLinks(text, false);
//                                waitingForCharaterEnterRunnable = null;
//                            }
//                        }
//                    };
//                    AndroidUtilities.runOnUIThread(waitingForCharaterEnterRunnable, 1000);
//                }
            }

            @Override
            public void needSendTyping() {
                //MessagesController.getInstance().sendTyping(dialog_id, classGuid);
            }

            @Override
            public void onAttachButtonHidden() {
                if (attachItem != null) {
                    attachItem.setVisibility(View.VISIBLE);
                }
                if (headerItem != null) {
                    headerItem.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAttachButtonShow() {
                if (attachItem != null) {
                    attachItem.setVisibility(View.GONE);
                }
                if (headerItem != null) {
                    headerItem.setVisibility(View.VISIBLE);
                }
            }
        });

        FrameLayout replyLayout = new FrameLayout(context);
        replyLayout.setClickable(true);
        chatActivityEnterView.addTopView(replyLayout, AndroidUtilities.dp(48));

        View lineView = new View(context);
        lineView.setBackgroundColor(0xffe8e8e8);
        replyLayout.addView(lineView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 1, Gravity.BOTTOM | Gravity.LEFT));
        replyIconImageView = new ImageView(context);
        replyIconImageView.setScaleType(ImageView.ScaleType.CENTER);
        replyLayout.addView(replyIconImageView, LayoutHelper.createFrame(52, 46, Gravity.TOP | Gravity.LEFT));

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.delete_reply);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        replyLayout.addView(imageView, LayoutHelper.createFrame(52, 46, Gravity.RIGHT | Gravity.TOP, 0, 0.5f, 0, 0));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (forwardingMessages != null) {
//                    forwardingMessages.clear();
//                }
//                showReplyPanel(false, null, null, foundWebPage, true, true);
            }
        });

        replyNameTextView = new TextView(context);
        replyNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        replyNameTextView.setTextColor(0xff377aae);
        replyNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        replyNameTextView.setSingleLine(true);
        replyNameTextView.setEllipsize(TextUtils.TruncateAt.END);
        replyNameTextView.setMaxLines(1);
        replyLayout.addView(replyNameTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 52, 4, 52, 0));

        replyObjectTextView = new TextView(context);
        replyObjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        replyObjectTextView.setTextColor(0xff999999);
        replyObjectTextView.setSingleLine(true);
        replyObjectTextView.setEllipsize(TextUtils.TruncateAt.END);
        replyObjectTextView.setMaxLines(1);
        replyLayout.addView(replyObjectTextView,
                LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                        Gravity.TOP | Gravity.LEFT, 52, 22, 52, 0));

        replyImageView = new BackupImageView(context);
        replyLayout.addView(replyImageView,
                LayoutHelper.createFrame(34, 34, Gravity.TOP | Gravity.LEFT, 52, 6, 0, 0));

        bottomOverlay = new FrameLayout(context);
        bottomOverlay.setBackgroundColor(0xffffffff);
        bottomOverlay.setVisibility(View.INVISIBLE);
        bottomOverlay.setFocusable(true);
        bottomOverlay.setFocusableInTouchMode(true);
        bottomOverlay.setClickable(true);
        contentView.addView(bottomOverlay, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM));

        bottomOverlayText = new TextView(context);
        bottomOverlayText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        bottomOverlayText.setTextColor(0xff7f7f7f);
        bottomOverlay.addView(bottomOverlayText,
                LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                        Gravity.CENTER));

        bottomOverlayChat = new FrameLayout(context);
        bottomOverlayChat.setBackgroundColor(0xfffbfcfd);
        bottomOverlayChat.setVisibility(View.INVISIBLE);
        contentView.addView(bottomOverlayChat, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM));
        bottomOverlayChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (getParentActivity() == null) {
//                    return;
//                }
//                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
//                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
//                if (currentUser != null && userBlocked) {
//                    builder.setMessage(LocaleController.getString("AreYouSureUnblockContact", R.string.AreYouSureUnblockContact));
//                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            MessagesController.getInstance().unblockUser(currentUser.id);
//                        }
//                    });
//                } else {
//                    builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
//                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            MessagesController.getInstance().deleteDialog(dialog_id, 0, false);
//                            finishFragment();
//                        }
//                    });
//                }
//                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
//                showAlertDialog(builder);
            }
        });

        bottomOverlayChatText = new TextView(context);
        bottomOverlayChatText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        bottomOverlayChatText.setTextColor(0xff3e6fa1);
        bottomOverlayChat.addView(bottomOverlayChatText, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        pagedownButton = new ImageView(context);
        pagedownButton.setVisibility(View.INVISIBLE);
        pagedownButton.setImageResource(R.drawable.pagedown);
        contentView.addView(pagedownButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.BOTTOM, 0, 0, 6, 4));
        pagedownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToLastMessage();
            }
        });

        updateContactStatus();
        updateBottomOverlay();

        updateEmpty(false);

        Bundle arguments = getIntent().getExtras();
        if (arguments == null) {
            finish();
            return;
        }
        // 判断单聊还是群聊
        chatType = arguments.getInt(ARGUMENT_CHAT_TYPE, CHATTYPE_SINGLE);
        chatId = arguments.getString(ARGUMENT_CHAT_ID);
        if (chatType == CHATTYPE_SINGLE) { // 单聊
            chatUser = IMMessagesController.getInstance().getUser(chatId);
        } else {
            if (chatType == CHATTYPE_GROUP) {
                onGroupChatCreation();
            } else {
                onChatRoomCreation();
            }
        }

        // for chatroom type, we only init conversation and create view adapter on success
        if (chatType != CHATTYPE_CHATROOM) {
            onConversationInit();

//            // show forward message if the message is not null
//            String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
//            if (forward_msg_id != null) {
//                // 显示发送要转发的消息
//                forwardMessage(forward_msg_id);
//            }
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesRead);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);


    }

    private void handleActionBarMenuClick(int id) {
        if (id == chat_menu_attach) {
            if (chatAttachView == null) {
                BottomSheet.Builder builder = new BottomSheet.Builder(IMChatActivity.this);
                chatAttachView = new ChatAttachView(IMChatActivity.this);
                chatAttachView.setDelegate(new ChatAttachView.ChatAttachViewDelegate() {
                    @Override
                    public void didPressedButton(int button) {
                        chatAttachViewSheet.dismissWithButtonClick(button);
                        processSelectedAttach(button);
                    }
                });
                builder.setDelegate(new BottomSheet.BottomSheetDelegate() {

                    @Override
                    public void onRevealAnimationStart(boolean open) {
                        if (chatAttachView != null) {
                            chatAttachView.onRevealAnimationStart(open);
                        }
                    }

                    @Override
                    public void onRevealAnimationProgress(boolean open, float radius, int x, int y) {
                        if (chatAttachView != null) {
                            chatAttachView.onRevealAnimationProgress(open, radius, x, y);
                        }
                    }

                    @Override
                    public void onRevealAnimationEnd(boolean open) {
                        if (chatAttachView != null) {
                            chatAttachView.onRevealAnimationEnd(open);
                        }
                    }

                    @Override
                    public void onOpenAnimationEnd() {
                        if (chatAttachView != null) {
                            chatAttachView.onRevealAnimationEnd(true);
                        }
                    }

                    @Override
                    public View getRevealView() {
                        return menuItem;
                    }
                });
                builder.setApplyTopPaddings(false);
                builder.setUseRevealAnimation();
                builder.setCustomView(chatAttachView);
                chatAttachViewSheet = builder.create();
            }

            chatAttachView.init(IMChatActivity.this);
            showDialog(chatAttachViewSheet);
        }
    }


    private void updateMessagesVisisblePart() {
//        if (chatListView == null) {
//            return;
//        }
//        int count = chatListView.getChildCount();
//        for (int a = 0; a < count; a++) {
//            View view = chatListView.getChildAt(a);
//            if (view instanceof ChatMessageCell) {
//                ChatMessageCell messageCell = (ChatMessageCell) view;
//                messageCell.getLocalVisibleRect(scrollRect);
//                messageCell.setVisiblePart(scrollRect.top, scrollRect.bottom - scrollRect.top);
//            }
//        }
    }

    private Rect scrollRect = new Rect();

    protected void onGroupChatCreation() {
        chatGroup = EMGroupManager.getInstance().getGroup(chatId);
        // 监听当前会话的群聊解散被T事件
        groupListener = new GroupListener();
        EMGroupManager.getInstance().addGroupChangeListener(groupListener);
    }

    private void refreshUIWithNewMessage() {
        if (chatAdapter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                refreshSelectLast();
            }
        });
    }

    private void refreshUI() {
        if (chatAdapter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                refreshWithLoadMessages();
            }
        });
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didReceivedNewMessages) {
            refreshUIWithNewMessage();
        }
    }

    /**
     * 用户信息变更
     *
     * @param userId
     */
    private void onUserProfileChanged(String userId) {
        chatUser = IMMessagesController.getInstance().getUser(userId);
        updateTitle();
        updateSubtitle();
        updateTitleIcons();
        checkAndUpdateAvatar();
    }

    class GroupListener extends GroupRemoveListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.equals(chatId, groupId)) {
//                        Toast.makeText(ChatActivity.this, st13, 1).show();
//                        if (GroupDetailsActivity.instance != null)
//                            GroupDetailsActivity.instance.finish();
                        finish();
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(final String groupId, String groupName) {
            // 群组解散正好在此页面，提示群组被解散，并finish此页面
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.equals(chatId, groupId)) {
//                        Toast.makeText(ChatActivity.this, st14, 1).show();
//                        if (GroupDetailsActivity.instance != null)
//                            GroupDetailsActivity.instance.finish();
                        finish();
                    }
                }
            });
        }

    }

    private void showPageDownButton(boolean show, boolean animated) {
        if (pagedownButton == null) {
            return;
        }
        if (show) {
            if (pagedownButton.getVisibility() == View.INVISIBLE) {
                if (animated) {
                    pagedownButton.setVisibility(View.VISIBLE);
                    ViewProxy.setAlpha(pagedownButton, 0);
                    ObjectAnimatorProxy.ofFloatProxy(pagedownButton, "alpha", 1.0f).setDuration(200).start();
                } else {
                    pagedownButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (pagedownButton.getVisibility() == View.VISIBLE) {
                if (animated) {
                    ObjectAnimatorProxy.ofFloatProxy(pagedownButton, "alpha", 0.0f).setDuration(200).addListener(new AnimatorListenerAdapterProxy() {
                        @Override
                        public void onAnimationEnd(Object animation) {
                            pagedownButton.setVisibility(View.INVISIBLE);
                        }
                    }).start();
                } else {
                    pagedownButton.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    protected void onChatRoomCreation() {
        EMChatManager.getInstance().joinChatRoom(chatId, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(EMChatRoom value) {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatRoom = EMChatManager.getInstance().getChatRoom(chatId);
                        onConversationInit();
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                finish();
            }
        });
    }


    protected void onConversationInit() {
        if (chatType == CHATTYPE_SINGLE) {
            conversation = EMChatManager.getInstance().getConversationByType(chatId, EMConversation.EMConversationType.Chat);
        } else if (chatType == CHATTYPE_GROUP) {
            conversation = EMChatManager.getInstance().getConversationByType(chatId, EMConversation.EMConversationType.GroupChat);
        } else if (chatType == CHATTYPE_CHATROOM) {
            conversation = EMChatManager.getInstance().getConversationByType(chatId, EMConversation.EMConversationType.ChatRoom);
        }

        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            if (chatType == CHATTYPE_SINGLE) {
                conversation.loadMoreMsgFromDB(msgId, pagesize);
            } else {
                conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
            }
        }


        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (TextUtils.equals(roomId, chatId)) {
                    finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {

            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                                       String participant) {
                if (TextUtils.equals(roomId, chatId)) {
                    String curUser = EMChatManager.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMChatManager.getInstance().leaveChatRoom(chatId);
                        finish();
                    }
                }
            }

        });

        updateTitle();
        updateSubtitle();
        updateTitleIcons();
        checkActionBarMenu();
        handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST).sendToTarget();
    }


    private void checkActionBarMenu() {
        if (menuItem != null) {
            menuItem.setVisibility(View.VISIBLE);
        }

        checkAndUpdateAvatar();
    }

    private void checkAndUpdateAvatar() {
        String newPhoto = null;
        AvatarDrawable avatarDrawable = null;
        if (chatType == CHATTYPE_SINGLE) {
            if (chatUser != null) {
                avatarDrawable = new AvatarDrawable();
                avatarDrawable.setInfo(chatUser.getId(), chatUser.getName());
                newPhoto = chatUser.getAvatar();
            }
        } else if (chatType == CHATTYPE_GROUP) {
            if (chatGroup != null) {
                avatarDrawable = new AvatarDrawable();
                int id = AvatarDrawable.tryStringToInt(chatGroup.getGroupId());
                avatarDrawable.setInfo(id, chatGroup.getGroupName());
            }
        } else if (chatType == CHATTYPE_CHATROOM) {
            if (chatRoom != null) {
                avatarDrawable = new AvatarDrawable();
                int id = AvatarDrawable.tryStringToInt(chatRoom.getId());
                avatarDrawable.setInfo(id, chatRoom.getName());
            }
        }
        if (avatarDrawable == null) {
            avatarDrawable.setInfo(0, chatId, "", true);
        }
        if (avatarImageView != null) {
            avatarImageView.setImageUrl(newPhoto, "50_50", avatarDrawable);
        }
    }

    private void updateContactStatus() {
//		if (addContactItem == null) {
//			return;
//		}
//		if (currentUser == null) {
//			addContactItem.setVisibility(View.GONE);
//		} else {
//			TLRPC.User user = MessagesController.getInstance().getUser(currentUser.id);
//			if (user != null) {
//				currentUser = user;
//			}
//			if (currentEncryptedChat != null && !(currentEncryptedChat instanceof TLRPC.TL_encryptedChat)
//					|| currentUser.id / 1000 == 333 || currentUser.id / 1000 == 777
//					|| currentUser instanceof TLRPC.TL_userEmpty || currentUser instanceof TLRPC.TL_userDeleted
//					|| ContactsController.getInstance().isLoadingContacts()
//					|| (currentUser.phone != null && currentUser.phone.length() != 0 && ContactsController.getInstance().contactsDict.get(currentUser.id) != null && (ContactsController.getInstance().contactsDict.size() != 0 || !ContactsController.getInstance().isLoadingContacts()))) {
//				addContactItem.setVisibility(View.GONE);
//			} else {
//				addContactItem.setVisibility(View.VISIBLE);
//				if (currentUser.phone != null && currentUser.phone.length() != 0) {
//					addContactItem.setText(LocaleController.getString("AddToContacts", com.yunuo.im.R.string.AddToContacts));
//				} else {
//					addContactItem.setText(LocaleController.getString("ShareMyContactInfo", com.yunuo.im.R.string.ShareMyContactInfo));
//				}
//			}
//		}
    }

    private void updateTitle() {
        if (nameTextView == null) {
            return;
        }
        String chatName = null;
        if (chatType == CHATTYPE_SINGLE) {
            if (chatUser != null) {
                chatName = chatUser.getName();
            }
        } else if (chatType == CHATTYPE_GROUP) {
            if (chatGroup != null) {
                chatName = chatGroup.getGroupName();
            }
        } else if (chatType == CHATTYPE_CHATROOM) {
            if (chatRoom != null) {
                chatName = chatRoom.getName();
            }
        }

        if (TextUtils.isEmpty(chatName)) {
            nameTextView.setText(chatId);
        } else {
            nameTextView.setText(chatName);
        }
    }

    private void updateTitleIcons() {
        boolean isBlock = false;
        if (chatType == CHATTYPE_SINGLE) {
            if (chatUser != null) {
                isBlock = false;
            }
        } else if (chatType == CHATTYPE_GROUP) {
            if (chatGroup != null) {
                isBlock = chatGroup.isMsgBlocked();
            }
        } else if (chatType == CHATTYPE_CHATROOM) {
            if (chatRoom != null) {
                isBlock = chatRoom.isMsgBlocked();
            }
        }
        int leftIcon = isBlock ? R.drawable.mute_fixed : 0;
        nameTextView.setCompoundDrawablesWithIntrinsicBounds(leftIcon, 0, 0, 0);

//		if (rightIcon != 0) {
//			muteItem.setText(LocaleController.getString("UnmuteNotifications", com.yunuo.im.R.string.UnmuteNotifications));
//		} else {
//			muteItem.setText(LocaleController.getString("MuteNotifications", com.yunuo.im.R.string.MuteNotifications));
//		}
    }

    private void updateSubtitle() {
        if (onlineTextView == null) {
            return;
        }

        String chatDesc = null;
        if (chatType == CHATTYPE_SINGLE) {
            if (chatUser != null) {
                chatDesc = chatUser.getPhone();
            }
        } else if (chatType == CHATTYPE_GROUP) {
            if (chatGroup != null) {
                List<?> members = chatGroup.getMembers();
                chatDesc = String.format("%d 成员", members == null ? 0 : members.size());
            }
        } else if (chatType == CHATTYPE_CHATROOM) {
            if (chatRoom != null) {
                List<?> members = chatRoom.getMembers();
                chatDesc = String.format("%d 成员", members == null ? 0 : members.size());
            }
        }

        if (TextUtils.isEmpty(chatDesc)) {
            onlineTextView.setText("");
        } else {
            onlineTextView.setText(chatDesc);
        }
    }

    private void updateBottomOverlay() {
//		if (currentUser == null) {
//			bottomOverlayChatText.setText(LocaleController.getString("DeleteThisGroup", com.yunuo.im.R.string.DeleteThisGroup));
//		} else {
//			if (userBlocked) {
//				bottomOverlayChatText.setText(LocaleController.getString("Unblock", com.yunuo.im.R.string.Unblock));
//			} else {
//				bottomOverlayChatText.setText(LocaleController.getString("DeleteThisChat", com.yunuo.im.R.string.DeleteThisChat));
//			}
//		}
//		if (currentChat != null && (currentChat instanceof TLRPC.TL_chatForbidden || currentChat.left) ||
//				currentUser != null && (currentUser instanceof TLRPC.TL_userDeleted || currentUser instanceof TLRPC.TL_userEmpty || userBlocked)) {
//			bottomOverlayChat.setVisibility(View.VISIBLE);
//			muteItem.setVisibility(View.GONE);
//			chatActivityEnterView.setFieldFocused(false);
//		} else {
//			muteItem.setVisibility(View.VISIBLE);
//			bottomOverlayChat.setVisibility(View.INVISIBLE);
//		}

        bottomOverlayChat.setVisibility(View.INVISIBLE);
    }


    private void updateEmpty(boolean loading) {
        if (loading && messages.isEmpty()) {
            progressView.setVisibility(View.VISIBLE);
            emptyViewContainer.setVisibility(View.INVISIBLE);
            //chatListView.setEmptyView(null);
        } else {
            progressView.setVisibility(View.INVISIBLE);
            emptyViewContainer.setVisibility(messages.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            //chatListView.setEmptyView(emptyViewContainer);
        }
    }

    private void scrollToLastMessage() {
        chatLayoutManager.scrollToPositionWithOffset(messages.size() - 1, -100000 - chatListView.getPaddingTop());
    }


    @Override
    public void onBackPressed() {
        ActionBar actionBar = getMyActionBar();
        if (actionBar.isActionModeShowed()) {
            actionBar.hideActionMode();
            return;
        } else if (chatActivityEnterView.isEmojiShowing()) {
            chatActivityEnterView.hidePopup();
            return;
        } else if (chatActivityEnterView.isVoicePopupShowing()) {
            chatActivityEnterView.hideVoicePopup();
            return;
        } else if (MessagePhotoViewer.getInstance().isVisible()) {
            MessagePhotoViewer.getInstance().closePhoto(true, false);
            return;
        }
        finish();
    }

    //    /**
//     * initView
//     */
//    protected void initView() {
//        recordingContainer = findViewById(R.id.recording_container);
//        micImage = (ImageView) findViewById(R.id.mic_image);
//        recordingHint = (TextView) findViewById(R.id.recording_hint);
//        listView = (ListView) findViewById(R.id.list);
//        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
//        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
//        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
//        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
//        buttonSend = findViewById(R.id.btn_send);
//        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
//        expressionViewpager = (ViewPager) findViewById(R.id.vPager);
//        emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
//        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
//        locationImgview = (ImageView) findViewById(R.id.btn_location);
//        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
//        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
//        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
//        btnMore = (Button) findViewById(R.id.btn_more);
//        iv_emoticons_normal.setVisibility(View.VISIBLE);
//        iv_emoticons_checked.setVisibility(View.INVISIBLE);
//        more = findViewById(R.id.more);
//        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
//        voiceCallBtn = (ImageView) findViewById(R.id.btn_voice_call);
//        videoCallBtn = (ImageView) findViewById(R.id.btn_video_call);
//
//        // 动画资源文件,用于录制语音时
//        micImages = new Drawable[]{getResources().getDrawable(R.drawable.record_animate_01),
//                getResources().getDrawable(R.drawable.record_animate_02),
//                getResources().getDrawable(R.drawable.record_animate_03),
//                getResources().getDrawable(R.drawable.record_animate_04),
//                getResources().getDrawable(R.drawable.record_animate_05),
//                getResources().getDrawable(R.drawable.record_animate_06),
//                getResources().getDrawable(R.drawable.record_animate_07),
//                getResources().getDrawable(R.drawable.record_animate_08),
//                getResources().getDrawable(R.drawable.record_animate_09),
//                getResources().getDrawable(R.drawable.record_animate_10),
//                getResources().getDrawable(R.drawable.record_animate_11),
//                getResources().getDrawable(R.drawable.record_animate_12),
//                getResources().getDrawable(R.drawable.record_animate_13),
//                getResources().getDrawable(R.drawable.record_animate_14),};
//
//        // 表情list
//        reslist = getExpressionRes(35);
//        // 初始化表情viewpager
//        List<View> views = new ArrayList<View>();
//        View gv1 = getGridChildView(1);
//        View gv2 = getGridChildView(2);
//        views.add(gv1);
//        views.add(gv2);
//        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
//        edittext_layout.requestFocus();
//        voiceRecorder = new VoiceRecorder(micImageHandler);
//        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
//        mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
//                } else {
//                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
//                }
//
//            }
//        });
//        mEditTextContent.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
//                more.setVisibility(View.GONE);
//                iv_emoticons_normal.setVisibility(View.VISIBLE);
//                iv_emoticons_checked.setVisibility(View.INVISIBLE);
//                emojiIconContainer.setVisibility(View.GONE);
//                btnContainer.setVisibility(View.GONE);
//            }
//        });
//        // 监听文字框
//        mEditTextContent.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!TextUtils.isEmpty(s)) {
//                    btnMore.setVisibility(View.GONE);
//                    buttonSend.setVisibility(View.VISIBLE);
//                } else {
//                    btnMore.setVisibility(View.VISIBLE);
//                    buttonSend.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_layout);
//
//        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
//                android.R.color.holo_orange_light, android.R.color.holo_red_light);
//
//        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
//                            List<EMMessage> messages;
//                            try {
//                                if (chatType == CHATTYPE_SINGLE) {
//                                    messages = conversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
//                                } else {
//                                    messages = conversation.loadMoreGroupMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
//                                }
//                            } catch (Exception e1) {
//                                swipeRefreshLayout.setRefreshing(false);
//                                return;
//                            }
//
//                            if (messages.size() > 0) {
//                                adapter.notifyDataSetChanged();
//                                adapter.refreshSeekTo(messages.size() - 1);
//                                if (messages.size() != pagesize) {
//                                    haveMoreData = false;
//                                }
//                            } else {
//                                haveMoreData = false;
//                            }
//
//                            isloading = false;
//
//                        } else {
//                            Toast.makeText(ChatActivity.this, getResources().getString(R.string.no_more_messages), Toast.LENGTH_SHORT).show();
//                        }
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 1000);
//            }
//        });
//    }
//
//    private void setUpView() {
//        iv_emoticons_normal.setOnClickListener(this);
//        iv_emoticons_checked.setOnClickListener(this);
//        // position = getIntent().getIntExtra("position", -1);
//        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
//                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
//        // 判断单聊还是群聊
//        chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);
//
//        if (chatType == CHATTYPE_SINGLE) { // 单聊
//            toChatUsername = getIntent().getStringExtra("userId");
//            ((TextView) findViewById(R.id.name)).setText(toChatUsername);
//        } else {
//            // 群聊
//            findViewById(R.id.container_to_group).setVisibility(View.VISIBLE);
//            findViewById(R.id.container_remove).setVisibility(View.GONE);
//            findViewById(R.id.container_voice_call).setVisibility(View.GONE);
//            findViewById(R.id.container_video_call).setVisibility(View.GONE);
//            toChatUsername = getIntent().getStringExtra("groupId");
//
//            if (chatType == CHATTYPE_GROUP) {
//                onGroupViewCreation();
//            } else {
//                onChatRoomViewCreation();
//            }
//        }
//
//        // for chatroom type, we only init conversation and create view adapter on success
//        if (chatType != CHATTYPE_CHATROOM) {
//            onConversationInit();
//
//            onListViewCreation();
//
//            // show forward message if the message is not null
//            String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
//            if (forward_msg_id != null) {
//                // 显示发送要转发的消息
//                forwardMessage(forward_msg_id);
//            }
//        }
//    }
//
//    protected void onConversationInit() {
//        if (chatType == CHATTYPE_SINGLE) {
//            conversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversationType.Chat);
//        } else if (chatType == CHATTYPE_GROUP) {
//            conversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversationType.GroupChat);
//        } else if (chatType == CHATTYPE_CHATROOM) {
//            conversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversationType.ChatRoom);
//        }
//
//        // 把此会话的未读数置为0
//        conversation.markAllMessagesAsRead();
//
//        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
//        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
//        final List<EMMessage> msgs = conversation.getAllMessages();
//        int msgCount = msgs != null ? msgs.size() : 0;
//        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
//            String msgId = null;
//            if (msgs != null && msgs.size() > 0) {
//                msgId = msgs.get(0).getMsgId();
//            }
//            if (chatType == CHATTYPE_SINGLE) {
//                conversation.loadMoreMsgFromDB(msgId, pagesize);
//            } else {
//                conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
//            }
//        }
//
//        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {
//
//            @Override
//            public void onChatRoomDestroyed(String roomId, String roomName) {
//                if (roomId.equals(toChatUsername)) {
//                    finish();
//                }
//            }
//
//            @Override
//            public void onMemberJoined(String roomId, String participant) {
//            }
//
//            @Override
//            public void onMemberExited(String roomId, String roomName,
//                                       String participant) {
//
//            }
//
//            @Override
//            public void onMemberKicked(String roomId, String roomName,
//                                       String participant) {
//                if (roomId.equals(toChatUsername)) {
//                    String curUser = EMChatManager.getInstance().getCurrentUser();
//                    if (curUser.equals(participant)) {
//                        EMChatManager.getInstance().leaveChatRoom(toChatUsername);
//                        finish();
//                    }
//                }
//            }
//
//        });
//    }
//
//    protected void onListViewCreation() {
//        adapter = new MessageAdapter(ChatActivity.this, toChatUsername, chatType);
//        // 显示消息
//        listView.setAdapter(adapter);
//
//        listView.setOnScrollListener(new ListScrollListener());
//        adapter.refreshSelectLast();
//
//        listView.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                hideKeyboard();
//                more.setVisibility(View.GONE);
//                iv_emoticons_normal.setVisibility(View.VISIBLE);
//                iv_emoticons_checked.setVisibility(View.INVISIBLE);
//                emojiIconContainer.setVisibility(View.GONE);
//                btnContainer.setVisibility(View.GONE);
//                return false;
//            }
//        });
//    }
//
//    protected void onGroupViewCreation() {
//        chatGroup = EMGroupManager.getInstance().getGroup(toChatUsername);
//
//        if (chatGroup != null) {
//            ((TextView) findViewById(R.id.name)).setText(chatGroup.getGroupName());
//        } else {
//            ((TextView) findViewById(R.id.name)).setText(toChatUsername);
//        }
//
//        // 监听当前会话的群聊解散被T事件
//        groupListener = new GroupListener();
//        EMGroupManager.getInstance().addGroupChangeListener(groupListener);
//    }
//
//    protected void onChatRoomViewCreation() {
//        findViewById(R.id.container_to_group).setVisibility(View.GONE);
//
//        final ProgressDialog pd = ProgressDialog.show(this, "", "Joining......");
//        EMChatManager.getInstance().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {
//
//            @Override
//            public void onSuccess(EMChatRoom value) {
//                // TODO Auto-generated method stub
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        pd.dismiss();
//                        chatRoom = EMChatManager.getInstance().getChatRoom(toChatUsername);
//                        if (chatRoom != null) {
//                            ((TextView) findViewById(R.id.name)).setText(chatRoom.getName());
//                        } else {
//                            ((TextView) findViewById(R.id.name)).setText(toChatUsername);
//                        }
//                        EMLog.d(TAG, "join chatRoom success : " + chatRoom.getName());
//
//                        onConversationInit();
//
//                        onListViewCreation();
//                    }
//                });
//            }
//
//            @Override
//            public void onError(final int error, String errorMsg) {
//                // TODO Auto-generated method stub
//                EMLog.d(TAG, "join chatRoom failure : " + error);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        pd.dismiss();
//                    }
//                });
//                finish();
//            }
//        });
//    }
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (resultCode == RESULT_OK) {
//                double latitude = data.getDoubleExtra(LocationActivity.RESULT_KEY_LAT, 0);
//                double longitude = data.getDoubleExtra(LocationActivity.RESULT_KEY_LON, 0);
//                String name = data.getStringExtra(LocationActivity.RESULT_KEY_NAME);
//                String address = data.getStringExtra(LocationActivity.RESULT_KEY_ADDRESS);
//                String location;
//                if (TextUtils.isEmpty(name)) {
//                    location = address;
//                } else {
//                    location = String.format("%s(%s)", name, address);
//                }
//                if (!TextUtils.isEmpty(location)) {
//                    sendLocationMsg(latitude, longitude, location);
//                } else {
//                    Toast.makeText(IMChatActivity.this, "定位地址为空,不能发送!", Toast.LENGTH_SHORT).show();
//                }
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
//                PhotoViewer.getInstance().setParentActivity(this);
//                final ArrayList<Object> arrayList = new ArrayList<>();
//                int orientation = 0;
//                try {
//                    ExifInterface ei = new ExifInterface(currentPicturePath);
//                    int exif = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                    switch (exif) {
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//                            orientation = 90;
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            orientation = 180;
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//                            orientation = 270;
//                            break;
//                    }
//                } catch (Exception e) {
//                    FileLog.e("tmessages", e);
//                }
//                arrayList.add(new MediaController.PhotoEntry(0, 0, 0, currentPicturePath, orientation, false));
//
//                PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 2, new PhotoViewer.EmptyPhotoViewerProvider() {
//                    @Override
//                    public void sendButtonPressed(int index) {
//                        MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(0);
//                        if (photoEntry.imagePath != null) {
//                            sendMessageForPhotoPath(photoEntry.imagePath);
//                        } else if (photoEntry.path != null) {
//                            sendMessageForPhotoPath(photoEntry.path);
//                        }
//                    }
//                }, null);
//                AndroidUtilities.addMediaToGallery(currentPicturePath);
//                currentPicturePath = null;
            }
        }
    }


    /**
     * 发送位置信息
     *
     * @param latitude
     * @param longitude
     * @param locationAddress
     */
    private void sendLocationMsg(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        } else if (chatType == CHATTYPE_CHATROOM) {
            message.setChatType(EMMessage.ChatType.ChatRoom);
        }
        LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
        message.addBody(locBody);
        message.setReceipt(chatId);
        conversation.addMessage(message);
        refreshSelectLast();
    }

//    /**
//     * onActivityResult
//     */
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_CODE_EXIT_GROUP) {
//            setResult(RESULT_OK);
//            finish();
//            return;
//        }
//        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
//            switch (resultCode) {
//                case RESULT_CODE_COPY: // 复制消息
//                    EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
//                    // clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
//                    // ((TextMessageBody) copyMsg.getBody()).getMessage()));
//                    clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
//                    break;
//                case RESULT_CODE_DELETE: // 删除消息
//                    EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
//                    conversation.removeMessage(deleteMsg.getMsgId());
//                    adapter.refreshSeekTo(data.getIntExtra("position", adapter.getCount()) - 1);
//                    break;
//
//                case RESULT_CODE_FORWARD: // 转发消息
//                    EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
//                    Intent intent = new Intent(this, ForwardMessageActivity.class);
//                    intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
//                    startActivity(intent);
//
//                    break;
//
//                default:
//                    break;
//            }
//        }
//        if (resultCode == RESULT_OK) { // 清空消息
//            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
//                // 清空会话
//                EMChatManager.getInstance().clearConversation(toChatUsername);
//                adapter.refresh();
//            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
//                if (cameraFile != null && cameraFile.exists())
//                    sendPicture(cameraFile.getAbsolutePath());
//            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频
//
//                int duration = data.getIntExtra("dur", 0);
//                String videoPath = data.getStringExtra("path");
//                File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
//                Bitmap bitmap = null;
//                FileOutputStream fos = null;
//                try {
//                    if (!file.getParentFile().exists()) {
//                        file.getParentFile().mkdirs();
//                    }
//                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
//                    if (bitmap == null) {
//                        EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
//                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
//                    }
//                    fos = new FileOutputStream(file);
//
//                    bitmap.compress(CompressFormat.JPEG, 100, fos);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (fos != null) {
//                        try {
//                            fos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        fos = null;
//                    }
//                    if (bitmap != null) {
//                        bitmap.recycle();
//                        bitmap = null;
//                    }
//
//                }
//                sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);
//
//            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
//                if (data != null) {
//                    Uri selectedImage = data.getData();
//                    if (selectedImage != null) {
//                        sendPicByUri(selectedImage);
//                    }
//                }
//            } else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
//                if (data != null) {
//                    Uri uri = data.getData();
//                    if (uri != null) {
//                        sendFile(uri);
//                    }
//                }
//
//            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
//                double latitude = data.getDoubleExtra("latitude", 0);
//                double longitude = data.getDoubleExtra("longitude", 0);
//                String locationAddress = data.getStringExtra("address");
//                if (locationAddress != null && !locationAddress.equals("")) {
//                    toggleMore(more);
//                    sendLocationMsg(latitude, longitude, "", locationAddress);
//                } else {
//                    String st = getResources().getString(R.string.unable_to_get_loaction);
//                    Toast.makeText(this, st, 0).show();
//                }
//                // 重发消息
//            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
//                    || requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION
//                    || requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
//                resendMessage();
//            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
//                // 粘贴
//                if (!TextUtils.isEmpty(clipboard.getText())) {
//                    String pasteText = clipboard.getText().toString();
//                    if (pasteText.startsWith(COPY_IMAGE)) {
//                        // 把图片前缀去掉，还原成正常的path
//                        sendPicture(pasteText.replace(COPY_IMAGE, ""));
//                    }
//
//                }
//            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
//                EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
//                addUserToBlacklist(deleteMsg.getFrom());
//            } else if (conversation.getMsgCount() > 0) {
//                adapter.refresh();
//                setResult(RESULT_OK);
//            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
//                adapter.refresh();
//            }
//        }
//    }
//
//    /**
//     * 消息图标点击事件
//     *
//     * @param view
//     */
//    @Override
//    public void onClick(View view) {
//        String st1 = getResources().getString(R.string.not_connect_to_server);
//        int id = view.getId();
//        if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
//            String s = mEditTextContent.getText().toString();
//            sendMessageForText(s);
//        } else if (id == R.id.btn_take_picture) {
//            selectPicFromCamera();// 点击照相图标
//        } else if (id == R.id.btn_picture) {
//            selectPicFromLocal(); // 点击图片图标
//        } else if (id == R.id.btn_location) { // 位置
//            startActivityForResult(new Intent(this, BaiduMapActivity.class), REQUEST_CODE_MAP);
//        } else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
//            more.setVisibility(View.VISIBLE);
//            iv_emoticons_normal.setVisibility(View.INVISIBLE);
//            iv_emoticons_checked.setVisibility(View.VISIBLE);
//            btnContainer.setVisibility(View.GONE);
//            emojiIconContainer.setVisibility(View.VISIBLE);
//            hideKeyboard();
//        } else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
//            iv_emoticons_normal.setVisibility(View.VISIBLE);
//            iv_emoticons_checked.setVisibility(View.INVISIBLE);
//            btnContainer.setVisibility(View.VISIBLE);
//            emojiIconContainer.setVisibility(View.GONE);
//            more.setVisibility(View.GONE);
//
//        } else if (id == R.id.btn_video) {
//            // 点击摄像图标
//            Intent intent = new Intent(ChatActivity.this, ImageGridActivity.class);
//            startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
//        } else if (id == R.id.btn_file) { // 点击文件图标
//            selectFileFromLocal();
//        } else if (id == R.id.btn_voice_call) { // 点击语音电话图标
//            if (!EMChatManager.getInstance().isConnected())
//                Toast.makeText(this, st1, 0).show();
//            else {
//                startActivity(new Intent(ChatActivity.this, VoiceCallActivity.class).putExtra("username",
//                        toChatUsername).putExtra("isComingCall", false));
//                voiceCallBtn.setEnabled(false);
//                toggleMore(null);
//            }
//        } else if (id == R.id.btn_video_call) { // 视频通话
//            if (!EMChatManager.getInstance().isConnected())
//                Toast.makeText(this, st1, 0).show();
//            else {
//                startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", toChatUsername).putExtra(
//                        "isComingCall", false));
//                videoCallBtn.setEnabled(false);
//                toggleMore(null);
//            }
//        }
//    }
//
//    /**
//     * 事件监听
//     *
//     * see {@link EMNotifierEvent}
//     */
//    @Override
//    public void onEvent(EMNotifierEvent event) {
//        switch (event.getEvent()) {
//            case EventNewMessage: {
//                //获取到message
//                EMMessage message = (EMMessage) event.getData();
//
//                String username = null;
//                //群组消息
//                if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
//                    username = message.getTo();
//                } else {
//                    //单聊消息
//                    username = message.getFrom();
//                }
//
//                //如果是当前会话的消息，刷新聊天页面
//                if (username.equals(getToChatUsername())) {
//                    refreshUIWithNewMessage();
//                    //声音和震动提示有新消息
//                    HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
//                } else {
//                    //如果消息不是和当前聊天ID的消息
//                    HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
//                }
//
//                break;
//            }
//            case EventDeliveryAck: {
//                //获取到message
//                EMMessage message = (EMMessage) event.getData();
//                refreshUI();
//                break;
//            }
//            case EventReadAck: {
//                //获取到message
//                EMMessage message = (EMMessage) event.getData();
//                refreshUI();
//                break;
//            }
//            case EventOfflineMessage: {
//                //a list of offline messages
//                //List<EMMessage> offlineMessages = (List<EMMessage>) event.getData();
//                refreshUI();
//                break;
//            }
//            default:
//                break;
//        }
//
//    }
//
//
//    private void refreshUIWithNewMessage() {
//        if (adapter == null) {
//            return;
//        }
//
//        runOnUiThread(new Runnable() {
//            public void run() {
//                adapter.refreshSelectLast();
//            }
//        });
//    }
//
//    private void refreshUI() {
//        if (adapter == null) {
//            return;
//        }
//
//        runOnUiThread(new Runnable() {
//            public void run() {
//                adapter.refresh();
//            }
//        });
//    }
//
//    /**
//     * 照相获取图片
//     */
//    public void selectPicFromCamera() {
//        if (!CommonUtils.isExitsSdcard()) {
//            String st = getResources().getString(R.string.sd_card_does_not_exist);
//            Toast.makeText(getApplicationContext(), st, 0).show();
//            return;
//        }
//
//        cameraFile = new File(PathUtil.getInstance().getImagePath(), DemoApplication.getInstance().getUserName()
//                + System.currentTimeMillis() + ".jpg");
//        cameraFile.getParentFile().mkdirs();
//        startActivityForResult(
//                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
//                REQUEST_CODE_CAMERA);
//    }
//
//    /**
//     * 选择文件
//     */
//    private void selectFileFromLocal() {
//        Intent intent = null;
//        if (Build.VERSION.SDK_INT < 19) {
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("*/*");
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        } else {
//            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        }
//        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
//    }
//
//    /**
//     * 从图库获取图片
//     */
//    public void selectPicFromLocal() {
//        Intent intent;
//        if (Build.VERSION.SDK_INT < 19) {
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//
//        } else {
//            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        }
//        startActivityForResult(intent, REQUEST_CODE_LOCAL);
//    }
//
//    /**
//     * 发送文本消息
//     *
//     * @param content
//     *            message content
//     * @param isResend
//     *            boolean resend
//     */
//    private void sendMessageForText(String content) {
//
//        if (content.length() > 0) {
//            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
//            // 如果是群聊，设置chattype,默认是单聊
//            if (chatType == CHATTYPE_GROUP) {
//                message.setChatType(ChatType.GroupChat);
//            } else if (chatType == CHATTYPE_CHATROOM) {
//                message.setChatType(ChatType.ChatRoom);
//            }
//
//            TextMessageBody txtBody = new TextMessageBody(content);
//            // 设置消息body
//            message.addBody(txtBody);
//            // 设置要发给谁,用户username或者群聊groupid
//            message.setReceipt(toChatUsername);
//            // 把messgage加到conversation中
//            conversation.addMessage(message);
//            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
//            adapter.refreshSelectLast();
//            mEditTextContent.setText("");
//
//            setResult(RESULT_OK);
//
//        }
//    }
//
//    /**
//     * 发送语音
//     *
//     * @param filePath
//     * @param fileName
//     * @param length
//     * @param isResend
//     */
//    private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
//        if (!(new File(filePath).exists())) {
//            return;
//        }
//        try {
//            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
//            // 如果是群聊，设置chattype,默认是单聊
//            if (chatType == CHATTYPE_GROUP) {
//                message.setChatType(ChatType.GroupChat);
//            } else if (chatType == CHATTYPE_CHATROOM) {
//                message.setChatType(ChatType.ChatRoom);
//            }
//            message.setReceipt(toChatUsername);
//            int len = Integer.parseInt(length);
//            VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
//            message.addBody(body);
//
//            conversation.addMessage(message);
//            adapter.refreshSelectLast();
//            setResult(RESULT_OK);
//            // send file
//            // sendVoiceSub(filePath, fileName, message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 发送图片
//     *
//     * @param filePath
//     */
//    private void sendPicture(final String filePath) {
//        String to = toChatUsername;
//        // create and add image message in view
//        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
//        // 如果是群聊，设置chattype,默认是单聊
//        if (chatType == CHATTYPE_GROUP) {
//            message.setChatType(ChatType.GroupChat);
//        } else if (chatType == CHATTYPE_CHATROOM) {
//            message.setChatType(ChatType.ChatRoom);
//        }
//
//        message.setReceipt(to);
//        ImageMessageBody body = new ImageMessageBody(new File(filePath));
//        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
//        // body.setSendOriginalImage(true);
//        message.addBody(body);
//        conversation.addMessage(message);
//
//        listView.setAdapter(adapter);
//        adapter.refreshSelectLast();
//        setResult(RESULT_OK);
//        // more(more);
//    }
//
//    /**
//     * 发送视频消息
//     */
//    private void sendVideo(final String filePath, final String thumbPath, final int length) {
//        final File videoFile = new File(filePath);
//        if (!videoFile.exists()) {
//            return;
//        }
//        try {
//            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
//            // 如果是群聊，设置chattype,默认是单聊
//            if (chatType == CHATTYPE_GROUP) {
//                message.setChatType(ChatType.GroupChat);
//            } else if (chatType == CHATTYPE_CHATROOM) {
//                message.setChatType(ChatType.ChatRoom);
//            }
//            String to = toChatUsername;
//            message.setReceipt(to);
//            VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
//            message.addBody(body);
//            conversation.addMessage(message);
//            listView.setAdapter(adapter);
//            adapter.refreshSelectLast();
//            setResult(RESULT_OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 根据图库图片uri发送图片
//     *
//     * @param selectedImage
//     */
//    private void sendPicByUri(Uri selectedImage) {
//        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
//        String st8 = getResources().getString(R.string.cant_find_pictures);
//        if (cursor != null) {
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex("_data");
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//            cursor = null;
//
//            if (picturePath == null || picturePath.equals("null")) {
//                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                return;
//            }
//            sendPicture(picturePath);
//        } else {
//            File file = new File(selectedImage.getPath());
//            if (!file.exists()) {
//                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                return;
//
//            }
//            sendPicture(file.getAbsolutePath());
//        }
//
//    }
//
//    /**
//     * 发送位置信息
//     *
//     * @param latitude
//     * @param longitude
//     * @param imagePath
//     * @param locationAddress
//     */
//    private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
//        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
//        // 如果是群聊，设置chattype,默认是单聊
//        if (chatType == CHATTYPE_GROUP) {
//            message.setChatType(ChatType.GroupChat);
//        } else if (chatType == CHATTYPE_CHATROOM) {
//            message.setChatType(ChatType.ChatRoom);
//        }
//        LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
//        message.addBody(locBody);
//        message.setReceipt(toChatUsername);
//        conversation.addMessage(message);
//        listView.setAdapter(adapter);
//        adapter.refreshSelectLast();
//        setResult(RESULT_OK);
//
//    }
//
//    /**
//     * 发送文件
//     *
//     * @param uri
//     */
//    private void sendFile(Uri uri) {
//        String filePath = null;
//        if ("content".equalsIgnoreCase(uri.getScheme())) {
//            String[] projection = {"_data"};
//            Cursor cursor = null;
//
//            try {
//                cursor = getContentResolver().query(uri, projection, null, null, null);
//                int column_index = cursor.getColumnIndexOrThrow("_data");
//                if (cursor.moveToFirst()) {
//                    filePath = cursor.getString(column_index);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            filePath = uri.getPath();
//        }
//        File file = new File(filePath);
//        if (file == null || !file.exists()) {
//            String st7 = getResources().getString(R.string.File_does_not_exist);
//            Toast.makeText(getApplicationContext(), st7, 0).show();
//            return;
//        }
//        if (file.length() > 10 * 1024 * 1024) {
//            String st6 = getResources().getString(R.string.The_file_is_not_greater_than_10_m);
//            Toast.makeText(getApplicationContext(), st6, 0).show();
//            return;
//        }
//
//        // 创建一个文件消息
//        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
//        // 如果是群聊，设置chattype,默认是单聊
//        if (chatType == CHATTYPE_GROUP) {
//            message.setChatType(ChatType.GroupChat);
//        } else if (chatType == CHATTYPE_CHATROOM) {
//            message.setChatType(ChatType.ChatRoom);
//        }
//
//        message.setReceipt(toChatUsername);
//        // add message body
//        NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
//        message.addBody(body);
//        conversation.addMessage(message);
//        listView.setAdapter(adapter);
//        adapter.refreshSelectLast();
//        setResult(RESULT_OK);
//    }
//
//    /**
//     * 重发消息
//     */
//    private void resendMessage() {
//        EMMessage msg = null;
//        msg = conversation.getMessage(resendPos);
//        // msg.setBackSend(true);
//        msg.status = EMMessage.Status.CREATE;
//
//        adapter.refreshSeekTo(resendPos);
//    }
//
//    /**
//     * 显示语音图标按钮
//     *
//     * @param view
//     */
//    public void setModeVoice(View view) {
//        hideKeyboard();
//        edittext_layout.setVisibility(View.GONE);
//        more.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
//        buttonSend.setVisibility(View.GONE);
//        btnMore.setVisibility(View.VISIBLE);
//        buttonPressToSpeak.setVisibility(View.VISIBLE);
//        iv_emoticons_normal.setVisibility(View.VISIBLE);
//        iv_emoticons_checked.setVisibility(View.INVISIBLE);
//        btnContainer.setVisibility(View.VISIBLE);
//        emojiIconContainer.setVisibility(View.GONE);
//
//    }
//
//    /**
//     * 显示键盘图标
//     *
//     * @param view
//     */
//    public void setModeKeyboard(View view) {
//        // mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
//        // {
//        // @Override
//        // public void onFocusChange(View v, boolean hasFocus) {
//        // if(hasFocus){
//        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        // }
//        // }
//        // });
//        edittext_layout.setVisibility(View.VISIBLE);
//        more.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//        buttonSetModeVoice.setVisibility(View.VISIBLE);
//        // mEditTextContent.setVisibility(View.VISIBLE);
//        mEditTextContent.requestFocus();
//        // buttonSend.setVisibility(View.VISIBLE);
//        buttonPressToSpeak.setVisibility(View.GONE);
//        if (TextUtils.isEmpty(mEditTextContent.getText())) {
//            btnMore.setVisibility(View.VISIBLE);
//            buttonSend.setVisibility(View.GONE);
//        } else {
//            btnMore.setVisibility(View.GONE);
//            buttonSend.setVisibility(View.VISIBLE);
//        }
//
//    }
//
//    /**
//     * 点击清空聊天记录
//     *
//     * @param view
//     */
//    public void emptyHistory(View view) {
//        String st5 = getResources().getString(R.string.Whether_to_empty_all_chats);
//        startActivityForResult(new Intent(this, AlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", st5)
//                .putExtra("cancel", true), REQUEST_CODE_EMPTY_HISTORY);
//    }
//
//    /**
//     * 点击进入群组详情
//     *
//     * @param view
//     */
//    public void toGroupDetails(View view) {
//        if (chatRoom == null && chatGroup == null) {
//            Toast.makeText(getApplicationContext(), R.string.gorup_not_found, 0).show();
//            return;
//        }
//        if (chatType == CHATTYPE_GROUP) {
//            startActivityForResult((new Intent(this, GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
//                    REQUEST_CODE_GROUP_DETAIL);
//        } else {
//            startActivityForResult((new Intent(this, ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername)),
//                    REQUEST_CODE_GROUP_DETAIL);
//        }
//    }
//
//    /**
//     * 显示或隐藏图标按钮页
//     *
//     * @param view
//     */
//    public void toggleMore(View view) {
//        if (more.getVisibility() == View.GONE) {
//            EMLog.d(TAG, "more gone");
//            hideKeyboard();
//            more.setVisibility(View.VISIBLE);
//            btnContainer.setVisibility(View.VISIBLE);
//            emojiIconContainer.setVisibility(View.GONE);
//        } else {
//            if (emojiIconContainer.getVisibility() == View.VISIBLE) {
//                emojiIconContainer.setVisibility(View.GONE);
//                btnContainer.setVisibility(View.VISIBLE);
//                iv_emoticons_normal.setVisibility(View.VISIBLE);
//                iv_emoticons_checked.setVisibility(View.INVISIBLE);
//            } else {
//                more.setVisibility(View.GONE);
//            }
//
//        }
//
//    }
//
//    /**
//     * 点击文字输入框
//     *
//     * @param v
//     */
//    public void editClick(View v) {
//        listView.setSelection(listView.getCount() - 1);
//        if (more.getVisibility() == View.VISIBLE) {
//            more.setVisibility(View.GONE);
//            iv_emoticons_normal.setVisibility(View.VISIBLE);
//            iv_emoticons_checked.setVisibility(View.INVISIBLE);
//        }
//
//    }
//
//    private PowerManager.WakeLock wakeLock;
//    private ImageView voiceCallBtn;
//    private ImageView videoCallBtn;
//
//    /**
//     * 按住说话listener
//     *
//     */
//    class PressToSpeakListen implements OnTouchListener {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    if (!CommonUtils.isExitsSdcard()) {
//                        String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
//                        Toast.makeText(ChatActivity.this, st4, Toast.LENGTH_SHORT).show();
//                        return false;
//                    }
//                    try {
//                        v.setPressed(true);
//                        wakeLock.acquire();
//                        if (VoicePlayClickListener.isPlaying)
//                            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
//                        recordingContainer.setVisibility(View.VISIBLE);
//                        recordingHint.setText(getString(R.string.move_up_to_cancel));
//                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
//                        voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        v.setPressed(false);
//                        if (wakeLock.isHeld())
//                            wakeLock.release();
//                        if (voiceRecorder != null)
//                            voiceRecorder.discardRecording();
//                        recordingContainer.setVisibility(View.INVISIBLE);
//                        Toast.makeText(ChatActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
//                        return false;
//                    }
//
//                    return true;
//                case MotionEvent.ACTION_MOVE: {
//                    if (event.getY() < 0) {
//                        recordingHint.setText(getString(R.string.release_to_cancel));
//                        recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
//                    } else {
//                        recordingHint.setText(getString(R.string.move_up_to_cancel));
//                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
//                    }
//                    return true;
//                }
//                case MotionEvent.ACTION_UP:
//                    v.setPressed(false);
//                    recordingContainer.setVisibility(View.INVISIBLE);
//                    if (wakeLock.isHeld())
//                        wakeLock.release();
//                    if (event.getY() < 0) {
//                        // discard the recorded audio.
//                        voiceRecorder.discardRecording();
//
//                    } else {
//                        // stop recording and send voice file
//                        String st1 = getResources().getString(R.string.Recording_without_permission);
//                        String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
//                        String st3 = getResources().getString(R.string.send_failure_please);
//                        try {
//                            int length = voiceRecorder.stopRecoding();
//                            if (length > 0) {
//                                sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
//                                        Integer.toString(length), false);
//                            } else if (length == EMError.INVALID_FILE) {
//                                Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Toast.makeText(ChatActivity.this, st3, Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                    return true;
//                default:
//                    recordingContainer.setVisibility(View.INVISIBLE);
//                    if (voiceRecorder != null)
//                        voiceRecorder.discardRecording();
//                    return false;
//            }
//        }
//    }
//
//    /**
//     * 获取表情的gridview的子view
//     *
//     * @param i
//     * @return
//     */
//    private View getGridChildView(int i) {
//        View view = View.inflate(this, R.layout.expression_gridview, null);
//        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
//        List<String> list = new ArrayList<String>();
//        if (i == 1) {
//            List<String> list1 = reslist.subList(0, 20);
//            list.addAll(list1);
//        } else if (i == 2) {
//            list.addAll(reslist.subList(20, reslist.size()));
//        }
//        list.add("delete_expression");
//        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
//        gv.setAdapter(expressionAdapter);
//        gv.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String filename = expressionAdapter.getItem(position);
//                try {
//                    // 文字输入框可见时，才可输入表情
//                    // 按住说话可见，不让输入表情
//                    if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {
//
//                        if (filename != "delete_expression") { // 不是删除键，显示表情
//                            // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
//                            Class clz = Class.forName("com.easemob.chatuidemo.utils.SmileUtils");
//                            Field field = clz.getField(filename);
//                            mEditTextContent.append(SmileUtils.getSmiledText(ChatActivity.this,
//                                    (String) field.get(null)));
//                        } else { // 删除文字或者表情
//                            if (!TextUtils.isEmpty(mEditTextContent.getText())) {
//
//                                int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
//                                if (selectionStart > 0) {
//                                    String body = mEditTextContent.getText().toString();
//                                    String tempStr = body.substring(0, selectionStart);
//                                    int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
//                                    if (i != -1) {
//                                        CharSequence cs = tempStr.substring(i, selectionStart);
//                                        if (SmileUtils.containsKey(cs.toString()))
//                                            mEditTextContent.getEditableText().delete(i, selectionStart);
//                                        else
//                                            mEditTextContent.getEditableText().delete(selectionStart - 1,
//                                                    selectionStart);
//                                    } else {
//                                        mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
//                                    }
//                                }
//                            }
//
//                        }
//                    }
//                } catch (Exception e) {
//                }
//
//            }
//        });
//        return view;
//    }
//
//    public List<String> getExpressionRes(int getSum) {
//        List<String> reslist = new ArrayList<String>();
//        for (int x = 1; x <= getSum; x++) {
//            String filename = "ee_" + x;
//
//            reslist.add(filename);
//
//        }
//        return reslist;
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        activityInstance = null;
//        if (groupListener != null) {
//            EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (chatGroup != null)
//            ((TextView) findViewById(R.id.name)).setText(chatGroup.getGroupName());
//        voiceCallBtn.setEnabled(true);
//        videoCallBtn.setEnabled(true);
//
//        if (adapter != null) {
//            adapter.refresh();
//        }
//
//        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
//        sdkHelper.pushActivity(this);
//        // register the event listener when enter the foreground
//        EMChatManager.getInstance().registerEventListener(
//                this,
//                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
//                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck});
//    }
//
//    @Override
//    protected void onStop() {
//        // unregister this event listener when this activity enters the
//        // background
//        EMChatManager.getInstance().unregisterEventListener(this);
//
//        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
//
//        // 把此activity 从foreground activity 列表里移除
//        sdkHelper.popActivity(this);
//
//        super.onStop();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (wakeLock.isHeld())
//            wakeLock.release();
//        if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
//            // 停止语音播放
//            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
//        }
//
//        try {
//            // 停止录音
//            if (voiceRecorder.isRecording()) {
//                voiceRecorder.discardRecording();
//                recordingContainer.setVisibility(View.INVISIBLE);
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * 隐藏软键盘
//     */
//    private void hideKeyboard() {
//        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
//            if (getCurrentFocus() != null)
//                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }
//
//    /**
//     * 加入到黑名单
//     *
//     * @param username
//     */
//    private void addUserToBlacklist(final String username) {
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage(getString(R.string.Is_moved_into_blacklist));
//        pd.setCanceledOnTouchOutside(false);
//        pd.show();
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    EMContactManager.getInstance().addUserToBlackList(username, false);
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            pd.dismiss();
//                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_success, 0).show();
//                        }
//                    });
//                } catch (EaseMobException e) {
//                    e.printStackTrace();
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            pd.dismiss();
//                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_failure, 0).show();
//                        }
//                    });
//                }
//            }
//        }).start();
//    }
//
//    /**
//     * 返回
//     *
//     * @param view
//     */
//    public void back(View view) {
//        EMChatManager.getInstance().unregisterEventListener(this);
//        if (chatType == CHATTYPE_CHATROOM) {
//            EMChatManager.getInstance().leaveChatRoom(toChatUsername);
//        }
//        finish();
//    }
//
//    /**
//     * 覆盖手机返回键
//     */
//    @Override
//    public void onBackPressed() {
//        if (more.getVisibility() == View.VISIBLE) {
//            more.setVisibility(View.GONE);
//            iv_emoticons_normal.setVisibility(View.VISIBLE);
//            iv_emoticons_checked.setVisibility(View.INVISIBLE);
//        } else {
//            super.onBackPressed();
//            if (chatType == CHATTYPE_CHATROOM) {
//                EMChatManager.getInstance().leaveChatRoom(toChatUsername);
//            }
//        }
//    }
//
//    /**
//     * listview滑动监听listener
//     *
//     */
//    private class ListScrollListener implements OnScrollListener {
//
//        @Override
//        public void onScrollStateChanged(AbsListView view, int scrollState) {
//            switch (scrollState) {
//                case OnScrollListener.SCROLL_STATE_IDLE:
//                /*if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData && conversation.getAllMessages().size() != 0) {
//					isloading = true;
//					loadmorePB.setVisibility(View.VISIBLE);
//					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
//					List<EMMessage> messages;
//					EMMessage firstMsg = conversation.getAllMessages().get(0);
//					try {
//						// 获取更多messges，调用此方法的时候从db获取的messages
//						// sdk会自动存入到此conversation中
//						if (chatType == CHATTYPE_SINGLE)
//							messages = conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), pagesize);
//						else
//							messages = conversation.loadMoreGroupMsgFromDB(firstMsg.getMsgId(), pagesize);
//					} catch (Exception e1) {
//						loadmorePB.setVisibility(View.GONE);
//						return;
//					}
//					try {
//						Thread.sleep(300);
//					} catch (InterruptedException e) {
//					}
//					if (messages.size() != 0) {
//						// 刷新ui
//						if (messages.size() > 0) {
//							adapter.refreshSeekTo(messages.size() - 1);
//						}
//
//						if (messages.size() != pagesize)
//							haveMoreData = false;
//					} else {
//						haveMoreData = false;
//					}
//					loadmorePB.setVisibility(View.GONE);
//					isloading = false;
//
//				}*/
//                    break;
//            }
//        }
//
//        @Override
//        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//        }
//
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        // 点击notification bar进入聊天页面，保证只有一个聊天页面
//        String username = intent.getStringExtra("userId");
//        if (toChatUsername.equals(username))
//            super.onNewIntent(intent);
//        else {
//            finish();
//            startActivity(intent);
//        }
//
//    }
//
//    /**
//     * 转发消息
//     *
//     * @param forward_msg_id
//     */
//    protected void forwardMessage(String forward_msg_id) {
//        final EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
//        EMMessage.Type type = forward_msg.getType();
//        switch (type) {
//            case TXT:
//                // 获取消息内容，发送消息
//                String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
//                sendMessageForText(content);
//                break;
//            case IMAGE:
//                // 发送图片
//                String filePath = ((ImageMessageBody) forward_msg.getBody()).getLocalUrl();
//                if (filePath != null) {
//                    File file = new File(filePath);
//                    if (!file.exists()) {
//                        // 不存在大图发送缩略图
//                        filePath = ImageUtils.getThumbnailImagePath(filePath);
//                    }
//                    sendPicture(filePath);
//                }
//                break;
//            default:
//                break;
//        }
//
//        if (forward_msg.getChatType() == ChatType.ChatRoom) {
//            EMChatManager.getInstance().leaveChatRoom(forward_msg.getTo());
//        }
//    }
//
//    /**
//     * 监测群组解散或者被T事件
//     *
//     */
//    class GroupListener extends GroupRemoveListener {
//
//        @Override
//        public void onUserRemoved(final String groupId, String groupName) {
//            runOnUiThread(new Runnable() {
//                String st13 = getResources().getString(R.string.you_are_group);
//
//                public void run() {
//                    if (toChatUsername.equals(groupId)) {
//                        Toast.makeText(ChatActivity.this, st13, 1).show();
//                        if (GroupDetailsActivity.instance != null)
//                            GroupDetailsActivity.instance.finish();
//                        finish();
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onGroupDestroy(final String groupId, String groupName) {
//            // 群组解散正好在此页面，提示群组被解散，并finish此页面
//            runOnUiThread(new Runnable() {
//                String st14 = getResources().getString(R.string.the_current_group);
//
//                public void run() {
//                    if (toChatUsername.equals(groupId)) {
//                        Toast.makeText(ChatActivity.this, st14, 1).show();
//                        if (GroupDetailsActivity.instance != null)
//                            GroupDetailsActivity.instance.finish();
//                        finish();
//                    }
//                }
//            });
//        }
//
//    }
//
//    public String getToChatUsername() {
//        return toChatUsername;
//    }
//
//    public ListView getListView() {
//        return listView;
//    }


    private void sendMessageForText(String content) {

        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP) {
                message.setChatType(EMMessage.ChatType.GroupChat);
            } else if (chatType == CHATTYPE_CHATROOM) {
                message.setChatType(EMMessage.ChatType.ChatRoom);
            }

            TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(chatId);
            // 把messgage加到conversation中
            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            refreshSelectLast();

        }
    }

    private void sendMessageForPhotoUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast.makeText(IMChatActivity.this, "图片路径不正确", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessageForPhotoPath(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast.makeText(IMChatActivity.this, "图片路径不正确", Toast.LENGTH_SHORT).show();
                return;

            }
            sendMessageForPhotoPath(file.getAbsolutePath());
        }
    }

    private void sendMessageForPhotoPath(String filePath) {
        String to = chatId;
        // create and add image message in view
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        } else if (chatType == CHATTYPE_CHATROOM) {
            message.setChatType(EMMessage.ChatType.ChatRoom);
        }

        message.setReceipt(to);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        body.setSendOriginalImage(true);
        message.addBody(body);
        conversation.addMessage(message);
        refreshSelectLast();
    }

    private class ChatAdapter extends RecyclerView.Adapter {

        private Context mContext;

        public ChatAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            int count = messages.size();
            return count;
        }

        public MessageObject getItem(int i) {
            return messages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        private class Holder extends RecyclerView.ViewHolder {

            public Holder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == MessageObject.MESSAGE_TYPE_RECV_VOICE) {
                view = new ChatVoiceMessageForFromCell(parent.getContext());
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            } else if (viewType == MessageObject.MESSAGE_TYPE_SENT_VOICE) {
                view = new ChatVoiceMessageForToCell(parent.getContext());
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            } else if (viewType == MessageObject.MESSAGE_TYPE_SENT_LOCATION) {
                view = new ChatLocationMessageForToCell(mContext);
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            } else if (viewType == MessageObject.MESSAGE_TYPE_RECV_LOCATION) {
                view = new ChatLocationMessageForFromCell(mContext);
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            } else if (viewType == MessageObject.MESSAGE_TYPE_SENT_IMAGE ||
                    viewType == MessageObject.MESSAGE_TYPE_SENT_VIDEO) {
                view = new ChatImageForToCell(parent.getContext());
                view.setClickable(true);
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            } else if (viewType == MessageObject.MESSAGE_TYPE_RECV_IMAGE ||
                    viewType == MessageObject.MESSAGE_TYPE_RECV_VIDEO) {
                view = new ChatImageForFromCell(parent.getContext());
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            } else if (viewType == MessageObject.MESSAGE_TYPE_RECV_TXT) {
                view = new ChatTextForFromCell(parent.getContext());
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            } else if (viewType == MessageObject.MESSAGE_TYPE_SENT_TXT) {
                view = new ChatTextForToCell(parent.getContext());
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            } else {
                view = new ChatTextForFromCell(parent.getContext());
                view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            }
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            View view = holder.itemView;
            final MessageObject message = getItem(position);
            if (view instanceof ChatCell) {
                final ChatCell cell = (ChatCell) view;
                cell.setValue(message);
                boolean isShowAvatar = chatType != CHATTYPE_SINGLE;
                boolean isNext = position > 0;
                if (isNext) {
                    MessageObject preMessage = getItem(position - 1);
                    if (preMessage.isOut() && message.isOut()) {
                        isNext = true;
                    } else if ((!preMessage.isOut()) && (!message.isOut())) {
                        isNext = TextUtils.equals(preMessage.messageOwner.getFrom(), message.messageOwner.getFrom());
                    } else {
                        isNext = false;
                    }
                }
                cell.updateCellState(isNext, isShowAvatar);
                if (message.isOut() && message.isUnSend()) {
                    cell.tryHandleMessageForCreate(message);
                }

                cell.setChatDelegate(new ChatCell.ChatDelegate() {
                    @Override
                    public void onCellClick(ChatCell cell) {
                        Toast.makeText(IMChatActivity.this, "aaaa", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCellLongClick(ChatCell cell) {
                        Toast.makeText(IMChatActivity.this, "bbbb", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAvatarClick(UserEntity userEntity) {
                    }

                    @Override
                    public void needRefreshAdapter(ChatCell cell) {

                    }
                });
                if (view instanceof ChatImageCell) {
                    ((ChatImageCell) view).setChatImageDelegate(new ChatImageCell.ChatImageDelegate() {
                        @Override
                        public void onImageClick(ChatImageCell cell) {
                            MessageObject message = cell.getMessageObject();
                            if (cell.isImageMessage(message)) {
                                MessagePhotoViewer.getInstance().setParentActivity(IMChatActivity.this);
                                MessagePhotoViewer.getInstance().openPhoto(message, photoViewerProvider);
                            } else if (cell.isVideoMessage(message)) {
//                                try {
//                                    File f = null;
//                                    if (message.messageOwner.attachPath != null && message.messageOwner.attachPath.length() != 0) {
//                                        f = new File(message.messageOwner.attachPath);
//                                    }
//                                    if (f == null || f != null && !f.exists()) {
//                                        f = FileLoader.getPathToMessage(message.messageOwner);
//                                    }
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setDataAndType(Uri.fromFile(f), "video/mp4");
//                                    startActivityForResult(intent, 500);
//                                } catch (Exception e) {
//                                    alertUserOpenError("无法播放该视频");
//                                }
                            }
                        }

                        @Override
                        public void onImageLongClick(ChatImageCell cell) {
                            Toast.makeText(IMChatActivity.this, "cccc", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (view instanceof ChatLocationMessageCell) {
                    ((ChatLocationMessageCell) view).setChatLocationDelegate(new ChatLocationMessageCell.ChatLocationDelegate() {
                        @Override
                        public void onLocationClick(ChatLocationMessageCell cell) {
                            MessageObject object = cell.getMessageObject();
                            LocationMessageBody locBody = (LocationMessageBody) object.messageOwner.getBody();
                            String locationAddress = locBody.getAddress();
                            double locationLat = locBody.getLatitude();
                            double locationLon = locBody.getLongitude();
//                            Intent intent = new Intent(IMChatActivity.this, LocationActivity.class);
//                            Bundle arguments = new Bundle();
//                            arguments.putBoolean(LocationActivity.ARGUMENTS_KEY_READONLY, true);
//                            arguments.putDouble(LocationActivity.ARGUMENTS_KEY_READ_LOCATION_LAT, locationLat);
//                            arguments.putDouble(LocationActivity.ARGUMENTS_KEY_READ_LOCATION_LON, locationLon);
//                            arguments.putString(LocationActivity.ARGUMENTS_KEY_READ_LOCATION_ADDRESS, locationAddress);
//                            String userId = object.messageOwner.getFrom();
//                            arguments.putString(LocationActivity.ARGUMENTS_KEY_READ_USER_ID, userId);
//                            intent.putExtras(arguments);
//                            startActivity(intent);
                        }
                    });
                }

            }
        }

        @Override
        public int getItemViewType(int position) {
            MessageObject message = getItem(position);
            return message.contentType;
        }

        public boolean isEmpty() {
            int count = messages.size();
            return count == 0;
        }
    }

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;

    private Handler handler = new Handler() {
        private void refreshList() {
            // UI线程不能直接使用conversation.getAllMessages()
            // 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
            messages.clear();
            List<EMMessage> historyMessages = conversation.getAllMessages();
            for (int i = 0; i < historyMessages.size(); i++) {
                // getMessage will set message as read status
                conversation.getMessage(i);
                messages.add(new MessageObject(historyMessages.get(i), true));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateAdapter();
                }
            });
        }

        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    refreshList();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (chatListView != null) {
                        if (messages.size() > 0) {
                            scrollToLastMessage();
                        }
                    }
                default:
                    break;
            }
        }
    };

    /**
     * 刷新页面, 选择最后一个
     */
    public void refreshSelectLast() {
        android.os.Message.obtain(handler, HANDLER_MESSAGE_REFRESH_LIST).sendToTarget();
        android.os.Message.obtain(handler, HANDLER_MESSAGE_SELECT_LAST).sendToTarget();
//        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
//        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
    }

    public void refreshWithLoadMessages() {
        android.os.Message.obtain(handler, HANDLER_MESSAGE_REFRESH_LIST).sendToTarget();
        //handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
    }


    @Override
    protected void onDestroy() {

        if (chatAttachView != null) {
            chatAttachView.onDestroy();
        }
        if (groupListener != null) {
            EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesRead);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.openedChatChanged);
        MessagePhotoViewer.getInstance().destroyPhotoViewer();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        chatActivityEnterView.onResume();

        if (chatAdapter != null) {
            refreshWithLoadMessages();
        }
        IMHXSDKHelper sdkHelper = (IMHXSDKHelper) IMHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                emEventListener,
                new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck,
                        EMNotifierEvent.Event.EventReadAck});
    }

    @Override
    protected void onStop() {
        // unregister this event listener when this activity enters the
        // background
        EMChatManager.getInstance().unregisterEventListener(emEventListener);
        IMHXSDKHelper sdkHelper = (IMHXSDKHelper) IMHXSDKHelper.getInstance();
        // 把此activity 从foreground activity 列表里移除
        sdkHelper.popActivity(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onPause();
//            String text = chatActivityEnterView.getFieldText();
//            if (text != null) {
//                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("dialog_" + dialog_id, text);
//                editor.commit();
//            }
            chatActivityEnterView.setFieldFocused(false);
        }
        if (chatAttachViewSheet != null) {
            chatAttachViewSheet.dismiss();
        }
        VoicePlayController.getInstance().onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String targetChatId = intent.getStringExtra(ARGUMENT_CHAT_ID);
        if (TextUtils.equals(chatId, targetChatId))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    private final EMEventListener emEventListener = new EMEventListener() {

        @Override
        public void onEvent(EMNotifierEvent event) {
            switch (event.getEvent()) {
                case EventNewMessage: {
                    //获取到message
                    EMMessage message = (EMMessage) event.getData();

                    String username = null;
                    //群组消息
                    if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                        username = message.getTo();
                    } else {
                        //单聊消息
                        username = message.getFrom();
                    }

                    //如果是当前会话的消息，刷新聊天页面
                    if (username.equals(chatId)) {
                        refreshUIWithNewMessage();
                        //声音和震动提示有新消息
                        IMHXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                    } else {
                        //如果消息不是和当前聊天ID的消息
                        IMHXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                    }

                    break;
                }
                case EventDeliveryAck: {
                    //获取到message
                    EMMessage message = (EMMessage) event.getData();
                    refreshUI();
                    break;
                }
                case EventReadAck: {
                    //获取到message
                    EMMessage message = (EMMessage) event.getData();
                    refreshUI();
                    break;
                }
                case EventOfflineMessage: {
                    //a list of offline messages
                    //List<EMMessage> offlineMessages = (List<EMMessage>) event.getData();
                    refreshUI();
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void updateAdapter() {
        chatAdapter.notifyDataSetChanged();
        updateEmpty(false);
    }


    private String currentPicturePath;

    private void processSelectedAttach(int which) {
        chatAttachViewSheet.dismiss();
        if (which == attach_location) {
            Intent intent = new Intent(IMChatActivity.this, LocationActivity.class);
            Bundle arguments = new Bundle();
            //arguments.putBoolean(LocationActivity.ARGUMENTS_KEY_IS_RETURN, true);
            intent.putExtras(arguments);
            startActivityForResult(intent, REQUEST_CODE_LOCATION);
            return;
        } else if (which == attach_photo) {
            try {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File image = AndroidUtilities.generatePicturePath();
                if (image != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                    currentPicturePath = image.getAbsolutePath();
                }
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }
//        if (which == attach_photo || which == attach_gallery || which == attach_document || which == attach_video) {
//            String action;
//            if (currentChat != null) {
//                if (currentChat.participants_count > MessagesController.getInstance().groupBigSize) {
//                    if (which == attach_photo || which == attach_gallery) {
//                        action = "bigchat_upload_photo";
//                    } else {
//                        action = "bigchat_upload_document";
//                    }
//                } else {
//                    if (which == attach_photo || which == attach_gallery) {
//                        action = "chat_upload_photo";
//                    } else {
//                        action = "chat_upload_document";
//                    }
//                }
//            } else {
//                if (which == attach_photo || which == attach_gallery) {
//                    action = "pm_upload_photo";
//                } else {
//                    action = "pm_upload_document";
//                }
//            }
//            if (action != null && !MessagesController.isFeatureEnabled(action, ChatActivity.this)) {
//                return;
//            }
//        }
//
//        if (which == attach_photo) {
//            try {
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                File image = AndroidUtilities.generatePicturePath();
//                if (image != null) {
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
//                    currentPicturePath = image.getAbsolutePath();
//                }
//                startActivityForResult(takePictureIntent, 0);
//            } catch (Exception e) {
//                FileLog.e("tmessages", e);
//            }
//        } else if (which == attach_gallery) {
//            PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(false, ChatActivity.this);
//            fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
//                @Override
//                public void didSelectPhotos(ArrayList<String> photos, ArrayList<String> captions, ArrayList<MediaController.SearchImage> webPhotos) {
//                    SendMessagesHelper.prepareSendingPhotos(photos, null, dialog_id, replyingMessageObject, captions);
//                    SendMessagesHelper.prepareSendingPhotosSearch(webPhotos, dialog_id, replyingMessageObject);
//                    showReplyPanel(false, null, null, null, false, true);
//                }
//
//                @Override
//                public void startPhotoSelectActivity() {
//                    try {
//                        Intent videoPickerIntent = new Intent();
//                        videoPickerIntent.setType("video/*");
//                        videoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
//                        videoPickerIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, (long) (1024 * 1024 * 1536));
//
//                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                        photoPickerIntent.setType("image/*");
//                        Intent chooserIntent = Intent.createChooser(photoPickerIntent, null);
//                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{videoPickerIntent});
//
//                        startActivityForResult(chooserIntent, 1);
//                    } catch (Exception e) {
//                        FileLog.e("tmessages", e);
//                    }
//                }
//
//                @Override
//                public boolean didSelectVideo(String path) {
//                    if (Build.VERSION.SDK_INT >= 16) {
//                        return !openVideoEditor(path, true, true);
//                    } else {
//                        SendMessagesHelper.prepareSendingVideo(path, 0, 0, 0, 0, null, dialog_id, replyingMessageObject);
//                        showReplyPanel(false, null, null, null, false, true);
//                        return true;
//                    }
//                }
//            });
//            presentFragment(fragment);
//        } else if (which == attach_video) {
//            try {
//                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                File video = AndroidUtilities.generateVideoPath();
//                if (video != null) {
//                    if (Build.VERSION.SDK_INT >= 18) {
//                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(video));
//                    }
//                    takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, (long) (1024 * 1024 * 1536));
//                    currentPicturePath = video.getAbsolutePath();
//                }
//                startActivityForResult(takeVideoIntent, 2);
//            } catch (Exception e) {
//                FileLog.e("tmessages", e);
//            }
//        } else if (which == attach_location) {
//            if (!isGoogleMapsInstalled()) {
//                return;
//            }
//            LocationActivity fragment = new LocationActivity();
//            fragment.setDelegate(new LocationActivity.LocationActivityDelegate() {
//                @Override
//                public void didSelectLocation(TLRPC.MessageMedia location) {
//                    SendMessagesHelper.getInstance().sendMessage(location, dialog_id, replyingMessageObject);
//                    moveScrollToLastMessage();
//                    showReplyPanel(false, null, null, null, false, true);
//                    if (paused) {
//                        scrollToTopOnResume = true;
//                    }
//                }
//            });
//            presentFragment(fragment);
//        } else if (which == attach_document) {
//            DocumentSelectActivity fragment = new DocumentSelectActivity();
//            fragment.setDelegate(new DocumentSelectActivity.DocumentSelectActivityDelegate() {
//                @Override
//                public void didSelectFiles(DocumentSelectActivity activity, ArrayList<String> files) {
//                    activity.finishFragment();
//                    SendMessagesHelper.prepareSendingDocuments(files, files, null, null, dialog_id, replyingMessageObject);
//                    showReplyPanel(false, null, null, null, false, true);
//                }
//
//                @Override
//                public void startDocumentSelectActivity() {
//                    try {
//                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                        photoPickerIntent.setType("*/*");
//                        startActivityForResult(photoPickerIntent, 21);
//                    } catch (Exception e) {
//                        FileLog.e("tmessages", e);
//                    }
//                }
//            });
//            presentFragment(fragment);
//        } else if (which == attach_audio) {
//            AudioSelectActivity fragment = new AudioSelectActivity();
//            fragment.setDelegate(new AudioSelectActivity.AudioSelectActivityDelegate() {
//                @Override
//                public void didSelectAudio(ArrayList<MessageObject> audios) {
//                    SendMessagesHelper.prepareSendingAudioDocuments(audios, dialog_id, replyingMessageObject);
//                    showReplyPanel(false, null, null, null, false, true);
//                }
//            });
//            presentFragment(fragment);
//        } else if (which == attach_contact) {
//            try {
//                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
//                startActivityForResult(intent, 31);
//            } catch (Exception e) {
//                FileLog.e("tmessages", e);
//            }
//        }
    }

    private void alertUserOpenError(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(IMChatActivity.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setPositiveButton("确定", null);
        builder.setMessage(error);
        showDialog(builder.create());
    }


    private MessagePhotoViewer.PhotoViewerProvider photoViewerProvider = new MessagePhotoViewer.PhotoViewerProvider() {

        @Override
        public MessagePhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject) {
            if (messageObject == null) {
                return null;
            }
            int count = chatListView.getChildCount();

            for (int a = 0; a < count; a++) {
                MessageObject messageToOpen = null;
                ImageReceiver imageReceiver = null;
                View view = chatListView.getChildAt(a);
                if (view instanceof ChatImageCell) {
                    ChatImageCell cell = (ChatImageCell) view;
                    MessageObject message = cell.getMessageObject();
                    if (message != null && message.getId() == messageObject.getId()) {
                        messageToOpen = message;
                        imageReceiver = cell.getImageReceiver();
                    }
                }

                if (messageToOpen != null) {
                    int coords[] = new int[2];
                    view.getLocationInWindow(coords);
                    MessagePhotoViewer.PlaceProviderObject object = new MessagePhotoViewer.PlaceProviderObject();
                    object.viewX = coords[0];
                    object.viewY = coords[1] - AndroidUtilities.statusBarHeight;
                    object.parentView = chatListView;
                    object.imageReceiver = imageReceiver;
                    object.thumb = imageReceiver.getBitmap();
                    object.radius = imageReceiver.getRoundRadius();
                    return object;
                }
            }
            return null;
        }

        @Override
        public Bitmap getThumbForPhoto(MessageObject messageObject) {
            return null;
        }

        @Override
        public void willSwitchFromPhoto(MessageObject messageObject) {

        }

        @Override
        public void willHidePhotoViewer() {

        }

        @Override
        public boolean isPhotoChecked() {
            return false;
        }

        @Override
        public void setPhotoChecked() {

        }

        @Override
        public void cancelButtonPressed() {

        }

        @Override
        public void sendButtonPressed() {

        }

        @Override
        public int getSelectedCount() {
            return 0;
        }

        @Override
        public void updatePhotoAtIndex() {

        }
    };

}
