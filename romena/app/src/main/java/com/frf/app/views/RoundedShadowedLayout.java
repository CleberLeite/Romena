package com.frf.app.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.frf.app.R;
import com.frf.app.utils.UTILS;

public class RoundedShadowedLayout extends FrameLayout {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rect = new RectF();
    private float shadowWidth = UTILS.convertDpToPixels(5);
    private int radius = UTILS.convertDpToPixels(getResources().getDimension(R.dimen._5sdp));
    private Context context;
    private Bitmap mask;

    public RoundedShadowedLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RoundedShadowedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public RoundedShadowedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundedShadowedLayout(
            Context context,
            AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }



    public void setBackgroundColor(int color) {
        paint.setColor(color);
    }

    private void init() {
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        paint.setColor(Color.WHITE);
        paint.setShadowLayer(shadowWidth, 0, 5, Color.parseColor("#350B3575"));
        paint.setStyle(Paint.Style.FILL);

        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.FILL);

        maskPaint.setXfermode(new PorterDuffXfermode((PorterDuff.Mode.DST_IN)));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect.set(shadowWidth, shadowWidth, w - shadowWidth, h - (shadowWidth + 5));
        if (mask != null && !mask.isRecycled()) {
            mask.recycle();
        }
        mask = makeBitmapMask();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int sc = canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), null,
                Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        canvas.drawBitmap(mask, 0F, 0F, maskPaint);

        canvas.restoreToCount(sc);
    }

    @Nullable
    private Bitmap makeBitmapMask() {
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            final Bitmap mask = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(mask);
            canvas.drawRoundRect(rect, radius, radius, blackPaint);
            return mask;
        } else {
            Log.e("",
                    "Can't create a mask with height 0 or width 0. Or the layout has no children and is wrap content");
            return null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(rect, radius, radius, paint);
        super.onDraw(canvas);
    }
}