package com.romens.yjk.health.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.OrderDao;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.ui.MyOrderActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 2016-03-30 00:08
 * @description
 */
public class OrderServiceFragment extends ServiceFragment implements AppNotificationCenter.NotificationCenterDelegate {

    public static OrderServiceFragment instance(FragmentManager fragmentManager) {
        return getService(OrderServiceFragment.class, fragmentManager);
    }

    @Override
    public void onCreate(final Bundle state) {
        super.onCreate(state);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.needUpdateOrderData) {
            syncOrderData();
        }
    }

    public void syncOrderData() {
        Map<String, String> args = new HashMap<>();
        args.put("USERGUID", UserSession.getInstance().getUser());
        args.put("ORDERSTATUS", "0");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "getMyOrders", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(MyOrderActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            final JsonNode response = responseProtocol.getResponse();
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    handleOrderData(response);
                                }
                            });

                        } else {
                            handleOrderData(null);
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(getActivity(), connect);
    }

    private void handleOrderData(JsonNode response) {
        List<OrderEntity> entities = new ArrayList<>();
        int count = response == null ? 0 : response.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                entities.add(new OrderEntity(response.get(i)));
            }
        }
        boolean needDB = entities.size() > 0;
        if (needDB) {
            OrderDao orderDao = DBInterface.instance().openWritableDb().getOrderDataDao();
            orderDao.insertOrReplaceInTx(entities);
        }
        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderDataUpdated);
    }

}