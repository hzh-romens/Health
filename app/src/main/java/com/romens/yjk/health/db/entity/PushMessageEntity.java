package com.romens.yjk.health.db.entity;

import android.provider.CalendarContract;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.io.json.JacksonMapper;
import com.romens.android.log.FileLog;
import com.romens.android.time.FastDateFormat;

import java.io.IOException;
import java.util.Calendar;

/**
 * @author Zhou Lisi
 * @create 2016-04-13 22:20
 * @description
 */
public class PushMessageEntity {

    private Long id;
    private Long create;
    private String title;
    private String content;
    private String extras;
    private Long messageId;
    private int state;

    public PushMessageEntity() {

    }

    public PushMessageEntity(String title, String content, String extras, Long messageId, int state) {
        this.create = Calendar.getInstance().getTimeInMillis();
        this.title = title;
        this.content = content;
        this.extras = extras;
        this.messageId = messageId;
        this.state = state;
    }

    public void setMessageId(Long id) {
        this.messageId = id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreate(Long create) {
        this.create = create;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getId() {
        return id;
    }

    public Long getCreate() {
        return create;
    }

    public String getTitle() {
        return title;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public boolean unRead() {
        return state == 1;
    }

    public void setRead() {
        state = 0;
    }

    public String getTime() {
        Calendar today = Calendar.getInstance();
        Calendar createDate = Calendar.getInstance();
        createDate.setTimeInMillis(create);
        boolean isToday = false;
        if (today.get(Calendar.YEAR) == createDate.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == createDate.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == createDate.get(Calendar.DAY_OF_MONTH)) {
            isToday = true;
        }

        String timeText = FastDateFormat.getInstance(isToday ? "HH:mm" : "yyyy/MM/dd").format(create);
        return timeText;
    }

    public String getContent() {
        return content;
    }

    public String getExtras() {
        return extras;
    }

    public JsonNode formatExtras() {
        return formatExtras(extras);
    }

    public static JsonNode formatExtras(String extras) {
        JsonNode jsonNode = null;
        if (!TextUtils.isEmpty(extras)) {
            try {
                jsonNode = JacksonMapper.getInstance().readTree(extras);
            } catch (IOException e) {
                FileLog.e(e);
            }
        }
        return jsonNode;
    }
}
