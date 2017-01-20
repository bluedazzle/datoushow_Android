package com.lypeer.zybuluo.mixture.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 游小光 on 2016/12/31.
 */

public class CircleProgressView extends LinearLayout {

    @BindView(R.id.tv_progress)
    TextView mTvProgress;
    @BindView(R.id.tv_text)
    TextView mTvText;
    @BindView(R.id.rp_progress)
    RoundProgressBar mRpProgress;
    @BindView(R.id.lly_container)
    LinearLayout mLlyContainer;

    private int mProgress = 0;
    private int mMax = 100;

    private int mLlyContainerBg = App.getRes().getColor(R.color.colorWhite);

    public CircleProgressView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        View view = View.inflate(context, R.layout.view_circle_progress, this);
        ButterKnife.bind(this, view);
    }

    public int getProgress() {
        return mProgress;
    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            mTvText.setVisibility(GONE);
        } else {
            mTvText.setVisibility(VISIBLE);
            mTvText.setText(text);
        }
    }

    public void setProgress(int progress, String progressText) {
        if (progress < 0) {
            return;
        }

        if (!TextUtils.isEmpty(progressText)) {
            mProgress = progress;
            mRpProgress.setProgress(progress);
            mTvProgress.setText(progressText);
        } else {
            mRpProgress.setProgress(progress);
            mTvProgress.setVisibility(GONE);
        }
    }

    public void show() {
        this.setVisibility(VISIBLE);
        mTvText.setVisibility(VISIBLE);
        mLlyContainer.setBackgroundColor(mLlyContainerBg);
    }

    public void dismiss() {
        this.setVisibility(GONE);
        mTvText.setVisibility(GONE);
    }

    public void setLlyContainerBg(int color) {
        mLlyContainer.setBackgroundColor(App.getRes().getColor(color));
        mLlyContainerBg = App.getRes().getColor(color);
    }

    public void setTextColor(int color) {
        mTvProgress.setTextColor(App.getRes().getColor(color));
        mTvText.setTextColor(App.getRes().getColor(color));
    }

    public void setRoundColor(int color){
        mRpProgress.setRoundColor(color);
    }

    public void setRoundProgressColor(int roundProgressColor) {
        mRpProgress.setRoundProgressColor(roundProgressColor);
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
        mRpProgress.setMax(max);
    }
}
