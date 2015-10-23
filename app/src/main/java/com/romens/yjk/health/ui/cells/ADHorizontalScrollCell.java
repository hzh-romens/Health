package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.romens.android.io.image.ImageManager;
import com.romens.android.io.image.ImageUtils;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.helper.ImageHelper;
import com.romens.yjk.health.model.GoodSpicsEntity;
import java.util.List;

/**
 * Created by AUSU on 2015/10/15.
 */
public class ADHorizontalScrollCell extends FrameLayout{
    private LinearLayout gallery;
    private List<GoodSpicsEntity> mResult;
    public ADHorizontalScrollCell(Context context,List<GoodSpicsEntity> result){
        super(context);
        View view= View.inflate(context, R.layout.list_item_horizontalscroll,null);
        gallery= (LinearLayout) view.findViewById(R.id.id_gallery);
        if(result!=null) {
            for (int i = 0; i <result.size(); i++) {
                ImageView iv=new ImageView(context);
                iv.setPadding(5,5,5,5);
                Drawable defaultDrawables = iv.getDrawable();
                ImageManager.loadForView(context,iv,result.get(i).getURL(), defaultDrawables, defaultDrawables);
                gallery.addView(iv);
            }
        }
        addView(view, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT,LayoutHelper.MATCH_PARENT));
    }
    public void setEntity(List<GoodSpicsEntity> result){
        this.mResult=result;
    }
}
