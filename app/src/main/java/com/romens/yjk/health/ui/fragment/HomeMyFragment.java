package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.android.ui.cells.HeaderCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextIconCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.helper.MonitorHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.ControlAddressActivity;
import com.romens.yjk.health.ui.FeedBackActivity;
import com.romens.yjk.health.ui.HelpActivity;
import com.romens.yjk.health.ui.HistoryActivity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.activity.LoginActivity;
import com.romens.yjk.health.ui.cells.LoginCell;
import com.romens.yjk.health.ui.cells.NewUserProfileCell;
import com.romens.yjk.health.ui.cells.SupportCell;
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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        listView = new ListView(context);
//        FrameLayout listContainer = new FrameLayout(context);
        swipeRefreshLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
//        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return content;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginSuccess);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long key) {
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
                    //startActivity(new Intent(getActivity(), CollectActivity.class));
                    UIOpenHelper.openFavoritesActivity(getActivity());
                } else if (position == historyRow) {
                    startActivity(new Intent(getActivity(), HistoryActivity.class));
                } else if (position == feedbackRow) {
                    startActivity(new Intent(getActivity(), FeedBackActivity.class));
                } else if (position == helpRow) {
                    startActivity(new Intent(getActivity(), HelpActivity.class));
                } else if (position == accountRow) {
                    //startActivity(new Intent(getActivity(), PersonalInformationActivity.class));
                    UIOpenHelper.openAccountSettingActivity(getActivity());
                    //startActivity(new Intent(getActivity(), AccountSettingActivity.class));
                } else if (position == exitRow) {
                    UserConfig.clearUser();
                    UserConfig.clearConfig();
                    FacadeToken.getInstance().expired();
                    userEntity = null;
                    updateData();
                    AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, -100000);
                } else if (position == checkUpdateRow) {
                    MonitorHelper.checkUpdate(getActivity(), true);
                } else if (position == memberRow) {
                    UIOpenHelper.openMemberActivity(getActivity());
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
        if (UserConfig.isClientLogined()) {
            UserEntity clientUserEntity = UserConfig.getClientUserEntity();
            userEntity = new UserEntity(0, clientUserEntity.getGuid(), clientUserEntity.getName(), clientUserEntity.getAvatar(), clientUserEntity.getPhone(), clientUserEntity.getEmail(), clientUserEntity.getDepartmentId(), 0);
        } else {
            userEntity = null;
        }

        rowCount = 0;
        if (userEntity != null) {
            loginRow = -1;
            userProfileRow = rowCount++;
            userInfoSectionRow1 = rowCount++;
            accountRow = rowCount++;
            myOrderRow = rowCount++;
            memberRow = rowCount++;
            collectRow = rowCount++;
            historyRow = rowCount++;
            addressRow = rowCount++;
            exitRow = rowCount++;
        } else {
            loginRow = rowCount++;
            userProfileRow = -1;
            userInfoSectionRow1 = -1;
            addressRow = -1;
            myOrderRow = -1;
            memberRow = -1;
            collectRow = -1;
            historyRow = -1;
            exitRow = -1;
            accountRow = -1;
            helpRow = -1;
            feedbackRow = -1;

        }

        otherInfoSectionRow1 = rowCount++;
        otherSectionRow = rowCount++;
        if (userEntity != null) {
            helpRow = rowCount++;
            feedbackRow = rowCount++;
        } else {
            helpRow = -1;
            feedbackRow = -1;
        }
        checkUpdateRow = rowCount++;

        supportRow = rowCount++;

        adapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int loginRow;
    private int userProfileRow;

    private int userInfoSectionRow1;
    private int addressRow;
    private int myOrderRow;

    private int otherInfoSectionRow1;

    private int accountRow;
    private int collectRow;
    private int historyRow;
    private int exitRow;

    private int otherSectionRow;
    private int helpRow;
    private int feedbackRow;
    private int checkUpdateRow;

    private int supportRow;
    private int memberRow;

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
            return i != userInfoSectionRow1 || i != otherInfoSectionRow1;
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
            } else if (i == userInfoSectionRow1) {
                return 2;
            } else if (i == addressRow || i == myOrderRow || i == exitRow || i == memberRow) {
                return 3;
            } else if (i == collectRow || i == historyRow || i == accountRow) {
                return 3;
            } else if (i == supportRow) {
                return 4;
            } else if (i == loginRow) {
                return 5;
            } else if (i == otherSectionRow) {
                return 6;
            } else if (i == checkUpdateRow || i == feedbackRow || i == helpRow) {
                return 7;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 8;
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
                    view = new NewUserProfileCell(adapterContext);
                }
                NewUserProfileCell cell = (NewUserProfileCell) view;
                cell.setUser(userEntity);
            } else if (type == 1) {
                if (view == null) {
                    view = new ShadowSectionCell(adapterContext);
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextIconCell(adapterContext);
                }
                TextIconCell cell = (TextIconCell) view;
                cell.setBackgroundResource(R.drawable.greydivider);
                if (position == userInfoSectionRow1) {
                    cell.setIconText(R.drawable.ic_person, "个人中心", false);
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new TextIconCell(adapterContext);
                }

                TextIconCell cell = (TextIconCell) view;
                cell.setTextColor(0xff212121);
                cell.setValueTextColor(ResourcesConfig.textPrimary);
                if (position == addressRow) {
                    cell.setIconTextAndNav(R.drawable.ic_address2, "收货地址管理", R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == myOrderRow) {
                    cell.setIconTextAndNav(R.drawable.ic_order, "我的订单", R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == collectRow) {
                    cell.setIconTextAndNav(R.drawable.ic_favorite_person, "我的收藏", R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == historyRow) {
                    cell.setIconTextAndNav(R.drawable.ic_history, "历史浏览", R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == accountRow) {
                    cell.setIconTextAndNav(R.drawable.ic_account, "账户管理", R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == exitRow) {
                    cell.setTextColor(0xffd01716);
                    cell.setIconText(R.drawable.ic_exit, "退出登录", true);
                } else if (position == memberRow) {
                    cell.setIconTextAndNav(R.drawable.ic_account, "会员管理", R.drawable.ic_chevron_right_grey600_24dp, true);
                }
            } else if (type == 4) {
                if (view == null) {
                    view = new SupportCell(adapterContext);
                    view.setPadding(0, AndroidUtilities.dp(8), 0, 0);
                }
                SupportCell cell = (SupportCell) view;
                cell.setBackgroundResource(R.drawable.greydivider);
                cell.setMultilineDetail(true);
                SpannableStringBuilder value = new SpannableStringBuilder();
                SpannableString supportOrg = new SpannableString("青岛雨诺网络信息股份有限公司");
                supportOrg.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, supportOrg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                value.append(supportOrg);
                value.append("\n");
                try {
                    PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                    value.append(String.format("%s for Android v%s (c%d)", getString(R.string.app_name), pInfo.versionName, pInfo.versionCode));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (position == supportRow) {
                    cell.setTextAndValue("技术支持", value, true);
                }
            } else if (type == 5) {
                if (view == null) {
                    view = new LoginCell(adapterContext);
                }
                LoginCell cell = (LoginCell) view;
                if (position == loginRow) {
                    cell.setLoginCellDelegate(new LoginCell.LoginCellDelegate() {
                        @Override
                        public void onLoginClick() {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            } else if (type == 6) {
                if (view == null) {
                    view = new HeaderCell(adapterContext);
                }
                HeaderCell cell = (HeaderCell) view;
                cell.setTextColor(ResourcesConfig.textPrimary);
                if (position == otherSectionRow) {
                    cell.setText("其他");
                }
            } else if (type == 7) {
                if (view == null) {
                    view = new TextSettingsCell(adapterContext);
                }
                TextSettingsCell cell = (TextSettingsCell) view;
                if (position == helpRow) {
                    cell.setTextAndIcon("帮助", R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == feedbackRow) {
                    cell.setTextAndIcon("意见反馈", R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == checkUpdateRow) {
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        cell.setTextAndValue("检查更新", "c" + pInfo.versionCode, true);
                    } catch (PackageManager.NameNotFoundException e) {
                        cell.setText("检查更新", true);
                    }
                }
            }
            return view;
        }
    }
}
