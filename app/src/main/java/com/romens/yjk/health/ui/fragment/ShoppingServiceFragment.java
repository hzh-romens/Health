package com.romens.yjk.health.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.romens.android.log.FileLog;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.base.BaseActionBarActivity;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.MedicineGoodsItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siery on 15/12/15.
 */
public class ShoppingServiceFragment extends ServiceFragment implements AppNotificationCenter.NotificationCenterDelegate {
    private static final int REQUEST_CODE_LOGIN_FOR_ADDTOSHOPPINGCART = 100;

    private ProgressDialog progressDialog;

    private int shoppingCartCount = 0;

    private MedicineGoodsItem waitingAddGoodsItem;

    public static ShoppingServiceFragment instance(FragmentManager fragmentManager) {
        return getService(ShoppingServiceFragment.class, fragmentManager);
    }

    @Override
    public void onCreate(final Bundle state) {
        super.onCreate(state);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginSuccess);
        loadShoppingCartCount();
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.loginSuccess);
        needHideProgress();
        super.onDestroy();

    }

    public int getShoppingCartCount() {
        return shoppingCartCount;
    }

    private void loadShoppingCartCount() {
        if (UserConfig.isClientLogined()) {
            Map<String, Object> args = new HashMap<>();
            args.put("USERGUID", UserConfig.getClientUserId());
            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetBuyCarCount", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol)
                    .build();
            FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
                @Override
                public void onTokenTimeout(Message msg) {
                    shoppingCartCount = 0;
                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    if (errorMsg == null) {
                        ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                        String response = responseProtocol.getResponse();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("BUYCOUNT")) {
                                shoppingCartCount = jsonObject.getInt("BUYCOUNT");
                                if (shoppingCartCount < 0) {
                                    shoppingCartCount = 0;
                                }
                                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onShoppingCartChanged, shoppingCartCount);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    shoppingCartCount = 0;
                }
            });
        }
    }


    public void tryAddToShoppingCart(MedicineGoodsItem goodsItem) {
        waitingAddGoodsItem = goodsItem;
        if (UserConfig.isClientLogined()) {
            needShowProgress("正在加入购物车...");
            Map<String, Object> args = new HashMap<>();
            args.put("GOODSGUID", goodsItem.guid);
            args.put("USERGUID", UserConfig.getClientUserEntity().getGuid());
            args.put("BUYCOUNT", "1");
            args.put("PRICE", goodsItem.userPrice);
            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "InsertIntoCar", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol)
                    .build();
            FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
                @Override
                public void onTokenTimeout(Message msg) {
                    needHideProgress();
                    showSnackBar("请求被拒绝,请稍后重试!", "重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tryAddToShoppingCart(waitingAddGoodsItem);
                        }
                    });
                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    needHideProgress();
                    waitingAddGoodsItem = null;
                    if (errorMsg == null) {
                        ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                        String response = responseProtocol.getResponse();
                        if ("ERROE".equals(response)) {
                            showAddShoppingCartError("未知原因");
                        } else {
                            Toast.makeText(getActivity(), "成功加入购物车", Toast.LENGTH_SHORT).show();
                            shoppingCartCount++;
                            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onShoppingCartChanged, shoppingCartCount);
                        }
                    } else {
                        showAddShoppingCartError(errorMsg.msg);
                    }
                }
            });
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("登录后可以加入购物车,是否登录?")
                    .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UIOpenHelper.openLoginActivity(ShoppingServiceFragment.this, REQUEST_CODE_LOGIN_FOR_ADDTOSHOPPINGCART);
                        }
                    }).setNegativeButton("取消", null).create().show();
        }
    }

    private void showAddShoppingCartError(String error) {
        new AlertDialog.Builder(getActivity())
                .setTitle("加入购物车失败")
                .setMessage(String.format("原因:%s", error))
                .setPositiveButton("知道了", null).create().show();
    }

    private void showSnackBar(String text, String action, View.OnClickListener listener) {
        Activity activity = getActivity();
        if (activity instanceof BaseActionBarActivity) {
            View view = ((BaseActionBarActivity) activity).getMyActionBar();
            Snackbar snackbar = Snackbar.make(view, text,
                    Snackbar.LENGTH_SHORT);
            if (!TextUtils.isEmpty(action)) {
                snackbar.setAction(action, listener);
            }
            snackbar.show();
        } else {
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }
    }

    public void needShowProgress(String progressText) {
        if (progressDialog != null) {
            return;
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(progressText);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void needHideProgress() {
        if (progressDialog == null) {
            return;
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        progressDialog = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LOGIN_FOR_ADDTOSHOPPINGCART) {
            if (resultCode == Activity.RESULT_OK) {
                if (waitingAddGoodsItem != null) {
                    tryAddToShoppingCart(waitingAddGoodsItem);
                }
            }
        }
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.loginSuccess) {
            loadShoppingCartCount();
        }
    }
}
