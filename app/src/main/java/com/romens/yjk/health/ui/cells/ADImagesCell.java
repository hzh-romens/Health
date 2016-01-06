package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.model.ADImageEntity;
import com.romens.yjk.health.model.ADImageListEntity;

/**
 * Created by siery on 15/9/8.
 */
public class ADImagesCell extends FrameLayout {
    private CloudImageView imageView1;
    private CloudImageView imageView2;
    private CloudImageView imageView3;
    private CloudImageView imageView4;


    private ADImageListEntity adImageListEntity;
    private ADImageCellDelegate cellDelegate;

    /**
     * 0
     * --------------
     * -   1234     -
     * --------------
     * 1
     * --------------
     * - 13  -  24  -
     * --------------
     * 2
     * --------------
     * -  1 -       -
     * ------   24  -
     * -  3 -       -
     * --------------
     * 3
     * --------------
     * -    -   2   -
     * - 13 ---------
     * -    -   4   -
     * --------------
     * 4
     * --------------
     * - 1  -   2   -
     * --------------
     * - 3  -   4   -
     * --------------
     */
    private int layoutStyle;

    public ADImagesCell(Context context) {
        super(context);
        layoutStyle = 4;
        imageView1 = CloudImageView.create(context);
        addView(imageView1, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        imageView2 = CloudImageView.create(context);
        addView(imageView2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        imageView3 = CloudImageView.create(context);
        addView(imageView3, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        imageView4 = CloudImageView.create(context);
        addView(imageView4, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        imageView1.setClickable(true);
        imageView2.setClickable(true);
        imageView3.setClickable(true);
        imageView4.setClickable(true);

        imageView1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView1.getVisibility() == GONE) {
                    return;
                }
                String key;
                if (layoutStyle == 0) {
                    key = "1234";
                } else if (layoutStyle == 1 || layoutStyle == 3) {
                    key = "13";
                } else {
                    key = "1";
                }
                onImageCellClick(key);
            }
        });
        imageView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView2.getVisibility() == GONE) {
                    return;
                }
                String key;
                if (layoutStyle == 1 || layoutStyle == 2) {
                    key = "24";
                } else {
                    key = "2";
                }
                onImageCellClick(key);
            }
        });
        imageView3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView3.getVisibility() == GONE) {
                    return;
                }
                onImageCellClick("3");
            }
        });
        imageView4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView4.getVisibility() == GONE) {
                    return;
                }
                onImageCellClick("4");
            }
        });
    }

    private void onImageCellClick(String key) {
        if (adImageListEntity != null && cellDelegate != null) {
            cellDelegate.onImageClick(adImageListEntity.get(key));
        }
    }

    public void setValue(ADImageListEntity entity) {
        adImageListEntity = entity;
        if (adImageListEntity.imageEntities.containsKey("1234")) {
            layoutStyle = 0;
        } else if (adImageListEntity.imageEntities.containsKey("13") && adImageListEntity.imageEntities.containsKey("24")) {
            layoutStyle = 1;
        } else if (adImageListEntity.imageEntities.containsKey("24") && adImageListEntity.imageEntities.containsKey("1")) {
            layoutStyle = 2;
        } else if (adImageListEntity.imageEntities.containsKey("13") && adImageListEntity.imageEntities.containsKey("2")) {
            layoutStyle = 3;
        } else {
            layoutStyle = 4;
        }
        switch (layoutStyle) {
            case 0:
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.GONE);
                imageView3.setVisibility(View.GONE);
                imageView4.setVisibility(View.GONE);

                imageView1.setImagePath(adImageListEntity.get("1234").iconValue);
                //imageView1.setRoundRadius(1);
                imageView2.setPlaceholderImage(null);
                imageView3.setPlaceholderImage(null);
                imageView4.setPlaceholderImage(null);

                break;
            case 1:
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.GONE);
                imageView4.setVisibility(View.GONE);

                imageView1.setImagePath(adImageListEntity.get("13").iconValue);
                //imageView1.setRoundRadius(4);
                imageView2.setImagePath(adImageListEntity.get("24").iconValue);
                //imageView2.setRoundRadius(4);
                imageView3.setPlaceholderImage(null);
                imageView4.setPlaceholderImage(null);

                break;
            case 2:
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.VISIBLE);
                imageView4.setVisibility(View.GONE);

                imageView1.setImagePath(adImageListEntity.get("1").iconValue);
                //imageView1.setRoundRadius(4);
                imageView2.setImagePath(adImageListEntity.get("24").iconValue);
                //imageView2.setRoundRadius(4);
                imageView3.setImagePath(adImageListEntity.get("3").iconValue);
                //imageView3.setRoundRadius(4);
                imageView4.setPlaceholderImage(null);
                break;
            case 3:
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.GONE);
                imageView4.setVisibility(View.VISIBLE);

                imageView1.setImagePath(adImageListEntity.get("13").iconValue);
                //imageView1.setRoundRadius(4);
                imageView2.setImagePath(adImageListEntity.get("2").iconValue);
                //imageView2.setRoundRadius(4);
                imageView3.setPlaceholderImage(null);
                imageView4.setImagePath(adImageListEntity.get("4").iconValue);
                //imageView4.setRoundRadius(4);

                break;
            case 4:
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.VISIBLE);
                imageView4.setVisibility(View.VISIBLE);

                imageView1.setImagePath(adImageListEntity.get("1").iconValue);
                //imageView1.setRoundRadius(4);
                imageView2.setImagePath(adImageListEntity.get("2").iconValue);
                //imageView2.setRoundRadius(4);
                imageView3.setImagePath(adImageListEntity.get("3").iconValue);
                //imageView3.setRoundRadius(4);
                imageView4.setImagePath(adImageListEntity.get("4").iconValue);
                //imageView4.setRoundRadius(4);
                break;
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = layoutStyle == 0 ? (measureWidth / 3) : (measureWidth / 2);
        int count = getChildCount();

        int childWidth = 0;
        int childHeight = 0;
        for (int index = 0; index < count; index++) {
            final View child = getChildAt(index);
            if (layoutStyle == 0) {
                if (index == 0) {
                    childWidth = measureWidth;
                    childHeight = measureHeight;
                } else {
                    childWidth = 0;
                    childHeight = 0;
                }
            } else if (layoutStyle == 1) {
                if (index == 0 || index == 1) {
                    childWidth = measureWidth / 1 - AndroidUtilities.dp(20);
                    childHeight = measureHeight - AndroidUtilities.dp(16);
                } else {
                    childWidth = 0;
                    childHeight = 0;
                }
            } else if (layoutStyle == 2) {
                if (index == 0 || index == 2) {
                    childWidth = measureWidth / 1 - AndroidUtilities.dp(20);
                    childHeight = measureHeight - AndroidUtilities.dp(10);
                } else if (index == 1) {
                    childWidth = measureWidth / 1 - AndroidUtilities.dp(20);
                    childHeight = measureHeight - AndroidUtilities.dp(16);
                } else {
                    childWidth = 0;
                    childHeight = 0;
                }
            } else if (layoutStyle == 3) {
                if (index == 0) {
                    childWidth = measureWidth / 1 - AndroidUtilities.dp(20);
                    childHeight = measureHeight - AndroidUtilities.dp(16);
                } else if (index == 1 || index == 3) {
                    childWidth = measureWidth / 1 - AndroidUtilities.dp(20);
                    childHeight = measureHeight - AndroidUtilities.dp(10);
                } else {
                    childWidth = 0;
                    childHeight = 0;
                }
            } else if (layoutStyle == 4) {
                childWidth = measureWidth / 1 - AndroidUtilities.dp(20);
                childHeight = measureHeight - AndroidUtilities.dp(10);
            }
            measureChild(child, MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        final int totalWidth = getMeasuredWidth();
        final int totalHeight = getMeasuredHeight();
        final int count = getChildCount();

        int cl = 0;
        int ct = 0;
        int cr = 0;
        int cb = 0;

        final int leftPadding = AndroidUtilities.dp(8);
        final int topPadding = AndroidUtilities.dp(4);
        final int middlePadding = AndroidUtilities.dp(4);

        for (int index = 0; index < count; index++) {
            View child = getChildAt(index);
            if (layoutStyle == 0) {
                if (index == 0) {
                    cl = leftPadding;
                    ct = topPadding;
                    cr = totalWidth - leftPadding;
                    cb = totalHeight - topPadding;
                } else {
                    cl = 0;
                    ct = 0;
                    cr = 0;
                    cb = 0;
                }
            } else if (layoutStyle == 1) {
                if (index == 0) {
                    cl = leftPadding;
                    ct = topPadding;
                    cr = totalWidth / 2 - (middlePadding / 2);
                    cb = totalHeight - topPadding;
                } else if (index == 1) {
                    cl = totalWidth / 2 + (middlePadding / 2);
                    ct = topPadding;
                    cr = totalWidth - leftPadding;
                    cb = totalHeight - topPadding;
                } else {
                    cl = 0;
                    ct = 0;
                    cr = 0;
                    cb = 0;
                }
            } else if (layoutStyle == 2) {
                if (index == 0) {
                    cl = leftPadding;
                    ct = topPadding;
                    cr = totalWidth / 2 - (middlePadding / 2);
                    cb = totalHeight / 2 - (middlePadding / 2);
                } else if (index == 2) {
                    cl = leftPadding;
                    ct = totalHeight / 2 + (middlePadding / 2);
                    cr = totalWidth / 2 - (middlePadding / 2);
                    cb = totalHeight - topPadding;
                } else if (index == 1) {
                    cl = totalWidth / 2 + (middlePadding / 2);
                    ct = topPadding;
                    cr = totalWidth - leftPadding;
                    cb = totalHeight - topPadding;
                } else {
                    cl = 0;
                    ct = 0;
                    cr = 0;
                    cb = 0;
                }
            } else if (layoutStyle == 3) {
                if (index == 0) {
                    cl = leftPadding;
                    ct = topPadding;
                    cr = totalWidth / 2 - (middlePadding / 2);
                    cb = totalHeight - topPadding;
                } else if (index == 1) {
                    cl = totalWidth / 2 + (middlePadding / 2);
                    ct = topPadding;
                    cr = totalWidth - leftPadding;
                    cb = totalHeight / 2 - (middlePadding / 2);
                } else if (index == 3) {
                    cl = totalWidth / 2 + (middlePadding / 2);
                    ct = totalHeight / 2 + (middlePadding / 2);
                    cr = totalWidth - leftPadding;
                    cb = totalHeight - topPadding;
                } else {
                    cl = 0;
                    ct = 0;
                    cr = 0;
                    cb = 0;
                }
            } else if (layoutStyle == 4) {
                if (index == 0) {
                    cl = leftPadding;
                    ct = topPadding;
                    cr = totalWidth / 2 - (middlePadding / 2);
                    cb = totalHeight / 2 - (middlePadding / 2);
                } else if (index == 1) {
                    cl = totalWidth / 2 + (middlePadding / 2);
                    ct = topPadding;
                    cr = totalWidth - leftPadding;
                    cb = totalHeight / 2 - (middlePadding / 2);
                } else if (index == 2) {
                    cl = leftPadding;
                    ct = totalHeight / 2 + (middlePadding / 2);
                    cr = totalWidth / 2 - (middlePadding / 2);
                    cb = totalHeight - topPadding;
                } else if (index == 3) {
                    cl = totalWidth / 2 + (middlePadding / 2);
                    ct = totalHeight / 2 + (middlePadding / 2);
                    cr = totalWidth - leftPadding;
                    cb = totalHeight - topPadding;
                } else {
                    cl = 0;
                    ct = 0;
                    cr = 0;
                    cb = 0;
                }
            }
            child.layout(cl, ct, cr, cb);
        }
    }

    public void setADImageCellDelegate(ADImageCellDelegate delegate) {
        cellDelegate = delegate;
    }

    public interface ADImageCellDelegate {
        void onImageClick(ADImageEntity entity);
    }
}
