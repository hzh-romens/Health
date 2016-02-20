package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2016/2/18.
 */
public class GridViewCell extends FrameLayout {

    private GridView gridView;
    private Context context;

    public GridViewCell(Context context) {
        super(context);
        gridView = new MyGridView(context);
        this.context = context;
        addView(gridView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    public void setData(List<Map<String, Object>> personControlList) {
        gridView.setNumColumns(3);
        gridView.setAdapter(new SimpleAdapter(context, personControlList, R.layout.list_item_attach,
                new String[]{"icon", "name"}, new int[]{R.id.grid_img, R.id.grid_txt}));
    }

    public GridView getGridView() {
        return gridView;
    }

    class MyGridView extends GridView {

        private Paint paint = new Paint();

        public MyGridView(Context context) {
            super(context);
            paint.setColor(0xffdcdcdc);
        }

        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int childCount = getChildCount();

            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                float x = child.getWidth() + child.getX();
                float y = child.getHeight() + child.getY();
                canvas.drawLine(child.getX(),
                        y,
                        child.getX() + child.getWidth(),
                        y,
                        paint);
                canvas.drawLine(x,
                        child.getY(),
                        x,
                        child.getY() + child.getHeight(),
                        paint);
            }
        }
    }
}
