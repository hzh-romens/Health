package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.io.image.ImageManager;
import com.romens.android.io.image.ImageUtils;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by AUSU on 2015/10/14.
 */
public class ADStoreCell extends FrameLayout{
    private ImageView pharmacy_buy;
    private TextView address, price, pharmacy_name, distance;
    private String pharmacyCount,text_address,text_price,text_name,text_distance;
    private LinearLayout ll_main;
    private ItemClickBack mItemClickBack;
    private ItemClickBack2 mItemClickBack2;
    private ImageView imageView;

    public ADStoreCell(Context context) {
        super(context);
        View view=View.inflate(context, R.layout.list_item_medicinal_store,null);
        pharmacy_buy = (ImageView) view.findViewById(R.id.pharmacy_buy);
        address = (TextView) view.findViewById(R.id.address);
        pharmacy_name = (TextView) view.findViewById(R.id.pharmacy_name);
        price = (TextView) view.findViewById(R.id.price);
        distance = (TextView) view.findViewById(R.id.distance);
        ll_main= (LinearLayout) view.findViewById(R.id.ll_main);
        imageView= (ImageView) view.findViewById(R.id.iv);
        //跳转到下一个页面
        ll_main.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickBack!=null){
                    mItemClickBack.ToStoreListener();
                }
            }
        });
        //加入购物车
        pharmacy_buy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickBack2!=null){
                    mItemClickBack2.AddToShopCarListener();
                }
            }
        });
        addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }
    public void setValue(String str_count,String str_addresss,String str_price,String str_name,String str_distance,String img_url){
        address.setText(str_addresss);
        pharmacy_name.setText(str_name);
        price.setText(str_price);
        distance.setText(str_distance);
        //if(img_url!=null) {
//        if("".equals(img_url)||img_url==null){
//        }else {
//            imageView.setImageBitmap(ImageUtils.bindLocalImage(img_url));
//            Drawable defaultDrawables = imageView.getDrawable();
//            ImageManager.loadForView(getContext(), imageView, img_url, defaultDrawables, defaultDrawables);
//        }

    }
    public interface ItemClickBack{
        void ToStoreListener();
    }
    public interface ItemClickBack2{
        void AddToShopCarListener();
    }
    //跳转到下一个页面
    public void ItemClickListener(ItemClickBack itemClickBack){
        this.mItemClickBack=itemClickBack;
    }
    //加入购物车
    public void ItemClickListener2(ItemClickBack2 itemClickBack2){
        this.mItemClickBack2=itemClickBack2;
    }

}
