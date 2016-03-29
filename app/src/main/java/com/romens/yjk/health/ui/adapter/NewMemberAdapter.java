package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.EmptyCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.model.MemberType;
import com.romens.yjk.health.ui.cells.MemberButtonCell;
import com.romens.yjk.health.ui.cells.MemberEditCell;
import com.romens.yjk.health.ui.cells.TipCell;
import com.romens.yjk.health.ui.fragment.HomeHealthNewFragment;

import java.util.Map;

/**
 * Created by HZH on 2016/3/18.
 */
public class NewMemberAdapter extends RecyclerView.Adapter {
    private SparseIntArray mTypes = new SparseIntArray();
    private Context mContext;

    public NewMemberAdapter(Context context) {
        this.mContext = context;
    }

    public void bindType(SparseIntArray types) {
        if (mTypes != null) {
            mTypes.clear();
        }
        this.mTypes = types;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MemberType.TIP) {
            TipCell cell = new TipCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        } else if (viewType == MemberType.EMPTY || viewType == MemberType.ADVICE) {
            EmptyCell cell = new EmptyCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            cell.setHeight(0);
            return new Holder(cell);
        } else if (viewType == MemberType.PHONE || viewType == MemberType.PSW) {
            MemberEditCell cell = new MemberEditCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        } else if (viewType == MemberType.BUTTON) {
            MemberButtonCell cell = new MemberButtonCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int itemViewType = getItemViewType(position);
        if (itemViewType == MemberType.TIP) {
            TipCell cell = (TipCell) holder.itemView;
            cell.setBackgroundColor(mContext.getResources().getColor(R.color.btn_primary_light));
            cell.setTextColor(Color.BLACK);
            cell.setTextSize(16);
            cell.setValue("验证手机号码立即开通会员");
        } else if (itemViewType == MemberType.EMPTY || itemViewType == MemberType.ADVICE) {
            EmptyCell cell = (EmptyCell) holder.itemView;
        } else if (itemViewType == MemberType.PHONE || itemViewType == MemberType.PSW) {
            final MemberEditCell cell = (MemberEditCell) holder.itemView;
            cell.setBackgroundColor(mContext.getResources().getColor(R.color.md_white_1000));
            if (itemViewType == MemberType.PHONE) {
                cell.setDrawableLeft(mContext.getResources().getDrawable(R.drawable.ic_edit_phonebumber));
                cell.setVisible(true);
                cell.setHintText("请输入手机号码");
                cell.setNeedDivider(true);
            } else if (itemViewType == MemberType.PSW) {
                cell.setDrawableLeft(mContext.getResources().getDrawable(R.drawable.ic_edit_phonepassword));
                cell.setVisible(false);
                cell.setHintText("请输入验证码");
                cell.setNeedDivider(true);
                cell.setSendRecommondListener(new MemberEditCell.SendRecommondListener() {
                    @Override
                    public void SendRecommond() {
                        if (TextUtils.isEmpty(phoneNumber)) {
                            Toast.makeText(mContext, "请填写您的手机号码", Toast.LENGTH_SHORT).show();
                        } else {
                            String textValue = cell.getTextValue();
                            if ("获取验证码".equals(textValue) || "再次获取".equals(textValue)) {
                                cell.setTextViewValueAndChrono("");
                                getRecommonCode();
                            }
                        }


                    }
                });
            }
            cell.SetEditTextChangeListener(new MemberEditCell.EditTextChangeListener() {
                @Override
                public void EditTextChange(String value) {
                    if (itemViewType == MemberType.PHONE) {
                        phoneNumber = value;
                    } else if (itemViewType == MemberType.PSW) {
                        password = value;
                    }
                }
            });
        } else {
            final MemberButtonCell cell = (MemberButtonCell) holder.itemView;
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.sureButtonClickListener(phoneNumber, password);
                }
            });
        }
    }

    private void getRecommonCode() {
        Map<String, String> args = new FacadeArgs.MapBuilder().
                put("PHONE", phoneNumber).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "SendCodeByCard", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(HomeHealthNewFragment.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(
                        new Connect.AckDelegate() {
                            @Override
                            public void onResult(Message message, Message errorMessage) {
                                if (errorMessage == null) {
                                    ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                                    JsonNode jsonNode = responseProtocol.getResponse();
                                    String result = jsonNode.get("RESULT").asText();
                                }
                            }
                        }
                ).build();

        ConnectManager.getInstance().request(mContext, connect);
    }


    static class Holder extends RecyclerView.ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        if (mTypes != null) {
            return mTypes.size();
        } else {
            return 0;
        }
    }

    private String phoneNumber, password;
    private onItemClickListener mListener;

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mListener = listener;
    }

    public interface onItemClickListener {
        void sureButtonClickListener(String phoneValue, String pswValue);
    }

    @Override
    public int getItemViewType(int position) {
        return mTypes.get(position);
    }

}
