package com.lypeer.zybuluo.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lypeer.zybuluo.R;

import java.util.Map;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * Created by lypeer on 2017/1/19.
 */

public class LyVideoPlayer extends JCVideoPlayer {

    public ImageView thumbImageView;
    private boolean mHasStartButton = true;

    public LyVideoPlayer(Context context) {
        super(context);
    }

    public LyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_video_layout;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        thumbImageView = (ImageView) findViewById(fm.jiecao.jcvideoplayer_lib.R.id.thumb);
        thumbImageView.setOnClickListener(this);
        if(mHasStartButton){
            startButton.setVisibility(VISIBLE);
        }else {
            startButton.setVisibility(GONE);
        }
    }

    @Override
    public boolean setUp(String url, int screen, Object... objects) {
        if (super.setUp(url, screen, objects)) {
            fullscreenButton.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void setUiWitStateAndScreen(int state) {
        super.setUiWitStateAndScreen(state);
        switch (currentState) {
            case CURRENT_STATE_NORMAL:
                if (mHasStartButton) {
                    startButton.setVisibility(View.VISIBLE);
                } else {
                    startButton.setVisibility(View.GONE);
                }
                thumbImageView.setVisibility(VISIBLE);
                break;
            case CURRENT_STATE_PREPARING:
                if (mHasStartButton) {
                    startButton.setVisibility(View.INVISIBLE);
                } else {
                    startButton.setVisibility(GONE);
                }
                thumbImageView.setVisibility(GONE);
                break;
            case CURRENT_STATE_PLAYING:
                if (mHasStartButton) {
                    startButton.setVisibility(View.VISIBLE);
                } else {
                    startButton.setVisibility(GONE);
                }
                break;
            case CURRENT_STATE_PAUSE:
                break;
            case CURRENT_STATE_ERROR:
                break;
        }
        updateStartImage();
    }

    private void updateStartImage() {
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.setImageResource(R.color.colorEmpty);
        } else if (currentState == CURRENT_STATE_ERROR) {
            if (mHasStartButton) {
                startButton.setImageResource(fm.jiecao.jcvideoplayer_lib.R.drawable.jc_click_error_selector);
            } else {
                startButton.setImageResource(R.color.colorEmpty);
            }
        } else {
            if (mHasStartButton) {
                startButton.setImageResource(fm.jiecao.jcvideoplayer_lib.R.drawable.jc_click_play_selector);
            } else {
                startButton.setImageResource(R.color.colorEmpty);
            }
        }
    }

    public void setHasStartButton(boolean hasStartButton) {
        this.mHasStartButton = hasStartButton;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.thumb) {
            startButton.performClick();
            return;
        }
        super.onClick(v);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (currentState == CURRENT_STATE_NORMAL) {
                Toast.makeText(getContext(), "Play video first", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        super.onProgressChanged(seekBar, progress, fromUser);
    }

    @Override
    public boolean backToOtherListener() {
        return false;
    }
}