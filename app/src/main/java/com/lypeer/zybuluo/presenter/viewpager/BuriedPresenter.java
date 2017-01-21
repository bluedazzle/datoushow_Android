package com.lypeer.zybuluo.presenter.viewpager;

import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.model.remote.viewpager.BuriedModel;
import com.lypeer.zybuluo.presenter.base.BasePresenter;
import com.lypeer.zybuluo.ui.fragment.viewpager.BuriedFragment;

/**
 * Created by lypeer on 2017/1/21.
 */

public class BuriedPresenter extends BasePresenter<BuriedFragment , BuriedModel> {
    @Override
    protected BuriedModel createModel() {
        return new BuriedModel(this);
    }
    public void refreshVideos(int currentPage , int type) {
        if(!isViewAttached()){
            return;
        }

        getModel().refreshVideos(currentPage , type);
    }

    public void loadMoreVideos(int currentPage , int type) {
        if(!isViewAttached()){
            return;
        }

        getModel().loadMoreVideos(currentPage , type);
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
