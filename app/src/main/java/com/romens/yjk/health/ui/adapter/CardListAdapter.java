package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.yjk.health.R;

import java.util.List;

/**
 * Created by HZH on 2016/2/1.
 */
public class CardListAdapter extends BaseAdapter {
    private List<String> mResult;
    private Context mContext;

    public CardListAdapter(Context context) {
        this.mContext = context;
    }

    public void BindData(List<String> result) {
        this.mResult = result;
    }

    @Override
    public int getCount() {
        return mResult == null ? 0 : mResult.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardHolder cardHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_card, parent, false);
            cardHolder = new CardHolder();
            cardHolder.cardSign = (ImageView) convertView.findViewById(R.id.cardSign);
            cardHolder.cardNumber = (TextView) convertView.findViewById(R.id.cardNumber);
            cardHolder.cardName = (TextView) convertView.findViewById(R.id.cardName);
            cardHolder.accountName = (TextView) convertView.findViewById(R.id.accountName);
            convertView.setTag(cardHolder);
        } else {
            cardHolder = (CardHolder) convertView.getTag();
        }
        //change cardSign、cardNumber's color、cardName's DrawableLeft according to data
        if ("先声再康".equals(mResult.get(position))) {

        } else if ("要健康".equals(mResult.get(position))) {

        } else if ("人民同泰".equals(mResult.get(position))) {

        }

        return convertView;
    }

    class CardHolder {
        private ImageView cardSign;
        private TextView cardName, accountName, cardNumber;
    }

}
