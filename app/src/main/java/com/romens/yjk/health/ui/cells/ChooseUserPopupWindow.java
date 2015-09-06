package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.components.AddRemindCallback;

import java.util.List;

/**
 * Created by anlc on 2015/8/28.
 */
public class ChooseUserPopupWindow {
    //选择提醒用户的弹窗
    private Context context;
    private ListView listView;
    private LinearLayout layout;
    private List<String> data;
    private PopupAdapter adapter;
    private PopupWindow popupWindow;
    private AddRemindCallback callBack;

    public ChooseUserPopupWindow(Context context, final List<String> data) {
        callBack= (AddRemindCallback) context;
        this.context = context;
        this.data = data;
        listView = new ListView(context);
        listView.setDividerHeight(0);
        listView.setDivider(null);
        listView.setScrollbarFadingEnabled(false);
        layout = new LinearLayout(context);
        layout.addView(listView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        adapter = new PopupAdapter();
        listView.setAdapter(adapter);

        popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int width = layout.getMeasuredWidth();
        popupWindow.setWidth(width);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                this.editUser.setText(data.get(position));
                callBack.setEditUserText(data.get(position));
                popupWindow.dismiss();
            }
        });
    }

    public void show(View view) {
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.drug_type_child));
        popupWindow.showAsDropDown(view);
    }

    class PopupAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linearLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(context);
            textView.setText(data.get(position));
            textView.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
            textView.setTextSize(16);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);
            return linearLayout;
        }
    }
}