package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.components.SwLin;
import com.romens.yjk.health.ui.components.SwLin.ScreenListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwLinAdapter extends BaseAdapter {

	private List<String> data;
	private Context context;
	private Map<Integer, SwLin> mapView;

	public SwLinAdapter(Context context, List<String> data) {
		this.context = context;
		this.data = data;
		mapView = new HashMap<Integer, SwLin>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		convertView = LayoutInflater.from(context).inflate(R.layout.list_item_swlin, null);
		
		// TODO Auto-generated method stub
		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("convertView position:" + position);

			}
		});
		SwLin sw = (SwLin) convertView.findViewById(R.id.swlin_layout);
		sw.setTag(position);

		mapView.put(position, sw);
		sw.setScreenListener(new ScreenListener() {

			@Override
			public boolean startTouch(String tag) {

				if (mTouch) {
					if (showMenuTag.equals(tag)) {
						mTouch = false;
					} else {
						int p = Integer.parseInt(showMenuTag);
						showMainLayout();
					}

				}
				return mTouch;
			}

			@Override
			public void changeScreen(int screen, String tag) {

				if (screen == 1) {
					mTouch = true;
					showMenuTag = tag;
				}
			}

			@Override
			public void canTouch(boolean flag) {
				System.out.println("canTouch:" + flag);
				mTouch = false;
			}

		});
		TextView time = (TextView) convertView.findViewById(R.id.swlin_time);
		time.setText("item:" + data.get(position));
		TextView delete = (TextView) convertView.findViewById(R.id.swlin_delete);
		time.setTag(position);
		delete.setTag(position);
		time.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SwLin s = mapView.get(position);

				if (s != null && s.getCurrentScreen() == 1) {
					s.showScreen(0);//0 为主页面 1编辑删除按钮界面
					return;
				}
				Toast.makeText(context, "您点击了item,position:" + v.getTag().toString(),
						Toast.LENGTH_SHORT).show();
				showMainLayout();
			}
		});
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(context, "您点击了删除按钮：" + v.getTag().toString(),
						Toast.LENGTH_SHORT).show();
				showMainLayout();
			}
		});
		return convertView;
	}

	public void showMainLayout() {

		for (int key : mapView.keySet()) {
			mapView.get(key).showScreen(0);

		}
	}

	private boolean mTouch = false;
	private String showMenuTag;

}
