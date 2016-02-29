package com.romens.yjk.health.ui.activity.medicare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.pay.MedicareCard;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;
import com.romens.yjk.health.ui.cells.MedicareCardCell;
import com.romens.yjk.health.ui.components.logger.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description 医保卡选择界面
 */
public class MedicareCardListActivity extends BaseActionBarActivityWithAnalytics {

    public static final String ARGUMENTS_KEY_ONLY_SELECT = "key_only_select";

    private ProgressBar progressBar;
    private TextView emptyTextView;

    private ListAdapter adapter;

    private boolean onlySelect = false;
    private final List<MedicareCard> medicareCards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(ARGUMENTS_KEY_ONLY_SELECT)) {
            onlySelect = intent.getBooleanExtra(ARGUMENTS_KEY_ONLY_SELECT, false);
        }
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    onCancel();
                } else if (id == 0) {
                    addNewMedicareCard();
                }
            }
        });

        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.ic_add_white_24dp);
        if (onlySelect) {
            actionBar.setBackButtonImage(R.drawable.ic_close_white_24dp);
            actionBar.setTitle("选择支付的社会保障卡");
        } else {
            actionBar.setBackButtonImage(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setTitle("已绑定的社会保障卡");
        }

        FrameLayout listContainer = new FrameLayout(this);
        content.addView(listContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ListView listView = new ListView(this);
        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setSelector(R.drawable.list_selector);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        listContainer.addView(progressBar, LayoutHelper.createFrame(48, 48, Gravity.CENTER));
        emptyTextView = new TextView(this);
        emptyTextView.setTextColor(0xff808080);
        emptyTextView.setTextSize(20);
        emptyTextView.setLineSpacing(AndroidUtilities.dp(4), 1.0f);
        emptyTextView.setGravity(Gravity.CENTER);
        String emptyText = "点击右上角 # 可以添加社会保障卡";
        int index = emptyText.indexOf("#");
        SpannableString helperSpan = new SpannableString(emptyText);
        helperSpan.setSpan(new ImageSpan(this, R.drawable.ic_add_grey600_24dp), index, index + 1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        emptyTextView.setText(helperSpan);
        listContainer.addView(emptyTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 16, 0, 16, 0));

        adapter = new ListAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onlySelect) {
                    MedicareCard card = medicareCards.get(position);
                    onSelectMedicareCard(card.cardNo);
                }
            }
        });

        loadUserMedicareCards();
    }


    private void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

    private void loadUserMedicareCards() {
        medicareCards.clear();
        updateAdapter();
        changeProgress(true);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserMedicareCards", "");
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(BindUserMedicareNoActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        changeProgress(false);
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (!response.has("ERROR")) {
                                bindData(response);
                            }
                        }
                        changeEmpty();
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    private void bindData(JsonNode response) {
        medicareCards.clear();
        int size = response.size();
        for (int i = 0; i < size; i++) {
            medicareCards.add(new MedicareCard(response.get(i).get("MEDICARENO").asText()));
        }
        updateAdapter();
    }

    private void changeProgress(boolean progress) {
        progressBar.setVisibility(progress ? View.VISIBLE : View.GONE);
        emptyTextView.setVisibility(progress ? View.GONE : View.VISIBLE);
    }

    private void changeEmpty() {
        boolean isEmpty = adapter.isEmpty();
        emptyTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void addNewMedicareCard() {
        Intent intent = new Intent(MedicareCardListActivity.this, BindUserMedicareNoActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                loadUserMedicareCards();
            }
        }
    }


    private void onSelectMedicareCard(String medicareNo) {
        Intent data = new Intent();
        data.putExtra("MEDICARENO", medicareNo);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    class ListAdapter extends BaseAdapter {
        private Context adapterContext;

        public ListAdapter(Context context) {
            adapterContext = context;
        }

        @Override
        public int getCount() {
            return medicareCards.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new MedicareCardCell(adapterContext);
            }

            MedicareCardCell cell = (MedicareCardCell) convertView;
            cell.setText(medicareCards.get(position).cardNo, true);
            return convertView;
        }
    }
}
