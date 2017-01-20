package com.lypeer.zybuluo.mixture.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;

/**
 * Created by lypeer on 2017/1/20.
 */
public class RoundProgressBar extends View {

    private Paint paintRound;
    private Paint paintProgress;

    private int roundColor = App.getRes().getColor(R.color.colorSubtitleWeak);
    private int roundProgressColor = App.getRes().getColor(R.color.colorWhite);

    private float roundWidth = 5;

    private int max = 100;
    private int progress = 0;

    private int style = 0;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public RoundProgressBar(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        paintRound = new Paint();
        paintProgress = new Paint();

        if (context != null && attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.RoundProgressBar);

            roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, App.getRes().getColor(R.color.colorSubtitleWeak));
            roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, App.getRes().getColor(R.color.colorWhite));
            roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
            max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
            style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

            mTypedArray.recycle();
        }

        paintRound.setColor(roundColor);
        paintRound.setStyle(Paint.Style.STROKE);
        paintRound.setStrokeWidth(roundWidth);
        paintRound.setAntiAlias(true);

        paintProgress.setStrokeWidth(roundWidth);
        paintProgress.setColor(roundProgressColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int center = getWidth() / 2;
        int radius = (int) (center - roundWidth / 2);

        canvas.drawCircle(center, center, radius, paintRound);

        RectF oval = new RectF(center - radius, center - radius, center
                + radius, center + radius);

        switch (style) {
            case STROKE: {
                paintProgress.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, -90, 360 * progress / max, false, paintProgress);
                break;
            }
            case FILL: {
                paintProgress.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, -90, 360 * progress / max, true, paintProgress);
                break;
            }
        }

    }


    public synchronized int getMax() {
        return max;
    }

    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            invalidate();
        }

    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }


}