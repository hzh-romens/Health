package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ADImageEntity;
import com.romens.yjk.health.model.ADImageListEntity;
import com.romens.yjk.health.model.DrugStoryDetailEntity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADImagesCell;
import com.romens.yjk.health.ui.cells.DrugStroyBottmImgView;
import com.romens.yjk.health.ui.cells.DrugStroyImageCell;
import com.romens.yjk.health.ui.cells.MenuBarCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/10/19.
 */
public class DrugStoryDetailAdapter extends RecyclerView.Adapter<ADHolder> {

    private Context context;
    private List<Map<String, Object>> entities;
    private List<View> bottomViewList;

    public DrugStoryDetailAdapter(Context context, List<Map<String, Object>> entities) {
        this.context = context;
        this.entities = entities;
        bottomViewList = new ArrayList<>();
    }

    @Override
    public ADHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_drugstory_title, null);
            return new ADHolder(view);
        } else if (viewType == 1) {
            ADImagesCell cell = new ADImagesCell(context);
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new ADHolder(cell);
        } else if (viewType == 2) {
            MenuBarCell cell = new MenuBarCell(context);
//            View view = LayoutInflater.from(context).inflate(R.layout.list_item_menu_bar, null);
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new ADHolder(cell);
        } else if (viewType == 3) {
//            bottomViewList.add(new DrugStroyImageCell(context));
            DrugStroyBottmImgView bottomImgView = new DrugStroyBottmImgView(context);
//            bottomImgView.setData(bottomViewList);
            return new ADHolder(bottomImgView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ADHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == 0) {
            View titleView = holder.itemView;
        } else if (type == 1) {
            ADImageListEntity entity = (ADImageListEntity) entities.get(position).get("adImageListEntity");
            ADImagesCell cell = (ADImagesCell) holder.itemView;
            cell.setValue(entity);
        } else if (type == 2) {
            MenuBarCell cell = (MenuBarCell) holder.itemView;
            cell.setMenuInfo("全部商品", "最新商品", "店铺动态", "", true);
            cell.hideFourView();
        } else if (type == 3) {
            bottomViewList.add(new DrugStroyImageCell(context));
            DrugStroyBottmImgView bottomImgView = (DrugStroyBottmImgView) holder.itemView;
            bottomImgView.setData(bottomViewList);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        } else if (position == 2) {
            return 2;
        }
        return 3;
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }
}
