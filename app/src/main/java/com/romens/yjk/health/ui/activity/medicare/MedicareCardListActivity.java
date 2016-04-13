package com.romens.yjk.health.ui.activity.medicare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.romens.android.ui.ActionBar.BottomSheet;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.viewholder.PaddingDividerItemDecoration;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.pay.MedicareCard;
import com.romens.yjk.health.pay.PayAppManager;
import com.romens.yjk.health.pay.PayModeEnum;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.MedicareCardCell;
import com.romens.yjk.health.ui.components.ToastCell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description 医保卡选择界面
 */
public class MedicareCardListActivity extends DarkActionBarActivity {

    public static final String ARGUMENTS_KEY_ONLY_SELECT = "key_only_select";
    public static final String ARGUMENTS_KEY_PAY_AMOUNT = "key_pay_amount";

    private ProgressBar progressBar;
    private TextView emptyTextView;

    private ListAdapter adapter;

    private boolean onlySelect = false;
    private BigDecimal payAmount = BigDecimal.ZERO;
    private final List<MedicareCard> medicareCards = new ArrayList<>();

    private Dialog editDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(ARGUMENTS_KEY_ONLY_SELECT)) {
            onlySelect = intent.getBooleanExtra(ARGUMENTS_KEY_ONLY_SELECT, false);
        }
        if (intent.hasExtra(ARGUMENTS_KEY_PAY_AMOUNT)) {
            double amount = intent.getDoubleExtra(ARGUMENTS_KEY_PAY_AMOUNT, 0);
            payAmount = new BigDecimal(amount);
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

        RecyclerView listView = new RecyclerView(this);
        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 16, 16, 16, 16));
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.addItemDecoration(new PaddingDividerItemDecoration(AndroidUtilities.dp(16)));

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

        adapter = new ListAdapter();
        listView.setAdapter(adapter);

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
            medicareCards.add(new MedicareCard.Builder(response.get(i).get("GUID").asText())
                    .withUserName(response.get(i).get("USERNAME").asText())
                    .withCertNo(response.get(i).get("CERTNO").asText())
                    .withCardNo(response.get(i).get("MEDICARENO").asText())
                    .build());
        }
        updateAdapter();
    }

    private void changeProgress(boolean progress) {
        progressBar.setVisibility(progress ? View.VISIBLE : View.GONE);
        emptyTextView.setVisibility(progress ? View.GONE : View.VISIBLE);
    }

    private void changeEmpty() {
        boolean isEmpty = medicareCards.size() <= 0;
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
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                loadUserMedicareCards();
            }
        }
    }


    public void onSelectMedicareCard(final MedicareCard medicareCard) {
        new AlertDialog.Builder(MedicareCardListActivity.this)
                .setTitle("选择社会保障卡")
                .setMessage(String.format("是否确定使用%s的卡号为: %s 的社会保障卡?", medicareCard.userName, medicareCard.cardNo))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent data = new Intent();
                        data.putExtra("MEDICARE_ID", medicareCard.id);
                        data.putExtra("MEDICARE_NAME", medicareCard.userName);
                        data.putExtra("MEDICARE_CERTNO", medicareCard.certNo);
                        data.putExtra("MEDICARE_CARDNO", medicareCard.cardNo);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    public void onLongSelectMedicareCard(final MedicareCard medicareCard) {
        editDialog = new BottomSheet.Builder(this)
                .setTitle("卡片操作")
                .setItems(new CharSequence[]{"修改卡片信息", "移除卡片"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        if (which == 0) {
                            editMedicareCardInfo(medicareCard);
                        } else if (which == 1) {
                            tryDeleteMedicareCard(medicareCard);
                        }
                    }
                }).create();
        editDialog.show();
    }

    private void editMedicareCardInfo(MedicareCard medicareCard) {
        Intent intent = new Intent(MedicareCardListActivity.this, BindUserMedicareNoActivity.class);
        intent.putExtra(BindUserMedicareNoActivity.ARGUMENTS_KEY_MEDICARE_ID, medicareCard.id);
        intent.putExtra(BindUserMedicareNoActivity.ARGUMENTS_KEY_MEDICARE_USER, medicareCard.userName);
        intent.putExtra(BindUserMedicareNoActivity.ARGUMENTS_KEY_MEDICARE_CERTNO, medicareCard.certNo);
        intent.putExtra(BindUserMedicareNoActivity.ARGUMENTS_KEY_MEDICARE_CARDNO, medicareCard.cardNo);
        startActivityForResult(intent, 1);
    }

    private void tryDeleteMedicareCard(final MedicareCard medicareCard) {
        new AlertDialog.Builder(MedicareCardListActivity.this)
                .setTitle("移除社会保障卡卡片")
                .setMessage(String.format("是否确定移除%s 的卡号为 %s 的社会保障卡", medicareCard.userName, medicareCard.cardNo))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMedicareCard(medicareCard);
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    private void deleteMedicareCard(final MedicareCard medicareCard) {
        needShowProgress("正在移除卡片...");
        Map<String, String> args = new HashMap<>();
        args.put("GUID", medicareCard.id);

        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "DeleteUserMedicareCards", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(BindUserMedicareNoActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        needHideProgress();
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (!response.has("ERROR")) {
                                ToastCell.toast(MedicareCardListActivity.this, "卡片移除成功!");
                                loadUserMedicareCards();
                                return;
                            }
                        }
                        ToastCell.toast(MedicareCardListActivity.this, "卡片移除失败!");
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    @Override
    public void onPause() {
        if (editDialog != null && editDialog.isShowing()) {
            editDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected String getActivityName() {
        return "社保卡列表";
    }

    private void onCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    class ListAdapter extends RecyclerView.Adapter {

        public ListAdapter() {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MedicareCardCell cell = new MedicareCardCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            MedicareCardCell cell = (MedicareCardCell) holder.itemView;
            cell.setClickable(true);
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onlySelect) {
                        MedicareCard card = medicareCards.get(position);
                        onSelectMedicareCard(card);
                    }
                }
            });
            cell.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MedicareCard card = medicareCards.get(position);
                    onLongSelectMedicareCard(card);
                    return true;
                }
            });
            cell.setDelegate(new MedicareCardCell.Delegate() {
                @Override
                public void onCheckBalance() {
                    MedicareCard card = medicareCards.get(position);
                    tryCheckMedicareCardBalance(card);
                }
            });
            MedicareCard card = medicareCards.get(position);
            cell.setValue(card.userName, card.certNo, card.cardNo);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return medicareCards.size();
        }
    }

    /**
     * 尝试发起医保卡余额查询
     *
     * @param card
     */
    private void tryCheckMedicareCardBalance(final MedicareCard card) {
        if (!PayAppManager.isSetupYBHEB(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("社会保障卡余额查询")
                    .setMessage("检测手机没有安装 哈尔滨银行 所需的客户端,是否跳转到银行官方页面下载?")
                    .setPositiveButton("现在去下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PayAppManager.needDownloadPayApp(MedicareCardListActivity.this, PayModeEnum.YB_HEB);
                        }
                    }).setNegativeButton("取消", null)
                    .create().show();
            return;
        } else {
            checkMedicareCardBalance(card);
        }
    }

    /**
     * 发起医保卡余额查询
     *
     * @param card
     */
    private void checkMedicareCardBalance(MedicareCard card) {
        ComponentName componentName = new ComponentName(getPackageName(),
                getPackageName() + ".pay.BalanceInfoActivity");
        Intent intent = new Intent();
        intent.putExtra("YJK_PAY_AMOUNT", payAmount.doubleValue());
        intent.putExtra("YJK_MEDICARE_USER", card.userName);
        intent.putExtra("YJK_MEDICARE_CARDNO", card.cardNo);
        intent.setComponent(componentName);
        startActivity(intent);
    }

    private static class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
