package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.android.ui.cells.HeaderCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextInfoCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.model.UserEntity;
import com.romens.yjk.health.ui.NewShippingAddressActivity;
import com.romens.yjk.health.ui.adapter.FocusAdapter;
import com.romens.yjk.health.ui.cells.UserProfileCell;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ResourceBundle;

/**
 * Created by siery on 15/8/10.
 */
public class HomeMyFragment extends BaseFragment {
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;


    //
    private UserEntity userEntity;
    private ListAdapter adapter;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        adapter = new ListAdapter(getActivity());
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout content = new FrameLayout(context);
        swipeRefreshLayout = new SwipeRefreshLayout(context);
        UIHelper.setupSwipeRefreshLayoutProgress(swipeRefreshLayout);
        content.addView(swipeRefreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        FrameLayout listContainer = new FrameLayout(context);
        swipeRefreshLayout.addView(listContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return content;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == addressRow) {
                    startActivity(new Intent(getActivity(), NewShippingAddressActivity.class));
                }
            }
        });
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        updateData(new UserEntity("0", "http://img1.imgtn.bdimg.com/it/u=2637703651,2176836148&fm=21&gp=0.jpg", "google"));
    }

    private void updateData(UserEntity entity) {
        userEntity = entity;
        rowCount = 0;
        if (entity != null) {
            userProfileRow = rowCount++;
            userInfoSectionRow = rowCount++;
            userInfoSectionRow1 = rowCount++;
            addressRow = rowCount++;
        } else {
            userInfoSectionRow = -1;
            userInfoSectionRow1 = -1;
            addressRow = -1;
        }

        otherInfoSectionRow = rowCount++;
        otherInfoSectionRow1 = rowCount++;
        checkUpdateRow = rowCount++;
        appInfoRow = rowCount++;
        adapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int userProfileRow;

    private int userInfoSectionRow;
    private int userInfoSectionRow1;
    private int addressRow;


    private int otherInfoSectionRow;
    private int otherInfoSectionRow1;
    private int checkUpdateRow;
    private int appInfoRow;

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
            return i == userProfileRow || i == addressRow;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int i) {
            return null;
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
            if (i == userProfileRow) {
                return 0;
            } else if (i == userInfoSectionRow1 || i == otherInfoSectionRow1) {
                return 2;
            } else if (i == addressRow | i == checkUpdateRow) {
                return 3;
            } else if (i == appInfoRow) {
                return 4;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 5;
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
                    view = new UserProfileCell(adapterContext);
                }
                UserProfileCell cell = (UserProfileCell) view;
                cell.setUser(userEntity);
            } else if (type == 1) {
                if (view == null) {
                    view = new ShadowSectionCell(adapterContext);
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new HeaderCell(adapterContext);
                }
                HeaderCell cell = (HeaderCell) view;
                cell.setTextColor(ResourcesConfig.primaryColor);
                if (position == otherInfoSectionRow1) {
                    cell.setText("其他");
                } else if (position == userInfoSectionRow1) {
                    cell.setText("个人信息");
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new TextSettingsCell(adapterContext);
                }
                TextSettingsCell cell = (TextSettingsCell) view;
                if (position == addressRow) {
                    cell.setText("收货地址管理", true);
                } else if (position == checkUpdateRow) {
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        cell.setTextAndValue("检查更新", "c" + pInfo.versionCode, true);
                    } catch (PackageManager.NameNotFoundException e) {
                        cell.setText("检查更新", true);
                    }
                }
            } else if (type == 4) {
                if (view == null) {
                    view = new TextInfoCell(adapterContext);
                    if (position == appInfoRow) {
                        try {
                            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                            ((TextInfoCell) view).setText(String.format("%s for Android v%s (c%d)", getString(R.string.app_name), pInfo.versionName, pInfo.versionCode));
                        } catch (Exception e) {
                            FileLog.e("YJKHealth", e);
                        }
                    }
                }
            }
            return view;
        }
    }
}
