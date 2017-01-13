package com.lypeer.zybuluo.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.lypeer.zybuluo.R;


/**
 * this is a LinearLayout whose ratio of height ang width can be controlled
 * Created by lypeer on 16-4-26.
 */
public class RatioLayout extends LinearLayout {

    private Context context;
    private AttributeSet attrs;
    private int defStyleAttr;

    /**
     * ratio = LayoutHeightSize / LayoutWidthSize
     */
    private float ratio;

    public RatioLayout(Context context) {
        super(context);
        init(context , null , 0);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs , 0);
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;
        if (attrs != null) {
            ratio = 0.0f;
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout, defStyleAttr, 0);
            try {
                ratio = a.getFloat(R.styleable.RatioLayout_ratio, 0.0f);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        if (ratio != 0.0f) {
            int childWidthSize = getMeasuredWidth();
            int childHeightSize = (int) (childWidthSize * ratio);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
