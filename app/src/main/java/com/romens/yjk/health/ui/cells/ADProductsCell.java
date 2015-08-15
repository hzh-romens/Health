package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.viewholder.PaddingDividerItemDecoration;

/**
 * Created by siery on 15/8/14.
 */
public class ADProductsCell extends FrameLayout {

    private int count=3;
    private RecyclerView recyclerView;

    public ADProductsCell(Context context) {
        super(context);
        setBackgroundColor(0xffe5e5e5);
        setPadding(AndroidUtilities.dp(8),AndroidUtilities.dp(8),AndroidUtilities.dp(8),AndroidUtilities.dp(8));
        recyclerView=new RecyclerView(context);
        recyclerView.setLayoutManager(new GridLayoutManager(context,count));
        PaddingDividerItemDecoration itemDecoration=new PaddingDividerItemDecoration(AndroidUtilities.dp(4));
        itemDecoration.setOrientation(3);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(new ProductAdapter());
        addView(recyclerView,LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,LayoutHelper.WRAP_CONTENT,Gravity.CENTER));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = measureWidth/count+AndroidUtilities.dp(32)+count*AndroidUtilities.dp(16) ;
        int count = getChildCount();

        for (int index = 0; index < count; index++) {
            final View child = getChildAt(index);
            if (child.getVisibility() != GONE) {
                measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth-getPaddingLeft()-getPaddingRight(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(measureHeight-getPaddingTop()-getPaddingBottom(), MeasureSpec.EXACTLY));
            }
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    class ProductAdapter extends RecyclerView.Adapter<CellHolder>{

        @Override
        public CellHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ProductCell cell=new ProductCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT,LayoutHelper.WRAP_CONTENT));
            return new CellHolder(cell);
        }

        @Override
        public void onBindViewHolder(CellHolder holder, int position) {
            ProductCell cell=(ProductCell)holder.itemView;
            cell.setValue("胃药胶囊", "http://img.yy960.com/2013/03/20130326132534.JPG");
        }

        @Override
        public int getItemCount() {
            return count;
        }
    }

    class CellHolder extends RecyclerView.ViewHolder{

        public CellHolder(View itemView) {
            super(itemView);
        }
    }

}
