package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.core.ImageReceiver;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.viewholder.PaddingDividerItemDecoration;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ADProductEntity;
import com.romens.yjk.health.model.ADProductListEntity;
import com.romens.yjk.health.ui.components.MoreButton;

/**
 * Created by siery on 15/8/14.
 */

public class ADProductsCell extends LinearLayout {
    private static final int DEFAULT_BG = 0xffefefef;
    private int count = 3;
    private TextView nameView;
    private TextView descView;
    private MoreButton moreButton;

    private FrameLayout content;
    private BackupImageView backupImageView;
    private RecyclerView recyclerView;

    private int cellStyle = 0;

    private ADProductsCellDelegate cellDelegate;

    public ADProductsCell(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(DEFAULT_BG);
        FrameLayout top = new FrameLayout(context);
        top.setBackgroundResource(R.drawable.list_selector);
        top.setClickable(true);
        top.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cellDelegate != null) {
                    cellDelegate.onMoreButtonClick();
                }
            }
        });
        //用来装载带有更多Button的那一行布局
        addView(top, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 56));
        nameView = new TextView(context);
        nameView.setTextColor(0xff212121);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        nameView.setLines(1);
        nameView.setMaxLines(1);
        nameView.setSingleLine(true);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);

        top.addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 24, 8, 72, 8));

        descView = new TextView(context);
        descView.setTextColor(0x8a000000);
        descView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        descView.setLines(1);
        descView.setMaxLines(1);
        descView.setSingleLine(true);
        descView.setEllipsize(TextUtils.TruncateAt.END);
        descView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);

        top.addView(descView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 24, 32, 72, 8));


        moreButton = new MoreButton(context);
        moreButton.setButtonColor(0xffff5722);
        moreButton.setTextColor(0xffffffff);
        moreButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        moreButton.setLines(1);
        moreButton.setMaxLines(1);
        moreButton.setSingleLine(true);
        moreButton.setText("更多");
        moreButton.setGravity(Gravity.CENTER);

        top.addView(moreButton, LayoutHelper.createFrame(48, 32, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 8, 8, 16, 8));

        content = new FrameLayout(context);
        addView(content, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        backupImageView = new BackupImageView(context);
        content.addView(backupImageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        //recyclerView的GridView样式的布局
        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), count));
        recyclerView.setHasFixedSize(true);
        PaddingDividerItemDecoration itemDecoration = new PaddingDividerItemDecoration(AndroidUtilities.dp(4));
        itemDecoration.setOrientation(3);
        recyclerView.addItemDecoration(itemDecoration);
        content.addView(recyclerView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.BOTTOM, 8, 8, 8, 8));
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

        nameView.setText(entity.name);
        descView.setText(entity.desc);

        String adUrl = entity.adBackground;
        if (TextUtils.isEmpty(adUrl)) {
            changeCellColor(0xffff5722, DEFAULT_BG);
            backupImageView.setImageBitmap(null);
        } else if (adUrl.startsWith("http")) {
            changeCellColor(0xffff5722, DEFAULT_BG);
            backupImageView.setImage(adUrl, null, null);
        } else if (adUrl.startsWith("#")) {
            int color = Color.parseColor(adUrl);
            changeCellColor(color, color);
            backupImageView.setImageBitmap(null);
        } else {
            changeCellColor(0xffff5722, DEFAULT_BG);
            backupImageView.setImageBitmap(null);
        }
        ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(count);
        ProductAdapter adapter = new ProductAdapter(entity);
        adapter.setProductCellDelegate(new ProductCell.ProductCellDelegate() {
            @Override
            public void onCellClick() {
                if (cellDelegate != null) {
                    cellDelegate.onChildClick();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        invalidate();
    }

    private void changeCellColor(int moreButtonColor, int bgColor) {
        moreButton.setButtonColor(moreButtonColor);
        content.setBackgroundColor(bgColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = measureWidth / count + AndroidUtilities.dp(32) + count * AndroidUtilities.dp(16);
        int count = getChildCount();

        for (int index = 0; index < count; index++) {
            final View child = getChildAt(index);
            if (child.getVisibility() != GONE) {
                if (child instanceof FrameLayout) {
                    measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(measureHeight, MeasureSpec.EXACTLY));
                }
            }
        }
        setMeasuredDimension(measureWidth, measureHeight + AndroidUtilities.dp(8) + AndroidUtilities.dp(48));
    }

    class ProductAdapter extends RecyclerView.Adapter<CellHolder> {
        private ADProductListEntity adProductListEntity;
        private int emptySize = 0;
        private ProductCell.ProductCellDelegate productCellDelegate;

        public ProductAdapter(ADProductListEntity entity) {
            adProductListEntity = entity;
            final int dataSize = adProductListEntity == null ? 0 : adProductListEntity.size();
            emptySize = count - dataSize;
        }

        public void setProductCellDelegate(ProductCell.ProductCellDelegate delegate) {
            productCellDelegate = delegate;
        }

        @Override
        public CellHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                ProductCell cell = new ProductCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new CellHolder(cell);
            } else if (viewType == 1) {
                ProductEmptyCell cell = new ProductEmptyCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new CellHolder(cell);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(CellHolder holder, int position) {
            final int viewType = getItemViewType(position);
            if (viewType == 0) {
                int index = cellStyle == 2 ? (position - emptySize) : position;
                ADProductEntity entity = adProductListEntity.get(index);
                ProductCell cell = (ProductCell) holder.itemView;
                cell.setProductCellDelegate(productCellDelegate);
                cell.setLayoutStyle(cellStyle == 1 ? ProductCell.LayoutStyle.DEFAULT : ProductCell.LayoutStyle.SMALL);
                cell.setValue(entity.icon, entity.name, entity.price, entity.price);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (cellStyle == 2) {
                if (emptySize > 0 && position < emptySize) {
                    return 1;
                }
            } else {
                int dataSize = adProductListEntity.size();
                if (emptySize > 0 && position >= dataSize) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int getItemCount() {
            return count;
        }
    }

    class CellHolder extends RecyclerView.ViewHolder {

        public CellHolder(View itemView) {
            super(itemView);
        }
    }

    public void setADProductsCellDelegate(ADProductsCellDelegate delegate) {
        cellDelegate = delegate;
    }

    public interface ADProductsCellDelegate {
        void onMoreButtonClick();

        void onChildClick();
    }

}
