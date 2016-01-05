package com.romens.yjk.health.web;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.romens.yjk.health.helper.UIOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siery on 15/9/9.
 */
public class ADWebJsInterface extends JsBaseInterface {
    private Handler handler = new Handler();

    public ADWebJsInterface(Context context) {
        super(context);
    }

    @JavascriptInterface
    public void openGoods(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String id = jsonObject.getString("ID");
            if (!TextUtils.isEmpty(id)) {
                UIOpenHelper.openMedicineActivity(context, id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJavascript() {
        handler.post(new Runnable() {
            public void run() {
                if (webView != null) {
                    String jsName = onCreateJsName();
                    //webView.loadUrl(String.format("javascript:insertJson(%s.query())", jsName));
                }
            }
        });
    }

    @Override
    protected String onCreateJsName() {
        return "Mobile";
    }
}
