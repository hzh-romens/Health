package com.romens.yjk.health.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ChoiceEntity;
import com.romens.yjk.health.model.PersonalInformationEntity;

import org.w3c.dom.Text;

import java.util.List;

import widget.OnWheelChangedListener;
import widget.WheelView;
import widget.adapters.ArrayWheelAdapter;

/**
 * Created by HZH on 2015/10/24.
 */
public class PersonalInformationAdapter extends BaseAdapter implements OnWheelChangedListener {
    private List<PersonalInformationEntity> mResult;
    private Context mContext;
   public PersonalInformationAdapter(List<PersonalInformationEntity> result,Context context){
        this.mContext=context;
        this.mResult=result;
   }
    @Override
    public int getCount() {
        return mResult==null?0:mResult.size();
    }

    @Override
    public Object getItem(int position) {
        return mResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(getItemViewType(position)==0){
                convertView=View.inflate(mContext, R.layout.list_item_personal_parent,null);
                TextView tv_parent= (TextView) convertView.findViewById(R.id.name);
                tv_parent.setText(mResult.get(position).getTitleName());

        } else if(getItemViewType(position)==1||getItemViewType(position)==2){
                convertView=View.inflate(mContext,R.layout.list_item_personal_child,null);
                TextView tv_child= (TextView) convertView.findViewById(R.id.childName);
                final EditText editor= (EditText) convertView.findViewById(R.id.editor);
                tv_child.setText(mResult.get(position).getTitleName());
            //EditText可编辑和不可编辑
                 if (mResult.get(position).getType()==1){
                    editor.setEnabled(true);
                     editor.setFocusable(true);
                 }else if(mResult.get(position).getType()==2){
                     editor.setEnabled(true);
                     editor.setFocusable(false);
                     editor.setCompoundDrawables(null, null, mContext.getResources().getDrawable(R.drawable.ic_go), null);
                     editor.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Log.d("点击了----","点击");
                             //打开popwindow
                             if("性别".equals(mResult.get(position).getTitleName())){
                                 String[] data=new String[2];
                                 data[0]="男";
                                 data[1]="女";
                                getPopWindowInstance(data,editor);
                             }else{
                                 String[] data=new String[2];
                                 data[0]="有";
                                 data[1]="无";
                                 getPopWindowInstance(data,editor);
                             }
                         }
                     });
                 }
        }else if(getItemViewType(position)==3){
                convertView=View.inflate(mContext,R.layout.list_item_surebutton,null);
                 LinearLayout btn_save= (LinearLayout) convertView.findViewById(R.id.btn_save);
                //保存个人信息
                btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return mResult.get(position).getType();
    }





    // 获取PopWindow实例 保持一个实例
    private void getPopWindowInstance(String[] data,EditText showAsView) {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopWindow(data,showAsView);
        }
    }

    private PopupWindow mPopupWindow;
    private int mScreenwidth;
    private int mScreenHeight;

    // 创建PopupWindow
    @SuppressWarnings("deprecation")
    private List<ChoiceEntity> choiceDatas;

    private void initPopWindow(final String[] data, final EditText showAsView) {
        mScreenwidth = ((Activity)mContext).getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = ((Activity)mContext).getWindowManager().getDefaultDisplay().getHeight();
        // 创建一个PopupWindow 并设置宽高
        // 参数1：contentView 指定PopupWindow的内容
        // 参数2：width 指定PopupWindow的width
        // 参数3：height 指定PopupWindow的height
       View view= View.inflate(mContext,R.layout.popwindows_selector,null);
        TextView popwindow_close= (TextView) view.findViewById(R.id.popwindow_close);
        TextView popwindow_complete= (TextView) view.findViewById(R.id.popwindow_complete);
        final WheelView wheelView= (WheelView) view.findViewById(R.id.wheelview);
        wheelView.addChangingListener(this);
        wheelView.setBackgroundColor(Color.WHITE);
        wheelView.setViewAdapter(new ArrayWheelAdapter<String>(mContext, data));
        wheelView.setVisibleItems(3);
        wheelView.setCurrentItem(0);
        mPopupWindow =new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.showAtLocation(showAsView, Gravity.BOTTOM, 0, 0);
        // #e0000000  半透明颜色
        mPopupWindow.setAnimationStyle(R.anim.choose_citys_anim);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(false);
        popwindow_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                mPopupWindow=null;
            }
        });
        popwindow_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectInfo = data[wheelView.getCurrentItem()];
                showAsView.setText(selectInfo);
                mPopupWindow.setAnimationStyle(R.anim.choose_citys_anim_exit);
                mPopupWindow.dismiss();
                mPopupWindow=null;
            }
        });

    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {

    }
}
