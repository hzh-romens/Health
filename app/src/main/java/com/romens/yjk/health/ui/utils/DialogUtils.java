package com.romens.yjk.health.ui.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.yjk.health.R;

/**
 * Created by romens007 on 2015/8/27.
 */
public class DialogUtils {
    /**
     * 弹出一个自定义对话框（确定，取消）
     * @num 数量
     * @title 标题名
     */
    private int startNum=0;
    private QuantityOfGoodsCallBack mQuantityOfGoodsCallBack;
    public interface QuantityOfGoodsCallBack{
        void getGoodsNum(int num);
    }
    public void show (String num, final Context owner, String title,QuantityOfGoodsCallBack quantityOfGoodsCallBack) {

        mQuantityOfGoodsCallBack=quantityOfGoodsCallBack;
        View v = LayoutInflater.from(owner).inflate(R.layout.dialog_shop_count, null);
        Button dialog_btnCancel= (Button) v.findViewById(R.id.dialog_btnCancel);
        Button  dialog_btnSure= (Button) v.findViewById(R.id.dialog_btnSure);
        final EditText dialog_editextNum= (EditText) v.findViewById(R.id.dialog_editextNum);
        Button  dialog_btnAdd= (Button) v.findViewById(R.id.dialog_btnAdd);
        Button  dialog_btnReduce= (Button) v.findViewById(R.id.dialog_btnReduce);
        dialog_editextNum.setText(num);

        Dialog dlg = new Dialog(owner, R.style.Base_Theme_AppCompat_Dialog);
        dlg.setCanceledOnTouchOutside(false);//设置用户点击其他区域不关闭
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(v);
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();

        lp.gravity = Gravity.CENTER;
        WindowManager wm = (WindowManager) owner.getSystemService(Context.WINDOW_SERVICE);
        lp.width = wm.getDefaultDisplay().getWidth();
        lp.horizontalMargin=20;
        w.setAttributes(lp);


        dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dlg.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(0x00ffffff));
        dlg.show();
        startNum = Integer.parseInt(dialog_editextNum.getText().toString());
        dialog_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNum++;
                dialog_editextNum.setText(startNum + "");
            }
        });

        dialog_btnReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startNum > 1) {
                    startNum--;
                    dialog_editextNum.setText(startNum + "");
                }
            }
        });

        dialog_btnSure.setTag(dlg);
        dialog_btnCancel.setTag(dlg);
        dialog_btnSure.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dlg = (Dialog ) v.getTag ();
                int i = Integer.parseInt(dialog_editextNum.getText().toString());
                mQuantityOfGoodsCallBack.getGoodsNum(i);
                dlg.cancel();

            }
        });

        dialog_btnCancel.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dlg = (Dialog ) v.getTag ();
                dlg.cancel();
            }
        });
    }


    /**
     * 弹出一个对话框（只有确定）
     * @param infor
     * @param owner
     */
    public void show_infor(String infor, Activity owner,String title) {



        View v = owner.getLayoutInflater().inflate(
                R.layout.dialog_page_standard_infor, null);
        TextView tv = (TextView) v.findViewById(R.id.tv_tips);
        tv.setText(infor);

        if (title != null) {
            TextView tv_title = (TextView) v.findViewById(R.id.tv_title);
            tv_title.setText(title);
        }

        Dialog dlg = new Dialog(owner, R.style.Float_Dialog);
        dlg.setCanceledOnTouchOutside(false);// 设置用户点击其他区域不关闭
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(v);
        dlg.getWindow().setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(0x00ffffff));

        // /< 如果太宽，限定在左右两侧留20dp
        v.measure(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int mWidth = v.getMeasuredWidth();
        int width = owner.getWindowManager().getDefaultDisplay().getWidth();
        if (mWidth >= width - 60) {
            WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
            lp.width = width - 60;
            dlg.getWindow().setAttributes(lp);
        }
        dlg.show();

        View ok = v.findViewById(R.id.btn_ok);

        ok.setTag(dlg);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dlg = (Dialog) v.getTag();
                dlg.cancel();
            }
        });
    }

}
