package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.ADProductEntity;
import com.romens.yjk.health.model.ADProductListEntity;

/**
 * Created by siery on 15/8/14.
 */

public class ADProductsCell extends LinearLayout implements ProductCell.ProductCellDelegate {
    private int count = 3;

    private ADProductGroupCell groupCell;

    private FrameLayout content;
    private BackupImageView backupImageView;
    private LinearLayout dataContainer;

    private int cellStyle = 0;

    private Bundle arguments = new Bundle();

    public ADProductsCell(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);

        groupCell = new ADProductGroupCell(context);
        groupCell.setClickable(true);
        groupCell.setBackgroundResource(R.drawable.list_selector);
        groupCell.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addView(groupCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        content = new FrameLayout(context);
        addView(content, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, (ProductCell.defaultSize +8)));

        backupImageView = new BackupImageView(context);
        content.addView(backupImageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        dataContainer = new LinearLayout(context);
        dataContainer.setOrientation(HORIZONTAL);
        content.addView(dataContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, ProductCell.defaultSize, Gravity.CENTER,0,4,0,4));
//        //recyclerView的GridView样式的布局
//        recyclerView = new RecyclerView(context);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), count));
//        recyclerView.setHasFixedSize(true);
//        PaddingDividerItemDecoration itemDecoration = new PaddingDividerItemDecoration(AndroidUtilities.dp(4));
//        itemDecoration.setOrientation(3);
//        recyclerView.addItemDecoration(itemDecoration);
//        content.addView(recyclerView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.BOTTOM, 8, 8, 8, 8));
    }

    /**
     * 多种风格的RecyclerView
     * cellStyle的值:0,1,2。
     * 1、0:默认的3个CardView
     * 2、1:两个CardView
     * 3、2：
     */
    public void setValue(ADProductListEntity entity) {
        cellStyle = 0;
        count = 3;
        final int size = entity.size();
        final String layoutStyle = entity.getLayoutStyle();
        if (!TextUtils.isEmpty(layoutStyle)) {

            if (size == 2 && TextUtils.equals(layoutStyle, "LARGE")) {
                //只有两张图片
                cellStyle = 1;
                count = 2;
            } else if (size < 3 && TextUtils.equals(layoutStyle, "RIGHT")) {
                cellStyle = 2;
            }
        }

        if (TextUtils.isEmpty(entity.name)) {
            groupCell.setVisibility(View.GONE);
            groupCell.setTextAndValue("", "更多", true, false);
        } else {
            groupCell.setVisibility(View.VISIBLE);
            groupCell.setTextAndValue(entity.name, "更多", true, false);
        }

        String adUrl = entity.adBackground;
        if (TextUtils.isEmpty(adUrl)) {
            changeCellColor(Color.TRANSPARENT);
            backupImageView.setVisibility(INVISIBLE);
            backupImageView.setImageBitmap(null);
        } else if (adUrl.startsWith("http")) {
            changeCellColor(Color.TRANSPARENT);
            backupImageView.setVisibility(VISIBLE);
            backupImageView.setImage(adUrl, null, null);
        } else if (adUrl.startsWith("#")) {
            int color = Color.parseColor(adUrl);
            changeCellColor(color);
            backupImageView.setVisibility(INVISIBLE);
            backupImageView.setImageBitmap(null);
        } else {
            changeCellColor(Color.TRANSPARENT);
            backupImageView.setVisibility(INVISIBLE);
            backupImageView.setImageBitmap(null);
        }

        dataContainer.removeAllViews();
        if (cellStyle == 2) {
            dataContainer.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            int emptyCellCount = count - size;
            for (int i = 0; i < emptyCellCount; i++) {
                addProductEmptyCell(dataContainer);
            }
            for (int i = 0; i < size; i++) {
                addProductCell(dataContainer, entity.get(i));
            }
        } else {
            for (int i = 0; i < size; i++) {
                addProductCell(dataContainer, entity.get(i));
            }
            int emptyCellCount = count - size;
            for (int i = 0; i < emptyCellCount; i++) {
                addProductEmptyCell(dataContainer);
            }
        }
        setWillNotDraw(false);
    }

    private void addProductCell(LinearLayout parent, ADProductEntity entity) {
        ProductCell cell = new ProductCell(getContext());
        //cell.setLayoutStyle(cellStyle == 1 ? ProductCell.LayoutStyle.DEFAULT : ProductCell.LayoutStyle.SMALL);
        cell.setValue(entity.icon, entity.name, entity.oldPrice, entity.price);
        Bundle arguments = new Bundle();
        arguments.putString("ID", entity.id);
        cell.setArguments(arguments);
        cell.setProductCellDelegate(this);
        parent.addView(cell, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 4, 0, 4, 0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        layoutParams.weight = 1;
        cell.setLayoutParams(layoutParams);
    }

    private void addProductEmptyCell(LinearLayout parent) {
        ProductEmptyCell cell = new ProductEmptyCell(getContext());
        parent.addView(cell, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 4, 0, 4, 0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        layoutParams.weight = 1;
        cell.setLayoutParams(layoutParams);
    }

    private void changeCellColor(int bgColor) {
        content.setBackgroundColor(bgColor);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int measureHeight = measureWidth / count + AndroidUtilities.dp(32) + count * AndroidUtilities.dp(16);
//        int count = getChildCount();
//
//        for (int index = 0; index < count; index++) {
//            final View child = getChildAt(index);
//            if (child.getVisibility() != GONE) {
//                if (child instanceof FrameLayout) {
//                    measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY),
//                            MeasureSpec.makeMeasureSpec(measureHeight, MeasureSpec.EXACTLY));
//                }
//            }
//        }
//        setMeasuredDimension(measureWidth, measureHeight + AndroidUtilities.dp(16) + AndroidUtilities.dp(48));
//    }

//    class ProductAdapter extends RecyclerView.Adapter<CellHolder> {
//        private ADProductListEntity adProductListEntity;
//        private int emptySize = 0;
//        private ProductCell.ProductCellDelegate productCellDelegate;
//
//        public ProductAdapter(ADProductListEntity entity) {
//            adProductListEntity = entity;
//            final int dataSize = adProductListEntity == null ? 0 : adProductListEntity.size();
//            emptySize = count - dataSize;
//        }
//
//        public void setProductCellDelegate(ProductCell.ProductCellDelegate delegate) {
//            productCellDelegate = delegate;
//        }
//
//        @Override
//        public CellHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            if (viewType == 0) {
//                ProductCell cell = new ProductCell(parent.getContext());
//                cell.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
//                return new CellHolder(cell);
//            } else if (viewType == 1) {
//                ProductEmptyCell cell = new ProductEmptyCell(parent.getContext());
//                cell.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
//                return new CellHolder(cell);
//            }
//            return null;
//        }
//
//        @Override
//        public void onBindViewHolder(CellHolder holder, int position) {
//            final int viewType = getItemViewType(position);
//            if (viewType == 0) {
//                int index = cellStyle == 2 ? (position - emptySize) : position;
//                ADProductEntity entity = adProductListEntity.get(index);
//                ProductCell cell = (ProductCell) holder.itemView;
//                cell.setProductCellDelegate(productCellDelegate);
//                cell.setLayoutStyle(cellStyle == 1 ? ProductCell.LayoutStyle.DEFAULT : ProductCell.LayoutStyle.SMALL);
//                cell.setValue(entity.icon, entity.name, entity.price, entity.price);
//            }
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            if (cellStyle == 2) {
//                if (emptySize > 0 && position < emptySize) {
//                    return 1;
//                }
//            } else {
//                int dataSize = adProductListEntity.size();
//                if (emptySize > 0 && position >= dataSize) {
//                    return 1;
//                }
//            }
//            return 0;
//        }
//
//        @Override
//        public int getItemCount() {
//            return count;
//        }
//    }
//
//    class CellHolder extends RecyclerView.ViewHolder {
//
//        public CellHolder(View itemView) {
//            super(itemView);
//        }
//    }

    @Override
    public void onCellClick(Bundle arguments) {
        if (arguments != null && arguments.containsKey("ID")) {
            String medicineId = arguments.getString("ID", "");
            UIOpenHelper.openMedicineActivity(getContext(), medicineId);
        }
    }
}
