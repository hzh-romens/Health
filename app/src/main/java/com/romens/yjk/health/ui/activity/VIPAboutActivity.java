package com.romens.yjk.health.ui.activity;

import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;

/**
 * @author Zhou Lisi
 * @create 16/4/18
 * @description
 */
public class VIPAboutActivity extends AboutActivity {

    @Override
    protected String onCreateURL(){
        return String.format("%sSystem?user=%s", FacadeConfig.getUrl(), FacadeToken.getInstance().getAuthToken());
    }

    @Override
    protected String onCreateActionBarTitle(){
        return "会员权益";
    }
}
