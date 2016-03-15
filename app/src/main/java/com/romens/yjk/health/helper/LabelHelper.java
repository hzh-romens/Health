package com.romens.yjk.health.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.yjk.health.R;

import java.util.List;

/**
 * Created by siery on 15/12/19.
 */
public class LabelHelper {
    public static class XImageSpan extends ImageSpan {
        public int uid;

        public XImageSpan(Drawable d, int verticalAlignment) {
            super(d, verticalAlignment);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            if (fm == null) {
                fm = new Paint.FontMetricsInt();
            }

            int sz = super.getSize(paint, text, start, end, fm);
            int offset = AndroidUtilities.dp(6);
            int w = (fm.bottom - fm.top) / 2;
            fm.top = -w - offset;
            fm.bottom = w - offset;
            fm.ascent = -w - offset;
            fm.leading = 0;
            fm.descent = w - offset;
            return sz;
        }
    }

    public static SpannableStringBuilder createChipForUserInfoLabels(List<String> labels) {
        SpannableStringBuilder ssb = new SpannableStringBuilder("");
        if (labels == null || labels.size() <= 0) {
            ssb.append("");
            return ssb;
        }
        XImageSpan span;
        for (String label : labels) {
            span = createImageSpanForUserInfoLabel(label);
            ssb.append("<<");
            ssb.setSpan(span, ssb.length() - 2, ssb.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" ");
        }
        return ssb;
    }

    public static SpannableStringBuilder createChipForUserInfoLabel(String label) {
        SpannableStringBuilder ssb = new SpannableStringBuilder("");
        XImageSpan span = createImageSpanForUserInfoLabel(label);
        ssb.append("<<");
        ssb.setSpan(span, ssb.length() - 2, ssb.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    public static XImageSpan createImageSpanForUserInfoLabel(String label) {
        LayoutInflater lf = (LayoutInflater) ApplicationLoader.applicationContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View textView = lf.inflate(R.layout.user_info_create_bubble, null);
        TextView text = (TextView) textView.findViewById(R.id.bubble_text_view);
        text.setText(label);

        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(spec, spec);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(textView.getWidth(), textView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.translate(-textView.getScrollX(), -textView.getScrollY());
        textView.draw(canvas);
        textView.setDrawingCacheEnabled(true);
//        Bitmap cacheBmp = textView.getDrawingCache();
//        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        textView.destroyDrawingCache();

        final BitmapDrawable bmpDrawable = new BitmapDrawable(b);
        bmpDrawable.setBounds(0, 0, b.getWidth(), b.getHeight());
        XImageSpan span = new XImageSpan(bmpDrawable, ImageSpan.ALIGN_BASELINE);
        return span;
    }
}
