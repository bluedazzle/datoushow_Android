package com.lypeer.zybuluo.presenter.viewpager;

import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.model.remote.viewpager.HotModel;
import com.lypeer.zybuluo.presenter.base.BasePresenter;
import com.lypeer.zybuluo.ui.fragment.viewpager.HotFragment;

/**
 * Created by lypeer on 2017/1/4.
 */

public class HotPresenter extends BasePresenter<HotFragment , HotModel> {
    @Override
    protected HotModel createModel() {
        return new HotModel(this);
    }

    public void refreshVideos(int currentPage) {
        if(!isViewAttached()){
            return;
        }

        getModel().refreshVideos(currentPage);
    }

    public void loadMoreVideos(int currentPage) {
        if(!isViewAttached()){
            return;
        }

        getModel().loadMoreVideos(currentPage);
    }

    public void refreshVideosSuccess(VideoResponse body) {
        if(!isViewAttached()){
            return;
        }
        getView().refreshVideosSuccess(body);
    }

    public void refreshVideosFail(String errorMessage) {
        if(!isViewAttached()){
            return;
        }
        getView().refreshVideosFail(errorMessage);
    }

    public void loadMoreVideosSuccess(VideoResponse videoResponse) {
        if(!isViewAttached()){
            return;
        }
        getView().loadMoreVideosSuccess(videoResponse);
    }

    public void loadMoreVideosFail(String errorMessage) {
        if(!isViewAttached()){
            return;
        }
        getView().loadMoreVideosFail(errorMessage);
    }
}
