package com.lypeer.zybuluo.mixture.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    @BindView(R.id.pb_progress)
    ProgressBar mPbProgress;

    private int mProgress = 0;

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

    private long mLastTime = 0;

    public void setText(String text) {
        mTvText.setVisibility(VISIBLE);
        mTvText.setText(text);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mTvProgress.setText("当前进度为：" + progress + "%");
    }

    public void show() {
        this.setVisibility(VISIBLE);
        mTvText.setVisibility(GONE);
    }

    public void dismiss() {
        this.setVisibility(GONE);
        mTvText.setVisibility(GONE);
    }
}
