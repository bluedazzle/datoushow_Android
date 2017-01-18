package com.lypeer.zybuluo.ui.fragment.guide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.event.EmptyEvent;
import com.lypeer.zybuluo.event.PageChangeEvent;
import com.lypeer.zybuluo.ui.base.BaseCustomFragment;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by lypeer on 2017/1/18.
 */

public class Guide3Fragment extends BaseCustomFragment {
    @BindView(R.id.video_player)
    JCVideoPlayerStandard mVideoPlayer;

    @Override
    protected int getResId() {
        return R.layout.fragment_guide_3;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        String path = FileUtil.getStorageDir() + "/video_guide_3.mp4";
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
        mVideoPlayer.setUp(path
                , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        mVideoPlayer.coverImageView.setVisibility(View.INVISIBLE);
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
            if (pageChangeEvent.getCurrentFragment() == Constants.GuideFragmentId.GUIDE_3) {

                mVideoPlayer.startButton.performClick();
            } else {
                mVideoPlayer.changeUiToCompleteClear();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}
