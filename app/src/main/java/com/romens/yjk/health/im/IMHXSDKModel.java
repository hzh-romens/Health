/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.romens.yjk.health.im;

import com.easemob.applib.model.DefaultHXSDKModel;
import com.romens.yjk.health.MyApplication;

public class IMHXSDKModel extends DefaultHXSDKModel {

    public IMHXSDKModel() {
        super();
    }

    // demo will not use HuanXin roster
    @Override
    public boolean getUseHXRoster() {
        return false;
    }

    // demo will switch on debug mode
    @Override
    public boolean isDebugMode() {
        return false;
    }

    @Override
    public boolean isSandboxMode() {
        return false;
    }


//    public boolean saveContactList(List<User> contactList) {
//        UserDao dao = new UserDao(context);
//        dao.saveContactList(contactList);
//        return true;
//    }
//
//    public Map<String, User> getContactList() {
//        UserDao dao = new UserDao(context);
//        return dao.getContactList();
//    }
//
//    public void closeDB() {
//        DemoDBManager.getInstance().closeDB();
//    }

    @Override
    public String getAppProcessName() {
        return MyApplication.applicationContext.getPackageName();
    }
}
