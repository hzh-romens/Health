package com.romens.yjk.health.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.yjk.health.R;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.PushMessageEntity;
import com.romens.yjk.health.helper.LabelHelper;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.PushMessageCell;
import com.romens.yjk.health.wx.push.PushManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhou Lisi
 * @create 2016-04-14 00:01
 * @description
 */
public class PushMessagesActivity extends DarkActionBarActivity implements AppNotificationCenter.NotificationCenterDelegate {

    private ListAdapter adapter;
    private final List<PushMessageEntity> pushMessageEntityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onReceivePushMessage);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onPushMessageStateChanged);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBar.setTitle("我的消息");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    onBackPressed();
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
                PushMessageEntity entity = adapter.getItem(position);
                PushManager.onPushMessageAction(PushMessagesActivity.this, entity);
            }
        });

        adapter = new ListAdapter(this);
        listView.setAdapter(adapter);
        loadLocalPushMessage();
    }

    private void loadLocalPushMessage() {
        pushMessageEntityList.clear();
        List<PushMessageEntity> messages = DBInterface.instance().loadLocalPushMessage();
        if (messages != null && messages.size() > 0) {
            pushMessageEntityList.addAll(messages);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected String getActivityName() {
        return "我的消息";
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onReceivePushMessage);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onPushMessageStateChanged);
        super.onDestroy();
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.onReceivePushMessage || i == AppNotificationCenter.onPushMessageStateChanged) {
            loadLocalPushMessage();
        }
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
            return pushMessageEntityList.size();
        }

        @Override
        public PushMessageEntity getItem(int i) {
            return pushMessageEntityList.get(i);
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
                    view = new PushMessageCell(adapterContext);
                }
                PushMessageCell cell = (PushMessageCell) view;
                cell.setMultilineDetail(true);

                PushMessageEntity entity = getItem(position);
                CharSequence title = createUnReadCount(entity.getTitle(), entity.unRead());
                cell.setTextAndValue(title, entity.getTime(), entity.getContent(), true);
            }
            return view;
        }
    }

    public static CharSequence createUnReadCount(String name, boolean unRead) {
        SpannableStringBuilder ssb = new SpannableStringBuilder("");
        if (unRead) {
            LabelHelper.XImageSpan span = LabelHelper.createImageSpanForUserInfoLabel("未读", R.layout.layout_label_unread_message, R.id.label_text_view);
            ssb.append("<<");
            ssb.setSpan(span, ssb.length() - 2, ssb.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" ");
        }
        ssb.append(name);
        return ssb;
    }
}

