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
import android.widget.Toast;

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
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.ui.CollectActivity;
import com.romens.yjk.health.ui.ControlAddressActivity;
import com.romens.yjk.health.ui.FeedBackActivity;
import com.romens.yjk.health.ui.HistoryActivity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.activity.LoginActivity;
import com.romens.yjk.health.ui.cells.LoginCell;
import com.romens.yjk.health.ui.cells.UserProfileCell;
import com.romens.yjk.health.ui.utils.UIHelper;

/**
 * Created by siery on 15/8/10.
 */
public class HomeMyFragment extends BaseFragment implements AppNotificationCenter.NotificationCenterDelegate {
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginSuccess);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == addressRow) {
//                    startActivity(new Intent(getActivity(), ControlAddressActivity.class));
//                }
//            }
//        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == addressRow) {
                    startActivity(new Intent(getActivity(), ControlAddressActivity.class));
                } else if (position == myOrderRow) {
                    startActivity(new Intent(getActivity(), MyOrderActivity.class));
                } else if (position == collectRow) {
                    startActivity(new Intent(getActivity(), CollectActivity.class));
                } else if (position == historyRow) {
                    startActivity(new Intent(getActivity(), HistoryActivity.class));
                } else if (position == feedbackRow) {
                    startActivity(new Intent(getActivity(), FeedBackActivity.class));
                } else if (position == helpRow) {
                    Toast.makeText(getActivity(), "开发中...", Toast.LENGTH_SHORT).show();
                } else if (position == accountRow) {
                    Toast.makeText(getActivity(), "开发中...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.loginSuccess);
        super.onDestroy();
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        updateData();
    }

    private void updateData() {
//        userEntity = new UserEntity();
//        userEntity.setName("anlc");
//        userEntity.setAvatar("anlc");
//        userEntity.setEmail("1235@yaojk.com");
//        userEntity.setGuid("111111111");
//        userEntity.setPhone("13323232323");
//        userEntity.setStatus(0);
//        userEntity.setDepartmentId("22222");
        if (UserConfig.isClientLogined()) {
            userEntity = new UserEntity(0, "", "siery", "", "", "", "", 0);
//            userEntity = new UserEntity();
//            userEntity.setName("siery");
        } else {
            userEntity = null;
        }

        rowCount = 0;
        if (userEntity != null) {
            loginRow = -1;
            userProfileRow = rowCount++;
            userInfoSectionRow = rowCount++;
            userInfoSectionRow1 = rowCount++;
            accountRow = rowCount++;
            myOrderRow = rowCount++;
            collectRow = rowCount++;
            historyRow = rowCount++;
            addressRow = rowCount++;
        } else {
            loginRow = rowCount++;
            userProfileRow = -1;
            userInfoSectionRow = -1;
            userInfoSectionRow1 = -1;
            addressRow = -1;
            myOrderRow = -1;
            collectRow = -1;
            historyRow = -1;
            accountRow = -1;
            helpRow = -1;
            feedbackRow = -1;
        }

        otherInfoSectionRow = rowCount++;
        otherInfoSectionRow1 = rowCount++;
        if (userEntity != null) {
            helpRow = rowCount++;
            feedbackRow = rowCount++;
        } else {
            helpRow = -1;
            feedbackRow = -1;
        }
        checkUpdateRow = rowCount++;
        appInfoRow = rowCount++;
        adapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int loginRow;
    private int userProfileRow;

    private int userInfoSectionRow;
    private int userInfoSectionRow1;
    private int addressRow;
    private int myOrderRow;

    private int otherInfoSectionRow;
    private int otherInfoSectionRow1;
    private int checkUpdateRow;
    private int appInfoRow;

    private int collectRow;
    private int historyRow;
    private int accountRow;
    private int helpRow;
    private int feedbackRow;

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.loginSuccess) {
            updateData();
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
            return i != userInfoSectionRow || i != userInfoSectionRow1 || i != otherInfoSectionRow || i != otherInfoSectionRow1;
//            return i == userProfileRow || i == addressRow || i == myOrderRow;
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
            } else if (i == addressRow || i == checkUpdateRow || i == myOrderRow || i == helpRow) {
                return 3;
            } else if (i == collectRow || i == historyRow || i == accountRow || i == feedbackRow) {
                return 3;
            } else if (i == appInfoRow) {
                return 4;
            } else if (i == loginRow) {
                return 5;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 6;
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
                } else if (position == myOrderRow) {
                    cell.setText("我的订单", false);
                } else if (position == collectRow) {
                    cell.setText("我的收藏", false);
                } else if (position == historyRow) {
                    cell.setText("历史游览", false);
                } else if (position == feedbackRow) {
                    cell.setText("意见反馈", false);
                } else if (position == helpRow) {
                    cell.setText("帮助", false);
                } else if (position == accountRow) {
                    cell.setText("账户管理", false);
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
            } else if (type == 5) {
                if (view == null) {
                    view = new LoginCell(adapterContext);
                    if (position == loginRow) {
                        ((LoginCell) view).setLoginCellDelegate(new LoginCell.LoginCellDelegate() {
                            @Override
                            public void onLoginClick() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
            return view;
        }
    }
}
