package com.lypeer.zybuluo.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.mixture.core.SubtitleInfo;
import com.lypeer.zybuluo.utils.DataFormatter;

import java.util.List;

/**
 * Created by lypeer on 2017/1/19.
 */

public class SubtitleView extends View {

    public boolean mEnable;
    private static final String TAG = "SubtitleView";

    private TextPaint mCurrentPaint = new TextPaint();
    private TextPaint mWeakPaint = new TextPaint();
    private TextPaint mNormalPaint = new TextPaint();
    private List<SubtitleInfo> mSubtitleInfos;
    private int mCurrentLine = -1;
    private float mDividerHeight = DataFormatter.dipToPixels(28);

    public SubtitleView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SubtitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SubtitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        this.setWillNotDraw(false);

        mCurrentPaint.setAntiAlias(true);
        int currentColor = R.color.colorSubtitleSelected;
        mCurrentPaint.setColor(App.getRes().getColor(currentColor));
        mCurrentPaint.setTextSize(DataFormatter.dipToPixels(19));
        mCurrentPaint.setTextAlign(Paint.Align.CENTER);
        mCurrentPaint.setStrokeWidth(DataFormatter.dipToPixels(6));
        mCurrentPaint.setStyle(Paint.Style.FILL);

        mNormalPaint.setAntiAlias(true);
        int normalColor = R.color.colorSubtitleNormal;
        mNormalPaint.setColor(App.getRes().getColor(normalColor));
        mNormalPaint.setTextSize(DataFormatter.dipToPixels(19));
        mNormalPaint.setTextAlign(Paint.Align.CENTER);
        mNormalPaint.setStrokeWidth(DataFormatter.dipToPixels(6));
        mNormalPaint.setStyle(Paint.Style.FILL);

        mWeakPaint.setAntiAlias(true);
        int weakColor = R.color.colorSubtitleWeak;
        mWeakPaint.setColor(App.getRes().getColor(weakColor));
        mWeakPaint.setTextSize(DataFormatter.dipToPixels(19));
        mWeakPaint.setTextAlign(Paint.Align.CENTER);
        mWeakPaint.setStrokeWidth(DataFormatter.dipToPixels(6));
        mWeakPaint.setStyle(Paint.Style.FILL);
    }

    public void setData(List<SubtitleInfo> data) {
        if (data == null || data.size() == 0) {
            mEnable = false;
        } else {
            mEnable = true;
            this.mSubtitleInfos = data;
            this.setVisibility(VISIBLE);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mEnable) {
            return;
        }

        float centerY = getHeight() / 2;
        float baseX = getWidth() / 2;

        if (mSubtitleInfos == null) {
            this.setVisibility(INVISIBLE);
            return;
        } else {
            this.setVisibility(VISIBLE);
        }

        if (mCurrentLine < 0 || mCurrentLine >= mSubtitleInfos.size()) {
            return;
        }

        float currY = centerY - (mCurrentPaint.descent() + mCurrentPaint.ascent()) / 2;

        if (mCurrentLine == 0) {
            canvas.drawText(mSubtitleInfos.get(mCurrentLine).getContent(), baseX, currY - mDividerHeight, mCurrentPaint);
            if (mSubtitleInfos.size() >= 3) {
                canvas.drawText(mSubtitleInfos.get(mCurrentLine + 1).getContent(), baseX, currY, mNormalPaint);
                canvas.drawText(mSubtitleInfos.get(mCurrentLine + 2).getContent(), baseX, currY + mDividerHeight, mWeakPaint);
            } else if (mSubtitleInfos.size() == 2) {
                canvas.drawText(mSubtitleInfos.get(mCurrentLine + 1).getContent(), baseX, currY, mNormalPaint);
            }

            return;
        }

        if (mCurrentLine == 1) {
            canvas.drawText(mSubtitleInfos.get(mCurrentLine - 1).getContent(), baseX, currY - mDividerHeight, mWeakPaint);
            canvas.drawText(mSubtitleInfos.get(mCurrentLine).getContent(), baseX, currY, mCurrentPaint);

            if (mSubtitleInfos.size() >= 3) {
                canvas.drawText(mSubtitleInfos.get(mCurrentLine + 1).getContent(), baseX, currY + mDividerHeight, mNormalPaint);
            }
            return;
        }

        canvas.drawText(mSubtitleInfos.get(mCurrentLine).getContent(), baseX, currY, mCurrentPaint);

        for (int i = mCurrentLine - 1; i >= 0; i--) {
            int j = mCurrentLine - i;
            if (j >= 2) {
                break;
            }

            float upY = currY - j * mDividerHeight;
            canvas.drawText(mSubtitleInfos.get(i).getContent(), baseX, upY, mWeakPaint);
        }

        for (int i = mCurrentLine + 1; i < mSubtitleInfos.size(); i++) {
            int j = i - mCurrentLine;
            if (j >= 2) {
                break;
            }

            float downY = currY + j * mDividerHeight;
            canvas.drawText(mSubtitleInfos.get(i).getContent(), baseX, downY, mNormalPaint);
        }
    }

    public void updateTime(long time) {
        if (mSubtitleInfos == null) {
            Log.e(TAG, "please invoke setData() first !");
            return;
        }

        if (!mEnable) {
            return;
        }

        for (int i = 0; i < mSubtitleInfos.size(); i++) {
            long nextStartTime = DataFormatter.string2Millisecond(mSubtitleInfos.get(i).getStart_time());
            long nextEndTime = DataFormatter.string2Millisecond(mSubtitleInfos.get(i).getEnd_time());

            if (nextStartTime == -1 || nextEndTime == -1) {
                Log.e(TAG, "time is -1 !");
                return;
            }

            if (nextStartTime <= time && nextEndTime >= time) {
                if (mCurrentLine != i) {
                    mCurrentLine = i;

                    this.invalidate();
                    this.requestLayout();
                }
                break;
            }
        }
    }
}
