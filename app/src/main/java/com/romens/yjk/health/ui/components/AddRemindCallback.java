package com.romens.yjk.health.ui.components;

import java.util.List;

/**
 * Created by anlc on 2015/8/28.
 */
public interface AddRemindCallback {
    void setEditUserText(String str);
    void setCountsText(int oldTimes);
    void setTimesHintText(int oldTimes);
    void setTimesDate(List<String> timesData);
}
