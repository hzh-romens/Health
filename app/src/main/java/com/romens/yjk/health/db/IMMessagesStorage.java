package com.romens.yjk.health.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Pair;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.romens.yjk.health.db.dao.DaoMaster;
import com.romens.yjk.health.db.dao.DaoSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by siery on 15/6/18.
 */
public class IMMessagesStorage {

    private DaoMaster.DevOpenHelper openHelper;
    private Context context = null;
    private String loginUserId = null;

    private static volatile IMMessagesStorage Instance = null;

    public static IMMessagesStorage getInstance() {
        IMMessagesStorage localInstance = Instance;
        if (localInstance == null) {
            synchronized (IMMessagesStorage.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new IMMessagesStorage();
                }
            }
        }
        return localInstance;
    }

    public IMMessagesStorage() {
    }

    public void close() {
        if (openHelper != null) {
            openHelper.close();
            openHelper = null;
            context = null;
            loginUserId = null;
        }
    }


    public void setupDb(Context ctx, String appKey, String loginId) {
        if (ctx == null || TextUtils.isEmpty(appKey) || TextUtils.isEmpty(loginId)) {
            throw new RuntimeException("#DBInterface# init DB exception!");
        }
        close();
        context = ctx;
        loginUserId = loginId;
        String DBName = String.format("im_%s_%s.db", appKey, loginUserId);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx, DBName, null);
        this.openHelper = helper;
    }

    /**
     * Query for readable DB
     */
    private DaoSession openReadableDb() {
        isInitOk();
        SQLiteDatabase db = openHelper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }

    /**
     * Query for writable DB
     */
    private DaoSession openWritableDb() {
        isInitOk();
        SQLiteDatabase db = openHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }


    private boolean isInitOk() {
        if (openHelper == null) {
            //logger.e("DBInterface#isInit not success or start,cause by openHelper is null");
            // 抛出异常 todo
            //throw new RuntimeException("DBInterface#isInit not success or start,cause by openHelper is null");
            return false;
        }
        return true;
    }


    /**
     * -------------------------下面开始message 操作相关---------------------------------------
     */

    // where (msgId >= startMsgId and msgId<=lastMsgId) or
    // (msgId=0 and status = 0)
    // order by created desc
    // limit count;
    // 按照时间排序
    public List<EMConversation> getHistoryMsg(String chatKey, int lastMsgId, int lastCreateTime, int count) {
        /**解决消息重复的问题*/
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        return formatMessage(conversations);
    }

    public List<EMConversation> formatMessage(Hashtable<String, EMConversation> conversations) {
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }
}
