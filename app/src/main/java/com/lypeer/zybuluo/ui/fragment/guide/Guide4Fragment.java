package com.lypeer.zybuluo.ui.fragment.guide;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.event.EmptyEvent;
import com.lypeer.zybuluo.event.PageChangeEvent;
import com.lypeer.zybuluo.ui.activity.MainActivity;
import com.lypeer.zybuluo.ui.base.BaseCustomFragment;
import com.lypeer.zybuluo.ui.custom.LyVideoPlayer;
import com.lypeer.zybuluo.utils.ActivityController;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by lypeer on 2017/1/18.
 */

public class Guide4Fragment extends BaseCustomFragment {
    @BindView(R.id.video_player)
    LyVideoPlayer mVideoPlayer;
    @BindView(R.id.btn_enter)
    Button mBtnEnter;

    @Override
    protected int getResId() {
        return R.layout.fragment_guide_4;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        String path = FileUtil.getStorageDir() + "/video_guide_4.mp4";
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        checkFile(path);
    }

    private void checkFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            initVideo(path);
            return;
        }

        try {
            FileUtil.copy();
            checkFile(path);
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(R.string.error_data_wrong);
        }
    }

    private void initVideo(String path) {
        mVideoPlayer.setHasStartButton(false);
        mVideoPlayer.setUp(path
                , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        mVideoPlayer.looping = true;
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(EmptyEvent event) {
        if (event == null) {
            return;
        }
        if (mVideoPlayer == null) {
            return;
        }
        if (event instanceof PageChangeEvent) {
            PageChangeEvent pageChangeEvent = (PageChangeEvent) event;
            if (pageChangeEvent.getCurrentFragment() == Constants.GuideFragmentId.GUIDE_4) {
                mVideoPlayer.startButton.performClick();
                showButton();
            } else {
                mVideoPlayer.setUiWitStateAndScreen(JCVideoPlayer.CURRENT_STATE_NORMAL);
            }
        }
    }

    private void showButton() {
        if (mBtnEnter != null && mBtnEnter.getVisibility() == View.INVISIBLE) {
            mBtnEnter.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator
                            .ofFloat(mBtnEnter, "alpha", 0.0F, 1F)
                            .setDuration(2000)
                            .start();
                    mBtnEnter.setVisibility(View.VISIBLE);
                }
            }, 2000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        JCVideoPlayer.releaseAllVideos();
    }

    @OnClick(R.id.btn_enter)
    public void onClick() {
        getActivity().finish();
        Intent intent = new Intent(getActivity() , MainActivity.class);
        startActivity(intent);
    }
}
