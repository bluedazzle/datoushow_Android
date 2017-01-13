package com.lypeer.zybuluo.presenter.main;

import android.preference.Preference;

import com.lypeer.zybuluo.impl.OnProgressChangedListener;
import com.lypeer.zybuluo.model.bean.CreateShareLinkResponse;
import com.lypeer.zybuluo.model.bean.Video;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.model.main.MyModel;
import com.lypeer.zybuluo.presenter.base.BasePresenter;
import com.lypeer.zybuluo.ui.fragment.MyFragment;

import java.util.List;

/**
 * Created by lypeer on 2017/1/4.
 */

public class MyPresenter extends BasePresenter<MyFragment , MyModel> {
    @Override
    protected MyModel createModel() {
        return new MyModel(this);
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

    public void refreshVideosSuccess(List<Video> videoList) {
        if(!isViewAttached()){
            return;
        }
        getView().refreshVideosSuccess(videoList);
    }

    public void refreshVideosFail(String errorMessage) {
        if(!isViewAttached()){
            return;
        }
        getView().refreshVideosFail(errorMessage);
    }

    public void loadMoreVideosSuccess(List<Video> videoList) {
        if(!isViewAttached()){
            return;
        }
        getView().loadMoreVideosSuccess(videoList);
    }

    public void loadMoreVideosFail(String errorMessage) {
        if(!isViewAttached()){
            return;
        }
        getView().loadMoreVideosFail(errorMessage);
    }

    public void share(Video itemValue , OnProgressChangedListener listener) {
        if(!isViewAttached()){
            return;
        }
        getModel().share(itemValue  , listener);
    }

    public void shareFail(String errorMessage) {
        if(!isViewAttached()){
            return;
        }
        getView().shareFail(errorMessage);
    }

    public void shareSuccess(CreateShareLinkResponse shareLinkResponse) {
        if(!isViewAttached()){
            return;
        }
        getView().shareSuccess(shareLinkResponse);
    }
}
